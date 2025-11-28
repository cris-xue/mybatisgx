package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.mybatisgx.annotation.*;
import com.mybatisgx.annotation.handler.GenerateValueHandler;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnInfoHandler {

    private List<ColumnInfo> getColumnInfoList(Type type, Map<Type, Class<?>> typeParameterMap) {
        if (type instanceof ParameterizedType) {
            // 处理复杂类型嵌套丢失真实类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Map<Type, Class<?>> childTypeParameterMap = new HashMap<>();
            Map<TypeVariable<?>, Type> typeMap = TypeUtils.getTypeArguments(parameterizedType);
            typeMap.forEach((typeVariable, typeValue) -> childTypeParameterMap.put(typeVariable, typeParameterMap.get(typeValue)));
            Field[] fields = FieldUtils.getAllFields((Class<?>) parameterizedType.getRawType());
            return this.processColumnInfo(fields, childTypeParameterMap);
        }
        if (type instanceof ParameterizedType) {
            Field[] fields = FieldUtils.getAllFields((Class<?>) type);
            return this.processColumnInfo(fields, typeParameterMap);
        }
        return new ArrayList<>();
    }

    public List<ColumnInfo> getColumnInfoList(Class<?> clazz, Map<Type, Class<?>> typeParameterMap) {
        Field[] fields = FieldUtils.getAllFields(clazz);
        return this.processColumnInfo(fields, typeParameterMap);
    }

    private List<ColumnInfo> processColumnInfo(Field[] fields, Map<Type, Class<?>> typeParameterMap) {
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
            this.processColumnType(field, columnInfo, typeParameterMap);
            if (columnInfo instanceof IdColumnInfo) {
                this.setIdColumnInfo(field, (IdColumnInfo) columnInfo, typeParameterMap);
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

    private void processColumnType(Field field, ColumnInfo columnInfo, Map<Type, Class<?>> typeParameterMap) {
        Type type = field.getGenericType();
        this.processColumnType(type, columnInfo, typeParameterMap);
        TypeHandler typeHandler = field.getAnnotation(TypeHandler.class);
        if (typeHandler != null) {
            columnInfo.setTypeHandler(typeHandler.value().getTypeName());
        }
    }

    /**
     * 处理字段类型
     *
     * @param type
     * @param columnInfo
     * @param typeParameterMap
     */
    private void processColumnType(Type type, ColumnInfo columnInfo, Map<Type, Class<?>> typeParameterMap) {
        Class<?> collectionType = null;
        Class<?> javaType = null;
        if (type instanceof TypeVariable) {
            javaType = typeParameterMap.get(type);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            collectionType = (Class<?>) TypeUtils.getCollectionType(parameterizedType);
            if (collectionType != null) {
                javaType = (Class<?>) TypeUtils.getActualType(parameterizedType);
            } else {
                javaType = (Class<?>) TypeUtils.getRawType(parameterizedType);
            }
        }
        if (type instanceof Class) {
            javaType = (Class<?>) type;
        }

        if (collectionType != null) {
            columnInfo.setCollectionType(collectionType);
        }
        if (javaType != null) {
            columnInfo.setJavaType(javaType);
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

    private void setIdColumnInfo(Field field, IdColumnInfo idColumnInfo, Map<Type, Class<?>> typeParameterMap) {
        Id id = field.getAnnotation(Id.class);
        EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
        List<ColumnInfo> idColumnInfoComposites = new ArrayList();
        if (embeddedId != null) {
            Type type = field.getGenericType();
            idColumnInfoComposites = this.getColumnInfoList(type, typeParameterMap);
        }
        idColumnInfo.setId(id);
        idColumnInfo.setEmbeddedId(embeddedId);
        idColumnInfo.setComposites(idColumnInfoComposites);
    }

    private void setRelationColumnInfo(Field field, RelationColumnInfo relationColumnInfo) {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
        JoinTable joinTable = field.getAnnotation(JoinTable.class);
        Fetch fetch = field.getAnnotation(Fetch.class);

        List<ForeignKeyInfo> foreignKeyColumnInfoList = new ArrayList();
        List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = new ArrayList();
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

        relationColumnInfo.setJoinColumn(joinColumn);
        relationColumnInfo.setJoinColumns(joinColumns);
        relationColumnInfo.setJoinTable(joinTable);
        relationColumnInfo.setFetch(fetch);
        relationColumnInfo.setForeignKeyInfoList(foreignKeyColumnInfoList);
        relationColumnInfo.setInverseForeignKeyInfoList(inverseForeignKeyColumnInfoList);
    }

    private List<ForeignKeyInfo> getForeignKeyList(JoinColumn[] joinColumnList) {
        List<ForeignKeyInfo> foreignKeyColumnInfoList = new ArrayList(5);
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
            ForeignKeyInfo foreignKeyColumnInfo = new ForeignKeyInfo(columnInfo, referencedColumnName);
            foreignKeyColumnInfoList.add(foreignKeyColumnInfo);
        }
        return foreignKeyColumnInfoList;
    }

    public void processRelation(EntityInfo entityInfo) {
        for (RelationColumnInfo relationColumnInfo : entityInfo.getRelationColumnInfoList()) {
            String mappedBy = relationColumnInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByRelationColumnInfo = this.validateEntityRelation(relationColumnInfo, mappedBy);
                relationColumnInfo.setMappedByRelationColumnInfo((RelationColumnInfo) mappedByRelationColumnInfo);
            } else {
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType != RelationType.MANY_TO_MANY) {
                    List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
                    for (ForeignKeyInfo inverseForeignKeyColumn : inverseForeignKeyColumnInfoList) {
                        Class<?> javaType = relationColumnInfo.getJavaType();
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);

                        String referencedColumnName = inverseForeignKeyColumn.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getColumnInfo(referencedColumnName);
                        inverseForeignKeyColumn.setReferencedColumnInfo(referencedColumnInfo);

                        // 补充外键字段信息
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumn.getColumnInfo();
                        // foreignKeyColumnInfo.setJavaType(referencedColumnInfo.getJavaType());
                        // foreignKeyColumnInfo.setDbTypeName(referencedColumnInfo.getDbTypeName());
                    }
                } else {
                    List<ForeignKeyInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyInfoList();
                    for (ForeignKeyInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                        Class<?> javaType = relationColumnInfo.getJavaType();
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);

                        String referencedColumnName = foreignKeyColumnInfo.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getDbColumnInfo(referencedColumnName);
                        foreignKeyColumnInfo.setReferencedColumnInfo(referencedColumnInfo);
                    }

                    List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
                    for (ForeignKeyInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
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
