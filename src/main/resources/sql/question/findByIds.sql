SELECT que.question_id,
       que.question_text,
       que.max_choices,
       que.question_choice_id,
       que.question_choice_text,
       que.score,
       que.allow_free_text
FROM (SELECT dq.question_id,
             q.question_text,
             NULL AS max_choices,
             NULL AS question_choice_id,
             NULL AS question_choice_text,
             NULL AS score,
             NULL AS allow_free_text
      FROM descriptive_question AS dq
               LEFT JOIN question q on q.question_id = dq.question_id
      UNION ALL
      SELECT sq.question_id,
             q.question_text,
             sq.max_choices,
             qc.question_choice_id,
             qc.question_choice_text,
             qc.score,
             qc.allow_free_text
      FROM selective_question AS sq
               LEFT JOIN question q on q.question_id = sq.question_id
               LEFT JOIN question_choice AS qc ON sq.question_id = qc.question_id) que
WHERE question_id IN (/*[# mb:p="questionIds"]*/ '0C6THP503VM0J' /*[/]*/)
ORDER BY question_id, question_choice_id