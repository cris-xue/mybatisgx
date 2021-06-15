grammar MethodName;

ql_statement
   : select_statement
   ;

select_statement
   : (select_clause)? (aggregate_expression)? from_clause (table_clause)? (join_clause)? (where_clause)? (groupby_clause)? (having_clause)? (orderby_clause)?
   ;

select_clause: INSERT_ACTION | DELETE_ACTION | UPDATE_ACTION | SELECT_ACTION;

aggregate_expression
   // : ('AVG' | 'MAX' | 'MIN' | 'SUM') '(' ('DISTINCT')? state_field_path_expression ')'
   : ('count' | 'max' | 'min' | 'sum')
   ;

from_clause
   :
   ;

table_clause
   : 'Table' field_clause
   ;

join_clause
   : 'LeftJoin' field_clause
   ;

// where_clause
//    : WHERE_LINK_OP ((FIELD)+ (CONDITION_OP)? (WHERE_LINK_OP)?)+
//    ;
where_clause
   : (where_link_op_clause field_clause (where_op_clause)?)+
   // | WHERE_LINK_OP FIELD (CONDITION_OP)? where_clause
   ;

where_link_op_clause: WHERE_LINK_OP ;

where_op_clause: CONDITION_OP ;

groupby_clause
   : KEY_WORD field_clause
   ;

groupby_item
   : field_clause
   ;

having_clause
   : 'Having'
   ;

// + 一次或者多次   ？：零次或者一次
orderby_clause
   : KEY_WORD (field_clause ('Asc' | 'Desc')?)+
   ;

// ?表示可选 |表示或
orderby_item
   : (field_clause ('Asc' | 'Desc')?)+
   ;

field_clause: (FIELD)+ ;

INSERT_ACTION: 'insert' | 'add' ;
DELETE_ACTION: 'delete' ;
UPDATE_ACTION: 'update' ;
SELECT_ACTION: 'find' | 'select' | 'get' | 'query' ;
// WHERE: 'By';
// LINK_OP: 'And' | 'Or' ;
WHERE_LINK_OP: 'By' | 'And' | 'Or' ;
CONDITION_OP: 'Lt' | 'Eq' ;
KEY_WORD: 'GroupBy' | 'OrderBy' ;
FIELD: [A-Z]+[a-z]+ ;
// FIELD: FIELD1 (FIELD1 | FIELD1)* ;
// FIELD1: [A-Z][a-z]+ ;
// FIELD: ([A-Z] [a-z] [0-9])+ ;
// FIELD: [0-9a-zA-Z_]{1,} ;
