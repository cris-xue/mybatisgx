// Generated from F:/devops/mybatisx/mybatisx-core/src/test/resources/MethodNameParser.g4 by ANTLR 4.13.1
package com.lc.mybatisx.syntax;
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
	 * Visit a parse tree produced by {@link MethodNameParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(MethodNameParser.EndContext ctx);
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
	 * Visit a parse tree produced by {@link MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_clause(MethodNameParser.Where_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_item(MethodNameParser.Where_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_link_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_link_op_clause(MethodNameParser.Where_link_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_condition_op_clause(MethodNameParser.Field_condition_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#condition_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_op_clause(MethodNameParser.Condition_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#dynamic_condition_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic_condition_clause(MethodNameParser.Dynamic_condition_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#group_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_clause(MethodNameParser.Group_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#group_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_op_clause(MethodNameParser.Group_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_clause(MethodNameParser.Order_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_op_clause(MethodNameParser.Order_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#order_op_direction_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder_op_direction_clause(MethodNameParser.Order_op_direction_clauseContext ctx);
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
}