package com.mybatisgx.dsl.mgxsql.parser;

import com.mybatisgx.dsl.mgxsql.MgxsqlSyntaxHelper;
import com.mybatisgx.dsl.mgxsql.model.MgxsqlContext;
import com.mybatisgx.exception.MybatisgxException;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 解析无状态读取辅助与静态校验：供 {@link MgxsqlScopeParser}（ctx 基）与
 * {@link MgxsqlBodyParser}（String 基）共用，原样搬迁自重构前的 {@code MgxsqlParser} 同名方法，
 * 逻辑零变更（仅改物理归属与可见性）。
 *
 * @author 薛承城
 * @description mgxsql 解析读取/校验静态辅助
 * @date 2026/7/27
 */
public final class MgxsqlReadHelpers {

    private MgxsqlReadHelpers() {
    }

    // ==================== 标识符校验 ====================

    static boolean isPlainIdentifier(String identifier) {
        if (identifier.isEmpty() || !MgxsqlSyntaxHelper.isIdentifierStartChar(identifier.charAt(0))) {
            return false;
        }
        for (int i = 1; i < identifier.length(); i++) {
            char c = identifier.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                return false;
            }
        }
        return true;
    }

    // ==================== bind value 校验与去冒号 ====================

    /**
     * bind value 校验与去冒号：仅允许 {@code :param} + 运算符 + 数字/字符串字面量；
     * 禁止 {@code $var} / 裸标识符 / {@code #{}} / {@code ${}}。返回去冒号后的 OGNL。
     */
    static String stripAndValidateBindValue(String raw, String posInfo) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        int len = raw.length();
        while (i < len) {
            char c = raw.charAt(i);
            if (Character.isWhitespace(c)) {
                out.append(c);
                i++;
                continue;
            }
            if (c == ':' && i + 1 < len && MgxsqlSyntaxHelper.isIdentifierStart(raw.charAt(i + 1))) {
                i++; // skip ':'
                out.append(raw.charAt(i));
                i++;
                while (i < len && (Character.isLetterOrDigit(raw.charAt(i)) || raw.charAt(i) == '_' || raw.charAt(i) == '.')) {
                    out.append(raw.charAt(i));
                    i++;
                }
                continue;
            }
            if (c == '\'') {
                out.append(c);
                i++;
                while (i < len) {
                    char sc = raw.charAt(i);
                    out.append(sc);
                    i++;
                    if (sc == '\'') {
                        if (i < len && raw.charAt(i) == '\'') {
                            out.append('\'');
                            i++;
                        } else {
                            break;
                        }
                    }
                }
                continue;
            }
            if (Character.isDigit(c)) {
                out.append(c);
                i++;
                while (i < len && (Character.isLetterOrDigit(raw.charAt(i)) || raw.charAt(i) == '.')) {
                    out.append(raw.charAt(i));
                    i++;
                }
                continue;
            }
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '(' || c == ')' || c == ',') {
                out.append(c);
                i++;
                continue;
            }
            throw new MybatisgxException("mgxsql 语法错误: #bind value 只接受 :param + 运算符 + 字面量（不允许 $var / 裸标识符 / #{} / ${}），%s", posInfo);
        }
        return out.toString();
    }

    // ==================== XML 标签静态判定 ====================

    static boolean isContainerTag(String tagName) {
        return "where".equals(tagName) || "set".equals(tagName) || "trim".equals(tagName);
    }

    static boolean containsMgxsqlMarker(String text) {
        boolean inString = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (c == '#' && i + 1 < text.length()) {
                char n = text.charAt(i + 1);
                if (n == '[' || n == '(') {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isConditionXmlTagStart(String text, int pos) {
        if (pos >= text.length() || text.charAt(pos) != '<') {
            return false;
        }
        int nextPos = pos + 1;
        if (nextPos >= text.length()) {
            return false;
        }
        char next = text.charAt(nextPos);
        if (next == '/' || next == '!' || next == '?') {
            return true;
        }
        if (Character.isLetter(next) || next == '_') {
            int nameEnd = nextPos + 1;
            while (nameEnd < text.length()) {
                char nc = text.charAt(nameEnd);
                if (Character.isLetterOrDigit(nc) || nc == '_' || nc == '-' || nc == '.' || nc == ':') {
                    nameEnd++;
                } else {
                    break;
                }
            }
            if (nameEnd < text.length()) {
                char afterName = text.charAt(nameEnd);
                return afterName == ' ' || afterName == '>' || afterName == '/' || afterName == '\t' || afterName == '\n' || afterName == '\r';
            }
            return true;
        }
        return false;
    }

    // ==================== String 基范围读取（移植自 processor） ====================

    static int[] readTextBracketRange(String text, int start) {
        int pos = start + 1;
        int depth = 1;
        while (pos < text.length() && depth > 0) {
            char bc = text.charAt(pos);
            if (bc == '[') {
                depth++;
                pos++;
            } else if (bc == ']') {
                depth--;
                pos++;
                if (depth == 0) {
                    break;
                }
            } else if (bc == '\'') {
                pos++;
                while (pos < text.length()) {
                    char sc = text.charAt(pos);
                    pos++;
                    if (sc == '\'') {
                        if (pos < text.length() && text.charAt(pos) == '\'') {
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                pos++;
            }
        }
        return new int[]{start, pos};
    }

    static int[] readTextParenRange(String text, int start) {
        int pos = start + 1;
        int depth = 1;
        while (pos < text.length() && depth > 0) {
            char pc = text.charAt(pos);
            if (pc == '(') {
                depth++;
                pos++;
            } else if (pc == ')') {
                depth--;
                pos++;
                if (depth == 0) {
                    break;
                }
            } else if (pc == '\'') {
                pos++;
                while (pos < text.length()) {
                    char sc = text.charAt(pos);
                    pos++;
                    if (sc == '\'') {
                        if (pos < text.length() && text.charAt(pos) == '\'') {
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                pos++;
            }
        }
        return new int[]{start, pos};
    }

    static int[] readDollarVarNameTextRange(String text, int pos) {
        int len = text.length();
        if (pos >= len || text.charAt(pos) != '$') {
            return null;
        }
        int nameStart = pos + 1;
        if (nameStart >= len || !MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(nameStart))) {
            return null;
        }
        int nameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, nameStart);
        return new int[]{nameStart, nameEnd};
    }

    // ==================== ctx 基范围读取（移植自扫描器） ====================

    static String readBracketedContent(MgxsqlContext ctx, boolean requireClose) {
        int depth = 1;
        StringBuilder content = new StringBuilder();
        while (ctx.hasMore() && depth > 0) {
            char c = ctx.currentChar();
            if (c == '[') {
                depth++;
                content.append(c);
                ctx.advance();
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    ctx.advance();
                    break;
                }
                content.append(c);
                ctx.advance();
            } else if (c == '\'') {
                content.append(c);
                ctx.advance();
                while (ctx.hasMore()) {
                    char sc = ctx.currentChar();
                    content.append(sc);
                    ctx.advance();
                    if (sc == '\'') {
                        if (ctx.hasMore() && ctx.currentChar() == '\'') {
                            content.append('\'');
                            ctx.advance();
                        } else {
                            break;
                        }
                    }
                }
            } else {
                content.append(c);
                ctx.advance();
            }
        }
        if (requireClose && depth > 0) {
            throw new MybatisgxException("mgxsql 语法错误: '[' 未闭合，缺少匹配的 ']'，%s", ctx.getPositionInfo());
        }
        return content.toString();
    }

    static String readParenthesizedContent(MgxsqlContext ctx) {
        if (!ctx.hasMore() || ctx.currentChar() != '(') {
            return "";
        }
        ctx.advance();
        int depth = 1;
        StringBuilder content = new StringBuilder();
        while (ctx.hasMore() && depth > 0) {
            char c = ctx.currentChar();
            if (c == '(') {
                depth++;
                content.append(c);
                ctx.advance();
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    ctx.advance();
                    break;
                }
                content.append(c);
                ctx.advance();
            } else if (c == '\'') {
                content.append(c);
                ctx.advance();
                while (ctx.hasMore()) {
                    char sc = ctx.currentChar();
                    content.append(sc);
                    ctx.advance();
                    if (sc == '\'') {
                        if (ctx.hasMore() && ctx.currentChar() == '\'') {
                            content.append('\'');
                            ctx.advance();
                        } else {
                            break;
                        }
                    }
                }
            } else {
                content.append(c);
                ctx.advance();
            }
        }
        return content.toString();
    }

    static String readForm1Content(MgxsqlContext ctx) {
        StringBuilder content = new StringBuilder();
        int parenDepth = 0;
        while (ctx.hasMore()) {
            char c = ctx.currentChar();
            if (c == '\n' || c == '\r') {
                ctx.advance();
                break;
            }
            if (c == '(') {
                parenDepth++;
            } else if (c == ')') {
                parenDepth--;
            }
            if (parenDepth == 0 && MgxsqlSyntaxHelper.isKeywordAt(ctx, "and")
                    && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx)
                    && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 3)) {
                throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，%s", ctx.getPositionInfo());
            }
            if (parenDepth == 0 && MgxsqlSyntaxHelper.isKeywordAt(ctx, "or")
                    && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx)
                    && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 2)) {
                throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，%s", ctx.getPositionInfo());
            }
            content.append(c);
            ctx.advance();
        }
        return content.toString();
    }

    static String readDollarVarNameCtx(MgxsqlContext ctx) {
        if (!ctx.hasMore() || ctx.currentChar() != '$') {
            return null;
        }
        ctx.advance();
        if (!ctx.hasMore() || !MgxsqlSyntaxHelper.isIdentifierStartChar(ctx.currentChar())) {
            return null;
        }
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
        return sb.length() > 0 ? sb.toString() : null;
    }

    // ==================== => 右侧读取结果 ====================

    static class ForeachRhs {
        final String valueExpr;
        final boolean composite;

        ForeachRhs(String valueExpr, boolean composite) {
            this.valueExpr = valueExpr;
            this.composite = composite;
        }
    }

    static ForeachRhs readArrowRhsCtx(MgxsqlContext ctx, String posInfo) {
        if (ctx.hasMore() && ctx.currentChar() == '[') {
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            List<String> parts = new ArrayList<String>();
            String first = readDollarVarNameCtx(ctx);
            if (first == null) {
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 内必须是 $variable，%s", posInfo);
            }
            parts.add("#{" + first + "}");
            while (ctx.hasMore()) {
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                if (ctx.currentChar() != ',') {
                    break;
                }
                ctx.advance();
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                String varName = readDollarVarNameCtx(ctx);
                if (varName == null) {
                    throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 内必须是 $variable，%s", posInfo);
                }
                parts.add("#{" + varName + "}");
            }
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            if (!ctx.hasMore() || ctx.currentChar() != ']') {
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 未以 ']' 闭合，%s", posInfo);
            }
            ctx.advance();
            return new ForeachRhs(String.join(",", parts), parts.size() > 1);
        }
        if (ctx.hasMore() && ctx.currentChar() == '$' && ctx.peekChar(1) == '{') {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}，%s", posInfo);
        }
        String varName = readDollarVarNameCtx(ctx);
        if (varName == null) {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}，%s", posInfo);
        }
        return new ForeachRhs("#{" + varName + "}", false);
    }

    static class ForeachRhsText {
        final String valueExpr;
        final boolean composite;
        final int end;

        ForeachRhsText(String valueExpr, boolean composite, int end) {
            this.valueExpr = valueExpr;
            this.composite = composite;
            this.end = end;
        }
    }

    static ForeachRhsText readArrowRhsText(String text, int pos, int start) {
        int len = text.length();
        if (pos < len && text.charAt(pos) == '[') {
            pos++;
            while (pos < len && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            List<String> parts = new ArrayList<String>();
            int[] first = readDollarVarNameTextRange(text, pos);
            if (first == null) {
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 内必须是 $variable，位置: %s", String.valueOf(start));
            }
            parts.add("#{" + text.substring(first[0], first[1]) + "}");
            pos = first[1];
            while (pos < len) {
                while (pos < len && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
                if (pos >= len || text.charAt(pos) != ',') {
                    break;
                }
                pos++;
                while (pos < len && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
                int[] v = readDollarVarNameTextRange(text, pos);
                if (v == null) {
                    throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 内必须是 $variable，位置: %s", String.valueOf(start));
                }
                parts.add("#{" + text.substring(v[0], v[1]) + "}");
                pos = v[1];
            }
            while (pos < len && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos >= len || text.charAt(pos) != ']') {
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 未以 ']' 闭合，位置: %s", String.valueOf(start));
            }
            pos++;
            return new ForeachRhsText(String.join(",", parts), parts.size() > 1, pos);
        }
        if (pos + 1 < len && text.charAt(pos) == '$' && text.charAt(pos + 1) == '{') {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}，位置: %s", String.valueOf(start));
        }
        int[] v = readDollarVarNameTextRange(text, pos);
        if (v == null) {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}，位置: %s", String.valueOf(start));
        }
        return new ForeachRhsText("#{" + text.substring(v[0], v[1]) + "}", false, v[1]);
    }
}
