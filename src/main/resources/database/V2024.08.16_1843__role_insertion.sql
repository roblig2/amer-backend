-- Wstawienie roli ROLE_ADMIN
insert into roles(id, name) values (gen_random_uuid(), 'ROLE_ADMIN');

-- Wstawienie roli ROLE_USER
insert into roles(id, name) values (gen_random_uuid(), 'ROLE_USER');
insert into user_credential(id, username, password)
values (gen_random_uuid(), 'admin@gmail.com', '$2a$10$xBHk.wXH9e.5yaf/Qch6SuRUfJ99eKD/SWrpNB4O8YPYdqPbH0AVK');

