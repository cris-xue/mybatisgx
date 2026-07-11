package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.MgxsqlCondition;
import com.mybatisgx.exception.MybatisgxException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 条件体处理器，处理纯文本中嵌套的 mgxsql 语法（递归引擎）
 * <p>
 * 负责：
 * <ul>
 *   <li>:param → #{param}，同时收集参数路径</li>
 *   <li>in :list → &lt;foreach&gt;</li>
 *   <li>in (item:list)=&gt;$item.prop → &lt;foreach&gt;</li>
 *   <li>%:name% / :name% / %:name → &lt;bind&gt; + like</li>
 *   <li>#{param} 原样保留</li>
 *   <li>#[body] 和 #(expr)[body] 递归处理</li>
 *   <li>#condition 形式1 递归处理</li>
 *   <li>构建 OGNL test 表达式</li>
 *   <li>guard 表达式中 :param 去冒号</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public class MgxsqlConditionBodyProcessor {

    private static final String IS_NOT_EMPTY = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%s)";

    /**
     * 条件体处理结果
     */
    public static class ProcessedBody {
        String body;
        List<String> paramPaths;

        public ProcessedBody(String body, List<String> paramPaths) {
            this.body = body;
            this.paramPaths = paramPaths;
        }

        public String getBody() {
            return body;
        }

        public List<String> getParamPaths() {
            return paramPaths;
        }
    }

    /**
     * 处理条件体文本中的 mgxsql 语法
     */
    public ProcessedBody processConditionBody(String text) {
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
            if (MgxsqlSyntaxHelper.isKeywordAt(text, i, "in") && MgxsqlSyntaxHelper.isWordBoundaryBefore(text, i) && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, i + 2)) {
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
            if (c == ':' && i + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(i + 1))) {
                int paramNameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, i + 1);
                String paramName = text.substring(i + 1, paramNameEnd);
                // 检测后面是否有 %
                if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
                    paramPaths.add(paramName);
                    String bindName = "_like_" + paramName.replace('.', '_');
                    String bindValue = paramName + " + '%'";
                    result.append(MgxsqlXmlFragment.bindTag(bindName, bindValue));
                    result.append(MgxsqlXmlFragment.paramRef(bindName));
                    i = paramNameEnd + 1;
                    continue;
                }
                // 普通 :param → #{param}
                paramPaths.add(paramName);
                result.append(MgxsqlXmlFragment.paramRef(paramName));
                i = paramNameEnd;
                continue;
            }

            // 检测 #{param} — 条件节点块内禁止，报语法错误
            if (c == '#' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #{param}，请使用 :param 代替，位置: %s", String.valueOf(i));
            }

            // 检测 ${param} — 条件节点块内禁止，报语法错误
            if (c == '$' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 ${param}，位置: %s", String.valueOf(i));
            }

            // 检测 < — 条件节点块内 XML 标签禁止，但 SQL 比较运算符（< 后不像 XML 标签名）允许转义输出
            if (c == '<') {
                if (isConditionXmlTagStart(text, i)) {
                    throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 XML 标签，位置: %s", String.valueOf(i));
                }
                // SQL 比较运算符，转义输出
                result.append("&lt;");
                i++;
                continue;
            }

            // 检测 > — SQL 比较运算符，转义输出
            if (c == '>') {
                result.append("&gt;");
                i++;
                continue;
            }

            result.append(c);
            i++;
        }
        return new ProcessedBody(result.toString(), paramPaths);
    }

    /**
     * 构建 OGNL test 表达式
     */
    public String buildTestExpression(List<String> paramPaths) {
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

    /**
     * guard 表达式处理：:param 去冒号 + < > && || 转义
     * <p>
     * 处理顺序：先去冒号，再做特殊字符转义（避免 :param 中的 : 干扰）
     *
     * @param expr 原始 guard 表达式
     * @return 去冒号并转义后的 OGNL 表达式
     */
    public String stripParamColons(String expr) {
        if (StringUtils.isBlank(expr)) {
            return expr;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            // :param → param（去冒号）
            if (c == ':' && i + 1 < expr.length() && MgxsqlSyntaxHelper.isIdentifierStartChar(expr.charAt(i + 1))) {
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
                continue;
            }
            // < → &lt;，> → &gt;
            if (c == '<') {
                result.append("&lt;");
                i++;
                continue;
            }
            if (c == '>') {
                result.append("&gt;");
                i++;
                continue;
            }
            // && → and，|| → or
            if (c == '&' && i + 1 < expr.length() && expr.charAt(i + 1) == '&') {
                result.append("and");
                i += 2;
                continue;
            }
            if (c == '|' && i + 1 < expr.length() && expr.charAt(i + 1) == '|') {
                result.append("or");
                i += 2;
                continue;
            }
            result.append(c);
            i++;
        }
        return result.toString();
    }

    /**
     * 构建 LIKE bind 值
     */
    public String buildLikeBindValue(String paramName, MgxsqlCondition.LikeType likeType) {
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

    // ==================== 条件体 # 语法递归处理 ====================

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
            // 不冒泡：嵌套条件块的参数由嵌套条件自己的 <if> 守卫

            result.append(MgxsqlXmlFragment.ifTag(testExpression, ifContent));

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
                // 不冒泡：空 guard 时嵌套条件块的参数由嵌套条件自己的 <if> 守卫

                result.append(MgxsqlXmlFragment.ifTag(testExpression, ifContent));
            } else {
                String testExpr = this.stripParamColons(guardContent);
                ProcessedBody processed = this.processConditionBody(bodyContent);
                String ifContent = processed.body.trim();

                result.append(MgxsqlXmlFragment.ifTag(testExpr, ifContent));
            }

            return pos;
        }

        // #{param} — 条件节点块内禁止，报语法错误
        if (next == '{') {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #{param}，请使用 :param 代替，位置: %s", String.valueOf(start));
        }

        // #and / #or — 条件节点块内禁止，报语法错误
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "and") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 4)) {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #and/#or 简写，请使用 #[and ...] 嵌套条件体，位置: %s", String.valueOf(start));
        }
        if (MgxsqlSyntaxHelper.isKeywordAt(text, start + 1, "or") && MgxsqlSyntaxHelper.isWordBoundaryAfter(text, start + 3)) {
            throw new MybatisgxException("mgxsql 语法错误: 条件节点块内不允许使用 #and/#or 简写，请使用 #[and ...] 嵌套条件体，位置: %s", String.valueOf(start));
        }

        // #identifier — 形式1
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
                // 括号和方括号外遇到独立的 and/or 报语法错误
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
            ProcessedBody processed = this.processConditionBody(condition);
            String testExpression = this.buildTestExpression(processed.paramPaths);
            String ifContent = processed.body.trim();
            // 不冒泡：形式1内嵌套条件块的参数由嵌套条件自己的 <if> 守卫

            result.append(MgxsqlXmlFragment.ifTag(testExpression, ifContent));

            return lineEnd;
        }

        return start;
    }

    // ==================== 条件体内部 IN / LIKE 处理 ====================

    /**
     * 条件体内 IN 子句入口：分发到子方法
     */
    private int processConditionIn(String text, int start, StringBuilder result, List<String> paramPaths) {
        int savedPos = start + 2;
        int pos = savedPos;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return start;
        }

        // 简单类型 IN：in :list
        if (text.charAt(pos) == ':' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            int nameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
            String collectionName = text.substring(pos + 1, nameEnd);
            this.processConditionInSimple(collectionName, result, paramPaths);
            return nameEnd;
        }

        // 括号包裹 IN：in (...)
        if (text.charAt(pos) == '(') {
            int parenResult = this.processConditionInParenthesized(text, pos, result, paramPaths, start, savedPos);
            if (parenResult > savedPos) {
                return parenResult;
            }
        }

        return savedPos;
    }

    /**
     * 条件体内简单 IN：in :list
     */
    private void processConditionInSimple(String collectionName, StringBuilder result, List<String> paramPaths) {
        paramPaths.add(collectionName);
        result.append("in ").append(MgxsqlXmlFragment.foreachSimple(collectionName));
    }

    /**
     * 条件体内括号包裹 IN 入口：in (...) 内部分发
     *
     * @return 消费到的位置，若无法识别返回 savedPos
     */
    private int processConditionInParenthesized(String text, int pos, StringBuilder result, List<String> paramPaths, int start, int savedPos) {
        int outerParenPos = pos;
        pos++; // 跳过 (
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos >= text.length()) {
            return savedPos;
        }

        // in ((item:collection)=>$item.prop) — 复杂 IN 外层括号包裹
        if (text.charAt(pos) == '(') {
            int innerStartPos = pos + 1; // 跳过内层 (
            while (innerStartPos < text.length() && Character.isWhitespace(text.charAt(innerStartPos))) {
                innerStartPos++;
            }
            int innerResult = this.processConditionInComplexWrapped(text, innerStartPos, result, paramPaths, savedPos);
            if (innerResult > savedPos) {
                // 成功解析内层复杂 IN，消费空白和外层 )
                while (innerResult < text.length() && Character.isWhitespace(text.charAt(innerResult))) {
                    innerResult++;
                }
                if (innerResult < text.length() && text.charAt(innerResult) == ')') {
                    innerResult++;
                }
                return innerResult;
            }
            // 外层括号包裹解析失败，恢复位置继续尝试其他路径
            pos = outerParenPos + 1;
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        // in (:list) — 简单 IN + 括号
        if (text.charAt(pos) == ':' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            int afterPos = this.processConditionInSimpleParen(text, pos, result, paramPaths);
            if (afterPos >= 0) {
                return afterPos;
            }
        }

        // in (#{list}) — MyBatis 原生，不翻译
        if (text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
            return start;
        }

        // in (item:collection)=>$item.prop — 复杂类型 IN（无外层括号）
        return this.processConditionInComplexParen(text, pos, result, paramPaths, savedPos);
    }

    /**
     * 条件体内复杂 IN 外层括号包裹：in ((item:collection)=&gt;$var)
     * <p>
     * pos 指向 item 标识符起始位置，已跳过内层 (
     *
     * @return 成功返回内层 ) 之后的下一个位置，失败返回 savedPos
     */
    private int processConditionInComplexWrapped(String text, int pos, StringBuilder result, List<String> paramPaths, int savedPos) {
        int itemNameStart = pos;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        String itemName = text.substring(itemNameStart, pos);
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos < text.length() && text.charAt(pos) == ':') {
            pos++; // 跳过 :
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
                pos++; // 跳过内层 )
                while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
                if (pos < text.length() && text.charAt(pos) == '=' && pos + 1 < text.length() && text.charAt(pos + 1) == '>') {
                    pos += 2; // 跳过 =>
                    while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                        pos++;
                    }
                    // => 右边只接受 $variable，禁止 #{} 和 ${}
                    if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
                        int varEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
                        String varName = text.substring(pos + 1, varEnd);
                        paramPaths.add(collectionName);
                        result.append("in ").append(MgxsqlXmlFragment.foreachComplex(itemName, collectionName, MgxsqlXmlFragment.paramRef(varName)));
                        return varEnd;
                    } else if (pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, 位置: %s", String.valueOf(pos));
                    } else if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, 位置: %s", String.valueOf(pos));
                    }
                }
            }
        }
        return savedPos;
    }

    /**
     * 条件体内括号包裹简单 IN：in (:list)
     *
     * @return 成功返回 ) 之后的下一个位置，失败返回 -1
     */
    private int processConditionInSimpleParen(String text, int pos, StringBuilder result, List<String> paramPaths) {
        int nameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
        String collectionName = text.substring(pos + 1, nameEnd);
        int afterName = nameEnd;
        while (afterName < text.length() && Character.isWhitespace(text.charAt(afterName))) {
            afterName++;
        }
        if (afterName < text.length() && text.charAt(afterName) == ')') {
            afterName++; // 跳过 )
            this.processConditionInSimple(collectionName, result, paramPaths);
            return afterName;
        }
        return -1;
    }

    /**
     * 条件体内复杂 IN（无外层括号）：in (item:collection)=&gt;$var
     * <p>
     * 合并原 parseComplexInInner 逻辑
     *
     * @return 成功返回消费到的位置，失败返回 savedPos
     */
    private int processConditionInComplexParen(String text, int pos, StringBuilder result, List<String> paramPaths, int savedPos) {
        int itemNameStart = pos;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_' || text.charAt(pos) == '.')) {
            pos++;
        }
        String itemName = text.substring(itemNameStart, pos);
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        if (pos < text.length() && text.charAt(pos) == ':') {
            pos++; // 跳过 :
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
                pos++; // 跳过 )
                while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
                if (pos < text.length() && text.charAt(pos) == '=' && pos + 1 < text.length() && text.charAt(pos + 1) == '>') {
                    pos += 2; // 跳过 =>
                    while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                        pos++;
                    }
                    // => 右边只接受 $variable，禁止 #{} 和 ${}
                    if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
                        int varEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
                        String varName = text.substring(pos + 1, varEnd);
                        paramPaths.add(collectionName);
                        result.append("in ").append(MgxsqlXmlFragment.foreachComplex(itemName, collectionName, MgxsqlXmlFragment.paramRef(varName)));
                        return varEnd;
                    } else if (pos < text.length() && text.charAt(pos) == '#' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, 位置: %s", String.valueOf(pos));
                    } else if (pos < text.length() && text.charAt(pos) == '$' && pos + 1 < text.length() && text.charAt(pos + 1) == '{') {
                        throw new MybatisgxException("mgxsql 语法错误: '=>' 右边只接受 $variable 形式，不允许 #{} / ${}, 位置: %s", String.valueOf(pos));
                    }
                }
            }
        }
        return savedPos;
    }

    private int processConditionLike(String text, int start, StringBuilder result, List<String> paramPaths) {
        int pos = start + 1;
        if (pos >= text.length() || text.charAt(pos) != ':' || pos + 1 >= text.length() || !MgxsqlSyntaxHelper.isIdentifierStart(text.charAt(pos + 1))) {
            return start;
        }
        int paramNameEnd = MgxsqlSyntaxHelper.findIdentifierEnd(text, pos + 1);
        String paramName = text.substring(pos + 1, paramNameEnd);
        paramPaths.add(paramName);

        MgxsqlCondition.LikeType likeType = MgxsqlCondition.LikeType.LEFT;
        if (paramNameEnd < text.length() && text.charAt(paramNameEnd) == '%') {
            likeType = MgxsqlCondition.LikeType.BOTH;
            paramNameEnd++;
        }

        String bindName = "_like_" + paramName.replace('.', '_');
        String bindValue = this.buildLikeBindValue(paramName, likeType);
        result.append(MgxsqlXmlFragment.bindTag(bindName, bindValue));
        result.append(MgxsqlXmlFragment.paramRef(bindName));

        return paramNameEnd;
    }

    // ==================== 条件体内 < 智能判断 ====================

    /**
     * 判断条件体文本中指定位置的 '<' 是否是 XML 标签的开始
     * <p>
     * 与 {@link MgxsqlSyntaxHelper#isXmlTagStart(MgxsqlContext)} 逻辑一致，
     * 但基于纯文本 + 索引操作（条件体处理不使用 MgxsqlContext）。
     *
     * @param text 条件体文本
     * @param pos  '<' 所在位置
     * @return 如果判断为 XML 标签开始则返回 true
     */
    private static boolean isConditionXmlTagStart(String text, int pos) {
        if (pos >= text.length() || text.charAt(pos) != '<') {
            return false;
        }
        int nextPos = pos + 1;
        if (nextPos >= text.length()) {
            return false;
        }
        char next = text.charAt(nextPos);

        // </...> 闭合标签
        if (next == '/') {
            return true;
        }
        // <!-- 注释 或 <![CDATA[
        if (next == '!') {
            return true;
        }
        // <?...?> 处理指令
        if (next == '?') {
            return true;
        }
        // 字母或 _ 开头，可能是 XML 标签名
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
            // 标签名后紧跟空格、> 或 / → 是 XML 标签
            if (nameEnd < text.length()) {
                char afterName = text.charAt(nameEnd);
                return afterName == ' ' || afterName == '>' || afterName == '/' || afterName == '\t' || afterName == '\n' || afterName == '\r';
            }
            return true;
        }
        // 其他：空格、数字、=、: 等 → SQL 比较运算符
        return false;
    }
}
