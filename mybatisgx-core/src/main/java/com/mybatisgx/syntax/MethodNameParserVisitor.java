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
	 * Visit a parse tree produced by {@link MethodNameParser#select_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_item(MethodNameParser.Select_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#select_column}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_column(MethodNameParser.Select_columnContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#select_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_count(MethodNameParser.Select_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#select_exist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_exist(MethodNameParser.Select_existContext ctx);
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
	 * Visit a parse tree produced by {@link MethodNameParser#limit_op_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_op_expression(MethodNameParser.Limit_op_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#ignore_reserved_word}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIgnore_reserved_word(MethodNameParser.Ignore_reserved_wordContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_start(MethodNameParser.Where_startContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#logic_op_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_and(MethodNameParser.Logic_op_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#logic_op_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_or(MethodNameParser.Logic_op_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#comparison_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op(MethodNameParser.Comparison_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#group_by_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_by_op(MethodNameParser.Group_by_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_op(MethodNameParser.Order_by_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_by_op_direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_by_op_direction(MethodNameParser.Order_by_op_directionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#limit_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_op(MethodNameParser.Limit_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#field_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_clause(MethodNameParser.Field_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#left_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeft_bracket(MethodNameParser.Left_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#right_bracket}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRight_bracket(MethodNameParser.Right_bracketContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(MethodNameParser.EndContext ctx);
}