create table if not exists mgxsql_test_user
(
    id        bigint       not null,
    user_name varchar(256) null,
    age       int          null,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;
