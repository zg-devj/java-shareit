-- для тестирования
drop table if exists requests;
drop table if exists comments;
drop table if exists bookings;
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

create table if not exists bookings
(
    id         bigint generated always as identity,
    start_date timestamp without time zone,
    end_date   timestamp without time zone,
    item_id    bigint not null,
    booker_id  bigint not null,
    status     varchar(8),
    constraint pk_bookings primary key (id),
    constraint fk_bookings_items foreign key (item_id) references items (id),
    constraint fk_bookings_users foreign key (booker_id) references users (id)
);

create table if not exists comments
(
    id        bigint generated always as identity,
    text      varchar(512) not null,
    item_id   bigint       not null,
    author_id bigint       not null,
    created   timestamp without time zone,
    constraint pk_comments primary key (id),
    constraint fk_comments_items foreign key (item_id) references items (id),
    constraint fk_comments_users foreign key (author_id) references users (id)
);

create table if not exists requests
(
    id        bigint generated always as identity,
    description varchar (512),
    requestor_id bigint       not null,
    created   timestamp without time zone,
    constraint pk_requests primary key (id),
    constraint fk_requests_users foreign key (requestor_id) references users (id)
);