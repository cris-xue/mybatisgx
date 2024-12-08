// Generated from F:/devops/mybatisx/mybatisx-core/src/test/resources/MethodNameParser.g4 by ANTLR 4.13.1
package com.lc.mybatisx.syntax;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MethodNameParser}.
 */
public interface MethodNameParserListener extends ParseTreeListener {
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
	 * Enter a parse tree produced by {@link MethodNameParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(MethodNameParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(MethodNameParser.EndContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterCondition_op_clause(MethodNameParser.Condition_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitCondition_op_clause(MethodNameParser.Condition_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#dynamic_condition_clause}.
	 * @param ctx the parse tree
	 */
	void enterDynamic_condition_clause(MethodNameParser.Dynamic_condition_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#dynamic_condition_clause}.
	 * @param ctx the parse tree
	 */
	void exitDynamic_condition_clause(MethodNameParser.Dynamic_condition_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#group_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_clause(MethodNameParser.Group_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#group_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_clause(MethodNameParser.Group_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#group_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_op_clause(MethodNameParser.Group_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#group_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_op_clause(MethodNameParser.Group_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_clause(MethodNameParser.Order_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_clause(MethodNameParser.Order_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_op_clause(MethodNameParser.Order_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_op_clause(MethodNameParser.Order_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_op_direction_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_op_direction_clause(MethodNameParser.Order_op_direction_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_op_direction_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_op_direction_clause(MethodNameParser.Order_op_direction_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#aggregate_function_clause}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_function_clause(MethodNameParser.Aggregate_function_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#aggregate_function_clause}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_function_clause(MethodNameParser.Aggregate_function_clauseContext ctx);
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