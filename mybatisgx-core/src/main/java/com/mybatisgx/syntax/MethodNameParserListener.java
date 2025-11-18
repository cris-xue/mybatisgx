// Generated from F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.mybatisgx.syntax;
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
	 * Enter a parse tree produced by {@link MethodNameParser#aggregate_operation_clause}.
	 * @param ctx the parse tree
	 */
	void enterAggregate_operation_clause(MethodNameParser.Aggregate_operation_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#aggregate_operation_clause}.
	 * @param ctx the parse tree
	 */
	void exitAggregate_operation_clause(MethodNameParser.Aggregate_operation_clauseContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void enterCondition_expression(MethodNameParser.Condition_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void exitCondition_expression(MethodNameParser.Condition_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(MethodNameParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(MethodNameParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expression(MethodNameParser.And_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expression(MethodNameParser.And_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void enterCondition_term(MethodNameParser.Condition_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void exitCondition_term(MethodNameParser.Condition_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#field_comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op_clause(MethodNameParser.Field_comparison_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#field_comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op_clause(MethodNameParser.Field_comparison_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by_clause(MethodNameParser.Group_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#group_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by_clause(MethodNameParser.Group_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_clause(MethodNameParser.Order_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_clause(MethodNameParser.Order_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by_item_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_item_clause(MethodNameParser.Order_by_item_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by_item_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_item_clause(MethodNameParser.Order_by_item_clauseContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#logic_op_and_clause}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_and_clause(MethodNameParser.Logic_op_and_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#logic_op_and_clause}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_and_clause(MethodNameParser.Logic_op_and_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#logic_op_or_clause}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_or_clause(MethodNameParser.Logic_op_or_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#logic_op_or_clause}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_or_clause(MethodNameParser.Logic_op_or_clauseContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#group_by_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by_op_clause(MethodNameParser.Group_by_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#group_by_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by_op_clause(MethodNameParser.Group_by_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_op_clause(MethodNameParser.Order_by_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_op_clause(MethodNameParser.Order_by_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by_op_direction_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_op_direction_clause(MethodNameParser.Order_by_op_direction_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by_op_direction_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_op_direction_clause(MethodNameParser.Order_by_op_direction_clauseContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket_clause(MethodNameParser.Left_bracket_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket_clause(MethodNameParser.Left_bracket_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket_clause(MethodNameParser.Right_bracket_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket_clause(MethodNameParser.Right_bracket_clauseContext ctx);
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
}