create database if not exists mybatisx;

create table if not exists user
(
    id             bigint       not null auto_increment,

    role_ids       varchar(256) not null comment '',
    name           varchar(16)  null comment '',
    age            int          null comment '',
    phone          varchar(11)  null comment '',
    email          varchar(32)  null comment '',
    user_name      varchar(32)  not null comment '',
    password       varchar(128) not null comment '',

    input_user_id  bigint       not null comment '输入用户id',
    input_time     datetime     not null comment '输入时间',
    update_user_id bigint       null comment '更新用户id',
    update_time    datetime     null comment '更新时间',
    status         int          not null comment '',
    version        int          not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists user_role
(
    id             bigint   not null auto_increment,

    user_id        bigint   not null comment '',
    role_id        bigint   null comment '',

    input_user_id  bigint   not null comment '输入用户id',
    input_time     datetime not null comment '输入时间',
    update_user_id bigint   null comment '更新用户id',
    update_time    datetime null comment '更新时间',
    status         int      not null comment '',
    version        int      not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists role
(
    id             bigint      not null auto_increment,

    name           varchar(64) not null comment '',
    code           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    status         int         not null comment '',
    version        int         not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists user_detail
(
    id             bigint      not null auto_increment,
    tenant_id      bigint      not null comment '',
    code           varchar(64) not null comment '',
    user_id        bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    status         int         not null comment '',
    version        int         not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;