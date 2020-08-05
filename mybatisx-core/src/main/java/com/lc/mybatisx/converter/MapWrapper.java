package com.lc.mybatisx.converter;

import com.google.common.base.CaseFormat;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Map;

/**
 * Map字段的驼峰命名
 *
 * @author cris
 */
public class MapWrapper extends org.apache.ibatis.reflection.wrapper.MapWrapper {

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
        }
        return name;
    }

}
