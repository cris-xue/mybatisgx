lexer grammar MethodNameLexer;

INSERT_ACTION: 'insert' | 'add' ;
DELETE_ACTION: 'delete' | 'remove' ;
UPDATE_ACTION: 'update' | 'modify' ;
SELECT_COLUMN_ACTION: 'find' | 'get' | 'select' | 'query' ;
SELECT_COUNT_ACTION: 'count' ;
BY: 'By' ;
LOGIC_OP_AND: 'And' ;
LOGIC_OP_OR: 'Or' ;
// 比较运算符
COMPARISON_NOT_OP: 'Not';
COMPARISON_OP: 'Lt'
    | 'Lteq'
    | 'Gt'
    | 'Gteq'
    | 'Eq' | 'Equal'
    | 'Like' | 'StartingWith' | 'EndingWith'
    | 'In'
    | 'Between'
    ;
COMPARISON_NULL_OP: 'IsNull' | 'IsNotNull' | 'NotNull' ;

ORDER_BY: 'OrderBy' ;
ORDER_BY_DIRECTION: 'Desc' | 'Asc' ;

LIMIT_TOP: TOP NUMBER ;

TOP: 'Top' ;

// 括号
LEFT_BRACKET: '(' ;
RIGHT_BRACKET: ')' ;

// 保留关键字
RESERVED_WORD: 'List' | 'One' | 'Page' | 'Batch' | 'Selective' ;

// antlr是从上向下解析的，常量一定要放在正则的上面
NUMBER: [0-9]+ ;
FIELD: [A-Z]+[a-z0-9]+ ;

// 分隔符
SEPARATOR: '$' ;

// 忽略空白符
WS: [ \t\r\n]+ -> skip ;