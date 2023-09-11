SELECT survey_id,
       survey_title,
       start_date_time,
       end_date_time,
       organization_id,
       is_public
FROM survey
WHERE survey_id = /*[# mb:p="surveyId"]*/ '0C6THP503VM0J' /*[/]*/