grammar jpa_method_name;

ql_statement
   : select_statement
   ;

select_statement
   : (select_clause)? (aggregate_expression)? from_clause (table_clause)? (join_clause)? (where_clause)? (groupby_clause)? (having_clause)? (orderby_clause)?
   ;

select_clause
   : SELECT_ACTION
   ;

aggregate_expression
   // : ('AVG' | 'MAX' | 'MIN' | 'SUM') '(' ('DISTINCT')? state_field_path_expression ')'
   : ('count' | 'max' | 'min' | 'sum')
   ;

from_clause
   :
   ;

table_clause
   : 'Table' FIELD
   ;

join_clause
   : 'LeftJoin' FIELD
   ;

where_clause
   : WHERE (FIELD (CONDITION_OP)? (LINK_OP)?)+
   ;

groupby_clause
   : KEY_WORD groupby_item (groupby_item)*
   ;

groupby_item
   : FIELD
   ;

having_clause
   : 'Having'
   ;

//
orderby_clause
   : KEY_WORD orderby_item (orderby_item)*
   ;

// ?表示可选 |表示或
orderby_item
   : (FIELD ('Asc' | 'Desc')?)+
   ;

SELECT_ACTION: ('select' | 'get' | 'query' | 'find' | 'add')?;
WHERE: 'By';
LINK_OP: 'And' | 'Or' ;
CONDITION_OP: 'Lt' | 'Eq' ;
KEY_WORD: 'GroupBy' | 'OrderBy' ;
FIELD: [A-Z][a-z]+ ;
// FIELD: [0-9a-zA-Z_]{1,} ;
