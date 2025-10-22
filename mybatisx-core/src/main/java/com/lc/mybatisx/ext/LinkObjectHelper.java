package com.lc.mybatisx.ext;

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
            idValueList.add(idValue instanceof Long ? idValue.toString() : (String) idValue);
        }
        return StringUtils.join(idValueList, "");
    }
}
