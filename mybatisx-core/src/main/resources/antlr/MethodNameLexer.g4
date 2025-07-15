lexer grammar MethodNameLexer;
// ? 表示可有可无（0或1次）
// + 表示至少有一个
// | 表示或的关系
// * 表示有0或者多个
// fragment 定义不被直接解析的规则
INSERT_ACTION: 'insert' | 'insert' ;
DELETE_ACTION: 'delete' | 'remove' ;
UPDATE_ACTION: 'update' | 'update' | 'modify' ;
SELECT_ACTION: 'find' | 'get' | 'select' | 'query' ;
BATCH_ACTION: 'Batch' | 'batch' ;
// WHERE_OP: 'By' ;
WHERE_LINK_OP: 'By' | 'And' | 'Or' ;
CONDITION_OP: 'Lt' | 'Lteq' | 'Gt' | 'Gteq' | 'In' | 'Is' | 'Eq' | 'Not' | 'Like' | 'Between';
DYNAMIC_CONDITION: 'Selective' ;
GROUP_OP: 'GroupBy' ;
ORDER_OP: 'OrderBy' ;
ORDER_OP_DIRECTION: 'Desc' | 'Asc' ;
AGGREGATE_FUNCTION: 'Sum' | 'Count' | 'Avg' | 'Max' | 'Min' |'top' | 'last' | 'first' ;
// antlr是从上向下解析的，常量一定要放在正则的上面
FIELD: [A-Z]+[a-z0-9]+ ;
// 忽略空白符
WS: [ \t\r\n]+ -> skip;