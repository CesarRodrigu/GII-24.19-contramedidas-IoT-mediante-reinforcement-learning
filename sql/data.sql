create table if not exists roles
(
    id        bigint auto_increment
        primary key,
    role_name varchar(255) not null,
    constraint UK716hgxp60ym1lifrdgp67xt5k
        unique (role_name)
);

create table if not exists users
(
    id         bigint auto_increment
        primary key,
    created    datetime(6)  null,
    first_name varchar(255) null,
    last_name  varchar(255) null,
    password   varchar(255) null,
    username   varchar(255) null,
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table if not exists trained_model
(
    model_id    bigint auto_increment
        primary key,
    description longtext     null,
    extension   varchar(255) null,
    file        longblob     null,
    file_name   varchar(255) not null,
    name        varchar(255) not null,
    user_id     bigint       not null,
    constraint UK1xyh39dk4u1fosh6x27i0g4sm
        unique (name),
    constraint FK4y3qrqb1w7isopna954ah3p56
        foreign key (user_id) references users (id)
);

create table if not exists users_roles
(
    users_id bigint not null,
    roles_id bigint not null,
    primary key (users_id, roles_id),
    constraint FKa62j07k5mhgifpp955h37ponj
        foreign key (roles_id) references roles (id),
    constraint FKml90kef4w2jy7oxyqv742tsfc
        foreign key (users_id) references users (id)
);



INSERT INTO WebDB.roles (id, role_name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO WebDB.roles (id, role_name)
VALUES (2, 'ROLE_USER');
INSERT INTO WebDB.users (id, created, first_name, last_name, password, username)
-- # Usuario de prueba para la aplicación web
-- # User: admin
-- # Password: admin
VALUES (1, '2025-05-08 10:17:01.452542', 'Nombre de prueba', 'Apellido de prueba',
        '$2a$10$QMAJrLsmAMxiVqTOyDkQo.p5vZrkAdO142SUqSl0ry3oYxiv59nWu', 'admin'); 


INSERT INTO WebDB.users_roles (roles_id, users_id)
VALUES (1, 1);

INSERT INTO WebDB.trained_model (model_id, user_id, name, description, extension, file, file_name)
VALUES (1, 1, 'Empty', 'Archivo comprimido vacío de prueba', '.zip', 0x504B0506000000000000000000000000000000000000, 'Empty.zip');