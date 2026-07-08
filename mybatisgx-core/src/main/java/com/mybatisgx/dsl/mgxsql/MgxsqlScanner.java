package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.S2Condition;
import com.mybatisgx.dsl.mgxsql.model.S2Context;
import com.mybatisgx.dsl.mgxsql.model.S2State;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 逐字符状态机扫描器（v2），将 mgxsql 简化语法文本转换为标准 MyBatis XML 文本
 * <p>
 * 语法规则（v2）：
 * <ul>
 *   <li>where → &lt;where&gt;...&lt;/where&gt;</li>
 *   <li>set → &lt;set&gt;...&lt;/set&gt;</li>
 *   <li>#(sql) → &lt;if test="参数非空"&gt; sql&lt;/if&gt;</li>
 *   <li>#(expr)(sql) → &lt;if test="expr"&gt; sql&lt;/if&gt;</li>
 *   <li>#(and/or sql) → &lt;if&gt; and/or sql&lt;/if&gt;</li>
 *   <li>:param → #{param}（参数绑定转换）</li>
 *   <li>#{param} → 原样保留（兼容旧写法）</li>
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
                this.processConditionNode(ctx);
                return;
            }
            if (next == '{') {
                // #{param} 原样输出（兼容旧写法）
                ctx.appendOutput(c);
                ctx.advance();
                return;
            }
            // # 后跟其他字符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
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
                this.processSetConditionNode(ctx);
                return;
            }
            if (next == '{') {
                // #{param} 原样输出（兼容旧写法）
                ctx.appendOutput(c);
                ctx.advance();
                return;
            }
            // # 后跟其他字符，原样输出
            ctx.appendOutput(c);
            ctx.advance();
            return;
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

    // ==================== 条件节点处理（WHERE 域） ====================

    /**
     * 处理 #(sql) 或 #(expr)(sql) 条件节点
     */
    private void processConditionNode(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取第一个 (...) 内容
        String firstParen = this.readParenthesizedContent(ctx);

        // 判定是隐式还是显式条件
        if (ctx.hasMore() && ctx.currentChar() == '(') {
            // 显式条件：#(expr)(sql)
            String expr = firstParen;
            String sql = this.readParenthesizedContent(ctx);
            this.emitExplicitCondition(ctx, expr, sql);
        } else {
            // 隐式条件：#(sql)
            this.emitImplicitCondition(ctx, firstParen);
        }
    }

    /**
     * 发射隐式条件 <if>：提取 :param 生成 isNotEmpty test
     */
    private void emitImplicitCondition(S2Context ctx, String sql) {
        // 处理条件体内部的 mgxsql 语法，同时提取 :param
        ProcessedBody processed = this.processConditionBody(sql);
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
     * 发射显式条件 <if>：expr 作为 test 表达式
     */
    private void emitExplicitCondition(S2Context ctx, String expr, String sql) {
        // expr 中的 :param 去冒号作为变量名
        String testExpr = this.stripParamColons(expr);
        // sql 中的 :param 转换为 #{param}
        ProcessedBody processed = this.processConditionBody(sql);
        String ifContent = processed.body.trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpr);
        ctx.appendOutput("\">");
        ctx.appendOutput(" ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== 条件节点处理（SET 域） ====================

    /**
     * 处理 SET 域的 #(sql) 或 #(expr)(sql) 条件节点
     * 与 WHERE 域相同，但不处理 and/or 前缀
     */
    private void processSetConditionNode(S2Context ctx) {
        // 跳过 #
        ctx.advance();
        // 读取第一个 (...) 内容
        String firstParen = this.readParenthesizedContent(ctx);

        if (ctx.hasMore() && ctx.currentChar() == '(') {
            // 显式条件
            String expr = firstParen;
            String sql = this.readParenthesizedContent(ctx);
            String testExpr = this.stripParamColons(expr);
            ProcessedBody processed = this.processConditionBody(sql);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpr);
            ctx.appendOutput("\"> ");
            ctx.appendOutput(ifContent);
            ctx.appendOutput("</if>");
        } else {
            // 隐式条件
            ProcessedBody processed = this.processConditionBody(firstParen);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();

            ctx.appendOutput("<if test=\"");
            ctx.appendOutput(testExpression);
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
}
