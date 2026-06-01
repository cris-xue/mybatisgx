// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
package com.mybatisgx.dsl.mgxql.syntax;
import com.mybatisgx.syntax.mgxql.MgxqlParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link com.mybatisgx.syntax.mgxql.MgxqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MgxqlParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSql_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Sql_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Insert_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Delete_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Update_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_statement(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_item_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_action(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_column_all(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_allContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_asterisk(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_column_custom(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_column_customContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_function(com.mybatisgx.syntax.mgxql.MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_maxContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_minContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_avgContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_count(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_max(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_min(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_avg(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_from_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_from(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_fromContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_left_join(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_entity(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entityContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_entity_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.Or_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_expression(com.mybatisgx.syntax.mgxql.MgxqlParser.And_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_term(com.mybatisgx.syntax.mgxql.MgxqlParser.Condition_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op_not_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving_comparison_op_param(com.mybatisgx.syntax.mgxql.MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_clause(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_item(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit(com.mybatisgx.syntax.mgxql.MgxqlParser.LimitContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(com.mybatisgx.syntax.mgxql.MgxqlParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComma_identifier(com.mybatisgx.syntax.mgxql.MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#size}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSize(com.mybatisgx.syntax.mgxql.MgxqlParser.SizeContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_start(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_startContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_and(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_or(com.mybatisgx.syntax.mgxql.MgxqlParser.Logic_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Relational_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_lt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_lt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_gt(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_gt_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_not_eq(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatching_op(com.mybatisgx.syntax.mgxql.MgxqlParser.Matching_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_not(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_between(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_in(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_left_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_right_like(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_is_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_is_not_null(com.mybatisgx.syntax.mgxql.MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_param_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_colon(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_colonContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_param_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#having}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving(com.mybatisgx.syntax.mgxql.MgxqlParser.HavingContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Group_byContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_byContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_direction(com.mybatisgx.syntax.mgxql.MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_name_alias(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_name(com.mybatisgx.syntax.mgxql.MgxqlParser.Field_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_name_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_value_field_access_chain(com.mybatisgx.syntax.mgxql.MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeft_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Left_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRight_bracket(com.mybatisgx.syntax.mgxql.MgxqlParser.Right_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#dot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDot(com.mybatisgx.syntax.mgxql.MgxqlParser.DotContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(com.mybatisgx.syntax.mgxql.MgxqlParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link com.mybatisgx.syntax.mgxql.MgxqlParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(MgxqlParser.EndContext ctx);
}