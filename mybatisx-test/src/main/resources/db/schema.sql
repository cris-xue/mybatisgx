create database if not exists mybatisx;

create table if not exists mybatisx
(
    id             bigint(20) not null auto_increment,
    input_user_id  bigint(20) not null comment '输入用户id',
    input_time     datetime   not null comment '输入时间',
    update_user_id bigint(20) null comment '更新用户id',
    update_time    datetime   null comment '更新时间',

    name           char(16)   null comment '',
    age            int(3)     null comment '',
    user_name      char(32)   not null comment '',
    password       char(128)  not null comment '',
    pay_status     int(2)     not null default 1 comment '',
    version        int(10)    not null default 1 comment '',

    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists user
(
    id             bigint       not null auto_increment,
    input_user_id  bigint       not null comment '输入用户id',
    input_time     datetime     not null comment '输入时间',
    update_user_id bigint       null comment '更新用户id',
    update_time    datetime     null comment '更新时间',

    role_ids       varchar(256) not null comment '',
    name           varchar(16)  null comment '',
    age            int          null comment '',
    phone          varchar(11)  null comment '',
    email          varchar(32)  null comment '',
    user_name      varchar(32)  not null comment '',
    password       varchar(128) not null comment '',
    status         int          not null comment '',
    version        int          not null comment '',

    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;