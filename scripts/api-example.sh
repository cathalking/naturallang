#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BASE_URL="${BASE_URL:-http://localhost:8080}"

create_session() {
  curl -s -X POST "$BASE_URL/api/practice/sessions" \
    -H "Content-Type: application/json" \
    -d '{"verbs":["DO"],"forms":["STATEMENT_POSITIVE"],"tenses":["PAST"],"subjects":["SING_1ST"],"maxQuestions":1,"pronounMode":"strict"}'
}

session_response=$(create_session)
session_id=$(python3 - <<'PY'
import json, sys
print(json.load(sys.stdin)["sessionId"])
PY
 <<<"$session_response")

echo "sessionId=$session_id"

prompt_response=$(curl -s "$BASE_URL/api/practice/sessions/$session_id/prompt")
echo "prompt -> $prompt_response"

answer_response=$(curl -s -X POST "$BASE_URL/api/practice/sessions/$session_id/answer" \
  -H "Content-Type: application/json" \
  -d '{"response":"wrong"}')
echo "answer -> $answer_response"

summary_response=$(curl -s "$BASE_URL/api/practice/sessions/$session_id/summary")
echo "summary -> $summary_response"
