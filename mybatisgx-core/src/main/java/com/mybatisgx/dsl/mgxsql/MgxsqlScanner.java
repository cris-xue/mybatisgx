package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.S2Condition;
import com.mybatisgx.dsl.mgxsql.model.S2Context;
import com.mybatisgx.dsl.mgxsql.model.S2State;
import com.mybatisgx.template.MybatisXmlHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 逐字符状态机扫描器，将 mgxsql 简化语法文本转换为标准 MyBatis XML 文本
 * <p>
 * 语法规则：
 * <ul>
 *   <li>where → &lt;where&gt;...&lt;/where&gt;</li>
 *   <li>set → &lt;set&gt;...&lt;/set&gt;</li>
 *   <li>?条件 → &lt;if test="参数非空"&gt; and 条件&lt;/if&gt;</li>
 *   <li>?(expr)(条件) → &lt;if test="expr"&gt; and (条件)&lt;/if&gt;</li>
 *   <li>in #{list} → &lt;foreach&gt;</li>
 *   <li>in (item:list)=>#{item.prop} → &lt;foreach&gt;</li>
 *   <li>%#{x}% / #{x}% / %#{x} → &lt;bind&gt; + like</li>
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
        // 检测 ? 可选条件
        if (c == '?') {
            this.processOptionalCondition(ctx);
            return;
        }
        // 检测 in 关键字（必须在 WHERE 域）
        if (this.isKeywordAt(ctx, "in") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
            this.processInClause(ctx);
            return;
        }
        // 检测 and/or 逻辑连接词（记录但不消耗，留给 ? 处理）
        if (this.isKeywordAt(ctx, "and") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 3)) {
            ctx.setLastLogicConnector("and");
            ctx.appendOutput(" and");
            ctx.setPosition(ctx.getPosition() + 3);
            return;
        }
        if (this.isKeywordAt(ctx, "or") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
            ctx.setLastLogicConnector("or");
            ctx.appendOutput(" or");
            ctx.setPosition(ctx.getPosition() + 2);
            return;
        }
        // 检测 %#{ （LIKE 模式 - 左侧或双侧模糊）
        if (c == '%' && ctx.peekChar(1) == '#' && ctx.peekChar(2) == '{') {
            this.processLikePattern(ctx);
            return;
        }
        // 检测 #{param}% （右侧模糊匹配）
        if (c == '#' && ctx.peekChar(1) == '{') {
            String rightLikeResult = this.tryProcessRightLike(ctx);
            if (rightLikeResult != null) {
                ctx.appendOutput(rightLikeResult);
                return;
            }
        }
        // 默认原样输出（包括普通的 #{} 参数引用）
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
        // 检测 ? 可选条件
        if (c == '?') {
            this.processSetOptional(ctx);
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

    // ==================== 可选条件处理 ====================

    private void processOptionalCondition(S2Context ctx) {
        // 跳过 ?
        ctx.advance();
        // 判断是 ?(expr) 还是 ?字段
        if (ctx.hasMore() && ctx.currentChar() == '(') {
            this.processExprCondition(ctx);
        } else {
            this.processSimpleCondition(ctx);
        }
    }

    /**
     * 处理 ?条件（简单可选条件）
     * 读取到同级 and/or 截止
     */
    private void processSimpleCondition(S2Context ctx) {
        // 读取条件体（到下一个同级 and/or 或字符串结束）
        String connector = ctx.getLastLogicConnector();
        StringBuilder conditionBody = new StringBuilder();
        List<String> paramPaths = new ArrayList<>();

        while (ctx.hasMore()) {
            // 检测 and/or 截止
            if (this.isKeywordAt(ctx, "and") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 3)) {
                break;
            }
            if (this.isKeywordAt(ctx, "or") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
                break;
            }
            // 检测 %#{ （LIKE 模式）
            char c = ctx.currentChar();
            if (c == '%' && ctx.peekChar(1) == '#' && ctx.peekChar(2) == '{') {
                String likeResult = this.extractLikePattern(ctx, conditionBody, paramPaths);
                if (likeResult != null) {
                    continue;
                }
            }
            // 检测 #{param}
            if (c == '#' && ctx.peekChar(1) == '{') {
                String paramPath = this.extractParamRef(ctx, conditionBody);
                if (paramPath != null) {
                    paramPaths.add(paramPath);
                    continue;
                }
            }
            // 检测 in #{list}
            if (this.isKeywordAt(ctx, "in") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 2)) {
                String inResult = this.extractInClause(ctx, conditionBody, paramPaths);
                if (inResult != null) {
                    continue;
                }
            }
            conditionBody.append(c);
            ctx.advance();
        }

        // 生成 <if> 标签
        String testExpression = this.buildTestExpression(paramPaths);
        String ifContent = conditionBody.toString().trim();
        String connectorInIf = this.resolveConnectorInIf(connector);

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\">");
        ctx.appendOutput(connectorInIf);
        ctx.appendOutput(" ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");

        ctx.setLastLogicConnector(null);
    }

    /**
     * 处理 ?(expr)(condition)（表达式可选条件）
     */
    private void processExprCondition(S2Context ctx) {
        // 读取表达式：?(expr) 中的 expr
        String expression = this.readParenthesizedContent(ctx);
        // 读取条件体：(condition) 中的 condition，同时处理其中的 in/like 等语法
        String conditionBody = this.readAndProcessExprConditionBody(ctx);

        String connector = ctx.getLastLogicConnector();
        String connectorInIf = this.resolveConnectorInIf(connector);

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(expression);
        ctx.appendOutput("\">");
        ctx.appendOutput(connectorInIf);
        ctx.appendOutput(" (");
        ctx.appendOutput(conditionBody.trim());
        ctx.appendOutput(")</if>");

        ctx.setLastLogicConnector(null);
    }

    /**
     * 读取并处理表达式条件体中的 mgxsql 语法（in #{}, %#{x}% 等）
     */
    private String readAndProcessExprConditionBody(S2Context ctx) {
        // 先用括号匹配读取原始条件体
        String rawBody = this.readParenthesizedContent(ctx);
        // 对条件体内部调用扫描器处理（子扫描）
        // 为了简化，直接用完整扫描器处理条件体
        // 但条件体不需要 where/set 转换，只需处理 in/like/#{} 等
        return this.processConditionBody(rawBody);
    }

    /**
     * 处理条件体文本中的 mgxsql 语法（in, like 等，但不处理 where/set/?）
     */
    private String processConditionBody(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);

            // 检测 in #{ (简单类型)
            if (this.isKeywordAt(text, i, "in") && this.isWordBoundaryBefore(text, i) && this.isWordBoundaryAfter(text, i + 2)) {
                int inEnd = this.processConditionIn(text, i, result);
                if (inEnd > i) {
                    i = inEnd;
                    continue;
                }
            }

            // 检测 %#{ （LIKE 模式）
            if (c == '%' && i + 1 < text.length() && text.charAt(i + 1) == '#' && i + 2 < text.length() && text.charAt(i + 2) == '{') {
                int likeEnd = this.processConditionLike(text, i, result);
                if (likeEnd > i) {
                    i = likeEnd;
                    continue;
                }
            }

            // 检测 #{x}% （右侧 LIKE）
            if (c == '#' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                int paramEnd = this.findParamEnd(text, i);
                if (paramEnd > i) {
                    String paramPath = text.substring(i + 2, paramEnd);
                    // 检测后面是否有 %
                    if (paramEnd + 1 < text.length() && text.charAt(paramEnd + 1) == '%') {
                        // #{x}% 右侧模糊
                        String bindName = "_like_" + paramPath.replace('.', '_');
                        result.append("<bind name=\"").append(bindName).append("\" value=\"").append(paramPath).append(" + '%'\"/>");
                        result.append("#{").append(bindName).append("}");
                        i = paramEnd + 2; // 跳过 }%
                        continue;
                    }
                    // 普通参数引用，原样输出
                    result.append(text.substring(i, paramEnd + 1));
                    i = paramEnd + 1;
                    continue;
                }
            }

            result.append(c);
            i++;
        }
        return result.toString();
    }

    private int processConditionIn(String text, int start, StringBuilder result) {
        int pos = start + 2; // 跳过 "in"
        // 跳过空白
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return start;
        }

        // 简单类型 IN：#{list}
        if (text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
            int paramEnd = this.findParamEnd(text, pos);
            if (paramEnd > pos) {
                String collectionName = text.substring(pos + 2, paramEnd);
                result.append("in <foreach item=\"item\" collection=\"").append(collectionName).append("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return paramEnd + 1;
            }
        }

        // 复杂类型 IN：(item:collection)=>#{item.prop}
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
                        // 读取 #{item.prop}
                        if (pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                            int valueEnd = this.findParamEnd(text, pos);
                            if (valueEnd > pos) {
                                String valueExpr = text.substring(pos, valueEnd + 1);
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

    private int processConditionLike(String text, int start, StringBuilder result) {
        // %#{x}% 或 %#{x}
        int pos = start + 1; // 跳过 %
        if (pos >= text.length() || text.charAt(pos) != '#' || pos + 1 >= text.length() || text.charAt(pos + 1) != '{') {
            return start;
        }
        int paramEnd = this.findParamEnd(text, pos);
        if (paramEnd <= pos) {
            return start;
        }
        String paramName = text.substring(pos + 2, paramEnd);
        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;

        if (paramEnd + 1 < text.length() && text.charAt(paramEnd + 1) == '%') {
            likeType = S2Condition.LikeType.BOTH;
            paramEnd++; // 跳过 %
        }

        String bindName = "_like_" + paramName.replace('.', '_');
        String bindValue = this.buildLikeBindValue(paramName, likeType);
        result.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
        result.append("#{").append(bindName).append("}");

        return paramEnd + 1;
    }

    private boolean isKeywordAt(String text, int pos, String keyword) {
        if (pos + keyword.length() > text.length()) {
            return false;
        }
        return text.substring(pos, pos + keyword.length()).equalsIgnoreCase(keyword);
    }

    private boolean isWordBoundaryBefore(String text, int pos) {
        if (pos == 0) {
            return true;
        }
        char prev = text.charAt(pos - 1);
        return !Character.isLetterOrDigit(prev) && prev != '_' && prev != '.';
    }

    private boolean isWordBoundaryAfter(String text, int pos) {
        if (pos >= text.length()) {
            return true;
        }
        char next = text.charAt(pos);
        return !Character.isLetterOrDigit(next) && next != '_' && next != '.';
    }

    private int findParamEnd(String text, int start) {
        // 找到 #{param} 中的 } 位置
        if (start >= text.length() || text.charAt(start) != '#' || start + 1 >= text.length() || text.charAt(start + 1) != '{') {
            return -1;
        }
        int pos = start + 2;
        while (pos < text.length() && text.charAt(pos) != '}') {
            pos++;
        }
        return pos < text.length() ? pos : -1;
    }

    // ==================== SET 域可选条件 ====================

    private void processSetOptional(S2Context ctx) {
        // 跳过 ?
        ctx.advance();

        // SET 域的 ? 类似 WHERE 域的简单可选条件，但不涉及 and/or
        // 读取条件体（到逗号或 where 截止）
        StringBuilder conditionBody = new StringBuilder();
        List<String> paramPaths = new ArrayList<>();

        while (ctx.hasMore()) {
            // 检测 where 截止
            if (this.isKeywordAt(ctx, "where") && this.isWordBoundaryBefore(ctx) && this.isWordBoundaryAfter(ctx, 5)) {
                break;
            }
            char c = ctx.currentChar();
            // 检测 #{param}
            if (c == '#' && ctx.peekChar(1) == '{') {
                String paramPath = this.extractParamRef(ctx, conditionBody);
                if (paramPath != null) {
                    paramPaths.add(paramPath);
                    continue;
                }
            }
            // 逗号截止（SET 域中每个条件以逗号分隔）
            // 但逗号可能在 #{param} 内部，所以只检测独立的逗号
            if (c == ',') {
                conditionBody.append(c);
                ctx.advance();
                break;
            }
            conditionBody.append(c);
            ctx.advance();
        }

        String testExpression = this.buildTestExpression(paramPaths);
        String ifContent = conditionBody.toString().trim();

        ctx.appendOutput("<if test=\"");
        ctx.appendOutput(testExpression);
        ctx.appendOutput("\"> ");
        ctx.appendOutput(ifContent);
        ctx.appendOutput("</if>");
    }

    // ==================== IN 子句处理 ====================

    private void processInClause(S2Context ctx) {
        // 跳过 "in"
        ctx.setPosition(ctx.getPosition() + 2);
        // 跳过空白
        this.skipWhitespace(ctx);

        if (!ctx.hasMore()) {
            ctx.appendOutput("in");
            return;
        }

        // 判断是 #{list} 还是 (item:list)=>#{item.prop}
        if (ctx.currentChar() == '#' && ctx.peekChar(1) == '{') {
            // 简单类型 IN
            String collectionName = this.readParamRef(ctx);
            if (collectionName != null) {
                ctx.appendOutput("in <foreach item=\"item\" collection=\"");
                ctx.appendOutput(collectionName);
                ctx.appendOutput("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return;
            }
        } else if (ctx.currentChar() == '(') {
            // 复杂类型 IN：(item:collection)=>#{item.prop}
            ctx.advance(); // 跳过 (
            String itemName = this.readIdentifier(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ':') {
                ctx.advance(); // 跳过 :
                String collectionName = this.readIdentifier(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance(); // 跳过 )
                    // 读取 =>#{item.prop}
                    if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
                        ctx.advance(); // 跳过 =
                        ctx.advance(); // 跳过 >
                        // 读取 #{item.prop}
                        String valueExpr = this.readParamRefFull(ctx);
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

    // ==================== LIKE 模式处理 ====================

    private void processLikePattern(S2Context ctx) {
        // 检测三种 LIKE 模式：%#{x}%、#{x}%、%#{x}
        // 这里的 %#{ 是由 WHERE 域检测到的
        S2Condition.LikeType likeType = S2Condition.LikeType.NONE;
        String paramName = null;

        // 已经检测到 %#{，读取参数名
        ctx.advance(); // 跳过 %
        paramName = this.readParamRef(ctx);

        // 检测后面的 %
        if (ctx.hasMore() && ctx.currentChar() == '%') {
            likeType = S2Condition.LikeType.BOTH; // %#{x}%
            ctx.advance(); // 跳过 %
        } else {
            likeType = S2Condition.LikeType.LEFT; // %#{x}
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

    /**
     * 在条件体提取中处理 LIKE 模式
     */
    private String extractLikePattern(S2Context ctx, StringBuilder conditionBody, List<String> paramPaths) {
        // 保存位置以便回退
        int savedPos = ctx.getPosition();

        // 检测 %#{
        ctx.advance(); // 跳过 %
        String paramName = this.readParamRef(ctx);
        S2Condition.LikeType likeType = S2Condition.LikeType.LEFT;

        if (ctx.hasMore() && ctx.currentChar() == '%') {
            likeType = S2Condition.LikeType.BOTH;
            ctx.advance(); // 跳过 %
        }

        if (paramName != null) {
            paramPaths.add(paramName);
            String bindName = "_like_" + paramName.replace('.', '_');
            String bindValue = this.buildLikeBindValue(paramName, likeType);
            conditionBody.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
            conditionBody.append("#{").append(bindName).append("}");
            return "";
        }
        // 回退
        ctx.setPosition(savedPos);
        return null;
    }

    // ==================== 参数引用提取 ====================

    /**
     * 从 #{param} 中提取参数路径，并将 #{param} 写入条件体
     * 同时检测 #{param}% 右侧模糊匹配模式
     */
    private String extractParamRef(S2Context ctx, StringBuilder conditionBody) {
        if (ctx.currentChar() != '#' || ctx.peekChar(1) != '{') {
            return null;
        }
        String paramPath = this.readParamRef(ctx);
        if (paramPath != null) {
            // 检测后面是否有 % （右侧模糊匹配）
            if (ctx.hasMore() && ctx.currentChar() == '%') {
                ctx.advance(); // 跳过 %
                String bindName = "_like_" + paramPath.replace('.', '_');
                String bindValue = paramPath + " + '%'";
                conditionBody.append("<bind name=\"").append(bindName).append("\" value=\"").append(bindValue).append("\"/>");
                conditionBody.append("#{").append(bindName).append("}");
            } else {
                conditionBody.append("#{").append(paramPath).append("}");
            }
        }
        return paramPath;
    }

    /**
     * 在条件体提取中处理 IN 子句
     */
    private String extractInClause(S2Context ctx, StringBuilder conditionBody, List<String> paramPaths) {
        int savedPos = ctx.getPosition();

        // 跳过 "in"
        ctx.setPosition(ctx.getPosition() + 2);
        this.skipWhitespace(ctx);

        if (!ctx.hasMore()) {
            ctx.setPosition(savedPos);
            return null;
        }

        if (ctx.currentChar() == '#' && ctx.peekChar(1) == '{') {
            String collectionName = this.readParamRef(ctx);
            if (collectionName != null) {
                paramPaths.add(collectionName);
                conditionBody.append("in <foreach item=\"item\" collection=\"")
                        .append(collectionName)
                        .append("\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>");
                return "";
            }
        } else if (ctx.currentChar() == '(') {
            ctx.advance();
            String itemName = this.readIdentifier(ctx);
            if (ctx.hasMore() && ctx.currentChar() == ':') {
                ctx.advance();
                String collectionName = this.readIdentifier(ctx);
                if (ctx.hasMore() && ctx.currentChar() == ')') {
                    ctx.advance();
                    if (ctx.hasMore() && ctx.currentChar() == '=' && ctx.peekChar(1) == '>') {
                        ctx.advance();
                        ctx.advance();
                        String valueExpr = this.readParamRefFull(ctx);
                        if (valueExpr != null) {
                            paramPaths.add(collectionName);
                            conditionBody.append("in <foreach item=\"")
                                    .append(itemName)
                                    .append("\" collection=\"")
                                    .append(collectionName)
                                    .append("\" open=\"(\" close=\")\" separator=\",\">")
                                    .append(valueExpr)
                                    .append("</foreach>");
                            return "";
                        }
                    }
                }
            }
        }

        ctx.setPosition(savedPos);
        return null;
    }

    // ==================== 辅助方法 ====================

    /**
     * 读取括号内的内容（已跳过第一个 '('）
     */
    private String readParenthesizedContent(S2Context ctx) {
        // 跳过 (
        if (ctx.hasMore() && ctx.currentChar() == '(') {
            ctx.advance();
        }
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
            if (c == '>' && depth > 0) {
                // 继续找
            }
            pos++;
        }
        return -1;
    }

    /**
     * 检测指定位置是否是自闭合标签（如 <if ... />）
     */
    private boolean isClosingTagAt(S2Context ctx, int pos) {
        // 往回找，看 > 前面是否有 /
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
     * 构建 <if> 的 test 表达式
     */
    private String buildTestExpression(List<String> paramPaths) {
        if (paramPaths == null || paramPaths.isEmpty()) {
            return "true";
        }
        List<String> testParts = new ArrayList<>();
        for (String paramPath : paramPaths) {
            // 对嵌套路径（如 entity.name），逐级生成 isNotEmpty
            String[] parts = paramPath.split("\\.");
            List<String> currentPath = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                currentPath.add(parts[i]);
                String path = StringUtils.join(currentPath, ".");
                testParts.add(String.format(IS_NOT_EMPTY, path));
            }
        }
        return StringUtils.join(testParts, " and ");
    }

    /**
     * 解析 ? 条件在 <if> 内的连接词
     */
    private String resolveConnectorInIf(String connector) {
        if (connector != null) {
            return " " + connector;
        }
        // 首位条件补 and（<where> 会自动去除）
        return " and";
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

    /**
     * 尝试检测并处理 #{param}% 右侧模糊匹配模式
     * 如果当前 #{param} 后面紧跟 %，则生成 <bind> + like
     * 否则返回 null
     */
    private String tryProcessRightLike(S2Context ctx) {
        int savedPos = ctx.getPosition();
        String paramName = this.readParamRef(ctx);
        if (paramName == null) {
            ctx.setPosition(savedPos);
            return null;
        }
        // 检测后面是否有 %
        if (ctx.hasMore() && ctx.currentChar() == '%') {
            ctx.advance(); // 跳过 %
            String bindName = "_like_" + paramName.replace('.', '_');
            String bindValue = paramName + " + '%'";
            return "<bind name=\"" + bindName + "\" value=\"" + bindValue + "\"/>#{" + bindName + "}";
        }
        // 不是右侧模糊，回退位置，让默认处理
        ctx.setPosition(savedPos);
        return null;
    }
}
