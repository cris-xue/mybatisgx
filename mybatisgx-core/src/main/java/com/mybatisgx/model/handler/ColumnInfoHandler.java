package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.mybatisgx.annotation.*;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段处理器
 * @author 薛承城
 * @date 2025/12/3 12:13
 */
public class ColumnInfoHandler {

    public List<ColumnInfo> getColumnInfoList(Class<?> clazz, Map<Type, Class<?>> typeParameterMap) {
        Field[] fields = FieldUtils.getAllFields(clazz);
        return this.processColumnInfo(null, fields, typeParameterMap);
    }

    private List<ColumnInfo> processColumnInfo(ColumnInfo parentColumnInfo, Field[] fields, Map<Type, Class<?>> typeParameterMap) {
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            Boolean isStatic = Modifier.isStatic(modifiers);
            if (isStatic) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            String fieldName = field.getName();
            String dbColumnName = this.getDbColumnName(field);

            ColumnInfo columnInfo = this.getColumnInfo(field);
            columnInfo.setParent(parentColumnInfo);
            columnInfo.setField(field);
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setJavaColumnNamePathList(this.getJavaColumnNamePathList(parentColumnInfo, columnInfo));
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(dbColumnName);

            columnInfo.setVersion(field.getAnnotation(Version.class));
            columnInfo.setLogicDelete(field.getAnnotation(LogicDelete.class));
            columnInfo.setGenerateValue(field.getAnnotation(GeneratedValue.class));

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

    private ColumnInfo getColumnInfo(Field field) {
        Id id = field.getAnnotation(Id.class);
        EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
        LogicDeleteId logicDeleteId = field.getAnnotation(LogicDeleteId.class);
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
        } else if (logicDeleteId != null) {
            LogicDeleteIdColumnInfo logicDeleteIdColumnInfo = new LogicDeleteIdColumnInfo();
            logicDeleteIdColumnInfo.setLogicDeleteId(logicDeleteId);
            return logicDeleteIdColumnInfo;
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
            idColumnInfoComposites = this.getColumnInfoList(idColumnInfo, type, typeParameterMap);
        }
        idColumnInfo.setId(id);
        idColumnInfo.setEmbeddedId(embeddedId);
        idColumnInfo.setComposites(idColumnInfoComposites);
    }

    private List<ColumnInfo> getColumnInfoList(ColumnInfo columnInfo, Type type, Map<Type, Class<?>> typeParameterMap) {
        if (type instanceof ParameterizedType) {
            // 处理复杂类型嵌套丢失真实类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Map<Type, Class<?>> childTypeParameterMap = new HashMap<>();
            Map<TypeVariable<?>, Type> typeMap = TypeUtils.getTypeArguments(parameterizedType);
            typeMap.forEach((typeVariable, typeValue) -> childTypeParameterMap.put(typeVariable, typeParameterMap.get(typeValue)));
            Field[] fields = FieldUtils.getAllFields((Class<?>) parameterizedType.getRawType());
            return this.processColumnInfo(columnInfo, fields, childTypeParameterMap);
        }
        if (type instanceof ParameterizedType) {
            Field[] fields = FieldUtils.getAllFields((Class<?>) type);
            return this.processColumnInfo(columnInfo, fields, typeParameterMap);
        }
        return new ArrayList<>();
    }

    private void setRelationColumnInfo(Field field, RelationColumnInfo relationColumnInfo) {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
        JoinTable joinTable = field.getAnnotation(JoinTable.class);
        Fetch fetch = field.getAnnotation(Fetch.class);
        if (StringUtils.isNotBlank(relationColumnInfo.getMappedBy())) {
            if (joinTable != null || joinColumns != null || joinColumn != null) {
                throw new MybatisgxException(
                        "%s字段已声明为关系反向方（mappedBy 不为空），不允许定义 JoinTable / JoinColumns / JoinColumn",
                        relationColumnInfo.getJavaColumnName()
                );
            }
        } else {
            if (relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY) {
                if (joinTable == null) {
                    throw new MybatisgxException("%s字段为多对多关系的维护方，必须使用 JoinTable 注解定义中间表", relationColumnInfo.getJavaColumnName());
                }
                if (joinColumns != null || joinColumn != null) {
                    throw new MybatisgxException("%s字段为多对多关系，不能直接使用 JoinColumn 或 JoinColumns，请通过 JoinTable 定义关联", relationColumnInfo.getJavaColumnName());
                }
                if (joinTable.joinColumns().length == 0) {
                    throw new MybatisgxException("%s字段的 JoinTable 必须定义 joinColumns（中间表指向当前实体的外键）", relationColumnInfo.getJavaColumnName());
                }
                if (joinTable.inverseJoinColumns().length == 0) {
                    throw new MybatisgxException("%s字段的 JoinTable 必须定义 inverseJoinColumns（中间表指向关联实体的外键）", relationColumnInfo.getJavaColumnName());
                }
            } else {
                if (joinTable != null) {
                    throw new MybatisgxException("%s字段不是多对多关系，不能使用 JoinTable 注解", relationColumnInfo.getJavaColumnName());
                }
                if (joinColumns == null && joinColumn == null) {
                    throw new MybatisgxException("%s字段为关系维护方，必须使用 JoinColumn 或 JoinColumns 定义外键", relationColumnInfo.getJavaColumnName());
                }
                if (joinColumns != null && joinColumn != null) {
                    throw new MybatisgxException("%s字段不能同时使用 JoinColumn 和 JoinColumns，请选择其中一种", relationColumnInfo.getJavaColumnName());
                }
                if (joinColumns != null && joinColumns.value().length == 0) {
                    throw new MybatisgxException("%s字段的 JoinColumns 至少需要定义一个 JoinColumn", relationColumnInfo.getJavaColumnName());
                }
            }
        }
        if (fetch == null) {
            throw new MybatisgxException("%s字段必须显式声明 Fetch 注解，用于指定关联加载时的抓取方式（如批量、单条等）", relationColumnInfo.getJavaColumnName());
        }

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
                if (relationColumnInfo.getRelationType() != RelationType.MANY_TO_MANY) {
                    for (ForeignKeyInfo inverseForeignKeyColumn : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(relationColumnInfo.getJavaType());
                        String referencedColumnName = inverseForeignKeyColumn.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getColumnInfo(referencedColumnName);
                        inverseForeignKeyColumn.setReferencedColumnInfo(referencedColumnInfo);
                    }
                } else {
                    for (ForeignKeyInfo foreignKeyColumnInfo : relationColumnInfo.getForeignKeyInfoList()) {
                        String referencedColumnName = foreignKeyColumnInfo.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = entityInfo.getColumnInfo(referencedColumnName);
                        foreignKeyColumnInfo.setReferencedColumnInfo(referencedColumnInfo);
                    }

                    for (ForeignKeyInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(relationColumnInfo.getJavaType());
                        String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                        ColumnInfo referencedColumnInfo = relationColumnEntityInfo.getDbColumnInfo(referencedColumnName);
                        inverseForeignKeyColumnInfo.setReferencedColumnInfo(referencedColumnInfo);
                    }
                }
            }
        }
    }

    private List<String> getJavaColumnNamePathList(ColumnInfo parentColumnInfo, ColumnInfo columnInfo) {
        List<String> javaColumnNamePathList = new ArrayList<>();
        if (parentColumnInfo != null) {
            javaColumnNamePathList.addAll(parentColumnInfo.getJavaColumnNamePathList());
        }
        javaColumnNamePathList.add(columnInfo.getJavaColumnName());
        return javaColumnNamePathList;
    }

    private ColumnInfo validateEntityRelation(RelationColumnInfo relationColumnInfo, String mappedBy) {
        Class<?> javaType = relationColumnInfo.getJavaType();
        EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);
        if (relationColumnEntityInfo == null) {
            throw new MybatisgxException("实体类 %s 不存在", javaType.getTypeName());
        }
        ColumnInfo mappedByRelationColumnInfo = relationColumnEntityInfo.getColumnInfo(mappedBy);
        if (mappedByRelationColumnInfo == null) {
            throw new MybatisgxException("实体类 %s 不存在 %s 字段", javaType.getTypeName(), mappedBy);
        }
        return mappedByRelationColumnInfo;
    }
}
