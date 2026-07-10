package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.MgxsqlCondition;
import com.mybatisgx.dsl.mgxsql.model.MgxsqlContext;
import com.mybatisgx.dsl.mgxsql.model.MgxsqlState;
import com.mybatisgx.exception.MybatisgxException;
import org.apache.commons.lang3.StringUtils;

/**
 * mgxsql 逐字符状态机扫描器（v4），将 mgxsql 简化语法文本转换为标准 MyBatis XML 文本
 * <p>
 * 语法规则（v4）：
 * <ul>
 *   <li>where → &lt;where&gt;...&lt;/where&gt;（到子句关键字或末尾）</li>
 *   <li>where[body] → &lt;where&gt;body&lt;/where&gt;（限定边界）</li>
 *   <li>set → &lt;set&gt;...&lt;/set&gt;（到where关键字或末尾）</li>
 *   <li>set[body] → &lt;set&gt;body&lt;/set&gt;（限定边界）</li>
 *   <li>#[body] → &lt;if test="参数非空"&gt; body&lt;/if&gt;（无guard条件体）</li>
 *   <li>#(expr)[body] → &lt;if test="expr"&gt; body&lt;/if&gt;（有guard条件体）</li>
 *   <li>#condition → &lt;if test="参数非空"&gt; condition&lt;/if&gt;（形式1，单条件简写）</li>
 *   <li>:param → #{param}（参数绑定转换）</li>
 *   <li>#{param} → 原样保留（MyBatis参数引用，不是条件语法）</li>
 *   <li>$variable → #{variable}（局部变量，foreach 迭代上下文）</li>
 *   <li>in :list → &lt;foreach&gt;</li>
 *   <li>in (:list) → &lt;foreach&gt;（括号包裹，与 in :list 等价）</li>
 *   <li>in (item:list)=&gt;$item.prop → &lt;foreach&gt;（复杂类型，=>右边只接受$variable）</li>
 *   <li>in #{list} → 原样保留（MyBatis原生，不翻译）</li>
 *   <li>%:name% / :name% / %:name → &lt;bind&gt; + like</li>
 *   <li>guard 表达式中冒号为可选前缀（:age 和 age 等价）</li>
 *   <li>XML 标签原样透传</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public class MgxsqlScanner {

    /**
     * 条件域类型，区分 WHERE 和 SET 域的输出格式差异
     */
    private enum ScopeType {
        /**
         * WHERE 域：&lt;if test="..."&gt; body&lt;/if&gt;（> 后有空格再换 body）
         */
        WHERE,
        /**
         * SET 域：&lt;if test="..."&gt; body&lt;/if&gt;（> 后多一个空格）
         */
        SET
    }

    private final MgxsqlConditionBodyProcessor conditionBodyProcessor = new MgxsqlConditionBodyProcessor();

    /**
     * 将 mgxsql 文本转换为标准 MyBatis XML 文本
     */
    public String process(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        MgxsqlContext ctx = new MgxsqlContext(input.trim());
        while (ctx.hasMore()) {
            switch (ctx.getState()) {
                case NORMAL:
                    this.processNormal(ctx);
                    break;
                case WHERE:
                    this.processWhere(ctx);
                    break;
                case WHERE_BOUNDED:
                    this.processWhereBounded(ctx);
                    break;
                case SET:
                    this.processSet(ctx);
                    break;
                case SET_BOUNDED:
                    this.processSetBounded(ctx);
                    break;
                case STRING_LITERAL:
                    this.processStringLiteral(ctx);
                    break;
                case XML_TAG:
                    this.processXmlTag(ctx);
                    break;
                default:
                    this.processNormal(ctx);
                    break;
            }
        }
        this.closeOpenScopes(ctx);
        return ctx.getOutputString();
    }

    // ==================== NORMAL 状态 ====================

    private void processNormal(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        if (c == '\'') {
            ctx.pushState(MgxsqlState.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '<') {
            if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                ctx.pushState(MgxsqlState.XML_TAG);
                return;
            }
            // SQL 比较运算符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx, "where")) {
            int afterWhere = ctx.getPosition() + 5;
            int checkPos = afterWhere;
            while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
                checkPos++;
            }
            if (checkPos < ctx.getInputLength() && ctx.charAt(checkPos) == '[') {
                ctx.appendOutput("<where>");
                ctx.setPosition(checkPos + 1);
                ctx.setState(MgxsqlState.WHERE_BOUNDED);
            } else {
                ctx.appendOutput("<where>");
                ctx.setPosition(afterWhere);
                ctx.setState(MgxsqlState.WHERE);
            }
            return;
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx, "set")) {
            int afterSet = ctx.getPosition() + 3;
            int checkPos = afterSet;
            while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
                checkPos++;
            }
            if (checkPos < ctx.getInputLength() && ctx.charAt(checkPos) == '[') {
                ctx.appendOutput("<set>");
                ctx.setPosition(checkPos + 1);
                ctx.setState(MgxsqlState.SET_BOUNDED);
            } else {
                ctx.appendOutput("<set>");
                ctx.setPosition(afterSet);
                ctx.setState(MgxsqlState.SET);
            }
            return;
        }
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== WHERE 域（无边界） ====================

    private void processWhere(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        if (c == '\'') {
            ctx.pushState(MgxsqlState.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '<') {
            if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                ctx.pushState(MgxsqlState.XML_TAG);
                return;
            }
            // SQL 比较运算符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (MgxsqlSyntaxHelper.isClauseKeywordAt(ctx)) {
            ctx.appendOutput("</where>");
            ctx.setState(MgxsqlState.NORMAL);
            return;
        }
        if (c == '#') {
            this.processHash(ctx, ScopeType.WHERE);
            return;
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx, "in") && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx) && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        if (c == '%' && ctx.peekChar(1) == ':') {
            this.processLikePattern(ctx);
            return;
        }
        if (c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== WHERE_BOUNDED 域 ====================

    private void processWhereBounded(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        if (c == ']') {
            ctx.appendOutput("</where>");
            ctx.setState(MgxsqlState.NORMAL);
            ctx.advance();
            return;
        }
        if (c == '\'') {
            ctx.pushState(MgxsqlState.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '<') {
            if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                ctx.pushState(MgxsqlState.XML_TAG);
                return;
            }
            // SQL 比较运算符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '#') {
            this.processHash(ctx, ScopeType.WHERE);
            return;
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx, "in") && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx) && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        if (c == '%' && ctx.peekChar(1) == ':') {
            this.processLikePattern(ctx);
            return;
        }
        if (c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== SET 域（无边界） ====================

    private void processSet(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        if (c == '\'') {
            ctx.pushState(MgxsqlState.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '<') {
            if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                ctx.pushState(MgxsqlState.XML_TAG);
                return;
            }
            // SQL 比较运算符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx, "where")) {
            ctx.appendOutput("</set>");
            int afterWhere = ctx.getPosition() + 5;
            int checkPos = afterWhere;
            while (checkPos < ctx.getInputLength() && Character.isWhitespace(ctx.charAt(checkPos))) {
                checkPos++;
            }
            if (checkPos < ctx.getInputLength() && ctx.charAt(checkPos) == '[') {
                ctx.appendOutput("<where>");
                ctx.setPosition(checkPos + 1);
                ctx.setState(MgxsqlState.WHERE_BOUNDED);
            } else {
                ctx.appendOutput("<where>");
                ctx.setPosition(afterWhere);
                ctx.setState(MgxsqlState.WHERE);
            }
            return;
        }
        if (c == '#') {
            this.processHash(ctx, ScopeType.SET);
            return;
        }
        if (c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== SET_BOUNDED 域 ====================

    private void processSetBounded(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        if (c == ']') {
            ctx.appendOutput("</set>");
            ctx.setState(MgxsqlState.NORMAL);
            ctx.advance();
            return;
        }
        if (c == '\'') {
            ctx.pushState(MgxsqlState.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '<') {
            if (MgxsqlSyntaxHelper.isXmlTagStart(ctx)) {
                ctx.pushState(MgxsqlState.XML_TAG);
                return;
            }
            // SQL 比较运算符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        if (c == '#') {
            this.processHash(ctx, ScopeType.SET);
            return;
        }
        if (c == ':' && MgxsqlSyntaxHelper.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== STRING_LITERAL 状态 ====================

    private void processStringLiteral(MgxsqlContext ctx) {
        char c = ctx.currentChar();
        ctx.appendOutput(c);
        if (c == '\'') {
            if (ctx.hasMore() && ctx.peekChar(1) == '\'') {
                ctx.appendOutput('\'');
                ctx.advance();
                ctx.advance();
                return;
            }
            ctx.popState();
        }
        ctx.advance();
    }

    // ==================== XML_TAG 状态 ====================

    private void processXmlTag(MgxsqlContext ctx) {
        int start = ctx.getPosition();
        int end = MgxsqlSyntaxHelper.findXmlTagEnd(ctx, start);
        if (end == -1) {
            ctx.appendOutput(ctx.substring(start, ctx.getInputLength()));
            ctx.setPosition(ctx.getInputLength());
        } else {
            String tagContent = ctx.substring(start, end + 1);
            ctx.appendOutput(tagContent);
            ctx.setPosition(end + 1);
        }
        ctx.popState();
    }

    // ==================== 统一 # 处理（WHERE/SET） ====================

    /**
     * 统一处理 # 字符，根据 scope 区分 WHERE/SET 域
     */
    private void processHash(MgxsqlContext ctx, ScopeType scope) {
        char next = ctx.peekChar(1);
        if (next == '[') {
            this.processConditionNodeNoGuard(ctx, scope);
            return;
        }
        if (next == '(') {
            this.processConditionNodeWithGuard(ctx, scope);
            return;
        }
        if (next == '{') {
            ctx.appendOutput('#');
            ctx.advance();
            return;
        }
        // #and / #or 行首前缀
        if (this.isAndOrKeywordAt(ctx)) {
            if (!MgxsqlSyntaxHelper.isAtLineStart(ctx)) {
                throw new MybatisgxException("mgxsql 语法错误: '#and'/'#or' 必须独占一行，%s",
                        ctx.getPositionInfo());
            }
            this.processForm1WithPrefix(ctx, scope);
            return;
        }
        if (MgxsqlSyntaxHelper.isIdentifierStartChar(next)) {
            if (!MgxsqlSyntaxHelper.isAtLineStart(ctx)) {
                throw new MybatisgxException("mgxsql 语法错误: '#condition' 形式1必须独占一行，%s",
                        ctx.getPositionInfo());
            }
            this.processForm1Condition(ctx, scope);
            return;
        }
        throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '['、'('、'{'、'and'/'or' 或标识符，%s",
                ctx.getPositionInfo());
    }

    /**
     * 检测 # 后面是否是 and/or 关键字（带词边界）
     */
    private boolean isAndOrKeywordAt(MgxsqlContext ctx) {
        int pos = ctx.getPosition() + 1; // # 后面
        // 检查 "and"
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "and")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 3)) {
            return true;
        }
        // 检查 "or"
        if (MgxsqlSyntaxHelper.isKeywordAt(ctx.getInput(), pos, "or")
                && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx.getInput(), pos + 2)) {
            return true;
        }
        return false;
    }

    // ==================== #[body] 无 guard 条件体 ====================

    private void processConditionNodeNoGuard(MgxsqlContext ctx, ScopeType scope) {
        ctx.advance(); // 跳过 #
        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        MgxsqlConditionBodyProcessor.ProcessedBody processed = this.conditionBodyProcessor.processConditionBody(bodyContent);
        String testExpression = this.conditionBodyProcessor.buildTestExpression(processed.getParamPaths());
        String ifContent = processed.getBody().trim();

        this.emitIfTag(ctx, scope, testExpression, ifContent);
    }

    // ==================== #(expr)[body] 有 guard 条件体 ====================

    private void processConditionNodeWithGuard(MgxsqlContext ctx, ScopeType scope) {
        ctx.advance(); // 跳过 #
        String guardContent = this.readParenthesizedContent(ctx).trim();

        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，%s",
                    ctx.getPositionInfo());
        }

        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        String testExpr;
        String ifContent;
        if (StringUtils.isBlank(guardContent)) {
            MgxsqlConditionBodyProcessor.ProcessedBody processed = this.conditionBodyProcessor.processConditionBody(bodyContent);
            testExpr = this.conditionBodyProcessor.buildTestExpression(processed.getParamPaths());
            ifContent = processed.getBody().trim();
        } else {
            testExpr = this.conditionBodyProcessor.stripParamColons(guardContent);
            MgxsqlConditionBodyProcessor.ProcessedBody processed = this.conditionBodyProcessor.processConditionBody(bodyContent);
            ifContent = processed.getBody().trim();
        }

        this.emitIfTag(ctx, scope, testExpr, ifContent);
    }

    // ==================== 形式1：#condition 单条件简写 ====================

    private void processForm1Condition(MgxsqlContext ctx, ScopeType scope) {
        ctx.advance(); // 跳过 #
        String condition = this.readForm1Content(ctx);
        MgxsqlConditionBodyProcessor.ProcessedBody processed = this.conditionBodyProcessor.processConditionBody(condition);
        String testExpression = this.conditionBodyProcessor.buildTestExpression(processed.getParamPaths());
        String ifContent = processed.getBody().trim();

        this.emitIfTag(ctx, scope, testExpression, ifContent);
    }

    // ==================== 形式1：#and/#or 行首前缀 ====================

    private void processForm1WithPrefix(MgxsqlContext ctx, ScopeType scope) {
        ctx.advance(); // 跳过 #
        // 读取前缀（and/or）
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
            throw new MybatisgxException("mgxsql 语法错误: '#' 后无法识别 and/or 前缀，%s",
                    ctx.getPositionInfo());
        }
        MgxsqlSyntaxHelper.skipWhitespace(ctx);
        String condition = this.readForm1Content(ctx);
        MgxsqlConditionBodyProcessor.ProcessedBody processed = this.conditionBodyProcessor.processConditionBody(condition);
        String testExpression = this.conditionBodyProcessor.buildTestExpression(processed.getParamPaths());
        String ifContent = prefix + " " + processed.getBody().trim();

        this.emitIfTag(ctx, scope, testExpression, ifContent);
    }

    // ==================== 统一 if 标签输出 ====================

    /**
     * 统一输出 &lt;if&gt; 标签，根据 scope 区分 WHERE/SET 域的空格格式
     */
    private void emitIfTag(MgxsqlContext ctx, ScopeType scope, String testExpr, String body) {
        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpr);
        if (scope == ScopeType.SET) {
            ctx.appendOutput("\"> ");
        } else {
            ctx.appendOutput("\">");
            ctx.appendOutput(" ");
        }
        ctx.appendOutput(body);
        ctx.appendOutput("</if>");
    }

    // ==================== 读取方法 ====================

    private String readForm1Content(MgxsqlContext ctx) {
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
            if (parenDepth == 0 && MgxsqlSyntaxHelper.isKeywordAt(ctx, "and") && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx) && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 3)) {
                throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，%s",
                        ctx.getPositionInfo());
            }
            if (parenDepth == 0 && MgxsqlSyntaxHelper.isKeywordAt(ctx, "or") && MgxsqlSyntaxHelper.isWordBoundaryBefore(ctx) && MgxsqlSyntaxHelper.isWordBoundaryAfter(ctx, 2)) {
                throw new MybatisgxException("mgxsql 语法错误: 形式1条件内不允许行内 and/or，请使用 #[...] 或拆行 #and/#or，%s",
                        ctx.getPositionInfo());
            }
            content.append(c);
            ctx.advance();
        }
        return content.toString();
    }

    private String readBracketedContent(MgxsqlContext ctx, boolean requireClose) {
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
            throw new MybatisgxException("mgxsql 语法错误: '[' 未闭合，缺少匹配的 ']'，%s",
                    ctx.getPositionInfo());
        }
        return content.toString();
    }

    private String readBracketedContent(MgxsqlContext ctx) {
        return this.readBracketedContent(ctx, true);
    }

    private String readParenthesizedContent(MgxsqlContext ctx) {
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
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    ctx.advance();
                    break;
                }
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
                continue;
            }
            content.append(c);
            ctx.advance();
        }
        return content.toString();
    }

    // ==================== IN 子句处理（WHERE 域直接扫描） ====================

    private void processInClause(MgxsqlContext ctx) {
        int savedPos = ctx.getPosition() + 2; // 保存 "in" 之后的位置
        ctx.setPosition(savedPos);
        MgxsqlSyntaxHelper.skipWhitespace(ctx);

        if (!ctx.hasMore()) {
            ctx.appendOutput("in");
            return;
        }

        // 简单类型 IN：in :list
        if (ctx.currentChar() == ':' && ctx.peekChar(1) != ':' && MgxsqlSyntaxHelper.isIdentifierStartAt(ctx, 1)) {
            String collectionName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
            if (collectionName != null) {
                ctx.appendOutput("in <foreach item=\"item\" collection=\"");
                ctx.appendOutput(collectionName);
                ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return;
            }
        }

        if (ctx.currentChar() == '(') {
            int outerParenPos = ctx.getPosition();
            ctx.advance(); // 跳过 (
            MgxsqlSyntaxHelper.skipWhitespace(ctx);

            if (!ctx.hasMore()) {
                ctx.appendOutput("in (");
                return;
            }

            // in ((item:collection)=>$item.prop) — 复杂类型 IN 外层括号包裹
            if (ctx.currentChar() == '(') {
                ctx.advance(); // 跳过内层 (
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                String itemName = MgxsqlSyntaxHelper.readIdentifier(ctx);
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ':') {
                    ctx.advance(); // 跳过 :
                    MgxsqlSyntaxHelper.skipWhitespace(ctx);
                    String collectionName = MgxsqlSyntaxHelper.readIdentifier(ctx);
                    MgxsqlSyntaxHelper.skipWhitespace(ctx);
                    if (ctx.hasMore() && ctx.currentChar() == ')') {
                        ctx.advance(); // 跳过内层 )
                        MgxsqlSyntaxHelper.skipWhitespace(ctx);
                        if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
                            ctx.advance(); // 跳过 =
                            ctx.advance(); // 跳过 >
                            MgxsqlSyntaxHelper.skipWhitespace(ctx);
                            String valueExpr = this.readArrowRightValue(ctx);
                            if (valueExpr != null) {
                                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                                // 消费外层 )
                                if (ctx.hasMore() && ctx.currentChar() == ')') {
                                    ctx.advance();
                                }
                                ctx.appendOutput("in <foreach item=\"");
                                ctx.appendOutput(itemName);
                                ctx.appendOutput("\" collection=\"");
                                ctx.appendOutput(collectionName);
                                ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">");
                                ctx.appendOutput(valueExpr);
                                ctx.appendOutput("</foreach>");
                                return;
                            }
                        }
                    }
                }
                // 外层括号包裹解析失败，恢复位置继续尝试其他路径
                ctx.setPosition(outerParenPos + 1); // 回到 ( 之后
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
            }

            // in (:list) 或 in ( :list ) — 简单 IN + 括号
            if (ctx.currentChar() == ':' && ctx.peekChar(1) != ':' && MgxsqlSyntaxHelper.isIdentifierStartAt(ctx, 1)) {
                String collectionName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
                if (collectionName != null) {
                    MgxsqlSyntaxHelper.skipWhitespace(ctx);
                    if (ctx.hasMore() && ctx.currentChar() == ')') {
                        ctx.advance(); // 跳过 )
                        ctx.appendOutput("in <foreach item=\"item\" collection=\"");
                        ctx.appendOutput(collectionName);
                        ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                        return;
                    }
                }
            }

            // in (#{list}) — MyBatis 原生，原样透传
            if (ctx.currentChar() == '#' && ctx.peekChar(1) == '{') {
                // 恢复到 ( 之前，原样输出整个 in (#{list})
                ctx.setPosition(savedPos);
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                ctx.appendOutput("in");
                return;
            }

            // in (item:collection)=>$item.prop — 复杂类型 IN（无外层括号）
            String itemName = MgxsqlSyntaxHelper.readIdentifier(ctx);
            MgxsqlSyntaxHelper.skipWhitespace(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ':') {
                ctx.advance(); // 跳过 :
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                String collectionName = MgxsqlSyntaxHelper.readIdentifier(ctx);
                MgxsqlSyntaxHelper.skipWhitespace(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance(); // 跳过 )
                    MgxsqlSyntaxHelper.skipWhitespace(ctx);
                    if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
                        ctx.advance(); // 跳过 =
                        ctx.advance(); // 跳过 >
                        MgxsqlSyntaxHelper.skipWhitespace(ctx);
                        String valueExpr = this.readArrowRightValue(ctx);
                        if (valueExpr != null) {
                            ctx.appendOutput("in <foreach item=\"");
                            ctx.appendOutput(itemName);
                            ctx.appendOutput("\" collection=\"");
                            ctx.appendOutput(collectionName);
                            ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">");
                            ctx.appendOutput(valueExpr);
                            ctx.appendOutput("</foreach>");
                            return;
                        }
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s",
                                ctx.getPositionInfo());
                    }
                }
            }
        }

        // 降级：恢复位置，原样输出 "in "
        ctx.setPosition(savedPos);
        ctx.appendOutput("in ");
    }

    /**
     * 读取 => 右边的值表达式，返回 #{variable} 形式；如果是 ${} 或 #{} 则报语法错误
     */
    private String readArrowRightValue(MgxsqlContext ctx) {
        // => 右边出现 ${} 报语法错误
        if (ctx.hasMore() && ctx.currentChar() == '$' && ctx.peekChar(1) == '{') {
            throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, %s",
                    ctx.getPositionInfo());
        }
        String valueExpr = MgxsqlSyntaxHelper.readDollarVarRef(ctx);
        return valueExpr;
    }

    // ==================== LIKE 模式处理（WHERE 域直接扫描） ====================

    private void processLikePattern(MgxsqlContext ctx) {
        ctx.advance(); // 跳过 %
        String paramName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
        MgxsqlCondition.LikeType likeType = MgxsqlCondition.LikeType.LEFT;

        if (ctx.hasMore() && ctx.currentChar() == '%') {
            likeType = MgxsqlCondition.LikeType.BOTH;
            ctx.advance();
        }

        if (paramName != null) {
            String bindName = "_like_" + paramName.replace('.', '_');
            String bindValue = this.conditionBodyProcessor.buildLikeBindValue(paramName, likeType);
            ctx.appendOutput("<bind name=\"");
            ctx.appendOutput(bindName);
            ctx.appendOutput("\" value=\"");
            ctx.appendOutput(bindValue);
            ctx.appendOutput("\"/>");
        }
    }

    // ==================== 参数引用处理（:param → #{param}） ====================

    private void processParamRef(MgxsqlContext ctx) {
        String paramName = MgxsqlSyntaxHelper.readColonParamRef(ctx);
        if (paramName != null) {
            ctx.appendOutput("#{");
            ctx.appendOutput(paramName);
            ctx.appendOutput("}");
        }
    }

    // ==================== 域关闭 ====================

    private void closeOpenScopes(MgxsqlContext ctx) {
        MgxsqlState state = ctx.getState();
        if (state == MgxsqlState.WHERE) {
            ctx.appendOutput("</where>");
        } else if (state == MgxsqlState.WHERE_BOUNDED) {
            throw new MybatisgxException("mgxsql 语法错误: 'where[' 未闭合，缺少匹配的 ']'，%s",
                    ctx.getPositionInfo());
        } else if (state == MgxsqlState.SET) {
            ctx.appendOutput("</set>");
        } else if (state == MgxsqlState.SET_BOUNDED) {
            throw new MybatisgxException("mgxsql 语法错误: 'set[' 未闭合，缺少匹配的 ']'，%s",
                    ctx.getPositionInfo());
        }
    }
}
