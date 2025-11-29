create table test_user_complex_rl_sp
(
    id1            NUMBER(19)  not null,
    id2            NUMBER(19)  not null,
    code           varchar(64) null,

    input_user_id  NUMBER(19)  not null,
    input_time     TIMESTAMP   not null,
    update_user_id NUMBER(19)  null,
    update_time    TIMESTAMP   null,
    primary key (id1, id2)
);

create table user_detail_complex_rl_sp
(
    id1            NUMBER(19)  not null,
    id2            NUMBER(19)  not null,
    code           varchar(64) not null,
    user_id1       NUMBER(19)  not null,
    user_id2       NUMBER(19)  not null,

    input_user_id  NUMBER(19)  not null,
    input_time     TIMESTAMP   not null,
    update_user_id NUMBER(19)  null,
    update_time    TIMESTAMP   null,
    primary key (id1, id2)
);