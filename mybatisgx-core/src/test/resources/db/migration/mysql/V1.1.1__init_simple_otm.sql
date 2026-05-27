create table if not exists simple_otm_org_complex
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

create table if not exists simple_otm_dept_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    org_id1        bigint      not null comment '',
    org_id2        bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists simple_otm_team_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    dept_id1       bigint      not null comment '',
    dept_id2       bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists simple_otm_user_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    team_id1       bigint      not null comment '',
    team_id2       bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists simple_otm_org_simple
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

create table if not exists simple_otm_dept_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    org_id         bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists simple_otm_team_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    dept_id        bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists simple_otm_user_simple
(
    id             bigint      not null,
    code           varchar(64) not null comment '',
    team_id        bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;
