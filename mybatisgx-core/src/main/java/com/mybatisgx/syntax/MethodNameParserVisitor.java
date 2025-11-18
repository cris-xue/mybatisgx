// Generated from F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.mybatisgx.syntax;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MethodNameParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MethodNameParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#sql_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSql_statement(MethodNameParser.Sql_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#insert_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_statement(MethodNameParser.Insert_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#insert_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert_clause(MethodNameParser.Insert_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#delete_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_statement(MethodNameParser.Delete_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#delete_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete_clause(MethodNameParser.Delete_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#update_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_statement(MethodNameParser.Update_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#update_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate_clause(MethodNameParser.Update_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#select_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_statement(MethodNameParser.Select_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#select_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_clause(MethodNameParser.Select_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#aggregate_operation_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_operation_clause(MethodNameParser.Aggregate_operation_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_clause(MethodNameParser.Where_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#condition_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_expression(MethodNameParser.Condition_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#or_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_expression(MethodNameParser.Or_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#and_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_expression(MethodNameParser.And_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#condition_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_term(MethodNameParser.Condition_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#field_comparison_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_comparison_op_clause(MethodNameParser.Field_comparison_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#group_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by_clause(MethodNameParser.Group_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_clause(MethodNameParser.Order_by_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_item_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_item_clause(MethodNameParser.Order_by_item_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#ignore_reserved_word_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIgnore_reserved_word_clause(MethodNameParser.Ignore_reserved_word_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_start_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_start_clause(MethodNameParser.Where_start_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#logic_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_clause(MethodNameParser.Logic_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#logic_op_and_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_and_clause(MethodNameParser.Logic_op_and_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#logic_op_or_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_or_clause(MethodNameParser.Logic_op_or_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_clause(MethodNameParser.Comparison_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#group_by_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by_op_clause(MethodNameParser.Group_by_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_op_clause(MethodNameParser.Order_by_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_op_direction_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_op_direction_clause(MethodNameParser.Order_by_op_direction_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#aggregate_function_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_function_clause(MethodNameParser.Aggregate_function_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#field_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_clause(MethodNameParser.Field_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeft_bracket_clause(MethodNameParser.Left_bracket_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRight_bracket_clause(MethodNameParser.Right_bracket_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(MethodNameParser.EndContext ctx);
}