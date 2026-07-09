package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.MgxsqlContext;

/**
 * mgxsql 语法辅助工具类，提供关键字检测、词边界判断、参数读取等静态方法
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public final class MgxsqlSyntaxHelper {

    /**
     * 子句关键字表：这些关键字在 WHERE 域中出现时，应关闭 &lt;where&gt; 标签
     * 每个 entry 是关键字组合（小写），以空格分隔的视为多 token 组合
     */
    public static final String[] CLAUSE_KEYWORDS = {
            "order by", "group by", "having", "limit",
            "union", "union all", "intersect", "except",
            "for update"
    };

    private MgxsqlSyntaxHelper() {
    }

    // ==================== 文本版辅助方法（String + int 参数） ====================

    /**
     * 检测文本指定位置是否匹配关键字（不区分大小写）
     */
    public static boolean isKeywordAt(String text, int pos, String keyword) {
        if (pos + keyword.length() > text.length()) {
            return false;
        }
        return text.substring(pos, pos + keyword.length()).equalsIgnoreCase(keyword);
    }

    /**
     * 检测文本指定位置前方是否为词边界
     */
    public static boolean isWordBoundaryBefore(String text, int pos) {
        if (pos == 0) {
            return true;
        }
        char prev = text.charAt(pos - 1);
        return !Character.isLetterOrDigit(prev) && prev != '_' && prev != '.';
    }

    /**
     * 检测文本指定位置后方是否为词边界
     */
    public static boolean isWordBoundaryAfter(String text, int pos) {
        if (pos >= text.length()) {
            return true;
        }
        char next = text.charAt(pos);
        return !Character.isLetterOrDigit(next) && next != '_' && next != '.';
    }

    /**
     * 从文本指定位置开始查找标识符结束位置
     */
    public static int findIdentifierEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        return pos;
    }

    /**
     * 从文本指定位置开始查找右花括号位置
     */
    public static int findBraceEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && text.charAt(pos) != '}') {
            pos++;
        }
        return pos < text.length() ? pos : -1;
    }

    /**
     * 判断字符是否为标识符起始字符（字母或下划线）
     */
    public static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    /**
     * 判断字符是否为标识符起始字符（字母或下划线），用于上下文版参数检测
     */
    public static boolean isIdentifierStartChar(char c) {
        return Character.isLetter(c) || c == '_';
    }

    // ==================== 上下文版辅助方法（MgxsqlContext 参数） ====================

    /**
     * 检测上下文当前位置是否匹配关键字（不区分大小写）
     */
    public static boolean isKeywordAt(MgxsqlContext ctx, String keyword) {
        int pos = ctx.getPosition();
        int len = keyword.length();
        if (pos + len > ctx.getInputLength()) {
            return false;
        }
        String sub = ctx.substring(pos, pos + len);
        return sub.equalsIgnoreCase(keyword);
    }

    /**
     * 检测上下文当前位置前方是否为词边界
     */
    public static boolean isWordBoundaryBefore(MgxsqlContext ctx) {
        int pos = ctx.getPosition();
        if (pos == 0) {
            return true;
        }
        char prev = ctx.charAt(pos - 1);
        return !Character.isLetterOrDigit(prev) && prev != '_' && prev != '.';
    }

    /**
     * 检测上下文当前位置后指定长度处是否为词边界
     */
    public static boolean isWordBoundaryAfter(MgxsqlContext ctx, int keywordLength) {
        int afterPos = ctx.getPosition() + keywordLength;
        if (afterPos >= ctx.getInputLength()) {
            return true;
        }
        char next = ctx.charAt(afterPos);
        return !Character.isLetterOrDigit(next) && next != '_' && next != '.';
    }

    /**
     * 跳过上下文中的空白字符
     */
    public static void skipWhitespace(MgxsqlContext ctx) {
        while (ctx.hasMore() && Character.isWhitespace(ctx.currentChar())) {
            ctx.advance();
        }
    }

    /**
     * 读取冒号参数引用（:param），返回参数名，ctx 停在参数名之后
     */
    public static String readColonParamRef(MgxsqlContext ctx) {
        if (ctx.currentChar() != ':') {
            return null;
        }
        ctx.advance(); // 跳过 :
        if (!ctx.hasMore() || !isIdentifierStartChar(ctx.currentChar())) {
            return null;
        }
        StringBuilder paramPath = new StringBuilder();
        while (ctx.hasMore()) {
            char c = ctx.currentChar();
            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                paramPath.append(c);
                ctx.advance();
            } else {
                break;
            }
        }
        return paramPath.length() > 0 ? paramPath.toString() : null;
    }

    /**
     * 读取标识符，返回标识符文本
     */
    public static String readIdentifier(MgxsqlContext ctx) {
        StringBuilder sb = new StringBuilder();
        while (ctx.hasMore()) {
            char c = ctx.currentChar();
            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                sb.append(c);
                ctx.advance();
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 读取 MyBatis 参数引用（#{param}），返回参数名
     */
    public static String readParamRef(MgxsqlContext ctx) {
        if (ctx.currentChar() != '#' || ctx.peekChar(1) != '{') {
            return null;
        }
        ctx.advance();
        ctx.advance();
        StringBuilder paramPath = new StringBuilder();
        while (ctx.hasMore() && ctx.currentChar() != '}') {
            paramPath.append(ctx.currentChar());
            ctx.advance();
        }
        if (ctx.hasMore()) {
            ctx.advance();
        }
        return paramPath.toString();
    }

    /**
     * 读取 MyBatis 参数引用完整形式（#{param}），返回包含 #{ 和 } 的完整文本
     */
    public static String readParamRefFull(MgxsqlContext ctx) {
        if (ctx.currentChar() != '#' || ctx.peekChar(1) != '{') {
            return null;
        }
        StringBuilder result = new StringBuilder();
        result.append('#');
        ctx.advance();
        result.append('{');
        ctx.advance();
        while (ctx.hasMore() && ctx.currentChar() != '}') {
            result.append(ctx.currentChar());
            ctx.advance();
        }
        if (ctx.hasMore()) {
            result.append('}');
            ctx.advance();
        }
        return result.toString();
    }

    /**
     * 读取 $ 变量引用（$variable → #{variable}），返回 #{variable} 形式
     */
    public static String readDollarVarRef(MgxsqlContext ctx) {
        if (ctx.currentChar() != '$') {
            return null;
        }
        ctx.advance();
        if (!ctx.hasMore() || !isIdentifierStartChar(ctx.currentChar())) {
            return null;
        }
        StringBuilder varName = new StringBuilder();
        while (ctx.hasMore()) {
            char c = ctx.currentChar();
            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                varName.append(c);
                ctx.advance();
            } else {
                break;
            }
        }
        return varName.length() > 0 ? "#{" + varName.toString() + "}" : null;
    }

    /**
     * 判断上下文当前位置是否为参数引用起始（:param）
     */
    public static boolean isParamRefStart(MgxsqlContext ctx) {
        if (ctx.peekChar(1) == ':') {
            return false;
        }
        char next = ctx.peekChar(1);
        return isIdentifierStartChar(next);
    }

    /**
     * 判断上下文指定偏移处是否为标识符起始字符
     */
    public static boolean isIdentifierStartAt(MgxsqlContext ctx, int offset) {
        char c = ctx.peekChar(offset);
        return isIdentifierStartChar(c);
    }

    /**
     * 检测上下文当前位置是否匹配子句关键字（用于 WHERE 域提前关闭）
     */
    public static boolean isClauseKeywordAt(MgxsqlContext ctx) {
        for (String keyword : CLAUSE_KEYWORDS) {
            if (matchesClauseKeyword(ctx, keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测上下文当前位置是否匹配指定的子句关键字
     * 多 token 关键字（如 "order by"）中间可有空白/换行
     */
    public static boolean matchesClauseKeyword(MgxsqlContext ctx, String keyword) {
        int pos = ctx.getPosition();
        // 检查前方单词边界
        if (pos > 0) {
            char prev = ctx.charAt(pos - 1);
            if (Character.isLetterOrDigit(prev) || prev == '_' || prev == '.') {
                return false;
            }
        }

        String[] tokens = keyword.split(" ");
        int checkPos = pos;
        for (int t = 0; t < tokens.length; t++) {
            String token = tokens[t];
            // 跳过空白
            while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
                checkPos++;
            }
            if (checkPos + token.length() > ctx.getInputLength()) {
                return false;
            }
            String sub = ctx.substring(checkPos, checkPos + token.length());
            if (!sub.equalsIgnoreCase(token)) {
                return false;
            }
            checkPos += token.length();
        }
        // 检查后方单词边界
        if (checkPos < ctx.getInputLength()) {
            char next = ctx.charAt(checkPos);
            if (Character.isLetterOrDigit(next) || next == '_' || next == '.') {
                return false;
            }
        }
        return true;
    }

    /**
     * 查找 XML 标签结束位置
     */
    public static int findXmlTagEnd(MgxsqlContext ctx, int start) {
        int depth = 0;
        int pos = start;
        while (pos < ctx.getInputLength()) {
            char c = ctx.charAt(pos);
            if (c == '\'') {
                pos++;
                while (pos < ctx.getInputLength() && ctx.charAt(pos) != '\'') {
                    pos++;
                }
                pos++;
                continue;
            }
            if (c == '"') {
                pos++;
                while (pos < ctx.getInputLength() && ctx.charAt(pos) != '"') {
                    pos++;
                }
                pos++;
                continue;
            }
            if (c == '<') {
                if (pos + 1 < ctx.getInputLength() && ctx.charAt(pos + 1) == '/') {
                    depth--;
                } else if (!isClosingTagAt(ctx, pos)) {
                    depth++;
                }
            }
            if (c == '>' && depth <= 0) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    /**
     * 检测指定位置是否为自闭合 XML 标签
     */
    public static boolean isClosingTagAt(MgxsqlContext ctx, int pos) {
        int searchPos = pos;
        while (searchPos < ctx.getInputLength()) {
            char c = ctx.charAt(searchPos);
            if (c == '>') {
                return searchPos > 0 && ctx.charAt(searchPos - 1) == '/';
            }
            searchPos++;
        }
        return false;
    }
}
