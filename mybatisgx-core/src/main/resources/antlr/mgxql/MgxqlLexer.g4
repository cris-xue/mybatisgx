lexer grammar MgxqlLexer;

INSERT_ACTION: 'insert' ;
DELETE_ACTION: 'delete' ;
UPDATE_ACTION: 'update' ;
SELECT_ACTION: 'select' ;

SELECT_ASTERISK: '*' ;
SELECT_COUNT: 'count' ;
SELECT_MAX: 'max' ;
SELECT_MIN: 'min' ;
SELECT_AVG: 'avg' ;
SELECT_SUM: 'sum' ;

FROM: 'from' ;
LEFT: 'left' ;
JOIN: 'join' ;
ON: 'on' ;

WHERE: 'where' ;
LOGIC_AND: 'and' ;
LOGIC_OR: 'or' ;

// 比较运算符
COMPARISON_OP_LT: '<' ;
COMPARISON_OP_LT_EQ: '<=' ;
COMPARISON_OP_GT: '>' ;
COMPARISON_OP_GT_EQ: '>=' ;
EQUAL: '=' ;
COMPARISON_OP_NOT_EQ: '!=' ;

// guard（#if(expr)/#when(expr)）独立文法算子（design D8）：antlr 最长匹配优先，== 会优先于 =
EQ_EQ: '==' ;
AND_AND: '&&' ;
OR_OR: '||' ;
// 字符串字面量（guard 右值、未来表达式用）：单引号包裹，支持转义单引号 \'
STRING_LITERAL: '\'' ('\\\'' | ~['\r\n])* '\'' ;
// STRING_LITERAL: '\'' ( '\\'\''
//                        | ~[\'\r\n]
//                        )* '\'' ;

COMPARISON_OP_NOT: 'not' ;
COMPARISON_OP_BETWEEN: 'between' ;
COMPARISON_OP_IN: 'in' ;
COMPARISON_OP_LIKE: 'like' ;
// left like / right like 已退役（design，LIKE 改字面量 %:name%）：模糊匹配由 like 后字面量右值表达，不再用算子名

COMPARISON_OP_IS_NULL: 'is null' ;
COMPARISON_OP_IS_NOT_NULL: 'is not null' ;

GROUP_BY: 'group by' ;
HAVING: 'having' ;
ORDER_BY: 'order by' ;
ORDER_BY_DIRECTION: 'desc' | 'asc' ;
LIMIT: 'limit' ;

// 括号
LEFT_BRACKET: '(' ;
RIGHT_BRACKET: ')' ;
// 逗号
COMMA: ',' ;
COLON: ':' ;
DOT: '.' ;
QUESTION_MARK: '?' ;

// 动态门（mgxsql 子集）标点 token：常量置正则之上（遵循"常量在正则上面"的解析顺序约束）
HASH: '#' ;
LEFT_SQUARE: '[' ;
RIGHT_SQUARE: ']' ;
ARROW: '=>' ;
DOLLAR: '$' ;
PERCENT: '%' ;

// 动态门关键字（常量置正则之上，避免被 LOWER_NAME 抢先匹配为字段名）
IF: 'if' ;
WHEN: 'when' ;
OTHERWISE: 'otherwise' ;
CHOOSE: 'choose' ;

// antlr是从上向下解析的，常量一定要放在正则的上面
UPPER_NAME: UPPER+ (NUMBER | UPPER | LOWER)* ;
QUOTED_NAME: '`' LOWER+ (NUMBER | UPPER | LOWER)* '`' ;
LOWER_NAME: LOWER+ (NUMBER | UPPER | LOWER)* ;
fragment UPPER: [A-Z] ;
fragment LOWER: [a-z] ;
NUMBER: [0-9]+ ;

// 忽略空白符
WS: [ \t\r\n]+ -> skip ;