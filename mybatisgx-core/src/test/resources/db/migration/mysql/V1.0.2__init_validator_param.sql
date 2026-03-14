create table if not exists insert_validator_user
(
    id   bigint       not null,
    name varchar(256) not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists insert_mapper_user
(
    id bigint not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists delete_validator_user
(
    id   bigint       not null,
    name varchar(256) not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists delete_mapper_user
(
    id bigint not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists update_validator_user
(
    id   bigint       not null,
    name varchar(256) not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists update_mapper_user
(
    id bigint not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists select_validator_user
(
    id   bigint       not null,
    name varchar(256) not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table if not exists select_mapper_user
(
    id bigint not null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;