create table role
(
    id          bigint auto_increment
        primary key,
    role        varchar(10)  null,
    role_name   varchar(30)  null,
    description varchar(256) null,
    constraint role
        unique (role)
);

create table users
(
    id       bigint auto_increment
        primary key,
    username varchar(100) not null,
    password varchar(200) not null,
    role     varchar(10)  not null,
    nickname varchar(10)  not null
);

