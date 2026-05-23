package com.mybatisgx.model;

@FunctionalInterface
public interface ObjectFactory<T> {
    T create();
}
