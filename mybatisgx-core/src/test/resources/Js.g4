grammar Js;

html_root: (script)? ;

script: start_script (import_script)? (export_default)? end_script;

start_script: SCRIPT_KEY ;
end_script: SCRIPT_KEY ;

import_script: (import_segment)*;
import_segment: SCRIPT_KEY import_obj SCRIPT_KEY import_path ;
// import_obj: '{' (NAME (SYMBOL)?)+ '}' ;
import_obj: '{' (NAME (SYMBOL)?)* '}' ;
import_path: '\'@' (SYMBOL NAME)+ '\'' ;

export_default: SCRIPT_KEY SCRIPT_KEY '{' (code_name)? (code_data)? (code_created)? (code_methods)? '}' ;
// 描述name属性
code_name: SCRIPT_KEY SYMBOL '\'' NAME '\',';
// 描述data()
code_data
    // : SCRIPT_KEY SYMBOL SYMBOL '{' SCRIPT_KEY '{' (code_data_connntent)* '}' '}' (SYMBOL)?
    : SCRIPT_KEY SYMBOL SYMBOL '{' SCRIPT_KEY '{' (code_data_connntent)* '}' '}'
    | SCRIPT_KEY SYMBOL SYMBOL '{' SCRIPT_KEY '{' (code_data_connntent)* '}' '},'
    ;
// 描述created()
code_created: method ;
// 描述methods
code_methods
    // methods中可能有多个方法
    : (comment)? 'methods:' '{' (method+)? '}'
    ;

// 描述return中的对象
code_data_connntent
    // : NAME SYMBOL '{' (NAME SYMBOL NAME (SYMBOL)?)* '}' (SYMBOL)?
    // 描述复制对象
    : NAME SYMBOL '{' (code_data_connntent)* '}' (SYMBOL)?
    // 描述description: 'bb',
    | NAME SYMBOL '\'' NAME '\','
    // 描述tenantId: 'cc'
    | NAME SYMBOL '\'' NAME '\''
    ;

// 描述方法
method
    // 描述无[,]结尾
    : (comment)? NAME SYMBOL SYMBOL '{' (method_body+)? '}'
    // 描述有[,]结尾
    | (comment)? NAME SYMBOL SYMBOL '{' (method_body+)? '},'
    ;

// 解析方法体
method_body
    // 引用注释描述
    : comment
    // 描述定义变量
    | 'let' NAME SYMBOL '\'' NAME '\''
    // 描述定义变量带分号
    | 'let' NAME SYMBOL '\'' NAME '\';'
    ;

// 描述注释
comment
    // 描述多行注释
    : SYMBOL (SYMBOL)+ (NAME)? SYMBOL SYMBOL
    // 描述单行注释
    | SYMBOL SYMBOL (NAME)?
    ;

SCRIPT_KEY: '<script>' | '</script>' | 'import' | 'from' | 'export' | 'default' | 'name' | 'data' | 'return' | 'let' ;
SCRIPT_SYMBOL: '{' | '}' ;

NAME: [a-z]+ | [a-zA-Z]+ ;
SYMBOL: '<' | '>' | '/' | '</' | '=' | '"' | '@' | ':' | ',' | '\'' | '(' | ')' | '*' ;
WS: [ \t\r\n]+ -> skip ;
