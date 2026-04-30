package com.mybatisgx.utils;

import com.google.common.base.CaseFormat;

public class FieldNameUtils {

    /**
     *
     * @param columnName
     * @return
     */
    public static String lowerCamelToUpperCamel(String columnName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, columnName);
    }

    /**
     * NameEq转nameEq
     * @param columnName
     * @return
     */
    public static String upperCamelToLowerCamel(String columnName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, columnName);
    }
}
