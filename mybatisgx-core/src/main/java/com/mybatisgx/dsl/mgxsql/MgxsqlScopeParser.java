package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.*;
import com.mybatisgx.exception.MybatisgxException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * mgxsql scope 层解析器（ctx 基）：以 {@link MgxsqlContext} 逐字符游标推进，
 * 处理顶层 {@code #[...]}、{@code #{}}、{@code ${}}、{@code <xml>} 容器下沉、
 * {@code where}/{@code set} 关键字块等。
 *
 * @author 薛承城
 * @description mgxsql scope 层解析器
 * @date 2026/7/27
 */
class MgxsqlScopeParser {

    private final MgxsqlContext ctx;
    private final Deque<String> descentCloseTags = new ArrayDeque<String>();
    private final BindRegistry bindRegistry;
    private final MgxqlBodyParser bodyParser;

    MgxsqlScopeParser(MgxsqlContext ctx, BindRegistry bindRegistry, MgxqlBodyParser bodyParser) {
        this.ctx = ctx;
        this.bindRegistry = bindRegistry;
        this.bodyParser = bodyParser;
    }

    void parse(List<MgxsqlNode> root) {
        parseContent(root, CloseMode.END_ONLY);
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
                if (mode == CloseMode.END_ONLY && !isIncludeKeywordAt() && !isBindKeywordAt()) {
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

            if (mode != CloseMode.END_ONLY && c == '$' && ctx.peekChar(1) != '{'
                    && MgxsqlSyntaxHelper.isIdentifierStartChar(ctx.peekChar(1))) {
                flushText(target, text);
                String varName = MgxsqlReadHelpers.readDollarVarNameCtx(ctx);
                if (varName != null) {
                    bindRegistry.checkBindReference(varName, ctx.getPositionInfo());
                    target.add(new LocalVarExpr(varName, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
                }
                continue;
            }
            if (mode != CloseMode.END_ONLY && c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
                flushText(target, text);
                int startPos = ctx.getPosition();
                int line = ctx.getLineNumber();
                int col = ctx.getColumnNumber();
                String paramName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
                if (paramName != null) {
                    if (ctx.hasMore() && ctx.currentChar() == '%') {
                        ctx.advance();
                        String bindName = "_like_" + paramName.replace('.', '_');
                        String bindValue = paramName + " + '%'";
                        target.add(new BindUnit(paramName, bindName, bindValue, true, startPos, line, col));
                    } else {
                        target.add(new ParamExpr(paramName, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
                    }
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
            target.add(new SqlText(text.toString(), ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
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
        if (isIfKeywordAt()) {
            parseIfNode(target);
            return;
        }
        if (next == '(') {
            throw new MybatisgxException("mgxsql 语法错误: '#(expr)' 已废弃，请改用 '#if(expr)'，%s", ctx.getPositionInfo());
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
        if (isForKeywordAt()) {
            parseForNode(target);
            return;
        }
        if (isBindKeywordAt()) {
            parseBindNode(target);
            return;
        }
        if (isIncludeKeywordAt()) {
            parseIncludeNode(target);
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

    private boolean isIfKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (!MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "if")) {
            return false;
        }
        return MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 2);
    }

    private boolean isForKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (!MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "for")) {
            return false;
        }
        return MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 3);
    }

    private boolean isBindKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (!MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "bind")) {
            return false;
        }
        return MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 4);
    }

    private boolean isIncludeKeywordAt() {
        int pos = ctx.getPosition() + 1;
        if (!MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "include")) {
            return false;
        }
        return MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 7);
    }

    private void parseConditionNodeNoGuard(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        ctx.advance();
        String bodyContent = MgxsqlReadHelpers.readBracketedContent(ctx, true);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().addAll(bodyParser.parseBody(bodyContent));
        target.add(unit);
    }

    private void parseIfNode(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();                            // skip '#'
        ctx.setPosition(ctx.getPosition() + 2);   // skip "if"
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '(') {
            throw new MybatisgxException("mgxsql 语法错误: #if 后必须跟 '(expr)'，%s", ctx.getPositionInfo());
        }
        String guardContent = MgxsqlReadHelpers.readParenthesizedContent(ctx).trim();
        if (guardContent.isEmpty()) {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 的圆括号内表达式不能为空，%s", ctx.getPositionInfo());
        }
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 后必须跟 '[body]'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        String bodyContent = MgxsqlReadHelpers.readBracketedContent(ctx, true);
        IfUnit unit = new IfUnit(guardContent, startPos, line, col);
        unit.getBody().addAll(bodyParser.parseBody(bodyContent));
        target.add(unit);
    }

    private void parseForm1Condition(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();
        String condition = MgxsqlReadHelpers.readForm1Content(ctx);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().addAll(bodyParser.parseBody(condition));
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
        String condition = MgxsqlReadHelpers.readForm1Content(ctx);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().add(new SqlText(prefix + " ", startPos, line, col));
        unit.getBody().addAll(bodyParser.parseBody(condition));
        target.add(unit);
    }

    private void parseForm1Comma(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.setPosition(ctx.getPosition() + 2);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String condition = MgxsqlReadHelpers.readForm1Content(ctx);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().add(new SqlText(", ", startPos, line, col));
        unit.getBody().addAll(bodyParser.parseBody(condition));
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

    // ==================== #for / #include / #bind（scope 层，ctx 基） ====================

    private void parseForNode(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();                            // skip '#'
        ctx.setPosition(ctx.getPosition() + 3);   // skip "for"
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '(') {
            throw new MybatisgxException("mgxsql 语法错误: #for 后必须跟 '(item:collection)'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String itemName = MgxsqlSyntaxHelper.readIdentifier(ctx);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != ':') {
            throw new MybatisgxException("mgxsql 语法错误: #for 的 (item:collection) 缺少 ':'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String collectionName = MgxsqlSyntaxHelper.readIdentifier(ctx);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != ')') {
            throw new MybatisgxException("mgxsql 语法错误: #for 的 (item:collection) 未以 ')' 闭合，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '=' || ctx.peekChar(1) != '>') {
            throw new MybatisgxException("mgxsql 语法错误: #for(item:collection) 后必须跟 '=>'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        ctx.advance();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        MgxsqlReadHelpers.ForeachRhs rhs = MgxsqlReadHelpers.readArrowRhsCtx(ctx, ctx.getPositionInfo());
        target.add(new ForeachUnit(itemName, collectionName, rhs.valueExpr, false, rhs.composite,
                startPos, line, col));
    }

    private void parseIncludeNode(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();                            // skip '#'
        ctx.setPosition(ctx.getPosition() + 7);   // skip "include"
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #include 后必须跟 '[sqlId]'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        StringBuilder refid = new StringBuilder();
        while (ctx.hasMore() && ctx.currentChar() != ']') {
            char c = ctx.currentChar();
            if (c == ':' || c == '#' || c == '$') {
                throw new MybatisgxException("mgxsql 语法错误: #include 的 refid 只接受静态标识符，不接受 :param / #{} / ${}，%s", ctx.getPositionInfo());
            }
            refid.append(c);
            ctx.advance();
        }
        if (!ctx.hasMore()) {
            throw new MybatisgxException("mgxsql 语法错误: #include[sqlId] 未以 ']' 闭合，%s", ctx.getPositionInfo());
        }
        ctx.advance(); // skip ']'
        String id = refid.toString().trim();
        if (id.isEmpty()) {
            throw new MybatisgxException("mgxsql 语法错误: #include[sqlId] 的 refid 不能为空，%s", ctx.getPositionInfo());
        }
        target.add(new IncludeUnit(id, startPos, line, col));
    }

    private void parseBindNode(List<MgxsqlNode> target) {
        int startPos = ctx.getPosition();
        int line = ctx.getLineNumber();
        int col = ctx.getColumnNumber();
        ctx.advance();                            // skip '#'
        ctx.setPosition(ctx.getPosition() + 4);   // skip "bind"
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #bind 后必须跟 '[name = expr]'，%s", ctx.getPositionInfo());
        }
        ctx.advance();
        String content = MgxsqlReadHelpers.readBracketedContent(ctx, true);
        int eqIdx = content.indexOf('=');
        if (eqIdx < 0) {
            throw new MybatisgxException("mgxsql 语法错误: #bind[name = expr] 缺少 '='，%s", ctx.getPositionInfo());
        }
        String name = content.substring(0, eqIdx).trim();
        String valueRaw = content.substring(eqIdx + 1).trim();
        if (name.isEmpty() || !MgxsqlReadHelpers.isPlainIdentifier(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name 必须是标识符，%s", ctx.getPositionInfo());
        }
        bindRegistry.putAndCheckDup(name, startPos, ctx.getPositionInfo());
        String valueOgnl = MgxsqlReadHelpers.stripAndValidateBindValue(valueRaw, ctx.getPositionInfo());
        target.add(new BindUnit(null, name, valueOgnl, false, startPos, line, col));
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
        String inner = MgxsqlReadHelpers.readBracketedContent(ctx, true);
        target.add(bodyParser.parseChooseBody(inner, startPos, line, col));
    }

    // ==================== IN 子句（scope 层，ctx 基） ====================

    private void parseInClause(List<MgxsqlNode> target) {
        int savedPos = ctx.getPosition() + 2;
        ctx.setPosition(savedPos);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore()) {
            target.add(new SqlText("in", ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
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
        target.add(new SqlText("in ", savedPos, ctx.getLineNumber(), ctx.getColumnNumber()));
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
                MgxsqlReadHelpers.ForeachRhs rhs = readArrowRightValue();
                if (rhs != null) {
                    return new ForeachUnit(itemName, collectionName, rhs.valueExpr, true, rhs.composite, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
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
                MgxsqlReadHelpers.ForeachRhs rhs = readArrowRightValue();
                if (rhs != null) {
                    return new ForeachUnit(itemName, collectionName, rhs.valueExpr, true, rhs.composite, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
                }
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s", ctx.getPositionInfo());
            }
        }
        return null;
    }

    private MgxsqlReadHelpers.ForeachRhs readArrowRightValue() {
        if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
            ctx.advance();
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            return MgxsqlReadHelpers.readArrowRhsCtx(ctx, ctx.getPositionInfo());
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
            target.add(new BindUnit(paramName, bindName, bindValue, true, startPos, line, col));
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
        if (MgxsqlReadHelpers.isContainerTag(tagName)) {
            int openEnd = findOpenTagClose(startPos);
            if (openEnd == -1) {
                String rest = ctx.substring(startPos, ctx.getInputLength());
                target.add(new XmlTagText(rest, startPos, line, col));
                ctx.setPosition(ctx.getInputLength());
                return;
            }
            String openTag = ctx.substring(startPos, openEnd + 1);
            if (openTag.endsWith("/>")) {
                target.add(new XmlTagText(openTag, startPos, line, col));
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
            target.add(new XmlTagText(rest, startPos, line, col));
            ctx.setPosition(ctx.getInputLength());
            return;
        }
        String tagContent = ctx.substring(startPos, end + 1);
        if (MgxsqlReadHelpers.containsMgxsqlMarker(tagContent)) {
            throw new MybatisgxException("mgxsql 语法错误: 最小单元块内不允许混合 mgxsql 语法，%s", ctx.getPositionInfo());
        }
        target.add(new XmlTagText(tagContent, startPos, line, col));
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
}
