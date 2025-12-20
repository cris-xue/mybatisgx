create table if not exists custom_condition_multi_id_user
(
    id1  bigint       not null,
    id2  bigint       not null,
    code varchar(256) not null,
    name varchar(256) not null,
    primary key (id1, id2)
) engine = InnoDB
  default charset = utf8mb4;