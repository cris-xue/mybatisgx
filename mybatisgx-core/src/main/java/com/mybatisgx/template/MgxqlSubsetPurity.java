package com.mybatisgx.template;

import com.mybatisgx.exception.MybatisgxException;
import org.apache.commons.lang3.StringUtils;

/**
 * MGXQL 子集文本纯净性断言。
 *
 * @author 薛承城
 * @date 2026/7/23
 */
public class MgxqlSubsetPurity {

    private static final String[] BLACKLIST_TOKENS = new String[]{
            "#condition", "#for", "#bind", "#include", "<xml", "#{", "${"
    };

    public static void assertPure(String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        String lowerText = text.toLowerCase();
        for (String token : BLACKLIST_TOKENS) {
            if (lowerText.contains(token.toLowerCase())) {
                throw new MybatisgxException("MGXQL 子集文本包含黑名单 token: " + token);
            }
        }
        assertNoNestedDynamicGate(text);
    }

    private static void assertNoNestedDynamicGate(String text) {
        int index = 0;
        while (index < text.length()) {
            int gateStart = findNextGate(text, index);
            if (gateStart < 0) {
                return;
            }
            int bodyStart = findBodyStart(text, gateStart);
            if (bodyStart < 0) {
                return;
            }
            int bodyEnd = findMatchingRightSquare(text, bodyStart);
            if (bodyEnd < 0) {
                throw new MybatisgxException("MGXQL 子集动态门方括号不匹配");
            }
            String body = text.substring(bodyStart + 1, bodyEnd);
            if (body.contains("#[") || body.contains("#if(") || body.contains("#choose[")) {
                throw new MybatisgxException("MGXQL 子集动态门 body 不允许嵌套动态门");
            }
            index = bodyEnd + 1;
        }
    }

    private static int findNextGate(String text, int start) {
        int bracket = text.indexOf("#[", start);
        int ifGate = text.indexOf("#if(", start);
        int choose = text.indexOf("#choose[", start);
        int result = minPositive(bracket, ifGate);
        return minPositive(result, choose);
    }

    private static int minPositive(int a, int b) {
        if (a < 0) {
            return b;
        }
        if (b < 0) {
            return a;
        }
        return Math.min(a, b);
    }

    private static int findBodyStart(String text, int gateStart) {
        if (text.startsWith("#[", gateStart) || text.startsWith("#choose[", gateStart)) {
            return text.indexOf('[', gateStart);
        }
        if (text.startsWith("#if(", gateStart)) {
            int guardEnd = findMatchingRightParenthesis(text, text.indexOf('(', gateStart));
            if (guardEnd < 0) {
                throw new MybatisgxException("MGXQL 子集 #if guard 圆括号不匹配");
            }
            return text.indexOf('[', guardEnd);
        }
        return -1;
    }

    private static int findMatchingRightParenthesis(String text, int leftIndex) {
        int depth = 0;
        for (int i = leftIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int findMatchingRightSquare(String text, int leftIndex) {
        int depth = 0;
        for (int i = leftIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}
