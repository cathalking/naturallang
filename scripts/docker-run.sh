#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
docker rm -f naturallang-demo >/dev/null 2>&1 || true
docker run --name naturallang-demo -p 8080:8080 naturallang &
echo "Container started. Waiting for server..."
sleep 5
echo "Use scripts/api-example.sh to hit the exposed port."
