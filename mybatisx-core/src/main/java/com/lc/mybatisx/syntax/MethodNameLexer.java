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
		INSERT_ACTION=1, DELETE_ACTION=2, UPDATE_ACTION=3, SELECT_ACTION=4, WHERE_LINK_OP=5, 
		CONDITION_OP=6, KEY_WORD=7, FIELD=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"INSERT_ACTION", "DELETE_ACTION", "UPDATE_ACTION", "SELECT_ACTION", "WHERE_LINK_OP", 
			"CONDITION_OP", "KEY_WORD", "FIELD"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\nx\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\65\n\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6>\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\5\bq\n\b\3\t\3\t\6\tu\n\t\r\t\16\tv\2\2\n\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\3\2\4\3\2C\\\3\2c|\2\u0086\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\3\23\3\2\2\2\5\32\3\2\2\2\7!\3\2\2\2\t\64\3\2\2\2\13=\3\2\2\2"+
		"\r`\3\2\2\2\17p\3\2\2\2\21r\3\2\2\2\23\24\7k\2\2\24\25\7p\2\2\25\26\7"+
		"u\2\2\26\27\7g\2\2\27\30\7t\2\2\30\31\7v\2\2\31\4\3\2\2\2\32\33\7f\2\2"+
		"\33\34\7g\2\2\34\35\7n\2\2\35\36\7g\2\2\36\37\7v\2\2\37 \7g\2\2 \6\3\2"+
		"\2\2!\"\7w\2\2\"#\7r\2\2#$\7f\2\2$%\7c\2\2%&\7v\2\2&\'\7g\2\2\'\b\3\2"+
		"\2\2()\7h\2\2)*\7k\2\2*+\7p\2\2+\65\7f\2\2,-\7s\2\2-.\7w\2\2./\7g\2\2"+
		"/\60\7t\2\2\60\65\7{\2\2\61\62\7i\2\2\62\63\7g\2\2\63\65\7v\2\2\64(\3"+
		"\2\2\2\64,\3\2\2\2\64\61\3\2\2\2\65\n\3\2\2\2\66\67\7D\2\2\67>\7{\2\2"+
		"89\7C\2\29:\7p\2\2:>\7f\2\2;<\7Q\2\2<>\7t\2\2=\66\3\2\2\2=8\3\2\2\2=;"+
		"\3\2\2\2>\f\3\2\2\2?@\7N\2\2@a\7v\2\2AB\7N\2\2BC\7v\2\2CD\7g\2\2Da\7s"+
		"\2\2EF\7I\2\2Fa\7v\2\2GH\7I\2\2HI\7v\2\2IJ\7g\2\2Ja\7s\2\2KL\7K\2\2La"+
		"\7p\2\2MN\7K\2\2Na\7u\2\2OP\7G\2\2Pa\7s\2\2QR\7P\2\2RS\7q\2\2Sa\7v\2\2"+
		"TU\7P\2\2UV\7q\2\2VW\7v\2\2WX\7g\2\2Xa\7s\2\2YZ\7D\2\2Z[\7g\2\2[\\\7v"+
		"\2\2\\]\7y\2\2]^\7g\2\2^_\7g\2\2_a\7p\2\2`?\3\2\2\2`A\3\2\2\2`E\3\2\2"+
		"\2`G\3\2\2\2`K\3\2\2\2`M\3\2\2\2`O\3\2\2\2`Q\3\2\2\2`T\3\2\2\2`Y\3\2\2"+
		"\2a\16\3\2\2\2bc\7I\2\2cd\7t\2\2de\7q\2\2ef\7w\2\2fg\7r\2\2gh\7D\2\2h"+
		"q\7{\2\2ij\7Q\2\2jk\7t\2\2kl\7f\2\2lm\7g\2\2mn\7t\2\2no\7D\2\2oq\7{\2"+
		"\2pb\3\2\2\2pi\3\2\2\2q\20\3\2\2\2rt\t\2\2\2su\t\3\2\2ts\3\2\2\2uv\3\2"+
		"\2\2vt\3\2\2\2vw\3\2\2\2w\22\3\2\2\2\b\2\64=`pv\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}