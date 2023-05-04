SELECT que.question_id,
       que.question_text,
       que.max_choices,
       que.survey_id,
       que.question_choice_id,
       que.question_choice_text,
       que.allow_free_text
FROM (SELECT q.question_id,
             q.question_text,
             NULL AS max_choices,
             sq.survey_id,
             NULL AS question_choice_id,
             NULL AS question_choice_text,
             NULL AS allow_free_text
      FROM question q
               INNER JOIN survey_question sq ON q.question_id = sq.question_id
      UNION ALL
      SELECT q.question_id,
             q.question_text,
             q.max_choices,
             sq.survey_id,
             qc.question_choice_id,
             qc.question_choice_text,
             qc.allow_free_text
      FROM selective_question AS q
               LEFT JOIN question_choice AS qc ON q.question_id = qc.question_id
               INNER JOIN survey_question sq ON q.question_id = sq.question_id) que
WHERE que.survey_id = /*[# mb:p="surveyId"]*/ '0C6THP503VM0J' /*[/]*/
ORDER BY question_id, question_choice_id