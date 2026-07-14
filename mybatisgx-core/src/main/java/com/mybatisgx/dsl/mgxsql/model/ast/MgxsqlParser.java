package com.mybatisgx.dsl.mgxsql.model.ast;

import com.mybatisgx.dsl.mgxsql.MgxsqlSyntaxHelper;
import com.mybatisgx.dsl.mgxsql.model.MgxsqlContext;
import com.mybatisgx.exception.MybatisgxException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * mgxsql 文本解析器：将 mgxsql 文本解析为 {@link MgxsqlNode} AST（Scope/Unit/Expression 三层）。
 * <p>复用 {@link MgxsqlContext}（逐字符读取 + 位置/行号）与 {@link MgxsqlSyntaxHelper}（关键字/词边界检测）
 * 的识别能力，产物是 AST 节点（不拼 XML）。解析与渲染严格分离（design Decision 1）。
 *
 * <p>识别逻辑自重构前的 {@code MgxsqlScanner}（scope 层，ctx 基）与 {@code MgxsqlConditionBodyProcessor}
 * （body 层，String 基）原样搬迁，仅把"拼字符串"换成"构造节点"。
 *
 * <p><b>未实现</b>：原生三标签下沉 {@code <where>/<set>/<trim>}（DescentScope）遇之抛
 * {@link UnsupportedOperationException}，留待后续。
 *
 * @author 薛承城
 * @description mgxsql 文本 → AST 解析器
 * @date 2026/7/13
 */
public class MgxsqlParser {

    private MgxsqlContext ctx;
    private int bodyInConsumed;
    private int bodyInComplexConsumed;
    private final Deque<String> descentCloseTags = new ArrayDeque<String>();

    public List<MgxsqlNode> parse(String input) {
        List<MgxsqlNode> root = new ArrayList<MgxsqlNode>();
        if (input == null) {
            return root;
        }
        if (input.trim().isEmpty()) {
            root.add(new PassthroughText(input, 0, 1, 1));
            return root;
        }
        this.ctx = new MgxsqlContext(input.trim());
        parseContent(root, CloseMode.END_ONLY);
        return root;
    }

    private enum CloseMode {
        END_ONLY,
        BRACKET,
        CLOSE_ON_CLAUSE,
        CLOSE_ON_WHERE,
        DESCENT
    }

    // ==================== scope 层内容解析 ====================

    private void parseContent(List<MgxsqlNode> target, CloseMode mode) {
        StringBuilder text = new StringBuilder();
        while (ctx.hasMore()) {
            char c = ctx.currentChar();

            if (mode == CloseMode.BRACKET && c == ']') {
                flushText(target, text);
                ctx.advance();
                return;
            }
            if (mode == CloseMode.CLOSE_ON_CLAUSE && MgxsqlSyntaxHelper.isClauseKeywordAt(ctx)) {
                flushText(target, text);
                return;
            }
            if (mode == CloseMode.CLOSE_ON_WHERE && MgxsqlSyntaxHelper.isKeywordAt(ctx, "where")) {
                flushText(target, text);
                return;
            }
            if (mode == CloseMode.DESCENT && !descentCloseTags.isEmpty()
                    && ctx.startsWithAt(descentCloseTags.peek(), ctx.getPosition())) {
                flushText(target, text);
                String closeTag = descentCloseTags.pop();
                ctx.setPosition(ctx.getPosition() + closeTag.length());
                return;
            }

            if (c == '\'') {
                text.append(c);
                ctx.advance();
                while (ctx.hasMore()) {
                    char sc = ctx.currentChar();
                    text.append(sc);
                    ctx.advance();
                    if (sc == '\'') {
                        if (ctx.hasMore() && ctx.currentChar() == '\'') {
                            text.append('\'');
                            ctx.advance();
                        } else {
                            break;
                        }
                    }
                }
                continue;
            }

            if (c == '<') {
                if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                    flushText(target, text);
                    parseXmlStart(target);
                    continue;
                }
                text.append(c);
                ctx.advance();
                continue;
            }

            if (c == '#') {
                if (mode == CloseMode.END_ONLY) {
                    text.append('#');
                    ctx.advance();
                    continue;
                }
                if (ctx.peekChar(1) == '{') {
                    text.append('#');
                    ctx.advance();
                    continue;
                }
                flushText(target, text);
                parseHash(target);
                continue;
            }

            if (mode != CloseMode.END_ONLY
                    && MgxsqlSyntaxHelper.isKeywordAt(ctx, "in")
                    && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx)
                    && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 2)) {
                flushText(target, text);
                parseInClause(target);
                continue;
            }

            if (mode != CloseMode.END_ONLY && c == '%' && ctx.peekChar(1) == ':') {
                flushText(target, text);
                parseLikePatternScope(target);
                continue;
            }

            if (mode != CloseMode.END_ONLY && c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
                flushText(target, text);
                String paramName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
                if (paramName != null) {
                    target.add(new ParamExpr(paramName, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
                }
                continue;
            }

            if (mode == CloseMode.END_ONLY && MgxsqlSyntaxHelper.isKeywordAt(ctx, "where")) {
                flushText(target, text);
                parseWhereScope(target);
                continue;
            }
            if (mode == CloseMode.END_ONLY && MgxsqlSyntaxHelper.isKeywordAt(ctx, "set")) {
                flushText(target, text);
                parseSetScope(target);
                continue;
            }

            text.append(c);
            ctx.advance();
        }
        flushText(target, text);
        if (mode == CloseMode.BRACKET) {
            throw new MybatisgxException("mgxsql 语法错误: '[' 未闭合，缺少匹配的 ']'，%s", ctx.getPositionInfo());
        }
        if (mode == CloseMode.DESCENT && !descentCloseTags.isEmpty()) {
            String unclosed = descentCloseTags.pop();
            throw new MybatisgxException("mgxsql 语法错误: 容器标签未闭合，缺少 %s，%s", unclosed, ctx.getPositionInfo());
        }
    }

    private void flushText(List<MgxsqlNode> target, StringBuilder text) {
        if (text.length() > 0) {
            target.add(new PassthroughText(text.toString(), ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
            text.setLength(0);
        }
    }

    private void parseWhereScope(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        int afterWhere = startPos + 5;
        int checkPos = afterWhere;
        while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
            checkPos++;
        }
        boolean bounded = checkPos < ctx.getInputLength() && ctx.charAt(checkPos) == '[';
        WhereScope scope = new WhereScope(bounded, startPos, line, col);
        target.add(scope);
        if (bounded) {
            ctx.setPosition(checkPos + 1);
            parseContent(scope.getChildren(), CloseMode.BRACKET);
        } else {
            ctx.setPosition(afterWhere);
            parseContent(scope.getChildren(), CloseMode.CLOSE_ON_CLAUSE);
        }
    }

    private void parseSetScope(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        int afterSet = startPos + 3;
        int checkPos = afterSet;
        while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
            checkPos++;
        }
        boolean bounded = checkPos < ctx.getInputLength() && ctx.charAt(checkPos) == '[';
        SetScope scope = new SetScope(bounded, startPos, line, col);
        target.add(scope);
        if (bounded) {
            ctx.setPosition(checkPos + 1);
            parseContent(scope.getChildren(), CloseMode.BRACKET);
        } else {
            ctx.setPosition(afterSet);
            parseContent(scope.getChildren(), CloseMode.CLOSE_ON_WHERE);
        }
    }

    // ==================== # 构造分发（scope 层） ====================

    private void parseHash(List<MgxsqlNode> target) {
        char next = ctx.peekChar(1);
        if (next == '[') {
            parseConditionNodeNoGuard(target);
            return;
        }
        if (next == '(') {
            parseConditionNodeWithGuard(target);
            return;
        }
        if (isAndOrKeywordAt()) {
            if (!MgxsqlSyntaxHelper.isAtLineStart(ctx)) {
                throw new MybatisgxException("mgxsql 语法错误: '#and'/'#or' 必须独占一行，%s", ctx.getPositionInfo());
            }
            parseForm1WithPrefix(target);
            return;
        }
        if (next == ',') {
            if (!MgxsqlSyntaxHelper.isAtLineStart(ctx)) {
                throw new MybatisgxException("mgxsql 语法错误: '#' 后带逗号前缀的形式1必须独占一行，%s", ctx.getPositionInfo());
            }
            parseForm1Comma(target);
            return;
        }
        if (isChooseKeywordAt()) {
            parseChooseNode(target);
            return;
        }
        if (MgxsqlSyntaxHelper.isIdentifierStartChar(next)) {
            if (!MgxsqlSyntaxHelper.isAtLineStart(ctx)) {
                throw new MybatisgxException("mgxsql 语法错误: '#condition' 形式1必须独占一行，%s", ctx.getPositionInfo());
            }
            parseForm1Condition(target);
            return;
        }
        throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '['、'('、'{'、'and'/'or' 或标识符，%s", ctx.getPositionInfo());
    }

    private void parseConditionNodeNoGuard(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        ctx.advance();
        String bodyContent = readBracketedContent(true);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().addAll(parseBody(bodyContent));
        target.add(unit);
    }

    private void parseConditionNodeWithGuard(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        String guardContent = readParenthesizedContent().trim();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        String bodyContent = readBracketedContent(true);
        String guard = guardContent.isEmpty() ? null : guardContent;
        IfUnit unit = new IfUnit(guard, startPos, line, col);
        unit.getBody().addAll(parseBody(bodyContent));
        target.add(unit);
    }

    private void parseForm1Condition(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        String condition = readForm1Content();
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().addAll(parseBody(condition));
        target.add(unit);
    }

    private void parseForm1WithPrefix(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        String prefix;
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), ctx.getPosition(), "and")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), ctx.getPosition() + 3)) {
            prefix = "and";
            ctx.setPosition(ctx.getPosition() + 3);
        } else if (MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), ctx.getPosition(), "or")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), ctx.getPosition() + 2)) {
            prefix = "or";
            ctx.setPosition(ctx.getPosition() + 2);
        } else {
            throw new MybatisgxException("mgxsql 语法错误: '#' 后无法识别 and/or 前缀，%s", ctx.getPositionInfo());
        }
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String condition = readForm1Content();
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().add(new PassthroughText(prefix + " ", startPos, line, col));
        unit.getBody().addAll(parseBody(condition));
        target.add(unit);
    }

    private void parseForm1Comma(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.setPosition(ctx.getPosition() + 2);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String condition = readForm1Content();
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().add(new PassthroughText(", ", startPos, line, col));
        unit.getBody().addAll(parseBody(condition));
        target.add(unit);
    }

    private boolean isAndOrKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "and")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 3)) {
            return true;
        }
        return MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "or")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 2);
    }

    private boolean isChooseKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (!MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "choose")) {
            return false;
        }
        return MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 6);
    }

    // ==================== #choose ====================

    private void parseChooseNode(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        ctx.setPosition(ctx.getPosition() + 6);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #choose 后必须跟 '['，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        String inner = readBracketedContent(true);
        target.add(parseChooseBody(inner, startPos, line, col));
    }

    private ChooseUnit parseChooseBody(String inner, int startPos, int line, int col) {
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
                int[] guardRange = readTextParenRange(inner, i);
                String guard = inner.substring(i + 1, guardRange[1] - 1).trim();
                i = guardRange[1];
                while (i < len && Character.isWhitespace(inner.charAt(i))) {
                    i++;
                }
                if (i >= len || inner.charAt(i) != '[') {
                    throw new MybatisgxException("mgxsql 语法错误: #when(expr) 后必须跟 [body]，位置: %s", String.valueOf(i));
                }
                int[] bodyRange = readTextBracketRange(inner, i);
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
                int[] bodyRange = readTextBracketRange(inner, i);
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

    // ==================== IN 子句（scope 层，ctx 基） ====================

    private void parseInClause(List<MgxsqlNode> target) {
        int savedPos = ctx.getPosition() + 2;
        ctx.setPosition(savedPos);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore()) {
            target.add(new PassthroughText("in", ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
            return;
        }
        if (ctx.currentChar() == ':' && ctx.peekChar(1) != ':' && MgxsqlSyntaxHelper.isIdentifierStartAt(ctx, 1)) {
            String collectionName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
            if (collectionName != null) {
                target.add(new ForeachUnit("item", collectionName, "#{item}", ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
                return;
            }
        }
        if (ctx.currentChar() == '(') {
            ForeachUnit foreach = parseInParenthesized();
            if (foreach != null) {
                target.add(foreach);
                return;
            }
        }
        ctx.setPosition(savedPos);
        target.add(new PassthroughText("in ", savedPos, ctx.getLineNumber(), ctx.getColumnNumber()));
    }

    private ForeachUnit parseInParenthesized() {
        int outerParenPos = ctx.getPosition();
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore()) {
            return null;
        }
        if (ctx.currentChar() == '(') {
            ForeachUnit wrapped = parseInComplexWrapped();
            if (wrapped != null) {
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance();
                }
                return wrapped;
            }
            ctx.setPosition(outerParenPos + 1);
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
        }
        if (ctx.currentChar() == ':' && ctx.peekChar(1) != ':' && MgxsqlSyntaxHelper.isIdentifierStartAt(ctx, 1)) {
            String collectionName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
            if (collectionName != null) {
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance();
                    return new ForeachUnit("item", collectionName, "#{item}", ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
                }
            }
        }
        if (ctx.currentChar() == '#' && ctx.peekChar(1) == '{') {
            return null;
        }
        return parseInComplexParen();
    }

    private ForeachUnit parseInComplexWrapped() {
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String itemName = MgxsqlSyntaxHelper.readIdentifier(ctx);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (ctx.hasMore() && ctx.currentChar() == ':') {
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            String collectionName = MgxsqlSyntaxHelper.readIdentifier(ctx);
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ')') {
                ctx.advance();
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                String valueExpr = readArrowRightValue();
                if (valueExpr != null) {
                    return new ForeachUnit(itemName, collectionName, valueExpr, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
                }
            }
        }
        return null;
    }

    private ForeachUnit parseInComplexParen() {
        String itemName = MgxsqlSyntaxHelper.readIdentifier(ctx);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (ctx.hasMore() && ctx.currentChar() == ':') {
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            String collectionName = MgxsqlSyntaxHelper.readIdentifier(ctx);
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ')') {
                ctx.advance();
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                String valueExpr = readArrowRightValue();
                if (valueExpr != null) {
                    return new ForeachUnit(itemName, collectionName, valueExpr, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
                }
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s", ctx.getPositionInfo());
            }
        }
        return null;
    }

    private String readArrowRightValue() {
        if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
            ctx.advance();
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            if (ctx.hasMore() && ctx.currentChar() == '$' && ctx.peekChar(1) == '{') {
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s", ctx.getPositionInfo());
            }
            return MgxsqlSyntaxHelper.readDollarVarRef(ctx);
        }
        return null;
    }

    // ==================== LIKE（scope 层，ctx 基） ====================

    private void parseLikePatternScope(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        String paramName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
        boolean both = false;
        if (ctx.hasMore() && ctx.currentChar() == '%') {
            both = true;
            ctx.advance();
        }
        if (paramName != null) {
            String bindName = "_like_" + paramName.replace('.', '_');
            String bindValue = both ? "'%' + " + paramName + " + '%'" : "'%' + " + paramName;
            target.add(new BindUnit(paramName, bindName, bindValue, false, startPos, line, col));
        }
    }

    // ==================== 原生 XML 标签：三标签下沉 vs 其余透传 ====================

    private void parseXmlStart(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        int nameStart = startPos + 1;
        if (nameStart < ctx.getInputLength() && ctx.charAt(nameStart) == '/') {
            appendXmlPassthrough(target, startPos, line, col);
            return;
        }
        int nameEnd = nameStart;
        while (nameEnd < ctx.getInputLength()) {
            char nc = ctx.charAt(nameEnd);
            if (Character.isLetterOrDigit(nc) || nc == '_' || nc == '-' || nc == '.' || nc == ':') {
                nameEnd++;
            } else {
                break;
            }
        }
        String tagName = ctx.substring(nameStart, nameEnd).toLowerCase();
        if (isContainerTag(tagName)) {
            int openEnd = findOpenTagClose(startPos);
            if (openEnd == -1) {
                String rest = ctx.substring(startPos, ctx.getInputLength());
                target.add(new PassthroughText(rest, startPos, line, col));
                ctx.setPosition(ctx.getInputLength());
                return;
            }
            String openTag = ctx.substring(startPos, openEnd + 1);
            if (openTag.endsWith("/>")) {
                target.add(new PassthroughText(openTag, startPos, line, col));
                ctx.setPosition(openEnd + 1);
                return;
            }
            String closeTag = "</" + tagName + ">";
            DescentScope scope = new DescentScope(openTag, closeTag, startPos, line, col);
            target.add(scope);
            ctx.setPosition(openEnd + 1);
            descentCloseTags.push(closeTag);
            parseContent(scope.getChildren(), CloseMode.DESCENT);
        } else {
            appendXmlPassthrough(target, startPos, line, col);
        }
    }

    private void appendXmlPassthrough(List<MgxsqlNode> target, int startPos, int line, int col) {
        int end = MgxsqlSyntaxHelper.findXmlTagEnd(ctx, startPos);
        if (end == -1) {
            String rest = ctx.substring(startPos, ctx.getInputLength());
            target.add(new PassthroughText(rest, startPos, line, col));
            ctx.setPosition(ctx.getInputLength());
            return;
        }
        String tagContent = ctx.substring(startPos, end + 1);
        if (containsMgxsqlMarker(tagContent)) {
            throw new MybatisgxException("mgxsql 语法错误: 最小单元块内不允许混合 mgxsql 语法，%s", ctx.getPositionInfo());
        }
        target.add(new PassthroughText(tagContent, startPos, line, col));
        ctx.setPosition(end + 1);
    }

    private int findOpenTagClose(int start) {
        int pos = start + 1;
        while (pos < ctx.getInputLength()) {
            char c = ctx.charAt(pos);
            if (c == '"' || c == '\'') {
                char quote = c;
                pos++;
                while (pos < ctx.getInputLength() && ctx.charAt(pos) != quote) {
                    pos++;
                }
                pos++;
                continue;
            }
            if (c == '>') {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private static boolean isContainerTag(String tagName) {
        return "where".equals(tagName) || "set".equals(tagName) || "trim".equals(tagName);
    }

    private static boolean containsMgxsqlMarker(String text) {
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

    // ==================== body 层解析（String 基，移植自 processor） ====================

    private List<MgxsqlNode> parseBody(String text) {
        List<MgxsqlNode> target = new ArrayList<MgxsqlNode>();
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

            if (c == '$' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 ${param}，位置: %s", String.valueOf(i));
            }
            if (c == '<' && isConditionXmlTagStart(text, i)) {
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
            target.add(new PassthroughText(buf.toString(), 0, 0, 0));
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
            int[] range = readTextBracketRange(text, pos);
            String inner = text.substring(pos + 1, range[1] - 1);
            target.add(parseChooseBody(inner, start, 0, 0));
            return range[1];
        }

        if (next == '[') {
            int[] range = readTextBracketRange(text, start + 1);
            String body = text.substring(start + 2, range[1] - 1);
            IfUnit unit = new IfUnit(null, start, 0, 0);
            unit.getBody().addAll(parseBody(body));
            target.add(unit);
            return range[1];
        }

        if (next == '(') {
            int[] guardRange = readTextParenRange(text, start + 1);
            String guard = text.substring(start + 2, guardRange[1] - 1).trim();
            int pos = guardRange[1];
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos >= text.length() || text.charAt(pos) != '[') {
                throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，位置: %s", String.valueOf(start));
            }
            int[] bodyRange = readTextBracketRange(text, pos);
            String body = text.substring(pos + 1, bodyRange[1] - 1);
            IfUnit unit = new IfUnit(guard.isEmpty() ? null : guard, start, 0, 0);
            unit.getBody().addAll(parseBody(body));
            target.add(unit);
            return bodyRange[1];
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

    // ==================== 读取辅助（ctx 基，移植自扫描器） ====================

    private String readBracketedContent(boolean requireClose) {
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

    private String readParenthesizedContent() {
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

    private String readForm1Content() {
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

    // ==================== 文本范围辅助（String 基，移植自 processor） ====================

    private int[] readTextBracketRange(String text, int start) {
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

    private int[] readTextParenRange(String text, int start) {
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

    private static boolean isConditionXmlTagStart(String text, int pos) {
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
}
