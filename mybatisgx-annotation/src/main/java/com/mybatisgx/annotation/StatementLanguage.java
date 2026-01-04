package com.mybatisgx.annotation;

/**
 * Statement语言
 * @author 薛承城
 * @date 2025/11/11 11:36
 */
public enum StatementLanguage {

    /**
     * 方法名衍生（默认）: findByAaaAnd(bbbOrCcc)、updateByIdAndName(bbbOrCcc)
     */
    METHOD,
    /**
     * 对象语言：FROM User u WHERE u.name LIKE ?
     */
    // OBJECT
}
