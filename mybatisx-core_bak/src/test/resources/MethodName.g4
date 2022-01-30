grammar MethodName;

sql_statement: insert_statement | delete_statement | update_statement | select_statement ;

insert_statement: insert_clause ;
insert_clause: INSERT_ACTION ;

delete_statement: delete_clause (where_clause)? ;
delete_clause: DELETE_ACTION ;

update_statement: update_clause (where_clause)? ;
update_clause: UPDATE_ACTION ;

select_statement: (select_clause)? (where_clause)? ;
select_clause: SELECT_ACTION ;

where_clause: (where_item)+ ;
where_item: where_link_op_clause field_clause (where_op_clause)? ;
where_link_op_clause: WHERE_LINK_OP ;
where_op_clause: CONDITION_OP ;

field_clause: (FIELD)+ ;

INSERT_ACTION: 'insert' ;
DELETE_ACTION: 'delete' ;
UPDATE_ACTION: 'update' ;
SELECT_ACTION: 'find' | 'query' | 'get' ;
WHERE_LINK_OP: 'By' | 'And' | 'Or' ;
CONDITION_OP: 'Lt' | 'Lteq' | 'Gt' | 'Gteq' | 'In' | 'Is' | 'Eq' | 'Not' | 'Noteq' | 'Between' ;
KEY_WORD: 'GroupBy' | 'OrderBy' ;
FIELD: [A-Z][a-z]+ ;
