SELECT ans.answer_id,
       ans.question_choice_id,
       ans.answer_text,
       ans.survey_id,
       ans.question_group_id,
       ans.question_id,
       ans.respondent_id
FROM (SELECT ca.answer_id,
             ca.question_choice_id,
             ca.answer_text,
             a.survey_id,
             a.question_group_id,
             a.question_id,
             a.respondent_id
      FROM chosen_answer ca
               INNER JOIN answer a ON ca.answer_id = a.answer_id
      UNION ALL
      SELECT da.answer_id,
             NULL AS question_choice_id,
             da.answer_text,
             a.survey_id,
             a.question_group_id,
             a.question_id,
             a.respondent_id
      FROM descriptive_answer da
               INNER JOIN answer a ON da.answer_id = a.answer_id) ans
WHERE ans.answer_id = /*[# mb:p="answerId"]*/ '0C6THP503VM0J' /*[/]*/
ORDER BY ans.answer_id