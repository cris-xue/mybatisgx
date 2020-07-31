package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：模型处理器
 * @date ：2020/7/20 12:24
 */
public abstract class ModelMapperHandler {

    public abstract Class<?> getModelClass(Method method, Class<?> entityClass);

    public List<ModelWrapper> buildModelWrapper(Class<?> modelClass) {
        PropertyDescriptor[] propertyDescriptors = ReflectUtils.getPropertyDescriptors(modelClass);

        List<ModelWrapper> modelWrapperList = new ArrayList<>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            String columnName = propertyDescriptor.getName();
            if ("class".equals(columnName)) {
                continue;
            }

            ModelWrapper modelWrapper = new ModelWrapper();
            modelWrapper.setDbColumn(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName));
            modelWrapper.setDbType("db_type");
            modelWrapper.setJavaColumn(columnName);
            modelWrapper.setJavaType("java_type");
            modelWrapperList.add(modelWrapper);
        }

        return modelWrapperList;
    }

}
