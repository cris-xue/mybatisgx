// 定义解析器
parser grammar MgxqlParser ;

options {
    // 表示解析token的词法解析器使用MgxqlLexer
    tokenVocab = MgxqlLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ;

delete_statement: delete_clause modify_entity where_clause ;
delete_clause: DELETE_ACTION ;

update_statement: update_clause modify_entity where_clause ;
update_clause: UPDATE_ACTION ;

modify_entity: entity_name ;

// 查询语法：1、select * from User where name = :name and (age < :age or status = :status)
// 查询语法：2、select id, name, inputTime from User where name = :name and (age < :age or status = :status)
// 查询语法：3、select user.id, user.name, user.inputTime, role.name from User user left join Role role where user.name = :user.name and (user.age < :user.age or role.status = :role.status)
// 查询语法：4、select count(1) from User where name = :name and (age < :age or status = :status)
// 查询语法：5、select count(*) from User where name = :name and (age < :age or status = :status)
// 查询语法：6、select count(id) from User where name = :name and (age < :age or status = :status)
// 查询语法：7、select max(id) from User where name = :name and (age < :age or status = :status)
// 查询语法：8、select min(id) from User where name = :name and (age < :age or status = :status)
// 查询语法：9、select avg(age) from User where name = :name and (age < :age or status = :status)
select_statement: select_action select_item_clause select_from_clause where_clause? group_by_clause? having_clause? order_by_clause? limit_clause? ;
select_item_clause: select_item (comma select_item)* ;
select_item: (select_column_all | select_column_custom | aggregate_function) select_item_alias? ;

select_column_all: select_asterisk | entity_name_alias dot select_asterisk ;
select_column_custom: field_reference ;
select_action: SELECT_ACTION ;
select_asterisk: SELECT_ASTERISK ;
// 查询字段列表，name、role.name、role.menu.name   支持嵌套属性链
select_item_alias: alias (entity_name_alias dot)* field_name ;

// 聚合函数
aggregate_function: aggregate_function_normal | aggregate_function_count ;

// MAX、MIN、AVG、SUM，只支持字段参数
aggregate_function_normal: aggregate_function_name left_bracket aggregate_function_argument right_bracket ;
aggregate_function_name: select_max | select_min | select_avg | select_sum ;
aggregate_function_argument: field_reference ;
select_max: SELECT_MAX ;
select_min: SELECT_MIN ;
select_avg: SELECT_AVG ;
select_sum: SELECT_SUM ;

// count聚合函数，需要支持count(*)、count(1)、count（field）
aggregate_function_count: select_count left_bracket aggregate_function_count_argument right_bracket ;
aggregate_function_count_argument: number | select_asterisk | field_reference ;
select_count: SELECT_COUNT ;

// from User user left join Role role on aaa.id = bbb.aaa_id
select_from_clause: select_from select_primary_entity select_join_entity* ;
select_primary_entity: select_entity select_entity_alias? ;
select_join_entity: select_left_join select_entity select_entity_alias? select_on select_on_expression ;
select_entity: entity_name ;
select_entity_alias: entity_name_alias ;
select_from: FROM ;
select_left_join: LEFT JOIN ;
select_on: ON ;
// user left join role on user = role
select_on_expression: entity_name_alias on_equal entity_name_alias ;
on_equal: EQUAL ;

// 条件语法   where name = :name and (age = :age or status = :status)
// 条件语法   where ?name = :name and (?age = :age or status = :status)
where_clause: where_start condition_or_expression ;
// OR 运算符 (最低优先级)   分层处理条件表达式，明确运算符优先级
condition_or_expression: condition_and_expression (logic_or condition_and_expression)* ;
// AND 运算符 (较高优先级)
condition_and_expression: condition_term (logic_and condition_term)* ;
// 条件项：基础条件或括号表达式
condition_term: condition_comparison | (left_bracket condition_or_expression right_bracket) ;
// 解析方法名和实体字段
condition_comparison: question_mark? field_reference (condition_comparison_param | condition_comparison_not_param) ;
condition_comparison_param: (relational_op | matching_op) condition_value ;
condition_comparison_not_param: comparison_op_null ;
condition_value: parameter_reference | number ;

// group by phone
group_by_clause: group_by group_by_expression ;
group_by_expression: field_reference (comma field_reference)* ;

// 1、having max(age) > :age count(id) > :count   2、having max(age) > :age AND count(id) > :count
having_clause: having having_or_expression ;
// OR 层，AND 优先
having_or_expression: having_and_expression (logic_or having_and_expression)* ;
// AND 层，每个项可能是单条聚合比较，也可能是括号包裹的表达式
having_and_expression: having_term (logic_and having_term)* ;
// 单条表达式或括号表达式
having_term: having_comparison | left_bracket having_or_expression right_bracket ;
// 单条聚合函数比较，例如 max(age) > :age 或 count(id) > 10    比较值只支持参数或数字，不允许字段引用。
having_comparison: aggregate_function relational_op having_value ;
having_value: parameter_reference | number ;

// 排序 order by name desc、order by name , age
order_by_clause: order_by order_by_expression (comma order_by_expression)* ;
order_by_expression: field_reference order_by_direction? ;

// 只支持固定分页 limit 2, 10 这种形式，动态分页采用Pageable或者PageHelper
limit_clause: limit offset comma size ;
limit: LIMIT ;
offset: NUMBER ;
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
comparison_op_eq: EQUAL ;
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

having: HAVING ;
group_by: GROUP_BY ;
order_by: ORDER_BY ;
order_by_direction: ORDER_BY_DIRECTION ;

// 字段引用，user.name  role.status   只允许实体别名.字段，不支持嵌套属性链
field_reference: field_name | entity_name_alias dot field_name ;
// 参数引用（对应查询实体和@Param）。:name   :表示从根节点开始取值
parameter_reference: param_colon field_name (dot field_name)* ;

alias: AS ;

entity_name: UPPER_NAME ;
entity_name_alias: LOWER_NAME | QUOTED_NAME ;
field_name: LOWER_NAME | QUOTED_NAME ;

left_bracket: LEFT_BRACKET ;
right_bracket: RIGHT_BRACKET ;
dot: DOT ;
param_colon: COLON ;
comma: COMMA ;
question_mark: QUESTION_MARK ;
number: NUMBER ;

// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;