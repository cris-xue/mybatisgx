lexer grammar MethodNameLexer;
// ? 表示可有可无
// + 表示至少有一个
// | 表示或的关系
// * 表示有0或者多个
// fragment 定义不被直接解析的规则
INSERT_ACTION: 'insert' ;
DELETE_ACTION: 'delete' ;
UPDATE_ACTION: 'update' ;
SELECT_ACTION: 'find' ;
WHERE_LINK_OP: 'By' | 'And' | 'Or' ;
CONDITION_OP: 'Lt' | 'Lteq' | 'Gt' | 'Gteq' | 'In' | 'Is' | 'Eq' | 'Not' | 'Noteq' | 'Between' ;
FIELD: [A-Z][a-z]+ ;
