// Generated from F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
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
		INSERT_ACTION=1, DELETE_ACTION=2, UPDATE_ACTION=3, SELECT_ACTION=4, SELECT_COUNT_ACTION=5, 
		SELECT_EXIST_ACTION=6, BY=7, LOGIC_OP_AND=8, LOGIC_OP_OR=9, COMPARISON_NOT_OP=10, 
		COMPARISON_OP=11, COMPARISON_NULL_OP=12, GROUP_BY_OP=13, ORDER_BY_OP=14, 
		ORDER_BY_OP_DIRECTION=15, LIMIT_OP=16, LEFT_BRACKET=17, RIGHT_BRACKET=18, 
		RESERVED_WORD=19, FIELD=20, WS=21;
	public static final int
		RULE_sql_statement = 0, RULE_insert_statement = 1, RULE_insert_clause = 2, 
		RULE_delete_statement = 3, RULE_delete_clause = 4, RULE_update_statement = 5, 
		RULE_update_clause = 6, RULE_select_statement = 7, RULE_select_item_clause = 8, 
		RULE_select_count_clause = 9, RULE_select_exist_clause = 10, RULE_where_clause = 11, 
		RULE_condition_expression = 12, RULE_or_expression = 13, RULE_and_expression = 14, 
		RULE_condition_term = 15, RULE_field_comparison_op_clause = 16, RULE_group_by_clause = 17, 
		RULE_order_by_clause = 18, RULE_order_by_item_clause = 19, RULE_limit_op_expression = 20, 
		RULE_ignore_reserved_word = 21, RULE_where_start = 22, RULE_logic_op_and = 23, 
		RULE_logic_op_or = 24, RULE_comparison_op = 25, RULE_group_by_op = 26, 
		RULE_order_by_op = 27, RULE_order_by_op_direction = 28, RULE_limit_op = 29, 
		RULE_field_clause = 30, RULE_left_bracket = 31, RULE_right_bracket = 32, 
		RULE_end = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "select_statement", 
			"select_item_clause", "select_count_clause", "select_exist_clause", "where_clause", 
			"condition_expression", "or_expression", "and_expression", "condition_term", 
			"field_comparison_op_clause", "group_by_clause", "order_by_clause", "order_by_item_clause", 
			"limit_op_expression", "ignore_reserved_word", "where_start", "logic_op_and", 
			"logic_op_or", "comparison_op", "group_by_op", "order_by_op", "order_by_op_direction", 
			"limit_op", "field_clause", "left_bracket", "right_bracket", "end"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'count'", "'exist'", "'By'", "'And'", 
			"'Or'", "'Not'", null, null, "'GroupBy'", "'OrderBy'", null, null, "'('", 
			"')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"SELECT_COUNT_ACTION", "SELECT_EXIST_ACTION", "BY", "LOGIC_OP_AND", "LOGIC_OP_OR", 
			"COMPARISON_NOT_OP", "COMPARISON_OP", "COMPARISON_NULL_OP", "GROUP_BY_OP", 
			"ORDER_BY_OP", "ORDER_BY_OP_DIRECTION", "LIMIT_OP", "LEFT_BRACKET", "RIGHT_BRACKET", 
			"RESERVED_WORD", "FIELD", "WS"
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
			setState(72);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				{
				setState(68);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				{
				setState(69);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				{
				setState(70);
				update_statement();
				}
				break;
			case SELECT_ACTION:
			case SELECT_COUNT_ACTION:
			case SELECT_EXIST_ACTION:
				{
				setState(71);
				select_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(74);
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
			setState(76);
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
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
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
			setState(78);
			match(INSERT_ACTION);
			setState(79);
			ignore_reserved_word();
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
			setState(81);
			delete_clause();
			setState(82);
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
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
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
			setState(84);
			match(DELETE_ACTION);
			setState(85);
			ignore_reserved_word();
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
			setState(87);
			update_clause();
			setState(88);
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
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
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
			setState(90);
			match(UPDATE_ACTION);
			setState(91);
			ignore_reserved_word();
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
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Select_item_clauseContext select_item_clause() {
			return getRuleContext(Select_item_clauseContext.class,0);
		}
		public Select_count_clauseContext select_count_clause() {
			return getRuleContext(Select_count_clauseContext.class,0);
		}
		public Select_exist_clauseContext select_exist_clause() {
			return getRuleContext(Select_exist_clauseContext.class,0);
		}
		public Order_by_clauseContext order_by_clause() {
			return getRuleContext(Order_by_clauseContext.class,0);
		}
		public Limit_opContext limit_op() {
			return getRuleContext(Limit_opContext.class,0);
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT_ACTION:
				{
				setState(93);
				select_item_clause();
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT_OP) {
					{
					setState(94);
					limit_op();
					}
				}

				}
				break;
			case SELECT_COUNT_ACTION:
				{
				setState(97);
				select_count_clause();
				}
				break;
			case SELECT_EXIST_ACTION:
				{
				setState(98);
				select_exist_clause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(101);
			where_clause();
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_BY_OP) {
				{
				setState(102);
				order_by_clause();
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
		public TerminalNode SELECT_ACTION() { return getToken(MethodNameParser.SELECT_ACTION, 0); }
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
		}
		public Select_item_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_item_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSelect_item_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSelect_item_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSelect_item_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_item_clauseContext select_item_clause() throws RecognitionException {
		Select_item_clauseContext _localctx = new Select_item_clauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_select_item_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(SELECT_ACTION);
			setState(106);
			ignore_reserved_word();
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
	public static class Select_count_clauseContext extends ParserRuleContext {
		public TerminalNode SELECT_COUNT_ACTION() { return getToken(MethodNameParser.SELECT_COUNT_ACTION, 0); }
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
		}
		public Select_count_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_count_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSelect_count_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSelect_count_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSelect_count_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_count_clauseContext select_count_clause() throws RecognitionException {
		Select_count_clauseContext _localctx = new Select_count_clauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_select_count_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(SELECT_COUNT_ACTION);
			setState(109);
			ignore_reserved_word();
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
	public static class Select_exist_clauseContext extends ParserRuleContext {
		public TerminalNode SELECT_EXIST_ACTION() { return getToken(MethodNameParser.SELECT_EXIST_ACTION, 0); }
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
		}
		public Select_exist_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_exist_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterSelect_exist_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitSelect_exist_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitSelect_exist_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_exist_clauseContext select_exist_clause() throws RecognitionException {
		Select_exist_clauseContext _localctx = new Select_exist_clauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_select_exist_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(SELECT_EXIST_ACTION);
			setState(112);
			ignore_reserved_word();
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
		public Condition_expressionContext condition_expression() {
			return getRuleContext(Condition_expressionContext.class,0);
		}
		public Ignore_reserved_wordContext ignore_reserved_word() {
			return getRuleContext(Ignore_reserved_wordContext.class,0);
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
		enterRule(_localctx, 22, RULE_where_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BY) {
				{
				setState(114);
				where_start();
				setState(115);
				condition_expression();
				}
			}

			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(119);
				ignore_reserved_word();
				}
				break;
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
	public static class Condition_expressionContext extends ParserRuleContext {
		public Or_expressionContext or_expression() {
			return getRuleContext(Or_expressionContext.class,0);
		}
		public Condition_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterCondition_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitCondition_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitCondition_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_expressionContext condition_expression() throws RecognitionException {
		Condition_expressionContext _localctx = new Condition_expressionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_condition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			or_expression();
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
	public static class Or_expressionContext extends ParserRuleContext {
		public List<And_expressionContext> and_expression() {
			return getRuleContexts(And_expressionContext.class);
		}
		public And_expressionContext and_expression(int i) {
			return getRuleContext(And_expressionContext.class,i);
		}
		public List<Logic_op_orContext> logic_op_or() {
			return getRuleContexts(Logic_op_orContext.class);
		}
		public Logic_op_orContext logic_op_or(int i) {
			return getRuleContext(Logic_op_orContext.class,i);
		}
		public Or_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOr_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOr_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOr_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Or_expressionContext or_expression() throws RecognitionException {
		Or_expressionContext _localctx = new Or_expressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_or_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			and_expression();
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OP_OR) {
				{
				{
				setState(125);
				logic_op_or();
				setState(126);
				and_expression();
				}
				}
				setState(132);
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
	public static class And_expressionContext extends ParserRuleContext {
		public List<Condition_termContext> condition_term() {
			return getRuleContexts(Condition_termContext.class);
		}
		public Condition_termContext condition_term(int i) {
			return getRuleContext(Condition_termContext.class,i);
		}
		public List<Logic_op_andContext> logic_op_and() {
			return getRuleContexts(Logic_op_andContext.class);
		}
		public Logic_op_andContext logic_op_and(int i) {
			return getRuleContext(Logic_op_andContext.class,i);
		}
		public And_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterAnd_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitAnd_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitAnd_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final And_expressionContext and_expression() throws RecognitionException {
		And_expressionContext _localctx = new And_expressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_and_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			condition_term();
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LOGIC_OP_AND) {
				{
				{
				setState(134);
				logic_op_and();
				setState(135);
				condition_term();
				}
				}
				setState(141);
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
		public Field_comparison_op_clauseContext field_comparison_op_clause() {
			return getRuleContext(Field_comparison_op_clauseContext.class,0);
		}
		public Left_bracketContext left_bracket() {
			return getRuleContext(Left_bracketContext.class,0);
		}
		public Condition_expressionContext condition_expression() {
			return getRuleContext(Condition_expressionContext.class,0);
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
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterCondition_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitCondition_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitCondition_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_termContext condition_term() throws RecognitionException {
		Condition_termContext _localctx = new Condition_termContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_condition_term);
		try {
			setState(147);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FIELD:
				enterOuterAlt(_localctx, 1);
				{
				setState(142);
				field_comparison_op_clause();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(143);
				left_bracket();
				setState(144);
				condition_expression();
				setState(145);
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
	public static class Field_comparison_op_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Comparison_opContext comparison_op() {
			return getRuleContext(Comparison_opContext.class,0);
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
		enterRule(_localctx, 32, RULE_field_comparison_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			field_clause();
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 7168L) != 0)) {
				{
				setState(150);
				comparison_op();
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
		public Group_by_opContext group_by_op() {
			return getRuleContext(Group_by_opContext.class,0);
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
		enterRule(_localctx, 34, RULE_group_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			group_by_op();
			setState(154);
			field_clause();
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
		public Order_by_opContext order_by_op() {
			return getRuleContext(Order_by_opContext.class,0);
		}
		public List<Order_by_item_clauseContext> order_by_item_clause() {
			return getRuleContexts(Order_by_item_clauseContext.class);
		}
		public Order_by_item_clauseContext order_by_item_clause(int i) {
			return getRuleContext(Order_by_item_clauseContext.class,i);
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
		enterRule(_localctx, 36, RULE_order_by_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			order_by_op();
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FIELD) {
				{
				{
				setState(157);
				order_by_item_clause();
				}
				}
				setState(162);
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
	public static class Order_by_item_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Order_by_op_directionContext order_by_op_direction() {
			return getRuleContext(Order_by_op_directionContext.class,0);
		}
		public Order_by_item_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_item_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_item_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_item_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_item_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_item_clauseContext order_by_item_clause() throws RecognitionException {
		Order_by_item_clauseContext _localctx = new Order_by_item_clauseContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_order_by_item_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			field_clause();
			setState(164);
			order_by_op_direction();
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
	public static class Limit_op_expressionContext extends ParserRuleContext {
		public Limit_opContext limit_op() {
			return getRuleContext(Limit_opContext.class,0);
		}
		public Limit_op_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit_op_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLimit_op_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLimit_op_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLimit_op_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Limit_op_expressionContext limit_op_expression() throws RecognitionException {
		Limit_op_expressionContext _localctx = new Limit_op_expressionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_limit_op_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			limit_op();
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
	public static class Ignore_reserved_wordContext extends ParserRuleContext {
		public List<TerminalNode> RESERVED_WORD() { return getTokens(MethodNameParser.RESERVED_WORD); }
		public TerminalNode RESERVED_WORD(int i) {
			return getToken(MethodNameParser.RESERVED_WORD, i);
		}
		public Ignore_reserved_wordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ignore_reserved_word; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterIgnore_reserved_word(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitIgnore_reserved_word(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitIgnore_reserved_word(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ignore_reserved_wordContext ignore_reserved_word() throws RecognitionException {
		Ignore_reserved_wordContext _localctx = new Ignore_reserved_wordContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_ignore_reserved_word);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(168);
					match(RESERVED_WORD);
					}
					} 
				}
				setState(173);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class Where_startContext extends ParserRuleContext {
		public TerminalNode BY() { return getToken(MethodNameParser.BY, 0); }
		public Where_startContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterWhere_start(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitWhere_start(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitWhere_start(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_startContext where_start() throws RecognitionException {
		Where_startContext _localctx = new Where_startContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_where_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
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
	public static class Logic_op_andContext extends ParserRuleContext {
		public TerminalNode LOGIC_OP_AND() { return getToken(MethodNameParser.LOGIC_OP_AND, 0); }
		public Logic_op_andContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_and; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLogic_op_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLogic_op_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLogic_op_and(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_andContext logic_op_and() throws RecognitionException {
		Logic_op_andContext _localctx = new Logic_op_andContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_logic_op_and);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(LOGIC_OP_AND);
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
	public static class Logic_op_orContext extends ParserRuleContext {
		public TerminalNode LOGIC_OP_OR() { return getToken(MethodNameParser.LOGIC_OP_OR, 0); }
		public Logic_op_orContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_or; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLogic_op_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLogic_op_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLogic_op_or(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_orContext logic_op_or() throws RecognitionException {
		Logic_op_orContext _localctx = new Logic_op_orContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_logic_op_or);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(LOGIC_OP_OR);
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
	public static class Comparison_opContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP() { return getToken(MethodNameParser.COMPARISON_OP, 0); }
		public TerminalNode COMPARISON_NOT_OP() { return getToken(MethodNameParser.COMPARISON_NOT_OP, 0); }
		public TerminalNode COMPARISON_NULL_OP() { return getToken(MethodNameParser.COMPARISON_NULL_OP, 0); }
		public Comparison_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterComparison_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitComparison_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitComparison_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_opContext comparison_op() throws RecognitionException {
		Comparison_opContext _localctx = new Comparison_opContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_comparison_op);
		int _la;
		try {
			setState(185);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMPARISON_NOT_OP:
			case COMPARISON_OP:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMPARISON_NOT_OP) {
					{
					setState(180);
					match(COMPARISON_NOT_OP);
					}
				}

				setState(183);
				match(COMPARISON_OP);
				}
				}
				break;
			case COMPARISON_NULL_OP:
				enterOuterAlt(_localctx, 2);
				{
				setState(184);
				match(COMPARISON_NULL_OP);
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
	public static class Group_by_opContext extends ParserRuleContext {
		public TerminalNode GROUP_BY_OP() { return getToken(MethodNameParser.GROUP_BY_OP, 0); }
		public Group_by_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterGroup_by_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitGroup_by_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitGroup_by_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_opContext group_by_op() throws RecognitionException {
		Group_by_opContext _localctx = new Group_by_opContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_group_by_op);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
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
	public static class Order_by_opContext extends ParserRuleContext {
		public TerminalNode ORDER_BY_OP() { return getToken(MethodNameParser.ORDER_BY_OP, 0); }
		public Order_by_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_opContext order_by_op() throws RecognitionException {
		Order_by_opContext _localctx = new Order_by_opContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_order_by_op);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
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
	public static class Order_by_op_directionContext extends ParserRuleContext {
		public TerminalNode ORDER_BY_OP_DIRECTION() { return getToken(MethodNameParser.ORDER_BY_OP_DIRECTION, 0); }
		public Order_by_op_directionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_by_op_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_by_op_direction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_by_op_direction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_by_op_direction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_by_op_directionContext order_by_op_direction() throws RecognitionException {
		Order_by_op_directionContext _localctx = new Order_by_op_directionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_order_by_op_direction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
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
	public static class Limit_opContext extends ParserRuleContext {
		public TerminalNode LIMIT_OP() { return getToken(MethodNameParser.LIMIT_OP, 0); }
		public Limit_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLimit_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLimit_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLimit_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Limit_opContext limit_op() throws RecognitionException {
		Limit_opContext _localctx = new Limit_opContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_limit_op);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(LIMIT_OP);
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
		enterRule(_localctx, 60, RULE_field_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(195);
				match(FIELD);
				}
				}
				setState(198); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==FIELD );
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
		public TerminalNode LEFT_BRACKET() { return getToken(MethodNameParser.LEFT_BRACKET, 0); }
		public Left_bracketContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_left_bracket; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterLeft_bracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitLeft_bracket(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitLeft_bracket(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Left_bracketContext left_bracket() throws RecognitionException {
		Left_bracketContext _localctx = new Left_bracketContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_left_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
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
		public TerminalNode RIGHT_BRACKET() { return getToken(MethodNameParser.RIGHT_BRACKET, 0); }
		public Right_bracketContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_right_bracket; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterRight_bracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitRight_bracket(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitRight_bracket(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Right_bracketContext right_bracket() throws RecognitionException {
		Right_bracketContext _localctx = new Right_bracketContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_right_bracket);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
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
		enterRule(_localctx, 66, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
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
		"\u0004\u0001\u0015\u00cf\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000I\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0003\u0007`\b\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u0007d\b\u0007\u0001\u0007\u0001\u0007\u0003\u0007h\b\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0003\u000bv\b\u000b\u0001\u000b\u0003"+
		"\u000by\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0005"+
		"\r\u0081\b\r\n\r\f\r\u0084\t\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0005\u000e\u008a\b\u000e\n\u000e\f\u000e\u008d\t\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0094\b\u000f"+
		"\u0001\u0010\u0001\u0010\u0003\u0010\u0098\b\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0005\u0012\u009f\b\u0012\n\u0012"+
		"\f\u0012\u00a2\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0005\u0015\u00aa\b\u0015\n\u0015\f\u0015\u00ad"+
		"\t\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0003\u0019\u00b6\b\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u00ba\b\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0004\u001e\u00c5"+
		"\b\u001e\u000b\u001e\f\u001e\u00c6\u0001\u001f\u0001\u001f\u0001 \u0001"+
		" \u0001!\u0001!\u0001!\u0000\u0000\"\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@B\u0000"+
		"\u0000\u00be\u0000H\u0001\u0000\u0000\u0000\u0002L\u0001\u0000\u0000\u0000"+
		"\u0004N\u0001\u0000\u0000\u0000\u0006Q\u0001\u0000\u0000\u0000\bT\u0001"+
		"\u0000\u0000\u0000\nW\u0001\u0000\u0000\u0000\fZ\u0001\u0000\u0000\u0000"+
		"\u000ec\u0001\u0000\u0000\u0000\u0010i\u0001\u0000\u0000\u0000\u0012l"+
		"\u0001\u0000\u0000\u0000\u0014o\u0001\u0000\u0000\u0000\u0016u\u0001\u0000"+
		"\u0000\u0000\u0018z\u0001\u0000\u0000\u0000\u001a|\u0001\u0000\u0000\u0000"+
		"\u001c\u0085\u0001\u0000\u0000\u0000\u001e\u0093\u0001\u0000\u0000\u0000"+
		" \u0095\u0001\u0000\u0000\u0000\"\u0099\u0001\u0000\u0000\u0000$\u009c"+
		"\u0001\u0000\u0000\u0000&\u00a3\u0001\u0000\u0000\u0000(\u00a6\u0001\u0000"+
		"\u0000\u0000*\u00ab\u0001\u0000\u0000\u0000,\u00ae\u0001\u0000\u0000\u0000"+
		".\u00b0\u0001\u0000\u0000\u00000\u00b2\u0001\u0000\u0000\u00002\u00b9"+
		"\u0001\u0000\u0000\u00004\u00bb\u0001\u0000\u0000\u00006\u00bd\u0001\u0000"+
		"\u0000\u00008\u00bf\u0001\u0000\u0000\u0000:\u00c1\u0001\u0000\u0000\u0000"+
		"<\u00c4\u0001\u0000\u0000\u0000>\u00c8\u0001\u0000\u0000\u0000@\u00ca"+
		"\u0001\u0000\u0000\u0000B\u00cc\u0001\u0000\u0000\u0000DI\u0003\u0002"+
		"\u0001\u0000EI\u0003\u0006\u0003\u0000FI\u0003\n\u0005\u0000GI\u0003\u000e"+
		"\u0007\u0000HD\u0001\u0000\u0000\u0000HE\u0001\u0000\u0000\u0000HF\u0001"+
		"\u0000\u0000\u0000HG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000"+
		"JK\u0003B!\u0000K\u0001\u0001\u0000\u0000\u0000LM\u0003\u0004\u0002\u0000"+
		"M\u0003\u0001\u0000\u0000\u0000NO\u0005\u0001\u0000\u0000OP\u0003*\u0015"+
		"\u0000P\u0005\u0001\u0000\u0000\u0000QR\u0003\b\u0004\u0000RS\u0003\u0016"+
		"\u000b\u0000S\u0007\u0001\u0000\u0000\u0000TU\u0005\u0002\u0000\u0000"+
		"UV\u0003*\u0015\u0000V\t\u0001\u0000\u0000\u0000WX\u0003\f\u0006\u0000"+
		"XY\u0003\u0016\u000b\u0000Y\u000b\u0001\u0000\u0000\u0000Z[\u0005\u0003"+
		"\u0000\u0000[\\\u0003*\u0015\u0000\\\r\u0001\u0000\u0000\u0000]_\u0003"+
		"\u0010\b\u0000^`\u0003:\u001d\u0000_^\u0001\u0000\u0000\u0000_`\u0001"+
		"\u0000\u0000\u0000`d\u0001\u0000\u0000\u0000ad\u0003\u0012\t\u0000bd\u0003"+
		"\u0014\n\u0000c]\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cb\u0001"+
		"\u0000\u0000\u0000de\u0001\u0000\u0000\u0000eg\u0003\u0016\u000b\u0000"+
		"fh\u0003$\u0012\u0000gf\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000"+
		"h\u000f\u0001\u0000\u0000\u0000ij\u0005\u0004\u0000\u0000jk\u0003*\u0015"+
		"\u0000k\u0011\u0001\u0000\u0000\u0000lm\u0005\u0005\u0000\u0000mn\u0003"+
		"*\u0015\u0000n\u0013\u0001\u0000\u0000\u0000op\u0005\u0006\u0000\u0000"+
		"pq\u0003*\u0015\u0000q\u0015\u0001\u0000\u0000\u0000rs\u0003,\u0016\u0000"+
		"st\u0003\u0018\f\u0000tv\u0001\u0000\u0000\u0000ur\u0001\u0000\u0000\u0000"+
		"uv\u0001\u0000\u0000\u0000vx\u0001\u0000\u0000\u0000wy\u0003*\u0015\u0000"+
		"xw\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y\u0017\u0001\u0000"+
		"\u0000\u0000z{\u0003\u001a\r\u0000{\u0019\u0001\u0000\u0000\u0000|\u0082"+
		"\u0003\u001c\u000e\u0000}~\u00030\u0018\u0000~\u007f\u0003\u001c\u000e"+
		"\u0000\u007f\u0081\u0001\u0000\u0000\u0000\u0080}\u0001\u0000\u0000\u0000"+
		"\u0081\u0084\u0001\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000"+
		"\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u001b\u0001\u0000\u0000\u0000"+
		"\u0084\u0082\u0001\u0000\u0000\u0000\u0085\u008b\u0003\u001e\u000f\u0000"+
		"\u0086\u0087\u0003.\u0017\u0000\u0087\u0088\u0003\u001e\u000f\u0000\u0088"+
		"\u008a\u0001\u0000\u0000\u0000\u0089\u0086\u0001\u0000\u0000\u0000\u008a"+
		"\u008d\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008b"+
		"\u008c\u0001\u0000\u0000\u0000\u008c\u001d\u0001\u0000\u0000\u0000\u008d"+
		"\u008b\u0001\u0000\u0000\u0000\u008e\u0094\u0003 \u0010\u0000\u008f\u0090"+
		"\u0003>\u001f\u0000\u0090\u0091\u0003\u0018\f\u0000\u0091\u0092\u0003"+
		"@ \u0000\u0092\u0094\u0001\u0000\u0000\u0000\u0093\u008e\u0001\u0000\u0000"+
		"\u0000\u0093\u008f\u0001\u0000\u0000\u0000\u0094\u001f\u0001\u0000\u0000"+
		"\u0000\u0095\u0097\u0003<\u001e\u0000\u0096\u0098\u00032\u0019\u0000\u0097"+
		"\u0096\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098"+
		"!\u0001\u0000\u0000\u0000\u0099\u009a\u00034\u001a\u0000\u009a\u009b\u0003"+
		"<\u001e\u0000\u009b#\u0001\u0000\u0000\u0000\u009c\u00a0\u00036\u001b"+
		"\u0000\u009d\u009f\u0003&\u0013\u0000\u009e\u009d\u0001\u0000\u0000\u0000"+
		"\u009f\u00a2\u0001\u0000\u0000\u0000\u00a0\u009e\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1%\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a0\u0001\u0000\u0000\u0000\u00a3\u00a4\u0003<\u001e\u0000\u00a4\u00a5"+
		"\u00038\u001c\u0000\u00a5\'\u0001\u0000\u0000\u0000\u00a6\u00a7\u0003"+
		":\u001d\u0000\u00a7)\u0001\u0000\u0000\u0000\u00a8\u00aa\u0005\u0013\u0000"+
		"\u0000\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ad\u0001\u0000\u0000"+
		"\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000"+
		"\u0000\u00ac+\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000"+
		"\u00ae\u00af\u0005\u0007\u0000\u0000\u00af-\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0005\b\u0000\u0000\u00b1/\u0001\u0000\u0000\u0000\u00b2\u00b3"+
		"\u0005\t\u0000\u0000\u00b31\u0001\u0000\u0000\u0000\u00b4\u00b6\u0005"+
		"\n\u0000\u0000\u00b5\u00b4\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000"+
		"\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00ba\u0005\u000b"+
		"\u0000\u0000\u00b8\u00ba\u0005\f\u0000\u0000\u00b9\u00b5\u0001\u0000\u0000"+
		"\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba3\u0001\u0000\u0000\u0000"+
		"\u00bb\u00bc\u0005\r\u0000\u0000\u00bc5\u0001\u0000\u0000\u0000\u00bd"+
		"\u00be\u0005\u000e\u0000\u0000\u00be7\u0001\u0000\u0000\u0000\u00bf\u00c0"+
		"\u0005\u000f\u0000\u0000\u00c09\u0001\u0000\u0000\u0000\u00c1\u00c2\u0005"+
		"\u0010\u0000\u0000\u00c2;\u0001\u0000\u0000\u0000\u00c3\u00c5\u0005\u0014"+
		"\u0000\u0000\u00c4\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000"+
		"\u0000\u0000\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000"+
		"\u0000\u0000\u00c7=\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005\u0011\u0000"+
		"\u0000\u00c9?\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005\u0012\u0000\u0000"+
		"\u00cbA\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005\u0000\u0000\u0001\u00cd"+
		"C\u0001\u0000\u0000\u0000\u000fH_cgux\u0082\u008b\u0093\u0097\u00a0\u00ab"+
		"\u00b5\u00b9\u00c6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}