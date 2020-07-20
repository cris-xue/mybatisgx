package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.wrapper.ModelWrapper;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：模型处理器
 * @date ：2020/7/20 12:24
 */
public class ModelMapperHandler {

    public List<ModelWrapper> buildModelWrapper(PropertyDescriptor[] propertyDescriptors) {
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

}
