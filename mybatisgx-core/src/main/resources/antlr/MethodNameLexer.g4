lexer grammar MethodNameLexer;

import MethodNameLexerStatus;

INSERT_ACTION: 'insert' | 'add' ;
DELETE_ACTION: 'delete' | 'remove' ;
UPDATE_ACTION: 'update' | 'update' | 'modify' ;
SELECT_ACTION: 'find' | 'get' | 'select' | 'query' ;
BY: 'By' ;
LOGIC_OP_AND: 'And' ;
LOGIC_OP_OR: 'Or' ;
// 比较运算符
COMPARISON_NOT_OP: 'Not';
COMPARISON_OP: 'Lt'
    | 'Lteq'
    | 'Gt'
    | 'Gteq'
    | 'In'
    | 'Eq' | 'Equal'
    | 'Like' | 'StartingWith' | 'EndingWith'
    | 'Between'
    ;
COMPARISON_NULL_OP: 'Null' | 'IsNull' | 'IsNotNull' | 'NotNull' ;
GROUP_BY_OP: 'GroupBy' ;
ORDER_BY_OP: 'OrderBy' ;
ORDER_BY_OP_DIRECTION: 'Desc' | 'Asc' ;
AGGREGATE_FUNCTION: 'Sum'
    | 'Count'
    | 'Avg'
    | 'Max'
    | 'Min'
    | 'Distinct'
    ;
LIMIT_OP: 'Limit' | 'Top' | 'Last' | 'First' ;
// 左括号
LEFT_BRACKET: '(' ;
// 右括号
RIGHT_BRACKET: ')' ;
// 保留关键字
RESERVED_WORD: 'List' | 'One' | 'Page' | 'Batch' | 'Selective' ;
NUMBER: [0-9]+ ;
// antlr是从上向下解析的，常量一定要放在正则的上面
FIELD: [A-Z]+[a-z0-9]+ ;
// 忽略空白符
WS: [ \t\r\n]+ -> skip ;