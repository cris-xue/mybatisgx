// 定义解析器
parser grammar MethodNameParser ;

options {
    // 表示解析token的词法解析器使用MethodNameLexer
    tokenVocab = MethodNameLexer ;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end ;

// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;

insert_statement: insert_clause dynamic_condition_clause ;
insert_clause: INSERT_ACTION ignore_reserved_word_clause ;

delete_statement: delete_clause where_clause dynamic_condition_clause ;
delete_clause: DELETE_ACTION ignore_reserved_word_clause ;

update_statement: update_clause where_clause dynamic_condition_clause ;
update_clause: UPDATE_ACTION ignore_reserved_word_clause ;

select_statement: select_clause where_clause group_clause order_clause ;
select_clause: SELECT_ACTION ignore_reserved_word_clause ;

// 忽略保留关键字
ignore_reserved_word_clause: (RESERVED_WORD)*;

// 动态条件
dynamic_condition_clause: (DYNAMIC_CONDITION)? ;

// 条件语法   ByNameLikeAndAgeEq
where_clause: (where_start_clause (condition_clause)*)? ;
where_start_clause: BY ;
// NameLikeAndAgeEq
condition_clause: condition_item_clause ;
// NameLike
condition_item_clause: logic_op_clause? field_condition_op_clause ;
logic_op_clause: AND | OR ;
// 解析方法名和实体字段
field_condition_op_clause: field_clause comparison_op_clause? ;
comparison_op_clause: COMPARISON_OP ;

// 分组
group_clause: (group_op_clause field_clause)? ;
group_op_clause: GROUP_OP ;

// 排序 OrderByNameDesc、OrderByName
order_clause: (order_op_clause (field_clause order_op_direction_clause?)*)? ;
order_op_clause: ORDER_OP ;
order_op_direction_clause: ORDER_OP_DIRECTION ;

// 聚合函数
aggregate_function_clause: AGGREGATE_FUNCTION ;

field_clause: (FIELD | RESERVED_WORD)+ ;