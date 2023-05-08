SELECT question_choice_id,
       question_choice_text,
       score,
       allow_free_text
FROM question_choice
WHERE question_id = /*[# mb:p="questionId"]*/ '0C6THP503VM0J' /*[/]*/
  AND question_choice_id = /*[# mb:p="questionChoiceId"]*/ '0C6THP503VM0J' /*[/]*/