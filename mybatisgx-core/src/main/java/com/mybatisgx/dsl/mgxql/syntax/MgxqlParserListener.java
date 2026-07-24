// Generated from F:/owner_project/mybatisgx-ai_coding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
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
	 * Enter a parse tree produced by {@link MgxqlParser#modify_entity}.
	 * @param ctx the parse tree
	 */
	void enterModify_entity(MgxqlParser.Modify_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#modify_entity}.
	 * @param ctx the parse tree
	 */
	void exitModify_entity(MgxqlParser.Modify_entityContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#aggregate_function_name}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_function_name(MgxqlParser.Aggregate_function_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#aggregate_function_name}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_function_name(MgxqlParser.Aggregate_function_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#aggregate_function_argument}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_function_argument(MgxqlParser.Aggregate_function_argumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#aggregate_function_argument}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_function_argument(MgxqlParser.Aggregate_function_argumentContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#select_sum}.
	 * @param ctx the parse tree
	 */
	void enterSelect_sum(MgxqlParser.Select_sumContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_sum}.
	 * @param ctx the parse tree
	 */
	void exitSelect_sum(MgxqlParser.Select_sumContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#select_primary_entity}.
	 * @param ctx the parse tree
	 */
	void enterSelect_primary_entity(MgxqlParser.Select_primary_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_primary_entity}.
	 * @param ctx the parse tree
	 */
	void exitSelect_primary_entity(MgxqlParser.Select_primary_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_join_entity}.
	 * @param ctx the parse tree
	 */
	void enterSelect_join_entity(MgxqlParser.Select_join_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_join_entity}.
	 * @param ctx the parse tree
	 */
	void exitSelect_join_entity(MgxqlParser.Select_join_entityContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#select_on}.
	 * @param ctx the parse tree
	 */
	void enterSelect_on(MgxqlParser.Select_onContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_on}.
	 * @param ctx the parse tree
	 */
	void exitSelect_on(MgxqlParser.Select_onContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#select_on_expression}.
	 * @param ctx the parse tree
	 */
	void enterSelect_on_expression(MgxqlParser.Select_on_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#select_on_expression}.
	 * @param ctx the parse tree
	 */
	void exitSelect_on_expression(MgxqlParser.Select_on_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#on_equal}.
	 * @param ctx the parse tree
	 */
	void enterOn_equal(MgxqlParser.On_equalContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#on_equal}.
	 * @param ctx the parse tree
	 */
	void exitOn_equal(MgxqlParser.On_equalContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#group_by_expression}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by_expression(MgxqlParser.Group_by_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#group_by_expression}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by_expression(MgxqlParser.Group_by_expressionContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#having_or_expression}.
	 * @param ctx the parse tree
	 */
	void enterHaving_or_expression(MgxqlParser.Having_or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_or_expression}.
	 * @param ctx the parse tree
	 */
	void exitHaving_or_expression(MgxqlParser.Having_or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_and_expression}.
	 * @param ctx the parse tree
	 */
	void enterHaving_and_expression(MgxqlParser.Having_and_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_and_expression}.
	 * @param ctx the parse tree
	 */
	void exitHaving_and_expression(MgxqlParser.Having_and_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_term}.
	 * @param ctx the parse tree
	 */
	void enterHaving_term(MgxqlParser.Having_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_term}.
	 * @param ctx the parse tree
	 */
	void exitHaving_term(MgxqlParser.Having_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_comparison}.
	 * @param ctx the parse tree
	 */
	void enterHaving_comparison(MgxqlParser.Having_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_comparison}.
	 * @param ctx the parse tree
	 */
	void exitHaving_comparison(MgxqlParser.Having_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#having_value}.
	 * @param ctx the parse tree
	 */
	void enterHaving_value(MgxqlParser.Having_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#having_value}.
	 * @param ctx the parse tree
	 */
	void exitHaving_value(MgxqlParser.Having_valueContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#order_by_expression}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_expression(MgxqlParser.Order_by_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#order_by_expression}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_expression(MgxqlParser.Order_by_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#limit_clause}.
	 * @param ctx the parse tree
	 */
	void enterLimit_clause(MgxqlParser.Limit_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#limit_clause}.
	 * @param ctx the parse tree
	 */
	void exitLimit_clause(MgxqlParser.Limit_clauseContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(MgxqlParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(MgxqlParser.EndContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#where_sequence}.
	 * @param ctx the parse tree
	 */
	void enterWhere_sequence(MgxqlParser.Where_sequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_sequence}.
	 * @param ctx the parse tree
	 */
	void exitWhere_sequence(MgxqlParser.Where_sequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_item}.
	 * @param ctx the parse tree
	 */
	void enterWhere_item(MgxqlParser.Where_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_item}.
	 * @param ctx the parse tree
	 */
	void exitWhere_item(MgxqlParser.Where_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#where_atom}.
	 * @param ctx the parse tree
	 */
	void enterWhere_atom(MgxqlParser.Where_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#where_atom}.
	 * @param ctx the parse tree
	 */
	void exitWhere_atom(MgxqlParser.Where_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#bracket_group}.
	 * @param ctx the parse tree
	 */
	void enterBracket_group(MgxqlParser.Bracket_groupContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#bracket_group}.
	 * @param ctx the parse tree
	 */
	void exitBracket_group(MgxqlParser.Bracket_groupContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#if_directive}.
	 * @param ctx the parse tree
	 */
	void enterIf_directive(MgxqlParser.If_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#if_directive}.
	 * @param ctx the parse tree
	 */
	void exitIf_directive(MgxqlParser.If_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#bracket_directive}.
	 * @param ctx the parse tree
	 */
	void enterBracket_directive(MgxqlParser.Bracket_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#bracket_directive}.
	 * @param ctx the parse tree
	 */
	void exitBracket_directive(MgxqlParser.Bracket_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#choose_directive}.
	 * @param ctx the parse tree
	 */
	void enterChoose_directive(MgxqlParser.Choose_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#choose_directive}.
	 * @param ctx the parse tree
	 */
	void exitChoose_directive(MgxqlParser.Choose_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#when_directive}.
	 * @param ctx the parse tree
	 */
	void enterWhen_directive(MgxqlParser.When_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#when_directive}.
	 * @param ctx the parse tree
	 */
	void exitWhen_directive(MgxqlParser.When_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#otherwise_directive}.
	 * @param ctx the parse tree
	 */
	void enterOtherwise_directive(MgxqlParser.Otherwise_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#otherwise_directive}.
	 * @param ctx the parse tree
	 */
	void exitOtherwise_directive(MgxqlParser.Otherwise_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#block_prefix}.
	 * @param ctx the parse tree
	 */
	void enterBlock_prefix(MgxqlParser.Block_prefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#block_prefix}.
	 * @param ctx the parse tree
	 */
	void exitBlock_prefix(MgxqlParser.Block_prefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#body_sequence}.
	 * @param ctx the parse tree
	 */
	void enterBody_sequence(MgxqlParser.Body_sequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#body_sequence}.
	 * @param ctx the parse tree
	 */
	void exitBody_sequence(MgxqlParser.Body_sequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#body_item}.
	 * @param ctx the parse tree
	 */
	void enterBody_item(MgxqlParser.Body_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#body_item}.
	 * @param ctx the parse tree
	 */
	void exitBody_item(MgxqlParser.Body_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#body_atom}.
	 * @param ctx the parse tree
	 */
	void enterBody_atom(MgxqlParser.Body_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#body_atom}.
	 * @param ctx the parse tree
	 */
	void exitBody_atom(MgxqlParser.Body_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_or_expression}.
	 * @param ctx the parse tree
	 */
	void enterGuard_or_expression(MgxqlParser.Guard_or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_or_expression}.
	 * @param ctx the parse tree
	 */
	void exitGuard_or_expression(MgxqlParser.Guard_or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_and_expression}.
	 * @param ctx the parse tree
	 */
	void enterGuard_and_expression(MgxqlParser.Guard_and_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_and_expression}.
	 * @param ctx the parse tree
	 */
	void exitGuard_and_expression(MgxqlParser.Guard_and_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_term}.
	 * @param ctx the parse tree
	 */
	void enterGuard_term(MgxqlParser.Guard_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_term}.
	 * @param ctx the parse tree
	 */
	void exitGuard_term(MgxqlParser.Guard_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_comparison}.
	 * @param ctx the parse tree
	 */
	void enterGuard_comparison(MgxqlParser.Guard_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_comparison}.
	 * @param ctx the parse tree
	 */
	void exitGuard_comparison(MgxqlParser.Guard_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_logic_or}.
	 * @param ctx the parse tree
	 */
	void enterGuard_logic_or(MgxqlParser.Guard_logic_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_logic_or}.
	 * @param ctx the parse tree
	 */
	void exitGuard_logic_or(MgxqlParser.Guard_logic_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_logic_and}.
	 * @param ctx the parse tree
	 */
	void enterGuard_logic_and(MgxqlParser.Guard_logic_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_logic_and}.
	 * @param ctx the parse tree
	 */
	void exitGuard_logic_and(MgxqlParser.Guard_logic_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_relational_op}.
	 * @param ctx the parse tree
	 */
	void enterGuard_relational_op(MgxqlParser.Guard_relational_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_relational_op}.
	 * @param ctx the parse tree
	 */
	void exitGuard_relational_op(MgxqlParser.Guard_relational_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_null_op}.
	 * @param ctx the parse tree
	 */
	void enterGuard_null_op(MgxqlParser.Guard_null_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_null_op}.
	 * @param ctx the parse tree
	 */
	void exitGuard_null_op(MgxqlParser.Guard_null_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#guard_operand}.
	 * @param ctx the parse tree
	 */
	void enterGuard_operand(MgxqlParser.Guard_operandContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#guard_operand}.
	 * @param ctx the parse tree
	 */
	void exitGuard_operand(MgxqlParser.Guard_operandContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_comparison}.
	 * @param ctx the parse tree
	 */
	void enterCondition_comparison(MgxqlParser.Condition_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_comparison}.
	 * @param ctx the parse tree
	 */
	void exitCondition_comparison(MgxqlParser.Condition_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_comparison_param}.
	 * @param ctx the parse tree
	 */
	void enterCondition_comparison_param(MgxqlParser.Condition_comparison_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_comparison_param}.
	 * @param ctx the parse tree
	 */
	void exitCondition_comparison_param(MgxqlParser.Condition_comparison_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_comparison_not_param}.
	 * @param ctx the parse tree
	 */
	void enterCondition_comparison_not_param(MgxqlParser.Condition_comparison_not_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_comparison_not_param}.
	 * @param ctx the parse tree
	 */
	void exitCondition_comparison_not_param(MgxqlParser.Condition_comparison_not_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#condition_value}.
	 * @param ctx the parse tree
	 */
	void enterCondition_value(MgxqlParser.Condition_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#condition_value}.
	 * @param ctx the parse tree
	 */
	void exitCondition_value(MgxqlParser.Condition_valueContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#left_square}.
	 * @param ctx the parse tree
	 */
	void enterLeft_square(MgxqlParser.Left_squareContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#left_square}.
	 * @param ctx the parse tree
	 */
	void exitLeft_square(MgxqlParser.Left_squareContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#right_square}.
	 * @param ctx the parse tree
	 */
	void enterRight_square(MgxqlParser.Right_squareContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#right_square}.
	 * @param ctx the parse tree
	 */
	void exitRight_square(MgxqlParser.Right_squareContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#matching_value}.
	 * @param ctx the parse tree
	 */
	void enterMatching_value(MgxqlParser.Matching_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#matching_value}.
	 * @param ctx the parse tree
	 */
	void exitMatching_value(MgxqlParser.Matching_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#like_pattern}.
	 * @param ctx the parse tree
	 */
	void enterLike_pattern(MgxqlParser.Like_patternContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#like_pattern}.
	 * @param ctx the parse tree
	 */
	void exitLike_pattern(MgxqlParser.Like_patternContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#in_collection}.
	 * @param ctx the parse tree
	 */
	void enterIn_collection(MgxqlParser.In_collectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#in_collection}.
	 * @param ctx the parse tree
	 */
	void exitIn_collection(MgxqlParser.In_collectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#simple_collection}.
	 * @param ctx the parse tree
	 */
	void enterSimple_collection(MgxqlParser.Simple_collectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#simple_collection}.
	 * @param ctx the parse tree
	 */
	void exitSimple_collection(MgxqlParser.Simple_collectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#complex_collection}.
	 * @param ctx the parse tree
	 */
	void enterComplex_collection(MgxqlParser.Complex_collectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#complex_collection}.
	 * @param ctx the parse tree
	 */
	void exitComplex_collection(MgxqlParser.Complex_collectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#collection_path}.
	 * @param ctx the parse tree
	 */
	void enterCollection_path(MgxqlParser.Collection_pathContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#collection_path}.
	 * @param ctx the parse tree
	 */
	void exitCollection_path(MgxqlParser.Collection_pathContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#value_expr_list}.
	 * @param ctx the parse tree
	 */
	void enterValue_expr_list(MgxqlParser.Value_expr_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#value_expr_list}.
	 * @param ctx the parse tree
	 */
	void exitValue_expr_list(MgxqlParser.Value_expr_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#item_name}.
	 * @param ctx the parse tree
	 */
	void enterItem_name(MgxqlParser.Item_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#item_name}.
	 * @param ctx the parse tree
	 */
	void exitItem_name(MgxqlParser.Item_nameContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#field_reference}.
	 * @param ctx the parse tree
	 */
	void enterField_reference(MgxqlParser.Field_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#field_reference}.
	 * @param ctx the parse tree
	 */
	void exitField_reference(MgxqlParser.Field_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#parameter_reference}.
	 * @param ctx the parse tree
	 */
	void enterParameter_reference(MgxqlParser.Parameter_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#parameter_reference}.
	 * @param ctx the parse tree
	 */
	void exitParameter_reference(MgxqlParser.Parameter_referenceContext ctx);
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
	 * Enter a parse tree produced by {@link MgxqlParser#comma}.
	 * @param ctx the parse tree
	 */
	void enterComma(MgxqlParser.CommaContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#comma}.
	 * @param ctx the parse tree
	 */
	void exitComma(MgxqlParser.CommaContext ctx);
	/**
	 * Enter a parse tree produced by {@link MgxqlParser#question_mark}.
	 * @param ctx the parse tree
	 */
	void enterQuestion_mark(MgxqlParser.Question_markContext ctx);
	/**
	 * Exit a parse tree produced by {@link MgxqlParser#question_mark}.
	 * @param ctx the parse tree
	 */
	void exitQuestion_mark(MgxqlParser.Question_markContext ctx);
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
}