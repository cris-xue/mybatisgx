package com.mybatisgx.model;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/5 21:16
 */
@FunctionalInterface
public interface PropertySetter<T, V> {
    void set(T target, V value);
}