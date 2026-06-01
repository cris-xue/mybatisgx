// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
package com.mybatisgx.dsl.mgxql.syntax;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MgxqlParser}.
 */
public interface MgxqlParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void enterSql_statement(MgxqlParser.Sql_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void exitSql_statement(MgxqlParser.Sql_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void enterInsert_statement(MgxqlParser.Insert_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void exitInsert_statement(MgxqlParser.Insert_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void enterInsert_clause(MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void exitInsert_clause(MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void enterDelete_statement(MgxqlParser.Delete_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void exitDelete_statement(MgxqlParser.Delete_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void enterDelete_clause(MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void exitDelete_clause(MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_statement(MgxqlParser.Update_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_statement(MgxqlParser.Update_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_clause(MgxqlParser.Update_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_clause(MgxqlParser.Update_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void enterSelect_statement(MgxqlParser.Select_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void exitSelect_statement(MgxqlParser.Select_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item_clause(MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item_clause(MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item(MgxqlParser.Select_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item(MgxqlParser.Select_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 */
	void enterSelect_action(MgxqlParser.Select_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 */
	void exitSelect_action(MgxqlParser.Select_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column_all(MgxqlParser.Select_column_allContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column_all(MgxqlParser.Select_column_allContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 */
	void enterSelect_asterisk(MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 */
	void exitSelect_asterisk(MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column_custom(MgxqlParser.Select_column_customContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column_custom(MgxqlParser.Select_column_customContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_function(MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_function(MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_count(MgxqlParser.Select_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_count(MgxqlParser.Select_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 */
	void enterSelect_max(MgxqlParser.Select_maxContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 */
	void exitSelect_max(MgxqlParser.Select_maxContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 */
	void enterSelect_min(MgxqlParser.Select_minContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 */
	void exitSelect_min(MgxqlParser.Select_minContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 */
	void enterSelect_avg(MgxqlParser.Select_avgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 */
	void exitSelect_avg(MgxqlParser.Select_avgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_count(MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_count(MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_max(MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_max(MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_min(MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_min(MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_avg(MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_avg(MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_from_clause(MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_from_clause(MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 */
	void enterSelect_from(MgxqlParser.Select_fromContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 */
	void exitSelect_from(MgxqlParser.Select_fromContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 */
	void enterSelect_left_join(MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 */
	void exitSelect_left_join(MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 */
	void enterSelect_entity(MgxqlParser.Select_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 */
	void exitSelect_entity(MgxqlParser.Select_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 */
	void enterSelect_entity_alias(MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 */
	void exitSelect_entity_alias(MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_clause(MgxqlParser.Where_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_clause(MgxqlParser.Where_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void enterCondition_expression(MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void exitCondition_expression(MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(MgxqlParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(MgxqlParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expression(MgxqlParser.And_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expression(MgxqlParser.And_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void enterCondition_term(MgxqlParser.Condition_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void exitCondition_term(MgxqlParser.Condition_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op(MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op(MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op_param(MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op_param(MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op_not_param(MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op_not_param(MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by_clause(MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by_clause(MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 */
	void enterHaving_clause(MgxqlParser.Having_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 */
	void exitHaving_clause(MgxqlParser.Having_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void enterHaving_comparison_op_param(MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void exitHaving_comparison_op_param(MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_clause(MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_clause(MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_item(MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_item(MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(MgxqlParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(MgxqlParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 */
	void enterLimit_identifier(MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 */
	void exitLimit_identifier(MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(MgxqlParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(MgxqlParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 */
	void enterComma_identifier(MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 */
	void exitComma_identifier(MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#size}.
	 * @param ctx the parse tree
	 */
	void enterSize(MgxqlParser.SizeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#size}.
	 * @param ctx the parse tree
	 */
	void exitSize(MgxqlParser.SizeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 */
	void enterWhere_start(MgxqlParser.Where_startContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 */
	void exitWhere_start(MgxqlParser.Where_startContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void enterLogic_and(MgxqlParser.Logic_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void exitLogic_and(MgxqlParser.Logic_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void enterLogic_or(MgxqlParser.Logic_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void exitLogic_or(MgxqlParser.Logic_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 */
	void enterRelational_op(MgxqlParser.Relational_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 */
	void exitRelational_op(MgxqlParser.Relational_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_lt(MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_lt(MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_lt_eq(MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_lt_eq(MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_gt(MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_gt(MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_gt_eq(MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_gt_eq(MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_eq(MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_eq(MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_not_eq(MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_not_eq(MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 */
	void enterMatching_op(MgxqlParser.Matching_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 */
	void exitMatching_op(MgxqlParser.Matching_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_not(MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_not(MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_between(MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_between(MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_in(MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_in(MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_like(MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_like(MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_left_like(MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_left_like(MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_right_like(MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_right_like(MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_null(MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_null(MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_is_null(MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_is_null(MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_is_not_null(MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_is_not_null(MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterWhere_param_name_field_access_chain(MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitWhere_param_name_field_access_chain(MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 */
	void enterParam_colon(MgxqlParser.Param_colonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 */
	void exitParam_colon(MgxqlParser.Param_colonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterWhere_param_value_field_access_chain(MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitWhere_param_value_field_access_chain(MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having}.
	 * @param ctx the parse tree
	 */
	void enterHaving(MgxqlParser.HavingContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having}.
	 * @param ctx the parse tree
	 */
	void exitHaving(MgxqlParser.HavingContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by(MgxqlParser.Group_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by(MgxqlParser.Group_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by(MgxqlParser.Order_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by(MgxqlParser.Order_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_direction(MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_direction(MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 */
	void enterEntity_name(MgxqlParser.Entity_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 */
	void exitEntity_name(MgxqlParser.Entity_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 */
	void enterEntity_name_alias(MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 */
	void exitEntity_name_alias(MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 */
	void enterField_name(MgxqlParser.Field_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 */
	void exitField_name(MgxqlParser.Field_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterEntity_field_access_chain(MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitEntity_field_access_chain(MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterParam_name_field_access_chain(MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitParam_name_field_access_chain(MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterParam_value_field_access_chain(MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitParam_value_field_access_chain(MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket(MgxqlParser.Left_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket(MgxqlParser.Left_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket(MgxqlParser.Right_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket(MgxqlParser.Right_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#dot}.
	 * @param ctx the parse tree
	 */
	void enterDot(MgxqlParser.DotContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#dot}.
	 * @param ctx the parse tree
	 */
	void exitDot(MgxqlParser.DotContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(MgxqlParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(MgxqlParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(MgxqlParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(MgxqlParser.EndContext ctx);
}