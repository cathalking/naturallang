#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BIN="$ROOT_DIR/build/install/naturallang/bin/naturallang"
MODE="script"
PASSTHRU=()

for arg in "$@"; do
  if [[ "$arg" == "--interactive" ]]; then
    MODE="interactive"
  else
    PASSTHRU+=("$arg")
  fi
done

if [[ "$MODE" == "interactive" ]]; then
  if [[ -x "$BIN" ]]; then
    exec "$BIN" "${PASSTHRU[@]}"
  fi
  if [[ ${#PASSTHRU[@]} -gt 0 ]]; then
    exec "$ROOT_DIR/gradlew" run --no-daemon --args="${PASSTHRU[*]}"
  fi
  exec "$ROOT_DIR/gradlew" run --no-daemon
fi

ARGS=""
if [[ ${#PASSTHRU[@]} -gt 0 ]]; then
  ARGS="--args=${PASSTHRU[*]}"
fi
exec "$ROOT_DIR/gradlew" run --no-daemon --quiet $ARGS
