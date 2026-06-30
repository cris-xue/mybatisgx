// Generated from F:/owner_project/mybatisgx-ai_conding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
package com.mybatisgx.dsl.mgxql.syntax;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MgxqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INSERT_ACTION=1, DELETE_ACTION=2, UPDATE_ACTION=3, SELECT_ACTION=4, SELECT_ASTERISK=5, 
		SELECT_COUNT=6, SELECT_MAX=7, SELECT_MIN=8, SELECT_AVG=9, SELECT_SUM=10, 
		AS=11, FROM=12, LEFT=13, JOIN=14, ON=15, WHERE=16, LOGIC_AND=17, LOGIC_OR=18, 
		COMPARISON_OP_LT=19, COMPARISON_OP_LT_EQ=20, COMPARISON_OP_GT=21, COMPARISON_OP_GT_EQ=22, 
		EQUAL=23, COMPARISON_OP_NOT_EQ=24, COMPARISON_OP_NOT=25, COMPARISON_OP_BETWEEN=26, 
		COMPARISON_OP_IN=27, COMPARISON_OP_LIKE=28, COMPARISON_OP_LEFT_LIKE=29, 
		COMPARISON_OP_RIGHT_LIKE=30, COMPARISON_OP_IS_NULL=31, COMPARISON_OP_IS_NOT_NULL=32, 
		GROUP_BY=33, HAVING=34, ORDER_BY=35, ORDER_BY_DIRECTION=36, LIMIT=37, 
		LEFT_BRACKET=38, RIGHT_BRACKET=39, COMMA=40, COLON=41, DOT=42, QUESTION_MARK=43, 
		UPPER_NAME=44, QUOTED_NAME=45, LOWER_NAME=46, NUMBER=47, WS=48;
	public static final int
		RULE_sql_statement = 0, RULE_insert_statement = 1, RULE_insert_clause = 2, 
		RULE_delete_statement = 3, RULE_delete_clause = 4, RULE_update_statement = 5, 
		RULE_update_clause = 6, RULE_modify_entity = 7, RULE_select_statement = 8, 
		RULE_select_item_clause = 9, RULE_select_item = 10, RULE_select_column_all = 11, 
		RULE_select_column_custom = 12, RULE_select_action = 13, RULE_select_asterisk = 14, 
		RULE_select_item_alias = 15, RULE_aggregate_function = 16, RULE_aggregate_function_name = 17, 
		RULE_aggregate_function_argument = 18, RULE_select_max = 19, RULE_select_min = 20, 
		RULE_select_avg = 21, RULE_select_sum = 22, RULE_select_count = 23, RULE_select_from_clause = 24, 
		RULE_select_primary_entity = 25, RULE_select_join_entity = 26, RULE_select_entity = 27, 
		RULE_select_entity_alias = 28, RULE_select_from = 29, RULE_select_left_join = 30, 
		RULE_select_on = 31, RULE_select_on_expression = 32, RULE_on_equal = 33, 
		RULE_where_clause = 34, RULE_condition_or_expression = 35, RULE_condition_and_expression = 36, 
		RULE_condition_term = 37, RULE_condition_comparison = 38, RULE_condition_comparison_param = 39, 
		RULE_condition_comparison_not_param = 40, RULE_condition_value = 41, RULE_group_by_clause = 42, 
		RULE_group_by_expression = 43, RULE_having_clause = 44, RULE_having_or_expression = 45, 
		RULE_having_and_expression = 46, RULE_having_term = 47, RULE_having_comparison = 48, 
		RULE_having_value = 49, RULE_order_by_clause = 50, RULE_order_by_expression = 51, 
		RULE_limit_clause = 52, RULE_limit = 53, RULE_offset = 54, RULE_size = 55, 
		RULE_where_start = 56, RULE_logic_and = 57, RULE_logic_or = 58, RULE_relational_op = 59, 
		RULE_comparison_op_lt = 60, RULE_comparison_op_lt_eq = 61, RULE_comparison_op_gt = 62, 
		RULE_comparison_op_gt_eq = 63, RULE_comparison_op_eq = 64, RULE_comparison_op_not_eq = 65, 
		RULE_matching_op = 66, RULE_comparison_op_not = 67, RULE_comparison_op_between = 68, 
		RULE_comparison_op_in = 69, RULE_comparison_op_like = 70, RULE_comparison_op_left_like = 71, 
		RULE_comparison_op_right_like = 72, RULE_comparison_op_null = 73, RULE_comparison_op_is_null = 74, 
		RULE_comparison_op_is_not_null = 75, RULE_having = 76, RULE_group_by = 77, 
		RULE_order_by = 78, RULE_order_by_direction = 79, RULE_field_reference = 80, 
		RULE_parameter_reference = 81, RULE_alias = 82, RULE_entity_name = 83, 
		RULE_entity_name_alias = 84, RULE_field_name = 85, RULE_left_bracket = 86, 
		RULE_right_bracket = 87, RULE_dot = 88, RULE_param_colon = 89, RULE_comma = 90, 
		RULE_question_mark = 91, RULE_number = 92, RULE_end = 93;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "modify_entity", 
			"select_statement", "select_item_clause", "select_item", "select_column_all", 
			"select_column_custom", "select_action", "select_asterisk", "select_item_alias", 
			"aggregate_function", "aggregate_function_name", "aggregate_function_argument", 
			"select_max", "select_min", "select_avg", "select_sum", "select_count", 
			"select_from_clause", "select_primary_entity", "select_join_entity", 
			"select_entity", "select_entity_alias", "select_from", "select_left_join", 
			"select_on", "select_on_expression", "on_equal", "where_clause", "condition_or_expression", 
			"condition_and_expression", "condition_term", "condition_comparison", 
			"condition_comparison_param", "condition_comparison_not_param", "condition_value", 
			"group_by_clause", "group_by_expression", "having_clause", "having_or_expression", 
			"having_and_expression", "having_term", "having_comparison", "having_value", 
			"order_by_clause", "order_by_expression", "limit_clause", "limit", "offset", 
			"size", "where_start", "logic_and", "logic_or", "relational_op", "comparison_op_lt", 
			"comparison_op_lt_eq", "comparison_op_gt", "comparison_op_gt_eq", "comparison_op_eq", 
			"comparison_op_not_eq", "matching_op", "comparison_op_not", "comparison_op_between", 
			"comparison_op_in", "comparison_op_like", "comparison_op_left_like", 
			"comparison_op_right_like", "comparison_op_null", "comparison_op_is_null", 
			"comparison_op_is_not_null", "having", "group_by", "order_by", "order_by_direction", 
			"field_reference", "parameter_reference", "alias", "entity_name", "entity_name_alias", 
			"field_name", "left_bracket", "right_bracket", "dot", "param_colon", 
			"comma", "question_mark", "number", "end"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'insert'", "'delete'", "'update'", "'select'", "'*'", "'count'", 
			"'max'", "'min'", "'avg'", "'sum'", "'as'", "'from'", "'left'", "'join'", 
			"'on'", "'where'", "'and'", "'or'", "'<'", "'<='", "'>'", "'>='", "'='", 
			"'!='", "'not'", "'between'", "'in'", "'like'", "'left like'", "'right like'", 
			"'is null'", "'is not null'", "'group by'", "'having'", "'order by'", 
			null, "'limit'", "'('", "')'", "','", "':'", "'.'", "'?'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"SELECT_ASTERISK", "SELECT_COUNT", "SELECT_MAX", "SELECT_MIN", "SELECT_AVG", 
			"SELECT_SUM", "AS", "FROM", "LEFT", "JOIN", "ON", "WHERE", "LOGIC_AND", 
			"LOGIC_OR", "COMPARISON_OP_LT", "COMPARISON_OP_LT_EQ", "COMPARISON_OP_GT", 
			"COMPARISON_OP_GT_EQ", "EQUAL", "COMPARISON_OP_NOT_EQ", "COMPARISON_OP_NOT", 
			"COMPARISON_OP_BETWEEN", "COMPARISON_OP_IN", "COMPARISON_OP_LIKE", "COMPARISON_OP_LEFT_LIKE", 
			"COMPARISON_OP_RIGHT_LIKE", "COMPARISON_OP_IS_NULL", "COMPARISON_OP_IS_NOT_NULL", 
			"GROUP_BY", "HAVING", "ORDER_BY", "ORDER_BY_DIRECTION", "LIMIT", "LEFT_BRACKET", 
			"RIGHT_BRACKET", "COMMA", "COLON", "DOT", "QUESTION_MARK", "UPPER_NAME", 
			"QUOTED_NAME", "LOWER_NAME", "NUMBER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "MgxqlParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MgxqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Sql_statementContext extends ParserRuleContext {
		public EndContext end() {
			return getRuleContext(EndContext.class,0);
		}
		public Insert_statementContext insert_statement() {
			return getRuleContext(Insert_statementContext.class,0);
		}
		public Delete_statementContext delete_statement() {
			return getRuleContext(Delete_statementContext.class,0);
		}
		public Update_statementContext update_statement() {
			return getRuleContext(Update_statementContext.class,0);
		}
		public Select_statementContext select_statement() {
			return getRuleContext(Select_statementContext.class,0);
		}
		public Sql_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSql_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSql_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSql_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_statementContext sql_statement() throws RecognitionException {
		Sql_statementContext _localctx = new Sql_statementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sql_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				{
				setState(188);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				{
				setState(189);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				{
				setState(190);
				update_statement();
				}
				break;
			case SELECT_ACTION:
				{
				setState(191);
				select_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(194);
			end();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Insert_statementContext extends ParserRuleContext {
		public Insert_clauseContext insert_clause() {
			return getRuleContext(Insert_clauseContext.class,0);
		}
		public Insert_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterInsert_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitInsert_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitInsert_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_statementContext insert_statement() throws RecognitionException {
		Insert_statementContext _localctx = new Insert_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_insert_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			insert_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Insert_clauseContext extends ParserRuleContext {
		public TerminalNode INSERT_ACTION() { return getToken(MgxqlParser.INSERT_ACTION, 0); }
		public Insert_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterInsert_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitInsert_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitInsert_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_clauseContext insert_clause() throws RecognitionException {
		Insert_clauseContext _localctx = new Insert_clauseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_insert_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(INSERT_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Delete_statementContext extends ParserRuleContext {
		public Delete_clauseContext delete_clause() {
			return getRuleContext(Delete_clauseContext.class,0);
		}
		public Modify_entityContext modify_entity() {
			return getRuleContext(Modify_entityContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Delete_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterDelete_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitDelete_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitDelete_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_statementContext delete_statement() throws RecognitionException {
		Delete_statementContext _localctx = new Delete_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_delete_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			delete_clause();
			setState(201);
			modify_entity();
			setState(202);
			where_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Delete_clauseContext extends ParserRuleContext {
		public TerminalNode DELETE_ACTION() { return getToken(MgxqlParser.DELETE_ACTION, 0); }
		public Delete_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterDelete_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitDelete_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitDelete_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_clauseContext delete_clause() throws RecognitionException {
		Delete_clauseContext _localctx = new Delete_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_delete_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			match(DELETE_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_statementContext extends ParserRuleContext {
		public Update_clauseContext update_clause() {
			return getRuleContext(Update_clauseContext.class,0);
		}
		public Modify_entityContext modify_entity() {
			return getRuleContext(Modify_entityContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Update_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterUpdate_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitUpdate_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitUpdate_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_statementContext update_statement() throws RecognitionException {
		Update_statementContext _localctx = new Update_statementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_update_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			update_clause();
			setState(207);
			modify_entity();
			setState(208);
			where_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_clauseContext extends ParserRuleContext {
		public TerminalNode UPDATE_ACTION() { return getToken(MgxqlParser.UPDATE_ACTION, 0); }
		public Update_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterUpdate_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitUpdate_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitUpdate_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_clauseContext update_clause() throws RecognitionException {
		Update_clauseContext _localctx = new Update_clauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_update_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(UPDATE_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Modify_entityContext extends ParserRuleContext {
		public Entity_nameContext entity_name() {
			return getRuleContext(Entity_nameContext.class,0);
		}
		public Modify_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modify_entity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterModify_entity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitModify_entity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitModify_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Modify_entityContext modify_entity() throws RecognitionException {
		Modify_entityContext _localctx = new Modify_entityContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_modify_entity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			entity_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_statementContext extends ParserRuleContext {
		public Select_actionContext select_action() {
			return getRuleContext(Select_actionContext.class,0);
		}
		public Select_item_clauseContext select_item_clause() {
			return getRuleContext(Select_item_clauseContext.class,0);
		}
		public Select_from_clauseContext select_from_clause() {
			return getRuleContext(Select_from_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Group_by_clauseContext group_by_clause() {
			return getRuleContext(Group_by_clauseContext.class,0);
		}
		public Having_clauseContext having_clause() {
			return getRuleContext(Having_clauseContext.class,0);
		}
		public Order_by_clauseContext order_by_clause() {
			return getRuleContext(Order_by_clauseContext.class,0);
		}
		public Limit_clauseContext limit_clause() {
			return getRuleContext(Limit_clauseContext.class,0);
		}
		public Select_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_statementContext select_statement() throws RecognitionException {
		Select_statementContext _localctx = new Select_statementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_select_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			select_action();
			setState(215);
			select_item_clause();
			setState(216);
			select_from_clause();
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(217);
				where_clause();
				}
			}

			setState(221);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP_BY) {
				{
				setState(220);
				group_by_clause();
				}
			}

			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HAVING) {
				{
				setState(223);
				having_clause();
				}
			}

			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY) {
				{
				setState(226);
				order_by_clause();
				}
			}

			setState(230);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(229);
				limit_clause();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_item_clauseContext extends ParserRuleContext {
		public List<Select_itemContext> select_item() {
			return getRuleContexts(Select_itemContext.class);
		}
		public Select_itemContext select_item(int i) {
			return getRuleContext(Select_itemContext.class,i);
		}
		public List<CommaContext> comma() {
			return getRuleContexts(CommaContext.class);
		}
		public CommaContext comma(int i) {
			return getRuleContext(CommaContext.class,i);
		}
		public Select_item_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_item_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_item_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_item_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_item_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_item_clauseContext select_item_clause() throws RecognitionException {
		Select_item_clauseContext _localctx = new Select_item_clauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_select_item_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			select_item();
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(233);
				comma();
				setState(234);
				select_item();
				}
				}
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_itemContext extends ParserRuleContext {
		public Select_column_allContext select_column_all() {
			return getRuleContext(Select_column_allContext.class,0);
		}
		public Select_column_customContext select_column_custom() {
			return getRuleContext(Select_column_customContext.class,0);
		}
		public Aggregate_functionContext aggregate_function() {
			return getRuleContext(Aggregate_functionContext.class,0);
		}
		public Select_item_aliasContext select_item_alias() {
			return getRuleContext(Select_item_aliasContext.class,0);
		}
		public Select_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_itemContext select_item() throws RecognitionException {
		Select_itemContext _localctx = new Select_itemContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_select_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(241);
				select_column_all();
				}
				break;
			case 2:
				{
				setState(242);
				select_column_custom();
				}
				break;
			case 3:
				{
				setState(243);
				aggregate_function();
				}
				break;
			}
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(246);
				select_item_alias();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_column_allContext extends ParserRuleContext {
		public Select_asteriskContext select_asterisk() {
			return getRuleContext(Select_asteriskContext.class,0);
		}
		public Entity_name_aliasContext entity_name_alias() {
			return getRuleContext(Entity_name_aliasContext.class,0);
		}
		public DotContext dot() {
			return getRuleContext(DotContext.class,0);
		}
		public Select_column_allContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_column_all; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_column_all(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_column_all(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_column_all(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_column_allContext select_column_all() throws RecognitionException {
		Select_column_allContext _localctx = new Select_column_allContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_select_column_all);
		try {
			setState(254);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_ASTERISK:
				enterOuterAlt(_localctx, 1);
				{
				setState(249);
				select_asterisk();
				}
				break;
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(250);
				entity_name_alias();
				setState(251);
				dot();
				setState(252);
				select_asterisk();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_column_customContext extends ParserRuleContext {
		public Field_referenceContext field_reference() {
			return getRuleContext(Field_referenceContext.class,0);
		}
		public Select_column_customContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_column_custom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_column_custom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_column_custom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_column_custom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_column_customContext select_column_custom() throws RecognitionException {
		Select_column_customContext _localctx = new Select_column_customContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_select_column_custom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			field_reference();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_actionContext extends ParserRuleContext {
		public TerminalNode SELECT_ACTION() { return getToken(MgxqlParser.SELECT_ACTION, 0); }
		public Select_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_action(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_action(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_actionContext select_action() throws RecognitionException {
		Select_actionContext _localctx = new Select_actionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_select_action);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(SELECT_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_asteriskContext extends ParserRuleContext {
		public TerminalNode SELECT_ASTERISK() { return getToken(MgxqlParser.SELECT_ASTERISK, 0); }
		public Select_asteriskContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_asterisk; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_asterisk(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_asterisk(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_asterisk(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_asteriskContext select_asterisk() throws RecognitionException {
		Select_asteriskContext _localctx = new Select_asteriskContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_select_asterisk);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			match(SELECT_ASTERISK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_item_aliasContext extends ParserRuleContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public Field_nameContext field_name() {
			return getRuleContext(Field_nameContext.class,0);
		}
		public List<Entity_name_aliasContext> entity_name_alias() {
			return getRuleContexts(Entity_name_aliasContext.class);
		}
		public Entity_name_aliasContext entity_name_alias(int i) {
			return getRuleContext(Entity_name_aliasContext.class,i);
		}
		public List<DotContext> dot() {
			return getRuleContexts(DotContext.class);
		}
		public DotContext dot(int i) {
			return getRuleContext(DotContext.class,i);
		}
		public Select_item_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_item_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_item_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_item_alias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_item_alias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_item_aliasContext select_item_alias() throws RecognitionException {
		Select_item_aliasContext _localctx = new Select_item_aliasContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_select_item_alias);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			alias();
			setState(268);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(263);
					entity_name_alias();
					setState(264);
					dot();
					}
					} 
				}
				setState(270);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			setState(271);
			field_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Aggregate_functionContext extends ParserRuleContext {
		public Aggregate_function_nameContext aggregate_function_name() {
			return getRuleContext(Aggregate_function_nameContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Aggregate_function_argumentContext aggregate_function_argument() {
			return getRuleContext(Aggregate_function_argumentContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Aggregate_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterAggregate_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitAggregate_function(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitAggregate_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_functionContext aggregate_function() throws RecognitionException {
		Aggregate_functionContext _localctx = new Aggregate_functionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_aggregate_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			aggregate_function_name();
			setState(274);
			left_bracket();
			setState(275);
			aggregate_function_argument();
			setState(276);
			right_bracket();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Aggregate_function_nameContext extends ParserRuleContext {
		public Select_maxContext select_max() {
			return getRuleContext(Select_maxContext.class,0);
		}
		public Select_minContext select_min() {
			return getRuleContext(Select_minContext.class,0);
		}
		public Select_avgContext select_avg() {
			return getRuleContext(Select_avgContext.class,0);
		}
		public Select_sumContext select_sum() {
			return getRuleContext(Select_sumContext.class,0);
		}
		public Select_countContext select_count() {
			return getRuleContext(Select_countContext.class,0);
		}
		public Aggregate_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterAggregate_function_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitAggregate_function_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitAggregate_function_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_function_nameContext aggregate_function_name() throws RecognitionException {
		Aggregate_function_nameContext _localctx = new Aggregate_function_nameContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_aggregate_function_name);
		try {
			setState(283);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_MAX:
				enterOuterAlt(_localctx, 1);
				{
				setState(278);
				select_max();
				}
				break;
			case SELECT_MIN:
				enterOuterAlt(_localctx, 2);
				{
				setState(279);
				select_min();
				}
				break;
			case SELECT_AVG:
				enterOuterAlt(_localctx, 3);
				{
				setState(280);
				select_avg();
				}
				break;
			case SELECT_SUM:
				enterOuterAlt(_localctx, 4);
				{
				setState(281);
				select_sum();
				}
				break;
			case SELECT_COUNT:
				enterOuterAlt(_localctx, 5);
				{
				setState(282);
				select_count();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Aggregate_function_argumentContext extends ParserRuleContext {
		public Field_referenceContext field_reference() {
			return getRuleContext(Field_referenceContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public Select_asteriskContext select_asterisk() {
			return getRuleContext(Select_asteriskContext.class,0);
		}
		public Aggregate_function_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterAggregate_function_argument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitAggregate_function_argument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitAggregate_function_argument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_function_argumentContext aggregate_function_argument() throws RecognitionException {
		Aggregate_function_argumentContext _localctx = new Aggregate_function_argumentContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_aggregate_function_argument);
		try {
			setState(288);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(285);
				field_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(286);
				number();
				}
				break;
			case SELECT_ASTERISK:
				enterOuterAlt(_localctx, 3);
				{
				setState(287);
				select_asterisk();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_maxContext extends ParserRuleContext {
		public TerminalNode SELECT_MAX() { return getToken(MgxqlParser.SELECT_MAX, 0); }
		public Select_maxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_max; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_max(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_max(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_max(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_maxContext select_max() throws RecognitionException {
		Select_maxContext _localctx = new Select_maxContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_select_max);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(SELECT_MAX);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_minContext extends ParserRuleContext {
		public TerminalNode SELECT_MIN() { return getToken(MgxqlParser.SELECT_MIN, 0); }
		public Select_minContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_min; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_min(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_min(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_min(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_minContext select_min() throws RecognitionException {
		Select_minContext _localctx = new Select_minContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_select_min);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			match(SELECT_MIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_avgContext extends ParserRuleContext {
		public TerminalNode SELECT_AVG() { return getToken(MgxqlParser.SELECT_AVG, 0); }
		public Select_avgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_avg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_avg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_avg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_avg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_avgContext select_avg() throws RecognitionException {
		Select_avgContext _localctx = new Select_avgContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_select_avg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(SELECT_AVG);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_sumContext extends ParserRuleContext {
		public TerminalNode SELECT_SUM() { return getToken(MgxqlParser.SELECT_SUM, 0); }
		public Select_sumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_sum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_sum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_sum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_sum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_sumContext select_sum() throws RecognitionException {
		Select_sumContext _localctx = new Select_sumContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_select_sum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(SELECT_SUM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_countContext extends ParserRuleContext {
		public TerminalNode SELECT_COUNT() { return getToken(MgxqlParser.SELECT_COUNT, 0); }
		public Select_countContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_count; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_count(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_count(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_count(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_countContext select_count() throws RecognitionException {
		Select_countContext _localctx = new Select_countContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_select_count);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(SELECT_COUNT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_from_clauseContext extends ParserRuleContext {
		public Select_fromContext select_from() {
			return getRuleContext(Select_fromContext.class,0);
		}
		public Select_primary_entityContext select_primary_entity() {
			return getRuleContext(Select_primary_entityContext.class,0);
		}
		public List<Select_join_entityContext> select_join_entity() {
			return getRuleContexts(Select_join_entityContext.class);
		}
		public Select_join_entityContext select_join_entity(int i) {
			return getRuleContext(Select_join_entityContext.class,i);
		}
		public Select_from_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_from_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_from_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_from_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_from_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_from_clauseContext select_from_clause() throws RecognitionException {
		Select_from_clauseContext _localctx = new Select_from_clauseContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_select_from_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			select_from();
			setState(301);
			select_primary_entity();
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LEFT) {
				{
				{
				setState(302);
				select_join_entity();
				}
				}
				setState(307);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_primary_entityContext extends ParserRuleContext {
		public Select_entityContext select_entity() {
			return getRuleContext(Select_entityContext.class,0);
		}
		public Select_entity_aliasContext select_entity_alias() {
			return getRuleContext(Select_entity_aliasContext.class,0);
		}
		public Select_primary_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_primary_entity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_primary_entity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_primary_entity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_primary_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_primary_entityContext select_primary_entity() throws RecognitionException {
		Select_primary_entityContext _localctx = new Select_primary_entityContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_select_primary_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			select_entity();
			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTED_NAME || _la==LOWER_NAME) {
				{
				setState(309);
				select_entity_alias();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_join_entityContext extends ParserRuleContext {
		public Select_left_joinContext select_left_join() {
			return getRuleContext(Select_left_joinContext.class,0);
		}
		public Select_entityContext select_entity() {
			return getRuleContext(Select_entityContext.class,0);
		}
		public Select_onContext select_on() {
			return getRuleContext(Select_onContext.class,0);
		}
		public Select_on_expressionContext select_on_expression() {
			return getRuleContext(Select_on_expressionContext.class,0);
		}
		public Select_entity_aliasContext select_entity_alias() {
			return getRuleContext(Select_entity_aliasContext.class,0);
		}
		public Select_join_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_join_entity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_join_entity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_join_entity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_join_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_join_entityContext select_join_entity() throws RecognitionException {
		Select_join_entityContext _localctx = new Select_join_entityContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_select_join_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			select_left_join();
			setState(313);
			select_entity();
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTED_NAME || _la==LOWER_NAME) {
				{
				setState(314);
				select_entity_alias();
				}
			}

			setState(317);
			select_on();
			setState(318);
			select_on_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_entityContext extends ParserRuleContext {
		public Entity_nameContext entity_name() {
			return getRuleContext(Entity_nameContext.class,0);
		}
		public Select_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_entity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_entity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_entity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_entityContext select_entity() throws RecognitionException {
		Select_entityContext _localctx = new Select_entityContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_select_entity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			entity_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_entity_aliasContext extends ParserRuleContext {
		public Entity_name_aliasContext entity_name_alias() {
			return getRuleContext(Entity_name_aliasContext.class,0);
		}
		public Select_entity_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_entity_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_entity_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_entity_alias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_entity_alias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_entity_aliasContext select_entity_alias() throws RecognitionException {
		Select_entity_aliasContext _localctx = new Select_entity_aliasContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_select_entity_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			entity_name_alias();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_fromContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(MgxqlParser.FROM, 0); }
		public Select_fromContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_from; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_from(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_from(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_from(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_fromContext select_from() throws RecognitionException {
		Select_fromContext _localctx = new Select_fromContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_select_from);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			match(FROM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_left_joinContext extends ParserRuleContext {
		public TerminalNode LEFT() { return getToken(MgxqlParser.LEFT, 0); }
		public TerminalNode JOIN() { return getToken(MgxqlParser.JOIN, 0); }
		public Select_left_joinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_left_join; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_left_join(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_left_join(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_left_join(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_left_joinContext select_left_join() throws RecognitionException {
		Select_left_joinContext _localctx = new Select_left_joinContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_select_left_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(LEFT);
			setState(327);
			match(JOIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_onContext extends ParserRuleContext {
		public TerminalNode ON() { return getToken(MgxqlParser.ON, 0); }
		public Select_onContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_on; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_on(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_on(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_on(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_onContext select_on() throws RecognitionException {
		Select_onContext _localctx = new Select_onContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_select_on);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(ON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_on_expressionContext extends ParserRuleContext {
		public List<Entity_name_aliasContext> entity_name_alias() {
			return getRuleContexts(Entity_name_aliasContext.class);
		}
		public Entity_name_aliasContext entity_name_alias(int i) {
			return getRuleContext(Entity_name_aliasContext.class,i);
		}
		public On_equalContext on_equal() {
			return getRuleContext(On_equalContext.class,0);
		}
		public Select_on_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_on_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSelect_on_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSelect_on_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSelect_on_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_on_expressionContext select_on_expression() throws RecognitionException {
		Select_on_expressionContext _localctx = new Select_on_expressionContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_select_on_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			entity_name_alias();
			setState(332);
			on_equal();
			setState(333);
			entity_name_alias();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class On_equalContext extends ParserRuleContext {
		public TerminalNode EQUAL() { return getToken(MgxqlParser.EQUAL, 0); }
		public On_equalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_on_equal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOn_equal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOn_equal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOn_equal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final On_equalContext on_equal() throws RecognitionException {
		On_equalContext _localctx = new On_equalContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_on_equal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(EQUAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Where_clauseContext extends ParserRuleContext {
		public Where_startContext where_start() {
			return getRuleContext(Where_startContext.class,0);
		}
		public Condition_or_expressionContext condition_or_expression() {
			return getRuleContext(Condition_or_expressionContext.class,0);
		}
		public Where_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhere_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhere_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhere_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_clauseContext where_clause() throws RecognitionException {
		Where_clauseContext _localctx = new Where_clauseContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			where_start();
			setState(338);
			condition_or_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_or_expressionContext extends ParserRuleContext {
		public List<Condition_and_expressionContext> condition_and_expression() {
			return getRuleContexts(Condition_and_expressionContext.class);
		}
		public Condition_and_expressionContext condition_and_expression(int i) {
			return getRuleContext(Condition_and_expressionContext.class,i);
		}
		public List<Logic_orContext> logic_or() {
			return getRuleContexts(Logic_orContext.class);
		}
		public Logic_orContext logic_or(int i) {
			return getRuleContext(Logic_orContext.class,i);
		}
		public Condition_or_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_or_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_or_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_or_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_or_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_or_expressionContext condition_or_expression() throws RecognitionException {
		Condition_or_expressionContext _localctx = new Condition_or_expressionContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_condition_or_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			condition_and_expression();
			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OR) {
				{
				{
				setState(341);
				logic_or();
				setState(342);
				condition_and_expression();
				}
				}
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_and_expressionContext extends ParserRuleContext {
		public List<Condition_termContext> condition_term() {
			return getRuleContexts(Condition_termContext.class);
		}
		public Condition_termContext condition_term(int i) {
			return getRuleContext(Condition_termContext.class,i);
		}
		public List<Logic_andContext> logic_and() {
			return getRuleContexts(Logic_andContext.class);
		}
		public Logic_andContext logic_and(int i) {
			return getRuleContext(Logic_andContext.class,i);
		}
		public Condition_and_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_and_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_and_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_and_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_and_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_and_expressionContext condition_and_expression() throws RecognitionException {
		Condition_and_expressionContext _localctx = new Condition_and_expressionContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_condition_and_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			condition_term();
			setState(355);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_AND) {
				{
				{
				setState(350);
				logic_and();
				setState(351);
				condition_term();
				}
				}
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_termContext extends ParserRuleContext {
		public Condition_comparisonContext condition_comparison() {
			return getRuleContext(Condition_comparisonContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Condition_or_expressionContext condition_or_expression() {
			return getRuleContext(Condition_or_expressionContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Condition_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_termContext condition_term() throws RecognitionException {
		Condition_termContext _localctx = new Condition_termContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_condition_term);
		try {
			setState(363);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUESTION_MARK:
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(358);
				condition_comparison();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(359);
				left_bracket();
				setState(360);
				condition_or_expression();
				setState(361);
				right_bracket();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_comparisonContext extends ParserRuleContext {
		public Field_referenceContext field_reference() {
			return getRuleContext(Field_referenceContext.class,0);
		}
		public Condition_comparison_paramContext condition_comparison_param() {
			return getRuleContext(Condition_comparison_paramContext.class,0);
		}
		public Condition_comparison_not_paramContext condition_comparison_not_param() {
			return getRuleContext(Condition_comparison_not_paramContext.class,0);
		}
		public Question_markContext question_mark() {
			return getRuleContext(Question_markContext.class,0);
		}
		public Condition_comparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_comparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_comparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_comparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_comparisonContext condition_comparison() throws RecognitionException {
		Condition_comparisonContext _localctx = new Condition_comparisonContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_condition_comparison);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUESTION_MARK) {
				{
				setState(365);
				question_mark();
				}
			}

			setState(368);
			field_reference();
			setState(371);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_LT:
			case COMPARISON_OP_LT_EQ:
			case COMPARISON_OP_GT:
			case COMPARISON_OP_GT_EQ:
			case EQUAL:
			case COMPARISON_OP_NOT_EQ:
			case COMPARISON_OP_NOT:
			case COMPARISON_OP_BETWEEN:
			case COMPARISON_OP_IN:
			case COMPARISON_OP_LIKE:
			case COMPARISON_OP_LEFT_LIKE:
			case COMPARISON_OP_RIGHT_LIKE:
				{
				setState(369);
				condition_comparison_param();
				}
				break;
			case COMPARISON_OP_IS_NULL:
			case COMPARISON_OP_IS_NOT_NULL:
				{
				setState(370);
				condition_comparison_not_param();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_comparison_paramContext extends ParserRuleContext {
		public Condition_valueContext condition_value() {
			return getRuleContext(Condition_valueContext.class,0);
		}
		public Relational_opContext relational_op() {
			return getRuleContext(Relational_opContext.class,0);
		}
		public Matching_opContext matching_op() {
			return getRuleContext(Matching_opContext.class,0);
		}
		public Condition_comparison_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_comparison_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_comparison_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_comparison_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_comparison_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_comparison_paramContext condition_comparison_param() throws RecognitionException {
		Condition_comparison_paramContext _localctx = new Condition_comparison_paramContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_condition_comparison_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_LT:
			case COMPARISON_OP_LT_EQ:
			case COMPARISON_OP_GT:
			case COMPARISON_OP_GT_EQ:
			case EQUAL:
			case COMPARISON_OP_NOT_EQ:
				{
				setState(373);
				relational_op();
				}
				break;
			case COMPARISON_OP_NOT:
			case COMPARISON_OP_BETWEEN:
			case COMPARISON_OP_IN:
			case COMPARISON_OP_LIKE:
			case COMPARISON_OP_LEFT_LIKE:
			case COMPARISON_OP_RIGHT_LIKE:
				{
				setState(374);
				matching_op();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(377);
			condition_value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_comparison_not_paramContext extends ParserRuleContext {
		public Comparison_op_nullContext comparison_op_null() {
			return getRuleContext(Comparison_op_nullContext.class,0);
		}
		public Condition_comparison_not_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_comparison_not_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_comparison_not_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_comparison_not_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_comparison_not_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_comparison_not_paramContext condition_comparison_not_param() throws RecognitionException {
		Condition_comparison_not_paramContext _localctx = new Condition_comparison_not_paramContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_condition_comparison_not_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			comparison_op_null();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_valueContext extends ParserRuleContext {
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public Condition_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCondition_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCondition_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCondition_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_valueContext condition_value() throws RecognitionException {
		Condition_valueContext _localctx = new Condition_valueContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_condition_value);
		try {
			setState(383);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(381);
				parameter_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(382);
				number();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Group_by_clauseContext extends ParserRuleContext {
		public Group_byContext group_by() {
			return getRuleContext(Group_byContext.class,0);
		}
		public Group_by_expressionContext group_by_expression() {
			return getRuleContext(Group_by_expressionContext.class,0);
		}
		public Group_by_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGroup_by_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGroup_by_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGroup_by_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_clauseContext group_by_clause() throws RecognitionException {
		Group_by_clauseContext _localctx = new Group_by_clauseContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_group_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			group_by();
			setState(386);
			group_by_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Group_by_expressionContext extends ParserRuleContext {
		public List<Field_referenceContext> field_reference() {
			return getRuleContexts(Field_referenceContext.class);
		}
		public Field_referenceContext field_reference(int i) {
			return getRuleContext(Field_referenceContext.class,i);
		}
		public List<CommaContext> comma() {
			return getRuleContexts(CommaContext.class);
		}
		public CommaContext comma(int i) {
			return getRuleContext(CommaContext.class,i);
		}
		public Group_by_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGroup_by_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGroup_by_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGroup_by_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_expressionContext group_by_expression() throws RecognitionException {
		Group_by_expressionContext _localctx = new Group_by_expressionContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_group_by_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			field_reference();
			setState(394);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(389);
				comma();
				setState(390);
				field_reference();
				}
				}
				setState(396);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_clauseContext extends ParserRuleContext {
		public HavingContext having() {
			return getRuleContext(HavingContext.class,0);
		}
		public Having_or_expressionContext having_or_expression() {
			return getRuleContext(Having_or_expressionContext.class,0);
		}
		public Having_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_clauseContext having_clause() throws RecognitionException {
		Having_clauseContext _localctx = new Having_clauseContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			having();
			setState(398);
			having_or_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_or_expressionContext extends ParserRuleContext {
		public List<Having_and_expressionContext> having_and_expression() {
			return getRuleContexts(Having_and_expressionContext.class);
		}
		public Having_and_expressionContext having_and_expression(int i) {
			return getRuleContext(Having_and_expressionContext.class,i);
		}
		public List<Logic_orContext> logic_or() {
			return getRuleContexts(Logic_orContext.class);
		}
		public Logic_orContext logic_or(int i) {
			return getRuleContext(Logic_orContext.class,i);
		}
		public Having_or_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_or_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_or_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_or_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_or_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_or_expressionContext having_or_expression() throws RecognitionException {
		Having_or_expressionContext _localctx = new Having_or_expressionContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_having_or_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			having_and_expression();
			setState(406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OR) {
				{
				{
				setState(401);
				logic_or();
				setState(402);
				having_and_expression();
				}
				}
				setState(408);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_and_expressionContext extends ParserRuleContext {
		public List<Having_termContext> having_term() {
			return getRuleContexts(Having_termContext.class);
		}
		public Having_termContext having_term(int i) {
			return getRuleContext(Having_termContext.class,i);
		}
		public List<Logic_andContext> logic_and() {
			return getRuleContexts(Logic_andContext.class);
		}
		public Logic_andContext logic_and(int i) {
			return getRuleContext(Logic_andContext.class,i);
		}
		public Having_and_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_and_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_and_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_and_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_and_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_and_expressionContext having_and_expression() throws RecognitionException {
		Having_and_expressionContext _localctx = new Having_and_expressionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_having_and_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			having_term();
			setState(415);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_AND) {
				{
				{
				setState(410);
				logic_and();
				setState(411);
				having_term();
				}
				}
				setState(417);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_termContext extends ParserRuleContext {
		public Having_comparisonContext having_comparison() {
			return getRuleContext(Having_comparisonContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Having_or_expressionContext having_or_expression() {
			return getRuleContext(Having_or_expressionContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Having_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_termContext having_term() throws RecognitionException {
		Having_termContext _localctx = new Having_termContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_having_term);
		try {
			setState(423);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_COUNT:
			case SELECT_MAX:
			case SELECT_MIN:
			case SELECT_AVG:
			case SELECT_SUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(418);
				having_comparison();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(419);
				left_bracket();
				setState(420);
				having_or_expression();
				setState(421);
				right_bracket();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_comparisonContext extends ParserRuleContext {
		public Aggregate_functionContext aggregate_function() {
			return getRuleContext(Aggregate_functionContext.class,0);
		}
		public Relational_opContext relational_op() {
			return getRuleContext(Relational_opContext.class,0);
		}
		public Having_valueContext having_value() {
			return getRuleContext(Having_valueContext.class,0);
		}
		public Having_comparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_comparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_comparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_comparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_comparisonContext having_comparison() throws RecognitionException {
		Having_comparisonContext _localctx = new Having_comparisonContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_having_comparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(425);
			aggregate_function();
			setState(426);
			relational_op();
			setState(427);
			having_value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_valueContext extends ParserRuleContext {
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public Having_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_valueContext having_value() throws RecognitionException {
		Having_valueContext _localctx = new Having_valueContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_having_value);
		try {
			setState(431);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(429);
				parameter_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				number();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_by_clauseContext extends ParserRuleContext {
		public Order_byContext order_by() {
			return getRuleContext(Order_byContext.class,0);
		}
		public List<Order_by_expressionContext> order_by_expression() {
			return getRuleContexts(Order_by_expressionContext.class);
		}
		public Order_by_expressionContext order_by_expression(int i) {
			return getRuleContext(Order_by_expressionContext.class,i);
		}
		public List<CommaContext> comma() {
			return getRuleContexts(CommaContext.class);
		}
		public CommaContext comma(int i) {
			return getRuleContext(CommaContext.class,i);
		}
		public Order_by_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOrder_by_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOrder_by_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOrder_by_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_clauseContext order_by_clause() throws RecognitionException {
		Order_by_clauseContext _localctx = new Order_by_clauseContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_order_by_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			order_by();
			setState(434);
			order_by_expression();
			setState(440);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(435);
				comma();
				setState(436);
				order_by_expression();
				}
				}
				setState(442);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_by_expressionContext extends ParserRuleContext {
		public Field_referenceContext field_reference() {
			return getRuleContext(Field_referenceContext.class,0);
		}
		public Order_by_directionContext order_by_direction() {
			return getRuleContext(Order_by_directionContext.class,0);
		}
		public Order_by_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOrder_by_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOrder_by_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOrder_by_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_expressionContext order_by_expression() throws RecognitionException {
		Order_by_expressionContext _localctx = new Order_by_expressionContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_order_by_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			field_reference();
			setState(445);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY_DIRECTION) {
				{
				setState(444);
				order_by_direction();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Limit_clauseContext extends ParserRuleContext {
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public OffsetContext offset() {
			return getRuleContext(OffsetContext.class,0);
		}
		public CommaContext comma() {
			return getRuleContext(CommaContext.class,0);
		}
		public SizeContext size() {
			return getRuleContext(SizeContext.class,0);
		}
		public Limit_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLimit_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLimit_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLimit_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Limit_clauseContext limit_clause() throws RecognitionException {
		Limit_clauseContext _localctx = new Limit_clauseContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_limit_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
			limit();
			setState(448);
			offset();
			setState(449);
			comma();
			setState(450);
			size();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LimitContext extends ParserRuleContext {
		public TerminalNode LIMIT() { return getToken(MgxqlParser.LIMIT, 0); }
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLimit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLimit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLimit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_limit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			match(LIMIT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OffsetContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(MgxqlParser.NUMBER, 0); }
		public OffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SizeContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(MgxqlParser.NUMBER, 0); }
		public SizeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_size; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSize(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSize(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSize(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SizeContext size() throws RecognitionException {
		SizeContext _localctx = new SizeContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_size);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Where_startContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(MgxqlParser.WHERE, 0); }
		public Where_startContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhere_start(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhere_start(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhere_start(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_startContext where_start() throws RecognitionException {
		Where_startContext _localctx = new Where_startContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_where_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(458);
			match(WHERE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Logic_andContext extends ParserRuleContext {
		public TerminalNode LOGIC_AND() { return getToken(MgxqlParser.LOGIC_AND, 0); }
		public Logic_andContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_and; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLogic_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLogic_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLogic_and(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_andContext logic_and() throws RecognitionException {
		Logic_andContext _localctx = new Logic_andContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_logic_and);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
			match(LOGIC_AND);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Logic_orContext extends ParserRuleContext {
		public TerminalNode LOGIC_OR() { return getToken(MgxqlParser.LOGIC_OR, 0); }
		public Logic_orContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_or; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLogic_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLogic_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLogic_or(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_orContext logic_or() throws RecognitionException {
		Logic_orContext _localctx = new Logic_orContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_logic_or);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
			match(LOGIC_OR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Relational_opContext extends ParserRuleContext {
		public Comparison_op_ltContext comparison_op_lt() {
			return getRuleContext(Comparison_op_ltContext.class,0);
		}
		public Comparison_op_lt_eqContext comparison_op_lt_eq() {
			return getRuleContext(Comparison_op_lt_eqContext.class,0);
		}
		public Comparison_op_gtContext comparison_op_gt() {
			return getRuleContext(Comparison_op_gtContext.class,0);
		}
		public Comparison_op_gt_eqContext comparison_op_gt_eq() {
			return getRuleContext(Comparison_op_gt_eqContext.class,0);
		}
		public Comparison_op_eqContext comparison_op_eq() {
			return getRuleContext(Comparison_op_eqContext.class,0);
		}
		public Comparison_op_not_eqContext comparison_op_not_eq() {
			return getRuleContext(Comparison_op_not_eqContext.class,0);
		}
		public Relational_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterRelational_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitRelational_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitRelational_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Relational_opContext relational_op() throws RecognitionException {
		Relational_opContext _localctx = new Relational_opContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_relational_op);
		try {
			setState(470);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_LT:
				enterOuterAlt(_localctx, 1);
				{
				setState(464);
				comparison_op_lt();
				}
				break;
			case COMPARISON_OP_LT_EQ:
				enterOuterAlt(_localctx, 2);
				{
				setState(465);
				comparison_op_lt_eq();
				}
				break;
			case COMPARISON_OP_GT:
				enterOuterAlt(_localctx, 3);
				{
				setState(466);
				comparison_op_gt();
				}
				break;
			case COMPARISON_OP_GT_EQ:
				enterOuterAlt(_localctx, 4);
				{
				setState(467);
				comparison_op_gt_eq();
				}
				break;
			case EQUAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(468);
				comparison_op_eq();
				}
				break;
			case COMPARISON_OP_NOT_EQ:
				enterOuterAlt(_localctx, 6);
				{
				setState(469);
				comparison_op_not_eq();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_ltContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_LT() { return getToken(MgxqlParser.COMPARISON_OP_LT, 0); }
		public Comparison_op_ltContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_lt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_lt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_lt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_lt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_ltContext comparison_op_lt() throws RecognitionException {
		Comparison_op_ltContext _localctx = new Comparison_op_ltContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_comparison_op_lt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(COMPARISON_OP_LT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_lt_eqContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_LT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_LT_EQ, 0); }
		public Comparison_op_lt_eqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_lt_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_lt_eq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_lt_eq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_lt_eq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_lt_eqContext comparison_op_lt_eq() throws RecognitionException {
		Comparison_op_lt_eqContext _localctx = new Comparison_op_lt_eqContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_comparison_op_lt_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
			match(COMPARISON_OP_LT_EQ);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_gtContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_GT() { return getToken(MgxqlParser.COMPARISON_OP_GT, 0); }
		public Comparison_op_gtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_gt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_gt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_gt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_gt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_gtContext comparison_op_gt() throws RecognitionException {
		Comparison_op_gtContext _localctx = new Comparison_op_gtContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_comparison_op_gt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			match(COMPARISON_OP_GT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_gt_eqContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_GT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_GT_EQ, 0); }
		public Comparison_op_gt_eqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_gt_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_gt_eq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_gt_eq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_gt_eq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_gt_eqContext comparison_op_gt_eq() throws RecognitionException {
		Comparison_op_gt_eqContext _localctx = new Comparison_op_gt_eqContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_comparison_op_gt_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			match(COMPARISON_OP_GT_EQ);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_eqContext extends ParserRuleContext {
		public TerminalNode EQUAL() { return getToken(MgxqlParser.EQUAL, 0); }
		public Comparison_op_eqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_eq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_eq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_eq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_eqContext comparison_op_eq() throws RecognitionException {
		Comparison_op_eqContext _localctx = new Comparison_op_eqContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_comparison_op_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(480);
			match(EQUAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_not_eqContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_NOT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_NOT_EQ, 0); }
		public Comparison_op_not_eqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_not_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_not_eq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_not_eq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_not_eq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_not_eqContext comparison_op_not_eq() throws RecognitionException {
		Comparison_op_not_eqContext _localctx = new Comparison_op_not_eqContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_comparison_op_not_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(482);
			match(COMPARISON_OP_NOT_EQ);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Matching_opContext extends ParserRuleContext {
		public Comparison_op_betweenContext comparison_op_between() {
			return getRuleContext(Comparison_op_betweenContext.class,0);
		}
		public Comparison_op_inContext comparison_op_in() {
			return getRuleContext(Comparison_op_inContext.class,0);
		}
		public Comparison_op_likeContext comparison_op_like() {
			return getRuleContext(Comparison_op_likeContext.class,0);
		}
		public Comparison_op_left_likeContext comparison_op_left_like() {
			return getRuleContext(Comparison_op_left_likeContext.class,0);
		}
		public Comparison_op_right_likeContext comparison_op_right_like() {
			return getRuleContext(Comparison_op_right_likeContext.class,0);
		}
		public Comparison_op_notContext comparison_op_not() {
			return getRuleContext(Comparison_op_notContext.class,0);
		}
		public Matching_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matching_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterMatching_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitMatching_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitMatching_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Matching_opContext matching_op() throws RecognitionException {
		Matching_opContext _localctx = new Matching_opContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_matching_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMPARISON_OP_NOT) {
				{
				setState(484);
				comparison_op_not();
				}
			}

			setState(492);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_BETWEEN:
				{
				setState(487);
				comparison_op_between();
				}
				break;
			case COMPARISON_OP_IN:
				{
				setState(488);
				comparison_op_in();
				}
				break;
			case COMPARISON_OP_LIKE:
				{
				setState(489);
				comparison_op_like();
				}
				break;
			case COMPARISON_OP_LEFT_LIKE:
				{
				setState(490);
				comparison_op_left_like();
				}
				break;
			case COMPARISON_OP_RIGHT_LIKE:
				{
				setState(491);
				comparison_op_right_like();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_notContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_NOT() { return getToken(MgxqlParser.COMPARISON_OP_NOT, 0); }
		public Comparison_op_notContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_not(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_not(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_not(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_notContext comparison_op_not() throws RecognitionException {
		Comparison_op_notContext _localctx = new Comparison_op_notContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_comparison_op_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			match(COMPARISON_OP_NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_betweenContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_BETWEEN() { return getToken(MgxqlParser.COMPARISON_OP_BETWEEN, 0); }
		public Comparison_op_betweenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_between; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_between(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_between(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_between(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_betweenContext comparison_op_between() throws RecognitionException {
		Comparison_op_betweenContext _localctx = new Comparison_op_betweenContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_comparison_op_between);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			match(COMPARISON_OP_BETWEEN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_inContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_IN() { return getToken(MgxqlParser.COMPARISON_OP_IN, 0); }
		public Comparison_op_inContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_in; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_in(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_in(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_in(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_inContext comparison_op_in() throws RecognitionException {
		Comparison_op_inContext _localctx = new Comparison_op_inContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_comparison_op_in);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(498);
			match(COMPARISON_OP_IN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_likeContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_LIKE() { return getToken(MgxqlParser.COMPARISON_OP_LIKE, 0); }
		public Comparison_op_likeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_like; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_like(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_like(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_like(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_likeContext comparison_op_like() throws RecognitionException {
		Comparison_op_likeContext _localctx = new Comparison_op_likeContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_comparison_op_like);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(500);
			match(COMPARISON_OP_LIKE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_left_likeContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_LEFT_LIKE() { return getToken(MgxqlParser.COMPARISON_OP_LEFT_LIKE, 0); }
		public Comparison_op_left_likeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_left_like; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_left_like(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_left_like(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_left_like(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_left_likeContext comparison_op_left_like() throws RecognitionException {
		Comparison_op_left_likeContext _localctx = new Comparison_op_left_likeContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_comparison_op_left_like);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(502);
			match(COMPARISON_OP_LEFT_LIKE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_right_likeContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_RIGHT_LIKE() { return getToken(MgxqlParser.COMPARISON_OP_RIGHT_LIKE, 0); }
		public Comparison_op_right_likeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_right_like; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_right_like(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_right_like(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_right_like(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_right_likeContext comparison_op_right_like() throws RecognitionException {
		Comparison_op_right_likeContext _localctx = new Comparison_op_right_likeContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_comparison_op_right_like);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(504);
			match(COMPARISON_OP_RIGHT_LIKE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_nullContext extends ParserRuleContext {
		public Comparison_op_is_nullContext comparison_op_is_null() {
			return getRuleContext(Comparison_op_is_nullContext.class,0);
		}
		public Comparison_op_is_not_nullContext comparison_op_is_not_null() {
			return getRuleContext(Comparison_op_is_not_nullContext.class,0);
		}
		public Comparison_op_nullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_null; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_null(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_null(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_null(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_nullContext comparison_op_null() throws RecognitionException {
		Comparison_op_nullContext _localctx = new Comparison_op_nullContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_comparison_op_null);
		try {
			setState(508);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_IS_NULL:
				enterOuterAlt(_localctx, 1);
				{
				setState(506);
				comparison_op_is_null();
				}
				break;
			case COMPARISON_OP_IS_NOT_NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(507);
				comparison_op_is_not_null();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_is_nullContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_IS_NULL() { return getToken(MgxqlParser.COMPARISON_OP_IS_NULL, 0); }
		public Comparison_op_is_nullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_is_null; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_is_null(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_is_null(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_is_null(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_is_nullContext comparison_op_is_null() throws RecognitionException {
		Comparison_op_is_nullContext _localctx = new Comparison_op_is_nullContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_comparison_op_is_null);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			match(COMPARISON_OP_IS_NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_op_is_not_nullContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP_IS_NOT_NULL() { return getToken(MgxqlParser.COMPARISON_OP_IS_NOT_NULL, 0); }
		public Comparison_op_is_not_nullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_is_not_null; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComparison_op_is_not_null(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComparison_op_is_not_null(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComparison_op_is_not_null(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_is_not_nullContext comparison_op_is_not_null() throws RecognitionException {
		Comparison_op_is_not_nullContext _localctx = new Comparison_op_is_not_nullContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_comparison_op_is_not_null);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			match(COMPARISON_OP_IS_NOT_NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HavingContext extends ParserRuleContext {
		public TerminalNode HAVING() { return getToken(MgxqlParser.HAVING, 0); }
		public HavingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterHaving(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitHaving(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitHaving(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HavingContext having() throws RecognitionException {
		HavingContext _localctx = new HavingContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_having);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(514);
			match(HAVING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Group_byContext extends ParserRuleContext {
		public TerminalNode GROUP_BY() { return getToken(MgxqlParser.GROUP_BY, 0); }
		public Group_byContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGroup_by(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGroup_by(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGroup_by(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_byContext group_by() throws RecognitionException {
		Group_byContext _localctx = new Group_byContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_group_by);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			match(GROUP_BY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_byContext extends ParserRuleContext {
		public TerminalNode ORDER_BY() { return getToken(MgxqlParser.ORDER_BY, 0); }
		public Order_byContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOrder_by(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOrder_by(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOrder_by(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_byContext order_by() throws RecognitionException {
		Order_byContext _localctx = new Order_byContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_order_by);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			match(ORDER_BY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_by_directionContext extends ParserRuleContext {
		public TerminalNode ORDER_BY_DIRECTION() { return getToken(MgxqlParser.ORDER_BY_DIRECTION, 0); }
		public Order_by_directionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOrder_by_direction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOrder_by_direction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOrder_by_direction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_directionContext order_by_direction() throws RecognitionException {
		Order_by_directionContext _localctx = new Order_by_directionContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_order_by_direction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(520);
			match(ORDER_BY_DIRECTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Field_referenceContext extends ParserRuleContext {
		public Field_nameContext field_name() {
			return getRuleContext(Field_nameContext.class,0);
		}
		public Entity_name_aliasContext entity_name_alias() {
			return getRuleContext(Entity_name_aliasContext.class,0);
		}
		public DotContext dot() {
			return getRuleContext(DotContext.class,0);
		}
		public Field_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterField_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitField_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitField_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_referenceContext field_reference() throws RecognitionException {
		Field_referenceContext _localctx = new Field_referenceContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_field_reference);
		try {
			setState(527);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(522);
				field_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(523);
				entity_name_alias();
				setState(524);
				dot();
				setState(525);
				field_name();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Parameter_referenceContext extends ParserRuleContext {
		public Param_colonContext param_colon() {
			return getRuleContext(Param_colonContext.class,0);
		}
		public List<Field_nameContext> field_name() {
			return getRuleContexts(Field_nameContext.class);
		}
		public Field_nameContext field_name(int i) {
			return getRuleContext(Field_nameContext.class,i);
		}
		public List<DotContext> dot() {
			return getRuleContexts(DotContext.class);
		}
		public DotContext dot(int i) {
			return getRuleContext(DotContext.class,i);
		}
		public Parameter_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterParameter_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitParameter_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitParameter_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Parameter_referenceContext parameter_reference() throws RecognitionException {
		Parameter_referenceContext _localctx = new Parameter_referenceContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_parameter_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(529);
			param_colon();
			setState(530);
			field_name();
			setState(536);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(531);
				dot();
				setState(532);
				field_name();
				}
				}
				setState(538);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AliasContext extends ParserRuleContext {
		public TerminalNode AS() { return getToken(MgxqlParser.AS, 0); }
		public AliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterAlias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitAlias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitAlias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasContext alias() throws RecognitionException {
		AliasContext _localctx = new AliasContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(539);
			match(AS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Entity_nameContext extends ParserRuleContext {
		public TerminalNode UPPER_NAME() { return getToken(MgxqlParser.UPPER_NAME, 0); }
		public Entity_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entity_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterEntity_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitEntity_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitEntity_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Entity_nameContext entity_name() throws RecognitionException {
		Entity_nameContext _localctx = new Entity_nameContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_entity_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			match(UPPER_NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Entity_name_aliasContext extends ParserRuleContext {
		public TerminalNode LOWER_NAME() { return getToken(MgxqlParser.LOWER_NAME, 0); }
		public TerminalNode QUOTED_NAME() { return getToken(MgxqlParser.QUOTED_NAME, 0); }
		public Entity_name_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entity_name_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterEntity_name_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitEntity_name_alias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitEntity_name_alias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Entity_name_aliasContext entity_name_alias() throws RecognitionException {
		Entity_name_aliasContext _localctx = new Entity_name_aliasContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_entity_name_alias);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(543);
			_la = _input.LA(1);
			if ( !(_la==QUOTED_NAME || _la==LOWER_NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Field_nameContext extends ParserRuleContext {
		public TerminalNode LOWER_NAME() { return getToken(MgxqlParser.LOWER_NAME, 0); }
		public TerminalNode QUOTED_NAME() { return getToken(MgxqlParser.QUOTED_NAME, 0); }
		public Field_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterField_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitField_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitField_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_nameContext field_name() throws RecognitionException {
		Field_nameContext _localctx = new Field_nameContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_field_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(545);
			_la = _input.LA(1);
			if ( !(_la==QUOTED_NAME || _la==LOWER_NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Left_bracketContext extends ParserRuleContext {
		public TerminalNode LEFT_BRACKET() { return getToken(MgxqlParser.LEFT_BRACKET, 0); }
		public Left_bracketContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_left_bracket; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLeft_bracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLeft_bracket(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLeft_bracket(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Left_bracketContext left_bracket() throws RecognitionException {
		Left_bracketContext _localctx = new Left_bracketContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_left_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(547);
			match(LEFT_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Right_bracketContext extends ParserRuleContext {
		public TerminalNode RIGHT_BRACKET() { return getToken(MgxqlParser.RIGHT_BRACKET, 0); }
		public Right_bracketContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_right_bracket; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterRight_bracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitRight_bracket(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitRight_bracket(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Right_bracketContext right_bracket() throws RecognitionException {
		Right_bracketContext _localctx = new Right_bracketContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_right_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			match(RIGHT_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DotContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(MgxqlParser.DOT, 0); }
		public DotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterDot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitDot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitDot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DotContext dot() throws RecognitionException {
		DotContext _localctx = new DotContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_dot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(551);
			match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Param_colonContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(MgxqlParser.COLON, 0); }
		public Param_colonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_colon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterParam_colon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitParam_colon(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitParam_colon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_colonContext param_colon() throws RecognitionException {
		Param_colonContext _localctx = new Param_colonContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_param_colon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			match(COLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommaContext extends ParserRuleContext {
		public TerminalNode COMMA() { return getToken(MgxqlParser.COMMA, 0); }
		public CommaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comma; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComma(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComma(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommaContext comma() throws RecognitionException {
		CommaContext _localctx = new CommaContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_comma);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(555);
			match(COMMA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Question_markContext extends ParserRuleContext {
		public TerminalNode QUESTION_MARK() { return getToken(MgxqlParser.QUESTION_MARK, 0); }
		public Question_markContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_question_mark; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterQuestion_mark(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitQuestion_mark(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitQuestion_mark(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Question_markContext question_mark() throws RecognitionException {
		Question_markContext _localctx = new Question_markContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_question_mark);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			match(QUESTION_MARK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(MgxqlParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(559);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EndContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MgxqlParser.EOF, 0); }
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u00010\u0234\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"+
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"+
		"K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0002O\u0007O\u0002"+
		"P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007S\u0002T\u0007T\u0002"+
		"U\u0007U\u0002V\u0007V\u0002W\u0007W\u0002X\u0007X\u0002Y\u0007Y\u0002"+
		"Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007]\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u00c1\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u00db\b\b\u0001\b\u0003"+
		"\b\u00de\b\b\u0001\b\u0003\b\u00e1\b\b\u0001\b\u0003\b\u00e4\b\b\u0001"+
		"\b\u0003\b\u00e7\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t\u00ed\b\t"+
		"\n\t\f\t\u00f0\t\t\u0001\n\u0001\n\u0001\n\u0003\n\u00f5\b\n\u0001\n\u0003"+
		"\n\u00f8\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000b\u00ff\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f"+
		"\u010b\b\u000f\n\u000f\f\u000f\u010e\t\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u011c\b\u0011\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0121\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005"+
		"\u0018\u0130\b\u0018\n\u0018\f\u0018\u0133\t\u0018\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u0137\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a"+
		"\u013c\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001"+
		"!\u0001!\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0005#\u0159"+
		"\b#\n#\f#\u015c\t#\u0001$\u0001$\u0001$\u0001$\u0005$\u0162\b$\n$\f$\u0165"+
		"\t$\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u016c\b%\u0001&\u0003&\u016f"+
		"\b&\u0001&\u0001&\u0001&\u0003&\u0174\b&\u0001\'\u0001\'\u0003\'\u0178"+
		"\b\'\u0001\'\u0001\'\u0001(\u0001(\u0001)\u0001)\u0003)\u0180\b)\u0001"+
		"*\u0001*\u0001*\u0001+\u0001+\u0001+\u0001+\u0005+\u0189\b+\n+\f+\u018c"+
		"\t+\u0001,\u0001,\u0001,\u0001-\u0001-\u0001-\u0001-\u0005-\u0195\b-\n"+
		"-\f-\u0198\t-\u0001.\u0001.\u0001.\u0001.\u0005.\u019e\b.\n.\f.\u01a1"+
		"\t.\u0001/\u0001/\u0001/\u0001/\u0001/\u0003/\u01a8\b/\u00010\u00010\u0001"+
		"0\u00010\u00011\u00011\u00031\u01b0\b1\u00012\u00012\u00012\u00012\u0001"+
		"2\u00052\u01b7\b2\n2\f2\u01ba\t2\u00013\u00013\u00033\u01be\b3\u00014"+
		"\u00014\u00014\u00014\u00014\u00015\u00015\u00016\u00016\u00017\u0001"+
		"7\u00018\u00018\u00019\u00019\u0001:\u0001:\u0001;\u0001;\u0001;\u0001"+
		";\u0001;\u0001;\u0003;\u01d7\b;\u0001<\u0001<\u0001=\u0001=\u0001>\u0001"+
		">\u0001?\u0001?\u0001@\u0001@\u0001A\u0001A\u0001B\u0003B\u01e6\bB\u0001"+
		"B\u0001B\u0001B\u0001B\u0001B\u0003B\u01ed\bB\u0001C\u0001C\u0001D\u0001"+
		"D\u0001E\u0001E\u0001F\u0001F\u0001G\u0001G\u0001H\u0001H\u0001I\u0001"+
		"I\u0003I\u01fd\bI\u0001J\u0001J\u0001K\u0001K\u0001L\u0001L\u0001M\u0001"+
		"M\u0001N\u0001N\u0001O\u0001O\u0001P\u0001P\u0001P\u0001P\u0001P\u0003"+
		"P\u0210\bP\u0001Q\u0001Q\u0001Q\u0001Q\u0001Q\u0005Q\u0217\bQ\nQ\fQ\u021a"+
		"\tQ\u0001R\u0001R\u0001S\u0001S\u0001T\u0001T\u0001U\u0001U\u0001V\u0001"+
		"V\u0001W\u0001W\u0001X\u0001X\u0001Y\u0001Y\u0001Z\u0001Z\u0001[\u0001"+
		"[\u0001\\\u0001\\\u0001]\u0001]\u0001]\u0000\u0000^\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e"+
		"\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6"+
		"\u00b8\u00ba\u0000\u0001\u0001\u0000-.\u0207\u0000\u00c0\u0001\u0000\u0000"+
		"\u0000\u0002\u00c4\u0001\u0000\u0000\u0000\u0004\u00c6\u0001\u0000\u0000"+
		"\u0000\u0006\u00c8\u0001\u0000\u0000\u0000\b\u00cc\u0001\u0000\u0000\u0000"+
		"\n\u00ce\u0001\u0000\u0000\u0000\f\u00d2\u0001\u0000\u0000\u0000\u000e"+
		"\u00d4\u0001\u0000\u0000\u0000\u0010\u00d6\u0001\u0000\u0000\u0000\u0012"+
		"\u00e8\u0001\u0000\u0000\u0000\u0014\u00f4\u0001\u0000\u0000\u0000\u0016"+
		"\u00fe\u0001\u0000\u0000\u0000\u0018\u0100\u0001\u0000\u0000\u0000\u001a"+
		"\u0102\u0001\u0000\u0000\u0000\u001c\u0104\u0001\u0000\u0000\u0000\u001e"+
		"\u0106\u0001\u0000\u0000\u0000 \u0111\u0001\u0000\u0000\u0000\"\u011b"+
		"\u0001\u0000\u0000\u0000$\u0120\u0001\u0000\u0000\u0000&\u0122\u0001\u0000"+
		"\u0000\u0000(\u0124\u0001\u0000\u0000\u0000*\u0126\u0001\u0000\u0000\u0000"+
		",\u0128\u0001\u0000\u0000\u0000.\u012a\u0001\u0000\u0000\u00000\u012c"+
		"\u0001\u0000\u0000\u00002\u0134\u0001\u0000\u0000\u00004\u0138\u0001\u0000"+
		"\u0000\u00006\u0140\u0001\u0000\u0000\u00008\u0142\u0001\u0000\u0000\u0000"+
		":\u0144\u0001\u0000\u0000\u0000<\u0146\u0001\u0000\u0000\u0000>\u0149"+
		"\u0001\u0000\u0000\u0000@\u014b\u0001\u0000\u0000\u0000B\u014f\u0001\u0000"+
		"\u0000\u0000D\u0151\u0001\u0000\u0000\u0000F\u0154\u0001\u0000\u0000\u0000"+
		"H\u015d\u0001\u0000\u0000\u0000J\u016b\u0001\u0000\u0000\u0000L\u016e"+
		"\u0001\u0000\u0000\u0000N\u0177\u0001\u0000\u0000\u0000P\u017b\u0001\u0000"+
		"\u0000\u0000R\u017f\u0001\u0000\u0000\u0000T\u0181\u0001\u0000\u0000\u0000"+
		"V\u0184\u0001\u0000\u0000\u0000X\u018d\u0001\u0000\u0000\u0000Z\u0190"+
		"\u0001\u0000\u0000\u0000\\\u0199\u0001\u0000\u0000\u0000^\u01a7\u0001"+
		"\u0000\u0000\u0000`\u01a9\u0001\u0000\u0000\u0000b\u01af\u0001\u0000\u0000"+
		"\u0000d\u01b1\u0001\u0000\u0000\u0000f\u01bb\u0001\u0000\u0000\u0000h"+
		"\u01bf\u0001\u0000\u0000\u0000j\u01c4\u0001\u0000\u0000\u0000l\u01c6\u0001"+
		"\u0000\u0000\u0000n\u01c8\u0001\u0000\u0000\u0000p\u01ca\u0001\u0000\u0000"+
		"\u0000r\u01cc\u0001\u0000\u0000\u0000t\u01ce\u0001\u0000\u0000\u0000v"+
		"\u01d6\u0001\u0000\u0000\u0000x\u01d8\u0001\u0000\u0000\u0000z\u01da\u0001"+
		"\u0000\u0000\u0000|\u01dc\u0001\u0000\u0000\u0000~\u01de\u0001\u0000\u0000"+
		"\u0000\u0080\u01e0\u0001\u0000\u0000\u0000\u0082\u01e2\u0001\u0000\u0000"+
		"\u0000\u0084\u01e5\u0001\u0000\u0000\u0000\u0086\u01ee\u0001\u0000\u0000"+
		"\u0000\u0088\u01f0\u0001\u0000\u0000\u0000\u008a\u01f2\u0001\u0000\u0000"+
		"\u0000\u008c\u01f4\u0001\u0000\u0000\u0000\u008e\u01f6\u0001\u0000\u0000"+
		"\u0000\u0090\u01f8\u0001\u0000\u0000\u0000\u0092\u01fc\u0001\u0000\u0000"+
		"\u0000\u0094\u01fe\u0001\u0000\u0000\u0000\u0096\u0200\u0001\u0000\u0000"+
		"\u0000\u0098\u0202\u0001\u0000\u0000\u0000\u009a\u0204\u0001\u0000\u0000"+
		"\u0000\u009c\u0206\u0001\u0000\u0000\u0000\u009e\u0208\u0001\u0000\u0000"+
		"\u0000\u00a0\u020f\u0001\u0000\u0000\u0000\u00a2\u0211\u0001\u0000\u0000"+
		"\u0000\u00a4\u021b\u0001\u0000\u0000\u0000\u00a6\u021d\u0001\u0000\u0000"+
		"\u0000\u00a8\u021f\u0001\u0000\u0000\u0000\u00aa\u0221\u0001\u0000\u0000"+
		"\u0000\u00ac\u0223\u0001\u0000\u0000\u0000\u00ae\u0225\u0001\u0000\u0000"+
		"\u0000\u00b0\u0227\u0001\u0000\u0000\u0000\u00b2\u0229\u0001\u0000\u0000"+
		"\u0000\u00b4\u022b\u0001\u0000\u0000\u0000\u00b6\u022d\u0001\u0000\u0000"+
		"\u0000\u00b8\u022f\u0001\u0000\u0000\u0000\u00ba\u0231\u0001\u0000\u0000"+
		"\u0000\u00bc\u00c1\u0003\u0002\u0001\u0000\u00bd\u00c1\u0003\u0006\u0003"+
		"\u0000\u00be\u00c1\u0003\n\u0005\u0000\u00bf\u00c1\u0003\u0010\b\u0000"+
		"\u00c0\u00bc\u0001\u0000\u0000\u0000\u00c0\u00bd\u0001\u0000\u0000\u0000"+
		"\u00c0\u00be\u0001\u0000\u0000\u0000\u00c0\u00bf\u0001\u0000\u0000\u0000"+
		"\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0003\u00ba]\u0000\u00c3"+
		"\u0001\u0001\u0000\u0000\u0000\u00c4\u00c5\u0003\u0004\u0002\u0000\u00c5"+
		"\u0003\u0001\u0000\u0000\u0000\u00c6\u00c7\u0005\u0001\u0000\u0000\u00c7"+
		"\u0005\u0001\u0000\u0000\u0000\u00c8\u00c9\u0003\b\u0004\u0000\u00c9\u00ca"+
		"\u0003\u000e\u0007\u0000\u00ca\u00cb\u0003D\"\u0000\u00cb\u0007\u0001"+
		"\u0000\u0000\u0000\u00cc\u00cd\u0005\u0002\u0000\u0000\u00cd\t\u0001\u0000"+
		"\u0000\u0000\u00ce\u00cf\u0003\f\u0006\u0000\u00cf\u00d0\u0003\u000e\u0007"+
		"\u0000\u00d0\u00d1\u0003D\"\u0000\u00d1\u000b\u0001\u0000\u0000\u0000"+
		"\u00d2\u00d3\u0005\u0003\u0000\u0000\u00d3\r\u0001\u0000\u0000\u0000\u00d4"+
		"\u00d5\u0003\u00a6S\u0000\u00d5\u000f\u0001\u0000\u0000\u0000\u00d6\u00d7"+
		"\u0003\u001a\r\u0000\u00d7\u00d8\u0003\u0012\t\u0000\u00d8\u00da\u0003"+
		"0\u0018\u0000\u00d9\u00db\u0003D\"\u0000\u00da\u00d9\u0001\u0000\u0000"+
		"\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00dd\u0001\u0000\u0000"+
		"\u0000\u00dc\u00de\u0003T*\u0000\u00dd\u00dc\u0001\u0000\u0000\u0000\u00dd"+
		"\u00de\u0001\u0000\u0000\u0000\u00de\u00e0\u0001\u0000\u0000\u0000\u00df"+
		"\u00e1\u0003X,\u0000\u00e0\u00df\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e3\u0001\u0000\u0000\u0000\u00e2\u00e4\u0003"+
		"d2\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000"+
		"\u0000\u00e4\u00e6\u0001\u0000\u0000\u0000\u00e5\u00e7\u0003h4\u0000\u00e6"+
		"\u00e5\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7"+
		"\u0011\u0001\u0000\u0000\u0000\u00e8\u00ee\u0003\u0014\n\u0000\u00e9\u00ea"+
		"\u0003\u00b4Z\u0000\u00ea\u00eb\u0003\u0014\n\u0000\u00eb\u00ed\u0001"+
		"\u0000\u0000\u0000\u00ec\u00e9\u0001\u0000\u0000\u0000\u00ed\u00f0\u0001"+
		"\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000\u0000\u0000\u00ee\u00ef\u0001"+
		"\u0000\u0000\u0000\u00ef\u0013\u0001\u0000\u0000\u0000\u00f0\u00ee\u0001"+
		"\u0000\u0000\u0000\u00f1\u00f5\u0003\u0016\u000b\u0000\u00f2\u00f5\u0003"+
		"\u0018\f\u0000\u00f3\u00f5\u0003 \u0010\u0000\u00f4\u00f1\u0001\u0000"+
		"\u0000\u0000\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f4\u00f3\u0001\u0000"+
		"\u0000\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6\u00f8\u0003\u001e"+
		"\u000f\u0000\u00f7\u00f6\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001\u0000"+
		"\u0000\u0000\u00f8\u0015\u0001\u0000\u0000\u0000\u00f9\u00ff\u0003\u001c"+
		"\u000e\u0000\u00fa\u00fb\u0003\u00a8T\u0000\u00fb\u00fc\u0003\u00b0X\u0000"+
		"\u00fc\u00fd\u0003\u001c\u000e\u0000\u00fd\u00ff\u0001\u0000\u0000\u0000"+
		"\u00fe\u00f9\u0001\u0000\u0000\u0000\u00fe\u00fa\u0001\u0000\u0000\u0000"+
		"\u00ff\u0017\u0001\u0000\u0000\u0000\u0100\u0101\u0003\u00a0P\u0000\u0101"+
		"\u0019\u0001\u0000\u0000\u0000\u0102\u0103\u0005\u0004\u0000\u0000\u0103"+
		"\u001b\u0001\u0000\u0000\u0000\u0104\u0105\u0005\u0005\u0000\u0000\u0105"+
		"\u001d\u0001\u0000\u0000\u0000\u0106\u010c\u0003\u00a4R\u0000\u0107\u0108"+
		"\u0003\u00a8T\u0000\u0108\u0109\u0003\u00b0X\u0000\u0109\u010b\u0001\u0000"+
		"\u0000\u0000\u010a\u0107\u0001\u0000\u0000\u0000\u010b\u010e\u0001\u0000"+
		"\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000"+
		"\u0000\u0000\u010d\u010f\u0001\u0000\u0000\u0000\u010e\u010c\u0001\u0000"+
		"\u0000\u0000\u010f\u0110\u0003\u00aaU\u0000\u0110\u001f\u0001\u0000\u0000"+
		"\u0000\u0111\u0112\u0003\"\u0011\u0000\u0112\u0113\u0003\u00acV\u0000"+
		"\u0113\u0114\u0003$\u0012\u0000\u0114\u0115\u0003\u00aeW\u0000\u0115!"+
		"\u0001\u0000\u0000\u0000\u0116\u011c\u0003&\u0013\u0000\u0117\u011c\u0003"+
		"(\u0014\u0000\u0118\u011c\u0003*\u0015\u0000\u0119\u011c\u0003,\u0016"+
		"\u0000\u011a\u011c\u0003.\u0017\u0000\u011b\u0116\u0001\u0000\u0000\u0000"+
		"\u011b\u0117\u0001\u0000\u0000\u0000\u011b\u0118\u0001\u0000\u0000\u0000"+
		"\u011b\u0119\u0001\u0000\u0000\u0000\u011b\u011a\u0001\u0000\u0000\u0000"+
		"\u011c#\u0001\u0000\u0000\u0000\u011d\u0121\u0003\u00a0P\u0000\u011e\u0121"+
		"\u0003\u00b8\\\u0000\u011f\u0121\u0003\u001c\u000e\u0000\u0120\u011d\u0001"+
		"\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000\u0000\u0120\u011f\u0001"+
		"\u0000\u0000\u0000\u0121%\u0001\u0000\u0000\u0000\u0122\u0123\u0005\u0007"+
		"\u0000\u0000\u0123\'\u0001\u0000\u0000\u0000\u0124\u0125\u0005\b\u0000"+
		"\u0000\u0125)\u0001\u0000\u0000\u0000\u0126\u0127\u0005\t\u0000\u0000"+
		"\u0127+\u0001\u0000\u0000\u0000\u0128\u0129\u0005\n\u0000\u0000\u0129"+
		"-\u0001\u0000\u0000\u0000\u012a\u012b\u0005\u0006\u0000\u0000\u012b/\u0001"+
		"\u0000\u0000\u0000\u012c\u012d\u0003:\u001d\u0000\u012d\u0131\u00032\u0019"+
		"\u0000\u012e\u0130\u00034\u001a\u0000\u012f\u012e\u0001\u0000\u0000\u0000"+
		"\u0130\u0133\u0001\u0000\u0000\u0000\u0131\u012f\u0001\u0000\u0000\u0000"+
		"\u0131\u0132\u0001\u0000\u0000\u0000\u01321\u0001\u0000\u0000\u0000\u0133"+
		"\u0131\u0001\u0000\u0000\u0000\u0134\u0136\u00036\u001b\u0000\u0135\u0137"+
		"\u00038\u001c\u0000\u0136\u0135\u0001\u0000\u0000\u0000\u0136\u0137\u0001"+
		"\u0000\u0000\u0000\u01373\u0001\u0000\u0000\u0000\u0138\u0139\u0003<\u001e"+
		"\u0000\u0139\u013b\u00036\u001b\u0000\u013a\u013c\u00038\u001c\u0000\u013b"+
		"\u013a\u0001\u0000\u0000\u0000\u013b\u013c\u0001\u0000\u0000\u0000\u013c"+
		"\u013d\u0001\u0000\u0000\u0000\u013d\u013e\u0003>\u001f\u0000\u013e\u013f"+
		"\u0003@ \u0000\u013f5\u0001\u0000\u0000\u0000\u0140\u0141\u0003\u00a6"+
		"S\u0000\u01417\u0001\u0000\u0000\u0000\u0142\u0143\u0003\u00a8T\u0000"+
		"\u01439\u0001\u0000\u0000\u0000\u0144\u0145\u0005\f\u0000\u0000\u0145"+
		";\u0001\u0000\u0000\u0000\u0146\u0147\u0005\r\u0000\u0000\u0147\u0148"+
		"\u0005\u000e\u0000\u0000\u0148=\u0001\u0000\u0000\u0000\u0149\u014a\u0005"+
		"\u000f\u0000\u0000\u014a?\u0001\u0000\u0000\u0000\u014b\u014c\u0003\u00a8"+
		"T\u0000\u014c\u014d\u0003B!\u0000\u014d\u014e\u0003\u00a8T\u0000\u014e"+
		"A\u0001\u0000\u0000\u0000\u014f\u0150\u0005\u0017\u0000\u0000\u0150C\u0001"+
		"\u0000\u0000\u0000\u0151\u0152\u0003p8\u0000\u0152\u0153\u0003F#\u0000"+
		"\u0153E\u0001\u0000\u0000\u0000\u0154\u015a\u0003H$\u0000\u0155\u0156"+
		"\u0003t:\u0000\u0156\u0157\u0003H$\u0000\u0157\u0159\u0001\u0000\u0000"+
		"\u0000\u0158\u0155\u0001\u0000\u0000\u0000\u0159\u015c\u0001\u0000\u0000"+
		"\u0000\u015a\u0158\u0001\u0000\u0000\u0000\u015a\u015b\u0001\u0000\u0000"+
		"\u0000\u015bG\u0001\u0000\u0000\u0000\u015c\u015a\u0001\u0000\u0000\u0000"+
		"\u015d\u0163\u0003J%\u0000\u015e\u015f\u0003r9\u0000\u015f\u0160\u0003"+
		"J%\u0000\u0160\u0162\u0001\u0000\u0000\u0000\u0161\u015e\u0001\u0000\u0000"+
		"\u0000\u0162\u0165\u0001\u0000\u0000\u0000\u0163\u0161\u0001\u0000\u0000"+
		"\u0000\u0163\u0164\u0001\u0000\u0000\u0000\u0164I\u0001\u0000\u0000\u0000"+
		"\u0165\u0163\u0001\u0000\u0000\u0000\u0166\u016c\u0003L&\u0000\u0167\u0168"+
		"\u0003\u00acV\u0000\u0168\u0169\u0003F#\u0000\u0169\u016a\u0003\u00ae"+
		"W\u0000\u016a\u016c\u0001\u0000\u0000\u0000\u016b\u0166\u0001\u0000\u0000"+
		"\u0000\u016b\u0167\u0001\u0000\u0000\u0000\u016cK\u0001\u0000\u0000\u0000"+
		"\u016d\u016f\u0003\u00b6[\u0000\u016e\u016d\u0001\u0000\u0000\u0000\u016e"+
		"\u016f\u0001\u0000\u0000\u0000\u016f\u0170\u0001\u0000\u0000\u0000\u0170"+
		"\u0173\u0003\u00a0P\u0000\u0171\u0174\u0003N\'\u0000\u0172\u0174\u0003"+
		"P(\u0000\u0173\u0171\u0001\u0000\u0000\u0000\u0173\u0172\u0001\u0000\u0000"+
		"\u0000\u0174M\u0001\u0000\u0000\u0000\u0175\u0178\u0003v;\u0000\u0176"+
		"\u0178\u0003\u0084B\u0000\u0177\u0175\u0001\u0000\u0000\u0000\u0177\u0176"+
		"\u0001\u0000\u0000\u0000\u0178\u0179\u0001\u0000\u0000\u0000\u0179\u017a"+
		"\u0003R)\u0000\u017aO\u0001\u0000\u0000\u0000\u017b\u017c\u0003\u0092"+
		"I\u0000\u017cQ\u0001\u0000\u0000\u0000\u017d\u0180\u0003\u00a2Q\u0000"+
		"\u017e\u0180\u0003\u00b8\\\u0000\u017f\u017d\u0001\u0000\u0000\u0000\u017f"+
		"\u017e\u0001\u0000\u0000\u0000\u0180S\u0001\u0000\u0000\u0000\u0181\u0182"+
		"\u0003\u009aM\u0000\u0182\u0183\u0003V+\u0000\u0183U\u0001\u0000\u0000"+
		"\u0000\u0184\u018a\u0003\u00a0P\u0000\u0185\u0186\u0003\u00b4Z\u0000\u0186"+
		"\u0187\u0003\u00a0P\u0000\u0187\u0189\u0001\u0000\u0000\u0000\u0188\u0185"+
		"\u0001\u0000\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000\u018a\u0188"+
		"\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000\u018bW\u0001"+
		"\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018d\u018e\u0003"+
		"\u0098L\u0000\u018e\u018f\u0003Z-\u0000\u018fY\u0001\u0000\u0000\u0000"+
		"\u0190\u0196\u0003\\.\u0000\u0191\u0192\u0003t:\u0000\u0192\u0193\u0003"+
		"\\.\u0000\u0193\u0195\u0001\u0000\u0000\u0000\u0194\u0191\u0001\u0000"+
		"\u0000\u0000\u0195\u0198\u0001\u0000\u0000\u0000\u0196\u0194\u0001\u0000"+
		"\u0000\u0000\u0196\u0197\u0001\u0000\u0000\u0000\u0197[\u0001\u0000\u0000"+
		"\u0000\u0198\u0196\u0001\u0000\u0000\u0000\u0199\u019f\u0003^/\u0000\u019a"+
		"\u019b\u0003r9\u0000\u019b\u019c\u0003^/\u0000\u019c\u019e\u0001\u0000"+
		"\u0000\u0000\u019d\u019a\u0001\u0000\u0000\u0000\u019e\u01a1\u0001\u0000"+
		"\u0000\u0000\u019f\u019d\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000"+
		"\u0000\u0000\u01a0]\u0001\u0000\u0000\u0000\u01a1\u019f\u0001\u0000\u0000"+
		"\u0000\u01a2\u01a8\u0003`0\u0000\u01a3\u01a4\u0003\u00acV\u0000\u01a4"+
		"\u01a5\u0003Z-\u0000\u01a5\u01a6\u0003\u00aeW\u0000\u01a6\u01a8\u0001"+
		"\u0000\u0000\u0000\u01a7\u01a2\u0001\u0000\u0000\u0000\u01a7\u01a3\u0001"+
		"\u0000\u0000\u0000\u01a8_\u0001\u0000\u0000\u0000\u01a9\u01aa\u0003 \u0010"+
		"\u0000\u01aa\u01ab\u0003v;\u0000\u01ab\u01ac\u0003b1\u0000\u01aca\u0001"+
		"\u0000\u0000\u0000\u01ad\u01b0\u0003\u00a2Q\u0000\u01ae\u01b0\u0003\u00b8"+
		"\\\u0000\u01af\u01ad\u0001\u0000\u0000\u0000\u01af\u01ae\u0001\u0000\u0000"+
		"\u0000\u01b0c\u0001\u0000\u0000\u0000\u01b1\u01b2\u0003\u009cN\u0000\u01b2"+
		"\u01b8\u0003f3\u0000\u01b3\u01b4\u0003\u00b4Z\u0000\u01b4\u01b5\u0003"+
		"f3\u0000\u01b5\u01b7\u0001\u0000\u0000\u0000\u01b6\u01b3\u0001\u0000\u0000"+
		"\u0000\u01b7\u01ba\u0001\u0000\u0000\u0000\u01b8\u01b6\u0001\u0000\u0000"+
		"\u0000\u01b8\u01b9\u0001\u0000\u0000\u0000\u01b9e\u0001\u0000\u0000\u0000"+
		"\u01ba\u01b8\u0001\u0000\u0000\u0000\u01bb\u01bd\u0003\u00a0P\u0000\u01bc"+
		"\u01be\u0003\u009eO\u0000\u01bd\u01bc\u0001\u0000\u0000\u0000\u01bd\u01be"+
		"\u0001\u0000\u0000\u0000\u01beg\u0001\u0000\u0000\u0000\u01bf\u01c0\u0003"+
		"j5\u0000\u01c0\u01c1\u0003l6\u0000\u01c1\u01c2\u0003\u00b4Z\u0000\u01c2"+
		"\u01c3\u0003n7\u0000\u01c3i\u0001\u0000\u0000\u0000\u01c4\u01c5\u0005"+
		"%\u0000\u0000\u01c5k\u0001\u0000\u0000\u0000\u01c6\u01c7\u0005/\u0000"+
		"\u0000\u01c7m\u0001\u0000\u0000\u0000\u01c8\u01c9\u0005/\u0000\u0000\u01c9"+
		"o\u0001\u0000\u0000\u0000\u01ca\u01cb\u0005\u0010\u0000\u0000\u01cbq\u0001"+
		"\u0000\u0000\u0000\u01cc\u01cd\u0005\u0011\u0000\u0000\u01cds\u0001\u0000"+
		"\u0000\u0000\u01ce\u01cf\u0005\u0012\u0000\u0000\u01cfu\u0001\u0000\u0000"+
		"\u0000\u01d0\u01d7\u0003x<\u0000\u01d1\u01d7\u0003z=\u0000\u01d2\u01d7"+
		"\u0003|>\u0000\u01d3\u01d7\u0003~?\u0000\u01d4\u01d7\u0003\u0080@\u0000"+
		"\u01d5\u01d7\u0003\u0082A\u0000\u01d6\u01d0\u0001\u0000\u0000\u0000\u01d6"+
		"\u01d1\u0001\u0000\u0000\u0000\u01d6\u01d2\u0001\u0000\u0000\u0000\u01d6"+
		"\u01d3\u0001\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000\u0000\u01d6"+
		"\u01d5\u0001\u0000\u0000\u0000\u01d7w\u0001\u0000\u0000\u0000\u01d8\u01d9"+
		"\u0005\u0013\u0000\u0000\u01d9y\u0001\u0000\u0000\u0000\u01da\u01db\u0005"+
		"\u0014\u0000\u0000\u01db{\u0001\u0000\u0000\u0000\u01dc\u01dd\u0005\u0015"+
		"\u0000\u0000\u01dd}\u0001\u0000\u0000\u0000\u01de\u01df\u0005\u0016\u0000"+
		"\u0000\u01df\u007f\u0001\u0000\u0000\u0000\u01e0\u01e1\u0005\u0017\u0000"+
		"\u0000\u01e1\u0081\u0001\u0000\u0000\u0000\u01e2\u01e3\u0005\u0018\u0000"+
		"\u0000\u01e3\u0083\u0001\u0000\u0000\u0000\u01e4\u01e6\u0003\u0086C\u0000"+
		"\u01e5\u01e4\u0001\u0000\u0000\u0000\u01e5\u01e6\u0001\u0000\u0000\u0000"+
		"\u01e6\u01ec\u0001\u0000\u0000\u0000\u01e7\u01ed\u0003\u0088D\u0000\u01e8"+
		"\u01ed\u0003\u008aE\u0000\u01e9\u01ed\u0003\u008cF\u0000\u01ea\u01ed\u0003"+
		"\u008eG\u0000\u01eb\u01ed\u0003\u0090H\u0000\u01ec\u01e7\u0001\u0000\u0000"+
		"\u0000\u01ec\u01e8\u0001\u0000\u0000\u0000\u01ec\u01e9\u0001\u0000\u0000"+
		"\u0000\u01ec\u01ea\u0001\u0000\u0000\u0000\u01ec\u01eb\u0001\u0000\u0000"+
		"\u0000\u01ed\u0085\u0001\u0000\u0000\u0000\u01ee\u01ef\u0005\u0019\u0000"+
		"\u0000\u01ef\u0087\u0001\u0000\u0000\u0000\u01f0\u01f1\u0005\u001a\u0000"+
		"\u0000\u01f1\u0089\u0001\u0000\u0000\u0000\u01f2\u01f3\u0005\u001b\u0000"+
		"\u0000\u01f3\u008b\u0001\u0000\u0000\u0000\u01f4\u01f5\u0005\u001c\u0000"+
		"\u0000\u01f5\u008d\u0001\u0000\u0000\u0000\u01f6\u01f7\u0005\u001d\u0000"+
		"\u0000\u01f7\u008f\u0001\u0000\u0000\u0000\u01f8\u01f9\u0005\u001e\u0000"+
		"\u0000\u01f9\u0091\u0001\u0000\u0000\u0000\u01fa\u01fd\u0003\u0094J\u0000"+
		"\u01fb\u01fd\u0003\u0096K\u0000\u01fc\u01fa\u0001\u0000\u0000\u0000\u01fc"+
		"\u01fb\u0001\u0000\u0000\u0000\u01fd\u0093\u0001\u0000\u0000\u0000\u01fe"+
		"\u01ff\u0005\u001f\u0000\u0000\u01ff\u0095\u0001\u0000\u0000\u0000\u0200"+
		"\u0201\u0005 \u0000\u0000\u0201\u0097\u0001\u0000\u0000\u0000\u0202\u0203"+
		"\u0005\"\u0000\u0000\u0203\u0099\u0001\u0000\u0000\u0000\u0204\u0205\u0005"+
		"!\u0000\u0000\u0205\u009b\u0001\u0000\u0000\u0000\u0206\u0207\u0005#\u0000"+
		"\u0000\u0207\u009d\u0001\u0000\u0000\u0000\u0208\u0209\u0005$\u0000\u0000"+
		"\u0209\u009f\u0001\u0000\u0000\u0000\u020a\u0210\u0003\u00aaU\u0000\u020b"+
		"\u020c\u0003\u00a8T\u0000\u020c\u020d\u0003\u00b0X\u0000\u020d\u020e\u0003"+
		"\u00aaU\u0000\u020e\u0210\u0001\u0000\u0000\u0000\u020f\u020a\u0001\u0000"+
		"\u0000\u0000\u020f\u020b\u0001\u0000\u0000\u0000\u0210\u00a1\u0001\u0000"+
		"\u0000\u0000\u0211\u0212\u0003\u00b2Y\u0000\u0212\u0218\u0003\u00aaU\u0000"+
		"\u0213\u0214\u0003\u00b0X\u0000\u0214\u0215\u0003\u00aaU\u0000\u0215\u0217"+
		"\u0001\u0000\u0000\u0000\u0216\u0213\u0001\u0000\u0000\u0000\u0217\u021a"+
		"\u0001\u0000\u0000\u0000\u0218\u0216\u0001\u0000\u0000\u0000\u0218\u0219"+
		"\u0001\u0000\u0000\u0000\u0219\u00a3\u0001\u0000\u0000\u0000\u021a\u0218"+
		"\u0001\u0000\u0000\u0000\u021b\u021c\u0005\u000b\u0000\u0000\u021c\u00a5"+
		"\u0001\u0000\u0000\u0000\u021d\u021e\u0005,\u0000\u0000\u021e\u00a7\u0001"+
		"\u0000\u0000\u0000\u021f\u0220\u0007\u0000\u0000\u0000\u0220\u00a9\u0001"+
		"\u0000\u0000\u0000\u0221\u0222\u0007\u0000\u0000\u0000\u0222\u00ab\u0001"+
		"\u0000\u0000\u0000\u0223\u0224\u0005&\u0000\u0000\u0224\u00ad\u0001\u0000"+
		"\u0000\u0000\u0225\u0226\u0005\'\u0000\u0000\u0226\u00af\u0001\u0000\u0000"+
		"\u0000\u0227\u0228\u0005*\u0000\u0000\u0228\u00b1\u0001\u0000\u0000\u0000"+
		"\u0229\u022a\u0005)\u0000\u0000\u022a\u00b3\u0001\u0000\u0000\u0000\u022b"+
		"\u022c\u0005(\u0000\u0000\u022c\u00b5\u0001\u0000\u0000\u0000\u022d\u022e"+
		"\u0005+\u0000\u0000\u022e\u00b7\u0001\u0000\u0000\u0000\u022f\u0230\u0005"+
		"/\u0000\u0000\u0230\u00b9\u0001\u0000\u0000\u0000\u0231\u0232\u0005\u0000"+
		"\u0000\u0001\u0232\u00bb\u0001\u0000\u0000\u0000$\u00c0\u00da\u00dd\u00e0"+
		"\u00e3\u00e6\u00ee\u00f4\u00f7\u00fe\u010c\u011b\u0120\u0131\u0136\u013b"+
		"\u015a\u0163\u016b\u016e\u0173\u0177\u017f\u018a\u0196\u019f\u01a7\u01af"+
		"\u01b8\u01bd\u01d6\u01e5\u01ec\u01fc\u020f\u0218";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}