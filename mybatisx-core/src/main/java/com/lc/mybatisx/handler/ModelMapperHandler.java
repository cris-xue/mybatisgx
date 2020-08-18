package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;

import java.lang.reflect.Field;
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
        Field[] fields = ReflectUtils.getAllField(modelClass);
        int fieldLength = fields.length;

        List<ModelWrapper> modelWrapperList = new ArrayList<>();
        for (int i = 0; i < fieldLength; i++) {
            Field field = fields[i];
            String javaColumn = field.getName();
            if ("class".equals(javaColumn) || ignoreField(field)) {
                continue;
            }
            String dbColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn);

            ModelWrapper modelWrapper = new ModelWrapper();
            modelWrapper.setDbColumn(dbColumn);
            modelWrapper.setDbType("db_type");
            modelWrapper.setJavaColumn(javaColumn);
            modelWrapper.setJavaType("java_type");
            modelWrapperList.add(modelWrapper);
        }

        return modelWrapperList;
    }

    protected boolean ignoreField(Field field) {
        return false;
    }

}
