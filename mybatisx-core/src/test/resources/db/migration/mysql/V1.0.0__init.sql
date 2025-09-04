create table if not exists test_user
(
    id             bigint       not null auto_increment,

    role_ids       varchar(256) not null comment '',
    name           varchar(256) null comment '',
    age            int          null comment '',
    phone          varchar(256) null comment '',
    email          varchar(256) null comment '',
    user_name      varchar(256) not null comment '',
    password       varchar(256) not null comment '',

    input_user_id  bigint       not null comment '输入用户id',
    input_time     datetime     not null comment '输入时间',
    update_user_id bigint       null comment '更新用户id',
    update_time    datetime     null comment '更新时间',
    status         int          not null comment '',
    version        int          not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists user_detail
(
    id             bigint      not null auto_increment,
    code           varchar(64) not null comment '',
    user_id        bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;