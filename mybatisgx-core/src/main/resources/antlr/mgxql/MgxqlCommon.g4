// 定义解析器
parser grammar MgxqlCommon ;

options {
    // 表示解析token的词法解析器使用MgxqlLexer
    tokenVocab = MgxqlLexer ;
}

// 字段引用，user.name  role.status   只允许实体别名.字段，不支持嵌套属性链
field_reference: field_name | entity_name_alias dot field_name ;
// 1、参数引用（对应查询实体和@Param）。:name   :表示从根节点开始取值  2、collection 侧不带冒号，形如 userList 或 query.userList。
parameter_reference: param_colon field_name (dot field_name)* ;

entity_name: UPPER_NAME ;
entity_name_alias: LOWER_NAME | QUOTED_NAME ;
field_name: LOWER_NAME | QUOTED_NAME ;

left_bracket: LEFT_BRACKET ;
right_bracket: RIGHT_BRACKET ;
dot: DOT ;
param_colon: COLON ;
comma: COMMA ;
question_mark: QUESTION_MARK ;
number: NUMBER ;