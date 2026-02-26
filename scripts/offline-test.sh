#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/build"
MAIN_CLASSES="$BUILD_DIR/classes/java/main"
TEST_CLASSES="$BUILD_DIR/classes/java/test"
RUNNER_SRC_DIR="$BUILD_DIR/tmp/test-runner"
RUNNER_CLASSES="$BUILD_DIR/classes/test-runner"
STUB_SRC_DIR="$BUILD_DIR/tmp/stubs"
STUB_CLASSES="$BUILD_DIR/classes/stubs"

if [[ "${1:-}" == "clean" ]]; then
  rm -rf "$BUILD_DIR"
fi

mkdir -p "$MAIN_CLASSES" "$TEST_CLASSES" "$RUNNER_SRC_DIR" "$RUNNER_CLASSES"
mkdir -p "$STUB_SRC_DIR" "$STUB_CLASSES"

pick_latest_jar() {
  local group_path="$1"
  local artifact="$2"
  local base="$HOME/.m2/repository/$group_path/$artifact"
  if [[ ! -d "$base" ]]; then
    echo ""; return
  fi
  local version
  version="$(find "$base" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort -V | tail -n 1)"
  local jar="$base/$version/$artifact-$version.jar"
  if [[ -f "$jar" ]]; then
    echo "$jar"
  else
    echo ""
  fi
}

append_cp() {
  local jar="$1"
  if [[ -n "$jar" ]]; then
    if [[ -z "${CP:-}" ]]; then
      CP="$jar"
    else
      CP="$CP:$jar"
    fi
  fi
}

CP=""

append_cp "$(pick_latest_jar org/apache/commons commons-lang3)"
append_cp "$(pick_latest_jar commons-io commons-io)"
JSOUP_JAR="$(pick_latest_jar org/jsoup jsoup)"
append_cp "$JSOUP_JAR"
append_cp "$(pick_latest_jar com/google/guava guava)"
append_cp "$(pick_latest_jar com/google/guava failureaccess)"
append_cp "$(pick_latest_jar com/google/guava listenablefuture)"
append_cp "$(pick_latest_jar com/google/code/findbugs jsr305)"
append_cp "$(pick_latest_jar org/checkerframework checker-qual)"
append_cp "$(pick_latest_jar com/google/errorprone error_prone_annotations)"
append_cp "$(pick_latest_jar com/google/j2objc j2objc-annotations)"
append_cp "$(pick_latest_jar org/codehaus/mojo animal-sniffer-annotations)"
append_cp "$(pick_latest_jar com/fasterxml/jackson/core jackson-databind)"
append_cp "$(pick_latest_jar com/fasterxml/jackson/core jackson-core)"
append_cp "$(pick_latest_jar com/fasterxml/jackson/core jackson-annotations)"

if [[ -z "$JSOUP_JAR" ]]; then
  mkdir -p "$STUB_SRC_DIR/org/jsoup/nodes" "$STUB_SRC_DIR/org/jsoup/select"
  cat > "$STUB_SRC_DIR/org/jsoup/Jsoup.java" <<'JAVA'
package org.jsoup;

import org.jsoup.nodes.Document;

public final class Jsoup {
    private Jsoup() {}

    public static Connection connect(String ignoredUrl) {
        return new Connection();
    }

    public static final class Connection {
        public Connection data(String... ignoredKeyValues) {
            return this;
        }

        public Connection userAgent(String ignoredUserAgent) {
            return this;
        }

        public Document get() {
            return new Document();
        }

        public Document post() {
            return new Document();
        }
    }
}
JAVA
  cat > "$STUB_SRC_DIR/org/jsoup/nodes/Document.java" <<'JAVA'
package org.jsoup.nodes;

import org.jsoup.select.Elements;

public class Document {
    public Elements select(String ignoredCssQuery) {
        return new Elements();
    }
}
JAVA
  cat > "$STUB_SRC_DIR/org/jsoup/nodes/Element.java" <<'JAVA'
package org.jsoup.nodes;

import org.jsoup.select.Elements;

public class Element {
    public String text() {
        return "";
    }

    public String attr(String ignoredAttribute) {
        return "";
    }

    public Elements select(String ignoredCssQuery) {
        return new Elements();
    }
}
JAVA
  cat > "$STUB_SRC_DIR/org/jsoup/select/Elements.java" <<'JAVA'
package org.jsoup.select;

import java.util.ArrayList;
import org.jsoup.nodes.Element;

public class Elements extends ArrayList<Element> {
    public Elements select(String ignoredCssQuery) {
        return new Elements();
    }
}
JAVA
  STUB_SOURCES_FILE="$BUILD_DIR/stub-sources.txt"
  find "$STUB_SRC_DIR" -name '*.java' | sort > "$STUB_SOURCES_FILE"
  javac --release 21 -encoding UTF-8 -d "$STUB_CLASSES" @"$STUB_SOURCES_FILE"
  append_cp "$STUB_CLASSES"
fi

JUNIT_CP=""
for coord in \
  "org/junit/jupiter junit-jupiter-api" \
  "org/junit/jupiter junit-jupiter-params" \
  "org/junit/jupiter junit-jupiter-engine" \
  "org/junit/platform junit-platform-launcher" \
  "org/junit/platform junit-platform-engine" \
  "org/junit/platform junit-platform-commons" \
  "org/apiguardian apiguardian-api" \
  "org/opentest4j opentest4j" \
  "org/hamcrest hamcrest"; do
  group_path="${coord%% *}"
  artifact="${coord##* }"
  jar="$(pick_latest_jar "$group_path" "$artifact")"
  if [[ -n "$jar" ]]; then
    if [[ -z "$JUNIT_CP" ]]; then
      JUNIT_CP="$jar"
    else
      JUNIT_CP="$JUNIT_CP:$jar"
    fi
  fi
done

if [[ -z "$JUNIT_CP" ]]; then
  echo "Missing local JUnit jars in ~/.m2/repository; cannot run offline tests." >&2
  exit 1
fi

MAIN_SOURCES_FILE="$BUILD_DIR/main-sources.txt"
TEST_SOURCES_FILE="$BUILD_DIR/test-sources.txt"
find "$ROOT_DIR/src/main/java" -name '*.java' | sort > "$MAIN_SOURCES_FILE"
find "$ROOT_DIR/src/test/java" -name '*.java' | sort > "$TEST_SOURCES_FILE"

javac --release 21 -encoding UTF-8 -cp "$CP" -d "$MAIN_CLASSES" @"$MAIN_SOURCES_FILE"
javac --release 21 -encoding UTF-8 -cp "$CP:$JUNIT_CP:$MAIN_CLASSES" -d "$TEST_CLASSES" @"$TEST_SOURCES_FILE"

cat > "$RUNNER_SRC_DIR/OfflineJunitLauncher.java" <<'JAVA'
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class OfflineJunitLauncher {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("ck.apps.leabharcleachtadh"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.execute(request, listener);

        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new java.io.PrintWriter(System.out));
        if (summary.getTotalFailureCount() > 0) {
            System.exit(1);
        }
    }
}
JAVA

javac --release 21 -encoding UTF-8 -cp "$JUNIT_CP" -d "$RUNNER_CLASSES" "$RUNNER_SRC_DIR/OfflineJunitLauncher.java"
java -cp "$CP:$JUNIT_CP:$MAIN_CLASSES:$TEST_CLASSES:$RUNNER_CLASSES" OfflineJunitLauncher
