package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ManyToManyInfo;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColumnInfoHandler {

    private static final Map<String, String> typeMap = Maps.newHashMap();

    static {
        typeMap.put("", "");
    }

    public List<ColumnInfo> getColumnInfoList(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : fields) {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            if (manyToMany != null) {
                continue;
            }

            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            String dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            Id id = field.getAnnotation(Id.class);
            Column column = field.getAnnotation(Column.class);
            TypeHandler typeHandler = field.getAnnotation(TypeHandler.class);
            Lock lock = field.getAnnotation(Lock.class);
            LogicDelete logicDelete = field.getAnnotation(LogicDelete.class);

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaType(fieldType);
            columnInfo.setJavaTypeName(fieldType.getTypeName());
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(column != null ? column.name() : dbColumnName);
            columnInfo.setId(id);
            if (typeHandler != null) {
                columnInfo.setTypeHandler(typeHandler.value().getTypeName());
            }
            columnInfo.setLock(lock);
            columnInfo.setDelete(logicDelete);

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    public List<ManyToManyInfo> getAssociationTableInfoList(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);
        List<ManyToManyInfo> manyToManyInfoList = new ArrayList<>();
        for (Field field : fields) {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            if (manyToMany == null) {
                continue;
            }
            ManyToManyInfo manyToManyInfo = new ManyToManyInfo();
            manyToManyInfo.setType("");
            manyToManyInfo.setTargetEntity(manyToMany.targetEntity());
            manyToManyInfo.setAssociationEntity(manyToMany.associationEntity());
            manyToManyInfo.setForeignKey(manyToMany.foreignKey());
            manyToManyInfo.setInverseForeignKey(manyToMany.inverseForeignKey());
            manyToManyInfo.setFetch(manyToMany.fetch());
            manyToManyInfoList.add(manyToManyInfo);
        }
        return manyToManyInfoList;
    }

}
