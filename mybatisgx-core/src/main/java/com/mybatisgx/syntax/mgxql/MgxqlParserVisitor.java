// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
package com.mybatisgx.syntax.mgxql;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MgxqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MgxqlParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#sql_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSql_statement(MgxqlParser.Sql_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#insert_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_statement(MgxqlParser.Insert_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#insert_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_clause(MgxqlParser.Insert_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#delete_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_statement(MgxqlParser.Delete_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#delete_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_clause(MgxqlParser.Delete_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#update_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_statement(MgxqlParser.Update_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#update_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_clause(MgxqlParser.Update_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_statement(MgxqlParser.Select_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_item_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_item_clause(MgxqlParser.Select_item_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_item(MgxqlParser.Select_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_action(MgxqlParser.Select_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_column_all}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_column_all(MgxqlParser.Select_column_allContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_asterisk}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_asterisk(MgxqlParser.Select_asteriskContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_column_custom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_column_custom(MgxqlParser.Select_column_customContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_function(MgxqlParser.Aggregate_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_count(MgxqlParser.Select_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_max}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_max(MgxqlParser.Select_maxContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_min}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_min(MgxqlParser.Select_minContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_avg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_avg(MgxqlParser.Select_avgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_aggregate_function_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_count(MgxqlParser.Select_aggregate_function_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_aggregate_function_max}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_max(MgxqlParser.Select_aggregate_function_maxContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_aggregate_function_min}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_min(MgxqlParser.Select_aggregate_function_minContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_aggregate_function_avg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_aggregate_function_avg(MgxqlParser.Select_aggregate_function_avgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_from_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_from_clause(MgxqlParser.Select_from_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_from}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_from(MgxqlParser.Select_fromContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_left_join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_left_join(MgxqlParser.Select_left_joinContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_entity(MgxqlParser.Select_entityContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#select_entity_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_entity_alias(MgxqlParser.Select_entity_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_clause(MgxqlParser.Where_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#condition_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_expression(MgxqlParser.Condition_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#or_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_expression(MgxqlParser.Or_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#and_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_expression(MgxqlParser.And_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#condition_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_term(MgxqlParser.Condition_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#field_comparison_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op(MgxqlParser.Field_comparison_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#field_comparison_op_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op_param(MgxqlParser.Field_comparison_op_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#field_comparison_op_not_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op_not_param(MgxqlParser.Field_comparison_op_not_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#group_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by_clause(MgxqlParser.Group_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#having_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving_clause(MgxqlParser.Having_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#having_comparison_op_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving_comparison_op_param(MgxqlParser.Having_comparison_op_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#order_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_clause(MgxqlParser.Order_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#order_by_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_item(MgxqlParser.Order_by_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit(MgxqlParser.LimitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#limit_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_identifier(MgxqlParser.Limit_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(MgxqlParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comma_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComma_identifier(MgxqlParser.Comma_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#size}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSize(MgxqlParser.SizeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#where_start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_start(MgxqlParser.Where_startContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#logic_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_and(MgxqlParser.Logic_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#logic_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_or(MgxqlParser.Logic_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#relational_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational_op(MgxqlParser.Relational_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_lt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_lt(MgxqlParser.Comparison_op_ltContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_lt_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_lt_eq(MgxqlParser.Comparison_op_lt_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_gt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_gt(MgxqlParser.Comparison_op_gtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_gt_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_gt_eq(MgxqlParser.Comparison_op_gt_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_eq(MgxqlParser.Comparison_op_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_not_eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_not_eq(MgxqlParser.Comparison_op_not_eqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#matching_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatching_op(MgxqlParser.Matching_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_not(MgxqlParser.Comparison_op_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_between}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_between(MgxqlParser.Comparison_op_betweenContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_in(MgxqlParser.Comparison_op_inContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_like(MgxqlParser.Comparison_op_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_left_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_left_like(MgxqlParser.Comparison_op_left_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_right_like}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_right_like(MgxqlParser.Comparison_op_right_likeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_null(MgxqlParser.Comparison_op_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_is_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_is_null(MgxqlParser.Comparison_op_is_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#comparison_op_is_not_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_is_not_null(MgxqlParser.Comparison_op_is_not_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#where_param_name_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_param_name_field_access_chain(MgxqlParser.Where_param_name_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#param_colon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_colon(MgxqlParser.Param_colonContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#where_param_value_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_param_value_field_access_chain(MgxqlParser.Where_param_value_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#having}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving(MgxqlParser.HavingContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#group_by}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by(MgxqlParser.Group_byContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#order_by}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by(MgxqlParser.Order_byContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#order_by_direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_direction(MgxqlParser.Order_by_directionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#entity_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_name(MgxqlParser.Entity_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#entity_name_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_name_alias(MgxqlParser.Entity_name_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#field_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_name(MgxqlParser.Field_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#entity_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_field_access_chain(MgxqlParser.Entity_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#param_name_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_name_field_access_chain(MgxqlParser.Param_name_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#param_value_field_access_chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_value_field_access_chain(MgxqlParser.Param_value_field_access_chainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#left_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeft_bracket(MgxqlParser.Left_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#right_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRight_bracket(MgxqlParser.Right_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#dot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDot(MgxqlParser.DotContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(MgxqlParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link MgxqlParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(MgxqlParser.EndContext ctx);
}