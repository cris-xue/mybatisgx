create table if not exists join_mtm_user_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_user_role_complex
(
    id       bigint  not null,
    user_id1 bigint  not null comment '',
    user_id2 bigint  not null comment '',
    role_id1 bigint  not null comment '',
    role_id2 bigint  not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_role_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_role_menu_complex
(
    id       bigint  not null,
    role_id1 bigint  not null comment '',
    role_id2 bigint  not null comment '',
    menu_id1 bigint  not null comment '',
    menu_id2 bigint  not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_menu_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_menu_resource_complex
(
    id           bigint  not null,
    menu_id1     bigint  not null comment '',
    menu_id2     bigint  not null comment '',
    resource_id1 bigint  not null comment '',
    resource_id2 bigint  not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_resource_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_user_simple
(
    id             bigint      not null,
    code           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_user_role_simple
(
    id      bigint not null,
    user_id bigint not null comment '',
    role_id bigint not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_role_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_role_menu_simple
(
    id      bigint not null,
    role_id bigint not null comment '',
    menu_id bigint not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_menu_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_menu_resource_simple
(
    id          bigint not null,
    menu_id     bigint not null comment '',
    resource_id bigint not null comment '',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists join_mtm_resource_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    name           varchar(64) null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;
