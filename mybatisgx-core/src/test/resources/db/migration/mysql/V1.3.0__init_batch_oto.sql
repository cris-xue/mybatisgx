create table if not exists batch_oto_user_complex
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

create table if not exists batch_oto_user_detail_complex
(
    id1            bigint      not null,
    id2            bigint      not null,
    code           varchar(64) not null comment '',
    user_id1       bigint      not null comment '',
    user_id2       bigint      not null comment '',

    input_user_id  bigint      not null comment '输入用户id',
    input_time     datetime    not null comment '输入时间',
    update_user_id bigint      null comment '更新用户id',
    update_time    datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists batch_oto_user_detail_item1_complex
(
    id1             bigint      not null,
    id2             bigint      not null,
    code            varchar(64) not null comment '',
    user_detail_id1 bigint      not null comment '',
    user_detail_id2 bigint      not null comment '',

    input_user_id   bigint      not null comment '输入用户id',
    input_time      datetime    not null comment '输入时间',
    update_user_id  bigint      null comment '更新用户id',
    update_time     datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists batch_oto_user_detail_item2_complex
(
    id1                   bigint      not null,
    id2                   bigint      not null,
    code                  varchar(64) not null comment '',
    user_detail_item1_id1 bigint      not null comment '',
    user_detail_item1_id2 bigint      not null comment '',

    input_user_id         bigint      not null comment '输入用户id',
    input_time            datetime    not null comment '输入时间',
    update_user_id        bigint      null comment '更新用户id',
    update_time           datetime    null comment '更新时间',
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;