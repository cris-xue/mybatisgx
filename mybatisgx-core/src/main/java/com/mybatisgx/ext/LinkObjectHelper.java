package com.mybatisgx.ext;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;

public class LinkObjectHelper {

    public static String getObjectKey(List<ResultMapping> idResultMappings, MetaObject metaObject) {
        if (metaObject.getOriginalObject() == null) {
            return "";
        }
        List<String> idValueList = new ArrayList<>();
        for (ResultMapping idResultMapping : idResultMappings) {
            Object idValue = metaObject.getValue(idResultMapping.getProperty());
            String idValueString;
            if (idValue instanceof Integer) {
                idValueString = idValue.toString();
            } else if (idValue instanceof Long) {
                idValueString = idValue.toString();
            } else {
                idValueString = (String) idValue;
            }
            idValueList.add(idValueString);
        }
        return StringUtils.join(idValueList, "");
    }
}
