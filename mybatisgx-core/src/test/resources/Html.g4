grammar Html;

html_root: (tag)? (script)? ;

// 定义标签规则
tag: start_tag (tag+)? end_tag ;

start_tag: SYMBOL tag_name (tag_attr)* (tag_event)* SYMBOL ;
end_tag: SYMBOL tag_name SYMBOL ;

tag_name: NAME ;
tag_attr: (SYMBOL)? ATTR_KEY SYMBOL ATTR_VALUE ;
tag_event: SYMBOL EVENT_KEY SYMBOL ATTR_VALUE ;

// ATTR: ATTR_KEY'=''"'ID'"' | '@'ID'=""';
ATTR_KEY: 'id' | 'name' | 'class' | 'label' | 'value' | 'style' ;
ATTR_VALUE: '"' NAME '"' | '\'' NAME '\'' ;

EVENT_KEY: 'click';
EVENT_VALUE: '"' NAME '"' ;

script: start_script (import_script)? (export_default)? end_script;

start_script: SCRIPT_KEY ;
end_script: SCRIPT_KEY ;

import_script: (import_segment)*;
import_segment: SCRIPT_KEY import_obj SCRIPT_KEY import_path ;
import_obj: '{' (NAME (SYMBOL)?)+ '}' ;
import_path: '\'@' (SYMBOL NAME)+ '\'' ;

export_default: SCRIPT_KEY SCRIPT_KEY '{' (code_name)? (code_data)? (code_created)? '}' ;
code_name: 'name:' '\'' NAME '\',';
code_data: 'data() {' 'return {' code_data_connntent '}' '},' ;
code_created: ;

code_data_connntent: NAME SYMBOL NAME (SYMBOL)? | NAME SYMBOL '{' NAME SYMBOL NAME (SYMBOL)? '}' ;

SCRIPT_KEY: '<script>' | '</script>' | 'import' | 'from' | 'export' | 'default' ;
SCRIPT_SYMBOL: '{' | '\}' ;

NAME: [a-z]+ | [a-zA-Z]+ ;
SYMBOL: '<' | '>' | '</' | '=' | '"' | '@' | ':' | ',' | '\'' ;
WS: [ \t\r\n]+ -> skip ;
