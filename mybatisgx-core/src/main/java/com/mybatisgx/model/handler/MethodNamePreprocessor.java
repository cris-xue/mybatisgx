package com.mybatisgx.model.handler;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.utils.FieldNameUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方法名预处理器：在 ANTLR 解析之前，自动转义有歧义的字段名
 * <p>
 * 核心原则：只在无歧义时自动转义，有歧义时保持现状（需要用户用 @Property 或 $...$ 显式声明）。
 * @author luozhan
 * @since 2026-06-29
 */
public class MethodNamePreprocessor {

    /**
     * ANTLR 词法层所有可能与字段名尾部冲突的关键字，按长度降序排列以优先匹配最长后缀
     */
    private static final Set<String> DSL_KEYWORDS = new LinkedHashSet<>(Arrays.asList(
            "StartingWith", "EndingWith",
            "IsNotNull", "IsNull", "NotNull",
            "Between", "Equal",
            "Lteq", "Gteq",
            "Like", "In", "Lt", "Gt", "Eq",
            "Not", "And", "Or"
    ));

    private static final Pattern BY_PATTERN = Pattern.compile("By(?=[A-Z(])");

    /**
     * 对方法名中无歧义的冲突字段自动添加 $...$ 转义。
     * <p>
     * 例：实体有 nameLike 但无 name 时，"findByNameLikeAndAge" → "findBy$NameLike$AndAge"
     */
    public static String escape(EntityInfo entityInfo, String methodName) {
        if (entityInfo == null || methodName == null) {
            return methodName;
        }
        // 已包含手动转义标记，跳过处理
        if (methodName.contains("$")) {
            return methodName;
        }

        List<AmbiguousField> ambiguousFields = findAmbiguousFields(entityInfo);
        if (ambiguousFields.isEmpty()) {
            return methodName;
        }

        String conditionPart = extractConditionPart(methodName);
        if (conditionPart == null) {
            return methodName;
        }

        int conditionStart = methodName.length() - conditionPart.length();
        String escapedCondition = escapeConditionPart(conditionPart, ambiguousFields);

        if (escapedCondition.equals(conditionPart)) {
            return methodName;
        }

        return methodName.substring(0, conditionStart) + escapedCondition;
    }

    /**
     * 扫描实体字段，找出尾部与 DSL 关键字冲突且无歧义的字段。
     * <p>
     * 判断标准：字段 nameLike 以 "Like" 结尾，截断后得到 "name"，若 name 不存在于实体中则为无歧义冲突字段。
     */
    static List<AmbiguousField> findAmbiguousFields(EntityInfo entityInfo) {
        List<AmbiguousField> result = new ArrayList<>();

        for (ColumnInfo columnInfo : entityInfo.getColumnInfoList()) {
            String fieldName = columnInfo.getJavaColumnName();
            String upperCamel = FieldNameUtils.lowerCamelToUpperCamel(fieldName);

            for (String keyword : DSL_KEYWORDS) {
                if (upperCamel.endsWith(keyword) && upperCamel.length() > keyword.length()) {
                    String shorterFieldUpper = upperCamel.substring(0, upperCamel.length() - keyword.length());
                    String shorterField = FieldNameUtils.upperCamelToLowerCamel(shorterFieldUpper);

                    // 截断字段不存在 → 无歧义，可以安全转义
                    if (entityInfo.getColumnInfo(shorterField) == null) {
                        result.add(new AmbiguousField(upperCamel, fieldName));
                    }
                    break;
                }
            }
        }

        // 按长度降序，优先替换更长的字段名避免短名误匹配
        result.sort((a, b) -> b.upperCamelName.length() - a.upperCamelName.length());
        return result;
    }

    /**
     * 提取 By 之后的条件部分，如 "findCustomByNameAndAge" → "NameAndAge"
     */
    static String extractConditionPart(String methodName) {
        Matcher matcher = BY_PATTERN.matcher(methodName);
        if (matcher.find()) {
            int byEnd = matcher.end();
            return methodName.substring(byEnd);
        }
        return null;
    }

    private static String escapeConditionPart(String condition, List<AmbiguousField> ambiguousFields) {
        for (AmbiguousField field : ambiguousFields) {
            condition = replaceFieldOccurrences(condition, field.upperCamelName);
        }
        return condition;
    }

    /**
     * 逐字符扫描条件字符串，将目标字段包裹为 $...$ 形式，同时跳过已转义区域
     */
    private static String replaceFieldOccurrences(String condition, String fieldUpperCamel) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < condition.length()) {
            // 跳过已有的 $...$ 转义区域
            if (condition.charAt(i) == '$') {
                int end = condition.indexOf('$', i + 1);
                if (end != -1) {
                    result.append(condition, i, end + 1);
                    i = end + 1;
                    continue;
                }
            }

            int matchIndex = condition.indexOf(fieldUpperCamel, i);
            if (matchIndex == i) {
                boolean validBoundary = isValidFieldBoundary(condition, matchIndex, fieldUpperCamel.length());
                if (validBoundary) {
                    result.append('$').append(fieldUpperCamel).append('$');
                    i += fieldUpperCamel.length();
                } else {
                    result.append(condition.charAt(i));
                    i++;
                }
            } else {
                result.append(condition.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

    /**
     * 判断匹配位置之后是否为有效的字段边界（大写字母、括号或字符串末尾）
     */
    private static boolean isValidFieldBoundary(String condition, int matchStart, int matchLen) {
        int afterMatch = matchStart + matchLen;
        if (afterMatch >= condition.length()) {
            return true;
        }
        char nextChar = condition.charAt(afterMatch);
        return Character.isUpperCase(nextChar) || nextChar == '(' || nextChar == ')';
    }

    static class AmbiguousField {
        final String upperCamelName;
        final String javaFieldName;

        AmbiguousField(String upperCamelName, String javaFieldName) {
            this.upperCamelName = upperCamelName;
            this.javaFieldName = javaFieldName;
        }
    }
}
