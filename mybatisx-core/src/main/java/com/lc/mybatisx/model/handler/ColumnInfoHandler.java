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

import java.lang.reflect.*;
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
        Map<String, Class<?>> typeParameterMap = GenericUtils.getTypeParameterMap(clazz);
        Field[] fields = FieldUtils.getAllFields(clazz);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            Boolean isStatic = Modifier.isStatic(modifiers);
            if (isStatic) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            Lock lock = field.getAnnotation(Lock.class);
            LogicDelete logicDelete = field.getAnnotation(LogicDelete.class);

            String fieldName = field.getName();
            String dbColumnName = this.getDbColumnName(field);

            ColumnInfo columnInfo = this.getColumnInfo(field);
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(dbColumnName);

            columnInfo.setLock(lock);
            columnInfo.setLogicDelete(logicDelete);

            this.setGenerateValueHandler(field, columnInfo);
            this.setType(field, columnInfo, typeParameterMap);
            if (columnInfo instanceof IdColumnInfo) {
                this.setIdColumnInfo(field, (IdColumnInfo) columnInfo);
            }
            if (columnInfo instanceof RelationColumnInfo) {
                this.setRelationColumnInfo(field, (RelationColumnInfo) columnInfo);
            }

            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    private String getDbColumnName(Field field) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);

        String dbColumnName = "";
        if (oneToOne != null || oneToMany != null || manyToOne != null) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                dbColumnName = joinColumn.name();
            }
        } else if (manyToMany != null) {

        } else {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (StringUtils.isNotBlank(column.name())) {
                    dbColumnName = column.name();
                }
            } else {
                String fieldName = field.getName();
                dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            }
        }
        return dbColumnName;
    }

    private void setType(Field field, ColumnInfo columnInfo, Map<String, Class<?>> typeParameterMap) {
        Type genericType = field.getGenericType();
        Class<?> fieldType = field.getType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            genericType = parameterizedType.getRawType();
        }
        if (genericType instanceof TypeVariable) {
            genericType = typeParameterMap.get(genericType.getTypeName());
        }
        if (genericType instanceof Class) {
            columnInfo.setJavaType((Class<?>) genericType);
        }
        if (fieldType == List.class || fieldType == Set.class) {
            columnInfo.setCollectionType(fieldType);
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

    private ColumnInfo getColumnInfo(Field field) {
        Id id = field.getAnnotation(Id.class);
        EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
        Column column = field.getAnnotation(Column.class);
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (oneToOne != null || oneToMany != null || manyToOne != null || manyToMany != null) {
            RelationColumnInfo relationColumnInfo = new RelationColumnInfo();
            relationColumnInfo.setOneToOne(oneToOne);
            relationColumnInfo.setOneToMany(oneToMany);
            relationColumnInfo.setManyToOne(manyToOne);
            relationColumnInfo.setManyToMany(manyToMany);
            relationColumnInfo.setColumn(column);
            return relationColumnInfo;
        } else if (id != null || embeddedId != null) {
            IdColumnInfo idColumnInfo = new IdColumnInfo();
            idColumnInfo.setId(id);
            idColumnInfo.setEmbeddedId(embeddedId);
            return idColumnInfo;
        } else {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumn(column);
            return columnInfo;
        }
    }

    private void setIdColumnInfo(Field field, IdColumnInfo idColumnInfo) {
        Id id = field.getAnnotation(Id.class);
        EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
        List<ColumnInfo> columnInfoList = new ArrayList();
        if (embeddedId != null) {
            Class<?> javaType = idColumnInfo.getJavaType();
            columnInfoList = this.getColumnInfoList(javaType);
        }
        idColumnInfo.setId(id);
        idColumnInfo.setEmbeddedId(embeddedId);
        idColumnInfo.setColumnInfoList(columnInfoList);
    }

    private void setRelationColumnInfo(Field field, RelationColumnInfo relationColumnInfo) {
        /*OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (!(oneToOne != null || oneToMany != null || manyToOne != null || manyToMany != null)) {
            return;
        }*/

        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
        JoinTable joinTable = field.getAnnotation(JoinTable.class);
        Fetch fetch = field.getAnnotation(Fetch.class);

        /*if (manyToMany != null && (StringUtils.isBlank(manyToMany.mappedBy()) && joinTable == null)) {
            throw new RuntimeException("many to many association must have mappedBy or joinTable");
        }*/

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

        // ColumnRelationInfo columnRelationInfo = new ColumnRelationInfo();
        /*relationColumnInfo.setOneToOne(oneToOne);
        relationColumnInfo.setOneToMany(oneToMany);
        relationColumnInfo.setManyToOne(manyToOne);
        relationColumnInfo.setManyToMany(manyToMany);*/
        relationColumnInfo.setJoinColumn(joinColumn);
        relationColumnInfo.setJoinColumns(joinColumns);
        relationColumnInfo.setJoinTable(joinTable);
        relationColumnInfo.setFetch(fetch);
        relationColumnInfo.setForeignKeyColumnInfoList(foreignKeyColumnInfoList);
        relationColumnInfo.setInverseForeignKeyColumnInfoList(inverseForeignKeyColumnInfoList);
    }

    private List<ForeignKeyColumnInfo> getForeignKeyList(JoinColumn[] joinColumnList) {
        List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList(5);
        for (JoinColumn joinColumn : joinColumnList) {
            String name = joinColumn.name();
            // orderColumn、order_column -> order_column
            String dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
            // order_column -> orderColumn
            String javaColumnName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, dbColumnName);

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaColumnName(javaColumnName);
            columnInfo.setDbColumnName(dbColumnName);

            String referencedColumnName = joinColumn.referencedColumnName();

            ForeignKeyColumnInfo foreignKeyColumnInfo = new ForeignKeyColumnInfo(columnInfo, referencedColumnName);
            foreignKeyColumnInfoList.add(foreignKeyColumnInfo);
        }
        return foreignKeyColumnInfoList;
    }

    public void processRelation(EntityInfo entityInfo) {
        List<ColumnInfo> relationColumnInfoList = entityInfo.getRelationColumnInfoList();
        for (ColumnInfo columnInfo : relationColumnInfoList) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
            String mappedBy = relationColumnInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByRelationColumnInfo = this.validateEntityRelation(relationColumnInfo, mappedBy);
                relationColumnInfo.setMappedByRelationColumnInfo((RelationColumnInfo) mappedByRelationColumnInfo);
            } else {
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType != RelationType.MANY_TO_MANY) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumn : inverseForeignKeyColumnInfoList) {
                        Class<?> javaType = relationColumnInfo.getJavaType();
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);

                        String referencedColumnName = inverseForeignKeyColumn.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getDbColumnInfo(referencedColumnName);
                        inverseForeignKeyColumn.setReferencedColumnInfo(referencedColumnInfo);

                        // 补充外键字段信息
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumn.getColumnInfo();
                        // foreignKeyColumnInfo.setJavaType(referencedColumnInfo.getJavaType());
                        // foreignKeyColumnInfo.setDbTypeName(referencedColumnInfo.getDbTypeName());
                    }
                } else {
                    List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                        Class<?> javaType = relationColumnInfo.getJavaType();
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);

                        String referencedColumnName = foreignKeyColumnInfo.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getDbColumnInfo(referencedColumnName);
                        foreignKeyColumnInfo.setReferencedColumnInfo(referencedColumnInfo);
                    }

                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        Class<?> javaType = relationColumnInfo.getJavaType();
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);

                        String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getDbColumnInfo(referencedColumnName);
                        inverseForeignKeyColumnInfo.setReferencedColumnInfo(referencedColumnInfo);
                    }
                }
            }
        }
    }

    private ColumnInfo validateEntityRelation(RelationColumnInfo relationColumnInfo, String mappedBy) {
        Class<?> javaType = relationColumnInfo.getJavaType();
        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);
        if (relationColumnEntityInfo == null) {
            throw new RuntimeException("实体类" + javaType + "不存在");
        }
        ColumnInfo mappedByRelationColumnInfo = relationColumnEntityInfo.getColumnInfo(mappedBy);
        if (mappedByRelationColumnInfo == null) {
            throw new RuntimeException("实体类" + javaType + "不存在" + mappedBy + "字段");
        }
        return mappedByRelationColumnInfo;
    }
}
