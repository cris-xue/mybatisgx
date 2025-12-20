package com.mybatisgx.api;

import org.apache.ibatis.reflection.MetaObject;

/**
 * 字段值处理上下文
 * @author 薛承城
 * @date 2025/12/20 17:38
 */
public interface ValueProcessContext {

    /**
     * 初始化上下文数据
     * @param phase
     * @param fieldMeta
     * @param originalValue
     * @param entityMetaObject
     */
    void init(ValueProcessPhase phase, FieldMeta fieldMeta, Object originalValue, MetaObject entityMetaObject);

    /**
     * 清理上下文数据
     */
    Object getFieldValueAndClear();

    /**
     * 获取数据处理阶段
     * @return
     */
    ValueProcessPhase getPhase();

    /**
     * 获取字段元信息
     * @return
     */
    FieldMeta getFieldMeta();

    /**
     * 获取当前处理字段值
     * @return
     */
    Object getFieldValue();

    /**
     * 保存当前处理字段值
     * @param fieldValue
     */
    void setFieldValue(Object fieldValue);

    /**
     * 获取实体对象中的字段值，支持多层级获取，如【user.name】
     * @param filedName
     * @return
     */
    Object getFieldValue(String filedName);
}
