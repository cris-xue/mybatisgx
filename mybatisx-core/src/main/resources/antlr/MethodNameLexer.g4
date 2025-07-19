lexer grammar MethodNameLexer;

import MethodNameLexerStatus;

INSERT_ACTION: 'insert' | 'insert' ;
DELETE_ACTION: 'delete' | 'remove' ;
UPDATE_ACTION: 'update' | 'update' | 'modify' ;
SELECT_ACTION: 'find' | 'get' | 'select' | 'query' ;
BY: 'By' ;
AND: 'And' ;
OR: 'Or' ;
// 比较运算符
COMPARISON_OP: 'Lt' | 'Lteq' | 'Gt' | 'Gteq' | 'In' | 'Is' | 'Eq' | 'Not' | 'Like' | 'Between';
GROUP_OP: 'GroupBy' ;
ORDER_OP: 'OrderBy' ;
ORDER_OP_DIRECTION: 'Desc' | 'Asc' ;
AGGREGATE_FUNCTION: 'Sum' | 'Count' | 'Avg' | 'Max' | 'Min' |'top' | 'last' | 'first' ;
DYNAMIC_CONDITION: 'Selective' ;
RESERVED_WORD: 'List' | 'One' | 'Page' | 'Batch' ;
// antlr是从上向下解析的，常量一定要放在正则的上面
FIELD: [A-Z]+[a-z0-9]+ ;
// 忽略空白符
WS: [ \t\r\n]+ -> skip;