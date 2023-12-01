// 定义解析器
parser grammar MethodNameParser;

options {
    // 表示解析token的词法解析器使用SearchLexer
    tokenVocab = MethodNameLexer;
}

sql_statement: (insert_statement | delete_statement | update_statement | select_statement) end;

// EOF(end of file)表示文件结束符，这个是Antlr中已经定义好的
end: EOF;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ;

delete_statement: delete_clause (where_clause)? ;
delete_clause: DELETE_ACTION ;

update_statement: update_clause (where_clause)? ;
update_clause: UPDATE_ACTION ;

select_statement: select_clause (where_clause)? ;
select_clause: SELECT_ACTION ;

where_clause: (where_item)+ ;
where_item: where_link_op_clause field_clause (where_op_clause)? ;

where_link_op_clause: WHERE_LINK_OP ;
where_op_clause: CONDITION_OP ;
field_clause: FIELD ;
