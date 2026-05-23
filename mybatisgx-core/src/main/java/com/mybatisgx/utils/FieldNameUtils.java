package com.mybatisgx.utils;

import com.google.common.base.CaseFormat;

public class FieldNameUtils {

    /**
     * orderColumn -> order_column
     * @param columnName
     * @return
     */
    public static String lowerCamelToLowerUnderscore(String columnName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
    }

    /**
     * order_column -> orderColumn
     * @param tableColumnName
     * @return
     */
    public static String lowerUnderscoreToLowerCamel(String tableColumnName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableColumnName);
    }

    /**
     * nameEq -> NameEq
     * @param columnName
     * @return
     */
    public static String lowerCamelToUpperCamel(String columnName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, columnName);
    }

    /**
     * NameEq -> nameEq
     * @param columnName
     * @return
     */
    public static String upperCamelToLowerCamel(String columnName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, columnName);
    }
}
