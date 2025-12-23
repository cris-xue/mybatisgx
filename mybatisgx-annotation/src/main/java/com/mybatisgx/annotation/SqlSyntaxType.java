package com.mybatisgx.annotation;

/**
 * sql语法类型
 * @author 薛承城
 * @date 2025/11/11 11:36
 */
public enum SqlSyntaxType {

    /**
     * 方法名衍生（默认）
     */
    METHOD_NAME,
    /**
     * 对象语言：FROM User u WHERE u.name LIKE ?
     */
    // OBJECT_LANGUAGE
}
