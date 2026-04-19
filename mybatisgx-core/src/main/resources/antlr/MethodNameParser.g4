// 定义解析器
parser grammar MethodNameParser ;

options {
    // 表示解析token的词法解析器使用MethodNameLexer
    tokenVocab = MethodNameLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

insert_statement: insert_clause business_semantic* ;
insert_clause: INSERT_ACTION ;

delete_statement: delete_clause business_semantic* where_clause ;
delete_clause: DELETE_ACTION ;

update_statement: update_clause business_semantic* where_clause ;
update_clause: UPDATE_ACTION ;

// 查询语法：1、findList findAll 2、findTop5 3、count  规则：查询方法名条件以By开始，查询实体不支持By语法、OrderBy语法
select_statement: select_item_clause business_semantic* limit? where_clause? order_by_clause? ;
select_item_clause: select_column | select_count ;
select_column: SELECT_COLUMN_ACTION ;
select_count: SELECT_COUNT_ACTION ;

// 条件语法   ByNameLikeAndAgeEq
where_clause: where_start condition_expression ;

// 分层处理条件表达式，明确运算符优先级
condition_expression: or_expression ;

// OR 运算符 (最低优先级)
or_expression: and_expression (logic_op_or and_expression)* ;

// AND 运算符 (较高优先级)
and_expression: condition_term (logic_op_and condition_term)* ;

// 条件项：基础条件或括号表达式
condition_term: field_comparison_op_clause | (left_bracket condition_expression right_bracket) ;

// 解析方法名和实体字段
field_comparison_op_clause: field_clause ((comparison_not_op? comparison_op) | comparison_null_op)* ;

// 排序 OrderByNameDesc、OrderByName
order_by_clause: order_by order_by_item_clause+ ;
order_by_item_clause: field_clause order_by_direction? ;

// 分页
limit: limit_top ;

// 业务语义（在解析中忽略）
business_semantic: FIELD ;
where_start: BY ;
logic_op_and: LOGIC_OP_AND ;
logic_op_or: LOGIC_OP_OR ;
comparison_op: COMPARISON_OP ;
comparison_not_op: COMPARISON_NOT_OP ;
comparison_null_op: COMPARISON_NULL_OP ;
order_by: ORDER_BY ;
order_by_direction: ORDER_BY_DIRECTION ;
limit_top: LIMIT_TOP ;
field_clause: (FIELD)+ ;
left_bracket: LEFT_BRACKET ;
right_bracket: RIGHT_BRACKET ;
// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;