// 定义解析器
parser grammar MgxqlParser ;

options {
    // 表示解析token的词法解析器使用MgxqlLexer
    tokenVocab = MgxqlLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ;

delete_statement: delete_clause where_clause ;
delete_clause: DELETE_ACTION ;

update_statement: update_clause where_clause ;
update_clause: UPDATE_ACTION ;

// 查询语法：1、select * from User where name = :name and (age < :age or status = :status)
// 查询语法：2、select id, name, inputTime from User where name = :name and (age < :age or status = :status)
// 查询语法：3、select user.id, user.name, user.inputTime, role.name from User user left join Role role where user.name = :user.name and (user.age < :user.age or role.status = :role.status)
// 查询语法：4、select count(1) from User where name = :name and (age < :age or status = :status)
// 查询语法：5、select count(*) from User where name = :name and (age < :age or status = :status)
// 查询语法：6、select max(id) from User where name = :name and (age < :age or status = :status)
// 查询语法：7、select min(id) from User where name = :name and (age < :age or status = :status)
// 查询语法：8、select avg(age) from User where name = :name and (age < :age or status = :status)
select_statement: select_action select_item_clause select_from_clause where_clause? group_by_clause? having_clause? order_by_clause? limit? ;
select_item_clause: select_item (comma_identifier select_item)* ;
select_item: select_column_all | select_column_custom | aggregate_function ;

select_action: SELECT_ACTION ;
select_column_all: (entity_name_alias dot)? select_asterisk ;
select_asterisk: SELECT_ASTERISK ;
select_column_custom: (entity_name_alias dot)? field_name ;

// 聚合函数
aggregate_function: select_count | select_max | select_min | select_avg ;
select_count: select_aggregate_function_count left_bracket (number| select_column_all | field_name) right_bracket ;
select_max: select_aggregate_function_max left_bracket field_name right_bracket ;
select_min: select_aggregate_function_min left_bracket field_name right_bracket ;
select_avg: select_aggregate_function_avg left_bracket field_name right_bracket ;
select_aggregate_function_count: SELECT_COUNT ;
select_aggregate_function_max: SELECT_MAX ;
select_aggregate_function_min: SELECT_MIN ;
select_aggregate_function_avg: SELECT_AVG ;

// from entity_1 left join entity_2
select_from_clause: select_from select_entity select_entity_alias? (select_left_join select_entity select_entity_alias?)* ;
select_from: FROM ;
select_left_join: LEFT JOIN ;
select_entity: entity_name ;
select_entity_alias: entity_name_alias ;

// 条件语法   where name = :name and (age = :age or status = :status)
where_clause: where_start condition_expression ;

// 分层处理条件表达式，明确运算符优先级
condition_expression: or_expression ;

// OR 运算符 (最低优先级)
or_expression: and_expression (logic_or and_expression)* ;

// AND 运算符 (较高优先级)
and_expression: condition_term (logic_and condition_term)* ;

// 条件项：基础条件或括号表达式
condition_term: field_comparison_op | (left_bracket condition_expression right_bracket) ;

// 解析方法名和实体字段
field_comparison_op: where_param_name_field_access_chain (field_comparison_op_param | field_comparison_op_not_param) ;
field_comparison_op_param: (relational_op | matching_op) param_colon where_param_value_field_access_chain ;
field_comparison_op_not_param: comparison_op_null ;

// group by phone
group_by_clause: group_by (entity_field_access_chain comma_identifier?)+ ;

// having max(age) > :age
having_clause: having (aggregate_function having_comparison_op_param)+ ;
having_comparison_op_param: relational_op param_colon where_param_value_field_access_chain ;

// 排序 order by name desc、order by name
order_by_clause: order_by order_by_item+ ;
order_by_item: entity_field_access_chain order_by_direction? ;

// 分页 limit 2, 10
limit: limit_identifier offset comma_identifier size ;
limit_identifier: LIMIT_IDENTIFIER ;
offset: NUMBER ;
comma_identifier: COMMA ;
size: NUMBER ;

where_start: WHERE ;
logic_and: LOGIC_AND ;
logic_or: LOGIC_OR ;

relational_op: comparison_op_lt
    | comparison_op_lt_eq
    | comparison_op_gt
    | comparison_op_gt_eq
    | comparison_op_eq
    | comparison_op_not_eq
    ;
comparison_op_lt: COMPARISON_OP_LT ;
comparison_op_lt_eq: COMPARISON_OP_LT_EQ ;
comparison_op_gt: COMPARISON_OP_GT ;
comparison_op_gt_eq: COMPARISON_OP_GT_EQ ;
comparison_op_eq: COMPARISON_OP_EQ ;
comparison_op_not_eq: COMPARISON_OP_NOT_EQ ;

matching_op: comparison_op_not? (
    comparison_op_between
    | comparison_op_in
    | comparison_op_like
    | comparison_op_left_like
    | comparison_op_right_like
    );
comparison_op_not: COMPARISON_OP_NOT ;
comparison_op_between: COMPARISON_OP_BETWEEN ;
comparison_op_in: COMPARISON_OP_IN ;
comparison_op_like: COMPARISON_OP_LIKE ;
comparison_op_left_like: COMPARISON_OP_LEFT_LIKE ;
comparison_op_right_like: COMPARISON_OP_RIGHT_LIKE ;

comparison_op_null: comparison_op_is_null | comparison_op_is_not_null ;
comparison_op_is_null: COMPARISON_OP_IS_NULL ;
comparison_op_is_not_null: COMPARISON_OP_IS_NOT_NULL ;

where_param_name_field_access_chain: param_name_field_access_chain ;
param_colon: COLON ;
where_param_value_field_access_chain: param_value_field_access_chain ;

having: HAVING ;

group_by: GROUP_BY ;

order_by: ORDER_BY ;
order_by_direction: ORDER_BY_DIRECTION ;

entity_name: UPPER_NAME ;
entity_name_alias: LOWER_NAME ;
field_name: LOWER_NAME ;

// 参数字段访问链，role.name = :role.name   menu.name = :role.menu.name
entity_field_access_chain: entity_name_alias (dot field_name)? ;
param_name_field_access_chain: entity_name_alias (dot field_name)? ;
param_value_field_access_chain: field_name (dot field_name)* ;

left_bracket: LEFT_BRACKET ;
right_bracket: RIGHT_BRACKET ;
dot: DOT ;
number: NUMBER ;

// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;