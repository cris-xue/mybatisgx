package com.mybatisgx.model;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2026/5/19 20:08
 */
public class LambdaAccessor {

    /**
     * 对象工厂
     */
    private ObjectFactory<Object> objectFactory;
    /**
     * 字段getter方法
     */
    private PropertyGetter<Object, Object> propertyGetter;
    /**
     * 字段getter方法
     */
    private PropertySetter<Object, Object> propertySetter;

    public ObjectFactory<Object> getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory<Object> objectFactory) {
        this.objectFactory = objectFactory;
    }

    public PropertyGetter<Object, Object> getPropertyGetter() {
        return propertyGetter;
    }

    public void setPropertyGetter(PropertyGetter<Object, Object> propertyGetter) {
        this.propertyGetter = propertyGetter;
    }

    public PropertySetter<Object, Object> getPropertySetter() {
        return propertySetter;
    }

    public void setPropertySetter(PropertySetter<Object, Object> propertySetter) {
        this.propertySetter = propertySetter;
    }

    public Object getValue(Object entity) {
        return propertyGetter.get(entity);
    }

    public void setValue(Object entity, Object value) {
        propertySetter.set(entity, value);
    }
}
