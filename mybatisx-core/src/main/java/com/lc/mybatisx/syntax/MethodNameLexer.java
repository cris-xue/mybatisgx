// Generated from D:/project/mybatisx/mybatisx-core/src/test/resources\MethodName.g4 by ANTLR 4.9.1
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
		INSERT_ACTION=10, DELETE_ACTION=11, UPDATE_ACTION=12, SELECT_ACTION=13, 
		WHERE_LINK_OP=14, CONDITION_OP=15, KEY_WORD=16, FIELD=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", "WHERE_LINK_OP", 
			"CONDITION_OP", "KEY_WORD", "FIELD"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23\u00ac\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13`\n\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0082\n\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u008b\n\17\3\20\3\20\3\20\3\20"+
		"\5\20\u0091\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\5\21\u00a1\n\21\3\22\6\22\u00a4\n\22\r\22\16\22\u00a5"+
		"\3\22\6\22\u00a9\n\22\r\22\16\22\u00aa\2\2\23\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23\3\2\4\3\2C"+
		"\\\3\2c|\2\u00b5\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\3%\3\2\2\2\5+\3\2\2\2\7/\3\2\2\2\t\63\3\2\2\2\13"+
		"\67\3\2\2\2\r=\3\2\2\2\17F\3\2\2\2\21M\3\2\2\2\23Q\3\2\2\2\25_\3\2\2\2"+
		"\27a\3\2\2\2\31h\3\2\2\2\33\u0081\3\2\2\2\35\u008a\3\2\2\2\37\u0090\3"+
		"\2\2\2!\u00a0\3\2\2\2#\u00a3\3\2\2\2%&\7e\2\2&\'\7q\2\2\'(\7w\2\2()\7"+
		"p\2\2)*\7v\2\2*\4\3\2\2\2+,\7o\2\2,-\7c\2\2-.\7z\2\2.\6\3\2\2\2/\60\7"+
		"o\2\2\60\61\7k\2\2\61\62\7p\2\2\62\b\3\2\2\2\63\64\7u\2\2\64\65\7w\2\2"+
		"\65\66\7o\2\2\66\n\3\2\2\2\678\7V\2\289\7c\2\29:\7d\2\2:;\7n\2\2;<\7g"+
		"\2\2<\f\3\2\2\2=>\7N\2\2>?\7g\2\2?@\7h\2\2@A\7v\2\2AB\7L\2\2BC\7q\2\2"+
		"CD\7k\2\2DE\7p\2\2E\16\3\2\2\2FG\7J\2\2GH\7c\2\2HI\7x\2\2IJ\7k\2\2JK\7"+
		"p\2\2KL\7i\2\2L\20\3\2\2\2MN\7C\2\2NO\7u\2\2OP\7e\2\2P\22\3\2\2\2QR\7"+
		"F\2\2RS\7g\2\2ST\7u\2\2TU\7e\2\2U\24\3\2\2\2VW\7k\2\2WX\7p\2\2XY\7u\2"+
		"\2YZ\7g\2\2Z[\7t\2\2[`\7v\2\2\\]\7c\2\2]^\7f\2\2^`\7f\2\2_V\3\2\2\2_\\"+
		"\3\2\2\2`\26\3\2\2\2ab\7f\2\2bc\7g\2\2cd\7n\2\2de\7g\2\2ef\7v\2\2fg\7"+
		"g\2\2g\30\3\2\2\2hi\7w\2\2ij\7r\2\2jk\7f\2\2kl\7c\2\2lm\7v\2\2mn\7g\2"+
		"\2n\32\3\2\2\2op\7h\2\2pq\7k\2\2qr\7p\2\2r\u0082\7f\2\2st\7u\2\2tu\7g"+
		"\2\2uv\7n\2\2vw\7g\2\2wx\7e\2\2x\u0082\7v\2\2yz\7i\2\2z{\7g\2\2{\u0082"+
		"\7v\2\2|}\7s\2\2}~\7w\2\2~\177\7g\2\2\177\u0080\7t\2\2\u0080\u0082\7{"+
		"\2\2\u0081o\3\2\2\2\u0081s\3\2\2\2\u0081y\3\2\2\2\u0081|\3\2\2\2\u0082"+
		"\34\3\2\2\2\u0083\u0084\7D\2\2\u0084\u008b\7{\2\2\u0085\u0086\7C\2\2\u0086"+
		"\u0087\7p\2\2\u0087\u008b\7f\2\2\u0088\u0089\7Q\2\2\u0089\u008b\7t\2\2"+
		"\u008a\u0083\3\2\2\2\u008a\u0085\3\2\2\2\u008a\u0088\3\2\2\2\u008b\36"+
		"\3\2\2\2\u008c\u008d\7N\2\2\u008d\u0091\7v\2\2\u008e\u008f\7G\2\2\u008f"+
		"\u0091\7s\2\2\u0090\u008c\3\2\2\2\u0090\u008e\3\2\2\2\u0091 \3\2\2\2\u0092"+
		"\u0093\7I\2\2\u0093\u0094\7t\2\2\u0094\u0095\7q\2\2\u0095\u0096\7w\2\2"+
		"\u0096\u0097\7r\2\2\u0097\u0098\7D\2\2\u0098\u00a1\7{\2\2\u0099\u009a"+
		"\7Q\2\2\u009a\u009b\7t\2\2\u009b\u009c\7f\2\2\u009c\u009d\7g\2\2\u009d"+
		"\u009e\7t\2\2\u009e\u009f\7D\2\2\u009f\u00a1\7{\2\2\u00a0\u0092\3\2\2"+
		"\2\u00a0\u0099\3\2\2\2\u00a1\"\3\2\2\2\u00a2\u00a4\t\2\2\2\u00a3\u00a2"+
		"\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6"+
		"\u00a8\3\2\2\2\u00a7\u00a9\t\3\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00aa\3\2"+
		"\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab$\3\2\2\2\n\2_\u0081"+
		"\u008a\u0090\u00a0\u00a5\u00aa\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}