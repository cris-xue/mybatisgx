package com.mybatisgx.ext;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;

public class LinkObjectHelper {

    public static String getObjectKey(List<ResultMapping> idResultMappings, MetaObject metaObject) {
        if (metaObject == null || metaObject.getOriginalObject() == null || idResultMappings == null || idResultMappings.isEmpty()) {
            return "";
        }
        List<String> idValueList = new ArrayList(idResultMappings.size());
        for (ResultMapping idResultMapping : idResultMappings) {
            Object idValue = metaObject.getValue(idResultMapping.getProperty());
            if (idValue == null) {
                return "";
            }
            idValueList.add(String.valueOf(idValue));
        }
        return StringUtils.join(idValueList, "_");
    }
}
