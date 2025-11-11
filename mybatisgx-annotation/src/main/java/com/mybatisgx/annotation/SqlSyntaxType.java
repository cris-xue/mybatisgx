package com.mybatisgx.annotation;

/**
 * sql语法类型
 * @author 薛承城
 * @date 2025/11/11 11:36
 */
public enum SqlSyntaxType {

    HQL,           // HQL查询: FROM User u WHERE u.name LIKE ?
    METHOD_NAME    // 方法名衍生（默认）
}
