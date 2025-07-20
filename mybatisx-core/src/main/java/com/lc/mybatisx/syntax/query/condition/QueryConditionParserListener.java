// Generated from F:/devops/mybatisx/mybatisx-core/src/main/resources/antlr/QueryConditionParser.g4 by ANTLR 4.13.2
package com.lc.mybatisx.syntax.query.condition;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryConditionParser}.
 */
public interface QueryConditionParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#sql_condition_statement}.
	 * @param ctx the parse tree
	 */
	void enterSql_condition_statement(QueryConditionParser.Sql_condition_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#sql_condition_statement}.
	 * @param ctx the parse tree
	 */
	void exitSql_condition_statement(QueryConditionParser.Sql_condition_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#condition_group_clause}.
	 * @param ctx the parse tree
	 */
	void enterCondition_group_clause(QueryConditionParser.Condition_group_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#condition_group_clause}.
	 * @param ctx the parse tree
	 */
	void exitCondition_group_clause(QueryConditionParser.Condition_group_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#condition_clause}.
	 * @param ctx the parse tree
	 */
	void enterCondition_clause(QueryConditionParser.Condition_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#condition_clause}.
	 * @param ctx the parse tree
	 */
	void exitCondition_clause(QueryConditionParser.Condition_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterField_condition_op_clause(QueryConditionParser.Field_condition_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitField_condition_op_clause(QueryConditionParser.Field_condition_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#logic_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterLogic_op_clause(QueryConditionParser.Logic_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#logic_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitLogic_op_clause(QueryConditionParser.Logic_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#field_clause}.
	 * @param ctx the parse tree
	 */
	void enterField_clause(QueryConditionParser.Field_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#field_clause}.
	 * @param ctx the parse tree
	 */
	void exitField_clause(QueryConditionParser.Field_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void enterComparison_op_clause(QueryConditionParser.Comparison_op_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 */
	void exitComparison_op_clause(QueryConditionParser.Comparison_op_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void enterLeft_bracket_clause(QueryConditionParser.Left_bracket_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void exitLeft_bracket_clause(QueryConditionParser.Left_bracket_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void enterRight_bracket_clause(QueryConditionParser.Right_bracket_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 */
	void exitRight_bracket_clause(QueryConditionParser.Right_bracket_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryConditionParser#end}.
	 * @param ctx the parse tree
	 */
	void enterEnd(QueryConditionParser.EndContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryConditionParser#end}.
	 * @param ctx the parse tree
	 */
	void exitEnd(QueryConditionParser.EndContext ctx);
}