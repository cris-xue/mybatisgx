// Generated from D:/project/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		INSERT_ACTION=10, DELETE_ACTION=11, UPDATE_ACTION=12, SELECT_ACTION=13, 
		WHERE_LINK_OP=14, CONDITION_OP=15, KEY_WORD=16, FIELD=17;
	public static final int
		RULE_ql_statement = 0, RULE_select_statement = 1, RULE_select_clause = 2, 
		RULE_aggregate_expression = 3, RULE_from_clause = 4, RULE_table_clause = 5, 
		RULE_join_clause = 6, RULE_where_clause = 7, RULE_where_link_op_clause = 8, 
		RULE_where_op_clause = 9, RULE_groupby_clause = 10, RULE_groupby_item = 11, 
		RULE_having_clause = 12, RULE_orderby_clause = 13, RULE_orderby_item = 14, 
		RULE_field_clause = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"ql_statement", "select_statement", "select_clause", "aggregate_expression", 
			"from_clause", "table_clause", "join_clause", "where_clause", "where_link_op_clause", 
			"where_op_clause", "groupby_clause", "groupby_item", "having_clause", 
			"orderby_clause", "orderby_item", "field_clause"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'count'", "'max'", "'min'", "'sum'", "'Table'", "'LeftJoin'", 
			"'Having'", "'Asc'", "'Desc'", null, "'delete'", "'update'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "INSERT_ACTION", 
			"DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", "WHERE_LINK_OP", "CONDITION_OP", 
			"KEY_WORD", "FIELD"
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

	public static class Ql_statementContext extends ParserRuleContext {
		public Select_statementContext select_statement() {
			return getRuleContext(Select_statementContext.class,0);
		}
		public Ql_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ql_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterQl_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitQl_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitQl_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ql_statementContext ql_statement() throws RecognitionException {
		Ql_statementContext _localctx = new Ql_statementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_ql_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			select_statement();
			}
		}
		catch (RecognitionException re) {
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
		public From_clauseContext from_clause() {
			return getRuleContext(From_clauseContext.class,0);
		}
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public Aggregate_expressionContext aggregate_expression() {
			return getRuleContext(Aggregate_expressionContext.class,0);
		}
		public Table_clauseContext table_clause() {
			return getRuleContext(Table_clauseContext.class,0);
		}
		public Join_clauseContext join_clause() {
			return getRuleContext(Join_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Groupby_clauseContext groupby_clause() {
			return getRuleContext(Groupby_clauseContext.class,0);
		}
		public Having_clauseContext having_clause() {
			return getRuleContext(Having_clauseContext.class,0);
		}
		public Orderby_clauseContext orderby_clause() {
			return getRuleContext(Orderby_clauseContext.class,0);
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
		enterRule(_localctx, 2, RULE_select_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INSERT_ACTION) | (1L << DELETE_ACTION) | (1L << UPDATE_ACTION) | (1L << SELECT_ACTION))) != 0)) {
				{
				setState(34);
				select_clause();
				}
			}

			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3))) != 0)) {
				{
				setState(37);
				aggregate_expression();
				}
			}

			setState(40);
			from_clause();
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(41);
				table_clause();
				}
			}

			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(44);
				join_clause();
				}
			}

			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE_LINK_OP) {
				{
				setState(47);
				where_clause();
				}
			}

			setState(51);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(50);
				groupby_clause();
				}
				break;
			}
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(53);
				having_clause();
				}
			}

			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==KEY_WORD) {
				{
				setState(56);
				orderby_clause();
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
		public TerminalNode INSERT_ACTION() { return getToken(MethodNameParser.INSERT_ACTION, 0); }
		public TerminalNode DELETE_ACTION() { return getToken(MethodNameParser.DELETE_ACTION, 0); }
		public TerminalNode UPDATE_ACTION() { return getToken(MethodNameParser.UPDATE_ACTION, 0); }
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
		enterRule(_localctx, 4, RULE_select_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INSERT_ACTION) | (1L << DELETE_ACTION) | (1L << UPDATE_ACTION) | (1L << SELECT_ACTION))) != 0)) ) {
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

	public static class Aggregate_expressionContext extends ParserRuleContext {
		public Aggregate_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterAggregate_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitAggregate_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitAggregate_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_expressionContext aggregate_expression() throws RecognitionException {
		Aggregate_expressionContext _localctx = new Aggregate_expressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_aggregate_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3))) != 0)) ) {
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

	public static class From_clauseContext extends ParserRuleContext {
		public From_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_from_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterFrom_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitFrom_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitFrom_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final From_clauseContext from_clause() throws RecognitionException {
		From_clauseContext _localctx = new From_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_from_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Table_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterTable_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitTable_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitTable_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_clauseContext table_clause() throws RecognitionException {
		Table_clauseContext _localctx = new Table_clauseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_table_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(T__4);
			setState(66);
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

	public static class Join_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Join_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterJoin_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitJoin_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitJoin_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Join_clauseContext join_clause() throws RecognitionException {
		Join_clauseContext _localctx = new Join_clauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_join_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__5);
			setState(69);
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

	public static class Where_clauseContext extends ParserRuleContext {
		public List<Where_link_op_clauseContext> where_link_op_clause() {
			return getRuleContexts(Where_link_op_clauseContext.class);
		}
		public Where_link_op_clauseContext where_link_op_clause(int i) {
			return getRuleContext(Where_link_op_clauseContext.class,i);
		}
		public List<Field_clauseContext> field_clause() {
			return getRuleContexts(Field_clauseContext.class);
		}
		public Field_clauseContext field_clause(int i) {
			return getRuleContext(Field_clauseContext.class,i);
		}
		public List<Where_op_clauseContext> where_op_clause() {
			return getRuleContexts(Where_op_clauseContext.class);
		}
		public Where_op_clauseContext where_op_clause(int i) {
			return getRuleContext(Where_op_clauseContext.class,i);
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
		enterRule(_localctx, 14, RULE_where_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(71);
				where_link_op_clause();
				setState(72);
				field_clause();
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION_OP) {
					{
					setState(73);
					where_op_clause();
					}
				}

				}
				}
				setState(78); 
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
		enterRule(_localctx, 16, RULE_where_link_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
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
		enterRule(_localctx, 18, RULE_where_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
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

	public static class Groupby_clauseContext extends ParserRuleContext {
		public TerminalNode KEY_WORD() { return getToken(MethodNameParser.KEY_WORD, 0); }
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Groupby_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupby_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterGroupby_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitGroupby_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitGroupby_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Groupby_clauseContext groupby_clause() throws RecognitionException {
		Groupby_clauseContext _localctx = new Groupby_clauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(KEY_WORD);
			setState(85);
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

	public static class Groupby_itemContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Groupby_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupby_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterGroupby_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitGroupby_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitGroupby_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Groupby_itemContext groupby_item() throws RecognitionException {
		Groupby_itemContext _localctx = new Groupby_itemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_groupby_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
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

	public static class Having_clauseContext extends ParserRuleContext {
		public Having_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterHaving_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitHaving_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitHaving_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Having_clauseContext having_clause() throws RecognitionException {
		Having_clauseContext _localctx = new Having_clauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Orderby_clauseContext extends ParserRuleContext {
		public TerminalNode KEY_WORD() { return getToken(MethodNameParser.KEY_WORD, 0); }
		public List<Field_clauseContext> field_clause() {
			return getRuleContexts(Field_clauseContext.class);
		}
		public Field_clauseContext field_clause(int i) {
			return getRuleContext(Field_clauseContext.class,i);
		}
		public Orderby_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderby_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterOrderby_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitOrderby_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitOrderby_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Orderby_clauseContext orderby_clause() throws RecognitionException {
		Orderby_clauseContext _localctx = new Orderby_clauseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_orderby_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(KEY_WORD);
			setState(96); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(92);
				field_clause();
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__7 || _la==T__8) {
					{
					setState(93);
					_la = _input.LA(1);
					if ( !(_la==T__7 || _la==T__8) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				}
				setState(98); 
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

	public static class Orderby_itemContext extends ParserRuleContext {
		public List<Field_clauseContext> field_clause() {
			return getRuleContexts(Field_clauseContext.class);
		}
		public Field_clauseContext field_clause(int i) {
			return getRuleContext(Field_clauseContext.class,i);
		}
		public Orderby_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderby_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).enterOrderby_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MethodNameListener ) ((MethodNameListener)listener).exitOrderby_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MethodNameVisitor ) return ((MethodNameVisitor<? extends T>)visitor).visitOrderby_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Orderby_itemContext orderby_item() throws RecognitionException {
		Orderby_itemContext _localctx = new Orderby_itemContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_orderby_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(100);
				field_clause();
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__7 || _la==T__8) {
					{
					setState(101);
					_la = _input.LA(1);
					if ( !(_la==T__7 || _la==T__8) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				}
				setState(106); 
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
		enterRule(_localctx, 30, RULE_field_clause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(109); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(108);
					match(FIELD);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(111); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23t\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2\3\3\5\3"+
		"&\n\3\3\3\5\3)\n\3\3\3\3\3\5\3-\n\3\3\3\5\3\60\n\3\3\3\5\3\63\n\3\3\3"+
		"\5\3\66\n\3\3\3\5\39\n\3\3\3\5\3<\n\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\t\5\tM\n\t\6\tO\n\t\r\t\16\tP\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17\5\17a\n\17\6\17c\n"+
		"\17\r\17\16\17d\3\20\3\20\5\20i\n\20\6\20k\n\20\r\20\16\20l\3\21\6\21"+
		"p\n\21\r\21\16\21q\3\21\2\2\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		" \2\5\3\2\f\17\3\2\3\6\3\2\n\13\2r\2\"\3\2\2\2\4%\3\2\2\2\6=\3\2\2\2\b"+
		"?\3\2\2\2\nA\3\2\2\2\fC\3\2\2\2\16F\3\2\2\2\20N\3\2\2\2\22R\3\2\2\2\24"+
		"T\3\2\2\2\26V\3\2\2\2\30Y\3\2\2\2\32[\3\2\2\2\34]\3\2\2\2\36j\3\2\2\2"+
		" o\3\2\2\2\"#\5\4\3\2#\3\3\2\2\2$&\5\6\4\2%$\3\2\2\2%&\3\2\2\2&(\3\2\2"+
		"\2\')\5\b\5\2(\'\3\2\2\2()\3\2\2\2)*\3\2\2\2*,\5\n\6\2+-\5\f\7\2,+\3\2"+
		"\2\2,-\3\2\2\2-/\3\2\2\2.\60\5\16\b\2/.\3\2\2\2/\60\3\2\2\2\60\62\3\2"+
		"\2\2\61\63\5\20\t\2\62\61\3\2\2\2\62\63\3\2\2\2\63\65\3\2\2\2\64\66\5"+
		"\26\f\2\65\64\3\2\2\2\65\66\3\2\2\2\668\3\2\2\2\679\5\32\16\28\67\3\2"+
		"\2\289\3\2\2\29;\3\2\2\2:<\5\34\17\2;:\3\2\2\2;<\3\2\2\2<\5\3\2\2\2=>"+
		"\t\2\2\2>\7\3\2\2\2?@\t\3\2\2@\t\3\2\2\2AB\3\2\2\2B\13\3\2\2\2CD\7\7\2"+
		"\2DE\5 \21\2E\r\3\2\2\2FG\7\b\2\2GH\5 \21\2H\17\3\2\2\2IJ\5\22\n\2JL\5"+
		" \21\2KM\5\24\13\2LK\3\2\2\2LM\3\2\2\2MO\3\2\2\2NI\3\2\2\2OP\3\2\2\2P"+
		"N\3\2\2\2PQ\3\2\2\2Q\21\3\2\2\2RS\7\20\2\2S\23\3\2\2\2TU\7\21\2\2U\25"+
		"\3\2\2\2VW\7\22\2\2WX\5 \21\2X\27\3\2\2\2YZ\5 \21\2Z\31\3\2\2\2[\\\7\t"+
		"\2\2\\\33\3\2\2\2]b\7\22\2\2^`\5 \21\2_a\t\4\2\2`_\3\2\2\2`a\3\2\2\2a"+
		"c\3\2\2\2b^\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2e\35\3\2\2\2fh\5 \21"+
		"\2gi\t\4\2\2hg\3\2\2\2hi\3\2\2\2ik\3\2\2\2jf\3\2\2\2kl\3\2\2\2lj\3\2\2"+
		"\2lm\3\2\2\2m\37\3\2\2\2np\7\23\2\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3"+
		"\2\2\2r!\3\2\2\2\21%(,/\62\658;LP`dhlq";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}