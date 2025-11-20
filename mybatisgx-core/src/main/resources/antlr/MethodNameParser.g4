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

select_statement: select_clause
    aggregate_operation_clause?
    limit_op_expression?
    where_clause
    group_by_clause?
    order_by_clause?
    ;
select_clause: SELECT_ACTION ignore_reserved_word_clause ;

aggregate_operation_clause: aggregate_function_clause field_clause ;

// 条件语法   ByNameLikeAndAgeEq
where_clause: (where_start_clause condition_expression)? ignore_reserved_word_clause? ;

// 分层处理条件表达式，明确运算符优先级
condition_expression: or_expression ;

// OR 运算符 (最低优先级)
or_expression: and_expression (logic_op_or and_expression)* ;

// AND 运算符 (较高优先级)
and_expression: condition_term (logic_op_and condition_term)* ;

// 条件项：基础条件或括号表达式
condition_term: field_comparison_op_clause | (left_bracket_clause condition_expression right_bracket_clause) ;

// 解析方法名和实体字段
field_comparison_op_clause: field_clause comparison_op_clause? ;

// 分组
group_by_clause: group_by_op_clause field_clause ;

// 排序 OrderByNameDesc、OrderByName
order_by_clause: order_by_op_clause order_by_item_clause* ;
order_by_item_clause: field_clause order_by_op_direction_clause ;

// 分页
limit_op_expression: limit_op ;

// 忽略保留关键字
ignore_reserved_word_clause: (RESERVED_WORD)*;
where_start_clause: BY ;
logic_op_and: LOGIC_OP_AND ;
logic_op_or: LOGIC_OP_OR ;
comparison_op_clause: (COMPARISON_NOT_OP? COMPARISON_OP) | COMPARISON_NULL_OP ;
group_by_op_clause: GROUP_BY_OP ;
order_by_op_clause: ORDER_BY_OP ;
order_by_op_direction_clause: ORDER_BY_OP_DIRECTION ;
// 聚合函数
aggregate_function_clause: AGGREGATE_FUNCTION ;
field_clause: (FIELD)+ ;
limit_op: LIMIT_OP ;
left_bracket_clause: LEFT_BRACKET ;
right_bracket_clause: RIGHT_BRACKET ;
// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;