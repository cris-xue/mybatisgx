package com.lc.mybatisx.context;

/**
 * mybatisx上下文加载器
 *
 * @author ccxuef
 * @date 2025/7/3 12:17
 */
public class MethodStatementContextHolder {

    private static final ThreadLocal<String> METHOD_STATEMENT = new ThreadLocal();

    public String get() {
        return METHOD_STATEMENT.get();
    }

    public void set(String statement) {
        METHOD_STATEMENT.set(statement);
    }
}
