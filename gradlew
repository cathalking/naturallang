#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$ROOT_DIR/gradle/wrapper/gradle-wrapper.jar"

if [[ -f "$WRAPPER_JAR" ]]; then
  exec java -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

if [[ "$*" == *"test"* ]]; then
  CLEAN=""
  if [[ "$*" == *"clean"* ]]; then
    CLEAN="clean"
  fi
  exec "$ROOT_DIR/scripts/offline-test.sh" "$CLEAN"
fi

echo "Gradle is not installed and gradle/wrapper/gradle-wrapper.jar is missing." >&2
echo "Run with a local Gradle installation, or execute './gradlew test' for offline test mode." >&2
exit 1
