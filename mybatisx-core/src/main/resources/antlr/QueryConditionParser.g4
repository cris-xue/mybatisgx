// 定义解析器
parser grammar QueryConditionParser ;

options {
    // 表示解析token的词法解析器使用SqlConditionGroupLexer
    tokenVocab = QueryConditionLexer ;
}

sql_condition_statement: condition_group_clause end ;

// 条件组：由多个条件单元和逻辑运算符组成
condition_group_clause: condition_clause* ;
// 条件单元：单个条件 或 括号内的子组
condition_clause: logic_op_clause? (field_condition_op_clause | (LEFT_BRACKET condition_group_clause RIGHT_BRACKET)) ;
// NameLike
field_condition_op_clause: field_clause comparison_op_clause? ;

// 逻辑运算符
logic_op_clause: AND | OR ;
field_clause: (FIELD | RESERVED_WORD)+ ;
comparison_op_clause: COMPARISON_OP ;
left_bracket_clause: LEFT_BRACKET ;
right_bracket_clause: RIGHT_BRACKET ;
// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF ;