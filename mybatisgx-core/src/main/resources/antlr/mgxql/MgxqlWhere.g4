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