package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
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

            this.setGenerateValueHandler(field, columnInfo);
            this.setType(field, columnInfo);
            this.setAssociationEntityInfo(field, columnInfo);

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    private void setType(Field field, ColumnInfo columnInfo) {
        Type genericType = field.getGenericType();
        Type fieldType = GenericUtils.getGenericType(genericType);
        Class<?> containerType = field.getType();

        columnInfo.setJavaType(fieldType);
        columnInfo.setJavaTypeName(fieldType.getTypeName());
        if (containerType == List.class || containerType == Set.class) {
            columnInfo.setContainerType(containerType);
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

    private void setAssociationEntityInfo(Field field, ColumnInfo columnInfo) {
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
        List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList();
        List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = new ArrayList();
        if (joinColumn != null) {
            JoinColumn[] joinColumnList = new JoinColumn[]{joinColumn};
            foreignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
        }
        if (joinColumns != null) {
            JoinColumn[] joinColumnList = joinColumns.value();
            foreignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
        }
        if (joinTable != null) {
            JoinColumn[] joinColumnList = joinTable.joinColumns();
            foreignKeyColumnInfoList = this.getForeignKeyList(joinColumnList);
            JoinColumn[] inverseJoinColumnList = joinTable.inverseJoinColumns();
            inverseForeignKeyColumnInfoList = this.getForeignKeyList(inverseJoinColumnList);
        }

        AssociationEntityInfo associationEntityInfo = new AssociationEntityInfo();
        associationEntityInfo.setOneToOne(oneToOne);
        associationEntityInfo.setOneToMany(oneToMany);
        associationEntityInfo.setManyToOne(manyToOne);
        associationEntityInfo.setManyToMany(manyToMany);
        associationEntityInfo.setJoinColumn(joinColumn);
        associationEntityInfo.setJoinColumns(joinColumns);
        associationEntityInfo.setJoinTable(joinTable);
        associationEntityInfo.setForeignKeyColumnInfoList(foreignKeyColumnInfoList);
        associationEntityInfo.setInverseForeignKeyColumnInfoList(inverseForeignKeyColumnInfoList);
        columnInfo.setAssociationEntityInfo(associationEntityInfo);
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

    public List<AssociationTableInfo> getAssociationTableInfoList(ColumnInfo columnInfo) {
        List<AssociationTableInfo> associationTableInfoList = new ArrayList();
        AssociationTableInfo associationTableInfo = null;
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        OneToOne oneToOne = associationEntityInfo.getOneToOne();
        if (oneToOne != null) {
            String mappedBy = oneToOne.mappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                EntityInfo targetEntityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo targetColumnInfo = targetEntityInfo.getColumnInfoMap().get(mappedBy);
                associationTableInfo = this.buildAssociationTableInfo(targetColumnInfo, targetColumnInfo.getJavaType(), columnInfo.getJavaType());
                JoinColumn joinColumn = targetColumnInfo.getAssociationEntityInfo().getJoinColumn();
                String name = joinColumn.name();
                String referencedColumnName = joinColumn.referencedColumnName();
                associationTableInfo.setForeignKey(name);
                associationTableInfo.setInverseForeignKey(referencedColumnName);
            } else {
                JoinColumn joinColumn = associationEntityInfo.getJoinColumn();
                if (joinColumn == null) {
                    throw new RuntimeException("关系维护方类：" + columnInfo.getJavaTypeName() + "字段：" + columnInfo.getJavaColumnName() + "不存在JoinColumn");
                }
                String name = joinColumn.name();
                String referencedColumnName = joinColumn.referencedColumnName();
                associationTableInfo = this.buildAssociationTableInfo(columnInfo, columnInfo.getJavaType(), columnInfo.getJavaType());
                associationTableInfo.setForeignKey(name);
                associationTableInfo.setInverseForeignKey(referencedColumnName);
            }
        }
        if (associationTableInfo != null) {
            associationTableInfoList.add(associationTableInfo);
        }
        return associationTableInfoList;
    }

    public List<AssociationTableInfo> getAssociationTableInfoList(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);
        List<AssociationTableInfo> associationTableInfoList = new ArrayList<>();
        for (Field field : fields) {
            AssociationTableInfo associationTableInfo = null;
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            JoinTable joinTable = field.getAnnotation(JoinTable.class);
            /*if (manyToMany != null) {
                if (joinTable == null) {

                } else {

                }
                Class<?> target = manyToMany.target();
                EntityInfo targetEntityInfo = EntityInfoContextHolder.get(target);
                ColumnInfo columnInfo = targetEntityInfo.getColumnInfoMap().get(manyToMany.mappedBy());
                JoinTable joinTable1 = columnInfo.getJoinTable();
                joinTable1.joinColumns();
                joinTable1.inverseJoinColumns();

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
            }*/

            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne != null) {
                String mappedBy = oneToOne.mappedBy();
                if (StringUtils.isNotBlank(mappedBy)) {
                    EntityInfo targetEntityInfo = EntityInfoContextHolder.get(null);
                    ColumnInfo columnInfo = targetEntityInfo.getColumnInfoMap().get(mappedBy);
                    JoinColumn joinColumn = null; // columnInfo.getJoinColumn();
                    String name = joinColumn.name();
                    String referencedColumnName = joinColumn.referencedColumnName();
                    associationTableInfo = this.buildAssociationTableInfo(field, columnInfo.getJavaType(), null);
                } else {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    String name = joinColumn.name();
                    String referencedColumnName = joinColumn.referencedColumnName();
                    associationTableInfo = this.buildAssociationTableInfo(field, (Class<?>) type, null);
                    associationTableInfo.setForeignKey(name);
                    associationTableInfo.setInverseForeignKey(referencedColumnName);
                }


                /*associationTableInfo = this.buildAssociationTableInfo(field, oneToOne.junctionEntity(), oneToOne.joinEntity());
                associationTableInfo.setForeignKey(oneToOne.foreignKeys()[0].name());*/
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

    private AssociationTableInfo buildAssociationTableInfo(ColumnInfo columnInfo, Class<?> junctionEntity, Class<?> joinEntity) {
        AssociationTableInfo associationTableInfo = new AssociationTableInfo();
        associationTableInfo.setJavaColumnName(columnInfo.getJavaColumnName());
        associationTableInfo.setJunctionEntity(junctionEntity);
        associationTableInfo.setJoinEntity(joinEntity);
        associationTableInfo.setJoinContainerType(columnInfo.getJavaType());
        associationTableInfo.setFetch(FetchType.LAZY);
        return associationTableInfo;
    }
}
