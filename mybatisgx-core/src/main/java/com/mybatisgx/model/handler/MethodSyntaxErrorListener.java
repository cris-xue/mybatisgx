package com.mybatisgx.model.handler;

import com.mybatisgx.exception.MybatisgxException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法名语法解析错误监听器
 *
 * @author 薛承城
 * @date 2026/1/5 18:07
 */
public class MethodSyntaxErrorListener extends BaseErrorListener {

    private static final Map<String, String> FRIENDLY_TOKEN_MAP = new HashMap<>();

    static {
        FRIENDLY_TOKEN_MAP.put("LIMIT_TOP", "top");
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException recognitionException) {
        Token token = (Token) offendingSymbol;
        String tokenSource = token.getTokenSource().getInputStream().toString();
        String errorDescription;
        if (recognitionException != null) {
            IntervalSet expected = recognitionException.getExpectedTokens();
            String friendlyExpected = this.friendlyExpected(expected, recognizer.getVocabulary());
            errorDescription = String.format("%s 语法错误，在(%s:%s)，非法输入：%s，当前位置可使用：%s", tokenSource, line, charPositionInLine, token.getText(), friendlyExpected);
        } else {
            errorDescription = String.format("%s 语法错误", tokenSource);
        }
        throw new MybatisgxException(errorDescription);
    }

    private String friendlyExpected(IntervalSet expected, Vocabulary vocab) {
        List<String> result = new ArrayList<>();
        for (int t : expected.toArray()) {
            String name = vocab.getDisplayName(t);
            if (isInternalToken(name)) {
                continue;
            }
            result.add(FRIENDLY_TOKEN_MAP.getOrDefault(name, name));
        }
        return String.join("、", result).replaceAll("'", "");
    }

    private boolean isInternalToken(String name) {
        return "EOF".equals(name) || name.equals("RESERVED_WORD");
    }
}
