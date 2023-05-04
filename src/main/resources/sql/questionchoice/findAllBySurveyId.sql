SELECT qc.question_choice_id,
       qc.question_choice_text,
       qc.allow_free_text
FROM question_choice qc
         INNER JOIN survey_question sq ON qc.question_id = sq.question_id
WHERE sq.survey_id = /*[# mb:p="surveyId"]*/ '0C6THP503VM0J' /*[/]*/
ORDER BY qc.question_choice_id