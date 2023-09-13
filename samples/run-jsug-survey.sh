#!/bin/bash

set -ex -o pipefail
API_URL=${API_URL:-http://localhost:8080}
USERNAME=${USERNAME:-admin@example.com}
PASSWORD=${PASSWORD:-admin}

organization_id=$(curl -sf -XPOST ${API_URL}/organizations -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d "{\"organization_name\":\"Org ${RANDOM}\", \"admin_email\":\"admin@example.com\"}" | jq -r .organization_id)

survey1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/surveys -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"survey_title":"Spring Fest 2023", "start_date_time":"2023-03-17T00:00:00.000+09:00", "end_date_time":"2024-03-17T00:00:00.000+09:00", "is_public": true}' | jq -r .survey_id)

question_group1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/question_groups -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_group_title":"イベント全体の感想", "question_group_type": "イベント全体の感想"}' | jq -r .question_group_id)
question_group2_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/question_groups -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_group_title":"セッションAの感想", "question_group_type": "セッション毎の感想"}' | jq -r .question_group_id)
question_group3_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/question_groups -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_group_title":"セッションBの感想", "question_group_type": "セッション毎の感想"}' | jq -r .question_group_id)
question_group4_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/question_groups -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_group_title":"セッションCの感想", "question_group_type": "セッション毎の感想"}' | jq -r .question_group_id)

question1_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "満足度はどうだったでしょうか？", "max_choices": 1}' | jq -r .question_id)
question2_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "次回、期待するコンテンツを教えてください"}' | jq -r .question_id)
question3_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "全体で何かご意見があれば教えてください"}' | jq -r .question_id)
question4_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "難易度は良かったでしょうか？", "max_choices": 1}' | jq -r .question_id)
question5_id=$(curl -sf -XPOST ${API_URL}/organizations/${organization_id}/questions -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_text": "何かご意見があれば教えてください"}' | jq -r .question_id)

curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question1_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question2_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group1_id}/question_group_questions/${question3_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group2_id}/question_group_questions/${question1_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group2_id}/question_group_questions/${question4_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group2_id}/question_group_questions/${question5_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group3_id}/question_group_questions/${question1_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group3_id}/question_group_questions/${question4_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group3_id}/question_group_questions/${question5_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group4_id}/question_group_questions/${question1_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group4_id}/question_group_questions/${question4_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": true}'
curl -sf -XPUT ${API_URL}/question_groups/${question_group4_id}/question_group_questions/${question5_id} -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"required": false}'

curl -sf -XPUT ${API_URL}/surveys/${survey1_id}/survey_question_groups/${question_group1_id} -u ${USERNAME}:${PASSWORD}
curl -sf -XPUT ${API_URL}/surveys/${survey1_id}/survey_question_groups/${question_group2_id} -u ${USERNAME}:${PASSWORD}
curl -sf -XPUT ${API_URL}/surveys/${survey1_id}/survey_question_groups/${question_group3_id} -u ${USERNAME}:${PASSWORD}
curl -sf -XPUT ${API_URL}/surveys/${survey1_id}/survey_question_groups/${question_group4_id} -u ${USERNAME}:${PASSWORD}

question1_choice1_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "とても良かった", "score": 5}' | jq -r .question_choice_id)
question1_choice2_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "良かった", "score": 4}' | jq -r .question_choice_id)
question1_choice3_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "普通", "score": 3}' | jq -r .question_choice_id)
question1_choice4_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "悪かった", "score": 2}' | jq -r .question_choice_id)
question1_choice5_id=$(curl -sf -XPOST ${API_URL}/questions/${question1_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "とても悪かった", "score": 1}' | jq -r .question_choice_id)

question4_choice1_id=$(curl -sf -XPOST ${API_URL}/questions/${question4_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "とても難しかった", "score": 5}' | jq -r .question_choice_id)
question4_choice2_id=$(curl -sf -XPOST ${API_URL}/questions/${question4_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "難しかった", "score": 4}' | jq -r .question_choice_id)
question4_choice3_id=$(curl -sf -XPOST ${API_URL}/questions/${question4_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "ちょうど良かった", "score": 3}' | jq -r .question_choice_id)
question4_choice4_id=$(curl -sf -XPOST ${API_URL}/questions/${question4_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "簡単だった", "score": 2}' | jq -r .question_choice_id)
question4_choice5_id=$(curl -sf -XPOST ${API_URL}/questions/${question4_id}/question_choices -u ${USERNAME}:${PASSWORD} -H 'Content-Type: application/json' -d '{"question_choice_text": "簡単すぎた", "score": 1}' | jq -r .question_choice_id)

curl -sf "${API_URL}/surveys/${survey1_id}?include_questions=true" | jq .

curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo1\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo2\", \"choices\": [{\"question_choice_id\": \"${question1_choice1_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo3\", \"choices\": [{\"question_choice_id\": \"${question1_choice2_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo4\", \"choices\": [{\"question_choice_id\": \"${question1_choice2_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo5\", \"choices\": [{\"question_choice_id\": \"${question1_choice3_id}\"}]}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question1_id}\", \"respondent_id\": \"demo6\", \"choices\": [{\"question_choice_id\": \"${question1_choice4_id}\"}]}"

curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo1\", \"answer_text\": \"Spring Boot 3への移行\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo2\", \"answer_text\": \"Spring Security\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question2_id}\", \"respondent_id\": \"demo3\", \"answer_text\": \"Spring Integration\"}"

curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo1\", \"answer_text\": \"ありがとうございました。\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo2\", \"answer_text\": \"お疲れさまでした。\"}"
curl -sf -XPOST ${API_URL}/surveys/${survey1_id}/answers -H 'Content-Type: application/json' -d "{\"question_group_id\": \"${question_group1_id}\", \"question_id\": \"${question3_id}\", \"respondent_id\": \"demo3\", \"answer_text\": \"次回も期待しています。\"}"

# アンケート回答表示
curl -sf ${API_URL}/surveys/${survey1_id}/answers -u ${USERNAME}:${PASSWORD} | jq .
