// Generated from F:/devops/mybatisgx/mybatisx/mybatisx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.mybatisgx.syntax;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MethodNameParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INSERT_ACTION=1, DELETE_ACTION=2, UPDATE_ACTION=3, SELECT_ACTION=4, BY=5, 
		AND=6, OR=7, COMPARISON_OP=8, GROUP_BY_OP=9, ORDER_BY_OP=10, ORDER_BY_OP_DIRECTION=11, 
		AGGREGATE_FUNCTION=12, LEFT_BRACKET=13, RIGHT_BRACKET=14, RESERVED_WORD=15, 
		FIELD=16, WS=17;
	public static final int
		RULE_sql_statement = 0, RULE_insert_statement = 1, RULE_insert_clause = 2, 
		RULE_delete_statement = 3, RULE_delete_clause = 4, RULE_update_statement = 5, 
		RULE_update_clause = 6, RULE_select_statement = 7, RULE_select_clause = 8, 
		RULE_aggregate_operation_clause = 9, RULE_where_clause = 10, RULE_condition_group_clause = 11, 
		RULE_condition_item_clause = 12, RULE_field_comparison_op_clause = 13, 
		RULE_group_by_clause = 14, RULE_order_by_clause = 15, RULE_ignore_reserved_word_clause = 16, 
		RULE_where_start_clause = 17, RULE_logic_op_clause = 18, RULE_logic_op_and_clause = 19, 
		RULE_logic_op_or_clause = 20, RULE_comparison_op_clause = 21, RULE_group_by_op_clause = 22, 
		RULE_order_by_op_clause = 23, RULE_order_by_op_direction_clause = 24, 
		RULE_aggregate_function_clause = 25, RULE_field_clause = 26, RULE_left_bracket_clause = 27, 
		RULE_right_bracket_clause = 28, RULE_end = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "select_statement", 
			"select_clause", "aggregate_operation_clause", "where_clause", "condition_group_clause", 
			"condition_item_clause", "field_comparison_op_clause", "group_by_clause", 
			"order_by_clause", "ignore_reserved_word_clause", "where_start_clause", 
			"logic_op_clause", "logic_op_and_clause", "logic_op_or_clause", "comparison_op_clause", 
			"group_by_op_clause", "order_by_op_clause", "order_by_op_direction_clause", 
			"aggregate_function_clause", "field_clause", "left_bracket_clause", "right_bracket_clause", 
			"end"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'By'", "'And'", "'Or'", null, "'GroupBy'", 
			"'OrderBy'", null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"BY", "AND", "OR", "COMPARISON_OP", "GROUP_BY_OP", "ORDER_BY_OP", "ORDER_BY_OP_DIRECTION", 
			"AGGREGATE_FUNCTION", "LEFT_BRACKET", "RIGHT_BRACKET", "RESERVED_WORD", 
			"FIELD", "WS"
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
	public String getGrammarFileName() { return "MethodNameParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MethodNameParser(TokenStream input) {
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
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSql_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSql_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSql_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_statementContext sql_statement() throws RecognitionException {
		Sql_statementContext _localctx = new Sql_statementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sql_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				{
				setState(60);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				{
				setState(61);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				{
				setState(62);
				update_statement();
				}
				break;
			case SELECT_ACTION:
				{
				setState(63);
				select_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(66);
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
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterInsert_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitInsert_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitInsert_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_statementContext insert_statement() throws RecognitionException {
		Insert_statementContext _localctx = new Insert_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_insert_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
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
		public TerminalNode INSERT_ACTION() { return getToken(MethodNameParser.INSERT_ACTION, 0); }
		public Ignore_reserved_word_clauseContext ignore_reserved_word_clause() {
			return getRuleContext(Ignore_reserved_word_clauseContext.class,0);
		}
		public Insert_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterInsert_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitInsert_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitInsert_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_clauseContext insert_clause() throws RecognitionException {
		Insert_clauseContext _localctx = new Insert_clauseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_insert_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(INSERT_ACTION);
			setState(71);
			ignore_reserved_word_clause();
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
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Delete_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterDelete_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitDelete_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitDelete_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_statementContext delete_statement() throws RecognitionException {
		Delete_statementContext _localctx = new Delete_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_delete_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			delete_clause();
			setState(74);
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
		public TerminalNode DELETE_ACTION() { return getToken(MethodNameParser.DELETE_ACTION, 0); }
		public Ignore_reserved_word_clauseContext ignore_reserved_word_clause() {
			return getRuleContext(Ignore_reserved_word_clauseContext.class,0);
		}
		public Delete_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterDelete_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitDelete_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitDelete_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_clauseContext delete_clause() throws RecognitionException {
		Delete_clauseContext _localctx = new Delete_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_delete_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(DELETE_ACTION);
			setState(77);
			ignore_reserved_word_clause();
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
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Update_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterUpdate_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitUpdate_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitUpdate_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_statementContext update_statement() throws RecognitionException {
		Update_statementContext _localctx = new Update_statementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_update_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			update_clause();
			setState(80);
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
		public TerminalNode UPDATE_ACTION() { return getToken(MethodNameParser.UPDATE_ACTION, 0); }
		public Ignore_reserved_word_clauseContext ignore_reserved_word_clause() {
			return getRuleContext(Ignore_reserved_word_clauseContext.class,0);
		}
		public Update_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterUpdate_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitUpdate_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitUpdate_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_clauseContext update_clause() throws RecognitionException {
		Update_clauseContext _localctx = new Update_clauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_update_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(UPDATE_ACTION);
			setState(83);
			ignore_reserved_word_clause();
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
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public Aggregate_operation_clauseContext aggregate_operation_clause() {
			return getRuleContext(Aggregate_operation_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Group_by_clauseContext group_by_clause() {
			return getRuleContext(Group_by_clauseContext.class,0);
		}
		public Order_by_clauseContext order_by_clause() {
			return getRuleContext(Order_by_clauseContext.class,0);
		}
		public Select_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSelect_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSelect_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSelect_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_statementContext select_statement() throws RecognitionException {
		Select_statementContext _localctx = new Select_statementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_select_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			select_clause();
			setState(86);
			aggregate_operation_clause();
			setState(87);
			where_clause();
			setState(88);
			group_by_clause();
			setState(89);
			order_by_clause();
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
	public static class Select_clauseContext extends ParserRuleContext {
		public TerminalNode SELECT_ACTION() { return getToken(MethodNameParser.SELECT_ACTION, 0); }
		public Ignore_reserved_word_clauseContext ignore_reserved_word_clause() {
			return getRuleContext(Ignore_reserved_word_clauseContext.class,0);
		}
		public Select_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSelect_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSelect_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSelect_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_clauseContext select_clause() throws RecognitionException {
		Select_clauseContext _localctx = new Select_clauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_select_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(SELECT_ACTION);
			setState(92);
			ignore_reserved_word_clause();
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
	public static class Aggregate_operation_clauseContext extends ParserRuleContext {
		public Aggregate_function_clauseContext aggregate_function_clause() {
			return getRuleContext(Aggregate_function_clauseContext.class,0);
		}
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Aggregate_operation_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_operation_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterAggregate_operation_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitAggregate_operation_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitAggregate_operation_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_operation_clauseContext aggregate_operation_clause() throws RecognitionException {
		Aggregate_operation_clauseContext _localctx = new Aggregate_operation_clauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_aggregate_operation_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AGGREGATE_FUNCTION) {
				{
				setState(94);
				aggregate_function_clause();
				setState(95);
				field_clause();
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
	public static class Where_clauseContext extends ParserRuleContext {
		public Where_start_clauseContext where_start_clause() {
			return getRuleContext(Where_start_clauseContext.class,0);
		}
		public Condition_group_clauseContext condition_group_clause() {
			return getRuleContext(Condition_group_clauseContext.class,0);
		}
		public Where_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterWhere_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitWhere_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitWhere_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_clauseContext where_clause() throws RecognitionException {
		Where_clauseContext _localctx = new Where_clauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_where_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BY) {
				{
				setState(99);
				where_start_clause();
				setState(100);
				condition_group_clause();
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
	public static class Condition_group_clauseContext extends ParserRuleContext {
		public List<Condition_item_clauseContext> condition_item_clause() {
			return getRuleContexts(Condition_item_clauseContext.class);
		}
		public Condition_item_clauseContext condition_item_clause(int i) {
			return getRuleContext(Condition_item_clauseContext.class,i);
		}
		public Condition_group_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_group_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterCondition_group_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitCondition_group_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitCondition_group_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_group_clauseContext condition_group_clause() throws RecognitionException {
		Condition_group_clauseContext _localctx = new Condition_group_clauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_condition_group_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 106688L) != 0)) {
				{
				{
				setState(104);
				condition_item_clause();
				}
				}
				setState(109);
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
	public static class Condition_item_clauseContext extends ParserRuleContext {
		public Field_comparison_op_clauseContext field_comparison_op_clause() {
			return getRuleContext(Field_comparison_op_clauseContext.class,0);
		}
		public Logic_op_clauseContext logic_op_clause() {
			return getRuleContext(Logic_op_clauseContext.class,0);
		}
		public Left_bracket_clauseContext left_bracket_clause() {
			return getRuleContext(Left_bracket_clauseContext.class,0);
		}
		public Condition_group_clauseContext condition_group_clause() {
			return getRuleContext(Condition_group_clauseContext.class,0);
		}
		public Right_bracket_clauseContext right_bracket_clause() {
			return getRuleContext(Right_bracket_clauseContext.class,0);
		}
		public Condition_item_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_item_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterCondition_item_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitCondition_item_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitCondition_item_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_item_clauseContext condition_item_clause() throws RecognitionException {
		Condition_item_clauseContext _localctx = new Condition_item_clauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_condition_item_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AND || _la==OR) {
				{
				setState(110);
				logic_op_clause();
				}
			}

			setState(118);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RESERVED_WORD:
			case FIELD:
				{
				setState(113);
				field_comparison_op_clause();
				}
				break;
			case LEFT_BRACKET:
				{
				{
				setState(114);
				left_bracket_clause();
				setState(115);
				condition_group_clause();
				setState(116);
				right_bracket_clause();
				}
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
	public static class Field_comparison_op_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Comparison_op_clauseContext comparison_op_clause() {
			return getRuleContext(Comparison_op_clauseContext.class,0);
		}
		public Field_comparison_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_comparison_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterField_comparison_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitField_comparison_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitField_comparison_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_comparison_op_clauseContext field_comparison_op_clause() throws RecognitionException {
		Field_comparison_op_clauseContext _localctx = new Field_comparison_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_field_comparison_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			field_clause();
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMPARISON_OP) {
				{
				setState(121);
				comparison_op_clause();
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
	public static class Group_by_clauseContext extends ParserRuleContext {
		public Group_by_op_clauseContext group_by_op_clause() {
			return getRuleContext(Group_by_op_clauseContext.class,0);
		}
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Group_by_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterGroup_by_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitGroup_by_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitGroup_by_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_clauseContext group_by_clause() throws RecognitionException {
		Group_by_clauseContext _localctx = new Group_by_clauseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_group_by_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP_BY_OP) {
				{
				setState(124);
				group_by_op_clause();
				setState(125);
				field_clause();
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
	public static class Order_by_clauseContext extends ParserRuleContext {
		public Order_by_op_clauseContext order_by_op_clause() {
			return getRuleContext(Order_by_op_clauseContext.class,0);
		}
		public List<Field_clauseContext> field_clause() {
			return getRuleContexts(Field_clauseContext.class);
		}
		public Field_clauseContext field_clause(int i) {
			return getRuleContext(Field_clauseContext.class,i);
		}
		public List<Order_by_op_direction_clauseContext> order_by_op_direction_clause() {
			return getRuleContexts(Order_by_op_direction_clauseContext.class);
		}
		public Order_by_op_direction_clauseContext order_by_op_direction_clause(int i) {
			return getRuleContext(Order_by_op_direction_clauseContext.class,i);
		}
		public Order_by_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_clauseContext order_by_clause() throws RecognitionException {
		Order_by_clauseContext _localctx = new Order_by_clauseContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_order_by_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY_OP) {
				{
				setState(129);
				order_by_op_clause();
				setState(136);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==RESERVED_WORD || _la==FIELD) {
					{
					{
					setState(130);
					field_clause();
					setState(132);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ORDER_BY_OP_DIRECTION) {
						{
						setState(131);
						order_by_op_direction_clause();
						}
					}

					}
					}
					setState(138);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
	public static class Ignore_reserved_word_clauseContext extends ParserRuleContext {
		public List<TerminalNode> RESERVED_WORD() { return getTokens(MethodNameParser.RESERVED_WORD); }
		public TerminalNode RESERVED_WORD(int i) {
			return getToken(MethodNameParser.RESERVED_WORD, i);
		}
		public Ignore_reserved_word_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ignore_reserved_word_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterIgnore_reserved_word_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitIgnore_reserved_word_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitIgnore_reserved_word_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ignore_reserved_word_clauseContext ignore_reserved_word_clause() throws RecognitionException {
		Ignore_reserved_word_clauseContext _localctx = new Ignore_reserved_word_clauseContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_ignore_reserved_word_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==RESERVED_WORD) {
				{
				{
				setState(141);
				match(RESERVED_WORD);
				}
				}
				setState(146);
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
	public static class Where_start_clauseContext extends ParserRuleContext {
		public TerminalNode BY() { return getToken(MethodNameParser.BY, 0); }
		public Where_start_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_start_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterWhere_start_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitWhere_start_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitWhere_start_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_start_clauseContext where_start_clause() throws RecognitionException {
		Where_start_clauseContext _localctx = new Where_start_clauseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_where_start_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(BY);
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
	public static class Logic_op_clauseContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(MethodNameParser.AND, 0); }
		public TerminalNode OR() { return getToken(MethodNameParser.OR, 0); }
		public Logic_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLogic_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLogic_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLogic_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_clauseContext logic_op_clause() throws RecognitionException {
		Logic_op_clauseContext _localctx = new Logic_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_logic_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
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
	public static class Logic_op_and_clauseContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(MethodNameParser.AND, 0); }
		public Logic_op_and_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_and_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLogic_op_and_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLogic_op_and_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLogic_op_and_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_and_clauseContext logic_op_and_clause() throws RecognitionException {
		Logic_op_and_clauseContext _localctx = new Logic_op_and_clauseContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_logic_op_and_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(AND);
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
	public static class Logic_op_or_clauseContext extends ParserRuleContext {
		public TerminalNode OR() { return getToken(MethodNameParser.OR, 0); }
		public Logic_op_or_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_or_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLogic_op_or_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLogic_op_or_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLogic_op_or_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_or_clauseContext logic_op_or_clause() throws RecognitionException {
		Logic_op_or_clauseContext _localctx = new Logic_op_or_clauseContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_logic_op_or_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			match(OR);
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
	public static class Comparison_op_clauseContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP() { return getToken(MethodNameParser.COMPARISON_OP, 0); }
		public Comparison_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterComparison_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitComparison_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitComparison_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_clauseContext comparison_op_clause() throws RecognitionException {
		Comparison_op_clauseContext _localctx = new Comparison_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_comparison_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(COMPARISON_OP);
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
	public static class Group_by_op_clauseContext extends ParserRuleContext {
		public TerminalNode GROUP_BY_OP() { return getToken(MethodNameParser.GROUP_BY_OP, 0); }
		public Group_by_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterGroup_by_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitGroup_by_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitGroup_by_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_op_clauseContext group_by_op_clause() throws RecognitionException {
		Group_by_op_clauseContext _localctx = new Group_by_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_group_by_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(GROUP_BY_OP);
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
	public static class Order_by_op_clauseContext extends ParserRuleContext {
		public TerminalNode ORDER_BY_OP() { return getToken(MethodNameParser.ORDER_BY_OP, 0); }
		public Order_by_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_op_clauseContext order_by_op_clause() throws RecognitionException {
		Order_by_op_clauseContext _localctx = new Order_by_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_order_by_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(ORDER_BY_OP);
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
	public static class Order_by_op_direction_clauseContext extends ParserRuleContext {
		public TerminalNode ORDER_BY_OP_DIRECTION() { return getToken(MethodNameParser.ORDER_BY_OP_DIRECTION, 0); }
		public Order_by_op_direction_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_op_direction_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_op_direction_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_op_direction_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_op_direction_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_op_direction_clauseContext order_by_op_direction_clause() throws RecognitionException {
		Order_by_op_direction_clauseContext _localctx = new Order_by_op_direction_clauseContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_order_by_op_direction_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(ORDER_BY_OP_DIRECTION);
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
	public static class Aggregate_function_clauseContext extends ParserRuleContext {
		public TerminalNode AGGREGATE_FUNCTION() { return getToken(MethodNameParser.AGGREGATE_FUNCTION, 0); }
		public Aggregate_function_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterAggregate_function_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitAggregate_function_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitAggregate_function_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_function_clauseContext aggregate_function_clause() throws RecognitionException {
		Aggregate_function_clauseContext _localctx = new Aggregate_function_clauseContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_aggregate_function_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(AGGREGATE_FUNCTION);
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
	public static class Field_clauseContext extends ParserRuleContext {
		public List<TerminalNode> FIELD() { return getTokens(MethodNameParser.FIELD); }
		public TerminalNode FIELD(int i) {
			return getToken(MethodNameParser.FIELD, i);
		}
		public List<TerminalNode> RESERVED_WORD() { return getTokens(MethodNameParser.RESERVED_WORD); }
		public TerminalNode RESERVED_WORD(int i) {
			return getToken(MethodNameParser.RESERVED_WORD, i);
		}
		public Field_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterField_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitField_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitField_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_clauseContext field_clause() throws RecognitionException {
		Field_clauseContext _localctx = new Field_clauseContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_field_clause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(166); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(165);
					_la = _input.LA(1);
					if ( !(_la==RESERVED_WORD || _la==FIELD) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(168); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
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
	public static class Left_bracket_clauseContext extends ParserRuleContext {
		public TerminalNode LEFT_BRACKET() { return getToken(MethodNameParser.LEFT_BRACKET, 0); }
		public Left_bracket_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_left_bracket_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLeft_bracket_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLeft_bracket_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLeft_bracket_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Left_bracket_clauseContext left_bracket_clause() throws RecognitionException {
		Left_bracket_clauseContext _localctx = new Left_bracket_clauseContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_left_bracket_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
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
	public static class Right_bracket_clauseContext extends ParserRuleContext {
		public TerminalNode RIGHT_BRACKET() { return getToken(MethodNameParser.RIGHT_BRACKET, 0); }
		public Right_bracket_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_right_bracket_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterRight_bracket_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitRight_bracket_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitRight_bracket_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Right_bracket_clauseContext right_bracket_clause() throws RecognitionException {
		Right_bracket_clauseContext _localctx = new Right_bracket_clauseContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_right_bracket_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
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
	public static class EndContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MethodNameParser.EOF, 0); }
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
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
		"\u0004\u0001\u0011\u00b1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000A\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0003\tb\b\t\u0001\n\u0001"+
		"\n\u0001\n\u0003\ng\b\n\u0001\u000b\u0005\u000bj\b\u000b\n\u000b\f\u000b"+
		"m\t\u000b\u0001\f\u0003\fp\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0003\fw\b\f\u0001\r\u0001\r\u0003\r{\b\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0003\u000e\u0080\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003"+
		"\u000f\u0085\b\u000f\u0005\u000f\u0087\b\u000f\n\u000f\f\u000f\u008a\t"+
		"\u000f\u0003\u000f\u008c\b\u000f\u0001\u0010\u0005\u0010\u008f\b\u0010"+
		"\n\u0010\f\u0010\u0092\t\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0004\u001a\u00a7\b\u001a\u000b"+
		"\u001a\f\u001a\u00a8\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0000\u0000\u001e\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468:\u0000\u0002\u0001\u0000\u0006\u0007\u0001\u0000\u000f\u0010\u00a1"+
		"\u0000@\u0001\u0000\u0000\u0000\u0002D\u0001\u0000\u0000\u0000\u0004F"+
		"\u0001\u0000\u0000\u0000\u0006I\u0001\u0000\u0000\u0000\bL\u0001\u0000"+
		"\u0000\u0000\nO\u0001\u0000\u0000\u0000\fR\u0001\u0000\u0000\u0000\u000e"+
		"U\u0001\u0000\u0000\u0000\u0010[\u0001\u0000\u0000\u0000\u0012a\u0001"+
		"\u0000\u0000\u0000\u0014f\u0001\u0000\u0000\u0000\u0016k\u0001\u0000\u0000"+
		"\u0000\u0018o\u0001\u0000\u0000\u0000\u001ax\u0001\u0000\u0000\u0000\u001c"+
		"\u007f\u0001\u0000\u0000\u0000\u001e\u008b\u0001\u0000\u0000\u0000 \u0090"+
		"\u0001\u0000\u0000\u0000\"\u0093\u0001\u0000\u0000\u0000$\u0095\u0001"+
		"\u0000\u0000\u0000&\u0097\u0001\u0000\u0000\u0000(\u0099\u0001\u0000\u0000"+
		"\u0000*\u009b\u0001\u0000\u0000\u0000,\u009d\u0001\u0000\u0000\u0000."+
		"\u009f\u0001\u0000\u0000\u00000\u00a1\u0001\u0000\u0000\u00002\u00a3\u0001"+
		"\u0000\u0000\u00004\u00a6\u0001\u0000\u0000\u00006\u00aa\u0001\u0000\u0000"+
		"\u00008\u00ac\u0001\u0000\u0000\u0000:\u00ae\u0001\u0000\u0000\u0000<"+
		"A\u0003\u0002\u0001\u0000=A\u0003\u0006\u0003\u0000>A\u0003\n\u0005\u0000"+
		"?A\u0003\u000e\u0007\u0000@<\u0001\u0000\u0000\u0000@=\u0001\u0000\u0000"+
		"\u0000@>\u0001\u0000\u0000\u0000@?\u0001\u0000\u0000\u0000AB\u0001\u0000"+
		"\u0000\u0000BC\u0003:\u001d\u0000C\u0001\u0001\u0000\u0000\u0000DE\u0003"+
		"\u0004\u0002\u0000E\u0003\u0001\u0000\u0000\u0000FG\u0005\u0001\u0000"+
		"\u0000GH\u0003 \u0010\u0000H\u0005\u0001\u0000\u0000\u0000IJ\u0003\b\u0004"+
		"\u0000JK\u0003\u0014\n\u0000K\u0007\u0001\u0000\u0000\u0000LM\u0005\u0002"+
		"\u0000\u0000MN\u0003 \u0010\u0000N\t\u0001\u0000\u0000\u0000OP\u0003\f"+
		"\u0006\u0000PQ\u0003\u0014\n\u0000Q\u000b\u0001\u0000\u0000\u0000RS\u0005"+
		"\u0003\u0000\u0000ST\u0003 \u0010\u0000T\r\u0001\u0000\u0000\u0000UV\u0003"+
		"\u0010\b\u0000VW\u0003\u0012\t\u0000WX\u0003\u0014\n\u0000XY\u0003\u001c"+
		"\u000e\u0000YZ\u0003\u001e\u000f\u0000Z\u000f\u0001\u0000\u0000\u0000"+
		"[\\\u0005\u0004\u0000\u0000\\]\u0003 \u0010\u0000]\u0011\u0001\u0000\u0000"+
		"\u0000^_\u00032\u0019\u0000_`\u00034\u001a\u0000`b\u0001\u0000\u0000\u0000"+
		"a^\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b\u0013\u0001\u0000"+
		"\u0000\u0000cd\u0003\"\u0011\u0000de\u0003\u0016\u000b\u0000eg\u0001\u0000"+
		"\u0000\u0000fc\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000g\u0015"+
		"\u0001\u0000\u0000\u0000hj\u0003\u0018\f\u0000ih\u0001\u0000\u0000\u0000"+
		"jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000"+
		"\u0000l\u0017\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000np\u0003"+
		"$\u0012\u0000on\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pv\u0001"+
		"\u0000\u0000\u0000qw\u0003\u001a\r\u0000rs\u00036\u001b\u0000st\u0003"+
		"\u0016\u000b\u0000tu\u00038\u001c\u0000uw\u0001\u0000\u0000\u0000vq\u0001"+
		"\u0000\u0000\u0000vr\u0001\u0000\u0000\u0000w\u0019\u0001\u0000\u0000"+
		"\u0000xz\u00034\u001a\u0000y{\u0003*\u0015\u0000zy\u0001\u0000\u0000\u0000"+
		"z{\u0001\u0000\u0000\u0000{\u001b\u0001\u0000\u0000\u0000|}\u0003,\u0016"+
		"\u0000}~\u00034\u001a\u0000~\u0080\u0001\u0000\u0000\u0000\u007f|\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u001d\u0001"+
		"\u0000\u0000\u0000\u0081\u0088\u0003.\u0017\u0000\u0082\u0084\u00034\u001a"+
		"\u0000\u0083\u0085\u00030\u0018\u0000\u0084\u0083\u0001\u0000\u0000\u0000"+
		"\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0087\u0001\u0000\u0000\u0000"+
		"\u0086\u0082\u0001\u0000\u0000\u0000\u0087\u008a\u0001\u0000\u0000\u0000"+
		"\u0088\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000"+
		"\u0089\u008c\u0001\u0000\u0000\u0000\u008a\u0088\u0001\u0000\u0000\u0000"+
		"\u008b\u0081\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000"+
		"\u008c\u001f\u0001\u0000\u0000\u0000\u008d\u008f\u0005\u000f\u0000\u0000"+
		"\u008e\u008d\u0001\u0000\u0000\u0000\u008f\u0092\u0001\u0000\u0000\u0000"+
		"\u0090\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000"+
		"\u0091!\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0093"+
		"\u0094\u0005\u0005\u0000\u0000\u0094#\u0001\u0000\u0000\u0000\u0095\u0096"+
		"\u0007\u0000\u0000\u0000\u0096%\u0001\u0000\u0000\u0000\u0097\u0098\u0005"+
		"\u0006\u0000\u0000\u0098\'\u0001\u0000\u0000\u0000\u0099\u009a\u0005\u0007"+
		"\u0000\u0000\u009a)\u0001\u0000\u0000\u0000\u009b\u009c\u0005\b\u0000"+
		"\u0000\u009c+\u0001\u0000\u0000\u0000\u009d\u009e\u0005\t\u0000\u0000"+
		"\u009e-\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\n\u0000\u0000\u00a0"+
		"/\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005\u000b\u0000\u0000\u00a21\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a4\u0005\f\u0000\u0000\u00a43\u0001\u0000"+
		"\u0000\u0000\u00a5\u00a7\u0007\u0001\u0000\u0000\u00a6\u00a5\u0001\u0000"+
		"\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000"+
		"\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a95\u0001\u0000\u0000"+
		"\u0000\u00aa\u00ab\u0005\r\u0000\u0000\u00ab7\u0001\u0000\u0000\u0000"+
		"\u00ac\u00ad\u0005\u000e\u0000\u0000\u00ad9\u0001\u0000\u0000\u0000\u00ae"+
		"\u00af\u0005\u0000\u0000\u0001\u00af;\u0001\u0000\u0000\u0000\r@afkov"+
		"z\u007f\u0084\u0088\u008b\u0090\u00a8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}