// Generated from F:/devops/mybatisx/mybatisx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.lc.mybatisx.syntax.method.name;
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
	 * Enter a parse tree produced by {@link MethodNameParser#ignore_reserved_word_clause}.
	 * @param ctx the parse tree
	 */
	void enterIgnore_reserved_word_clause(MethodNameParser.Ignore_reserved_word_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#ignore_reserved_word_clause}.
	 * @param ctx the parse tree
	 */
	void exitIgnore_reserved_word_clause(MethodNameParser.Ignore_reserved_word_clauseContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#where_start_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_start_clause(MethodNameParser.Where_start_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_start_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_start_clause(MethodNameParser.Where_start_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#condition_clause}.
	 * @param ctx the parse tree
	 */
	void enterCondition_clause(MethodNameParser.Condition_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#condition_clause}.
	 * @param ctx the parse tree
	 */
	void exitCondition_clause(MethodNameParser.Condition_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#condition_item_clause}.
	 * @param ctx the parse tree
	 */
	void enterCondition_item_clause(MethodNameParser.Condition_item_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#condition_item_clause}.
	 * @param ctx the parse tree
	 */
	void exitCondition_item_clause(MethodNameParser.Condition_item_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#logic_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_clause(MethodNameParser.Logic_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#logic_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_clause(MethodNameParser.Logic_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterField_condition_op_clause(MethodNameParser.Field_condition_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitField_condition_op_clause(MethodNameParser.Field_condition_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_clause(MethodNameParser.Comparison_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_clause(MethodNameParser.Comparison_op_clauseContext ctx);
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