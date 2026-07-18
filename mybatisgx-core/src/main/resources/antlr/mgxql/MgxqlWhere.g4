// 定义解析器
parser grammar MgxqlWhere ;

options {
    // 表示解析token的词法解析器使用MgxqlLexer
    tokenVocab = MgxqlLexer ;
}

import MgxqlCommon ;

// 条件语法   where name = :name and (age = :age or status = :status)
// 条件语法   where #[name = :name] #[and age = :age or status = :status]
// 条件语法   where #[name = :name] #if(status == 5)[and age = :age or status = :status]
// where_clause: where_start (condition_or_expression | dynamic_expression*) ;
where_clause: where_start (condition_and_expression | dynamic_expression)* ;
// OR 运算符 (最低优先级)   分层处理条件表达式，明确运算符优先级
condition_or_expression: condition_and_expression (logic_or condition_and_expression)* ;
// AND 运算符 (较高优先级)
condition_and_expression: condition_term (logic_and condition_term)* ;
// 条件项：基础条件或括号表达式
condition_term: condition_comparison | (left_bracket condition_or_expression right_bracket) ;
// 解析方法名和实体字段
condition_comparison: field_reference (condition_comparison_param | condition_comparison_not_param) ;
condition_comparison_param: (relational_op | matching_op) condition_value ;
condition_comparison_not_param: comparison_op_null ;
condition_value: parameter_reference | number ;

// 动态表达式
dynamic_expression: if_expression | choose_expression | optional_expression ;

if_expression: HASH IF left_bracket guard_expression right_bracket left_square condition_or_expression right_square ;
choose_expression: HASH CHOOSE left_square choose_body right_square ;
optional_expression: HASH left_square condition_or_expression right_square ;

choose_body: when_expression+ otherwise_expression? ;
when_expression: HASH WHEN left_bracket guard_expression right_bracket left_square condition_or_expression right_square ;
otherwise_expression: HASH OTHERWISE left_square condition_or_expression right_square ;

left_square: LEFT_SQUARE ;
right_square: RIGHT_SQUARE ;
guard_expression: expression ;

/* 守卫表达式 */
expression: expression_or ;

expression_or: expression_and (logic_or expression_and)* ;

expression_and: expression_atom (logic_and expression_atom)* ;

expression_atom: expression_compare | left_bracket expression_or right_bracket ;

expression_compare: expression_value expression_operator expression_value ;

expression_value: parameter_reference | literal ;

expression_operator:
      EQUAL
    | COMPARISON_OP_NOT_EQ
    | COMPARISON_OP_GT
    | COMPARISON_OP_GT_EQ
    | COMPARISON_OP_LT
    | COMPARISON_OP_LT_EQ
;

literal: NUMBER | STRING ;

/* 条件原子语法 */
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