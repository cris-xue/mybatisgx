package com.mybatisgx.dsl.mgxsql;

import org.apache.commons.lang3.StringUtils;

/**
 * mgxsql 语法检测器，检测 XML 标签内的 SQL 文本是否包含 mgxsql 语法标记（v4）
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public class MgxsqlDetector {

    /**
     * 检测 SQL 文本是否包含 mgxsql 语法标记
     * <p>
     * 检测标记（v4）：#[、#(、#identifier（形式1）、:param（非 :: ）、in :、%:、where[
     *
     * @param sqlText SQL 文本
     * @return true 如果包含 mgxsql 语法
     */
    public static boolean containsMgxsqlSyntax(String sqlText) {
        if (StringUtils.isBlank(sqlText)) {
            return false;
        }
        String trimmed = sqlText.trim();
        // 检测 #[ 条件节点标记（无guard）
        if (containsMgxsqlConditionNodeBracket(trimmed)) {
            return true;
        }
        // 检测 #( 条件节点标记（有guard）
        if (containsMgxsqlConditionNodeParen(trimmed)) {
            return true;
        }
        // 检测 #identifier 形式1简写条件
        if (containsMgxsqlForm1Condition(trimmed)) {
            return true;
        }
        // 检测 :param 参数绑定（排除字符串字面量内的，排除 :: ）
        if (containsMgxsqlParamRef(trimmed)) {
            return true;
        }
        // 检测 in : （简单类型 IN）
        if (trimmed.matches("(?i).*\\bin\\s+:\\w.*")) {
            return true;
        }
        // 检测 in ( （复杂类型 IN）
        if (trimmed.matches("(?i).*\\bin\\s*\\(\\w+:.*")) {
            return true;
        }
        // 检测 %: （LIKE 模式）
        if (containsMgxsqlLike(trimmed)) {
            return true;
        }
        // 检测 where[ （有边界 where）
        if (containsMgxsqlWhereBracket(trimmed)) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否包含 #[ 条件节点标记（无guard，v4新增）
     */
    private static boolean containsMgxsqlConditionNodeBracket(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == '#' && i + 1 < sqlText.length() && sqlText.charAt(i + 1) == '[') {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含 #( 条件节点标记（有guard）
     */
    private static boolean containsMgxsqlConditionNodeParen(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == '#' && i + 1 < sqlText.length() && sqlText.charAt(i + 1) == '(') {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含 mgxsql 的 #identifier 形式1简写条件
     */
    private static boolean containsMgxsqlForm1Condition(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == '#' && i + 1 < sqlText.length()) {
                char next = sqlText.charAt(i + 1);
                if ((Character.isLetter(next) || next == '_') && next != '{' && next != '[' && next != '(') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测是否包含 :param 参数绑定
     */
    private static boolean containsMgxsqlParamRef(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == ':') {
                if (i > 0 && sqlText.charAt(i - 1) == ':') {
                    continue;
                }
                if (i + 1 < sqlText.length() && sqlText.charAt(i + 1) == ':') {
                    continue;
                }
                if (i + 1 < sqlText.length()) {
                    char next = sqlText.charAt(i + 1);
                    if (Character.isLetter(next) || next == '_') {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测是否包含 LIKE 模式（%:name）
     */
    private static boolean containsMgxsqlLike(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == '%' && i + 1 < sqlText.length() && sqlText.charAt(i + 1) == ':') {
                if (i + 2 < sqlText.length()) {
                    char next = sqlText.charAt(i + 2);
                    if (Character.isLetter(next) || next == '_') {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测是否包含 where[ 边界语法（v4新增）
     */
    private static boolean containsMgxsqlWhereBracket(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && i + 5 < sqlText.length()) {
                if (sqlText.substring(i, i + 5).equalsIgnoreCase("where")
                        && sqlText.charAt(i + 5) == '[') {
                    // 检查前方单词边界
                    if (i == 0 || !Character.isLetterOrDigit(sqlText.charAt(i - 1))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
