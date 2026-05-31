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
COMPARISON_OP_LT: '<' ;
COMPARISON_OP_LT_EQ: '<=' ;
COMPARISON_OP_GT: '>' ;
COMPARISON_OP_GT_EQ: '>=' ;
COMPARISON_OP_EQ: '=' ;
COMPARISON_OP_NOT_EQ: '!=' ;

COMPARISON_OP_NOT: 'not' ;
COMPARISON_OP_BETWEEN: 'between' ;
COMPARISON_OP_IN: 'in' ;
COMPARISON_OP_LIKE: 'like' ;
COMPARISON_OP_LEFT_LIKE: 'left like' ;
COMPARISON_OP_RIGHT_LIKE: 'right like' ;

COMPARISON_OP_IS_NULL: 'is null' ;
COMPARISON_OP_IS_NOT_NULL: 'is not null' ;
COMPARISON_OP_NOT_NULL: 'is not null' ;

ORDER_BY: 'order by' ;
ORDER_BY_DIRECTION: 'desc' | 'asc' ;

LIMIT_IDENTIFIER: 'limit' ;

// 括号
LEFT_BRACKET: '(' ;
RIGHT_BRACKET: ')' ;
// 逗号
COMMA: ',' ;
COLON: ':' ;

ENTITY_IDENTIFIER: [A-Z]+[a-z0-9]* ;
// antlr是从上向下解析的，常量一定要放在正则的上面
FIELD_IDENTIFIER: [a-z0-9]* ;
NUMBER: [0-9]* ;

// 忽略空白符
WS: [ \t\r\n]+ -> skip ;