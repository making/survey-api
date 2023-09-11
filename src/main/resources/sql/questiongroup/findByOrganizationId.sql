SELECT question_group_id,
       organization_id,
       question_group_title,
       question_group_type
FROM question_group
WHERE organization_id = /*[# mb:p="organizationId"]*/ '0DHXSDEM47X7Z' /*[/]*/
ORDER BY question_group_id