drop table if exists users;

create table if not exists users
(
    id    bigint generated always as identity,
    name  varchar(255) NOT NULL,
    email varchar(512) NOT NULL,
    constraint pk_user primary key (id),
    constraint uq_user_email unique (email)
);
