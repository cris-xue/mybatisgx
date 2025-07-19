// Generated from F:/devops/mybatisx/mybatisx-core/src/main/resources/antlr/MethodNameParser.g4 by ANTLR 4.13.2
package com.lc.mybatisx.syntax;
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
		AND=6, OR=7, COMPARISON_OP=8, GROUP_OP=9, ORDER_OP=10, ORDER_OP_DIRECTION=11, 
		AGGREGATE_FUNCTION=12, DYNAMIC_CONDITION=13, RESERVED_WORD=14, FIELD=15, 
		WS=16;
	public static final int
		RULE_sql_statement = 0, RULE_end = 1, RULE_insert_statement = 2, RULE_insert_clause = 3, 
		RULE_delete_statement = 4, RULE_delete_clause = 5, RULE_update_statement = 6, 
		RULE_update_clause = 7, RULE_select_statement = 8, RULE_select_clause = 9, 
		RULE_ignore_reserved_word_clause = 10, RULE_dynamic_condition_clause = 11, 
		RULE_where_clause = 12, RULE_where_start_clause = 13, RULE_condition_clause = 14, 
		RULE_condition_item_clause = 15, RULE_logic_op_clause = 16, RULE_comparison_op_clause = 17, 
		RULE_group_clause = 18, RULE_group_op_clause = 19, RULE_order_clause = 20, 
		RULE_order_op_clause = 21, RULE_order_op_direction_clause = 22, RULE_aggregate_function_clause = 23, 
		RULE_field_clause = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "end", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "select_statement", 
			"select_clause", "ignore_reserved_word_clause", "dynamic_condition_clause", 
			"where_clause", "where_start_clause", "condition_clause", "condition_item_clause", 
			"logic_op_clause", "comparison_op_clause", "group_clause", "group_op_clause", 
			"order_clause", "order_op_clause", "order_op_direction_clause", "aggregate_function_clause", 
			"field_clause"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'By'", "'And'", "'Or'", null, "'GroupBy'", 
			"'OrderBy'", null, null, "'Selective'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"BY", "AND", "OR", "COMPARISON_OP", "GROUP_OP", "ORDER_OP", "ORDER_OP_DIRECTION", 
			"AGGREGATE_FUNCTION", "DYNAMIC_CONDITION", "RESERVED_WORD", "FIELD", 
			"WS"
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
			setState(54);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				{
				setState(50);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				{
				setState(51);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				{
				setState(52);
				update_statement();
				}
				break;
			case SELECT_ACTION:
				{
				setState(53);
				select_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(56);
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
		enterRule(_localctx, 2, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
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
	public static class Insert_statementContext extends ParserRuleContext {
		public Insert_clauseContext insert_clause() {
			return getRuleContext(Insert_clauseContext.class,0);
		}
		public Dynamic_condition_clauseContext dynamic_condition_clause() {
			return getRuleContext(Dynamic_condition_clauseContext.class,0);
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
		enterRule(_localctx, 4, RULE_insert_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			insert_clause();
			setState(61);
			dynamic_condition_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 6, RULE_insert_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(INSERT_ACTION);
			setState(64);
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
		enterRule(_localctx, 8, RULE_delete_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			delete_clause();
			setState(67);
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
		enterRule(_localctx, 10, RULE_delete_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(DELETE_ACTION);
			setState(70);
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
		public Dynamic_condition_clauseContext dynamic_condition_clause() {
			return getRuleContext(Dynamic_condition_clauseContext.class,0);
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
		enterRule(_localctx, 12, RULE_update_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			update_clause();
			setState(73);
			where_clause();
			setState(74);
			dynamic_condition_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 14, RULE_update_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(UPDATE_ACTION);
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
	public static class Select_statementContext extends ParserRuleContext {
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Group_clauseContext group_clause() {
			return getRuleContext(Group_clauseContext.class,0);
		}
		public Order_clauseContext order_clause() {
			return getRuleContext(Order_clauseContext.class,0);
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
		enterRule(_localctx, 16, RULE_select_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			select_clause();
			setState(80);
			where_clause();
			setState(81);
			group_clause();
			setState(82);
			order_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 18, RULE_select_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(SELECT_ACTION);
			setState(85);
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
		enterRule(_localctx, 20, RULE_ignore_reserved_word_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==RESERVED_WORD) {
				{
				{
				setState(87);
				match(RESERVED_WORD);
				}
				}
				setState(92);
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
	public static class Dynamic_condition_clauseContext extends ParserRuleContext {
		public TerminalNode DYNAMIC_CONDITION() { return getToken(MethodNameParser.DYNAMIC_CONDITION, 0); }
		public Dynamic_condition_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dynamic_condition_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterDynamic_condition_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitDynamic_condition_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitDynamic_condition_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dynamic_condition_clauseContext dynamic_condition_clause() throws RecognitionException {
		Dynamic_condition_clauseContext _localctx = new Dynamic_condition_clauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_dynamic_condition_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DYNAMIC_CONDITION) {
				{
				setState(93);
				match(DYNAMIC_CONDITION);
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
		public List<Condition_clauseContext> condition_clause() {
			return getRuleContexts(Condition_clauseContext.class);
		}
		public Condition_clauseContext condition_clause(int i) {
			return getRuleContext(Condition_clauseContext.class,i);
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
		enterRule(_localctx, 24, RULE_where_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BY) {
				{
				setState(96);
				where_start_clause();
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 49344L) != 0)) {
					{
					{
					setState(97);
					condition_clause();
					}
					}
					setState(102);
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
		enterRule(_localctx, 26, RULE_where_start_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
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
	public static class Condition_clauseContext extends ParserRuleContext {
		public Condition_item_clauseContext condition_item_clause() {
			return getRuleContext(Condition_item_clauseContext.class,0);
		}
		public Condition_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterCondition_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitCondition_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitCondition_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_clauseContext condition_clause() throws RecognitionException {
		Condition_clauseContext _localctx = new Condition_clauseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_condition_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			condition_item_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Logic_op_clauseContext logic_op_clause() {
			return getRuleContext(Logic_op_clauseContext.class,0);
		}
		public Comparison_op_clauseContext comparison_op_clause() {
			return getRuleContext(Comparison_op_clauseContext.class,0);
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
		enterRule(_localctx, 30, RULE_condition_item_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AND || _la==OR) {
				{
				setState(109);
				logic_op_clause();
				}
			}

			setState(112);
			field_clause();
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMPARISON_OP) {
				{
				setState(113);
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
		enterRule(_localctx, 32, RULE_logic_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
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
		enterRule(_localctx, 34, RULE_comparison_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
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
	public static class Group_clauseContext extends ParserRuleContext {
		public Group_op_clauseContext group_op_clause() {
			return getRuleContext(Group_op_clauseContext.class,0);
		}
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Group_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterGroup_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitGroup_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitGroup_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_clauseContext group_clause() throws RecognitionException {
		Group_clauseContext _localctx = new Group_clauseContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_group_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP_OP) {
				{
				setState(120);
				group_op_clause();
				setState(121);
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
	public static class Group_op_clauseContext extends ParserRuleContext {
		public TerminalNode GROUP_OP() { return getToken(MethodNameParser.GROUP_OP, 0); }
		public Group_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterGroup_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitGroup_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitGroup_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_op_clauseContext group_op_clause() throws RecognitionException {
		Group_op_clauseContext _localctx = new Group_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_group_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(GROUP_OP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_clauseContext extends ParserRuleContext {
		public Order_op_clauseContext order_op_clause() {
			return getRuleContext(Order_op_clauseContext.class,0);
		}
		public List<Field_clauseContext> field_clause() {
			return getRuleContexts(Field_clauseContext.class);
		}
		public Field_clauseContext field_clause(int i) {
			return getRuleContext(Field_clauseContext.class,i);
		}
		public List<Order_op_direction_clauseContext> order_op_direction_clause() {
			return getRuleContexts(Order_op_direction_clauseContext.class);
		}
		public Order_op_direction_clauseContext order_op_direction_clause(int i) {
			return getRuleContext(Order_op_direction_clauseContext.class,i);
		}
		public Order_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_clauseContext order_clause() throws RecognitionException {
		Order_clauseContext _localctx = new Order_clauseContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_order_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_OP) {
				{
				setState(127);
				order_op_clause();
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==RESERVED_WORD || _la==FIELD) {
					{
					{
					setState(128);
					field_clause();
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ORDER_OP_DIRECTION) {
						{
						setState(129);
						order_op_direction_clause();
						}
					}

					}
					}
					setState(136);
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
	public static class Order_op_clauseContext extends ParserRuleContext {
		public TerminalNode ORDER_OP() { return getToken(MethodNameParser.ORDER_OP, 0); }
		public Order_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_op_clauseContext order_op_clause() throws RecognitionException {
		Order_op_clauseContext _localctx = new Order_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_order_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(ORDER_OP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_op_direction_clauseContext extends ParserRuleContext {
		public TerminalNode ORDER_OP_DIRECTION() { return getToken(MethodNameParser.ORDER_OP_DIRECTION, 0); }
		public Order_op_direction_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_op_direction_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).enterOrder_op_direction_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameParserListener ) ((MethodNameParserListener)listener).exitOrder_op_direction_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameParserVisitor ) return ((MethodNameParserVisitor<? extends T>)visitor).visitOrder_op_direction_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Order_op_direction_clauseContext order_op_direction_clause() throws RecognitionException {
		Order_op_direction_clauseContext _localctx = new Order_op_direction_clauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_order_op_direction_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(ORDER_OP_DIRECTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 46, RULE_aggregate_function_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
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
		enterRule(_localctx, 48, RULE_field_clause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(146); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(145);
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
				setState(148); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
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
		"\u0004\u0001\u0010\u0097\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u00007\b"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0005"+
		"\nY\b\n\n\n\f\n\\\t\n\u0001\u000b\u0003\u000b_\b\u000b\u0001\f\u0001\f"+
		"\u0005\fc\b\f\n\f\f\ff\t\f\u0003\fh\b\f\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0003\u000fo\b\u000f\u0001\u000f\u0001\u000f\u0003"+
		"\u000fs\b\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0003\u0012|\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u0083\b\u0014\u0005"+
		"\u0014\u0085\b\u0014\n\u0014\f\u0014\u0088\t\u0014\u0003\u0014\u008a\b"+
		"\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0004\u0018\u0093\b\u0018\u000b\u0018\f\u0018\u0094"+
		"\u0001\u0018\u0000\u0000\u0019\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0\u0000\u0002\u0001"+
		"\u0000\u0006\u0007\u0001\u0000\u000e\u000f\u008b\u00006\u0001\u0000\u0000"+
		"\u0000\u0002:\u0001\u0000\u0000\u0000\u0004<\u0001\u0000\u0000\u0000\u0006"+
		"?\u0001\u0000\u0000\u0000\bB\u0001\u0000\u0000\u0000\nE\u0001\u0000\u0000"+
		"\u0000\fH\u0001\u0000\u0000\u0000\u000eL\u0001\u0000\u0000\u0000\u0010"+
		"O\u0001\u0000\u0000\u0000\u0012T\u0001\u0000\u0000\u0000\u0014Z\u0001"+
		"\u0000\u0000\u0000\u0016^\u0001\u0000\u0000\u0000\u0018g\u0001\u0000\u0000"+
		"\u0000\u001ai\u0001\u0000\u0000\u0000\u001ck\u0001\u0000\u0000\u0000\u001e"+
		"n\u0001\u0000\u0000\u0000 t\u0001\u0000\u0000\u0000\"v\u0001\u0000\u0000"+
		"\u0000${\u0001\u0000\u0000\u0000&}\u0001\u0000\u0000\u0000(\u0089\u0001"+
		"\u0000\u0000\u0000*\u008b\u0001\u0000\u0000\u0000,\u008d\u0001\u0000\u0000"+
		"\u0000.\u008f\u0001\u0000\u0000\u00000\u0092\u0001\u0000\u0000\u00002"+
		"7\u0003\u0004\u0002\u000037\u0003\b\u0004\u000047\u0003\f\u0006\u0000"+
		"57\u0003\u0010\b\u000062\u0001\u0000\u0000\u000063\u0001\u0000\u0000\u0000"+
		"64\u0001\u0000\u0000\u000065\u0001\u0000\u0000\u000078\u0001\u0000\u0000"+
		"\u000089\u0003\u0002\u0001\u00009\u0001\u0001\u0000\u0000\u0000:;\u0005"+
		"\u0000\u0000\u0001;\u0003\u0001\u0000\u0000\u0000<=\u0003\u0006\u0003"+
		"\u0000=>\u0003\u0016\u000b\u0000>\u0005\u0001\u0000\u0000\u0000?@\u0005"+
		"\u0001\u0000\u0000@A\u0003\u0014\n\u0000A\u0007\u0001\u0000\u0000\u0000"+
		"BC\u0003\n\u0005\u0000CD\u0003\u0018\f\u0000D\t\u0001\u0000\u0000\u0000"+
		"EF\u0005\u0002\u0000\u0000FG\u0003\u0014\n\u0000G\u000b\u0001\u0000\u0000"+
		"\u0000HI\u0003\u000e\u0007\u0000IJ\u0003\u0018\f\u0000JK\u0003\u0016\u000b"+
		"\u0000K\r\u0001\u0000\u0000\u0000LM\u0005\u0003\u0000\u0000MN\u0003\u0014"+
		"\n\u0000N\u000f\u0001\u0000\u0000\u0000OP\u0003\u0012\t\u0000PQ\u0003"+
		"\u0018\f\u0000QR\u0003$\u0012\u0000RS\u0003(\u0014\u0000S\u0011\u0001"+
		"\u0000\u0000\u0000TU\u0005\u0004\u0000\u0000UV\u0003\u0014\n\u0000V\u0013"+
		"\u0001\u0000\u0000\u0000WY\u0005\u000e\u0000\u0000XW\u0001\u0000\u0000"+
		"\u0000Y\\\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000"+
		"\u0000\u0000[\u0015\u0001\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000"+
		"]_\u0005\r\u0000\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000"+
		"_\u0017\u0001\u0000\u0000\u0000`d\u0003\u001a\r\u0000ac\u0003\u001c\u000e"+
		"\u0000ba\u0001\u0000\u0000\u0000cf\u0001\u0000\u0000\u0000db\u0001\u0000"+
		"\u0000\u0000de\u0001\u0000\u0000\u0000eh\u0001\u0000\u0000\u0000fd\u0001"+
		"\u0000\u0000\u0000g`\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000"+
		"h\u0019\u0001\u0000\u0000\u0000ij\u0005\u0005\u0000\u0000j\u001b\u0001"+
		"\u0000\u0000\u0000kl\u0003\u001e\u000f\u0000l\u001d\u0001\u0000\u0000"+
		"\u0000mo\u0003 \u0010\u0000nm\u0001\u0000\u0000\u0000no\u0001\u0000\u0000"+
		"\u0000op\u0001\u0000\u0000\u0000pr\u00030\u0018\u0000qs\u0003\"\u0011"+
		"\u0000rq\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000\u0000s\u001f\u0001"+
		"\u0000\u0000\u0000tu\u0007\u0000\u0000\u0000u!\u0001\u0000\u0000\u0000"+
		"vw\u0005\b\u0000\u0000w#\u0001\u0000\u0000\u0000xy\u0003&\u0013\u0000"+
		"yz\u00030\u0018\u0000z|\u0001\u0000\u0000\u0000{x\u0001\u0000\u0000\u0000"+
		"{|\u0001\u0000\u0000\u0000|%\u0001\u0000\u0000\u0000}~\u0005\t\u0000\u0000"+
		"~\'\u0001\u0000\u0000\u0000\u007f\u0086\u0003*\u0015\u0000\u0080\u0082"+
		"\u00030\u0018\u0000\u0081\u0083\u0003,\u0016\u0000\u0082\u0081\u0001\u0000"+
		"\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0085\u0001\u0000"+
		"\u0000\u0000\u0084\u0080\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000"+
		"\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000"+
		"\u0000\u0000\u0087\u008a\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000"+
		"\u0000\u0000\u0089\u007f\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000"+
		"\u0000\u0000\u008a)\u0001\u0000\u0000\u0000\u008b\u008c\u0005\n\u0000"+
		"\u0000\u008c+\u0001\u0000\u0000\u0000\u008d\u008e\u0005\u000b\u0000\u0000"+
		"\u008e-\u0001\u0000\u0000\u0000\u008f\u0090\u0005\f\u0000\u0000\u0090"+
		"/\u0001\u0000\u0000\u0000\u0091\u0093\u0007\u0001\u0000\u0000\u0092\u0091"+
		"\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0092"+
		"\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u00951\u0001"+
		"\u0000\u0000\u0000\f6Z^dgnr{\u0082\u0086\u0089\u0094";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}