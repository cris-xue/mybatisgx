// 定义解析器
parser grammar SqlConditionGroupParser ;

options {
    // 表示解析token的词法解析器使用SqlConditionGroupLexer
    tokenVocab = SqlConditionGroupLexer ;
}

sql_condition_statement: condition_group_clause end ;

// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;

// 条件组：由多个条件单元和逻辑运算符组成
condition_group_clause: conditionUnit* (logic_op conditionUnit)* ;
// 条件单元：单个条件 或 括号内的子组
conditionUnit: where_clause | (LEFT_BRACKET condition_group_clause RIGHT_BRACKET) ;

// NameLikeAndAgeEq
where_clause: (where_item)+ ;
// NameLikeAnd
where_item:  field_condition_op_clause (logic_op field_condition_op_clause)* ;
// NameLike
field_condition_op_clause: field_clause (condition_op_clause)? ;
condition_op_clause: CONDITION_OP ;

// 逻辑运算符
logic_op: AND | OR ;
field_clause: FIELD+ ;
left_bracket_clause: LEFT_BRACKET ;
right_bracket_clause: RIGHT_BRACKET ;