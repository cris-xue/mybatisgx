package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.wrapper.ModelWrapper;
import org.springframework.beans.BeanUtils;

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
        PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(modelClass);

        List<ModelWrapper> modelWrapperList = new ArrayList<>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            ModelWrapper modelWrapper = new ModelWrapper();

            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            String fieldName = propertyDescriptor.getName();
            if ("class".equals(fieldName)) {
                continue;
            }
            modelWrapper.setDbColumn(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName));
            modelWrapper.setEntityColumn(fieldName);
            modelWrapperList.add(modelWrapper);
        }

        return modelWrapperList;
    }

    private static PropertyDescriptor[] getBeanPropertyList(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

}
