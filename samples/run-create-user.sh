#!/bin/bash

set -e -o pipefail
API_URL=${API_URL:-http://localhost:8080}
EMAIL=${EMAIL:-admin@example.com}
PASSWORD=${PASSWORD:-admin}

# ユーザー作成
curl -s -XPOST ${API_URL}/users -H 'Content-Type: application/json' -d "{\"email\": \"${EMAIL}\", \"password\": \"${PASSWORD}\"}" | jq -r .user_id