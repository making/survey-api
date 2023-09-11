DELETE
FROM organization_user
WHERE organization_id = :organizationId
  AND user_id = :userId
  AND role_id = :roleId