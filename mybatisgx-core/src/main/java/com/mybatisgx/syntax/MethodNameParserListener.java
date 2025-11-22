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
	 * Enter a parse tree produced by {@link MethodNameParser#select_item}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item(MethodNameParser.Select_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_item}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item(MethodNameParser.Select_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#select_column}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column(MethodNameParser.Select_columnContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_column}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column(MethodNameParser.Select_columnContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#select_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_count(MethodNameParser.Select_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_count(MethodNameParser.Select_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#select_exist}.
	 * @param ctx the parse tree
	 */
	void enterSelect_exist(MethodNameParser.Select_existContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#select_exist}.
	 * @param ctx the parse tree
	 */
	void exitSelect_exist(MethodNameParser.Select_existContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(MethodNameParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(MethodNameParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#ignore_reserved_word}.
	 * @param ctx the parse tree
	 */
	void enterIgnore_reserved_word(MethodNameParser.Ignore_reserved_wordContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#ignore_reserved_word}.
	 * @param ctx the parse tree
	 */
	void exitIgnore_reserved_word(MethodNameParser.Ignore_reserved_wordContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#where_start}.
	 * @param ctx the parse tree
	 */
	void enterWhere_start(MethodNameParser.Where_startContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#where_start}.
	 * @param ctx the parse tree
	 */
	void exitWhere_start(MethodNameParser.Where_startContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#logic_op_and}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_and(MethodNameParser.Logic_op_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#logic_op_and}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_and(MethodNameParser.Logic_op_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#logic_op_or}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_or(MethodNameParser.Logic_op_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#logic_op_or}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_or(MethodNameParser.Logic_op_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op(MethodNameParser.Comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op(MethodNameParser.Comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#group_by}.
	 * @param ctx the parse tree
	 */
	void enterGroup_by(MethodNameParser.Group_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#group_by}.
	 * @param ctx the parse tree
	 */
	void exitGroup_by(MethodNameParser.Group_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by(MethodNameParser.Order_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by(MethodNameParser.Order_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_direction(MethodNameParser.Order_by_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_direction(MethodNameParser.Order_by_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#limit_top}.
	 * @param ctx the parse tree
	 */
	void enterLimit_top(MethodNameParser.Limit_topContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#limit_top}.
	 * @param ctx the parse tree
	 */
	void exitLimit_top(MethodNameParser.Limit_topContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#limit_first}.
	 * @param ctx the parse tree
	 */
	void enterLimit_first(MethodNameParser.Limit_firstContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#limit_first}.
	 * @param ctx the parse tree
	 */
	void exitLimit_first(MethodNameParser.Limit_firstContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#limit_last}.
	 * @param ctx the parse tree
	 */
	void enterLimit_last(MethodNameParser.Limit_lastContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#limit_last}.
	 * @param ctx the parse tree
	 */
	void exitLimit_last(MethodNameParser.Limit_lastContext ctx);
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
	 * Enter a parse tree produced by {@link MethodNameParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket(MethodNameParser.Left_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket(MethodNameParser.Left_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link MethodNameParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket(MethodNameParser.Right_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link MethodNameParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket(MethodNameParser.Right_bracketContext ctx);
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