// Generated from F:/ec/lc/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
package com.lc.mybatisx.syntax;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MethodNameLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		SELECT_ACTION=10, WHERE=11, LINK_OP=12, CONDITION_OP=13, KEY_WORD=14, 
		FIELD=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"SELECT_ACTION", "WHERE", "LINK_OP", "CONDITION_OP", "KEY_WORD", "FIELD"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'count'", "'max'", "'min'", "'sum'", "'Table'", "'LeftJoin'", 
			"'Having'", "'Asc'", "'Desc'", null, "'By'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "SELECT_ACTION", 
			"WHERE", "LINK_OP", "CONDITION_OP", "KEY_WORD", "FIELD"
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


	public MethodNameLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MethodName.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\21\u008f\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\5\13h\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\5\rr\n\r\3\16\3\16\3"+
		"\16\3\16\5\16x\n\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\5\17\u0088\n\17\3\20\3\20\6\20\u008c\n\20\r\20\16"+
		"\20\u008d\2\2\21\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21\3\2\4\3\2C\\\3\2c|\2\u0097\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3!\3\2\2\2\5\'\3\2\2\2\7+\3\2\2\2\t/\3"+
		"\2\2\2\13\63\3\2\2\2\r9\3\2\2\2\17B\3\2\2\2\21I\3\2\2\2\23M\3\2\2\2\25"+
		"g\3\2\2\2\27i\3\2\2\2\31q\3\2\2\2\33w\3\2\2\2\35\u0087\3\2\2\2\37\u0089"+
		"\3\2\2\2!\"\7e\2\2\"#\7q\2\2#$\7w\2\2$%\7p\2\2%&\7v\2\2&\4\3\2\2\2\'("+
		"\7o\2\2()\7c\2\2)*\7z\2\2*\6\3\2\2\2+,\7o\2\2,-\7k\2\2-.\7p\2\2.\b\3\2"+
		"\2\2/\60\7u\2\2\60\61\7w\2\2\61\62\7o\2\2\62\n\3\2\2\2\63\64\7V\2\2\64"+
		"\65\7c\2\2\65\66\7d\2\2\66\67\7n\2\2\678\7g\2\28\f\3\2\2\29:\7N\2\2:;"+
		"\7g\2\2;<\7h\2\2<=\7v\2\2=>\7L\2\2>?\7q\2\2?@\7k\2\2@A\7p\2\2A\16\3\2"+
		"\2\2BC\7J\2\2CD\7c\2\2DE\7x\2\2EF\7k\2\2FG\7p\2\2GH\7i\2\2H\20\3\2\2\2"+
		"IJ\7C\2\2JK\7u\2\2KL\7e\2\2L\22\3\2\2\2MN\7F\2\2NO\7g\2\2OP\7u\2\2PQ\7"+
		"e\2\2Q\24\3\2\2\2RS\7u\2\2ST\7g\2\2TU\7n\2\2UV\7g\2\2VW\7e\2\2Wh\7v\2"+
		"\2XY\7i\2\2YZ\7g\2\2Zh\7v\2\2[\\\7s\2\2\\]\7w\2\2]^\7g\2\2^_\7t\2\2_h"+
		"\7{\2\2`a\7h\2\2ab\7k\2\2bc\7p\2\2ch\7f\2\2de\7c\2\2ef\7f\2\2fh\7f\2\2"+
		"gR\3\2\2\2gX\3\2\2\2g[\3\2\2\2g`\3\2\2\2gd\3\2\2\2gh\3\2\2\2h\26\3\2\2"+
		"\2ij\7D\2\2jk\7{\2\2k\30\3\2\2\2lm\7C\2\2mn\7p\2\2nr\7f\2\2op\7Q\2\2p"+
		"r\7t\2\2ql\3\2\2\2qo\3\2\2\2r\32\3\2\2\2st\7N\2\2tx\7v\2\2uv\7G\2\2vx"+
		"\7s\2\2ws\3\2\2\2wu\3\2\2\2x\34\3\2\2\2yz\7I\2\2z{\7t\2\2{|\7q\2\2|}\7"+
		"w\2\2}~\7r\2\2~\177\7D\2\2\177\u0088\7{\2\2\u0080\u0081\7Q\2\2\u0081\u0082"+
		"\7t\2\2\u0082\u0083\7f\2\2\u0083\u0084\7g\2\2\u0084\u0085\7t\2\2\u0085"+
		"\u0086\7D\2\2\u0086\u0088\7{\2\2\u0087y\3\2\2\2\u0087\u0080\3\2\2\2\u0088"+
		"\36\3\2\2\2\u0089\u008b\t\2\2\2\u008a\u008c\t\3\2\2\u008b\u008a\3\2\2"+
		"\2\u008c\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e "+
		"\3\2\2\2\b\2gqw\u0087\u008d\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}