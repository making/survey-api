SELECT u.user_id,
       u.email,
       u.password,
       o.organization_id,
       o.organization_name,
       p.permission_id,
       p.resource,
       p.verb
FROM "user" AS u
         LEFT JOIN public.organization_user ou on u.user_id = ou.user_id
         LEFT JOIN public.organization o on o.organization_id = ou.organization_id
         LEFT JOIN public.role r on r.role_id = ou.role_id
         LEFT JOIN public.role_permission rp on r.role_id = rp.role_id
         LEFT JOIN public.permission p on p.permission_id = rp.permission_id
WHERE email = :email
ORDER BY user_id, organization_id, permission_id