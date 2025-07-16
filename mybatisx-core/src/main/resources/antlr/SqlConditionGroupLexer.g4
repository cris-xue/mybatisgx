lexer grammar SqlConditionGroupLexer;
// ? 表示可有可无（0或1次）
// + 表示至少有一个
// | 表示或的关系
// * 表示有0或者多个
// fragment 定义不被直接解析的规则

import MethodNameLexer;

LEFT_BRACKET: '(' ;
RIGHT_BRACKET: ')' ;