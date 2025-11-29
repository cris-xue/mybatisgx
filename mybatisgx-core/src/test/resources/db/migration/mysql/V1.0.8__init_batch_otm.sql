create table if not exists batch_otm_user
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

create table if not exists batch_otm_org
(
    id        varchar(64) not null,
    code      varchar(64) null comment '',
    parent_id varchar(64) not null comment '',

    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;