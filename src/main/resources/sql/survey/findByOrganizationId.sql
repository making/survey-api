SELECT survey_id,
       survey_title,
       start_date_time,
       end_date_time,
       organization_id,
       is_public
FROM survey
WHERE organization_id = /*[# mb:p="organizationId"]*/ '0DHXSDEM47X7Z' /*[/]*/
ORDER BY survey_id