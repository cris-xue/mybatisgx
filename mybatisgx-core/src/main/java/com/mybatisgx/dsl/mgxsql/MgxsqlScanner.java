package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.S2Condition;
import com.mybatisgx.dsl.mgxsql.model.S2Context;
import com.mybatisgx.dsl.mgxsql.model.S2State;
import com.mybatisgx.exception.MybatisgxException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
 *   <li>in (item:list)=&gt;$item.prop → &lt;foreach&gt;</li>
 *   <li>%:name% / :name% / %:name → &lt;bind&gt; + like</li>
 *   <li>guard 表达式中冒号为可选前缀（:age 和 age 等价）</li>
 *   <li>XML 标签原样透传</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public class MgxsqlScanner {

    private static final String IS_NOT_EMPTY = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%s)";

    /**
     * 子句关键字表：这些关键字在 WHERE 域中出现时，应关闭 &lt;where&gt; 标签
     * 每个 entry 是关键字组合（小写），以空格分隔的视为多 token 组合
     */
    private static final String[] CLAUSE_KEYWORDS = {
            "order by", "group by", "having", "limit",
            "union", "union all", "intersect", "except", "minus"
    };

    /**
     * 将 mgxsql 文本转换为标准 MyBatis XML 文本
     */
    public String process(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        S2Context ctx = new S2Context(input.trim());
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
        // 关闭未关闭的域
        this.closeOpenScopes(ctx);
        return ctx.getOutputString();
    }

    // ==================== NORMAL 状态 ====================

    private void processNormal(S2Context ctx) {
        char c = ctx.currentChar();
        // 检测字符串字面量
        if (c == '\'') {
            ctx.pushState(S2State.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        // 检测 XML 标签
        if (c == '<') {
            ctx.pushState(S2State.XML_TAG);
            return;
        }
        // 检测 where 关键字
        if (this.isKeywordAt(ctx, "where")) {
            // 检测 where 后是否紧跟 [（无空格，where[ 为 mgxsql 边界标记）
            int afterWhere = ctx.getPosition() + 5;
            if (afterWhere < ctx.getInputLength() && ctx.charAt(afterWhere) == '[') {
                // where[body] — 有边界
                ctx.appendOutput("<where>");
                ctx.setPosition(afterWhere + 1); // 跳过 where[
                ctx.setState(S2State.WHERE_BOUNDED);
            } else {
                // where — 无边界
                ctx.appendOutput("<where>");
                ctx.setPosition(ctx.getPosition() + 5);
                ctx.setState(S2State.WHERE);
            }
            return;
        }
        // 检测 set 关键字（仅在 update 上下文）
        if (this.isKeywordAt(ctx, "set")) {
            int afterSet = ctx.getPosition() + 3;
            if (afterSet < ctx.getInputLength() && ctx.charAt(afterSet) == '[') {
                // set[body] — 有边界
                ctx.appendOutput("<set>");
                ctx.setPosition(afterSet + 1); // 跳过 set[
                ctx.setState(S2State.SET_BOUNDED);
            } else {
                // set — 无边界
                ctx.appendOutput("<set>");
                ctx.setPosition(ctx.getPosition() + 3);
                ctx.setState(S2State.SET);
            }
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== WHERE 域（无边界） ====================

    private void processWhere(S2Context ctx) {
        char c = ctx.currentChar();
        // 检测字符串字面量
        if (c == '\'') {
            ctx.pushState(S2State.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        // 检测 XML 标签
        if (c == '<') {
            ctx.pushState(S2State.XML_TAG);
            return;
        }
        // 检测子句关键字 → 关闭 WHERE 域，回到 NORMAL
        if (this.isClauseKeywordAt(ctx)) {
            ctx.appendOutput("</where>");
            ctx.setState(S2State.NORMAL);
            // 不 advance，让 NORMAL 处理后续内容
            return;
        }
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            this.processWhereHash(ctx);
            return;
        }
        // 检测 in 关键字
        if (this.isKeywordAt(ctx, "in") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        // 检测 %:name （LIKE 模式）
        if (c == '%' && ctx.peekChar(1) == ':') {
            this.processLikePattern(ctx);
            return;
        }
        // 检测 :param 参数绑定
        if (c == ':' && this.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== WHERE_BOUNDED 域（有边界 where[body]） ====================

    private void processWhereBounded(S2Context ctx) {
        char c = ctx.currentChar();
        // 匹配的 ] → 关闭 WHERE_BOUNDED
        if (c == ']') {
            ctx.appendOutput("</where>");
            ctx.setState(S2State.NORMAL);
            ctx.advance();
            return;
        }
        // 检测字符串字面量
        if (c == '\'') {
            ctx.pushState(S2State.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        // 检测 XML 标签
        if (c == '<') {
            ctx.pushState(S2State.XML_TAG);
            return;
        }
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            this.processWhereHash(ctx);
            return;
        }
        // 检测 in 关键字
        if (this.isKeywordAt(ctx, "in") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        // 检测 %:name （LIKE 模式）
        if (c == '%' && ctx.peekChar(1) == ':') {
            this.processLikePattern(ctx);
            return;
        }
        // 检测 :param 参数绑定
        if (c == ':' && this.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== SET 域（无边界） ====================

    private void processSet(S2Context ctx) {
        char c = ctx.currentChar();
        // 检测字符串字面量
        if (c == '\'') {
            ctx.pushState(S2State.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        // 检测 XML 标签
        if (c == '<') {
            ctx.pushState(S2State.XML_TAG);
            return;
        }
        // 检测 where 关键字 → 关闭 SET 域，开启 WHERE 域
        if (this.isKeywordAt(ctx, "where")) {
            ctx.appendOutput("</set>");
            // 检测 where 后是否紧跟 [
            int afterWhere = ctx.getPosition() + 5;
            if (afterWhere < ctx.getInputLength() && ctx.charAt(afterWhere) == '[') {
                ctx.appendOutput("<where>");
                ctx.setPosition(afterWhere + 1);
                ctx.setState(S2State.WHERE_BOUNDED);
            } else {
                ctx.appendOutput("<where>");
                ctx.setPosition(ctx.getPosition() + 5);
                ctx.setState(S2State.WHERE);
            }
            return;
        }
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            this.processSetHash(ctx);
            return;
        }
        // 检测 :param 参数绑定
        if (c == ':' && this.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== SET_BOUNDED 域（有边界 set[body]） ====================

    private void processSetBounded(S2Context ctx) {
        char c = ctx.currentChar();
        // 匹配的 ] → 关闭 SET_BOUNDED
        if (c == ']') {
            ctx.appendOutput("</set>");
            ctx.setState(S2State.NORMAL);
            ctx.advance();
            return;
        }
        // 检测字符串字面量
        if (c == '\'') {
            ctx.pushState(S2State.STRING_LITERAL);
            ctx.appendOutput(c);
            ctx.advance();
            return;
        }
        // 检测 XML 标签
        if (c == '<') {
            ctx.pushState(S2State.XML_TAG);
            return;
        }
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            this.processSetHash(ctx);
            return;
        }
        // 检测 :param 参数绑定
        if (c == ':' && this.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== STRING_LITERAL 状态 ====================

    private void processStringLiteral(S2Context ctx) {
        char c = ctx.currentChar();
        ctx.appendOutput(c);
        if (c == '\'') {
            // 检查是否是转义的单引号 ''
            if (ctx.hasMore() && ctx.peekChar(1) == '\'') {
                ctx.appendOutput('\'');
                ctx.advance();
                ctx.advance();
                return;
            }
            // 字符串结束
            ctx.popState();
        }
        ctx.advance();
    }

    // ==================== XML_TAG 状态 ====================

    private void processXmlTag(S2Context ctx) {
        int start = ctx.getPosition();
        int end = this.findXmlTagEnd(ctx, start);
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

    // ==================== WHERE/SET 域 # 处理 ====================

    /**
     * WHERE 域处理 # 字符
     */
    private void processWhereHash(S2Context ctx) {
        char next = ctx.peekChar(1);
        if (next == '[') {
            // #[body] — 无 guard 条件体
            this.processConditionNodeNoGuard(ctx);
            return;
        }
        if (next == '(') {
            // #(expr)[body] — 有 guard 条件体
            this.processConditionNodeWithGuard(ctx);
            return;
        }
        if (next == '{') {
            // #{param} 原样输出（MyBatis参数引用）
            ctx.appendOutput('#');
            ctx.advance();
            return;
        }
        if (this.isIdentifierStartChar(next)) {
            // 形式1：#condition（单条件简写，读到行尾）
            this.processForm1Condition(ctx);
            return;
        }
        // # 后跟其他字符，语法错误
        throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '['、'('、'{' 或标识符，%s",
                ctx.getPositionInfo());
    }

    /**
     * SET 域处理 # 字符
     */
    private void processSetHash(S2Context ctx) {
        char next = ctx.peekChar(1);
        if (next == '[') {
            // #[body] — 无 guard 条件体（SET 域）
            this.processConditionNodeNoGuardSet(ctx);
            return;
        }
        if (next == '(') {
            // #(expr)[body] — 有 guard 条件体（SET 域）
            this.processConditionNodeWithGuardSet(ctx);
            return;
        }
        if (next == '{') {
            // #{param} 原样输出
            ctx.appendOutput('#');
            ctx.advance();
            return;
        }
        if (this.isIdentifierStartChar(next)) {
            // 形式1：#condition（SET 域）
            this.processForm1ConditionSet(ctx);
            return;
        }
        throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '['、'('、'{' 或标识符，%s",
                ctx.getPositionInfo());
    }

    // ==================== #[body] 无 guard 条件体（WHERE 域） ====================

    /**
     * 处理 #[body]：无 guard，从 body 中提取 :param 推导隐式 guard
     */
    private void processConditionNodeNoGuard(S2Context ctx) {
        ctx.advance(); // 跳过 #
        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        ProcessedBody processed = this.processConditionBody(bodyContent);
        String testExpression = this.buildTestExpression(processed.paramPaths);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\">");
        ctx.appendOutput(" ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== #(expr)[body] 有 guard 条件体（WHERE 域） ====================

    /**
     * 处理 #(expr)[body]：有 guard，expr 中 :param 去冒号
     */
    private void processConditionNodeWithGuard(S2Context ctx) {
        ctx.advance(); // 跳过 #
        String guardContent = this.readParenthesizedContent(ctx);

        // 检测后面是否有 [
        this.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，%s",
                    ctx.getPositionInfo());
        }

        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        if (StringUtils.isBlank(guardContent)) {
            // 空 guard → 隐式推导
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpression);
            ctx.appendOutput("\">");
            ctx.appendOutput(" ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        } else {
            // 显式 guard → 去冒号
            String testExpr = this.stripParamColons(guardContent);
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpr);
            ctx.appendOutput("\">");
            ctx.appendOutput(" ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        }
    }

    // ==================== #[body] 无 guard 条件体（SET 域） ====================

    private void processConditionNodeNoGuardSet(S2Context ctx) {
        ctx.advance(); // 跳过 #
        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        ProcessedBody processed = this.processConditionBody(bodyContent);
        String testExpression = this.buildTestExpression(processed.paramPaths);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\"> ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== #(expr)[body] 有 guard 条件体（SET 域） ====================

    private void processConditionNodeWithGuardSet(S2Context ctx) {
        ctx.advance(); // 跳过 #
        String guardContent = this.readParenthesizedContent(ctx);

        this.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '[') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，%s",
                    ctx.getPositionInfo());
        }

        ctx.advance(); // 跳过 [
        String bodyContent = this.readBracketedContent(ctx);

        if (StringUtils.isBlank(guardContent)) {
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpression);
            ctx.appendOutput("\"> ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        } else {
            String testExpr = this.stripParamColons(guardContent);
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpr);
            ctx.appendOutput("\"> ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        }
    }

    // ==================== 形式1：#condition 单条件简写（WHERE 域） ====================

    private void processForm1Condition(S2Context ctx) {
        ctx.advance(); // 跳过 #
        String condition = this.readForm1Content(ctx);
        ProcessedBody processed = this.processConditionBody(condition);
        String testExpression = this.buildTestExpression(processed.paramPaths);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\">");
        ctx.appendOutput(" ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== 形式1：#condition 单条件简写（SET 域） ====================

    private void processForm1ConditionSet(S2Context ctx) {
        ctx.advance(); // 跳过 #
        String condition = this.readForm1Content(ctx);
        ProcessedBody processed = this.processConditionBody(condition);
        String testExpression = this.buildTestExpression(processed.paramPaths);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\"> ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== 条件体处理 ====================

    private static class ProcessedBody {
        String body;
        List<String> paramPaths;

        ProcessedBody(String body, List<String> paramPaths) {
            this.body = body;
            this.paramPaths = paramPaths;
        }
    }

    /**
     * 处理条件体文本中的 mgxsql 语法
     * - :param → #{param}，同时收集参数路径
     * - and/or 前缀提取（WHERE 域）
     * - in :list → &lt;foreach&gt;
     * - in (item:list)=&gt;$item.prop → &lt;foreach&gt;
     * - %:name% / :name% / %:name → &lt;bind&gt; + like
     * - #{param} 原样保留
     * - #[body] 和 #(expr)[body] 递归处理
     * - #condition 形式1 递归处理
     */
    private ProcessedBody processConditionBody(String text) {
        if (StringUtils.isBlank(text)) {
            return new ProcessedBody(text, new ArrayList<String>());
        }
        StringBuilder result = new StringBuilder();
        List<String> paramPaths = new ArrayList<String>();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);

            // 检测 # 开头语法
            if (c == '#') {
                int consumed = this.processConditionBodyHash(text, i, result, paramPaths);
                if (consumed > i) {
                    i = consumed;
                    continue;
                }
            }

            // 检测 in :list 或 in (item:list)=>$item.prop
            if (this.isKeywordAt(text, i, "in") && this.isWordBoundaryBefore(text, i) && this.isWordBoundaryAfter(text, i + 2)) {
                int inEnd = this.processConditionIn(text, i, result, paramPaths);
                if (inEnd > i) {
                    i = inEnd;
                    continue;
                }
            }

            // 检测 %:name （LIKE 左侧或双侧模糊）
            if (c == '%' && i + 1 < text.length() && text.charAt(i + 1) == ':') {
                int likeEnd = this.processConditionLike(text, i, result, paramPaths);
                if (likeEnd > i) {
                    i = likeEnd;
                    continue;
                }
            }

            // 检测 :name% （LIKE 右侧模糊）
            if (c == ':' && i + 1 < text.length() && this.isIdentifierStart(text.charAt(i + 1))) {
                int paramNameEnd = this.findIdentifierEnd(text, i + 1);
                String paramName = text.substring(i + 1, paramNameEnd);
                // 检测后面是否有 %
                if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
                    paramPaths.add(paramName);
                    String bindName = "_like_" + paramName.replace('.', '_');
                    String bindValue = paramName + " + '%'";
                    result.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
                    result.append("#{").append(bindName).append("}");
                    i = paramNameEnd + 1;
                    continue;
                }
                // 普通 :param → #{param}
                paramPaths.add(paramName);
                result.append("#{").append(paramName).append("}");
                i = paramNameEnd;
                continue;
            }

            // 检测 #{param}（兼容旧写法，原样保留）
            if (c == '#' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                int paramEnd = this.findBraceEnd(text, i + 2);
                if (paramEnd > i) {
                    result.append(text.substring(i, paramEnd + 1));
                    i = paramEnd + 1;
                    continue;
                }
            }

            // 检测 $variable（局部变量）→ #{variable}
            if (c == '$' && i + 1 < text.length() && this.isIdentifierStart(text.charAt(i + 1))) {
                int varNameEnd = this.findIdentifierEnd(text, i + 1);
                String varName = text.substring(i + 1, varNameEnd);
                result.append("#{").append(varName).append("}");
                i = varNameEnd;
                continue;
            }

            result.append(c);
            i++;
        }
        return new ProcessedBody(result.toString(), paramPaths);
    }

    /**
     * 处理条件体中的 # 开头语法，递归处理
     *
     * @return 消费到的位置（下一个待处理的字符索引），若无法识别返回原位置
     */
    private int processConditionBodyHash(String text, int start, StringBuilder result, List<String> paramPaths) {
        if (start + 1 >= text.length()) {
            return start;
        }
        char next = text.charAt(start + 1);

        // #[body] — 无 guard 条件体
        if (next == '[') {
            int pos = start + 2; // 跳过 #[
            int bracketDepth = 1;
            StringBuilder bodySb = new StringBuilder();
            while (pos < text.length() && bracketDepth > 0) {
                char bc = text.charAt(pos);
                if (bc == '[') {
                    bracketDepth++;
                } else if (bc == ']') {
                    bracketDepth--;
                    if (bracketDepth == 0) {
                        pos++; // 跳过 ]
                        break;
                    }
                } else if (bc == '\'') {
                    bodySb.append(bc);
                    pos++;
                    while (pos < text.length()) {
                        char sc = text.charAt(pos);
                        bodySb.append(sc);
                        pos++;
                        if (sc == '\'') {
                            if (pos < text.length() && text.charAt(pos) == '\'') {
                                bodySb.append('\'');
                                pos++;
                            } else {
                                break;
                            }
                        }
                    }
                    continue;
                }
                if (bracketDepth > 0) {
                    bodySb.append(bc);
                }
                pos++;
            }
            String bodyContent = bodySb.toString();

            // 递归处理 body
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();
            paramPaths.addAll(processed.paramPaths);

            result.append("<if test=\"");
            result.append(testExpression);
            result.append("\"> ");
            result.append(ifContent);
            result.append("</if>");

            return pos;
        }

        // #(expr)[body] — 有 guard 条件体
        if (next == '(') {
            int pos = start + 1; // 指向 (
            // 读取 () 内容
            StringBuilder guardSb = new StringBuilder();
            int depth = 0;
            pos++; // 跳过 (
            depth = 1;
            while (pos < text.length() && depth > 0) {
                char gc = text.charAt(pos);
                if (gc == '(') {
                    depth++;
                } else if (gc == ')') {
                    depth--;
                    if (depth == 0) {
                        pos++; // 跳过 )
                        break;
                    }
                } else if (gc == '\'') {
                    guardSb.append(gc);
                    pos++;
                    while (pos < text.length()) {
                        char sc = text.charAt(pos);
                        guardSb.append(sc);
                        pos++;
                        if (sc == '\'') {
                            if (pos < text.length() && text.charAt(pos) == '\'') {
                                guardSb.append('\'');
                                pos++;
                            } else {
                                break;
                            }
                        }
                    }
                    continue;
                }
                if (depth > 0) {
                    guardSb.append(gc);
                }
                pos++;
            }
            String guardContent = guardSb.toString().trim();

            // 跳过空白，检测 [
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos >= text.length() || text.charAt(pos) != '[') {
                throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '[]'，位置: %s，附近文本: %s",
                        String.valueOf(start), text.substring(Math.max(0, start - 5), Math.min(text.length(), start + 20)));
            }

            pos++; // 跳过 [
            int bracketDepth = 1;
            StringBuilder bodySb = new StringBuilder();
            while (pos < text.length() && bracketDepth > 0) {
                char bc = text.charAt(pos);
                if (bc == '[') {
                    bracketDepth++;
                } else if (bc == ']') {
                    bracketDepth--;
                    if (bracketDepth == 0) {
                        pos++; // 跳过 ]
                        break;
                    }
                } else if (bc == '\'') {
                    bodySb.append(bc);
                    pos++;
                    while (pos < text.length()) {
                        char sc = text.charAt(pos);
                        bodySb.append(sc);
                        pos++;
                        if (sc == '\'') {
                            if (pos < text.length() && text.charAt(pos) == '\'') {
                                bodySb.append('\'');
                                pos++;
                            } else {
                                break;
                            }
                        }
                    }
                    continue;
                }
                if (bracketDepth > 0) {
                    bodySb.append(bc);
                }
                pos++;
            }
            String bodyContent = bodySb.toString();

            // 递归处理
            if (StringUtils.isBlank(guardContent)) {
                ProcessedBody processed = this.processConditionBody(bodyContent);
                String testExpression = this.buildTestExpression(processed.paramPaths);
                String ifContent = processed.body.trim();
                paramPaths.addAll(processed.paramPaths);

                result.append("<if test=\"");
                result.append(testExpression);
                result.append("\"> ");
                result.append(ifContent);
                result.append("</if>");
            } else {
                String testExpr = this.stripParamColons(guardContent);
                ProcessedBody processed = this.processConditionBody(bodyContent);
                String ifContent = processed.body.trim();

                result.append("<if test=\"");
                result.append(testExpr);
                result.append("\"> ");
                result.append(ifContent);
                result.append("</if>");
            }

            return pos;
        }

        // #{param} — MyBatis参数引用，原样保留
        if (next == '{') {
            int paramEnd = this.findBraceEnd(text, start + 2);
            if (paramEnd > start) {
                result.append(text.substring(start, paramEnd + 1));
                return paramEnd + 1;
            }
            return start;
        }

        // #identifier — 形式1
        if (this.isIdentifierStart(next)) {
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
                // 括号和方括号外遇到独立的 and/or 截断
                if (form1ParenDepth == 0 && form1BracketDepth == 0) {
                    if (lineEnd + 3 <= text.length()
                            && text.substring(lineEnd, lineEnd + 3).equalsIgnoreCase("and")
                            && this.isWordBoundaryBefore(text, lineEnd)
                            && this.isWordBoundaryAfter(text, lineEnd + 3)) {
                        break;
                    }
                    if (lineEnd + 2 <= text.length()
                            && text.substring(lineEnd, lineEnd + 2).equalsIgnoreCase("or")
                            && this.isWordBoundaryBefore(text, lineEnd)
                            && this.isWordBoundaryAfter(text, lineEnd + 2)) {
                        break;
                    }
                }
                lineEnd++;
            }
            String condition = text.substring(start + 1, lineEnd).trim();
            ProcessedBody processed = this.processConditionBody(condition);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();
            paramPaths.addAll(processed.paramPaths);

            result.append("<if test=\"");
            result.append(testExpression);
            result.append("\"> ");
            result.append(ifContent);
            result.append("</if>");

            return lineEnd;
        }

        return start;
    }

    // ==================== 读取方法 ====================

    /**
     * 读取形式1条件内容（到行尾或独立的 and/or 关键字为止）
     */
    private String readForm1Content(S2Context ctx) {
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
            if (parenDepth == 0 && this.isKeywordAt(ctx, "and") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 3)) {
                break;
            }
            if (parenDepth == 0 && this.isKeywordAt(ctx, "or") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
                break;
            }
            content.append(c);
            ctx.advance();
        }
        return content.toString();
    }

    /**
     * 读取 [] 内内容，支持嵌套深度计数，返回 body 原文
     * 调用前已跳过 [，ctx.currentChar() 指向 body 第一个字符
     * @param requireClose true=到末尾未闭合时报语法错误，false=未闭合则返回已读内容
     */
    private String readBracketedContent(S2Context ctx, boolean requireClose) {
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
                    ctx.advance(); // 跳过匹配的 ]
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

    /**
     * 读取 [] 内内容（要求闭合），便捷方法
     */
    private String readBracketedContent(S2Context ctx) {
        return this.readBracketedContent(ctx, true);
    }

    /**
     * 读取括号内的内容（已到达第一个 '(' 位置）
     */
    private String readParenthesizedContent(S2Context ctx) {
        if (!ctx.hasMore() || ctx.currentChar() != '(') {
            return "";
        }
        ctx.advance(); // 跳过 (
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

    private void processInClause(S2Context ctx) {
        ctx.setPosition(ctx.getPosition() + 2); // 跳过 "in"
        this.skipWhitespace(ctx);

        if (!ctx.hasMore()) {
            ctx.appendOutput("in");
            return;
        }

        // 简单类型 IN：:list
        if (ctx.currentChar() == ':' && ctx.peekChar(1) != ':' && this.isIdentifierStartAt(ctx, 1)) {
            String collectionName = this.readColonParamRef(ctx);
            if (collectionName != null) {
                ctx.appendOutput("in <foreach item=\"item\" collection=\"");
                ctx.appendOutput(collectionName);
                ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return;
            }
        }
        // 兼容旧写法：#{list}
        if (ctx.currentChar() == '#' && ctx.peekChar(1) == '{') {
            String collectionName = this.readParamRef(ctx);
            if (collectionName != null) {
                ctx.appendOutput("in <foreach item=\"item\" collection=\"");
                ctx.appendOutput(collectionName);
                ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return;
            }
        } else if (ctx.currentChar() == '(') {
            // 复杂类型 IN：(item:collection)=>$item.prop
            ctx.advance(); // 跳过 (
            String itemName = this.readIdentifier(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ':') {
                ctx.advance(); // 跳过 :
                String collectionName = this.readIdentifier(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance(); // 跳过 )
                    if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
                        ctx.advance(); // 跳过 =
                        ctx.advance(); // 跳过 >
                        String valueExpr = this.readDollarVarRef(ctx);
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
                        valueExpr = this.readParamRefFull(ctx);
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
                    }
                }
            }
        }
        ctx.appendOutput("in");
    }

    // ==================== LIKE 模式处理（WHERE 域直接扫描） ====================

    private void processLikePattern(S2Context ctx) {
        ctx.advance(); // 跳过 %
        String paramName = this.readColonParamRef(ctx);
        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;

        if (ctx.hasMore() && ctx.currentChar() == '%') {
            likeType = S2Condition.LikeType.BOTH;
            ctx.advance();
        }

        if (paramName != null) {
            String bindName = "_like_" + paramName.replace('.', '_');
            String bindValue = this.buildLikeBindValue(paramName, likeType);
            ctx.appendOutput("<bind name=\"");
            ctx.appendOutput(bindName);
            ctx.appendOutput("\" value=\"");
            ctx.appendOutput(bindValue);
            ctx.appendOutput("\"/>");
        }
    }

    // ==================== 参数引用处理（:param → #{param}） ====================

    private boolean isParamRefStart(S2Context ctx) {
        if (ctx.peekChar(1) == ':') {
            return false;
        }
        char next = ctx.peekChar(1);
        return this.isIdentifierStartChar(next);
    }

    private boolean isIdentifierStartChar(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentifierStartAt(S2Context ctx, int offset) {
        char c = ctx.peekChar(offset);
        return this.isIdentifierStartChar(c);
    }

    private void processParamRef(S2Context ctx) {
        String paramName = this.readColonParamRef(ctx);
        if (paramName != null) {
            ctx.appendOutput("#{");
            ctx.appendOutput(paramName);
            ctx.appendOutput("}");
        }
    }

    private String readColonParamRef(S2Context ctx) {
        if (ctx.currentChar() != ':') {
            return null;
        }
        ctx.advance(); // 跳过 :
        if (!ctx.hasMore() || !this.isIdentifierStartChar(ctx.currentChar())) {
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

    // ==================== 表达式中 :param 去冒号 ====================

    private String stripParamColons(String expr) {
        if (StringUtils.isBlank(expr)) {
            return expr;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (c == ':' && i + 1 < expr.length() && this.isIdentifierStartChar(expr.charAt(i + 1))) {
                if (i + 1 < expr.length() && expr.charAt(i + 1) == ':') {
                    result.append(c);
                    i++;
                    continue;
                }
                i++; // 跳过 :
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_' || expr.charAt(i) == '.')) {
                    result.append(expr.charAt(i));
                    i++;
                }
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    // ==================== 子句关键字检测 ====================

    /**
     * 检测当前位置是否匹配子句关键字（用于 WHERE 域提前关闭）
     * 支持 "order by"、"group by" 等多 token 关键字
     */
    private boolean isClauseKeywordAt(S2Context ctx) {
        for (String keyword : CLAUSE_KEYWORDS) {
            if (this.matchesClauseKeyword(ctx, keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当前位置是否匹配指定的子句关键字
     * 多 token 关键字（如 "order by"）中间可有空白/换行
     */
    private boolean matchesClauseKeyword(S2Context ctx, String keyword) {
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

    // ==================== 辅助方法 ====================

    private String readParamRef(S2Context ctx) {
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

    private String readParamRefFull(S2Context ctx) {
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

    private String readDollarVarRef(S2Context ctx) {
        if (ctx.currentChar() != '$') {
            return null;
        }
        ctx.advance();
        if (!ctx.hasMore() || !this.isIdentifierStartChar(ctx.currentChar())) {
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

    private String readIdentifier(S2Context ctx) {
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

    private int findIdentifierEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        return pos;
    }

    private int findBraceEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && text.charAt(pos) != '}') {
            pos++;
        }
        return pos < text.length() ? pos : -1;
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isKeywordAt(S2Context ctx, String keyword) {
        int pos = ctx.getPosition();
        int len = keyword.length();
        if (pos + len > ctx.getInputLength()) {
            return false;
        }
        String sub = ctx.substring(pos, pos + len);
        return sub.equalsIgnoreCase(keyword);
    }

    private boolean isKeywordAt(String text, int pos, String keyword) {
        if (pos + keyword.length() > text.length()) {
            return false;
        }
        return text.substring(pos, pos + keyword.length()).equalsIgnoreCase(keyword);
    }

    private boolean isWordBoundaryBefore(S2Context ctx) {
        int pos = ctx.getPosition();
        if (pos == 0) {
            return true;
        }
        char prev = ctx.charAt(pos - 1);
        return !Character.isLetterOrDigit(prev) && prev != '_' && prev != '.';
    }

    private boolean isWordBoundaryBefore(String text, int pos) {
        if (pos == 0) {
            return true;
        }
        char prev = text.charAt(pos - 1);
        return !Character.isLetterOrDigit(prev) && prev != '_' && prev != '.';
    }

    private boolean isWordBoundaryAfter(S2Context ctx, int keywordLength) {
        int afterPos = ctx.getPosition() + keywordLength;
        if (afterPos >= ctx.getInputLength()) {
            return true;
        }
        char next = ctx.charAt(afterPos);
        return !Character.isLetterOrDigit(next) && next != '_' && next != '.';
    }

    private boolean isWordBoundaryAfter(String text, int pos) {
        if (pos >= text.length()) {
            return true;
        }
        char next = text.charAt(pos);
        return !Character.isLetterOrDigit(next) && next != '_' && next != '.';
    }

    private void skipWhitespace(S2Context ctx) {
        while (ctx.hasMore() && Character.isWhitespace(ctx.currentChar())) {
            ctx.advance();
        }
    }

    private int findXmlTagEnd(S2Context ctx, int start) {
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
                } else if (!this.isClosingTagAt(ctx, pos)) {
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

    private boolean isClosingTagAt(S2Context ctx, int pos) {
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

    private String buildTestExpression(List<String> paramPaths) {
        if (paramPaths == null || paramPaths.isEmpty()) {
            return "true";
        }
        List<String> testParts = new ArrayList<String>();
        for (String paramPath : paramPaths) {
            String[] parts = paramPath.split("\\.");
            List<String> currentPath = new ArrayList<String>();
            for (int i = 0; i < parts.length; i++) {
                currentPath.add(parts[i]);
                String path = StringUtils.join(currentPath, ".");
                testParts.add(String.format(IS_NOT_EMPTY, path));
            }
        }
        return StringUtils.join(testParts, " and ");
    }

    private String buildLikeBindValue(String paramName, S2Condition.LikeType likeType) {
        switch (likeType) {
            case BOTH:
                return "'%' + " + paramName + " + '%'";
            case RIGHT:
                return paramName + " + '%'";
            case LEFT:
                return "'%' + " + paramName;
            default:
                return paramName;
        }
    }

    private void closeOpenScopes(S2Context ctx) {
        S2State state = ctx.getState();
        if (state == S2State.WHERE) {
            ctx.appendOutput("</where>");
        } else if (state == S2State.WHERE_BOUNDED) {
            // where[body] 中 ] 未闭合，语法错误
            throw new MybatisgxException("mgxsql 语法错误: 'where[' 未闭合，缺少匹配的 ']'，%s",
                    ctx.getPositionInfo());
        } else if (state == S2State.SET) {
            ctx.appendOutput("</set>");
        } else if (state == S2State.SET_BOUNDED) {
            throw new MybatisgxException("mgxsql 语法错误: 'set[' 未闭合，缺少匹配的 ']'，%s",
                    ctx.getPositionInfo());
        }
    }

    // ==================== 条件体内部 IN / LIKE 处理 ====================

    private int processConditionIn(String text, int start, StringBuilder result, List<String> paramPaths) {
        int pos = start + 2;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return start;
        }

        // 简单类型 IN：:list
        if (text.charAt(pos) == ':' && pos + 1 < text.length() && this.isIdentifierStart(text.charAt(pos + 1))) {
            int nameEnd = this.findIdentifierEnd(text, pos + 1);
            String collectionName = text.substring(pos + 1, nameEnd);
            paramPaths.add(collectionName);
            result.append("in <foreach item=\"item\" collection=\"").append(collectionName).append("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
            return nameEnd;
        }

        // 复杂类型 IN：(item:collection)=>$item.prop
        if (text.charAt(pos) == '(') {
            pos++;
            int itemNameStart = pos;
            while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
                pos++;
            }
            String itemName = text.substring(itemNameStart, pos);
            if (pos < text.length() && text.charAt(pos) == ':') {
                pos++;
                int collStart = pos;
                while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
                    pos++;
                }
                String collectionName = text.substring(collStart, pos);
                if (pos < text.length() && text.charAt(pos) == ')') {
                    pos++;
                    if (pos < text.length() && text.charAt(pos) == '=' && pos + 1 < text.length() && text.charAt(pos + 1) == '>') {
                        pos += 2;
                        if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && this.isIdentifierStart(text.charAt(pos + 1))) {
                            int varEnd = this.findIdentifierEnd(text, pos + 1);
                            String varName = text.substring(pos + 1, varEnd);
                            paramPaths.add(collectionName);
                            result.append("in <foreach item=\"").append(itemName).append("\" collection=\"").append(collectionName).append("\" open=\"(\" close=\")\" separator=\",\">#{").append(varName).append("}</foreach>");
                            return varEnd;
                        } else if (pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                            int valueEnd = this.findBraceEnd(text, pos + 2);
                            if (valueEnd > pos) {
                                String valueExpr = text.substring(pos, valueEnd + 1);
                                paramPaths.add(collectionName);
                                result.append("in <foreach item=\"").append(itemName).append("\" collection=\"").append(collectionName).append("\" open=\"(\" close=\")\" separator=\",\">").append(valueExpr).append("</foreach>");
                                return valueEnd + 1;
                            }
                        }
                    }
                }
            }
        }

        return start;
    }

    private int processConditionLike(String text, int start, StringBuilder result, List<String> paramPaths) {
        int pos = start + 1;
        if (pos >= text.length() || text.charAt(pos) != ':' || pos + 1 >= text.length() || !this.isIdentifierStart(text.charAt(pos + 1))) {
            return start;
        }
        int paramNameEnd = this.findIdentifierEnd(text, pos + 1);
        String paramName = text.substring(pos + 1, paramNameEnd);
        paramPaths.add(paramName);

        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;
        if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
            likeType = S2Condition.LikeType.BOTH;
            paramNameEnd++;
        }

        String bindName = "_like_" + paramName.replace('.', '_');
        String bindValue = this.buildLikeBindValue(paramName, likeType);
        result.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
        result.append("#{").append(bindName).append("}");

        return paramNameEnd;
    }
}
