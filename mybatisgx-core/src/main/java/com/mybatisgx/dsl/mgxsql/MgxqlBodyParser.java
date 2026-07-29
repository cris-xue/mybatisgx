package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.*;
import com.mybatisgx.exception.MybatisgxException;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql body 层解析器（String 基子串解析）：承接重构前 {@link MgxsqlParser} 的 body 层方法族，
 * 处理条件体内部的 {@code #if}/{@code #for}/{@code #bind}/{@code #include}/{@code #choose}/{@code in}/{@code like} 等。
 * <p>双轨制约束：本类全部以 {@code String} + 整型下标推进，不依赖 {@link MgxqlContext} 游标（与重构前一致）。
 * {@code bodyInConsumed}/{@code bodyInComplexConsumed} 两 int 字段为重构前逃生舱，原样保留（见
 * mgxsql-parser-internal-structure spec「字段逃生舱原样保留」）。
 *
 * @author 薛承城
 * @description mgxsql body 层解析器（String 基）
 * @date 2026/7/27
 */
public class MgxqlBodyParser {

    private int bodyInConsumed;
    private int bodyInComplexConsumed;
    private final BindRegistry bindRegistry;

    MgxqlBodyParser(BindRegistry bindRegistry) {
        this.bindRegistry = bindRegistry;
    }

    ChooseUnit parseChooseBody(String inner, int startPos, int line, int col) {
        ChooseUnit choose = new ChooseUnit(startPos, line, col);
        int i = 0;
        int len = inner.length();
        while (i < len) {
            while (i < len && Character.isWhitespace(inner.charAt(i))) {
                i++;
            }
            if (i >= len) {
                break;
            }
            if (inner.charAt(i) != '#') {
                throw new MybatisgxException("mgxsql 语法错误: #choose 内只允许 #when/#otherwise，位置: %s", String.valueOf(i));
            }
            i++;
            if (MgxsqlSyntaxHelper.isKeywordAt(inner, i, "when") && MgxsqlSyntaxHelper.isWordBoundaryAfter(inner, i + 4)) {
                i += 4;
                while (i < len && Character.isWhitespace(inner.charAt(i))) {
                    i++;
                }
                if (i >= len || inner.charAt(i) != '(') {
                    throw new MybatisgxException("mgxsql 语法错误: #when 必须带 guard，写法 #when(expr)[body]，位置: %s", String.valueOf(i));
                }
                int[] guardRange = MgxsqlReadHelpers.readTextParenRange(inner, i);
                String guard = inner.substring(i + 1, guardRange[1] - 1).trim();
                if (guard.isEmpty()) {
                    throw new MybatisgxException("mgxsql 语法错误: #when(expr) 的圆括号内表达式不能为空，位置: %s", String.valueOf(i));
                }
                i = guardRange[1];
                while (i < len && Character.isWhitespace(inner.charAt(i))) {
                    i++;
                }
                if (i >= len || inner.charAt(i) != '[') {
                    throw new MybatisgxException("mgxsql 语法错误: #when(expr) 后必须跟 [body]，位置: %s", String.valueOf(i));
                }
                int[] bodyRange = MgxsqlReadHelpers.readTextBracketRange(inner, i);
                String body = inner.substring(i + 1, bodyRange[1] - 1);
                i = bodyRange[1];
                WhenUnit when = new WhenUnit(guard, i, line, col);
                when.getBody().addAll(parseBody(body));
                choose.getWhens().add(when);
            } else if (MgxsqlSyntaxHelper.isKeywordAt(inner, i, "otherwise") && MgxsqlSyntaxHelper.isWordBoundaryAfter(inner, i + 9)) {
                i += 9;
                while (i < len && Character.isWhitespace(inner.charAt(i))) {
                    i++;
                }
                if (i >= len || inner.charAt(i) != '[') {
                    throw new MybatisgxException("mgxsql 语法错误: #otherwise 后必须跟 [body]，位置: %s", String.valueOf(i));
                }
                int[] bodyRange = MgxsqlReadHelpers.readTextBracketRange(inner, i);
                String body = inner.substring(i + 1, bodyRange[1] - 1);
                i = bodyRange[1];
                OtherwiseUnit otherwise = new OtherwiseUnit(i, line, col);
                otherwise.getBody().addAll(parseBody(body));
                choose.setOtherwise(otherwise);
            } else {
                throw new MybatisgxException("mgxsql 语法错误: #choose 内只允许 #when/#otherwise，位置: %s", String.valueOf(i));
            }
        }
        return choose;
    }
    // ==================== body 层解析（String 基，移植自 processor） ====================

    List<MgxsqlNode> parseBody(String text) {
        List<MgxsqlNode> target = new ArrayList();
        if (text == null || text.isEmpty()) {
            return target;
        }
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == '#') {
                flushBodyText(target, buf);
                int consumed = parseBodyHash(text, i, target, buf);
                if (consumed > i) {
                    i = consumed;
                    continue;
                }
            }

            if (MgxsqlSyntaxHelper.isKeywordAt(text, i, "in")
                    && MgxsqlSyntaxHelper.isWordBoundaryBefore(text, i)
                    && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, i + 2)) {
                flushBodyText(target, buf);
                int inEnd = parseBodyIn(text, i, target);
                if (inEnd > i) {
                    i = inEnd;
                    continue;
                }
            }

            if (c == '%' && i + 1 < text.length() && text.charAt(i + 1) == ':') {
                flushBodyText(target, buf);
                int likeEnd = parseBodyLike(text, i, target);
                if (likeEnd > i) {
                    i = likeEnd;
                    continue;
                }
            }

            if (c == ':' && i + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(i + 1))) {
                int paramNameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, i + 1);
                if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '[') {
                    int closeBracket = text.indexOf(']', paramNameEnd);
                    if (closeBracket > paramNameEnd) {
                        paramNameEnd = closeBracket + 1;
                    }
                }
                String paramName = text.substring(i + 1, paramNameEnd);
                if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
                    flushBodyText(target, buf);
                    String bindName = "_like_" + paramName.replace('.', '_');
                    String bindValue = paramName + " + '%'";
                    target.add(new BindUnit(paramName, bindName, bindValue, true, i, 0, 0));
                    i = paramNameEnd + 1;
                    continue;
                }
                flushBodyText(target, buf);
                target.add(new ParamExpr(paramName, i, 0, 0));
                i = paramNameEnd;
                continue;
            }

            if (c == '$' && i + 1 < text.length() && text.charAt(i + 1) != '{'
                    && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(i + 1))) {
                flushBodyText(target, buf);
                int varEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, i + 1);
                String varName = text.substring(i + 1, varEnd);
                bindRegistry.checkBindReference(varName, "位置: " + i);
                target.add(new LocalVarExpr(varName, i, 0, 0));
                i = varEnd;
                continue;
            }
            if (c == '$' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 ${param}，位置: %s", String.valueOf(i));
            }
            if (c == '<' && MgxsqlReadHelpers.isConditionXmlTagStart(text, i)) {
                throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 XML 标签，位置: %s", String.valueOf(i));
            }

            buf.append(c);
            i++;
        }
        flushBodyText(target, buf);
        return target;
    }

    private void flushBodyText(List<MgxsqlNode> target, StringBuilder buf) {
        if (buf.length() > 0) {
            target.add(new SqlText(buf.toString(), 0, 0, 0));
            buf.setLength(0);
        }
    }

    private int parseBodyHash(String text, int start, List<MgxsqlNode> target, StringBuilder buf) {
        if (start + 1 >= text.length()) {
            return start;
        }
        char next = text.charAt(start + 1);

        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "choose") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 7)) {
            int pos = start + 7;
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos >= text.length() || text.charAt(pos) != '[') {
                throw new MybatisgxException("mgxsql 语法错误: #choose 后必须跟 '['，位置: %s", String.valueOf(start));
            }
            int[] range = MgxsqlReadHelpers.readTextBracketRange(text, pos);
            String inner = text.substring(pos + 1, range[1] - 1);
            target.add(parseChooseBody(inner, start, 0, 0));
            return range[1];
        }

        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "if") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 3)) {
            return parseBodyIf(text, start, target);
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "for") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 4)) {
            return parseBodyFor(text, start, target);
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "include") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 8)) {
            return parseBodyInclude(text, start, target);
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "bind") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 5)) {
            return parseBodyBind(text, start, target);
        }

        if (next == '[') {
            int[] range = MgxsqlReadHelpers.readTextBracketRange(text, start + 1);
            String body = text.substring(start + 2, range[1] - 1);
            IfUnit unit = new IfUnit(null, start, 0, 0);
            unit.getBody().addAll(parseBody(body));
            target.add(unit);
            return range[1];
        }

        if (next == '(') {
            throw new MybatisgxException("mgxsql 语法错误: '#(expr)' 已废弃，请改用 '#if(expr)'，位置: %s", String.valueOf(start));
        }

        if (next == '{') {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #{param}，请使用 :param 代替，位置: %s", String.valueOf(start));
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "and") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 4)) {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #and/#or 简写，请使用 #[and ...] 嵌套条件体，位置: %s", String.valueOf(start));
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "or") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 3)) {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #and/#or 简写，请使用 #[and ...] 嵌套条件体，位置: %s", String.valueOf(start));
        }

        if (MgxsqlSyntaxHelper.isIdentifierStart(next)) {
            int lineEnd = start + 1;
            int form1ParenDepth = 0;
            int form1BracketDepth = 0;
            while (lineEnd < text.length() && text.charAt(lineEnd) != '\n' && text.charAt(lineEnd) != '\r') {
                char fc = text.charAt(lineEnd);
                if (fc == '(') {
                    form1ParenDepth++;
                } else if (fc == ')') {
                    form1ParenDepth--;
                } else if (fc == '[') {
                    form1BracketDepth++;
                } else if (fc == ']') {
                    form1BracketDepth--;
                }
                if (form1ParenDepth == 0 && form1BracketDepth == 0) {
                    if (lineEnd + 3 <= text.length()
                            && text.substring(lineEnd, lineEnd + 3).equalsIgnoreCase("and")
                            && MgxsqlSyntaxHelper.isWordBoundaryBefore(text, lineEnd)
                            && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, lineEnd + 3)) {
                        throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，位置: %s", String.valueOf(lineEnd));
                    }
                    if (lineEnd + 2 <= text.length()
                            && text.substring(lineEnd, lineEnd + 2).equalsIgnoreCase("or")
                            && MgxsqlSyntaxHelper.isWordBoundaryBefore(text, lineEnd)
                            && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, lineEnd + 2)) {
                        throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，位置: %s", String.valueOf(lineEnd));
                    }
                }
                lineEnd++;
            }
            String condition = text.substring(start + 1, lineEnd).trim();
            IfUnit unit = new IfUnit(null, start, 0, 0);
            unit.getBody().addAll(parseBody(condition));
            target.add(unit);
            return lineEnd;
        }

        return start;
    }

    // ==================== body 层 #if/#for/#include/#bind（String 基） ====================

    private int parseBodyIf(String text, int start, List<MgxsqlNode> target) {
        int pos = start + 3; // after "#if"
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '(') {
            throw new MybatisgxException("mgxsql 语法错误: #if 后必须跟 '(expr)'，位置: %s", String.valueOf(start));
        }
        int[] guardRange = MgxsqlReadHelpers.readTextParenRange(text, pos);
        String guard = text.substring(pos + 1, guardRange[1] - 1).trim();
        if (guard.isEmpty()) {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 的圆括号内表达式不能为空，位置: %s", String.valueOf(start));
        }
        pos = guardRange[1];
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 后必须跟 '[body]'，位置: %s", String.valueOf(start));
        }
        int[] bodyRange = MgxsqlReadHelpers.readTextBracketRange(text, pos);
        String body = text.substring(pos + 1, bodyRange[1] - 1);
        IfUnit unit = new IfUnit(guard, start, 0, 0);
        unit.getBody().addAll(parseBody(body));
        target.add(unit);
        return bodyRange[1];
    }

    private int parseBodyFor(String text, int start, List<MgxsqlNode> target) {
        int pos = start + 4; // after "#for"
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '(') {
            throw new MybatisgxException("mgxsql 语法错误: #for 后必须跟 '(item:collection)'，位置: %s", String.valueOf(start));
        }
        int[] parenRange = MgxsqlReadHelpers.readTextParenRange(text, pos);
        String header = text.substring(pos + 1, parenRange[1] - 1).trim();
        int colon = header.indexOf(':');
        if (colon < 0) {
            throw new MybatisgxException("mgxsql 语法错误: #for 的 (item:collection) 缺少 ':'，位置: %s", String.valueOf(start));
        }
        String itemName = header.substring(0, colon).trim();
        String collectionName = header.substring(colon + 1).trim();
        pos = parenRange[1];
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos + 1 >= text.length() || text.charAt(pos) != '=' || text.charAt(pos + 1) != '>') {
            throw new MybatisgxException("mgxsql 语法错误: #for(item:collection) 后必须跟 '=>'，位置: %s", String.valueOf(start));
        }
        pos += 2;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        MgxsqlReadHelpers.ForeachRhsText rhs = MgxsqlReadHelpers.readArrowRhsText(text, pos, start);
        target.add(new ForeachUnit(itemName, collectionName, rhs.valueExpr, false, rhs.composite, start, 0, 0));
        return rhs.end;
    }

    private int parseBodyInclude(String text, int start, List<MgxsqlNode> target) {
        int pos = start + 8; // after "#include"
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #include 后必须跟 '[sqlId]'，位置: %s", String.valueOf(start));
        }
        int[] range = MgxsqlReadHelpers.readTextBracketRange(text, pos);
        String raw = text.substring(pos + 1, range[1] - 1);
        for (int k = 0; k < raw.length(); k++) {
            char rc = raw.charAt(k);
            if (rc == ':' || rc == '#' || rc == '$') {
                throw new MybatisgxException("mgxsql 语法错误: #include 的 refid 只接受静态标识符，位置: %s", String.valueOf(start));
            }
        }
        String id = raw.trim();
        if (id.isEmpty()) {
            throw new MybatisgxException("mgxsql 语法错误: #include[sqlId] 的 refid 不能为空，位置: %s", String.valueOf(start));
        }
        target.add(new IncludeUnit(id, start, 0, 0));
        return range[1];
    }

    private int parseBodyBind(String text, int start, List<MgxsqlNode> target) {
        int pos = start + 5; // after "#bind"
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #bind 后必须跟 '[name = expr]'，位置: %s", String.valueOf(start));
        }
        int[] range = MgxsqlReadHelpers.readTextBracketRange(text, pos);
        String content = text.substring(pos + 1, range[1] - 1);
        int eqIdx = content.indexOf('=');
        if (eqIdx < 0) {
            throw new MybatisgxException("mgxsql 语法错误: #bind[name = expr] 缺少 '='，位置: %s", String.valueOf(start));
        }
        String name = content.substring(0, eqIdx).trim();
        String valueRaw = content.substring(eqIdx + 1).trim();
        if (name.isEmpty() || !MgxsqlReadHelpers.isPlainIdentifier(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name 必须是标识符，位置: %s", String.valueOf(start));
        }
        bindRegistry.putAndCheckDup(name, start, "位置: " + start);
        String valueOgnl = MgxsqlReadHelpers.stripAndValidateBindValue(valueRaw, "位置: " + start);
        target.add(new BindUnit(null, name, valueOgnl, false, start, 0, 0));
        return range[1];
    }

    private int parseBodyIn(String text, int start, List<MgxsqlNode> target) {
        int savedPos = start + 2;
        int pos = savedPos;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return start;
        }
        if (text.charAt(pos) == ':' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            int nameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
            String collectionName = text.substring(pos + 1, nameEnd);
            target.add(new ForeachUnit("item", collectionName, "#{item}", start, 0, 0));
            return nameEnd;
        }
        if (text.charAt(pos) == '(') {
            ForeachUnit foreach = parseBodyInParenthesized(text, pos);
            if (foreach != null) {
                target.add(foreach);
                return bodyInConsumed;
            }
        }
        return savedPos;
    }

    private ForeachUnit parseBodyInParenthesized(String text, int openParen) {
        int outerParenPos = openParen;
        int pos = openParen + 1;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return null;
        }
        if (text.charAt(pos) == '(') {
            int innerStart = pos + 1;
            while (innerStart < text.length() && Character.isWhitespace(text.charAt(innerStart))) {
                innerStart++;
            }
            ForeachUnit wrapped = parseBodyInComplex(text, innerStart);
            if (wrapped != null) {
                int after = bodyInComplexConsumed;
                while (after < text.length() && Character.isWhitespace(text.charAt(after))) {
                    after++;
                }
                if (after < text.length() && text.charAt(after) == ')') {
                    after++;
                }
                bodyInConsumed = after;
                return wrapped;
            }
            pos = outerParenPos + 1;
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }
        if (text.charAt(pos) == ':' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            int nameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
            String collectionName = text.substring(pos + 1, nameEnd);
            int afterName = nameEnd;
            while (afterName < text.length() && Character.isWhitespace(text.charAt(afterName))) {
                afterName++;
            }
            if (afterName < text.length() && text.charAt(afterName) == ')') {
                afterName++;
                bodyInConsumed = afterName;
                return new ForeachUnit("item", collectionName, "#{item}", 0, 0, 0);
            }
        }
        if (text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
            return null;
        }
        ForeachUnit complex = parseBodyInComplex(text, pos);
        if (complex != null) {
            bodyInConsumed = bodyInComplexConsumed;
            return complex;
        }
        return null;
    }

    private ForeachUnit parseBodyInComplex(String text, int pos) {
        int itemNameStart = pos;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        String itemName = text.substring(itemNameStart, pos);
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos < text.length() && text.charAt(pos) == ':') {
            pos++;
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            int collStart = pos;
            while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
                pos++;
            }
            String collectionName = text.substring(collStart, pos);
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos < text.length() && text.charAt(pos) == ')') {
                pos++;
                while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
                if (pos < text.length() && text.charAt(pos) == '=' && pos + 1 < text.length() && text.charAt(pos + 1) == '>') {
                    pos += 2;
                    while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                        pos++;
                    }
                    if (pos < text.length() && text.charAt(pos) == '[') {
                        MgxsqlReadHelpers.ForeachRhsText rhs = MgxsqlReadHelpers.readArrowRhsText(text, pos, pos);
                        bodyInComplexConsumed = rhs.end;
                        return new ForeachUnit(itemName, collectionName, rhs.valueExpr, true, rhs.composite, 0, 0, 0);
                    }
                    if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
                        int varEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
                        String varName = text.substring(pos + 1, varEnd);
                        bodyInComplexConsumed = varEnd;
                        return new ForeachUnit(itemName, collectionName, "#{" + varName + "}", 0, 0, 0);
                    } else if ((pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{')
                            || (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && text.charAt(pos + 1) == '{')) {
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, 位置: %s", String.valueOf(pos));
                    }
                }
            }
        }
        return null;
    }

    private int parseBodyLike(String text, int start, List<MgxsqlNode> target) {
        int pos = start + 1;
        if (pos >= text.length() || text.charAt(pos) != ':' || pos + 1 >= text.length() || !MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            return start;
        }
        int paramNameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
        String paramName = text.substring(pos + 1, paramNameEnd);
        boolean both = false;
        if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
            both = true;
            paramNameEnd++;
        }
        String bindName = "_like_" + paramName.replace('.', '_');
        String bindValue = both ? "'%' + " + paramName + " + '%'" : "'%' + " + paramName;
        target.add(new BindUnit(paramName, bindName, bindValue, true, start, 0, 0));
        return paramNameEnd;
    }
}
