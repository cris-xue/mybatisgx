package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.S2Condition;
import com.mybatisgx.dsl.mgxsql.model.S2Context;
import com.mybatisgx.dsl.mgxsql.model.S2State;
import com.mybatisgx.exception.MybatisgxException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 逐字符状态机扫描器（v3），将 mgxsql 简化语法文本转换为标准 MyBatis XML 文本
 * <p>
 * 语法规则（v3）：
 * <ul>
 *   <li>where → &lt;where&gt;...&lt;/where&gt;</li>
 *   <li>set → &lt;set&gt;...&lt;/set&gt;</li>
 *   <li>#condition → &lt;if test="参数非空"&gt; condition&lt;/if&gt;（形式1，单条件简写，读到行尾）</li>
 *   <li>#(){body} → &lt;if test="参数非空"&gt; body&lt;/if&gt;（形式2，空guard，隐式推导）</li>
 *   <li>#(expr){body} → &lt;if test="expr"&gt; body&lt;/if&gt;（形式2，显式guard）</li>
 *   <li>:param → #{param}（参数绑定转换）</li>
 *   <li>#{param} → 原样保留（MyBatis参数引用，不是条件语法）</li>
 *   <li>$variable → #{variable}（局部变量，foreach 迭代上下文）</li>
 *   <li>in :list → &lt;foreach&gt;</li>
 *   <li>in (item:list)=&gt;$item.prop → &lt;foreach&gt;</li>
 *   <li>%:name% / :name% / %:name → &lt;bind&gt; + like</li>
 *   <li>XML 标签原样透传</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlScanner {

    private static final String IS_NOT_EMPTY = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%s)";

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
                case SET:
                    this.processSet(ctx);
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
            ctx.appendOutput("<where>");
            ctx.setPosition(ctx.getPosition() + 5);
            ctx.setState(S2State.WHERE);
            return;
        }
        // 检测 set 关键字（仅在 update 上下文）
        if (this.isKeywordAt(ctx, "set")) {
            ctx.appendOutput("<set>");
            ctx.setPosition(ctx.getPosition() + 3);
            ctx.setState(S2State.SET);
            return;
        }
        // 默认原样输出
        ctx.appendOutput(c);
        ctx.advance();
    }

    // ==================== WHERE 域 ====================

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
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            char next = ctx.peekChar(1);
            if (next == '(') {
                // 形式2：#(guard?){body}
                this.processConditionNodeV3(ctx);
                return;
            }
            if (next == '{') {
                // #{param} 原样输出（MyBatis参数引用）
                ctx.appendOutput(c);
                ctx.advance();
                return;
            }
            if (this.isIdentifierStartChar(next)) {
                // 形式1：#condition（单条件简写，读到行尾）
                this.processForm1Condition(ctx);
                return;
            }
            // # 后跟其他字符，语法错误
            throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '('、'{' 或标识符，位置: %s，附近文本: %s",
                    String.valueOf(ctx.getPosition()), this.nearbyText(ctx, 10));
        }
        // 检测 in 关键字（必须在 WHERE 域）
        if (this.isKeywordAt(ctx, "in") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        // 检测 %:name （LIKE 模式 - 左侧或双侧模糊）
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

    // ==================== SET 域 ====================

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
            ctx.appendOutput("<where>");
            ctx.setPosition(ctx.getPosition() + 5);
            ctx.setState(S2State.WHERE);
            return;
        }
        // 检测 # 条件节点或 MyBatis #{param}
        if (c == '#') {
            char next = ctx.peekChar(1);
            if (next == '(') {
                // 形式2：#(guard?){body}
                this.processSetConditionNodeV3(ctx);
                return;
            }
            if (next == '{') {
                // #{param} 原样输出（MyBatis参数引用）
                ctx.appendOutput(c);
                ctx.advance();
                return;
            }
            if (this.isIdentifierStartChar(next)) {
                // 形式1：#condition（单条件简写，读到行尾）
                this.processForm1ConditionSet(ctx);
                return;
            }
            // # 后跟其他字符，语法错误
            throw new MybatisgxException("mgxsql 语法错误: '#' 后必须跟 '('、'{' 或标识符，位置: %s，附近文本: %s",
                    String.valueOf(ctx.getPosition()), this.nearbyText(ctx, 10));
        }
        // 检测 :param 参数绑定
        if (c == ':' && this.isParamRefStart(ctx)) {
            this.processParamRef(ctx);
            return;
        }
        // 逗号处理（SET 域的逗号保留，<set> 自动去除尾部逗号）
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
        // 找到标签结束 > 并原样输出整个标签
        int start = ctx.getPosition();
        int end = this.findXmlTagEnd(ctx, start);
        if (end == -1) {
            // 没找到结束，原样输出剩余
            ctx.appendOutput(ctx.substring(start, ctx.getInputLength()));
            ctx.setPosition(ctx.getInputLength());
        } else {
            // 原样输出标签（包含 >）
            String tagContent = ctx.substring(start, end + 1);
            ctx.appendOutput(tagContent);
            ctx.setPosition(end + 1);
        }
        ctx.popState();
    }

    // ==================== 形式1：#condition 单条件简写（WHERE 域） ====================

    /**
     * 处理形式1条件：#condition
     * 读取到行尾或遇到独立的 and/or 关键字（不在括号内）为止
     */
    private void processForm1Condition(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取条件内容（到行尾或独立的 and/or 关键字为止）
        String condition = this.readForm1Content(ctx);
        // 处理条件体内部的 mgxsql 语法
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

    /**
     * 处理形式1条件：#condition（SET 域，不处理 and/or 前缀）
     */
    private void processForm1ConditionSet(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取条件内容（到行尾或独立的 and/or 关键字为止）
        String condition = this.readForm1Content(ctx);
        // 处理条件体内部的 mgxsql 语法
        ProcessedBody processed = this.processConditionBody(condition);
        String testExpression = this.buildTestExpression(processed.paramPaths);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\"> ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== 形式2：#(guard?){body} 条件节点（WHERE 域） ====================

    /**
     * 处理形式2条件节点：#(guard?){body}（v3）
     * 读 () → 检测 { → 无 { 报语法错误 → 有 { 读 body → 根据 guard 是否为空选择隐式/显式 guard
     */
    private void processConditionNodeV3(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取第一个 (...) 内容
        String guardContent = this.readParenthesizedContent(ctx);

        // 检测后面是否有 {
        this.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '{') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '{}'，位置: %s，附近文本: %s",
                    String.valueOf(ctx.getPosition()), this.nearbyText(ctx, 10));
        }

        // 读取 {} 内内容
        String bodyContent = this.readBracedContent(ctx);

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

    // ==================== 形式2：#(guard?){body} 条件节点（SET 域） ====================

    /**
     * 处理 SET 域的形式2条件节点：#(guard?){body}（v3）
     * 与 WHERE 域相同逻辑，但不处理 and/or 前缀
     */
    private void processSetConditionNodeV3(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取第一个 (...) 内容
        String guardContent = this.readParenthesizedContent(ctx);

        // 检测后面是否有 {
        this.skipWhitespace(ctx);
        if (!ctx.hasMore() || ctx.currentChar() != '{') {
            throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '{}'，位置: %s，附近文本: %s",
                    String.valueOf(ctx.getPosition()), this.nearbyText(ctx, 10));
        }

        // 读取 {} 内内容
        String bodyContent = this.readBracedContent(ctx);

        if (StringUtils.isBlank(guardContent)) {
            // 空 guard → 隐式推导
            ProcessedBody processed = this.processConditionBody(bodyContent);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpression);
            ctx.appendOutput("\"> ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        } else {
            // 显式 guard → 去冒号
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

    // ==================== 条件体处理 ====================

    /**
     * 条件体处理结果
     */
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
     * - in :list → <foreach>
     * - in (item:list)=>$item.prop → <foreach>
     * - %:name% / :name% / %:name → <bind> + like
     * - #{param} 原样保留
     * - #condition 和 #(guard?){body} 递归处理（v3新增）
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

            // 检测 #condition 或 #(guard?){body}（v3新增，递归处理）
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
                    // :name% 右侧模糊
                    paramPaths.add(paramName);
                    String bindName = "_like_" + paramName.replace('.', '_');
                    String bindValue = paramName + " + '%'";
                    result.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
                    result.append("#{").append(bindName).append("}");
                    i = paramNameEnd + 1; // 跳过 name%
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
                    // 原样输出 #{param}
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
     * 处理条件体中的 # 开头语法（形式1或形式2），递归处理
     *
     * @return 消费到的位置（下一个待处理的字符索引），若无法识别返回原位置
     */
    private int processConditionBodyHash(String text, int start, StringBuilder result, List<String> paramPaths) {
        if (start + 1 >= text.length()) {
            return start;
        }
        char next = text.charAt(start + 1);

        // #(guard?){body} — 形式2
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
                    // 字符串字面量
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

            // 跳过空白，检测 {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            if (pos >= text.length() || text.charAt(pos) != '{') {
                // #() 后无 {}，语法错误
                throw new MybatisgxException("mgxsql 语法错误: #() 后必须跟 '{}'，位置: %s，附近文本: %s",
                        String.valueOf(start), text.substring(Math.max(0, start - 5), Math.min(text.length(), start + 20)));
            }

            // 读取 {} 内容
            pos++; // 跳过 {
            int braceDepth = 1;
            StringBuilder bodySb = new StringBuilder();
            while (pos < text.length() && braceDepth > 0) {
                char bc = text.charAt(pos);
                if (bc == '{') {
                    braceDepth++;
                } else if (bc == '}') {
                    braceDepth--;
                    if (braceDepth == 0) {
                        pos++; // 跳过 }
                        break;
                    }
                } else if (bc == '\'') {
                    // 字符串字面量
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
                if (braceDepth > 0) {
                    bodySb.append(bc);
                }
                pos++;
            }
            String bodyContent = bodySb.toString();

            // 递归处理 body
            if (StringUtils.isBlank(guardContent)) {
                // 空 guard → 隐式推导
                ProcessedBody processed = this.processConditionBody(bodyContent);
                String testExpression = this.buildTestExpression(processed.paramPaths);
                String ifContent = processed.body.trim();
                // 收集参数路径
                paramPaths.addAll(processed.paramPaths);

                result.append("<if test=\"");
                result.append(testExpression);
                result.append("\"> ");
                result.append(ifContent);
                result.append("</if>");
            } else {
                // 显式 guard → 去冒号
                String testExpr = this.stripParamColons(guardContent);
                ProcessedBody processed = this.processConditionBody(bodyContent);
                String ifContent = processed.body.trim();
                // 收集参数路径（显式guard不从body提取，但仍需收集用于test推导以外的用途）
                // 注意：显式guard时，body中的param仍需收集用于可能的嵌套条件
                // 但最外层guard由用户显式指定，不依赖paramPaths
                // 嵌套条件的paramPaths已经在递归的processConditionBody中处理了

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
            // 读取到行尾或独立的 and/or 关键字为止（括号内忽略）
            int lineEnd = start + 1;
            int form1ParenDepth = 0;
            while (lineEnd < text.length() && text.charAt(lineEnd) != '\n' && text.charAt(lineEnd) != '\r') {
                char fc = text.charAt(lineEnd);
                if (fc == '(') {
                    form1ParenDepth++;
                } else if (fc == ')') {
                    form1ParenDepth--;
                }
                // 括号外遇到独立的 and/or 截断
                if (form1ParenDepth == 0 && lineEnd + 3 <= text.length()
                        && text.substring(lineEnd, lineEnd + 3).equalsIgnoreCase("and")
                        && this.isWordBoundaryBefore(text, lineEnd)
                        && this.isWordBoundaryAfter(text, lineEnd + 3)) {
                    break;
                }
                if (form1ParenDepth == 0 && lineEnd + 2 <= text.length()
                        && text.substring(lineEnd, lineEnd + 2).equalsIgnoreCase("or")
                        && this.isWordBoundaryBefore(text, lineEnd)
                        && this.isWordBoundaryAfter(text, lineEnd + 2)) {
                    break;
                }
                lineEnd++;
            }
            String condition = text.substring(start + 1, lineEnd).trim();
            // 处理条件体
            ProcessedBody processed = this.processConditionBody(condition);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();
            // 收集参数路径
            paramPaths.addAll(processed.paramPaths);

            result.append("<if test=\"");
            result.append(testExpression);
            result.append("\"> ");
            result.append(ifContent);
            result.append("</if>");

            return lineEnd;
        }

        // 无法识别
        return start;
    }

    // ==================== 读取方法 ====================

    /**
     * 读取形式1条件内容（到行尾或独立的 and/or 关键字为止，括号内的 and/or 忽略）
     */
    private String readForm1Content(S2Context ctx) {
        StringBuilder content = new StringBuilder();
        int parenDepth = 0;
        while (ctx.hasMore()) {
            char c = ctx.currentChar();
            if (c == '\n' || c == '\r') {
                // 行尾停止
                ctx.advance();
                break;
            }
            if (c == '(') {
                parenDepth++;
            } else if (c == ')') {
                parenDepth--;
            }
            // 只在括号外检测独立的 and/or 关键字
            if (parenDepth == 0 && this.isKeywordAt(ctx, "and") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 3)) {
                // 遇到独立的 and，截断
                break;
            }
            if (parenDepth == 0 && this.isKeywordAt(ctx, "or") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
                // 遇到独立的 or，截断
                break;
            }
            content.append(c);
            ctx.advance();
        }
        return content.toString();
    }

    /**
     * 读取 {} 内内容，支持嵌套深度计数，返回 body 原文
     * 调用时 ctx.currentChar() == '{'
     */
    private String readBracedContent(S2Context ctx) {
        if (!ctx.hasMore() || ctx.currentChar() != '{') {
            return "";
        }
        ctx.advance(); // 跳过 {
        int depth = 1;
        StringBuilder content = new StringBuilder();
        while (ctx.hasMore() && depth > 0) {
            char c = ctx.currentChar();
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    ctx.advance();
                    break;
                }
            } else if (c == '\'') {
                // 字符串字面量
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
                // 字符串字面量
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
        // 跳过 "in"
        ctx.setPosition(ctx.getPosition() + 2);
        // 跳过空白
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
                        // 读取 $item.prop
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
                        // 兼容旧写法 #{item.prop}
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
        // 无法识别，原样输出 in
        ctx.appendOutput("in");
    }

    // ==================== LIKE 模式处理（WHERE 域直接扫描） ====================

    private void processLikePattern(S2Context ctx) {
        // 已检测到 %:name 或 %:name%
        ctx.advance(); // 跳过 %
        String paramName = this.readColonParamRef(ctx);
        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;

        if (ctx.hasMore() && ctx.currentChar() == '%') {
            likeType = S2Condition.LikeType.BOTH; // %:name%
            ctx.advance(); // 跳过 %
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

    /**
     * 检测当前位置是否是 :param 的起始（: 后跟标识符起始字符，且不是 ::）
     */
    private boolean isParamRefStart(S2Context ctx) {
        int pos = ctx.getPosition();
        // 不是 ::
        if (ctx.peekChar(1) == ':') {
            return false;
        }
        // : 后跟标识符起始字符
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

    /**
     * 处理 :param → #{param}
     */
    private void processParamRef(S2Context ctx) {
        String paramName = this.readColonParamRef(ctx);
        if (paramName != null) {
            ctx.appendOutput("#{");
            ctx.appendOutput(paramName);
            ctx.appendOutput("}");
        }
    }

    /**
     * 读取 :param 中的参数名
     */
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

    /**
     * 将表达式中的 :param 去冒号，例如 :age → age
     */
    private String stripParamColons(String expr) {
        if (StringUtils.isBlank(expr)) {
            return expr;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (c == ':' && i + 1 < expr.length() && this.isIdentifierStartChar(expr.charAt(i + 1))) {
                // 检测不是 ::
                if (i + 1 < expr.length() && expr.charAt(i + 1) == ':') {
                    result.append(c);
                    i++;
                    continue;
                }
                // 跳过 :，输出标识符
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

    // ==================== 辅助方法 ====================

    /**
     * 读取 #{param} 中的参数名，返回参数路径
     */
    private String readParamRef(S2Context ctx) {
        if (ctx.currentChar() != '#' || ctx.peekChar(1) != '{') {
            return null;
        }
        ctx.advance(); // 跳过 #
        ctx.advance(); // 跳过 {
        StringBuilder paramPath = new StringBuilder();
        while (ctx.hasMore() && ctx.currentChar() != '}') {
            paramPath.append(ctx.currentChar());
            ctx.advance();
        }
        if (ctx.hasMore()) {
            ctx.advance(); // 跳过 }
        }
        return paramPath.toString();
    }

    /**
     * 读取完整的 #{param.path} 表达式（包含 #{} ）
     */
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

    /**
     * 读取 $variable 引用，返回 #{variable} 形式
     */
    private String readDollarVarRef(S2Context ctx) {
        if (ctx.currentChar() != '$') {
            return null;
        }
        ctx.advance(); // 跳过 $
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

    /**
     * 读取标识符（字母、数字、下划线、点号）
     */
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

    /**
     * 在文本中查找标识符结束位置
     */
    private int findIdentifierEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        return pos;
    }

    /**
     * 在文本中查找 } 结束位置
     */
    private int findBraceEnd(String text, int start) {
        int pos = start;
        while (pos < text.length() && text.charAt(pos) != '}') {
            pos++;
        }
        return pos < text.length() ? pos : -1;
    }

    /**
     * 检测字符是否为标识符起始字符
     */
    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    /**
     * 检测指定位置是否是关键字
     */
    private boolean isKeywordAt(S2Context ctx, String keyword) {
        int pos = ctx.getPosition();
        int len = keyword.length();
        if (pos + len > ctx.getInputLength()) {
            return false;
        }
        String sub = ctx.substring(pos, pos + len);
        return sub.equalsIgnoreCase(keyword);
    }

    /**
     * 在文本中检测指定位置是否是关键字
     */
    private boolean isKeywordAt(String text, int pos, String keyword) {
        if (pos + keyword.length() > text.length()) {
            return false;
        }
        return text.substring(pos, pos + keyword.length()).equalsIgnoreCase(keyword);
    }

    /**
     * 检测关键字前是否有单词边界（空格、换行、行首）
     */
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

    /**
     * 检测关键字后是否有单词边界
     */
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

    /**
     * 获取当前位置附近的文本片段（用于错误提示）
     */
    private String nearbyText(S2Context ctx, int radius) {
        int pos = ctx.getPosition();
        int start = Math.max(0, pos - radius);
        int end = Math.min(ctx.getInputLength(), pos + radius + 1);
        return "..." + ctx.substring(start, end) + "...";
    }

    /**
     * 跳过空白字符
     */
    private void skipWhitespace(S2Context ctx) {
        while (ctx.hasMore() && Character.isWhitespace(ctx.currentChar())) {
            ctx.advance();
        }
    }

    /**
     * 查找 XML 标签结束位置
     */
    private int findXmlTagEnd(S2Context ctx, int start) {
        int depth = 0;
        int pos = start;
        while (pos < ctx.getInputLength()) {
            char c = ctx.charAt(pos);
            if (c == '\'') {
                // 跳过属性值中的字符串
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
                // 检查是否是闭合标签
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

    /**
     * 检测指定位置是否是自闭合标签（如 <if ... />）
     */
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

    /**
     * 构建 <if> 的 test 表达式（隐式条件）
     */
    private String buildTestExpression(List<String> paramPaths) {
        if (paramPaths == null || paramPaths.isEmpty()) {
            return "true";
        }
        List<String> testParts = new ArrayList<String>();
        for (String paramPath : paramPaths) {
            // 对嵌套路径（如 entity.name），逐级生成 isNotEmpty
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

    /**
     * 构建 LIKE 的 <bind> value 表达式
     */
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

    /**
     * 关闭未关闭的域
     */
    private void closeOpenScopes(S2Context ctx) {
        if (ctx.getState() == S2State.WHERE) {
            ctx.appendOutput("</where>");
        } else if (ctx.getState() == S2State.SET) {
            ctx.appendOutput("</set>");
        }
    }

    // ==================== 条件体内部 IN / LIKE 处理 ====================

    /**
     * 处理条件体中的 IN 子句
     */
    private int processConditionIn(String text, int start, StringBuilder result, List<String> paramPaths) {
        int pos = start + 2; // 跳过 "in"
        // 跳过空白
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
            pos++; // 跳过 (
            int itemNameStart = pos;
            while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
                pos++;
            }
            String itemName = text.substring(itemNameStart, pos);
            if (pos < text.length() && text.charAt(pos) == ':') {
                pos++; // 跳过 :
                int collStart = pos;
                while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
                    pos++;
                }
                String collectionName = text.substring(collStart, pos);
                if (pos < text.length() && text.charAt(pos) == ')') {
                    pos++; // 跳过 )
                    if (pos < text.length() && text.charAt(pos) == '=' && pos + 1 < text.length() && text.charAt(pos + 1) == '>') {
                        pos += 2; // 跳过 =>
                        // 读取 $item.prop 或 #{item.prop}
                        if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && this.isIdentifierStart(text.charAt(pos + 1))) {
                            // $variable 局部变量
                            int varEnd = this.findIdentifierEnd(text, pos + 1);
                            String varName = text.substring(pos + 1, varEnd);
                            paramPaths.add(collectionName);
                            result.append("in <foreach item=\"").append(itemName).append("\" collection=\"").append(collectionName).append("\" open=\"(\" close=\")\" separator=\",\">#{").append(varName).append("}</foreach>");
                            return varEnd;
                        } else if (pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                            // #{item.prop} 兼容旧写法
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

        return start; // 无法识别
    }

    /**
     * 处理条件体中的 LIKE 模式（%:name% 或 %:name）
     */
    private int processConditionLike(String text, int start, StringBuilder result, List<String> paramPaths) {
        int pos = start + 1; // 跳过 %
        if (pos >= text.length() || text.charAt(pos) != ':' || pos + 1 >= text.length() || !this.isIdentifierStart(text.charAt(pos + 1))) {
            return start;
        }
        // 读取 :name
        int paramNameEnd = this.findIdentifierEnd(text, pos + 1);
        String paramName = text.substring(pos + 1, paramNameEnd);
        paramPaths.add(paramName);

        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;
        if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
            likeType = S2Condition.LikeType.BOTH; // %:name%
            paramNameEnd++; // 跳过 %
        }

        String bindName = "_like_" + paramName.replace('.', '_');
        String bindValue = this.buildLikeBindValue(paramName, likeType);
        result.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
        result.append("#{").append(bindName).append("}");

        return paramNameEnd;
    }
}
