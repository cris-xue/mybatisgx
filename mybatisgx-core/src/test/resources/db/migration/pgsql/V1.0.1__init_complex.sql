create table test_user_complex
(
    id1            bigint    not null,
    id2            bigint    not null,
    code           varchar(64) null,

    input_user_id  bigint    not null,
    input_time     TIMESTAMP not null,
    update_user_id bigint null,
    update_time    TIMESTAMP null,
    primary key (id1, id2)
);

create table user_detail_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null,
    user_id1       bigint      not null,
    user_id2       bigint      not null,

    input_user_id  bigint      not null,
    input_time     TIMESTAMP   not null,
    update_user_id bigint null,
    update_time    TIMESTAMP null,
    primary key (id1, id2)
);

create table user_detail_item1_complex
(
    id1             bigint      not null,
    id2             bigint      not null,
    code            varchar(64) not null,
    user_detail_id1 bigint      not null,
    user_detail_id2 bigint      not null,

    input_user_id   bigint      not null,
    input_time      TIMESTAMP   not null,
    update_user_id  bigint null,
    update_time     TIMESTAMP null,
    primary key (id1, id2)
);

create table user_detail_item2_complex
(
    id1                   bigint      not null,
    id2                   bigint      not null,
    code                  varchar(64) not null,
    user_detail_item1_id1 bigint      not null,
    user_detail_item1_id2 bigint      not null,

    input_user_id         bigint      not null,
    input_time            TIMESTAMP   not null,
    update_user_id        bigint null,
    update_time           TIMESTAMP null,
    primary key (id1, id2)
);

create table user_role_complex
(
    id1     bigint not null,
    id2     bigint not null,
    user_id bigint not null,
    role_id bigint null,
    primary key (id1, id2)
);

create table test_org_complex
(
    id        varchar(64) not null,
    code      varchar(64) null,
    parent_id varchar(64) not null,

    primary key (id)
);

create table role_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    name           varchar(64) not null,
    code           varchar(64) null,

    input_user_id  bigint      not null,
    input_time     TIMESTAMP   not null,
    update_user_id bigint null,
    update_time    TIMESTAMP null,
    status         int         not null,
    version        int         not null,
    primary key (id1, id2)
);