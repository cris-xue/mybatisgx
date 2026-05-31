lexer grammar MgxqlLexer;

INSERT_ACTION: 'insert' ;
DELETE_ACTION: 'delete' ;
UPDATE_ACTION: 'update' ;
SELECT_ACTION: 'select' ;

SELECT_COLUMN: '*' ;
SELECT_COUNT: 'count' ;

FROM: 'from' ;

WHERE: 'where' ;
LOGIC_AND: 'and' ;
LOGIC_OR: 'or' ;

// 比较运算符
COMPARISON_OP_NOT: 'not';
COMPARISON_OP: 'Lt'
    | 'Lteq'
    | 'Gt'
    | 'Gteq'
    | 'Eq' | 'Equal'
    | 'Like' | 'StartingWith' | 'EndingWith'
    | 'In'
    | 'Between'
    ;
COMPARISON_OP_NULL: 'IsNull' | 'IsNotNull' | 'NotNull' ;

ORDER_BY: 'order by' ;
ORDER_BY_DIRECTION: 'desc' | 'asc' ;

LIMIT: 'limit' ;

// 括号
LEFT_BRACKET: '(' ;
RIGHT_BRACKET: ')' ;

ENTITY_IDENTIFIER: [A-Z]+[a-z0-9]* ;
// antlr是从上向下解析的，常量一定要放在正则的上面
FIELD_IDENTIFIER: [a-z0-9]* ;

// 忽略空白符
WS: [ \t\r\n]+ -> skip ;