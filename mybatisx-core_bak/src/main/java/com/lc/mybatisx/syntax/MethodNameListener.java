// Generated from F:/ec/lc/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
package com.lc.mybatisx.syntax;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MethodNameParser}.
 */
public interface MethodNameListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void enterSql_statement(MethodNameParser.Sql_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void exitSql_statement(MethodNameParser.Sql_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void enterInsert_statement(MethodNameParser.Insert_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void exitInsert_statement(MethodNameParser.Insert_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void enterInsert_clause(MethodNameParser.Insert_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void exitInsert_clause(MethodNameParser.Insert_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void enterDelete_statement(MethodNameParser.Delete_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void exitDelete_statement(MethodNameParser.Delete_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void enterDelete_clause(MethodNameParser.Delete_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void exitDelete_clause(MethodNameParser.Delete_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_statement(MethodNameParser.Update_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_statement(MethodNameParser.Update_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_clause(MethodNameParser.Update_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_clause(MethodNameParser.Update_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void enterSelect_statement(MethodNameParser.Select_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void exitSelect_statement(MethodNameParser.Select_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#select_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_clause(MethodNameParser.Select_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_clause(MethodNameParser.Select_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_clause(MethodNameParser.Where_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_clause(MethodNameParser.Where_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#where_item}.
	 * @param ctx the parse tree
	 */
	void enterWhere_item(MethodNameParser.Where_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_item}.
	 * @param ctx the parse tree
	 */
	void exitWhere_item(MethodNameParser.Where_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#where_link_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_link_op_clause(MethodNameParser.Where_link_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_link_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_link_op_clause(MethodNameParser.Where_link_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#where_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_op_clause(MethodNameParser.Where_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_op_clause(MethodNameParser.Where_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#field_clause}.
	 * @param ctx the parse tree
	 */
	void enterField_clause(MethodNameParser.Field_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#field_clause}.
	 * @param ctx the parse tree
	 */
	void exitField_clause(MethodNameParser.Field_clauseContext ctx);
}