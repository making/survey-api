SELECT question_group_id,
       question_id,
       required
FROM question_group_question
WHERE question_group_id IN (/*[# mb:p="questionGroupIds"]*/ '0C6THP503VM0J' /*[/]*/)
ORDER BY question_group_id, question_id