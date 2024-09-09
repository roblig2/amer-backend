--liquibase formatted sql
-- changeset rligman:1
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE user_credential (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 username VARCHAR(50) NOT NULL UNIQUE,
                                 password VARCHAR(120) NOT NULL
);
CREATE TABLE user_credential_role (
                           user_id UUID,
                           role_id UUID,
                           PRIMARY KEY (user_id, role_id),
                           FOREIGN KEY (user_id) REFERENCES user_credential(id) ON DELETE CASCADE,
                           FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);


CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       is_driver BOOLEAN NOT NULL,
                       phone_number VARCHAR(20),
                       date_of_birth DATE,
                       user_credential_id UUID,
                       FOREIGN KEY (user_credential_id) REFERENCES user_credential(id) ON DELETE CASCADE
);

CREATE TABLE USER_DATE (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           date DATE NOT NULL,
                           remark VARCHAR(255),
                           user_id UUID,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



CREATE TABLE events (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(255),
                        location VARCHAR(255),
                        date DATE,
                        date_packing DATE,
                        required_users INT,
                        required_drivers INT,
                        is_missing_people BOOLEAN,
                        event_time VARCHAR(50),
                        packing_time VARCHAR(50)
);


CREATE TABLE event_available_users (
                                       event_id UUID NOT NULL,
                                       user_id UUID NOT NULL,
                                       PRIMARY KEY (event_id, user_id),
                                       FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE event_available_packing_users (
                                               event_id UUID NOT NULL,
                                               user_id UUID NOT NULL,
                                               PRIMARY KEY (event_id, user_id),
                                               FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE event_blacklisted_users (
                                         event_id UUID NOT NULL,
                                         user_id UUID NOT NULL,
                                         PRIMARY KEY (event_id, user_id),
                                         FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE event_confirmed_users (
                                       event_id UUID NOT NULL,
                                       user_id UUID NOT NULL,
                                       PRIMARY KEY (event_id, user_id),
                                       FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMIT;
