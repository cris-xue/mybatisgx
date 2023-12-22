package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.YesOrNo;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ColumnInfoHandler {

    public List<ColumnInfo> getColumnInfoList(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            String dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            Column column = field.getAnnotation(Column.class);
            Id id = field.getAnnotation(Id.class);

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaType(fieldType);
            columnInfo.setJavaTypeName(fieldType.getTypeName());
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(null);
            columnInfo.setDbColumnName(column != null ? column.name() : dbColumnName);
            columnInfo.setPrimaryKey(id != null ? YesOrNo.YES : YesOrNo.NO);
            columnInfo.setTypeHandler(null);

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

}
