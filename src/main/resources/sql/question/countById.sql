SELECT COUNT(question_id)
FROM (SELECT question_id
      FROM question
      WHERE question_id = /*[# mb:p="questionId"]*/ '0C6THP503VM0J' /*[/]*/
      UNION ALL
      SELECT question_id
      FROM selective_question
      WHERE question_id = /*[# mb:p="questionId"]*/ '0C6THP503VM0J' /*[/]*/) AS que