// 定义解析器
parser grammar MethodNameParser ;

options {
    // 表示解析token的词法解析器使用MethodNameLexer
    tokenVocab = MethodNameLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ignore_reserved_word_clause ;

delete_statement: delete_clause where_clause ;
delete_clause: DELETE_ACTION ignore_reserved_word_clause ;

update_statement: update_clause where_clause ;
update_clause: UPDATE_ACTION ignore_reserved_word_clause ;

select_statement: select_clause aggregate_operation_clause where_clause group_by_clause order_by_clause ;
select_clause: SELECT_ACTION ignore_reserved_word_clause ;

aggregate_operation_clause: (aggregate_function_clause field_clause)? ;

// 条件语法   ByNameLikeAndAgeEq
where_clause: (where_start_clause condition_group_clause)? ;
// NameLikeAndAgeEq
// condition_clause: condition_item_clause ;
// 条件组：由多个条件单元和逻辑运算符组成
condition_group_clause: condition_item_clause* ;
// 条件单元：单个条件 或 括号内的子组
condition_item_clause: logic_op_clause? (field_comparison_op_clause | (left_bracket_clause condition_group_clause right_bracket_clause)) ;
// NameLike
// condition_item_clause: logic_op_clause? field_condition_op_clause ;

// 解析方法名和实体字段
field_comparison_op_clause: field_clause comparison_op_clause? ;

// 分组
group_by_clause: (group_by_op_clause field_clause)? ;

// 排序 OrderByNameDesc、OrderByName
order_by_clause: (order_by_op_clause (field_clause order_by_op_direction_clause?)*)? ;

// 忽略保留关键字
ignore_reserved_word_clause: (RESERVED_WORD)*;
where_start_clause: BY ;
logic_op_clause: AND | OR ;
logic_op_and_clause: AND ;
logic_op_or_clause: OR ;
comparison_op_clause: COMPARISON_OP ;
group_by_op_clause: GROUP_BY_OP ;
order_by_op_clause: ORDER_BY_OP ;
order_by_op_direction_clause: ORDER_BY_OP_DIRECTION ;
// 聚合函数
aggregate_function_clause: AGGREGATE_FUNCTION ;
field_clause: (FIELD | RESERVED_WORD)+ ;
left_bracket_clause: LEFT_BRACKET ;
right_bracket_clause: RIGHT_BRACKET ;
// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;