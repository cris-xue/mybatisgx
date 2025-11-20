// 定义解析器
parser grammar MethodNameParser ;

options {
    // 表示解析token的词法解析器使用MethodNameLexer
    tokenVocab = MethodNameLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ignore_reserved_word ;

delete_statement: delete_clause where_clause ;
delete_clause: DELETE_ACTION ignore_reserved_word ;

update_statement: update_clause where_clause ;
update_clause: UPDATE_ACTION ignore_reserved_word ;

select_statement: select_clause
    aggregate_function_expression?
    where_clause
    group_by_clause?
    order_by_clause?
    limit_op_expression?
    ;
select_clause: SELECT_ACTION ignore_reserved_word ;

aggregate_function_expression: aggregate_function field_clause ;

// 条件语法   ByNameLikeAndAgeEq
where_clause: (where_start condition_expression)? ignore_reserved_word? ;

// 分层处理条件表达式，明确运算符优先级
condition_expression: or_expression ;

// OR 运算符 (最低优先级)
or_expression: and_expression (logic_op_or and_expression)* ;

// AND 运算符 (较高优先级)
and_expression: condition_term (logic_op_and condition_term)* ;

// 条件项：基础条件或括号表达式
condition_term: field_comparison_op_clause | (left_bracket condition_expression right_bracket) ;

// 解析方法名和实体字段
field_comparison_op_clause: field_clause comparison_op? ;

// 分组
group_by_clause: group_by_op field_clause ;

// 排序 OrderByNameDesc、OrderByName
order_by_clause: order_by_op order_by_item_clause* ;
order_by_item_clause: field_clause order_by_op_direction ;

// 分页
limit_op_expression: limit_op limit_op_number ;

// 忽略保留关键字
ignore_reserved_word: (RESERVED_WORD)*;
where_start: BY ;
logic_op_and: LOGIC_OP_AND ;
logic_op_or: LOGIC_OP_OR ;
comparison_op: (COMPARISON_NOT_OP? COMPARISON_OP) | COMPARISON_NULL_OP ;
group_by_op: GROUP_BY_OP ;
order_by_op: ORDER_BY_OP ;
order_by_op_direction: ORDER_BY_OP_DIRECTION ;
// 聚合函数
aggregate_function: AGGREGATE_FUNCTION ;
limit_op: LIMIT_OP ;
limit_op_number: NUMBER ;
field_clause: (FIELD)+ ;
left_bracket: LEFT_BRACKET ;
right_bracket: RIGHT_BRACKET ;
// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;