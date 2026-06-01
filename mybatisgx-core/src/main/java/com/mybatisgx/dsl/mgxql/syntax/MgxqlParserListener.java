// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
package com.mybatisgx.dsl.mgxql.syntax;
import com.mybatisgx.syntax.mgxql.MgxqlParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser}.
 */
public interface MgxqlParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void enterSql_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Sql_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void exitSql_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Sql_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void enterInsert_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Insert_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void exitInsert_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Insert_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void enterInsert_clause(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void exitInsert_clause(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void enterDelete_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Delete_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void exitDelete_statement(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Delete_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void enterDelete_clause(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.dsl.mgxql.syntax.MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void exitDelete_clause(com.mybatisgx.dsl.mgxql.syntax.MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void enterSelect_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void exitSelect_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 */
	void enterSelect_action(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 */
	void exitSelect_action(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column_all(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_allContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column_all(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_allContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 */
	void enterSelect_asterisk(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 */
	void exitSelect_asterisk(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column_custom(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_customContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column_custom(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_customContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_function(com.mybatisgx.syntax.mgxql.MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_function(com.mybatisgx.syntax.mgxql.MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 */
	void enterSelect_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_maxContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 */
	void exitSelect_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_maxContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 */
	void enterSelect_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_minContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 */
	void exitSelect_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_minContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 */
	void enterSelect_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_avgContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 */
	void exitSelect_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_avgContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 */
	void enterSelect_aggregate_function_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 */
	void exitSelect_aggregate_function_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_from_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_from_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 */
	void enterSelect_from(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_fromContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 */
	void exitSelect_from(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_fromContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 */
	void enterSelect_left_join(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 */
	void exitSelect_left_join(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 */
	void enterSelect_entity(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 */
	void exitSelect_entity(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 */
	void enterSelect_entity_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 */
	void exitSelect_entity_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void enterCondition_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void exitCondition_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.And_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.And_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void enterCondition_term(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void exitCondition_term(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op_not_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op_not_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 */
	void enterHaving_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 */
	void exitHaving_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void enterHaving_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 */
	void exitHaving_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(com.mybatisgx.syntax.mgxql.MgxqlParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(com.mybatisgx.syntax.mgxql.MgxqlParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 */
	void enterLimit_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 */
	void exitLimit_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(com.mybatisgx.syntax.mgxql.MgxqlParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(com.mybatisgx.syntax.mgxql.MgxqlParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 */
	void enterComma_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 */
	void exitComma_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#size}.
	 * @param ctx the parse tree
	 */
	void enterSize(com.mybatisgx.syntax.mgxql.MgxqlParser.SizeContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#size}.
	 * @param ctx the parse tree
	 */
	void exitSize(com.mybatisgx.syntax.mgxql.MgxqlParser.SizeContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 */
	void enterWhere_start(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_startContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 */
	void exitWhere_start(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_startContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void enterLogic_and(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void exitLogic_and(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void enterLogic_or(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void exitLogic_or(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 */
	void enterRelational_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Relational_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 */
	void exitRelational_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Relational_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_lt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_lt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_lt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_lt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_gt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_gt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_gt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_gt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_not_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_not_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 */
	void enterMatching_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Matching_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 */
	void exitMatching_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Matching_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_not(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_not(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_between(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_between(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_in(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_in(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_left_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_left_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_right_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_right_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_is_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_is_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_is_not_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_is_not_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterWhere_param_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitWhere_param_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 */
	void enterParam_colon(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_colonContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 */
	void exitParam_colon(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_colonContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterWhere_param_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitWhere_param_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having}.
	 * @param ctx the parse tree
	 */
	void enterHaving(com.mybatisgx.syntax.mgxql.MgxqlParser.HavingContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having}.
	 * @param ctx the parse tree
	 */
	void exitHaving(com.mybatisgx.syntax.mgxql.MgxqlParser.HavingContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_direction(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_direction(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 */
	void enterEntity_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 */
	void exitEntity_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 */
	void enterEntity_name_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 */
	void exitEntity_name_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 */
	void enterField_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 */
	void exitField_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterEntity_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitEntity_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterParam_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitParam_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void enterParam_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 */
	void exitParam_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Left_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Left_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Right_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Right_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#dot}.
	 * @param ctx the parse tree
	 */
	void enterDot(com.mybatisgx.syntax.mgxql.MgxqlParser.DotContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#dot}.
	 * @param ctx the parse tree
	 */
	void exitDot(com.mybatisgx.syntax.mgxql.MgxqlParser.DotContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(com.mybatisgx.syntax.mgxql.MgxqlParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(com.mybatisgx.syntax.mgxql.MgxqlParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(com.mybatisgx.syntax.mgxql.MgxqlParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(MgxqlParser.EndContext ctx);
}