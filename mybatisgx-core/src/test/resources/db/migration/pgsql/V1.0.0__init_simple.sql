create table test_user_simple
(
    id             bigint       not null,
    role_ids       varchar(256) not null,
    name           varchar(256) null,
    age            int          null,
    phone          varchar(256) null,
    email          varchar(256) null,
    user_name      varchar(256) not null,
    password       varchar(256) not null,

    input_user_id  bigint       not null,
    input_time     TIMESTAMP    not null,
    update_user_id bigint       null,
    update_time    TIMESTAMP    null,
    status         int          not null,
    version        int          not null,
    primary key (id)
);

create table user_detail_simple
(
    id             bigint      not null,
    code           varchar(64) not null,
    user_id        bigint      not null,

    input_user_id  bigint      not null,
    input_time     TIMESTAMP   not null,
    update_user_id bigint      null,
    update_time    TIMESTAMP   null,
    primary key (id)
);

create table user_role_simple
(
    id      bigint not null,
    user_id bigint not null,
    role_id bigint null,
    primary key (id)
);

create table role_simple
(
    id             bigint      not null,
    name           varchar(64) not null,
    code           varchar(64) null,

    input_user_id  bigint      not null,
    input_time     TIMESTAMP   not null,
    update_user_id bigint      null,
    update_time    TIMESTAMP   null,
    status         int         not null,
    version        int         not null,
    primary key (id)
);