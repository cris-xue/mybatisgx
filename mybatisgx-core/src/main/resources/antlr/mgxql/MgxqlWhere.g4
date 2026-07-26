// 定义解析器
parser grammar MgxqlWhere ;

options {
    // 表示解析token的词法解析器使用MgxqlLexer
    tokenVocab = MgxqlLexer ;
}

import MgxqlCommon ;

// 条件语法   where name = :name and (age = :age or status = :status)
// 条件语法   where #[name = :name] #[and age = :age or status = :status]
// 条件语法   where #[name = :name] #if(status == 5)[and age = :age or status = :status]
// 条件语法   where id = :id #[and a = :a and b = :b]
//
// WHERE 文法形状：扁平序列（design Q7，2026-07-19 调整）。
// 用户决策「门内 #[and...] 连接词在 [ 内首 token、门前可无显式 and」与原优先级嵌套文法不兼容，改扁平序列。
// 每元素携自身 connector（NULL/AND/OR）；优先级靠显式括号分组 bracket_group 表达。
where_clause: where_start where_sequence ;

// 扁平序列：一个或多个 where_item，每项携可选前置连接词（design D9，task 3.5）。
where_sequence: where_item+ ;
where_item: (logic_and | logic_or)? where_atom ;

// 序列原子：普通条件、括号分组、或动态门块（if/bracket/choose）。
where_atom: condition_comparison | bracket_group | if_directive | bracket_directive | choose_directive ;

// 括号分组：复用现有 WhereConditionNode.subExpression 载体（design Q2），承载优先级括号。
bracket_group: left_bracket where_sequence right_bracket ;

// #if(guard)[body] —— guard 走独立文法 guard_or_expression（design D8，不复用 condition_*，因算子 == 与 = 冲突）。
// body 前置可选连接词（block_prefix）提升为该 if 块 logicOperator（design D9）。
if_directive: HASH IF left_bracket guard_or_expression right_bracket left_square block_prefix? body_sequence right_square ;
// #[body] —— auto-guard 收集归 mgxsql 消费阶段（design D5）。
bracket_directive: HASH LEFT_SQUARE block_prefix? body_sequence RIGHT_SQUARE ;
// #choose[#when(expr)[body]+ #otherwise[body]?]（design，task 5.1）
choose_directive: HASH CHOOSE LEFT_SQUARE when_directive+ otherwise_directive? RIGHT_SQUARE ;
when_directive: HASH WHEN left_bracket guard_or_expression right_bracket left_square block_prefix? body_sequence right_square ;
otherwise_directive: HASH OTHERWISE LEFT_SQUARE body_sequence RIGHT_SQUARE ;

// 块前置连接词：块与前一元素的连接词，提升为块 logicOperator（design D9）。
block_prefix: logic_and | logic_or ;

// 动态门 body：扁平条件序列（普通条件 + 括号分组），文法层禁止 body 内出现动态门（design D2 不支持嵌套）。
// body_atom 不含 if/bracket/choose 指令分支，故 body 无法嵌套动态门（task 3.3）。
body_sequence: body_item+ ;
body_item: (logic_and | logic_or)? body_atom ;
body_atom: condition_comparison | (left_bracket body_sequence right_bracket) ;

// guard 独立递归文法（design D8）：等号用 ==（SHALL NOT 接受 =），逻辑连接 && / and 同义、|| / or 同义。
// 算子 != / < / > / <= / >= / is null / is not null 复用现有 comparison token；左操作数收 field_reference 与 parameter_reference；右操作数收 parameter_reference / number / 字符串字面量。
guard_or_expression: guard_and_expression (guard_logic_or guard_and_expression)* ;
guard_and_expression: guard_term (guard_logic_and guard_term)* ;
guard_term: guard_comparison | (left_bracket guard_or_expression right_bracket) ;
// guard 比较：operand OP operand（关系）或 operand is [not] null。
guard_comparison: guard_operand guard_relational_op guard_operand | guard_operand guard_null_op ;
guard_logic_or: AND_AND | LOGIC_OR ;
guard_logic_and: AND_AND | LOGIC_AND ;
guard_relational_op: EQ_EQ | COMPARISON_OP_NOT_EQ | COMPARISON_OP_LT | COMPARISON_OP_LT_EQ | COMPARISON_OP_GT | COMPARISON_OP_GT_EQ ;
guard_null_op: comparison_op_is_null | comparison_op_is_not_null ;
// guard 左/右操作数：字段引用或参数引用（guard 可为 :id > 5 形式，design D11）；右值还收 number 与字符串字面量。
guard_operand: field_reference | parameter_reference | number | STRING_LITERAL ;

// 解析方法名和实体字段
// ? 前缀可选条件已退役（design D3）：移除 question_mark?，? 在条件前缀场景报语法错（task 3.6）。
condition_comparison: field_reference (condition_comparison_param | condition_comparison_not_param) ;
condition_comparison_param: relational_op condition_value | matching_op matching_value ;
condition_comparison_not_param: comparison_op_null ;
condition_value: parameter_reference | number ;

/* 条件原子语法 */
where_start: WHERE ;
logic_and: LOGIC_AND ;
logic_or: LOGIC_OR ;
// left_square/right_square 包装（动态门门用 [ ]），左括号 right_bracket 在 MgxqlCommon.g4 已定义
left_square: LEFT_SQUARE ;
right_square: RIGHT_SQUARE ;

relational_op: comparison_op_lt
    | comparison_op_lt_eq
    | comparison_op_gt
    | comparison_op_gt_eq
    | comparison_op_eq
    | comparison_op_not_eq
    ;
comparison_op_lt: COMPARISON_OP_LT ;
comparison_op_lt_eq: COMPARISON_OP_LT_EQ ;
comparison_op_gt: COMPARISON_OP_GT ;
comparison_op_gt_eq: COMPARISON_OP_GT_EQ ;
comparison_op_eq: EQUAL ;
comparison_op_not_eq: COMPARISON_OP_NOT_EQ ;

matching_op: comparison_op_not? (comparison_op_between | comparison_op_in | comparison_op_like) ;
comparison_op_not: COMPARISON_OP_NOT ;
comparison_op_between: COMPARISON_OP_BETWEEN ;
comparison_op_in: COMPARISON_OP_IN ;
comparison_op_like: COMPARISON_OP_LIKE ;
// left like / right like 已退役（LIKE 改字面量 %:name%）

// matching 右值（LIKE 模式 / IN 集合 / 裸参数），与 relational_op 的 condition_value 分离（避免 %:name 与 :name 歧义、按算子收不同右值）。
// 顺序：in_collection（LEFT_BRACKET 开头，distinct）→ like_pattern（含 PERCENT，distinct）→ parameter_reference（裸参 :name）。
matching_value: in_collection | like_pattern | parameter_reference ;
// LIKE 模式：%:name / :name% / %:name% 及多 % 变体。以 PERCENT 开头或 parameter_reference 后跟 PERCENT（至少一个 %）。
// 为消除与裸 parameter_reference 的歧义，显式三态（前 %、后 %、前后 %），ANTLR4 按 matching_value 分支顺序与前瞻完整匹配择优。
like_pattern: PERCENT+ parameter_reference | parameter_reference PERCENT+ | PERCENT+ parameter_reference PERCENT+ ;
// IN 集合：简单 (:idList) 或复杂 (item:objectList)=>$item.id（design D6，对齐 mgxsql ForeachUnit）。
in_collection: simple_collection | complex_collection ;
simple_collection: left_bracket parameter_reference right_bracket ;
// (item:list)=>$item
complex_collection: left_bracket item_name param_colon collection_path right_bracket ARROW DOLLAR value_expr_list ;
// 复杂 IN 集合路径：collection 侧不带冒号，形如 userList 或 query.userList。
collection_path: field_name (dot field_name)* ;
// 复杂 IN 迭代值表达式字段列表：单字段 $item.id 或多字段 $item.id,item.name（多字段 → mgxsql tuple foreach）。
value_expr_list: field_name (dot field_name)* ;
// item_name（迭代变量名）为小写标识符
item_name: LOWER_NAME ;

comparison_op_null: comparison_op_is_null | comparison_op_is_not_null ;
comparison_op_is_null: COMPARISON_OP_IS_NULL ;
comparison_op_is_not_null: COMPARISON_OP_IS_NOT_NULL ;
