create table if not exists validator_user
(
    id   bigint       not null,
    name varchar(256) not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists mapper_user
(
    id bigint not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;