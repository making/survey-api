SELECT o.organization_id, o.organization_name, ou.user_id, ou.role_id
FROM organization AS o
         LEFT JOIN organization_user ou on o.organization_id = ou.organization_id
WHERE o.organization_id = :organizationId