// Generated from F:/ec/lc/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
package com.lc.mybatisx.syntax;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MethodNameParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INSERT_ACTION=1, DELETE_ACTION=2, UPDATE_ACTION=3, SELECT_ACTION=4, WHERE_LINK_OP=5, 
		CONDITION_OP=6, KEY_WORD=7, FIELD=8;
	public static final int
		RULE_sql_statement = 0, RULE_insert_statement = 1, RULE_insert_clause = 2, 
		RULE_delete_statement = 3, RULE_delete_clause = 4, RULE_update_statement = 5, 
		RULE_update_clause = 6, RULE_select_statement = 7, RULE_select_clause = 8, 
		RULE_where_clause = 9, RULE_where_item = 10, RULE_where_link_op_clause = 11, 
		RULE_where_op_clause = 12, RULE_field_clause = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql_statement", "insert_statement", "insert_clause", "delete_statement", 
			"delete_clause", "update_statement", "update_clause", "select_statement", 
			"select_clause", "where_clause", "where_item", "where_link_op_clause", 
			"where_op_clause", "field_clause"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'insert'", "'delete'", "'update'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", 
			"WHERE_LINK_OP", "CONDITION_OP", "KEY_WORD", "FIELD"
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
	public String getGrammarFileName() { return "MethodName.g4"; }

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

	public static class Sql_statementContext extends ParserRuleContext {
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
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterSql_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitSql_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitSql_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_statementContext sql_statement() throws RecognitionException {
		Sql_statementContext _localctx = new Sql_statementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sql_statement);
		try {
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INSERT_ACTION:
				enterOuterAlt(_localctx, 1);
				{
				setState(28);
				insert_statement();
				}
				break;
			case DELETE_ACTION:
				enterOuterAlt(_localctx, 2);
				{
				setState(29);
				delete_statement();
				}
				break;
			case UPDATE_ACTION:
				enterOuterAlt(_localctx, 3);
				{
				setState(30);
				update_statement();
				}
				break;
			case EOF:
			case SELECT_ACTION:
			case WHERE_LINK_OP:
				enterOuterAlt(_localctx, 4);
				{
				setState(31);
				select_statement();
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
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterInsert_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitInsert_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitInsert_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_statementContext insert_statement() throws RecognitionException {
		Insert_statementContext _localctx = new Insert_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_insert_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
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

	public static class Insert_clauseContext extends ParserRuleContext {
		public TerminalNode INSERT_ACTION() { return getToken(MethodNameParser.INSERT_ACTION, 0); }
		public Insert_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterInsert_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitInsert_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitInsert_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_clauseContext insert_clause() throws RecognitionException {
		Insert_clauseContext _localctx = new Insert_clauseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_insert_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
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
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterDelete_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitDelete_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitDelete_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_statementContext delete_statement() throws RecognitionException {
		Delete_statementContext _localctx = new Delete_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_delete_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			delete_clause();
			setState(40);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE_LINK_OP) {
				{
				setState(39);
				where_clause();
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

	public static class Delete_clauseContext extends ParserRuleContext {
		public TerminalNode DELETE_ACTION() { return getToken(MethodNameParser.DELETE_ACTION, 0); }
		public Delete_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterDelete_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitDelete_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitDelete_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_clauseContext delete_clause() throws RecognitionException {
		Delete_clauseContext _localctx = new Delete_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_delete_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
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
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterUpdate_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitUpdate_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitUpdate_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_statementContext update_statement() throws RecognitionException {
		Update_statementContext _localctx = new Update_statementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_update_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			update_clause();
			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE_LINK_OP) {
				{
				setState(45);
				where_clause();
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

	public static class Update_clauseContext extends ParserRuleContext {
		public TerminalNode UPDATE_ACTION() { return getToken(MethodNameParser.UPDATE_ACTION, 0); }
		public Update_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterUpdate_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitUpdate_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitUpdate_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_clauseContext update_clause() throws RecognitionException {
		Update_clauseContext _localctx = new Update_clauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_update_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
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

	public static class Select_statementContext extends ParserRuleContext {
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Select_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterSelect_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitSelect_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitSelect_statement(this);
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
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SELECT_ACTION) {
				{
				setState(50);
				select_clause();
				}
			}

			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE_LINK_OP) {
				{
				setState(53);
				where_clause();
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

	public static class Select_clauseContext extends ParserRuleContext {
		public TerminalNode SELECT_ACTION() { return getToken(MethodNameParser.SELECT_ACTION, 0); }
		public Select_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterSelect_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitSelect_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitSelect_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_clauseContext select_clause() throws RecognitionException {
		Select_clauseContext _localctx = new Select_clauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_select_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
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

	public static class Where_clauseContext extends ParserRuleContext {
		public List<Where_itemContext> where_item() {
			return getRuleContexts(Where_itemContext.class);
		}
		public Where_itemContext where_item(int i) {
			return getRuleContext(Where_itemContext.class,i);
		}
		public Where_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterWhere_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitWhere_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitWhere_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_clauseContext where_clause() throws RecognitionException {
		Where_clauseContext _localctx = new Where_clauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_where_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(58);
				where_item();
				}
				}
				setState(61); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WHERE_LINK_OP );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Where_itemContext extends ParserRuleContext {
		public Where_link_op_clauseContext where_link_op_clause() {
			return getRuleContext(Where_link_op_clauseContext.class,0);
		}
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Where_op_clauseContext where_op_clause() {
			return getRuleContext(Where_op_clauseContext.class,0);
		}
		public Where_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterWhere_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitWhere_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitWhere_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_itemContext where_item() throws RecognitionException {
		Where_itemContext _localctx = new Where_itemContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_where_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			where_link_op_clause();
			setState(64);
			field_clause();
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CONDITION_OP) {
				{
				setState(65);
				where_op_clause();
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

	public static class Where_link_op_clauseContext extends ParserRuleContext {
		public TerminalNode WHERE_LINK_OP() { return getToken(MethodNameParser.WHERE_LINK_OP, 0); }
		public Where_link_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_link_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterWhere_link_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitWhere_link_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitWhere_link_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_link_op_clauseContext where_link_op_clause() throws RecognitionException {
		Where_link_op_clauseContext _localctx = new Where_link_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_where_link_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(WHERE_LINK_OP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Where_op_clauseContext extends ParserRuleContext {
		public TerminalNode CONDITION_OP() { return getToken(MethodNameParser.CONDITION_OP, 0); }
		public Where_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterWhere_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitWhere_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitWhere_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_op_clauseContext where_op_clause() throws RecognitionException {
		Where_op_clauseContext _localctx = new Where_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_where_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(CONDITION_OP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

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
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterField_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitField_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitField_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_clauseContext field_clause() throws RecognitionException {
		Field_clauseContext _localctx = new Field_clauseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_field_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(72);
				match(FIELD);
				}
				}
				setState(75); 
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\nP\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\5\2#\n\2\3\3\3\3\3"+
		"\4\3\4\3\5\3\5\5\5+\n\5\3\6\3\6\3\7\3\7\5\7\61\n\7\3\b\3\b\3\t\5\t\66"+
		"\n\t\3\t\5\t9\n\t\3\n\3\n\3\13\6\13>\n\13\r\13\16\13?\3\f\3\f\3\f\5\f"+
		"E\n\f\3\r\3\r\3\16\3\16\3\17\6\17L\n\17\r\17\16\17M\3\17\2\2\20\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\2\2\2K\2\"\3\2\2\2\4$\3\2\2\2\6&\3\2\2"+
		"\2\b(\3\2\2\2\n,\3\2\2\2\f.\3\2\2\2\16\62\3\2\2\2\20\65\3\2\2\2\22:\3"+
		"\2\2\2\24=\3\2\2\2\26A\3\2\2\2\30F\3\2\2\2\32H\3\2\2\2\34K\3\2\2\2\36"+
		"#\5\4\3\2\37#\5\b\5\2 #\5\f\7\2!#\5\20\t\2\"\36\3\2\2\2\"\37\3\2\2\2\""+
		" \3\2\2\2\"!\3\2\2\2#\3\3\2\2\2$%\5\6\4\2%\5\3\2\2\2&\'\7\3\2\2\'\7\3"+
		"\2\2\2(*\5\n\6\2)+\5\24\13\2*)\3\2\2\2*+\3\2\2\2+\t\3\2\2\2,-\7\4\2\2"+
		"-\13\3\2\2\2.\60\5\16\b\2/\61\5\24\13\2\60/\3\2\2\2\60\61\3\2\2\2\61\r"+
		"\3\2\2\2\62\63\7\5\2\2\63\17\3\2\2\2\64\66\5\22\n\2\65\64\3\2\2\2\65\66"+
		"\3\2\2\2\668\3\2\2\2\679\5\24\13\28\67\3\2\2\289\3\2\2\29\21\3\2\2\2:"+
		";\7\6\2\2;\23\3\2\2\2<>\5\26\f\2=<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2"+
		"\2@\25\3\2\2\2AB\5\30\r\2BD\5\34\17\2CE\5\32\16\2DC\3\2\2\2DE\3\2\2\2"+
		"E\27\3\2\2\2FG\7\7\2\2G\31\3\2\2\2HI\7\b\2\2I\33\3\2\2\2JL\7\n\2\2KJ\3"+
		"\2\2\2LM\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\35\3\2\2\2\n\"*\60\658?DM";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}