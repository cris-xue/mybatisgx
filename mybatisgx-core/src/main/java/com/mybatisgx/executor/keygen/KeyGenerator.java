package com.mybatisgx.executor.keygen;

/**
 * 主键生成器接口
 * @author 薛承城
 * @date 2025/12/20 18:14
 */
public interface KeyGenerator<T> {

    T get();
}
