drop table if exists items;
drop table if exists users;

create table if not exists users
(
    id    bigint generated always as identity,
    name  varchar(50)  not null,
    email varchar(512) not null,
    constraint pk_users primary key (id),
    constraint uq_users_email unique (email)
);

create table if not exists items
(
    id           bigint generated always as identity,
    name         varchar(100) not null,
    description  varchar(255) not null,
    is_available bool         not null,
    owner_id     bigint       not null,
    request_id   bigint       null,
    constraint pk_items primary key (id),
    constraint fk_items_users foreign key (owner_id) references users (id)
);