SELECT question_choice_id,
       question_choice_text,
       allow_free_text
FROM question_choice
WHERE question_id = /*[# mb:p="questionId"]*/ '0C6THP503VM0J' /*[/]*/
ORDER BY question_choice_id