package com.mybatisgx.dsl.mgxsql;

import org.apache.commons.lang3.StringUtils;

/**
 * mgxsql 语法检测器，检测 XML 标签内的 SQL 文本是否包含 mgxsql 语法标记
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlDetector {

    /**
     * 检测 SQL 文本是否包含 mgxsql 语法标记
     * <p>
     * 检测标记：?、?(、in #{、%#{
     *
     * @param sqlText SQL 文本
     * @return true 如果包含 mgxsql 语法
     */
    public static boolean containsMgxsqlSyntax(String sqlText) {
        if (StringUtils.isBlank(sqlText)) {
            return false;
        }
        String trimmed = sqlText.trim();
        // 检测 ? 条件标记（排除字符串字面量内的 ?）
        if (containsMgxsqlQuestionMark(trimmed)) {
            return true;
        }
        // 检测 in #{ （简单类型 IN）
        if (trimmed.contains("in #{") || trimmed.matches("(?i).*\\bin\\s+\\#\\{.*")) {
            return true;
        }
        // 检测 in ( （复杂类型 IN）
        if (trimmed.matches("(?i).*\\bin\\s*\\(\\w+:.*")) {
            return true;
        }
        // 检测 %#{ （LIKE 模式）
        if (trimmed.contains("%#{")) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否包含 mgxsql 的 ? 标记（排除字符串字面量内的 ?）
     */
    private static boolean containsMgxsqlQuestionMark(String sqlText) {
        boolean inString = false;
        for (int i = 0; i < sqlText.length(); i++) {
            char c = sqlText.charAt(i);
            if (c == '\'') {
                inString = !inString;
                continue;
            }
            if (!inString && c == '?') {
                // ? 后面跟字母（?name）或 ?( （?(expr)）
                if (i + 1 < sqlText.length()) {
                    char next = sqlText.charAt(i + 1);
                    if (Character.isLetter(next) || next == '(') {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
