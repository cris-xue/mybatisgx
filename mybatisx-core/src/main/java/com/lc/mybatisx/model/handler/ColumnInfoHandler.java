package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ColumnInfoAnnotationInfo;
import com.lc.mybatisx.model.ForeignKeyColumnInfo;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnInfoHandler {

    private static final Map<String, String> typeMap = Maps.newHashMap();

    static {
        typeMap.put("", "");
    }

    public List<ColumnInfo> getColumnInfoList(Class<?> clazz) {
        Field[] fields = FieldUtils.getAllFields(clazz);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            Boolean isStatic = Modifier.isStatic(modifiers);
            if (isStatic) {
                continue;
            }

            Id id = field.getAnnotation(Id.class);
            Column column = field.getAnnotation(Column.class);
            Lock lock = field.getAnnotation(Lock.class);
            LogicDelete logicDelete = field.getAnnotation(LogicDelete.class);

            String fieldName = field.getName();
            String dbColumnName;
            if (column != null && StringUtils.isNotBlank(column.name())) {
                dbColumnName = column.name();
            } else {
                dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            }

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(dbColumnName);

            columnInfo.setId(id);
            columnInfo.setLock(lock);
            columnInfo.setLogicDelete(logicDelete);

            this.setGenerateValueHandler(field, columnInfo);
            this.setType(field, columnInfo);
            this.setColumnRelationInfo(field, columnInfo);

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    private void setType(Field field, ColumnInfo columnInfo) {
        Type genericType = field.getGenericType();
        Type fieldType = GenericUtils.getGenericType(genericType);
        Class<?> containerType = field.getType();
        if (fieldType instanceof Class) {
            columnInfo.setJavaType((Class<?>) fieldType);
        }
        if (containerType == List.class || containerType == Set.class) {
            columnInfo.setCollectionType(containerType);
        }
        TypeHandler typeHandler = field.getAnnotation(TypeHandler.class);
        if (typeHandler != null) {
            columnInfo.setTypeHandler(typeHandler.value().getTypeName());
        }
    }

    private void setGenerateValueHandler(Field field, ColumnInfo columnInfo) {
        try {
            GenerateValue generateValue = field.getAnnotation(GenerateValue.class);
            if (generateValue != null) {
                GenerateValueHandler generateValueHandler = generateValue.handler().newInstance();
                columnInfo.setGenerateValue(generateValue);
                columnInfo.setGenerateValueHandler(generateValueHandler);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setColumnRelationInfo(Field field, ColumnInfo columnInfo) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (!(oneToOne != null || oneToMany != null || manyToOne != null || manyToMany != null)) {
            return;
        }

        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
        JoinTable joinTable = field.getAnnotation(JoinTable.class);

        if (manyToMany != null && (StringUtils.isBlank(manyToMany.mappedBy()) && joinTable == null)) {
            throw new RuntimeException("many to many association must have mappedBy or joinTable");
        }

        List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList();
        List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = new ArrayList();
        if (joinColumn != null) {
            JoinColumn[] joinColumnList = new JoinColumn[]{joinColumn};
            inverseForeignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
        }
        if (joinColumns != null) {
            JoinColumn[] joinColumnList = joinColumns.value();
            inverseForeignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
        }
        if (joinTable != null) {
            JoinColumn[] joinColumnList = joinTable.joinColumns();
            foreignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
            JoinColumn[] inverseJoinColumnList = joinTable.inverseJoinColumns();
            inverseForeignKeyColumnInfoList = this.getForeignKeyList(inverseJoinColumnList);
        }

        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = new ColumnInfoAnnotationInfo();
        columnInfoAnnotationInfo.setOneToOne(oneToOne);
        columnInfoAnnotationInfo.setOneToMany(oneToMany);
        columnInfoAnnotationInfo.setManyToOne(manyToOne);
        columnInfoAnnotationInfo.setManyToMany(manyToMany);
        columnInfoAnnotationInfo.setJoinColumn(joinColumn);
        columnInfoAnnotationInfo.setJoinColumns(joinColumns);
        columnInfoAnnotationInfo.setJoinTable(joinTable);
        columnInfoAnnotationInfo.setForeignKeyColumnInfoList(foreignKeyColumnInfoList);
        columnInfoAnnotationInfo.setInverseForeignKeyColumnInfoList(inverseForeignKeyColumnInfoList);
        columnInfo.setColumnInfoAnnotationInfo(columnInfoAnnotationInfo);
    }

    private List<ForeignKeyColumnInfo> getForeignKeyList(JoinColumn[] joinColumnList) {
        List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList(5);
        for (JoinColumn joinColumn : joinColumnList) {
            Class<?> table = joinColumn.table();
            String name = joinColumn.name();
            String referencedColumnName = joinColumn.referencedColumnName();
            ForeignKeyColumnInfo foreignKeyColumnInfo = new ForeignKeyColumnInfo();
            foreignKeyColumnInfo.setName(name);
            foreignKeyColumnInfo.setReferencedColumnName(referencedColumnName);
            if (foreignKeyColumnInfo != null) {
                foreignKeyColumnInfoList.add(foreignKeyColumnInfo);
            }
        }
        return foreignKeyColumnInfoList;
    }
}
