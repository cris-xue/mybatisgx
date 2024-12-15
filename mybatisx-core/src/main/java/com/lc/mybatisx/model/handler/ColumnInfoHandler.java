package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.model.AssociationTableInfo;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.FetchType;
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
            String dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            dbColumnName = (column != null && StringUtils.isNotBlank(column.name())) ? column.name() : dbColumnName;

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(dbColumnName);
            columnInfo.setId(id);

            columnInfo.setLock(lock);
            columnInfo.setLogicDelete(logicDelete);

            this.setType(field, columnInfo);
            this.setAssociationInfo(field, columnInfo);

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    private void setType(Field field, ColumnInfo columnInfo) {
        TypeHandler typeHandler = field.getAnnotation(TypeHandler.class);
        Type genericType = field.getGenericType();
        Type fieldType = GenericUtils.getGenericType(genericType);
        Class<?> containerType = field.getType();

        columnInfo.setJavaType(fieldType);
        columnInfo.setJavaTypeName(fieldType.getTypeName());
        if (containerType == List.class || containerType == Set.class) {
            columnInfo.setContainerType(containerType);
        }
        if (typeHandler != null) {
            columnInfo.setTypeHandler(typeHandler.value().getTypeName());
        }
    }

    private void setAssociationInfo(Field field, ColumnInfo columnInfo) {
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        JoinTable joinTable = field.getAnnotation(JoinTable.class);

        columnInfo.setManyToMany(manyToMany);
        columnInfo.setManyToOne(manyToOne);
        columnInfo.setOneToOne(oneToOne);
        columnInfo.setOneToMany(oneToMany);
        columnInfo.setJoinColumn(joinColumn);
        columnInfo.setJoinTable(joinTable);
    }

    public List<AssociationTableInfo> getAssociationTableInfoList(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);
        List<AssociationTableInfo> associationTableInfoList = new ArrayList<>();
        for (Field field : fields) {
            AssociationTableInfo associationTableInfo = null;
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            if (manyToMany != null) {
                associationTableInfo = this.buildAssociationTableInfo(field, manyToMany.junctionEntity(), manyToMany.joinEntity());
                associationTableInfo.setForeignKey(manyToMany.foreignKeys()[0].name());
                associationTableInfo.setInverseForeignKey(manyToMany.inverseForeignKeys()[0].name());
            }

            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
            if (manyToOne != null) {
                associationTableInfo = this.buildAssociationTableInfo(field, manyToOne.junctionEntity(), manyToOne.joinEntity());
                associationTableInfo.setForeignKey(manyToOne.foreignKeys()[0].name());
            }

            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            if (oneToMany != null) {
                associationTableInfo = this.buildAssociationTableInfo(field, oneToMany.junctionEntity(), oneToMany.joinEntity());
                associationTableInfo.setForeignKey(oneToMany.foreignKeys()[0].name());
            }

            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne != null) {
                associationTableInfo = this.buildAssociationTableInfo(field, oneToOne.junctionEntity(), oneToOne.joinEntity());
                associationTableInfo.setForeignKey(oneToOne.foreignKeys()[0].name());
            }

            if (associationTableInfo != null) {
                associationTableInfoList.add(associationTableInfo);
            }
        }
        return associationTableInfoList;
    }

    private AssociationTableInfo buildAssociationTableInfo(Field field, Class<?> junctionEntity, Class<?> joinEntity) {
        AssociationTableInfo associationTableInfo = new AssociationTableInfo();
        associationTableInfo.setJavaColumnName(field.getName());
        associationTableInfo.setJunctionEntity(junctionEntity);
        associationTableInfo.setJoinEntity(joinEntity);
        associationTableInfo.setJoinContainerType(field.getType());
        associationTableInfo.setFetch(FetchType.LAZY);
        return associationTableInfo;
    }

}
