package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.*;
import com.mybatisgx.exception.MybatisgxException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    /**
     * 显式 {@code #bind} 声明记录：name → 声明位置（同一 select/update 作用域内）。
     * 用于"声明先于引用"校验与名字唯一性校验。
     */
    private final Map<String, Integer> declaredBinds = new LinkedHashMap<String, Integer>();

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
        this.declaredBinds.clear();
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
                String varName = readDollarVarNameCtx();
                if (varName != null) {
                    checkBindReference(varName, ctx.getPositionInfo());
                    target.add(new LocalVarExpr(varName, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber()));
                }
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
        String bodyContent = readBracketedContent(true);
        IfUnit unit = new IfUnit(null, startPos, line, col);
        unit.getBody().addAll(parseBody(bodyContent));
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
        String guardContent = readParenthesizedContent().trim();
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 后必须跟 '[body]'，%s", ctx.getPositionInfo());
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
        ForeachRhs rhs = readArrowRhsCtx(ctx.getPositionInfo());
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
        String content = readBracketedContent(true);
        int eqIdx = content.indexOf('=');
        if (eqIdx < 0) {
            throw new MybatisgxException("mgxsql 语法错误: #bind[name = expr] 缺少 '='，%s", ctx.getPositionInfo());
        }
        String name = content.substring(0, eqIdx).trim();
        String valueRaw = content.substring(eqIdx + 1).trim();
        if (name.isEmpty() || !isPlainIdentifier(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name 必须是标识符，%s", ctx.getPositionInfo());
        }
        if (declaredBinds.containsKey(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name '%s' 在同一作用域内重复，%s", name, ctx.getPositionInfo());
        }
        String valueOgnl = stripAndValidateBindValue(valueRaw, ctx.getPositionInfo());
        declaredBinds.put(name, startPos);
        target.add(new BindUnit(null, name, valueOgnl, false, startPos, line, col));
    }

    private static boolean isPlainIdentifier(String s) {
        if (s.isEmpty() || !MgxsqlSyntaxHelper.isIdentifierStartChar(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@code =>} 右侧读取结果：valueExpr（{@code #{var}} 或逗号连接的多个）+ 是否复合。
     */
    private static class ForeachRhs {
        final String valueExpr;
        final boolean composite;

        ForeachRhs(String valueExpr, boolean composite) {
            this.valueExpr = valueExpr;
            this.composite = composite;
        }
    }

    /**
     * ctx 基：读取 {@code =>} 右侧（{@code $x} / {@code [$x]} / {@code $x.y} / {@code [$x.y,$z.w]}）。
     * 当前位置应指向 {@code $} 或 {@code [}。
     */
    private ForeachRhs readArrowRhsCtx(String posInfo) {
        if (ctx.hasMore() && ctx.currentChar() == '[') {
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            List<String> parts = new ArrayList<String>();
            String first = readDollarVarNameCtx();
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
                String v = readDollarVarNameCtx();
                if (v == null) {
                    throw new MybatisgxException("mgxsql 语法错误: '=>' 右侧 [] 内必须是 $variable，%s", posInfo);
                }
                parts.add("#{" + v + "}");
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
        String v = readDollarVarNameCtx();
        if (v == null) {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}，%s", posInfo);
        }
        return new ForeachRhs("#{" + v + "}", false);
    }

    /**
     * ctx 基：读取 {@code $variable}，返回变量名（不含 {@code #{}}）；当前位置应指向 {@code $}。
     */
    private String readDollarVarNameCtx() {
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
                ForeachRhs rhs = readArrowRightValue();
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
                ForeachRhs rhs = readArrowRightValue();
                if (rhs != null) {
                    return new ForeachUnit(itemName, collectionName, rhs.valueExpr, true, rhs.composite, ctx.getPosition(), ctx.getLineNumber(), ctx.getColumnNumber());
                }
                throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s", ctx.getPositionInfo());
            }
        }
        return null;
    }

    private ForeachRhs readArrowRightValue() {
        if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
            ctx.advance();
            ctx.advance();
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            return readArrowRhsCtx(ctx.getPositionInfo());
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

            if (c == '$' && i + 1 < text.length() && text.charAt(i + 1) != '{'
                    && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(i + 1))) {
                flushBodyText(target, buf);
                int varEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, i + 1);
                String varName = text.substring(i + 1, varEnd);
                checkBindReference(varName, "位置: " + i);
                target.add(new LocalVarExpr(varName, i, 0, 0));
                i = varEnd;
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
            int[] range = readTextBracketRange(text, start + 1);
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

    private void checkBindReference(String varName, String posInfo) {
        if (!declaredBinds.containsKey(varName)) {
            throw new MybatisgxException("mgxsql 语法错误: $%s 引用先于声明（缺少前置 #bind[%s = ...]），%s", varName, varName, posInfo);
        }
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
        int[] guardRange = readTextParenRange(text, pos);
        String guard = text.substring(pos + 1, guardRange[1] - 1).trim();
        pos = guardRange[1];
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length() || text.charAt(pos) != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #if(expr) 后必须跟 '[body]'，位置: %s", String.valueOf(start));
        }
        int[] bodyRange = readTextBracketRange(text, pos);
        String body = text.substring(pos + 1, bodyRange[1] - 1);
        IfUnit unit = new IfUnit(guard.isEmpty() ? null : guard, start, 0, 0);
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
        int[] parenRange = readTextParenRange(text, pos);
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
        ForeachRhsText rhs = readArrowRhsText(text, pos, start);
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
        int[] range = readTextBracketRange(text, pos);
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
        int[] range = readTextBracketRange(text, pos);
        String content = text.substring(pos + 1, range[1] - 1);
        int eqIdx = content.indexOf('=');
        if (eqIdx < 0) {
            throw new MybatisgxException("mgxsql 语法错误: #bind[name = expr] 缺少 '='，位置: %s", String.valueOf(start));
        }
        String name = content.substring(0, eqIdx).trim();
        String valueRaw = content.substring(eqIdx + 1).trim();
        if (name.isEmpty() || !isPlainIdentifier(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name 必须是标识符，位置: %s", String.valueOf(start));
        }
        if (declaredBinds.containsKey(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name '%s' 在同一作用域内重复，位置: %s", name, String.valueOf(start));
        }
        String valueOgnl = stripAndValidateBindValue(valueRaw, "位置: " + start);
        declaredBinds.put(name, start);
        target.add(new BindUnit(null, name, valueOgnl, false, start, 0, 0));
        return range[1];
    }

    /**
     * String 基 {@code =>} 右侧读取结果（含结束位置）。
     */
    private static class ForeachRhsText {
        final String valueExpr;
        final boolean composite;
        final int end;

        ForeachRhsText(String valueExpr, boolean composite, int end) {
            this.valueExpr = valueExpr;
            this.composite = composite;
            this.end = end;
        }
    }

    private ForeachRhsText readArrowRhsText(String text, int pos, int start) {
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

    private static int[] readDollarVarNameTextRange(String text, int pos) {
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
                        ForeachRhsText rhs = readArrowRhsText(text, pos, pos);
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
