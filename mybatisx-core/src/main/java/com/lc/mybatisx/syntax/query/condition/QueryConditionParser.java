// Generated from F:/devops/mybatisx/mybatisx-core/src/main/resources/antlr/QueryConditionParser.g4 by ANTLR 4.13.2
package com.lc.mybatisx.syntax.query.condition;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class QueryConditionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LEFT_BRACKET=1, RIGHT_BRACKET=2, INSERT_ACTION=3, DELETE_ACTION=4, UPDATE_ACTION=5, 
		SELECT_ACTION=6, BY=7, AND=8, OR=9, COMPARISON_OP=10, GROUP_OP=11, ORDER_OP=12, 
		ORDER_OP_DIRECTION=13, AGGREGATE_FUNCTION=14, DYNAMIC_CONDITION=15, RESERVED_WORD=16, 
		FIELD=17, WS=18;
	public static final int
		RULE_query_condition_statement = 0, RULE_condition_group_clause = 1, RULE_condition_clause = 2, 
		RULE_field_condition_op_clause = 3, RULE_logic_op_clause = 4, RULE_field_clause = 5, 
		RULE_comparison_op_clause = 6, RULE_left_bracket_clause = 7, RULE_right_bracket_clause = 8, 
		RULE_end = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"query_condition_statement", "condition_group_clause", "condition_clause", 
			"field_condition_op_clause", "logic_op_clause", "field_clause", "comparison_op_clause", 
			"left_bracket_clause", "right_bracket_clause", "end"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", null, null, null, null, "'By'", "'And'", "'Or'", 
			null, "'GroupBy'", "'OrderBy'", null, null, "'Selective'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LEFT_BRACKET", "RIGHT_BRACKET", "INSERT_ACTION", "DELETE_ACTION", 
			"UPDATE_ACTION", "SELECT_ACTION", "BY", "AND", "OR", "COMPARISON_OP", 
			"GROUP_OP", "ORDER_OP", "ORDER_OP_DIRECTION", "AGGREGATE_FUNCTION", "DYNAMIC_CONDITION", 
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
	public String getGrammarFileName() { return "QueryConditionParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QueryConditionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_condition_statementContext extends ParserRuleContext {
		public Condition_group_clauseContext condition_group_clause() {
			return getRuleContext(Condition_group_clauseContext.class,0);
		}
		public EndContext end() {
			return getRuleContext(EndContext.class,0);
		}
		public Query_condition_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_condition_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterQuery_condition_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitQuery_condition_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitQuery_condition_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_condition_statementContext query_condition_statement() throws RecognitionException {
		Query_condition_statementContext _localctx = new Query_condition_statementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_query_condition_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			condition_group_clause();
			setState(21);
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
	public static class Condition_group_clauseContext extends ParserRuleContext {
		public List<Condition_clauseContext> condition_clause() {
			return getRuleContexts(Condition_clauseContext.class);
		}
		public Condition_clauseContext condition_clause(int i) {
			return getRuleContext(Condition_clauseContext.class,i);
		}
		public Condition_group_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_group_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterCondition_group_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitCondition_group_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitCondition_group_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_group_clauseContext condition_group_clause() throws RecognitionException {
		Condition_group_clauseContext _localctx = new Condition_group_clauseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_condition_group_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 197378L) != 0)) {
				{
				{
				setState(23);
				condition_clause();
				}
				}
				setState(28);
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
	public static class Condition_clauseContext extends ParserRuleContext {
		public Field_condition_op_clauseContext field_condition_op_clause() {
			return getRuleContext(Field_condition_op_clauseContext.class,0);
		}
		public Logic_op_clauseContext logic_op_clause() {
			return getRuleContext(Logic_op_clauseContext.class,0);
		}
		public TerminalNode LEFT_BRACKET() { return getToken(QueryConditionParser.LEFT_BRACKET, 0); }
		public Condition_group_clauseContext condition_group_clause() {
			return getRuleContext(Condition_group_clauseContext.class,0);
		}
		public TerminalNode RIGHT_BRACKET() { return getToken(QueryConditionParser.RIGHT_BRACKET, 0); }
		public Condition_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterCondition_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitCondition_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitCondition_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_clauseContext condition_clause() throws RecognitionException {
		Condition_clauseContext _localctx = new Condition_clauseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_condition_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AND || _la==OR) {
				{
				setState(29);
				logic_op_clause();
				}
			}

			setState(37);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RESERVED_WORD:
			case FIELD:
				{
				setState(32);
				field_condition_op_clause();
				}
				break;
			case LEFT_BRACKET:
				{
				{
				setState(33);
				match(LEFT_BRACKET);
				setState(34);
				condition_group_clause();
				setState(35);
				match(RIGHT_BRACKET);
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
	public static class Field_condition_op_clauseContext extends ParserRuleContext {
		public Field_clauseContext field_clause() {
			return getRuleContext(Field_clauseContext.class,0);
		}
		public Comparison_op_clauseContext comparison_op_clause() {
			return getRuleContext(Comparison_op_clauseContext.class,0);
		}
		public Field_condition_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_condition_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterField_condition_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitField_condition_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitField_condition_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_condition_op_clauseContext field_condition_op_clause() throws RecognitionException {
		Field_condition_op_clauseContext _localctx = new Field_condition_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_field_condition_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			field_clause();
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMPARISON_OP) {
				{
				setState(40);
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
		public TerminalNode AND() { return getToken(QueryConditionParser.AND, 0); }
		public TerminalNode OR() { return getToken(QueryConditionParser.OR, 0); }
		public Logic_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterLogic_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitLogic_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitLogic_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Logic_op_clauseContext logic_op_clause() throws RecognitionException {
		Logic_op_clauseContext _localctx = new Logic_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_logic_op_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
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
	public static class Field_clauseContext extends ParserRuleContext {
		public List<TerminalNode> FIELD() { return getTokens(QueryConditionParser.FIELD); }
		public TerminalNode FIELD(int i) {
			return getToken(QueryConditionParser.FIELD, i);
		}
		public List<TerminalNode> RESERVED_WORD() { return getTokens(QueryConditionParser.RESERVED_WORD); }
		public TerminalNode RESERVED_WORD(int i) {
			return getToken(QueryConditionParser.RESERVED_WORD, i);
		}
		public Field_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterField_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitField_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitField_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Field_clauseContext field_clause() throws RecognitionException {
		Field_clauseContext _localctx = new Field_clauseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_field_clause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(46); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(45);
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
				setState(48); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
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
	public static class Comparison_op_clauseContext extends ParserRuleContext {
		public TerminalNode COMPARISON_OP() { return getToken(QueryConditionParser.COMPARISON_OP, 0); }
		public Comparison_op_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_op_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterComparison_op_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitComparison_op_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitComparison_op_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_op_clauseContext comparison_op_clause() throws RecognitionException {
		Comparison_op_clauseContext _localctx = new Comparison_op_clauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_comparison_op_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
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
	public static class Left_bracket_clauseContext extends ParserRuleContext {
		public TerminalNode LEFT_BRACKET() { return getToken(QueryConditionParser.LEFT_BRACKET, 0); }
		public Left_bracket_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_left_bracket_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterLeft_bracket_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitLeft_bracket_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitLeft_bracket_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Left_bracket_clauseContext left_bracket_clause() throws RecognitionException {
		Left_bracket_clauseContext _localctx = new Left_bracket_clauseContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_left_bracket_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
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
		public TerminalNode RIGHT_BRACKET() { return getToken(QueryConditionParser.RIGHT_BRACKET, 0); }
		public Right_bracket_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_right_bracket_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterRight_bracket_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitRight_bracket_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitRight_bracket_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Right_bracket_clauseContext right_bracket_clause() throws RecognitionException {
		Right_bracket_clauseContext _localctx = new Right_bracket_clauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_right_bracket_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
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
		public TerminalNode EOF() { return getToken(QueryConditionParser.EOF, 0); }
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryConditionParserListener ) ((QueryConditionParserListener)listener).exitEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryConditionParserVisitor ) return ((QueryConditionParserVisitor<? extends T>)visitor).visitEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
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
		"\u0004\u0001\u0012;\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0005\u0001\u0019\b\u0001\n\u0001\f\u0001\u001c\t\u0001\u0001\u0002\u0003"+
		"\u0002\u001f\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0003\u0002&\b\u0002\u0001\u0003\u0001\u0003\u0003\u0003*\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0004\u0005/\b\u0005\u000b\u0005"+
		"\f\u00050\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0000\u0002\u0004\u0006\b\n\f"+
		"\u000e\u0010\u0012\u0000\u0002\u0001\u0000\b\t\u0001\u0000\u0010\u0011"+
		"5\u0000\u0014\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000\u0000"+
		"\u0004\u001e\u0001\u0000\u0000\u0000\u0006\'\u0001\u0000\u0000\u0000\b"+
		"+\u0001\u0000\u0000\u0000\n.\u0001\u0000\u0000\u0000\f2\u0001\u0000\u0000"+
		"\u0000\u000e4\u0001\u0000\u0000\u0000\u00106\u0001\u0000\u0000\u0000\u0012"+
		"8\u0001\u0000\u0000\u0000\u0014\u0015\u0003\u0002\u0001\u0000\u0015\u0016"+
		"\u0003\u0012\t\u0000\u0016\u0001\u0001\u0000\u0000\u0000\u0017\u0019\u0003"+
		"\u0004\u0002\u0000\u0018\u0017\u0001\u0000\u0000\u0000\u0019\u001c\u0001"+
		"\u0000\u0000\u0000\u001a\u0018\u0001\u0000\u0000\u0000\u001a\u001b\u0001"+
		"\u0000\u0000\u0000\u001b\u0003\u0001\u0000\u0000\u0000\u001c\u001a\u0001"+
		"\u0000\u0000\u0000\u001d\u001f\u0003\b\u0004\u0000\u001e\u001d\u0001\u0000"+
		"\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f%\u0001\u0000\u0000"+
		"\u0000 &\u0003\u0006\u0003\u0000!\"\u0005\u0001\u0000\u0000\"#\u0003\u0002"+
		"\u0001\u0000#$\u0005\u0002\u0000\u0000$&\u0001\u0000\u0000\u0000% \u0001"+
		"\u0000\u0000\u0000%!\u0001\u0000\u0000\u0000&\u0005\u0001\u0000\u0000"+
		"\u0000\')\u0003\n\u0005\u0000(*\u0003\f\u0006\u0000)(\u0001\u0000\u0000"+
		"\u0000)*\u0001\u0000\u0000\u0000*\u0007\u0001\u0000\u0000\u0000+,\u0007"+
		"\u0000\u0000\u0000,\t\u0001\u0000\u0000\u0000-/\u0007\u0001\u0000\u0000"+
		".-\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u00000.\u0001\u0000\u0000"+
		"\u000001\u0001\u0000\u0000\u00001\u000b\u0001\u0000\u0000\u000023\u0005"+
		"\n\u0000\u00003\r\u0001\u0000\u0000\u000045\u0005\u0001\u0000\u00005\u000f"+
		"\u0001\u0000\u0000\u000067\u0005\u0002\u0000\u00007\u0011\u0001\u0000"+
		"\u0000\u000089\u0005\u0000\u0000\u00019\u0013\u0001\u0000\u0000\u0000"+
		"\u0005\u001a\u001e%)0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}