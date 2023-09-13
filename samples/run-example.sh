#!/bin/bash
# https://scrapbox.io/kawasima/%E3%82%A2%E3%83%B3%E3%82%B1%E3%83%BC%E3%83%88

set -ex -o pipefail
API_URL=${API_URL:-http://localhost:8080}
USERNAME=${USERNAME:-admin@example.com}
PASSWORD=${PASSWORD:-admin}

# 組織作成
organization_id=$(curl -sf -XPOST ${API_URL}/organizations -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d "{\"organization_name\":\"Org ${RANDOM}\", \"admin_email\":\"admin@example.com\"}" | jq -r .organization_id)

# アンケート作成
survey1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/surveys -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"survey_title":"テストアンケート", "start_date_time":"2019-10-01T00:00:00.000+09:00", "end_date_time":"2020-10-01T00:00:00.000+09:00", "is_public": false}' | jq -r .survey_id)

# 設問グループ作成
question_group1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/question_groups -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_group_title":"設計に関するアンケート", "question_group_type": "default"}' | jq -r .question_group_id)

# 選択回答設問作成
question1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "この設計はいけてますか?", "max_choices": 1}' | jq -r .question_id)
# 記述式回答設問作成
question2_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "どういうところがいけてますか?"}' | jq -r .question_id)
# 選択回答設問作成
question3_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "他にも取り上げて欲しい設計がありますか?", "max_choices": 3}' | jq -r .question_id)

# 設問グループと設問をマッピング
curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question1_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question2_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question3_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'

# アンケートと設問グループをマッピング
curl -sf -XPUT ${API_URL}/surveys/${survey1_id}/survey_question_groups/${question_group1_id} -u ${USERNAME}:${PASSWORD}

# 設問選択肢追加
question1_choice1_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "はい", "score": 1, "allow_free_text": false}' | jq -r .question_choice_id)
question1_choice2_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "いいえ", "score": 0, "allow_free_text": false}' | jq -r .question_choice_id)

question3_choice1_id=$(curl -sf -XPOST ${API_URL}/questions/${question3_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "在庫", "score": 0, "allow_free_text": false}' | jq -r .question_choice_id)
question3_choice2_id=$(curl -sf -XPOST ${API_URL}/questions/${question3_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "カート", "score": 0, "allow_free_text": false}' | jq -r .question_choice_id)
question3_choice3_id=$(curl -sf -XPOST ${API_URL}/questions/${question3_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "お気に入り", "score": 0, "allow_free_text": false}' | jq -r .question_choice_id)
question3_choice4_id=$(curl -sf -XPOST ${API_URL}/questions/${question3_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "リコメンド", "score": 0, "allow_free_text": false}' | jq -r .question_choice_id)
question3_choice5_id=$(curl -sf -XPOST ${API_URL}/questions/${question3_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "その他", "score": 0, "allow_free_text": true}' | jq -r .question_choice_id)

# アンケート表示
curl -sf "${API_URL}/surveys/${survey1_id}?include_questions=true" -u ${USERNAME}:${PASSWORD} | jq .

# 選択回答作成
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo1\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo2\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo3\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo4\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo5\", \"choices\": [{\"question_choice_id\": \"${question1_choice2_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo6\", \"choices\": [{\"question_choice_id\": \"${question1_choice2_id}\"}]}"

curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo1\", \"answer_text\": \"具体的なデータがあってわかりやすい\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo2\", \"answer_text\": \"ER図がわかりやすい\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo3\", \"answer_text\": \"ここまで複雑なモデルが必要なの?\"}"

curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo1\", \"choices\": [{\"question_choice_id\": \"${question3_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo2\", \"choices\": [{\"question_choice_id\": \"${question3_choice1_id}\"}, {\"question_choice_id\": \"${question3_choice2_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo3\", \"choices\": [{\"question_choice_id\": \"${question3_choice2_id}\"}, {\"question_choice_id\": \"${question3_choice3_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo4\", \"choices\": [{\"question_choice_id\": \"${question3_choice4_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo5\", \"choices\": [{\"question_choice_id\": \"${question3_choice4_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -u ${USERNAME}:${PASSWORD} -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo6\", \"choices\": [{\"question_choice_id\": \"${question3_choice5_id}\", \"answer_text\": \"検索\"}]}"

# アンケート回答表示
curl -sf ${API_URL}/surveys/${survey1_id}/answers -u ${USERNAME}:${PASSWORD} | jq .
