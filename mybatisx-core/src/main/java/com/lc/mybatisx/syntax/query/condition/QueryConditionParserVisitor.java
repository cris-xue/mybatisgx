// Generated from F:/devops/mybatisx/mybatisx-core/src/main/resources/antlr/QueryConditionParser.g4 by ANTLR 4.13.2
package com.lc.mybatisx.syntax.query.condition;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QueryConditionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QueryConditionParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#query_condition_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_condition_statement(QueryConditionParser.Query_condition_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#condition_group_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_group_clause(QueryConditionParser.Condition_group_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#condition_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_clause(QueryConditionParser.Condition_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#field_condition_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_condition_op_clause(QueryConditionParser.Field_condition_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#logic_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic_op_clause(QueryConditionParser.Logic_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#field_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField_clause(QueryConditionParser.Field_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#comparison_op_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_op_clause(QueryConditionParser.Comparison_op_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#left_bracket_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeft_bracket_clause(QueryConditionParser.Left_bracket_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#right_bracket_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRight_bracket_clause(QueryConditionParser.Right_bracket_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryConditionParser#end}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(QueryConditionParser.EndContext ctx);
}