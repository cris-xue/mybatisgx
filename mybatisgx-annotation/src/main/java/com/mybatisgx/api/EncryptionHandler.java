package com.mybatisgx.api;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/10 19:19
 */
public interface EncryptionHandler {

    Object insert(Object columnInfo, Object originalValue);

    Object update(Object columnInfo, Object originalValue);
}
