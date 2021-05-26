// Generated from F:/ec/lc/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
package com.lc.mybatisx.syntax;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MethodNameParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MethodNameVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#ql_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQl_statement(MethodNameParser.Ql_statementContext ctx);
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
	 * Visit a parse tree produced by {@link MethodNameParser#aggregate_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_expression(MethodNameParser.Aggregate_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#from_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrom_clause(MethodNameParser.From_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#table_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_clause(MethodNameParser.Table_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#join_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin_clause(MethodNameParser.Join_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere_clause(MethodNameParser.Where_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#groupby_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupby_clause(MethodNameParser.Groupby_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#groupby_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupby_item(MethodNameParser.Groupby_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#having_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving_clause(MethodNameParser.Having_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#orderby_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderby_clause(MethodNameParser.Orderby_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MethodNameParser#orderby_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderby_item(MethodNameParser.Orderby_itemContext ctx);
}