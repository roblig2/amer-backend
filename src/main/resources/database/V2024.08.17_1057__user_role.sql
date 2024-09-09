insert into user_credential_role(user_id, role_id)
values (
           (select id from user_credential where username = 'admin@gmail.com'),
           (select id from roles where name = 'ROLE_ADMIN')
       );
