// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.mybatisgx.dsl.method.syntax;
import com.mybatisgx.syntax.MethodNameParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link com.mybatisgx.syntax.MethodNameParser}.
 */
public interface MethodNameParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void enterSql_statement(com.mybatisgx.syntax.MethodNameParser.Sql_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#sql_statement}.
	 * @param ctx the parse tree
	 */
	void exitSql_statement(com.mybatisgx.syntax.MethodNameParser.Sql_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void enterInsert_statement(com.mybatisgx.syntax.MethodNameParser.Insert_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#insert_statement}.
	 * @param ctx the parse tree
	 */
	void exitInsert_statement(com.mybatisgx.syntax.MethodNameParser.Insert_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void enterInsert_clause(com.mybatisgx.syntax.MethodNameParser.Insert_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#insert_clause}.
	 * @param ctx the parse tree
	 */
	void exitInsert_clause(com.mybatisgx.syntax.MethodNameParser.Insert_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void enterDelete_statement(com.mybatisgx.syntax.MethodNameParser.Delete_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#delete_statement}.
	 * @param ctx the parse tree
	 */
	void exitDelete_statement(com.mybatisgx.syntax.MethodNameParser.Delete_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void enterDelete_clause(com.mybatisgx.syntax.MethodNameParser.Delete_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#delete_clause}.
	 * @param ctx the parse tree
	 */
	void exitDelete_clause(com.mybatisgx.syntax.MethodNameParser.Delete_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_statement(com.mybatisgx.syntax.MethodNameParser.Update_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#update_statement}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_statement(com.mybatisgx.syntax.MethodNameParser.Update_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void enterUpdate_clause(com.mybatisgx.syntax.MethodNameParser.Update_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#update_clause}.
	 * @param ctx the parse tree
	 */
	void exitUpdate_clause(com.mybatisgx.syntax.MethodNameParser.Update_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void enterSelect_statement(com.mybatisgx.syntax.MethodNameParser.Select_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_statement}.
	 * @param ctx the parse tree
	 */
	void exitSelect_statement(com.mybatisgx.syntax.MethodNameParser.Select_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void enterSelect_item_clause(com.mybatisgx.syntax.MethodNameParser.Select_item_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_item_clause}.
	 * @param ctx the parse tree
	 */
	void exitSelect_item_clause(com.mybatisgx.syntax.MethodNameParser.Select_item_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_column}.
	 * @param ctx the parse tree
	 */
	void enterSelect_column(com.mybatisgx.syntax.MethodNameParser.Select_columnContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_column}.
	 * @param ctx the parse tree
	 */
	void exitSelect_column(com.mybatisgx.syntax.MethodNameParser.Select_columnContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_count}.
	 * @param ctx the parse tree
	 */
	void enterSelect_count(com.mybatisgx.syntax.MethodNameParser.Select_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#select_count}.
	 * @param ctx the parse tree
	 */
	void exitSelect_count(com.mybatisgx.syntax.MethodNameParser.Select_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void enterWhere_clause(com.mybatisgx.syntax.MethodNameParser.Where_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#where_clause}.
	 * @param ctx the parse tree
	 */
	void exitWhere_clause(com.mybatisgx.syntax.MethodNameParser.Where_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void enterCondition_expression(com.mybatisgx.syntax.MethodNameParser.Condition_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#condition_expression}.
	 * @param ctx the parse tree
	 */
	void exitCondition_expression(com.mybatisgx.syntax.MethodNameParser.Condition_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(com.mybatisgx.syntax.MethodNameParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(com.mybatisgx.syntax.MethodNameParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expression(com.mybatisgx.syntax.MethodNameParser.And_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expression(com.mybatisgx.syntax.MethodNameParser.And_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void enterCondition_term(com.mybatisgx.syntax.MethodNameParser.Condition_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#condition_term}.
	 * @param ctx the parse tree
	 */
	void exitCondition_term(com.mybatisgx.syntax.MethodNameParser.Condition_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterField_comparison_op(com.mybatisgx.syntax.MethodNameParser.Field_comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field_comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitField_comparison_op(com.mybatisgx.syntax.MethodNameParser.Field_comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_clause(com.mybatisgx.syntax.MethodNameParser.Order_by_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_clause}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_clause(com.mybatisgx.syntax.MethodNameParser.Order_by_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_item(com.mybatisgx.syntax.MethodNameParser.Order_by_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_item}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_item(com.mybatisgx.syntax.MethodNameParser.Order_by_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(com.mybatisgx.syntax.MethodNameParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(com.mybatisgx.syntax.MethodNameParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#where_start}.
	 * @param ctx the parse tree
	 */
	void enterWhere_start(com.mybatisgx.syntax.MethodNameParser.Where_startContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#where_start}.
	 * @param ctx the parse tree
	 */
	void exitWhere_start(com.mybatisgx.syntax.MethodNameParser.Where_startContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void enterLogic_and(com.mybatisgx.syntax.MethodNameParser.Logic_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#logic_and}.
	 * @param ctx the parse tree
	 */
	void exitLogic_and(com.mybatisgx.syntax.MethodNameParser.Logic_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void enterLogic_or(com.mybatisgx.syntax.MethodNameParser.Logic_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#logic_or}.
	 * @param ctx the parse tree
	 */
	void exitLogic_or(com.mybatisgx.syntax.MethodNameParser.Logic_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op(com.mybatisgx.syntax.MethodNameParser.Comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op(com.mybatisgx.syntax.MethodNameParser.Comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_not(com.mybatisgx.syntax.MethodNameParser.Comparison_op_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op_not}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_not(com.mybatisgx.syntax.MethodNameParser.Comparison_op_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_null(com.mybatisgx.syntax.MethodNameParser.Comparison_op_nullContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#comparison_op_null}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_null(com.mybatisgx.syntax.MethodNameParser.Comparison_op_nullContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by(com.mybatisgx.syntax.MethodNameParser.Order_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by(com.mybatisgx.syntax.MethodNameParser.Order_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void enterOrder_by_direction(com.mybatisgx.syntax.MethodNameParser.Order_by_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#order_by_direction}.
	 * @param ctx the parse tree
	 */
	void exitOrder_by_direction(com.mybatisgx.syntax.MethodNameParser.Order_by_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#limit_top}.
	 * @param ctx the parse tree
	 */
	void enterLimit_top(com.mybatisgx.syntax.MethodNameParser.Limit_topContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#limit_top}.
	 * @param ctx the parse tree
	 */
	void exitLimit_top(com.mybatisgx.syntax.MethodNameParser.Limit_topContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(com.mybatisgx.syntax.MethodNameParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(com.mybatisgx.syntax.MethodNameParser.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field_identifier}.
	 * @param ctx the parse tree
	 */
	void enterField_identifier(com.mybatisgx.syntax.MethodNameParser.Field_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#field_identifier}.
	 * @param ctx the parse tree
	 */
	void exitField_identifier(com.mybatisgx.syntax.MethodNameParser.Field_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#escaped_identifier}.
	 * @param ctx the parse tree
	 */
	void enterEscaped_identifier(com.mybatisgx.syntax.MethodNameParser.Escaped_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#escaped_identifier}.
	 * @param ctx the parse tree
	 */
	void exitEscaped_identifier(com.mybatisgx.syntax.MethodNameParser.Escaped_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket(com.mybatisgx.syntax.MethodNameParser.Left_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#left_bracket}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket(com.mybatisgx.syntax.MethodNameParser.Left_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket(com.mybatisgx.syntax.MethodNameParser.Right_bracketContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#right_bracket}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket(com.mybatisgx.syntax.MethodNameParser.Right_bracketContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#business_semantic}.
	 * @param ctx the parse tree
	 */
	void enterBusiness_semantic(com.mybatisgx.syntax.MethodNameParser.Business_semanticContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#business_semantic}.
	 * @param ctx the parse tree
	 */
	void exitBusiness_semantic(com.mybatisgx.syntax.MethodNameParser.Business_semanticContext ctx);
	/**
	 * Enter a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(com.mybatisgx.syntax.MethodNameParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link com.mybatisgx.syntax.MethodNameParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(MethodNameParser.EndContext ctx);
}