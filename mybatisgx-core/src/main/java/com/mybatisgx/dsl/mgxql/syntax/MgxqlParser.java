// Generated from F:/owner_project/mybatisgx-ai_coding/mybatisgx/mybatisgx-core/src/main/resources/antlr/mgxql/MgxqlParser.g4 by ANTLR 4.13.2
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
		FROM=11, LEFT=12, JOIN=13, ON=14, WHERE=15, LOGIC_AND=16, LOGIC_OR=17, 
		COMPARISON_OP_LT=18, COMPARISON_OP_LT_EQ=19, COMPARISON_OP_GT=20, COMPARISON_OP_GT_EQ=21, 
		EQUAL=22, COMPARISON_OP_NOT_EQ=23, EQ_EQ=24, AND_AND=25, OR_OR=26, STRING_LITERAL=27, 
		COMPARISON_OP_NOT=28, COMPARISON_OP_BETWEEN=29, COMPARISON_OP_IN=30, COMPARISON_OP_LIKE=31, 
		COMPARISON_OP_IS_NULL=32, COMPARISON_OP_IS_NOT_NULL=33, GROUP_BY=34, HAVING=35, 
		ORDER_BY=36, ORDER_BY_DIRECTION=37, LIMIT=38, LEFT_BRACKET=39, RIGHT_BRACKET=40, 
		COMMA=41, COLON=42, DOT=43, QUESTION_MARK=44, HASH=45, LEFT_SQUARE=46, 
		RIGHT_SQUARE=47, ARROW=48, DOLLAR=49, PERCENT=50, IF=51, WHEN=52, OTHERWISE=53, 
		CHOOSE=54, UPPER_NAME=55, QUOTED_NAME=56, LOWER_NAME=57, NUMBER=58, WS=59;
	public static final int
		RULE_sql_statement = 0, RULE_insert_statement = 1, RULE_insert_clause = 2, 
		RULE_delete_statement = 3, RULE_delete_clause = 4, RULE_update_statement = 5, 
		RULE_update_clause = 6, RULE_modify_entity = 7, RULE_select_statement = 8, 
		RULE_select_item_clause = 9, RULE_select_item = 10, RULE_select_column_all = 11, 
		RULE_select_column_custom = 12, RULE_select_action = 13, RULE_select_asterisk = 14, 
		RULE_aggregate_function = 15, RULE_aggregate_function_name = 16, RULE_aggregate_function_argument = 17, 
		RULE_select_max = 18, RULE_select_min = 19, RULE_select_avg = 20, RULE_select_sum = 21, 
		RULE_select_count = 22, RULE_select_from_clause = 23, RULE_select_primary_entity = 24, 
		RULE_select_join_entity = 25, RULE_select_entity = 26, RULE_select_entity_alias = 27, 
		RULE_select_from = 28, RULE_select_left_join = 29, RULE_select_on = 30, 
		RULE_select_on_expression = 31, RULE_on_equal = 32, RULE_group_by_clause = 33, 
		RULE_group_by_expression = 34, RULE_having_clause = 35, RULE_having_or_expression = 36, 
		RULE_having_and_expression = 37, RULE_having_term = 38, RULE_having_comparison = 39, 
		RULE_having_value = 40, RULE_order_by_clause = 41, RULE_order_by_expression = 42, 
		RULE_limit_clause = 43, RULE_limit = 44, RULE_offset = 45, RULE_size = 46, 
		RULE_having = 47, RULE_group_by = 48, RULE_order_by = 49, RULE_order_by_direction = 50, 
		RULE_end = 51, RULE_where_clause = 52, RULE_where_sequence = 53, RULE_where_item = 54, 
		RULE_where_atom = 55, RULE_bracket_group = 56, RULE_if_directive = 57, 
		RULE_bracket_directive = 58, RULE_choose_directive = 59, RULE_when_directive = 60, 
		RULE_otherwise_directive = 61, RULE_block_prefix = 62, RULE_body_sequence = 63, 
		RULE_body_item = 64, RULE_body_atom = 65, RULE_guard_or_expression = 66, 
		RULE_guard_and_expression = 67, RULE_guard_term = 68, RULE_guard_comparison = 69, 
		RULE_guard_logic_or = 70, RULE_guard_logic_and = 71, RULE_guard_relational_op = 72, 
		RULE_guard_null_op = 73, RULE_guard_operand = 74, RULE_condition_comparison = 75, 
		RULE_condition_comparison_param = 76, RULE_condition_comparison_not_param = 77, 
		RULE_condition_value = 78, RULE_where_start = 79, RULE_logic_and = 80, 
		RULE_logic_or = 81, RULE_left_square = 82, RULE_right_square = 83, RULE_relational_op = 84, 
		RULE_comparison_op_lt = 85, RULE_comparison_op_lt_eq = 86, RULE_comparison_op_gt = 87, 
		RULE_comparison_op_gt_eq = 88, RULE_comparison_op_eq = 89, RULE_comparison_op_not_eq = 90, 
		RULE_matching_op = 91, RULE_comparison_op_not = 92, RULE_comparison_op_between = 93, 
		RULE_comparison_op_in = 94, RULE_comparison_op_like = 95, RULE_matching_value = 96, 
		RULE_like_pattern = 97, RULE_in_collection = 98, RULE_simple_collection = 99, 
		RULE_complex_collection = 100, RULE_collection_path = 101, RULE_value_expr_list = 102, 
		RULE_item_name = 103, RULE_comparison_op_null = 104, RULE_comparison_op_is_null = 105, 
		RULE_comparison_op_is_not_null = 106, RULE_field_reference = 107, RULE_parameter_reference = 108, 
		RULE_entity_name = 109, RULE_entity_name_alias = 110, RULE_field_name = 111, 
		RULE_left_bracket = 112, RULE_right_bracket = 113, RULE_dot = 114, RULE_param_colon = 115, 
		RULE_comma = 116, RULE_question_mark = 117, RULE_number = 118;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "modify_entity", 
			"select_statement", "select_item_clause", "select_item", "select_column_all", 
			"select_column_custom", "select_action", "select_asterisk", "aggregate_function", 
			"aggregate_function_name", "aggregate_function_argument", "select_max", 
			"select_min", "select_avg", "select_sum", "select_count", "select_from_clause", 
			"select_primary_entity", "select_join_entity", "select_entity", "select_entity_alias", 
			"select_from", "select_left_join", "select_on", "select_on_expression", 
			"on_equal", "group_by_clause", "group_by_expression", "having_clause", 
			"having_or_expression", "having_and_expression", "having_term", "having_comparison", 
			"having_value", "order_by_clause", "order_by_expression", "limit_clause", 
			"limit", "offset", "size", "having", "group_by", "order_by", "order_by_direction", 
			"end", "where_clause", "where_sequence", "where_item", "where_atom", 
			"bracket_group", "if_directive", "bracket_directive", "choose_directive", 
			"when_directive", "otherwise_directive", "block_prefix", "body_sequence", 
			"body_item", "body_atom", "guard_or_expression", "guard_and_expression", 
			"guard_term", "guard_comparison", "guard_logic_or", "guard_logic_and", 
			"guard_relational_op", "guard_null_op", "guard_operand", "condition_comparison", 
			"condition_comparison_param", "condition_comparison_not_param", "condition_value", 
			"where_start", "logic_and", "logic_or", "left_square", "right_square", 
			"relational_op", "comparison_op_lt", "comparison_op_lt_eq", "comparison_op_gt", 
			"comparison_op_gt_eq", "comparison_op_eq", "comparison_op_not_eq", "matching_op", 
			"comparison_op_not", "comparison_op_between", "comparison_op_in", "comparison_op_like", 
			"matching_value", "like_pattern", "in_collection", "simple_collection", 
			"complex_collection", "collection_path", "value_expr_list", "item_name", 
			"comparison_op_null", "comparison_op_is_null", "comparison_op_is_not_null", 
			"field_reference", "parameter_reference", "entity_name", "entity_name_alias", 
			"field_name", "left_bracket", "right_bracket", "dot", "param_colon", 
			"comma", "question_mark", "number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'insert'", "'delete'", "'update'", "'select'", "'*'", "'count'", 
			"'max'", "'min'", "'avg'", "'sum'", "'from'", "'left'", "'join'", "'on'", 
			"'where'", "'and'", "'or'", "'<'", "'<='", "'>'", "'>='", "'='", "'!='", 
			"'=='", "'&&'", "'||'", null, "'not'", "'between'", "'in'", "'like'", 
			"'is null'", "'is not null'", "'group by'", "'having'", "'order by'", 
			null, "'limit'", "'('", "')'", "','", "':'", "'.'", "'?'", "'#'", "'['", 
			"']'", "'=>'", "'$'", "'%'", "'if'", "'when'", "'otherwise'", "'choose'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"SELECT_ASTERISK", "SELECT_COUNT", "SELECT_MAX", "SELECT_MIN", "SELECT_AVG", 
			"SELECT_SUM", "FROM", "LEFT", "JOIN", "ON", "WHERE", "LOGIC_AND", "LOGIC_OR", 
			"COMPARISON_OP_LT", "COMPARISON_OP_LT_EQ", "COMPARISON_OP_GT", "COMPARISON_OP_GT_EQ", 
			"EQUAL", "COMPARISON_OP_NOT_EQ", "EQ_EQ", "AND_AND", "OR_OR", "STRING_LITERAL", 
			"COMPARISON_OP_NOT", "COMPARISON_OP_BETWEEN", "COMPARISON_OP_IN", "COMPARISON_OP_LIKE", 
			"COMPARISON_OP_IS_NULL", "COMPARISON_OP_IS_NOT_NULL", "GROUP_BY", "HAVING", 
			"ORDER_BY", "ORDER_BY_DIRECTION", "LIMIT", "LEFT_BRACKET", "RIGHT_BRACKET", 
			"COMMA", "COLON", "DOT", "QUESTION_MARK", "HASH", "LEFT_SQUARE", "RIGHT_SQUARE", 
			"ARROW", "DOLLAR", "PERCENT", "IF", "WHEN", "OTHERWISE", "CHOOSE", "UPPER_NAME", 
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
			setState(242);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				{
				setState(238);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				{
				setState(239);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				{
				setState(240);
				update_statement();
				}
				break;
			case SELECT_ACTION:
				{
				setState(241);
				select_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(244);
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
			setState(246);
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
			setState(248);
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
			setState(250);
			delete_clause();
			setState(251);
			modify_entity();
			setState(252);
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
			setState(254);
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
			setState(256);
			update_clause();
			setState(257);
			modify_entity();
			setState(258);
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
			setState(260);
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
			setState(262);
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
			setState(264);
			select_action();
			setState(265);
			select_item_clause();
			setState(266);
			select_from_clause();
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(267);
				where_clause();
				}
			}

			setState(271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP_BY) {
				{
				setState(270);
				group_by_clause();
				}
			}

			setState(274);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HAVING) {
				{
				setState(273);
				having_clause();
				}
			}

			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY) {
				{
				setState(276);
				order_by_clause();
				}
			}

			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(279);
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
			setState(282);
			select_item();
			setState(288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(283);
				comma();
				setState(284);
				select_item();
				}
				}
				setState(290);
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
		try {
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(291);
				select_column_all();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(292);
				select_column_custom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(293);
				aggregate_function();
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
			setState(301);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_ASTERISK:
				enterOuterAlt(_localctx, 1);
				{
				setState(296);
				select_asterisk();
				}
				break;
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(297);
				entity_name_alias();
				setState(298);
				dot();
				setState(299);
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
			setState(303);
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
			setState(305);
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
			setState(307);
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
		enterRule(_localctx, 30, RULE_aggregate_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			aggregate_function_name();
			setState(310);
			left_bracket();
			setState(311);
			aggregate_function_argument();
			setState(312);
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
		enterRule(_localctx, 32, RULE_aggregate_function_name);
		try {
			setState(319);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_MAX:
				enterOuterAlt(_localctx, 1);
				{
				setState(314);
				select_max();
				}
				break;
			case SELECT_MIN:
				enterOuterAlt(_localctx, 2);
				{
				setState(315);
				select_min();
				}
				break;
			case SELECT_AVG:
				enterOuterAlt(_localctx, 3);
				{
				setState(316);
				select_avg();
				}
				break;
			case SELECT_SUM:
				enterOuterAlt(_localctx, 4);
				{
				setState(317);
				select_sum();
				}
				break;
			case SELECT_COUNT:
				enterOuterAlt(_localctx, 5);
				{
				setState(318);
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
		enterRule(_localctx, 34, RULE_aggregate_function_argument);
		try {
			setState(324);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(321);
				field_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				number();
				}
				break;
			case SELECT_ASTERISK:
				enterOuterAlt(_localctx, 3);
				{
				setState(323);
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
		enterRule(_localctx, 36, RULE_select_max);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
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
		enterRule(_localctx, 38, RULE_select_min);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
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
		enterRule(_localctx, 40, RULE_select_avg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
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
		enterRule(_localctx, 42, RULE_select_sum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
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
		enterRule(_localctx, 44, RULE_select_count);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
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
		enterRule(_localctx, 46, RULE_select_from_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			select_from();
			setState(337);
			select_primary_entity();
			setState(341);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LEFT) {
				{
				{
				setState(338);
				select_join_entity();
				}
				}
				setState(343);
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
		enterRule(_localctx, 48, RULE_select_primary_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			select_entity();
			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTED_NAME || _la==LOWER_NAME) {
				{
				setState(345);
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
		enterRule(_localctx, 50, RULE_select_join_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			select_left_join();
			setState(349);
			select_entity();
			setState(351);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QUOTED_NAME || _la==LOWER_NAME) {
				{
				setState(350);
				select_entity_alias();
				}
			}

			setState(353);
			select_on();
			setState(354);
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
		enterRule(_localctx, 52, RULE_select_entity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
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
		enterRule(_localctx, 54, RULE_select_entity_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
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
		enterRule(_localctx, 56, RULE_select_from);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
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
		enterRule(_localctx, 58, RULE_select_left_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(LEFT);
			setState(363);
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
		enterRule(_localctx, 60, RULE_select_on);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
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
		enterRule(_localctx, 62, RULE_select_on_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367);
			entity_name_alias();
			setState(368);
			on_equal();
			setState(369);
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
		enterRule(_localctx, 64, RULE_on_equal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
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
		enterRule(_localctx, 66, RULE_group_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
			group_by();
			setState(374);
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
		enterRule(_localctx, 68, RULE_group_by_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			field_reference();
			setState(382);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(377);
				comma();
				setState(378);
				field_reference();
				}
				}
				setState(384);
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
		enterRule(_localctx, 70, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			having();
			setState(386);
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
		enterRule(_localctx, 72, RULE_having_or_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			having_and_expression();
			setState(394);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OR) {
				{
				{
				setState(389);
				logic_or();
				setState(390);
				having_and_expression();
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
		enterRule(_localctx, 74, RULE_having_and_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			having_term();
			setState(403);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_AND) {
				{
				{
				setState(398);
				logic_and();
				setState(399);
				having_term();
				}
				}
				setState(405);
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
		enterRule(_localctx, 76, RULE_having_term);
		try {
			setState(411);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_COUNT:
			case SELECT_MAX:
			case SELECT_MIN:
			case SELECT_AVG:
			case SELECT_SUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(406);
				having_comparison();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(407);
				left_bracket();
				setState(408);
				having_or_expression();
				setState(409);
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
		enterRule(_localctx, 78, RULE_having_comparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(413);
			aggregate_function();
			setState(414);
			relational_op();
			setState(415);
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
		enterRule(_localctx, 80, RULE_having_value);
		try {
			setState(419);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				parameter_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(418);
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
		enterRule(_localctx, 82, RULE_order_by_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			order_by();
			setState(422);
			order_by_expression();
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(423);
				comma();
				setState(424);
				order_by_expression();
				}
				}
				setState(430);
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
		enterRule(_localctx, 84, RULE_order_by_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			field_reference();
			setState(433);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY_DIRECTION) {
				{
				setState(432);
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
		enterRule(_localctx, 86, RULE_limit_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435);
			limit();
			setState(436);
			offset();
			setState(437);
			comma();
			setState(438);
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
		enterRule(_localctx, 88, RULE_limit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
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
		enterRule(_localctx, 90, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
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
		enterRule(_localctx, 92, RULE_size);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444);
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
		enterRule(_localctx, 94, RULE_having);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(446);
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
		enterRule(_localctx, 96, RULE_group_by);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
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
		enterRule(_localctx, 98, RULE_order_by);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(450);
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
		enterRule(_localctx, 100, RULE_order_by_direction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
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
		enterRule(_localctx, 102, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Where_clauseContext extends ParserRuleContext {
		public Where_startContext where_start() {
			return getRuleContext(Where_startContext.class,0);
		}
		public Where_sequenceContext where_sequence() {
			return getRuleContext(Where_sequenceContext.class,0);
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
		enterRule(_localctx, 104, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
			where_start();
			setState(457);
			where_sequence();
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
	public static class Where_sequenceContext extends ParserRuleContext {
		public List<Where_itemContext> where_item() {
			return getRuleContexts(Where_itemContext.class);
		}
		public Where_itemContext where_item(int i) {
			return getRuleContext(Where_itemContext.class,i);
		}
		public Where_sequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_sequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhere_sequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhere_sequence(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhere_sequence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_sequenceContext where_sequence() throws RecognitionException {
		Where_sequenceContext _localctx = new Where_sequenceContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_where_sequence);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(459);
				where_item();
				}
				}
				setState(462); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 216208516241883136L) != 0) );
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
	public static class Where_itemContext extends ParserRuleContext {
		public Where_atomContext where_atom() {
			return getRuleContext(Where_atomContext.class,0);
		}
		public Logic_andContext logic_and() {
			return getRuleContext(Logic_andContext.class,0);
		}
		public Logic_orContext logic_or() {
			return getRuleContext(Logic_orContext.class,0);
		}
		public Where_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhere_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhere_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhere_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_itemContext where_item() throws RecognitionException {
		Where_itemContext _localctx = new Where_itemContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_where_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOGIC_AND:
				{
				setState(464);
				logic_and();
				}
				break;
			case LOGIC_OR:
				{
				setState(465);
				logic_or();
				}
				break;
			case LEFT_BRACKET:
			case HASH:
			case QUOTED_NAME:
			case LOWER_NAME:
				break;
			default:
				break;
			}
			setState(468);
			where_atom();
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
	public static class Where_atomContext extends ParserRuleContext {
		public Condition_comparisonContext condition_comparison() {
			return getRuleContext(Condition_comparisonContext.class,0);
		}
		public Bracket_groupContext bracket_group() {
			return getRuleContext(Bracket_groupContext.class,0);
		}
		public If_directiveContext if_directive() {
			return getRuleContext(If_directiveContext.class,0);
		}
		public Bracket_directiveContext bracket_directive() {
			return getRuleContext(Bracket_directiveContext.class,0);
		}
		public Choose_directiveContext choose_directive() {
			return getRuleContext(Choose_directiveContext.class,0);
		}
		public Where_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhere_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhere_atom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhere_atom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_atomContext where_atom() throws RecognitionException {
		Where_atomContext _localctx = new Where_atomContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_where_atom);
		try {
			setState(475);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(470);
				condition_comparison();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(471);
				bracket_group();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(472);
				if_directive();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(473);
				bracket_directive();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(474);
				choose_directive();
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
	public static class Bracket_groupContext extends ParserRuleContext {
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Where_sequenceContext where_sequence() {
			return getRuleContext(Where_sequenceContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Bracket_groupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracket_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBracket_group(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBracket_group(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBracket_group(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bracket_groupContext bracket_group() throws RecognitionException {
		Bracket_groupContext _localctx = new Bracket_groupContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_bracket_group);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477);
			left_bracket();
			setState(478);
			where_sequence();
			setState(479);
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
	public static class If_directiveContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(MgxqlParser.HASH, 0); }
		public TerminalNode IF() { return getToken(MgxqlParser.IF, 0); }
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Guard_or_expressionContext guard_or_expression() {
			return getRuleContext(Guard_or_expressionContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Left_squareContext left_square() {
			return getRuleContext(Left_squareContext.class,0);
		}
		public Body_sequenceContext body_sequence() {
			return getRuleContext(Body_sequenceContext.class,0);
		}
		public Right_squareContext right_square() {
			return getRuleContext(Right_squareContext.class,0);
		}
		public Block_prefixContext block_prefix() {
			return getRuleContext(Block_prefixContext.class,0);
		}
		public If_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterIf_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitIf_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitIf_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_directiveContext if_directive() throws RecognitionException {
		If_directiveContext _localctx = new If_directiveContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_if_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481);
			match(HASH);
			setState(482);
			match(IF);
			setState(483);
			left_bracket();
			setState(484);
			guard_or_expression();
			setState(485);
			right_bracket();
			setState(486);
			left_square();
			setState(488);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(487);
				block_prefix();
				}
				break;
			}
			setState(490);
			body_sequence();
			setState(491);
			right_square();
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
	public static class Bracket_directiveContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(MgxqlParser.HASH, 0); }
		public TerminalNode LEFT_SQUARE() { return getToken(MgxqlParser.LEFT_SQUARE, 0); }
		public Body_sequenceContext body_sequence() {
			return getRuleContext(Body_sequenceContext.class,0);
		}
		public TerminalNode RIGHT_SQUARE() { return getToken(MgxqlParser.RIGHT_SQUARE, 0); }
		public Block_prefixContext block_prefix() {
			return getRuleContext(Block_prefixContext.class,0);
		}
		public Bracket_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracket_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBracket_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBracket_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBracket_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bracket_directiveContext bracket_directive() throws RecognitionException {
		Bracket_directiveContext _localctx = new Bracket_directiveContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_bracket_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(493);
			match(HASH);
			setState(494);
			match(LEFT_SQUARE);
			setState(496);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(495);
				block_prefix();
				}
				break;
			}
			setState(498);
			body_sequence();
			setState(499);
			match(RIGHT_SQUARE);
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
	public static class Choose_directiveContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(MgxqlParser.HASH, 0); }
		public TerminalNode CHOOSE() { return getToken(MgxqlParser.CHOOSE, 0); }
		public TerminalNode LEFT_SQUARE() { return getToken(MgxqlParser.LEFT_SQUARE, 0); }
		public TerminalNode RIGHT_SQUARE() { return getToken(MgxqlParser.RIGHT_SQUARE, 0); }
		public List<When_directiveContext> when_directive() {
			return getRuleContexts(When_directiveContext.class);
		}
		public When_directiveContext when_directive(int i) {
			return getRuleContext(When_directiveContext.class,i);
		}
		public Otherwise_directiveContext otherwise_directive() {
			return getRuleContext(Otherwise_directiveContext.class,0);
		}
		public Choose_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_choose_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterChoose_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitChoose_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitChoose_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Choose_directiveContext choose_directive() throws RecognitionException {
		Choose_directiveContext _localctx = new Choose_directiveContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_choose_directive);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(501);
			match(HASH);
			setState(502);
			match(CHOOSE);
			setState(503);
			match(LEFT_SQUARE);
			setState(505); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(504);
					when_directive();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(507); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(510);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HASH) {
				{
				setState(509);
				otherwise_directive();
				}
			}

			setState(512);
			match(RIGHT_SQUARE);
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
	public static class When_directiveContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(MgxqlParser.HASH, 0); }
		public TerminalNode WHEN() { return getToken(MgxqlParser.WHEN, 0); }
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Guard_or_expressionContext guard_or_expression() {
			return getRuleContext(Guard_or_expressionContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Left_squareContext left_square() {
			return getRuleContext(Left_squareContext.class,0);
		}
		public Body_sequenceContext body_sequence() {
			return getRuleContext(Body_sequenceContext.class,0);
		}
		public Right_squareContext right_square() {
			return getRuleContext(Right_squareContext.class,0);
		}
		public Block_prefixContext block_prefix() {
			return getRuleContext(Block_prefixContext.class,0);
		}
		public When_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterWhen_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitWhen_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitWhen_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final When_directiveContext when_directive() throws RecognitionException {
		When_directiveContext _localctx = new When_directiveContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_when_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(514);
			match(HASH);
			setState(515);
			match(WHEN);
			setState(516);
			left_bracket();
			setState(517);
			guard_or_expression();
			setState(518);
			right_bracket();
			setState(519);
			left_square();
			setState(521);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(520);
				block_prefix();
				}
				break;
			}
			setState(523);
			body_sequence();
			setState(524);
			right_square();
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
	public static class Otherwise_directiveContext extends ParserRuleContext {
		public TerminalNode HASH() { return getToken(MgxqlParser.HASH, 0); }
		public TerminalNode OTHERWISE() { return getToken(MgxqlParser.OTHERWISE, 0); }
		public TerminalNode LEFT_SQUARE() { return getToken(MgxqlParser.LEFT_SQUARE, 0); }
		public Body_sequenceContext body_sequence() {
			return getRuleContext(Body_sequenceContext.class,0);
		}
		public TerminalNode RIGHT_SQUARE() { return getToken(MgxqlParser.RIGHT_SQUARE, 0); }
		public Otherwise_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_otherwise_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterOtherwise_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitOtherwise_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitOtherwise_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Otherwise_directiveContext otherwise_directive() throws RecognitionException {
		Otherwise_directiveContext _localctx = new Otherwise_directiveContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_otherwise_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			match(HASH);
			setState(527);
			match(OTHERWISE);
			setState(528);
			match(LEFT_SQUARE);
			setState(529);
			body_sequence();
			setState(530);
			match(RIGHT_SQUARE);
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
	public static class Block_prefixContext extends ParserRuleContext {
		public Logic_andContext logic_and() {
			return getRuleContext(Logic_andContext.class,0);
		}
		public Logic_orContext logic_or() {
			return getRuleContext(Logic_orContext.class,0);
		}
		public Block_prefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block_prefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBlock_prefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBlock_prefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBlock_prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Block_prefixContext block_prefix() throws RecognitionException {
		Block_prefixContext _localctx = new Block_prefixContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_block_prefix);
		try {
			setState(534);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOGIC_AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(532);
				logic_and();
				}
				break;
			case LOGIC_OR:
				enterOuterAlt(_localctx, 2);
				{
				setState(533);
				logic_or();
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
	public static class Body_sequenceContext extends ParserRuleContext {
		public List<Body_itemContext> body_item() {
			return getRuleContexts(Body_itemContext.class);
		}
		public Body_itemContext body_item(int i) {
			return getRuleContext(Body_itemContext.class,i);
		}
		public Body_sequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body_sequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBody_sequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBody_sequence(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBody_sequence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Body_sequenceContext body_sequence() throws RecognitionException {
		Body_sequenceContext _localctx = new Body_sequenceContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_body_sequence);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(537); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(536);
				body_item();
				}
				}
				setState(539); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 216173331869794304L) != 0) );
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
	public static class Body_itemContext extends ParserRuleContext {
		public Body_atomContext body_atom() {
			return getRuleContext(Body_atomContext.class,0);
		}
		public Logic_andContext logic_and() {
			return getRuleContext(Logic_andContext.class,0);
		}
		public Logic_orContext logic_or() {
			return getRuleContext(Logic_orContext.class,0);
		}
		public Body_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBody_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBody_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBody_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Body_itemContext body_item() throws RecognitionException {
		Body_itemContext _localctx = new Body_itemContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_body_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(543);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOGIC_AND:
				{
				setState(541);
				logic_and();
				}
				break;
			case LOGIC_OR:
				{
				setState(542);
				logic_or();
				}
				break;
			case LEFT_BRACKET:
			case QUOTED_NAME:
			case LOWER_NAME:
				break;
			default:
				break;
			}
			setState(545);
			body_atom();
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
	public static class Body_atomContext extends ParserRuleContext {
		public Condition_comparisonContext condition_comparison() {
			return getRuleContext(Condition_comparisonContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Body_sequenceContext body_sequence() {
			return getRuleContext(Body_sequenceContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Body_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterBody_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitBody_atom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitBody_atom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Body_atomContext body_atom() throws RecognitionException {
		Body_atomContext _localctx = new Body_atomContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_body_atom);
		try {
			setState(552);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(547);
				condition_comparison();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(548);
				left_bracket();
				setState(549);
				body_sequence();
				setState(550);
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
	public static class Guard_or_expressionContext extends ParserRuleContext {
		public List<Guard_and_expressionContext> guard_and_expression() {
			return getRuleContexts(Guard_and_expressionContext.class);
		}
		public Guard_and_expressionContext guard_and_expression(int i) {
			return getRuleContext(Guard_and_expressionContext.class,i);
		}
		public List<Guard_logic_orContext> guard_logic_or() {
			return getRuleContexts(Guard_logic_orContext.class);
		}
		public Guard_logic_orContext guard_logic_or(int i) {
			return getRuleContext(Guard_logic_orContext.class,i);
		}
		public Guard_or_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_or_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_or_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_or_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_or_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_or_expressionContext guard_or_expression() throws RecognitionException {
		Guard_or_expressionContext _localctx = new Guard_or_expressionContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_guard_or_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			guard_and_expression();
			setState(560);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OR || _la==AND_AND) {
				{
				{
				setState(555);
				guard_logic_or();
				setState(556);
				guard_and_expression();
				}
				}
				setState(562);
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
	public static class Guard_and_expressionContext extends ParserRuleContext {
		public List<Guard_termContext> guard_term() {
			return getRuleContexts(Guard_termContext.class);
		}
		public Guard_termContext guard_term(int i) {
			return getRuleContext(Guard_termContext.class,i);
		}
		public List<Guard_logic_andContext> guard_logic_and() {
			return getRuleContexts(Guard_logic_andContext.class);
		}
		public Guard_logic_andContext guard_logic_and(int i) {
			return getRuleContext(Guard_logic_andContext.class,i);
		}
		public Guard_and_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_and_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_and_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_and_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_and_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_and_expressionContext guard_and_expression() throws RecognitionException {
		Guard_and_expressionContext _localctx = new Guard_and_expressionContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_guard_and_expression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(563);
			guard_term();
			setState(569);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(564);
					guard_logic_and();
					setState(565);
					guard_term();
					}
					} 
				}
				setState(571);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
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
	public static class Guard_termContext extends ParserRuleContext {
		public Guard_comparisonContext guard_comparison() {
			return getRuleContext(Guard_comparisonContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Guard_or_expressionContext guard_or_expression() {
			return getRuleContext(Guard_or_expressionContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Guard_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_termContext guard_term() throws RecognitionException {
		Guard_termContext _localctx = new Guard_termContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_guard_term);
		try {
			setState(577);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
			case COLON:
			case QUOTED_NAME:
			case LOWER_NAME:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(572);
				guard_comparison();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(573);
				left_bracket();
				setState(574);
				guard_or_expression();
				setState(575);
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
	public static class Guard_comparisonContext extends ParserRuleContext {
		public List<Guard_operandContext> guard_operand() {
			return getRuleContexts(Guard_operandContext.class);
		}
		public Guard_operandContext guard_operand(int i) {
			return getRuleContext(Guard_operandContext.class,i);
		}
		public Guard_relational_opContext guard_relational_op() {
			return getRuleContext(Guard_relational_opContext.class,0);
		}
		public Guard_null_opContext guard_null_op() {
			return getRuleContext(Guard_null_opContext.class,0);
		}
		public Guard_comparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_comparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_comparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_comparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_comparisonContext guard_comparison() throws RecognitionException {
		Guard_comparisonContext _localctx = new Guard_comparisonContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_guard_comparison);
		try {
			setState(586);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(579);
				guard_operand();
				setState(580);
				guard_relational_op();
				setState(581);
				guard_operand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(583);
				guard_operand();
				setState(584);
				guard_null_op();
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
	public static class Guard_logic_orContext extends ParserRuleContext {
		public TerminalNode AND_AND() { return getToken(MgxqlParser.AND_AND, 0); }
		public TerminalNode LOGIC_OR() { return getToken(MgxqlParser.LOGIC_OR, 0); }
		public Guard_logic_orContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_logic_or; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_logic_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_logic_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_logic_or(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_logic_orContext guard_logic_or() throws RecognitionException {
		Guard_logic_orContext _localctx = new Guard_logic_orContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_guard_logic_or);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(588);
			_la = _input.LA(1);
			if ( !(_la==LOGIC_OR || _la==AND_AND) ) {
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
	public static class Guard_logic_andContext extends ParserRuleContext {
		public TerminalNode AND_AND() { return getToken(MgxqlParser.AND_AND, 0); }
		public TerminalNode LOGIC_AND() { return getToken(MgxqlParser.LOGIC_AND, 0); }
		public Guard_logic_andContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_logic_and; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_logic_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_logic_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_logic_and(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_logic_andContext guard_logic_and() throws RecognitionException {
		Guard_logic_andContext _localctx = new Guard_logic_andContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_guard_logic_and);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			_la = _input.LA(1);
			if ( !(_la==LOGIC_AND || _la==AND_AND) ) {
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
	public static class Guard_relational_opContext extends ParserRuleContext {
		public TerminalNode EQ_EQ() { return getToken(MgxqlParser.EQ_EQ, 0); }
		public TerminalNode COMPARISON_OP_NOT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_NOT_EQ, 0); }
		public TerminalNode COMPARISON_OP_LT() { return getToken(MgxqlParser.COMPARISON_OP_LT, 0); }
		public TerminalNode COMPARISON_OP_LT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_LT_EQ, 0); }
		public TerminalNode COMPARISON_OP_GT() { return getToken(MgxqlParser.COMPARISON_OP_GT, 0); }
		public TerminalNode COMPARISON_OP_GT_EQ() { return getToken(MgxqlParser.COMPARISON_OP_GT_EQ, 0); }
		public Guard_relational_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_relational_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_relational_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_relational_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_relational_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_relational_opContext guard_relational_op() throws RecognitionException {
		Guard_relational_opContext _localctx = new Guard_relational_opContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_guard_relational_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(592);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 29097984L) != 0)) ) {
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
	public static class Guard_null_opContext extends ParserRuleContext {
		public Comparison_op_is_nullContext comparison_op_is_null() {
			return getRuleContext(Comparison_op_is_nullContext.class,0);
		}
		public Comparison_op_is_not_nullContext comparison_op_is_not_null() {
			return getRuleContext(Comparison_op_is_not_nullContext.class,0);
		}
		public Guard_null_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_null_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_null_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_null_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_null_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_null_opContext guard_null_op() throws RecognitionException {
		Guard_null_opContext _localctx = new Guard_null_opContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_guard_null_op);
		try {
			setState(596);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_IS_NULL:
				enterOuterAlt(_localctx, 1);
				{
				setState(594);
				comparison_op_is_null();
				}
				break;
			case COMPARISON_OP_IS_NOT_NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(595);
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
	public static class Guard_operandContext extends ParserRuleContext {
		public Field_referenceContext field_reference() {
			return getRuleContext(Field_referenceContext.class,0);
		}
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(MgxqlParser.STRING_LITERAL, 0); }
		public Guard_operandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_guard_operand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterGuard_operand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitGuard_operand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitGuard_operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Guard_operandContext guard_operand() throws RecognitionException {
		Guard_operandContext _localctx = new Guard_operandContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_guard_operand);
		try {
			setState(602);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTED_NAME:
			case LOWER_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(598);
				field_reference();
				}
				break;
			case COLON:
				enterOuterAlt(_localctx, 2);
				{
				setState(599);
				parameter_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(600);
				number();
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(601);
				match(STRING_LITERAL);
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
		enterRule(_localctx, 150, RULE_condition_comparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			field_reference();
			setState(607);
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
				{
				setState(605);
				condition_comparison_param();
				}
				break;
			case COMPARISON_OP_IS_NULL:
			case COMPARISON_OP_IS_NOT_NULL:
				{
				setState(606);
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
		public Relational_opContext relational_op() {
			return getRuleContext(Relational_opContext.class,0);
		}
		public Condition_valueContext condition_value() {
			return getRuleContext(Condition_valueContext.class,0);
		}
		public Matching_opContext matching_op() {
			return getRuleContext(Matching_opContext.class,0);
		}
		public Matching_valueContext matching_value() {
			return getRuleContext(Matching_valueContext.class,0);
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
		enterRule(_localctx, 152, RULE_condition_comparison_param);
		try {
			setState(615);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_LT:
			case COMPARISON_OP_LT_EQ:
			case COMPARISON_OP_GT:
			case COMPARISON_OP_GT_EQ:
			case EQUAL:
			case COMPARISON_OP_NOT_EQ:
				enterOuterAlt(_localctx, 1);
				{
				setState(609);
				relational_op();
				setState(610);
				condition_value();
				}
				break;
			case COMPARISON_OP_NOT:
			case COMPARISON_OP_BETWEEN:
			case COMPARISON_OP_IN:
			case COMPARISON_OP_LIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(612);
				matching_op();
				setState(613);
				matching_value();
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
		enterRule(_localctx, 154, RULE_condition_comparison_not_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
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
		enterRule(_localctx, 156, RULE_condition_value);
		try {
			setState(621);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(619);
				parameter_reference();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(620);
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
		enterRule(_localctx, 158, RULE_where_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(623);
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
		enterRule(_localctx, 160, RULE_logic_and);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(625);
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
		enterRule(_localctx, 162, RULE_logic_or);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
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
	public static class Left_squareContext extends ParserRuleContext {
		public TerminalNode LEFT_SQUARE() { return getToken(MgxqlParser.LEFT_SQUARE, 0); }
		public Left_squareContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_left_square; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLeft_square(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLeft_square(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLeft_square(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Left_squareContext left_square() throws RecognitionException {
		Left_squareContext _localctx = new Left_squareContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_left_square);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(629);
			match(LEFT_SQUARE);
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
	public static class Right_squareContext extends ParserRuleContext {
		public TerminalNode RIGHT_SQUARE() { return getToken(MgxqlParser.RIGHT_SQUARE, 0); }
		public Right_squareContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_right_square; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterRight_square(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitRight_square(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitRight_square(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Right_squareContext right_square() throws RecognitionException {
		Right_squareContext _localctx = new Right_squareContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_right_square);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(631);
			match(RIGHT_SQUARE);
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
		enterRule(_localctx, 168, RULE_relational_op);
		try {
			setState(639);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_LT:
				enterOuterAlt(_localctx, 1);
				{
				setState(633);
				comparison_op_lt();
				}
				break;
			case COMPARISON_OP_LT_EQ:
				enterOuterAlt(_localctx, 2);
				{
				setState(634);
				comparison_op_lt_eq();
				}
				break;
			case COMPARISON_OP_GT:
				enterOuterAlt(_localctx, 3);
				{
				setState(635);
				comparison_op_gt();
				}
				break;
			case COMPARISON_OP_GT_EQ:
				enterOuterAlt(_localctx, 4);
				{
				setState(636);
				comparison_op_gt_eq();
				}
				break;
			case EQUAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(637);
				comparison_op_eq();
				}
				break;
			case COMPARISON_OP_NOT_EQ:
				enterOuterAlt(_localctx, 6);
				{
				setState(638);
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
		enterRule(_localctx, 170, RULE_comparison_op_lt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(641);
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
		enterRule(_localctx, 172, RULE_comparison_op_lt_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(643);
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
		enterRule(_localctx, 174, RULE_comparison_op_gt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(645);
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
		enterRule(_localctx, 176, RULE_comparison_op_gt_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(647);
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
		enterRule(_localctx, 178, RULE_comparison_op_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(649);
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
		enterRule(_localctx, 180, RULE_comparison_op_not_eq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
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
		enterRule(_localctx, 182, RULE_matching_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(654);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMPARISON_OP_NOT) {
				{
				setState(653);
				comparison_op_not();
				}
			}

			setState(659);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_BETWEEN:
				{
				setState(656);
				comparison_op_between();
				}
				break;
			case COMPARISON_OP_IN:
				{
				setState(657);
				comparison_op_in();
				}
				break;
			case COMPARISON_OP_LIKE:
				{
				setState(658);
				comparison_op_like();
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
		enterRule(_localctx, 184, RULE_comparison_op_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(661);
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
		enterRule(_localctx, 186, RULE_comparison_op_between);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(663);
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
		enterRule(_localctx, 188, RULE_comparison_op_in);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(665);
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
		enterRule(_localctx, 190, RULE_comparison_op_like);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(667);
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
	public static class Matching_valueContext extends ParserRuleContext {
		public In_collectionContext in_collection() {
			return getRuleContext(In_collectionContext.class,0);
		}
		public Like_patternContext like_pattern() {
			return getRuleContext(Like_patternContext.class,0);
		}
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public Matching_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matching_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterMatching_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitMatching_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitMatching_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Matching_valueContext matching_value() throws RecognitionException {
		Matching_valueContext _localctx = new Matching_valueContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_matching_value);
		try {
			setState(672);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(669);
				in_collection();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				like_pattern();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(671);
				parameter_reference();
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
	public static class Like_patternContext extends ParserRuleContext {
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public List<TerminalNode> PERCENT() { return getTokens(MgxqlParser.PERCENT); }
		public TerminalNode PERCENT(int i) {
			return getToken(MgxqlParser.PERCENT, i);
		}
		public Like_patternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_like_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterLike_pattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitLike_pattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitLike_pattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Like_patternContext like_pattern() throws RecognitionException {
		Like_patternContext _localctx = new Like_patternContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_like_pattern);
		int _la;
		try {
			setState(697);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(675); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(674);
					match(PERCENT);
					}
					}
					setState(677); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==PERCENT );
				setState(679);
				parameter_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(680);
				parameter_reference();
				setState(682); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(681);
					match(PERCENT);
					}
					}
					setState(684); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==PERCENT );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(687); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(686);
					match(PERCENT);
					}
					}
					setState(689); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==PERCENT );
				setState(691);
				parameter_reference();
				setState(693); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(692);
					match(PERCENT);
					}
					}
					setState(695); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==PERCENT );
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
	public static class In_collectionContext extends ParserRuleContext {
		public Simple_collectionContext simple_collection() {
			return getRuleContext(Simple_collectionContext.class,0);
		}
		public Complex_collectionContext complex_collection() {
			return getRuleContext(Complex_collectionContext.class,0);
		}
		public In_collectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_collection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterIn_collection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitIn_collection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitIn_collection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final In_collectionContext in_collection() throws RecognitionException {
		In_collectionContext _localctx = new In_collectionContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_in_collection);
		try {
			setState(701);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(699);
				simple_collection();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(700);
				complex_collection();
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
	public static class Simple_collectionContext extends ParserRuleContext {
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Parameter_referenceContext parameter_reference() {
			return getRuleContext(Parameter_referenceContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public Simple_collectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_collection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterSimple_collection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitSimple_collection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitSimple_collection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Simple_collectionContext simple_collection() throws RecognitionException {
		Simple_collectionContext _localctx = new Simple_collectionContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_simple_collection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(703);
			left_bracket();
			setState(704);
			parameter_reference();
			setState(705);
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
	public static class Complex_collectionContext extends ParserRuleContext {
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Item_nameContext item_name() {
			return getRuleContext(Item_nameContext.class,0);
		}
		public Param_colonContext param_colon() {
			return getRuleContext(Param_colonContext.class,0);
		}
		public Collection_pathContext collection_path() {
			return getRuleContext(Collection_pathContext.class,0);
		}
		public Right_bracketContext right_bracket() {
			return getRuleContext(Right_bracketContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(MgxqlParser.ARROW, 0); }
		public TerminalNode DOLLAR() { return getToken(MgxqlParser.DOLLAR, 0); }
		public Value_expr_listContext value_expr_list() {
			return getRuleContext(Value_expr_listContext.class,0);
		}
		public Complex_collectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complex_collection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterComplex_collection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitComplex_collection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitComplex_collection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Complex_collectionContext complex_collection() throws RecognitionException {
		Complex_collectionContext _localctx = new Complex_collectionContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_complex_collection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(707);
			left_bracket();
			setState(708);
			item_name();
			setState(709);
			param_colon();
			setState(710);
			collection_path();
			setState(711);
			right_bracket();
			setState(712);
			match(ARROW);
			setState(713);
			match(DOLLAR);
			setState(714);
			value_expr_list();
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
	public static class Collection_pathContext extends ParserRuleContext {
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
		public Collection_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collection_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterCollection_path(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitCollection_path(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitCollection_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Collection_pathContext collection_path() throws RecognitionException {
		Collection_pathContext _localctx = new Collection_pathContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_collection_path);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			field_name();
			setState(722);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(717);
				dot();
				setState(718);
				field_name();
				}
				}
				setState(724);
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
	public static class Value_expr_listContext extends ParserRuleContext {
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
		public Value_expr_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_expr_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterValue_expr_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitValue_expr_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitValue_expr_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_expr_listContext value_expr_list() throws RecognitionException {
		Value_expr_listContext _localctx = new Value_expr_listContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_value_expr_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			field_name();
			setState(731);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(726);
				dot();
				setState(727);
				field_name();
				}
				}
				setState(733);
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
	public static class Item_nameContext extends ParserRuleContext {
		public TerminalNode LOWER_NAME() { return getToken(MgxqlParser.LOWER_NAME, 0); }
		public Item_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).enterItem_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MgxqlParserListener ) ((MgxqlParserListener)listener).exitItem_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MgxqlParserVisitor ) return ((MgxqlParserVisitor<? extends T>)visitor).visitItem_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Item_nameContext item_name() throws RecognitionException {
		Item_nameContext _localctx = new Item_nameContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_item_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(734);
			match(LOWER_NAME);
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
		enterRule(_localctx, 208, RULE_comparison_op_null);
		try {
			setState(738);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_OP_IS_NULL:
				enterOuterAlt(_localctx, 1);
				{
				setState(736);
				comparison_op_is_null();
				}
				break;
			case COMPARISON_OP_IS_NOT_NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(737);
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
		enterRule(_localctx, 210, RULE_comparison_op_is_null);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(740);
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
		enterRule(_localctx, 212, RULE_comparison_op_is_not_null);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(742);
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
		enterRule(_localctx, 214, RULE_field_reference);
		try {
			setState(749);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(744);
				field_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(745);
				entity_name_alias();
				setState(746);
				dot();
				setState(747);
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
		enterRule(_localctx, 216, RULE_parameter_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
			param_colon();
			setState(752);
			field_name();
			setState(758);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(753);
				dot();
				setState(754);
				field_name();
				}
				}
				setState(760);
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
		enterRule(_localctx, 218, RULE_entity_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(761);
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
		enterRule(_localctx, 220, RULE_entity_name_alias);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(763);
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
		enterRule(_localctx, 222, RULE_field_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(765);
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
		enterRule(_localctx, 224, RULE_left_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(767);
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
		enterRule(_localctx, 226, RULE_right_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(769);
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
		enterRule(_localctx, 228, RULE_dot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(771);
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
		enterRule(_localctx, 230, RULE_param_colon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(773);
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
		enterRule(_localctx, 232, RULE_comma);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(775);
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
		enterRule(_localctx, 234, RULE_question_mark);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(777);
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
		enterRule(_localctx, 236, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(779);
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

	public static final String _serializedATN =
		"\u0004\u0001;\u030e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007]\u0002^\u0007^\u0002"+
		"_\u0007_\u0002`\u0007`\u0002a\u0007a\u0002b\u0007b\u0002c\u0007c\u0002"+
		"d\u0007d\u0002e\u0007e\u0002f\u0007f\u0002g\u0007g\u0002h\u0007h\u0002"+
		"i\u0007i\u0002j\u0007j\u0002k\u0007k\u0002l\u0007l\u0002m\u0007m\u0002"+
		"n\u0007n\u0002o\u0007o\u0002p\u0007p\u0002q\u0007q\u0002r\u0007r\u0002"+
		"s\u0007s\u0002t\u0007t\u0002u\u0007u\u0002v\u0007v\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u0000\u00f3\b\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u010d\b\b\u0001\b\u0003\b\u0110"+
		"\b\b\u0001\b\u0003\b\u0113\b\b\u0001\b\u0003\b\u0116\b\b\u0001\b\u0003"+
		"\b\u0119\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t\u011f\b\t\n\t\f\t"+
		"\u0122\t\t\u0001\n\u0001\n\u0001\n\u0003\n\u0127\b\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u012e\b\u000b\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0003\u0010\u0140\b\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0003\u0011\u0145\b\u0011\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u0154\b\u0017"+
		"\n\u0017\f\u0017\u0157\t\u0017\u0001\u0018\u0001\u0018\u0003\u0018\u015b"+
		"\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0160\b\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001 \u0001 \u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0005"+
		"\"\u017d\b\"\n\"\f\"\u0180\t\"\u0001#\u0001#\u0001#\u0001$\u0001$\u0001"+
		"$\u0001$\u0005$\u0189\b$\n$\f$\u018c\t$\u0001%\u0001%\u0001%\u0001%\u0005"+
		"%\u0192\b%\n%\f%\u0195\t%\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u019c"+
		"\b&\u0001\'\u0001\'\u0001\'\u0001\'\u0001(\u0001(\u0003(\u01a4\b(\u0001"+
		")\u0001)\u0001)\u0001)\u0001)\u0005)\u01ab\b)\n)\f)\u01ae\t)\u0001*\u0001"+
		"*\u0003*\u01b2\b*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001,\u0001,\u0001"+
		"-\u0001-\u0001.\u0001.\u0001/\u0001/\u00010\u00010\u00011\u00011\u0001"+
		"2\u00012\u00013\u00013\u00014\u00014\u00014\u00015\u00045\u01cd\b5\u000b"+
		"5\f5\u01ce\u00016\u00016\u00036\u01d3\b6\u00016\u00016\u00017\u00017\u0001"+
		"7\u00017\u00017\u00037\u01dc\b7\u00018\u00018\u00018\u00018\u00019\u0001"+
		"9\u00019\u00019\u00019\u00019\u00019\u00039\u01e9\b9\u00019\u00019\u0001"+
		"9\u0001:\u0001:\u0001:\u0003:\u01f1\b:\u0001:\u0001:\u0001:\u0001;\u0001"+
		";\u0001;\u0001;\u0004;\u01fa\b;\u000b;\f;\u01fb\u0001;\u0003;\u01ff\b"+
		";\u0001;\u0001;\u0001<\u0001<\u0001<\u0001<\u0001<\u0001<\u0001<\u0003"+
		"<\u020a\b<\u0001<\u0001<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001=\u0001"+
		"=\u0001>\u0001>\u0003>\u0217\b>\u0001?\u0004?\u021a\b?\u000b?\f?\u021b"+
		"\u0001@\u0001@\u0003@\u0220\b@\u0001@\u0001@\u0001A\u0001A\u0001A\u0001"+
		"A\u0001A\u0003A\u0229\bA\u0001B\u0001B\u0001B\u0001B\u0005B\u022f\bB\n"+
		"B\fB\u0232\tB\u0001C\u0001C\u0001C\u0001C\u0005C\u0238\bC\nC\fC\u023b"+
		"\tC\u0001D\u0001D\u0001D\u0001D\u0001D\u0003D\u0242\bD\u0001E\u0001E\u0001"+
		"E\u0001E\u0001E\u0001E\u0001E\u0003E\u024b\bE\u0001F\u0001F\u0001G\u0001"+
		"G\u0001H\u0001H\u0001I\u0001I\u0003I\u0255\bI\u0001J\u0001J\u0001J\u0001"+
		"J\u0003J\u025b\bJ\u0001K\u0001K\u0001K\u0003K\u0260\bK\u0001L\u0001L\u0001"+
		"L\u0001L\u0001L\u0001L\u0003L\u0268\bL\u0001M\u0001M\u0001N\u0001N\u0003"+
		"N\u026e\bN\u0001O\u0001O\u0001P\u0001P\u0001Q\u0001Q\u0001R\u0001R\u0001"+
		"S\u0001S\u0001T\u0001T\u0001T\u0001T\u0001T\u0001T\u0003T\u0280\bT\u0001"+
		"U\u0001U\u0001V\u0001V\u0001W\u0001W\u0001X\u0001X\u0001Y\u0001Y\u0001"+
		"Z\u0001Z\u0001[\u0003[\u028f\b[\u0001[\u0001[\u0001[\u0003[\u0294\b[\u0001"+
		"\\\u0001\\\u0001]\u0001]\u0001^\u0001^\u0001_\u0001_\u0001`\u0001`\u0001"+
		"`\u0003`\u02a1\b`\u0001a\u0004a\u02a4\ba\u000ba\fa\u02a5\u0001a\u0001"+
		"a\u0001a\u0004a\u02ab\ba\u000ba\fa\u02ac\u0001a\u0004a\u02b0\ba\u000b"+
		"a\fa\u02b1\u0001a\u0001a\u0004a\u02b6\ba\u000ba\fa\u02b7\u0003a\u02ba"+
		"\ba\u0001b\u0001b\u0003b\u02be\bb\u0001c\u0001c\u0001c\u0001c\u0001d\u0001"+
		"d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001e\u0001e\u0001"+
		"e\u0001e\u0005e\u02d1\be\ne\fe\u02d4\te\u0001f\u0001f\u0001f\u0001f\u0005"+
		"f\u02da\bf\nf\ff\u02dd\tf\u0001g\u0001g\u0001h\u0001h\u0003h\u02e3\bh"+
		"\u0001i\u0001i\u0001j\u0001j\u0001k\u0001k\u0001k\u0001k\u0001k\u0003"+
		"k\u02ee\bk\u0001l\u0001l\u0001l\u0001l\u0001l\u0005l\u02f5\bl\nl\fl\u02f8"+
		"\tl\u0001m\u0001m\u0001n\u0001n\u0001o\u0001o\u0001p\u0001p\u0001q\u0001"+
		"q\u0001r\u0001r\u0001s\u0001s\u0001t\u0001t\u0001u\u0001u\u0001v\u0001"+
		"v\u0001v\u0000\u0000w\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\"+
		"^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090"+
		"\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8"+
		"\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0"+
		"\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8"+
		"\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u0000\u0004"+
		"\u0002\u0000\u0011\u0011\u0019\u0019\u0002\u0000\u0010\u0010\u0019\u0019"+
		"\u0002\u0000\u0012\u0015\u0017\u0018\u0001\u000089\u02e4\u0000\u00f2\u0001"+
		"\u0000\u0000\u0000\u0002\u00f6\u0001\u0000\u0000\u0000\u0004\u00f8\u0001"+
		"\u0000\u0000\u0000\u0006\u00fa\u0001\u0000\u0000\u0000\b\u00fe\u0001\u0000"+
		"\u0000\u0000\n\u0100\u0001\u0000\u0000\u0000\f\u0104\u0001\u0000\u0000"+
		"\u0000\u000e\u0106\u0001\u0000\u0000\u0000\u0010\u0108\u0001\u0000\u0000"+
		"\u0000\u0012\u011a\u0001\u0000\u0000\u0000\u0014\u0126\u0001\u0000\u0000"+
		"\u0000\u0016\u012d\u0001\u0000\u0000\u0000\u0018\u012f\u0001\u0000\u0000"+
		"\u0000\u001a\u0131\u0001\u0000\u0000\u0000\u001c\u0133\u0001\u0000\u0000"+
		"\u0000\u001e\u0135\u0001\u0000\u0000\u0000 \u013f\u0001\u0000\u0000\u0000"+
		"\"\u0144\u0001\u0000\u0000\u0000$\u0146\u0001\u0000\u0000\u0000&\u0148"+
		"\u0001\u0000\u0000\u0000(\u014a\u0001\u0000\u0000\u0000*\u014c\u0001\u0000"+
		"\u0000\u0000,\u014e\u0001\u0000\u0000\u0000.\u0150\u0001\u0000\u0000\u0000"+
		"0\u0158\u0001\u0000\u0000\u00002\u015c\u0001\u0000\u0000\u00004\u0164"+
		"\u0001\u0000\u0000\u00006\u0166\u0001\u0000\u0000\u00008\u0168\u0001\u0000"+
		"\u0000\u0000:\u016a\u0001\u0000\u0000\u0000<\u016d\u0001\u0000\u0000\u0000"+
		">\u016f\u0001\u0000\u0000\u0000@\u0173\u0001\u0000\u0000\u0000B\u0175"+
		"\u0001\u0000\u0000\u0000D\u0178\u0001\u0000\u0000\u0000F\u0181\u0001\u0000"+
		"\u0000\u0000H\u0184\u0001\u0000\u0000\u0000J\u018d\u0001\u0000\u0000\u0000"+
		"L\u019b\u0001\u0000\u0000\u0000N\u019d\u0001\u0000\u0000\u0000P\u01a3"+
		"\u0001\u0000\u0000\u0000R\u01a5\u0001\u0000\u0000\u0000T\u01af\u0001\u0000"+
		"\u0000\u0000V\u01b3\u0001\u0000\u0000\u0000X\u01b8\u0001\u0000\u0000\u0000"+
		"Z\u01ba\u0001\u0000\u0000\u0000\\\u01bc\u0001\u0000\u0000\u0000^\u01be"+
		"\u0001\u0000\u0000\u0000`\u01c0\u0001\u0000\u0000\u0000b\u01c2\u0001\u0000"+
		"\u0000\u0000d\u01c4\u0001\u0000\u0000\u0000f\u01c6\u0001\u0000\u0000\u0000"+
		"h\u01c8\u0001\u0000\u0000\u0000j\u01cc\u0001\u0000\u0000\u0000l\u01d2"+
		"\u0001\u0000\u0000\u0000n\u01db\u0001\u0000\u0000\u0000p\u01dd\u0001\u0000"+
		"\u0000\u0000r\u01e1\u0001\u0000\u0000\u0000t\u01ed\u0001\u0000\u0000\u0000"+
		"v\u01f5\u0001\u0000\u0000\u0000x\u0202\u0001\u0000\u0000\u0000z\u020e"+
		"\u0001\u0000\u0000\u0000|\u0216\u0001\u0000\u0000\u0000~\u0219\u0001\u0000"+
		"\u0000\u0000\u0080\u021f\u0001\u0000\u0000\u0000\u0082\u0228\u0001\u0000"+
		"\u0000\u0000\u0084\u022a\u0001\u0000\u0000\u0000\u0086\u0233\u0001\u0000"+
		"\u0000\u0000\u0088\u0241\u0001\u0000\u0000\u0000\u008a\u024a\u0001\u0000"+
		"\u0000\u0000\u008c\u024c\u0001\u0000\u0000\u0000\u008e\u024e\u0001\u0000"+
		"\u0000\u0000\u0090\u0250\u0001\u0000\u0000\u0000\u0092\u0254\u0001\u0000"+
		"\u0000\u0000\u0094\u025a\u0001\u0000\u0000\u0000\u0096\u025c\u0001\u0000"+
		"\u0000\u0000\u0098\u0267\u0001\u0000\u0000\u0000\u009a\u0269\u0001\u0000"+
		"\u0000\u0000\u009c\u026d\u0001\u0000\u0000\u0000\u009e\u026f\u0001\u0000"+
		"\u0000\u0000\u00a0\u0271\u0001\u0000\u0000\u0000\u00a2\u0273\u0001\u0000"+
		"\u0000\u0000\u00a4\u0275\u0001\u0000\u0000\u0000\u00a6\u0277\u0001\u0000"+
		"\u0000\u0000\u00a8\u027f\u0001\u0000\u0000\u0000\u00aa\u0281\u0001\u0000"+
		"\u0000\u0000\u00ac\u0283\u0001\u0000\u0000\u0000\u00ae\u0285\u0001\u0000"+
		"\u0000\u0000\u00b0\u0287\u0001\u0000\u0000\u0000\u00b2\u0289\u0001\u0000"+
		"\u0000\u0000\u00b4\u028b\u0001\u0000\u0000\u0000\u00b6\u028e\u0001\u0000"+
		"\u0000\u0000\u00b8\u0295\u0001\u0000\u0000\u0000\u00ba\u0297\u0001\u0000"+
		"\u0000\u0000\u00bc\u0299\u0001\u0000\u0000\u0000\u00be\u029b\u0001\u0000"+
		"\u0000\u0000\u00c0\u02a0\u0001\u0000\u0000\u0000\u00c2\u02b9\u0001\u0000"+
		"\u0000\u0000\u00c4\u02bd\u0001\u0000\u0000\u0000\u00c6\u02bf\u0001\u0000"+
		"\u0000\u0000\u00c8\u02c3\u0001\u0000\u0000\u0000\u00ca\u02cc\u0001\u0000"+
		"\u0000\u0000\u00cc\u02d5\u0001\u0000\u0000\u0000\u00ce\u02de\u0001\u0000"+
		"\u0000\u0000\u00d0\u02e2\u0001\u0000\u0000\u0000\u00d2\u02e4\u0001\u0000"+
		"\u0000\u0000\u00d4\u02e6\u0001\u0000\u0000\u0000\u00d6\u02ed\u0001\u0000"+
		"\u0000\u0000\u00d8\u02ef\u0001\u0000\u0000\u0000\u00da\u02f9\u0001\u0000"+
		"\u0000\u0000\u00dc\u02fb\u0001\u0000\u0000\u0000\u00de\u02fd\u0001\u0000"+
		"\u0000\u0000\u00e0\u02ff\u0001\u0000\u0000\u0000\u00e2\u0301\u0001\u0000"+
		"\u0000\u0000\u00e4\u0303\u0001\u0000\u0000\u0000\u00e6\u0305\u0001\u0000"+
		"\u0000\u0000\u00e8\u0307\u0001\u0000\u0000\u0000\u00ea\u0309\u0001\u0000"+
		"\u0000\u0000\u00ec\u030b\u0001\u0000\u0000\u0000\u00ee\u00f3\u0003\u0002"+
		"\u0001\u0000\u00ef\u00f3\u0003\u0006\u0003\u0000\u00f0\u00f3\u0003\n\u0005"+
		"\u0000\u00f1\u00f3\u0003\u0010\b\u0000\u00f2\u00ee\u0001\u0000\u0000\u0000"+
		"\u00f2\u00ef\u0001\u0000\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000"+
		"\u00f2\u00f1\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f5\u0003f3\u0000\u00f5\u0001\u0001\u0000\u0000\u0000\u00f6\u00f7"+
		"\u0003\u0004\u0002\u0000\u00f7\u0003\u0001\u0000\u0000\u0000\u00f8\u00f9"+
		"\u0005\u0001\u0000\u0000\u00f9\u0005\u0001\u0000\u0000\u0000\u00fa\u00fb"+
		"\u0003\b\u0004\u0000\u00fb\u00fc\u0003\u000e\u0007\u0000\u00fc\u00fd\u0003"+
		"h4\u0000\u00fd\u0007\u0001\u0000\u0000\u0000\u00fe\u00ff\u0005\u0002\u0000"+
		"\u0000\u00ff\t\u0001\u0000\u0000\u0000\u0100\u0101\u0003\f\u0006\u0000"+
		"\u0101\u0102\u0003\u000e\u0007\u0000\u0102\u0103\u0003h4\u0000\u0103\u000b"+
		"\u0001\u0000\u0000\u0000\u0104\u0105\u0005\u0003\u0000\u0000\u0105\r\u0001"+
		"\u0000\u0000\u0000\u0106\u0107\u0003\u00dam\u0000\u0107\u000f\u0001\u0000"+
		"\u0000\u0000\u0108\u0109\u0003\u001a\r\u0000\u0109\u010a\u0003\u0012\t"+
		"\u0000\u010a\u010c\u0003.\u0017\u0000\u010b\u010d\u0003h4\u0000\u010c"+
		"\u010b\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d"+
		"\u010f\u0001\u0000\u0000\u0000\u010e\u0110\u0003B!\u0000\u010f\u010e\u0001"+
		"\u0000\u0000\u0000\u010f\u0110\u0001\u0000\u0000\u0000\u0110\u0112\u0001"+
		"\u0000\u0000\u0000\u0111\u0113\u0003F#\u0000\u0112\u0111\u0001\u0000\u0000"+
		"\u0000\u0112\u0113\u0001\u0000\u0000\u0000\u0113\u0115\u0001\u0000\u0000"+
		"\u0000\u0114\u0116\u0003R)\u0000\u0115\u0114\u0001\u0000\u0000\u0000\u0115"+
		"\u0116\u0001\u0000\u0000\u0000\u0116\u0118\u0001\u0000\u0000\u0000\u0117"+
		"\u0119\u0003V+\u0000\u0118\u0117\u0001\u0000\u0000\u0000\u0118\u0119\u0001"+
		"\u0000\u0000\u0000\u0119\u0011\u0001\u0000\u0000\u0000\u011a\u0120\u0003"+
		"\u0014\n\u0000\u011b\u011c\u0003\u00e8t\u0000\u011c\u011d\u0003\u0014"+
		"\n\u0000\u011d\u011f\u0001\u0000\u0000\u0000\u011e\u011b\u0001\u0000\u0000"+
		"\u0000\u011f\u0122\u0001\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000"+
		"\u0000\u0120\u0121\u0001\u0000\u0000\u0000\u0121\u0013\u0001\u0000\u0000"+
		"\u0000\u0122\u0120\u0001\u0000\u0000\u0000\u0123\u0127\u0003\u0016\u000b"+
		"\u0000\u0124\u0127\u0003\u0018\f\u0000\u0125\u0127\u0003\u001e\u000f\u0000"+
		"\u0126\u0123\u0001\u0000\u0000\u0000\u0126\u0124\u0001\u0000\u0000\u0000"+
		"\u0126\u0125\u0001\u0000\u0000\u0000\u0127\u0015\u0001\u0000\u0000\u0000"+
		"\u0128\u012e\u0003\u001c\u000e\u0000\u0129\u012a\u0003\u00dcn\u0000\u012a"+
		"\u012b\u0003\u00e4r\u0000\u012b\u012c\u0003\u001c\u000e\u0000\u012c\u012e"+
		"\u0001\u0000\u0000\u0000\u012d\u0128\u0001\u0000\u0000\u0000\u012d\u0129"+
		"\u0001\u0000\u0000\u0000\u012e\u0017\u0001\u0000\u0000\u0000\u012f\u0130"+
		"\u0003\u00d6k\u0000\u0130\u0019\u0001\u0000\u0000\u0000\u0131\u0132\u0005"+
		"\u0004\u0000\u0000\u0132\u001b\u0001\u0000\u0000\u0000\u0133\u0134\u0005"+
		"\u0005\u0000\u0000\u0134\u001d\u0001\u0000\u0000\u0000\u0135\u0136\u0003"+
		" \u0010\u0000\u0136\u0137\u0003\u00e0p\u0000\u0137\u0138\u0003\"\u0011"+
		"\u0000\u0138\u0139\u0003\u00e2q\u0000\u0139\u001f\u0001\u0000\u0000\u0000"+
		"\u013a\u0140\u0003$\u0012\u0000\u013b\u0140\u0003&\u0013\u0000\u013c\u0140"+
		"\u0003(\u0014\u0000\u013d\u0140\u0003*\u0015\u0000\u013e\u0140\u0003,"+
		"\u0016\u0000\u013f\u013a\u0001\u0000\u0000\u0000\u013f\u013b\u0001\u0000"+
		"\u0000\u0000\u013f\u013c\u0001\u0000\u0000\u0000\u013f\u013d\u0001\u0000"+
		"\u0000\u0000\u013f\u013e\u0001\u0000\u0000\u0000\u0140!\u0001\u0000\u0000"+
		"\u0000\u0141\u0145\u0003\u00d6k\u0000\u0142\u0145\u0003\u00ecv\u0000\u0143"+
		"\u0145\u0003\u001c\u000e\u0000\u0144\u0141\u0001\u0000\u0000\u0000\u0144"+
		"\u0142\u0001\u0000\u0000\u0000\u0144\u0143\u0001\u0000\u0000\u0000\u0145"+
		"#\u0001\u0000\u0000\u0000\u0146\u0147\u0005\u0007\u0000\u0000\u0147%\u0001"+
		"\u0000\u0000\u0000\u0148\u0149\u0005\b\u0000\u0000\u0149\'\u0001\u0000"+
		"\u0000\u0000\u014a\u014b\u0005\t\u0000\u0000\u014b)\u0001\u0000\u0000"+
		"\u0000\u014c\u014d\u0005\n\u0000\u0000\u014d+\u0001\u0000\u0000\u0000"+
		"\u014e\u014f\u0005\u0006\u0000\u0000\u014f-\u0001\u0000\u0000\u0000\u0150"+
		"\u0151\u00038\u001c\u0000\u0151\u0155\u00030\u0018\u0000\u0152\u0154\u0003"+
		"2\u0019\u0000\u0153\u0152\u0001\u0000\u0000\u0000\u0154\u0157\u0001\u0000"+
		"\u0000\u0000\u0155\u0153\u0001\u0000\u0000\u0000\u0155\u0156\u0001\u0000"+
		"\u0000\u0000\u0156/\u0001\u0000\u0000\u0000\u0157\u0155\u0001\u0000\u0000"+
		"\u0000\u0158\u015a\u00034\u001a\u0000\u0159\u015b\u00036\u001b\u0000\u015a"+
		"\u0159\u0001\u0000\u0000\u0000\u015a\u015b\u0001\u0000\u0000\u0000\u015b"+
		"1\u0001\u0000\u0000\u0000\u015c\u015d\u0003:\u001d\u0000\u015d\u015f\u0003"+
		"4\u001a\u0000\u015e\u0160\u00036\u001b\u0000\u015f\u015e\u0001\u0000\u0000"+
		"\u0000\u015f\u0160\u0001\u0000\u0000\u0000\u0160\u0161\u0001\u0000\u0000"+
		"\u0000\u0161\u0162\u0003<\u001e\u0000\u0162\u0163\u0003>\u001f\u0000\u0163"+
		"3\u0001\u0000\u0000\u0000\u0164\u0165\u0003\u00dam\u0000\u01655\u0001"+
		"\u0000\u0000\u0000\u0166\u0167\u0003\u00dcn\u0000\u01677\u0001\u0000\u0000"+
		"\u0000\u0168\u0169\u0005\u000b\u0000\u0000\u01699\u0001\u0000\u0000\u0000"+
		"\u016a\u016b\u0005\f\u0000\u0000\u016b\u016c\u0005\r\u0000\u0000\u016c"+
		";\u0001\u0000\u0000\u0000\u016d\u016e\u0005\u000e\u0000\u0000\u016e=\u0001"+
		"\u0000\u0000\u0000\u016f\u0170\u0003\u00dcn\u0000\u0170\u0171\u0003@ "+
		"\u0000\u0171\u0172\u0003\u00dcn\u0000\u0172?\u0001\u0000\u0000\u0000\u0173"+
		"\u0174\u0005\u0016\u0000\u0000\u0174A\u0001\u0000\u0000\u0000\u0175\u0176"+
		"\u0003`0\u0000\u0176\u0177\u0003D\"\u0000\u0177C\u0001\u0000\u0000\u0000"+
		"\u0178\u017e\u0003\u00d6k\u0000\u0179\u017a\u0003\u00e8t\u0000\u017a\u017b"+
		"\u0003\u00d6k\u0000\u017b\u017d\u0001\u0000\u0000\u0000\u017c\u0179\u0001"+
		"\u0000\u0000\u0000\u017d\u0180\u0001\u0000\u0000\u0000\u017e\u017c\u0001"+
		"\u0000\u0000\u0000\u017e\u017f\u0001\u0000\u0000\u0000\u017fE\u0001\u0000"+
		"\u0000\u0000\u0180\u017e\u0001\u0000\u0000\u0000\u0181\u0182\u0003^/\u0000"+
		"\u0182\u0183\u0003H$\u0000\u0183G\u0001\u0000\u0000\u0000\u0184\u018a"+
		"\u0003J%\u0000\u0185\u0186\u0003\u00a2Q\u0000\u0186\u0187\u0003J%\u0000"+
		"\u0187\u0189\u0001\u0000\u0000\u0000\u0188\u0185\u0001\u0000\u0000\u0000"+
		"\u0189\u018c\u0001\u0000\u0000\u0000\u018a\u0188\u0001\u0000\u0000\u0000"+
		"\u018a\u018b\u0001\u0000\u0000\u0000\u018bI\u0001\u0000\u0000\u0000\u018c"+
		"\u018a\u0001\u0000\u0000\u0000\u018d\u0193\u0003L&\u0000\u018e\u018f\u0003"+
		"\u00a0P\u0000\u018f\u0190\u0003L&\u0000\u0190\u0192\u0001\u0000\u0000"+
		"\u0000\u0191\u018e\u0001\u0000\u0000\u0000\u0192\u0195\u0001\u0000\u0000"+
		"\u0000\u0193\u0191\u0001\u0000\u0000\u0000\u0193\u0194\u0001\u0000\u0000"+
		"\u0000\u0194K\u0001\u0000\u0000\u0000\u0195\u0193\u0001\u0000\u0000\u0000"+
		"\u0196\u019c\u0003N\'\u0000\u0197\u0198\u0003\u00e0p\u0000\u0198\u0199"+
		"\u0003H$\u0000\u0199\u019a\u0003\u00e2q\u0000\u019a\u019c\u0001\u0000"+
		"\u0000\u0000\u019b\u0196\u0001\u0000\u0000\u0000\u019b\u0197\u0001\u0000"+
		"\u0000\u0000\u019cM\u0001\u0000\u0000\u0000\u019d\u019e\u0003\u001e\u000f"+
		"\u0000\u019e\u019f\u0003\u00a8T\u0000\u019f\u01a0\u0003P(\u0000\u01a0"+
		"O\u0001\u0000\u0000\u0000\u01a1\u01a4\u0003\u00d8l\u0000\u01a2\u01a4\u0003"+
		"\u00ecv\u0000\u01a3\u01a1\u0001\u0000\u0000\u0000\u01a3\u01a2\u0001\u0000"+
		"\u0000\u0000\u01a4Q\u0001\u0000\u0000\u0000\u01a5\u01a6\u0003b1\u0000"+
		"\u01a6\u01ac\u0003T*\u0000\u01a7\u01a8\u0003\u00e8t\u0000\u01a8\u01a9"+
		"\u0003T*\u0000\u01a9\u01ab\u0001\u0000\u0000\u0000\u01aa\u01a7\u0001\u0000"+
		"\u0000\u0000\u01ab\u01ae\u0001\u0000\u0000\u0000\u01ac\u01aa\u0001\u0000"+
		"\u0000\u0000\u01ac\u01ad\u0001\u0000\u0000\u0000\u01adS\u0001\u0000\u0000"+
		"\u0000\u01ae\u01ac\u0001\u0000\u0000\u0000\u01af\u01b1\u0003\u00d6k\u0000"+
		"\u01b0\u01b2\u0003d2\u0000\u01b1\u01b0\u0001\u0000\u0000\u0000\u01b1\u01b2"+
		"\u0001\u0000\u0000\u0000\u01b2U\u0001\u0000\u0000\u0000\u01b3\u01b4\u0003"+
		"X,\u0000\u01b4\u01b5\u0003Z-\u0000\u01b5\u01b6\u0003\u00e8t\u0000\u01b6"+
		"\u01b7\u0003\\.\u0000\u01b7W\u0001\u0000\u0000\u0000\u01b8\u01b9\u0005"+
		"&\u0000\u0000\u01b9Y\u0001\u0000\u0000\u0000\u01ba\u01bb\u0005:\u0000"+
		"\u0000\u01bb[\u0001\u0000\u0000\u0000\u01bc\u01bd\u0005:\u0000\u0000\u01bd"+
		"]\u0001\u0000\u0000\u0000\u01be\u01bf\u0005#\u0000\u0000\u01bf_\u0001"+
		"\u0000\u0000\u0000\u01c0\u01c1\u0005\"\u0000\u0000\u01c1a\u0001\u0000"+
		"\u0000\u0000\u01c2\u01c3\u0005$\u0000\u0000\u01c3c\u0001\u0000\u0000\u0000"+
		"\u01c4\u01c5\u0005%\u0000\u0000\u01c5e\u0001\u0000\u0000\u0000\u01c6\u01c7"+
		"\u0005\u0000\u0000\u0001\u01c7g\u0001\u0000\u0000\u0000\u01c8\u01c9\u0003"+
		"\u009eO\u0000\u01c9\u01ca\u0003j5\u0000\u01cai\u0001\u0000\u0000\u0000"+
		"\u01cb\u01cd\u0003l6\u0000\u01cc\u01cb\u0001\u0000\u0000\u0000\u01cd\u01ce"+
		"\u0001\u0000\u0000\u0000\u01ce\u01cc\u0001\u0000\u0000\u0000\u01ce\u01cf"+
		"\u0001\u0000\u0000\u0000\u01cfk\u0001\u0000\u0000\u0000\u01d0\u01d3\u0003"+
		"\u00a0P\u0000\u01d1\u01d3\u0003\u00a2Q\u0000\u01d2\u01d0\u0001\u0000\u0000"+
		"\u0000\u01d2\u01d1\u0001\u0000\u0000\u0000\u01d2\u01d3\u0001\u0000\u0000"+
		"\u0000\u01d3\u01d4\u0001\u0000\u0000\u0000\u01d4\u01d5\u0003n7\u0000\u01d5"+
		"m\u0001\u0000\u0000\u0000\u01d6\u01dc\u0003\u0096K\u0000\u01d7\u01dc\u0003"+
		"p8\u0000\u01d8\u01dc\u0003r9\u0000\u01d9\u01dc\u0003t:\u0000\u01da\u01dc"+
		"\u0003v;\u0000\u01db\u01d6\u0001\u0000\u0000\u0000\u01db\u01d7\u0001\u0000"+
		"\u0000\u0000\u01db\u01d8\u0001\u0000\u0000\u0000\u01db\u01d9\u0001\u0000"+
		"\u0000\u0000\u01db\u01da\u0001\u0000\u0000\u0000\u01dco\u0001\u0000\u0000"+
		"\u0000\u01dd\u01de\u0003\u00e0p\u0000\u01de\u01df\u0003j5\u0000\u01df"+
		"\u01e0\u0003\u00e2q\u0000\u01e0q\u0001\u0000\u0000\u0000\u01e1\u01e2\u0005"+
		"-\u0000\u0000\u01e2\u01e3\u00053\u0000\u0000\u01e3\u01e4\u0003\u00e0p"+
		"\u0000\u01e4\u01e5\u0003\u0084B\u0000\u01e5\u01e6\u0003\u00e2q\u0000\u01e6"+
		"\u01e8\u0003\u00a4R\u0000\u01e7\u01e9\u0003|>\u0000\u01e8\u01e7\u0001"+
		"\u0000\u0000\u0000\u01e8\u01e9\u0001\u0000\u0000\u0000\u01e9\u01ea\u0001"+
		"\u0000\u0000\u0000\u01ea\u01eb\u0003~?\u0000\u01eb\u01ec\u0003\u00a6S"+
		"\u0000\u01ecs\u0001\u0000\u0000\u0000\u01ed\u01ee\u0005-\u0000\u0000\u01ee"+
		"\u01f0\u0005.\u0000\u0000\u01ef\u01f1\u0003|>\u0000\u01f0\u01ef\u0001"+
		"\u0000\u0000\u0000\u01f0\u01f1\u0001\u0000\u0000\u0000\u01f1\u01f2\u0001"+
		"\u0000\u0000\u0000\u01f2\u01f3\u0003~?\u0000\u01f3\u01f4\u0005/\u0000"+
		"\u0000\u01f4u\u0001\u0000\u0000\u0000\u01f5\u01f6\u0005-\u0000\u0000\u01f6"+
		"\u01f7\u00056\u0000\u0000\u01f7\u01f9\u0005.\u0000\u0000\u01f8\u01fa\u0003"+
		"x<\u0000\u01f9\u01f8\u0001\u0000\u0000\u0000\u01fa\u01fb\u0001\u0000\u0000"+
		"\u0000\u01fb\u01f9\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000"+
		"\u0000\u01fc\u01fe\u0001\u0000\u0000\u0000\u01fd\u01ff\u0003z=\u0000\u01fe"+
		"\u01fd\u0001\u0000\u0000\u0000\u01fe\u01ff\u0001\u0000\u0000\u0000\u01ff"+
		"\u0200\u0001\u0000\u0000\u0000\u0200\u0201\u0005/\u0000\u0000\u0201w\u0001"+
		"\u0000\u0000\u0000\u0202\u0203\u0005-\u0000\u0000\u0203\u0204\u00054\u0000"+
		"\u0000\u0204\u0205\u0003\u00e0p\u0000\u0205\u0206\u0003\u0084B\u0000\u0206"+
		"\u0207\u0003\u00e2q\u0000\u0207\u0209\u0003\u00a4R\u0000\u0208\u020a\u0003"+
		"|>\u0000\u0209\u0208\u0001\u0000\u0000\u0000\u0209\u020a\u0001\u0000\u0000"+
		"\u0000\u020a\u020b\u0001\u0000\u0000\u0000\u020b\u020c\u0003~?\u0000\u020c"+
		"\u020d\u0003\u00a6S\u0000\u020dy\u0001\u0000\u0000\u0000\u020e\u020f\u0005"+
		"-\u0000\u0000\u020f\u0210\u00055\u0000\u0000\u0210\u0211\u0005.\u0000"+
		"\u0000\u0211\u0212\u0003~?\u0000\u0212\u0213\u0005/\u0000\u0000\u0213"+
		"{\u0001\u0000\u0000\u0000\u0214\u0217\u0003\u00a0P\u0000\u0215\u0217\u0003"+
		"\u00a2Q\u0000\u0216\u0214\u0001\u0000\u0000\u0000\u0216\u0215\u0001\u0000"+
		"\u0000\u0000\u0217}\u0001\u0000\u0000\u0000\u0218\u021a\u0003\u0080@\u0000"+
		"\u0219\u0218\u0001\u0000\u0000\u0000\u021a\u021b\u0001\u0000\u0000\u0000"+
		"\u021b\u0219\u0001\u0000\u0000\u0000\u021b\u021c\u0001\u0000\u0000\u0000"+
		"\u021c\u007f\u0001\u0000\u0000\u0000\u021d\u0220\u0003\u00a0P\u0000\u021e"+
		"\u0220\u0003\u00a2Q\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u021f\u021e"+
		"\u0001\u0000\u0000\u0000\u021f\u0220\u0001\u0000\u0000\u0000\u0220\u0221"+
		"\u0001\u0000\u0000\u0000\u0221\u0222\u0003\u0082A\u0000\u0222\u0081\u0001"+
		"\u0000\u0000\u0000\u0223\u0229\u0003\u0096K\u0000\u0224\u0225\u0003\u00e0"+
		"p\u0000\u0225\u0226\u0003~?\u0000\u0226\u0227\u0003\u00e2q\u0000\u0227"+
		"\u0229\u0001\u0000\u0000\u0000\u0228\u0223\u0001\u0000\u0000\u0000\u0228"+
		"\u0224\u0001\u0000\u0000\u0000\u0229\u0083\u0001\u0000\u0000\u0000\u022a"+
		"\u0230\u0003\u0086C\u0000\u022b\u022c\u0003\u008cF\u0000\u022c\u022d\u0003"+
		"\u0086C\u0000\u022d\u022f\u0001\u0000\u0000\u0000\u022e\u022b\u0001\u0000"+
		"\u0000\u0000\u022f\u0232\u0001\u0000\u0000\u0000\u0230\u022e\u0001\u0000"+
		"\u0000\u0000\u0230\u0231\u0001\u0000\u0000\u0000\u0231\u0085\u0001\u0000"+
		"\u0000\u0000\u0232\u0230\u0001\u0000\u0000\u0000\u0233\u0239\u0003\u0088"+
		"D\u0000\u0234\u0235\u0003\u008eG\u0000\u0235\u0236\u0003\u0088D\u0000"+
		"\u0236\u0238\u0001\u0000\u0000\u0000\u0237\u0234\u0001\u0000\u0000\u0000"+
		"\u0238\u023b\u0001\u0000\u0000\u0000\u0239\u0237\u0001\u0000\u0000\u0000"+
		"\u0239\u023a\u0001\u0000\u0000\u0000\u023a\u0087\u0001\u0000\u0000\u0000"+
		"\u023b\u0239\u0001\u0000\u0000\u0000\u023c\u0242\u0003\u008aE\u0000\u023d"+
		"\u023e\u0003\u00e0p\u0000\u023e\u023f\u0003\u0084B\u0000\u023f\u0240\u0003"+
		"\u00e2q\u0000\u0240\u0242\u0001\u0000\u0000\u0000\u0241\u023c\u0001\u0000"+
		"\u0000\u0000\u0241\u023d\u0001\u0000\u0000\u0000\u0242\u0089\u0001\u0000"+
		"\u0000\u0000\u0243\u0244\u0003\u0094J\u0000\u0244\u0245\u0003\u0090H\u0000"+
		"\u0245\u0246\u0003\u0094J\u0000\u0246\u024b\u0001\u0000\u0000\u0000\u0247"+
		"\u0248\u0003\u0094J\u0000\u0248\u0249\u0003\u0092I\u0000\u0249\u024b\u0001"+
		"\u0000\u0000\u0000\u024a\u0243\u0001\u0000\u0000\u0000\u024a\u0247\u0001"+
		"\u0000\u0000\u0000\u024b\u008b\u0001\u0000\u0000\u0000\u024c\u024d\u0007"+
		"\u0000\u0000\u0000\u024d\u008d\u0001\u0000\u0000\u0000\u024e\u024f\u0007"+
		"\u0001\u0000\u0000\u024f\u008f\u0001\u0000\u0000\u0000\u0250\u0251\u0007"+
		"\u0002\u0000\u0000\u0251\u0091\u0001\u0000\u0000\u0000\u0252\u0255\u0003"+
		"\u00d2i\u0000\u0253\u0255\u0003\u00d4j\u0000\u0254\u0252\u0001\u0000\u0000"+
		"\u0000\u0254\u0253\u0001\u0000\u0000\u0000\u0255\u0093\u0001\u0000\u0000"+
		"\u0000\u0256\u025b\u0003\u00d6k\u0000\u0257\u025b\u0003\u00d8l\u0000\u0258"+
		"\u025b\u0003\u00ecv\u0000\u0259\u025b\u0005\u001b\u0000\u0000\u025a\u0256"+
		"\u0001\u0000\u0000\u0000\u025a\u0257\u0001\u0000\u0000\u0000\u025a\u0258"+
		"\u0001\u0000\u0000\u0000\u025a\u0259\u0001\u0000\u0000\u0000\u025b\u0095"+
		"\u0001\u0000\u0000\u0000\u025c\u025f\u0003\u00d6k\u0000\u025d\u0260\u0003"+
		"\u0098L\u0000\u025e\u0260\u0003\u009aM\u0000\u025f\u025d\u0001\u0000\u0000"+
		"\u0000\u025f\u025e\u0001\u0000\u0000\u0000\u0260\u0097\u0001\u0000\u0000"+
		"\u0000\u0261\u0262\u0003\u00a8T\u0000\u0262\u0263\u0003\u009cN\u0000\u0263"+
		"\u0268\u0001\u0000\u0000\u0000\u0264\u0265\u0003\u00b6[\u0000\u0265\u0266"+
		"\u0003\u00c0`\u0000\u0266\u0268\u0001\u0000\u0000\u0000\u0267\u0261\u0001"+
		"\u0000\u0000\u0000\u0267\u0264\u0001\u0000\u0000\u0000\u0268\u0099\u0001"+
		"\u0000\u0000\u0000\u0269\u026a\u0003\u00d0h\u0000\u026a\u009b\u0001\u0000"+
		"\u0000\u0000\u026b\u026e\u0003\u00d8l\u0000\u026c\u026e\u0003\u00ecv\u0000"+
		"\u026d\u026b\u0001\u0000\u0000\u0000\u026d\u026c\u0001\u0000\u0000\u0000"+
		"\u026e\u009d\u0001\u0000\u0000\u0000\u026f\u0270\u0005\u000f\u0000\u0000"+
		"\u0270\u009f\u0001\u0000\u0000\u0000\u0271\u0272\u0005\u0010\u0000\u0000"+
		"\u0272\u00a1\u0001\u0000\u0000\u0000\u0273\u0274\u0005\u0011\u0000\u0000"+
		"\u0274\u00a3\u0001\u0000\u0000\u0000\u0275\u0276\u0005.\u0000\u0000\u0276"+
		"\u00a5\u0001\u0000\u0000\u0000\u0277\u0278\u0005/\u0000\u0000\u0278\u00a7"+
		"\u0001\u0000\u0000\u0000\u0279\u0280\u0003\u00aaU\u0000\u027a\u0280\u0003"+
		"\u00acV\u0000\u027b\u0280\u0003\u00aeW\u0000\u027c\u0280\u0003\u00b0X"+
		"\u0000\u027d\u0280\u0003\u00b2Y\u0000\u027e\u0280\u0003\u00b4Z\u0000\u027f"+
		"\u0279\u0001\u0000\u0000\u0000\u027f\u027a\u0001\u0000\u0000\u0000\u027f"+
		"\u027b\u0001\u0000\u0000\u0000\u027f\u027c\u0001\u0000\u0000\u0000\u027f"+
		"\u027d\u0001\u0000\u0000\u0000\u027f\u027e\u0001\u0000\u0000\u0000\u0280"+
		"\u00a9\u0001\u0000\u0000\u0000\u0281\u0282\u0005\u0012\u0000\u0000\u0282"+
		"\u00ab\u0001\u0000\u0000\u0000\u0283\u0284\u0005\u0013\u0000\u0000\u0284"+
		"\u00ad\u0001\u0000\u0000\u0000\u0285\u0286\u0005\u0014\u0000\u0000\u0286"+
		"\u00af\u0001\u0000\u0000\u0000\u0287\u0288\u0005\u0015\u0000\u0000\u0288"+
		"\u00b1\u0001\u0000\u0000\u0000\u0289\u028a\u0005\u0016\u0000\u0000\u028a"+
		"\u00b3\u0001\u0000\u0000\u0000\u028b\u028c\u0005\u0017\u0000\u0000\u028c"+
		"\u00b5\u0001\u0000\u0000\u0000\u028d\u028f\u0003\u00b8\\\u0000\u028e\u028d"+
		"\u0001\u0000\u0000\u0000\u028e\u028f\u0001\u0000\u0000\u0000\u028f\u0293"+
		"\u0001\u0000\u0000\u0000\u0290\u0294\u0003\u00ba]\u0000\u0291\u0294\u0003"+
		"\u00bc^\u0000\u0292\u0294\u0003\u00be_\u0000\u0293\u0290\u0001\u0000\u0000"+
		"\u0000\u0293\u0291\u0001\u0000\u0000\u0000\u0293\u0292\u0001\u0000\u0000"+
		"\u0000\u0294\u00b7\u0001\u0000\u0000\u0000\u0295\u0296\u0005\u001c\u0000"+
		"\u0000\u0296\u00b9\u0001\u0000\u0000\u0000\u0297\u0298\u0005\u001d\u0000"+
		"\u0000\u0298\u00bb\u0001\u0000\u0000\u0000\u0299\u029a\u0005\u001e\u0000"+
		"\u0000\u029a\u00bd\u0001\u0000\u0000\u0000\u029b\u029c\u0005\u001f\u0000"+
		"\u0000\u029c\u00bf\u0001\u0000\u0000\u0000\u029d\u02a1\u0003\u00c4b\u0000"+
		"\u029e\u02a1\u0003\u00c2a\u0000\u029f\u02a1\u0003\u00d8l\u0000\u02a0\u029d"+
		"\u0001\u0000\u0000\u0000\u02a0\u029e\u0001\u0000\u0000\u0000\u02a0\u029f"+
		"\u0001\u0000\u0000\u0000\u02a1\u00c1\u0001\u0000\u0000\u0000\u02a2\u02a4"+
		"\u00052\u0000\u0000\u02a3\u02a2\u0001\u0000\u0000\u0000\u02a4\u02a5\u0001"+
		"\u0000\u0000\u0000\u02a5\u02a3\u0001\u0000\u0000\u0000\u02a5\u02a6\u0001"+
		"\u0000\u0000\u0000\u02a6\u02a7\u0001\u0000\u0000\u0000\u02a7\u02ba\u0003"+
		"\u00d8l\u0000\u02a8\u02aa\u0003\u00d8l\u0000\u02a9\u02ab\u00052\u0000"+
		"\u0000\u02aa\u02a9\u0001\u0000\u0000\u0000\u02ab\u02ac\u0001\u0000\u0000"+
		"\u0000\u02ac\u02aa\u0001\u0000\u0000\u0000\u02ac\u02ad\u0001\u0000\u0000"+
		"\u0000\u02ad\u02ba\u0001\u0000\u0000\u0000\u02ae\u02b0\u00052\u0000\u0000"+
		"\u02af\u02ae\u0001\u0000\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000"+
		"\u02b1\u02af\u0001\u0000\u0000\u0000\u02b1\u02b2\u0001\u0000\u0000\u0000"+
		"\u02b2\u02b3\u0001\u0000\u0000\u0000\u02b3\u02b5\u0003\u00d8l\u0000\u02b4"+
		"\u02b6\u00052\u0000\u0000\u02b5\u02b4\u0001\u0000\u0000\u0000\u02b6\u02b7"+
		"\u0001\u0000\u0000\u0000\u02b7\u02b5\u0001\u0000\u0000\u0000\u02b7\u02b8"+
		"\u0001\u0000\u0000\u0000\u02b8\u02ba\u0001\u0000\u0000\u0000\u02b9\u02a3"+
		"\u0001\u0000\u0000\u0000\u02b9\u02a8\u0001\u0000\u0000\u0000\u02b9\u02af"+
		"\u0001\u0000\u0000\u0000\u02ba\u00c3\u0001\u0000\u0000\u0000\u02bb\u02be"+
		"\u0003\u00c6c\u0000\u02bc\u02be\u0003\u00c8d\u0000\u02bd\u02bb\u0001\u0000"+
		"\u0000\u0000\u02bd\u02bc\u0001\u0000\u0000\u0000\u02be\u00c5\u0001\u0000"+
		"\u0000\u0000\u02bf\u02c0\u0003\u00e0p\u0000\u02c0\u02c1\u0003\u00d8l\u0000"+
		"\u02c1\u02c2\u0003\u00e2q\u0000\u02c2\u00c7\u0001\u0000\u0000\u0000\u02c3"+
		"\u02c4\u0003\u00e0p\u0000\u02c4\u02c5\u0003\u00ceg\u0000\u02c5\u02c6\u0003"+
		"\u00e6s\u0000\u02c6\u02c7\u0003\u00cae\u0000\u02c7\u02c8\u0003\u00e2q"+
		"\u0000\u02c8\u02c9\u00050\u0000\u0000\u02c9\u02ca\u00051\u0000\u0000\u02ca"+
		"\u02cb\u0003\u00ccf\u0000\u02cb\u00c9\u0001\u0000\u0000\u0000\u02cc\u02d2"+
		"\u0003\u00deo\u0000\u02cd\u02ce\u0003\u00e4r\u0000\u02ce\u02cf\u0003\u00de"+
		"o\u0000\u02cf\u02d1\u0001\u0000\u0000\u0000\u02d0\u02cd\u0001\u0000\u0000"+
		"\u0000\u02d1\u02d4\u0001\u0000\u0000\u0000\u02d2\u02d0\u0001\u0000\u0000"+
		"\u0000\u02d2\u02d3\u0001\u0000\u0000\u0000\u02d3\u00cb\u0001\u0000\u0000"+
		"\u0000\u02d4\u02d2\u0001\u0000\u0000\u0000\u02d5\u02db\u0003\u00deo\u0000"+
		"\u02d6\u02d7\u0003\u00e4r\u0000\u02d7\u02d8\u0003\u00deo\u0000\u02d8\u02da"+
		"\u0001\u0000\u0000\u0000\u02d9\u02d6\u0001\u0000\u0000\u0000\u02da\u02dd"+
		"\u0001\u0000\u0000\u0000\u02db\u02d9\u0001\u0000\u0000\u0000\u02db\u02dc"+
		"\u0001\u0000\u0000\u0000\u02dc\u00cd\u0001\u0000\u0000\u0000\u02dd\u02db"+
		"\u0001\u0000\u0000\u0000\u02de\u02df\u00059\u0000\u0000\u02df\u00cf\u0001"+
		"\u0000\u0000\u0000\u02e0\u02e3\u0003\u00d2i\u0000\u02e1\u02e3\u0003\u00d4"+
		"j\u0000\u02e2\u02e0\u0001\u0000\u0000\u0000\u02e2\u02e1\u0001\u0000\u0000"+
		"\u0000\u02e3\u00d1\u0001\u0000\u0000\u0000\u02e4\u02e5\u0005 \u0000\u0000"+
		"\u02e5\u00d3\u0001\u0000\u0000\u0000\u02e6\u02e7\u0005!\u0000\u0000\u02e7"+
		"\u00d5\u0001\u0000\u0000\u0000\u02e8\u02ee\u0003\u00deo\u0000\u02e9\u02ea"+
		"\u0003\u00dcn\u0000\u02ea\u02eb\u0003\u00e4r\u0000\u02eb\u02ec\u0003\u00de"+
		"o\u0000\u02ec\u02ee\u0001\u0000\u0000\u0000\u02ed\u02e8\u0001\u0000\u0000"+
		"\u0000\u02ed\u02e9\u0001\u0000\u0000\u0000\u02ee\u00d7\u0001\u0000\u0000"+
		"\u0000\u02ef\u02f0\u0003\u00e6s\u0000\u02f0\u02f6\u0003\u00deo\u0000\u02f1"+
		"\u02f2\u0003\u00e4r\u0000\u02f2\u02f3\u0003\u00deo\u0000\u02f3\u02f5\u0001"+
		"\u0000\u0000\u0000\u02f4\u02f1\u0001\u0000\u0000\u0000\u02f5\u02f8\u0001"+
		"\u0000\u0000\u0000\u02f6\u02f4\u0001\u0000\u0000\u0000\u02f6\u02f7\u0001"+
		"\u0000\u0000\u0000\u02f7\u00d9\u0001\u0000\u0000\u0000\u02f8\u02f6\u0001"+
		"\u0000\u0000\u0000\u02f9\u02fa\u00057\u0000\u0000\u02fa\u00db\u0001\u0000"+
		"\u0000\u0000\u02fb\u02fc\u0007\u0003\u0000\u0000\u02fc\u00dd\u0001\u0000"+
		"\u0000\u0000\u02fd\u02fe\u0007\u0003\u0000\u0000\u02fe\u00df\u0001\u0000"+
		"\u0000\u0000\u02ff\u0300\u0005\'\u0000\u0000\u0300\u00e1\u0001\u0000\u0000"+
		"\u0000\u0301\u0302\u0005(\u0000\u0000\u0302\u00e3\u0001\u0000\u0000\u0000"+
		"\u0303\u0304\u0005+\u0000\u0000\u0304\u00e5\u0001\u0000\u0000\u0000\u0305"+
		"\u0306\u0005*\u0000\u0000\u0306\u00e7\u0001\u0000\u0000\u0000\u0307\u0308"+
		"\u0005)\u0000\u0000\u0308\u00e9\u0001\u0000\u0000\u0000\u0309\u030a\u0005"+
		",\u0000\u0000\u030a\u00eb\u0001\u0000\u0000\u0000\u030b\u030c\u0005:\u0000"+
		"\u0000\u030c\u00ed\u0001\u0000\u0000\u00009\u00f2\u010c\u010f\u0112\u0115"+
		"\u0118\u0120\u0126\u012d\u013f\u0144\u0155\u015a\u015f\u017e\u018a\u0193"+
		"\u019b\u01a3\u01ac\u01b1\u01ce\u01d2\u01db\u01e8\u01f0\u01fb\u01fe\u0209"+
		"\u0216\u021b\u021f\u0228\u0230\u0239\u0241\u024a\u0254\u025a\u025f\u0267"+
		"\u026d\u027f\u028e\u0293\u02a0\u02a5\u02ac\u02b1\u02b7\u02b9\u02bd\u02d2"+
		"\u02db\u02e2\u02ed\u02f6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}