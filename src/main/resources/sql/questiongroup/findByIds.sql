SELECT question_group_id,
       question_group_title,
       question_group_type
FROM question_group
WHERE question_group_id IN (/*[# mb:p="questionGroupIds"]*/ '0C6THP503VM0J' /*[/]*/)
ORDER BY question_group_id