SELECT r.role_id,
       r.role_name,
       rp.permission_id
FROM role AS r
         LEFT JOIN role_permission rp ON r.role_id = rp.role_id
WHERE r.role_name = :roleName