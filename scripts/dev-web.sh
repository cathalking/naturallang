#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_LOG="$ROOT_DIR/build/backend.log"
FRONTEND_LOG="$ROOT_DIR/build/frontend.log"

cd "$ROOT_DIR"
echo "Building backend (Spring Boot API)..."
./gradlew build
echo "Starting backend (Spring Boot API)..."
./gradlew bootRun >"$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!

trap 'echo "Stopping gradle..."; kill $BACKEND_PID >/dev/null 2>&1'  EXIT
trap 'echo "Stopping java..."; lsof -i:8080 | sed -n "2,$p" | awk "{ print $2 }" | xargs kill -9' EXIT

cd ui
echo "Building frontend (Vite dev server)..."
npm install >/dev/null
npm run build
echo "Starting frontend (Vite dev server)..."
npm run dev -- --host 0.0.0.0 --port 4173 >"$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!

trap 'kill $FRONTEND_PID >/dev/null 2>&1' EXIT

sleep 2
open "http://localhost:4173" >/dev/null 2>&1 || true
wait $FRONTEND_PID
