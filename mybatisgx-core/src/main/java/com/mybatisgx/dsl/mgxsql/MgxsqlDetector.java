package com.mybatisgx.dsl.mgxsql;

import org.apache.commons.lang3.StringUtils;

/**
 * mgxsql 语法检测器，检测 XML 标签内的 SQL 文本是否包含 mgxsql 语法标记（v3）
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlDetector {

    /**
     * 检测 SQL 文本是否包含 mgxsql 语法标记
     * <p>
     * 检测标记（v3）：#(、#identifier（形式1）、:param（非 :: ）、in :、%:
     *
     * @param sqlText SQL 文本
     * @return true 如果包含 mgxsql 语法
     */
    public static boolean containsMgxsqlSyntax(String sqlText) {
        if (StringUtils.isBlank(sqlText)) {
            return false;
        }
        String trimmed = sqlText.trim();
        // 检测 #( 条件节点标记（形式2）
        if (containsMgxsqlConditionNode(trimmed)) {
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
        return false;
    }

    /**
     * 检测是否包含 mgxsql 的 #( 条件节点标记（形式2）
     */
    private static boolean containsMgxsqlConditionNode(String sqlText) {
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
     * 检测是否包含 mgxsql 的 #identifier 形式1简写条件（v3新增）
     * 条件：# 后跟标识符起始字符（字母或下划线），且不是 #{
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
                // # 后跟标识符起始字符，且不是 #{ （排除 MyBatis 参数引用）
                if ((Character.isLetter(next) || next == '_') && next != '{') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测是否包含 mgxsql 的 :param 参数绑定（排除字符串字面量内的，排除 ::）
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
                // 排除 ::（前一个字符也是 : 的情况）
                if (i > 0 && sqlText.charAt(i - 1) == ':') {
                    continue;
                }
                // 排除 ::（后一个字符也是 : 的情况）
                if (i + 1 < sqlText.length() && sqlText.charAt(i + 1) == ':') {
                    continue;
                }
                // : 后跟标识符起始字符
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
     * 检测是否包含 mgxsql 的 LIKE 模式（%:name）
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
                // %: 后跟标识符
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
}
