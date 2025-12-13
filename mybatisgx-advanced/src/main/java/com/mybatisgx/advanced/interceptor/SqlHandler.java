package com.mybatisgx.advanced.interceptor;

/**
 * sql处理器
 * @author 薛承城
 * @date 2025/11/13 18:10
 */
public interface SqlHandler {

    void process(MybatisgxExecutorInfo mybatisgxExecutorInfo);
}
