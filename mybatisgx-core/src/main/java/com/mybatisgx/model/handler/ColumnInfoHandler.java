package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.*;
import com.mybatisgx.context.DaoMethodManager;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.spi.ValueProcessor;
import com.mybatisgx.utils.BeanMethodUtils;
import com.mybatisgx.utils.FieldNameUtils;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字段处理器
 *
 * @author 薛承城
 * @date 2025/12/3 12:13
 */
public class ColumnInfoHandler {

    private TypeResolver typeResolver = new TypeResolver();

    public List<ColumnInfo> getColumnInfoList(Class<?> clazz, Map<Type, Class<?>> typeParameterMap) {
        return this.processColumnInfo(clazz, null, typeParameterMap);
    }

    private List<ColumnInfo> processColumnInfo(Class<?> clazz, ColumnInfo parentColumnInfo, Map<Type, Class<?>> typeParameterMap) {
        Map<String, PropertyDescriptor> getterMethodMap = BeanMethodUtils.getGetterMethodMap(clazz);
        Map<String, PropertyDescriptor> setterMethodMap = BeanMethodUtils.getSetterMethodMap(clazz);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        for (Field field : FieldUtils.getAllFields(clazz)) {
            int modifiers = field.getModifiers();
            Boolean isStatic = Modifier.isStatic(modifiers);
            if (isStatic) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            String fieldName = field.getName();
            String tableColumnName = this.getTableColumnName(field);

            ColumnInfo columnInfo = this.getColumnInfo(field);
            columnInfo.setParent(parentColumnInfo);
            columnInfo.setField(field);
            columnInfo.setJavaColumnName(fieldName);
            columnInfo.setDbTypeName(column != null ? column.columnDefinition() : null);
            columnInfo.setDbColumnName(tableColumnName);

            columnInfo.setProperty(field.getAnnotation(Property.class));
            columnInfo.setQueryColumn(field.getAnnotation(QueryColumn.class));
            columnInfo.setVersion(field.getAnnotation(Version.class));
            columnInfo.setLogicDelete(field.getAnnotation(LogicDelete.class));

            this.processGenerateValue(field, columnInfo);
            this.processJavaColumnChain(parentColumnInfo, columnInfo);
            this.processColumnType(field, columnInfo, typeParameterMap);
            this.processPropertyMethod(getterMethodMap, setterMethodMap, columnInfo);

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

    private String getTableColumnName(Field field) {
        String tableColumnName = "";
        if (!isGenerateTableColumnName(field)) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (StringUtils.isNotBlank(column.name())) {
                    tableColumnName = column.name();
                }
            } else {
                String fieldName = field.getName();
                tableColumnName = FieldNameUtils.lowerCamelToLowerUnderscore(fieldName);
            }
        }
        return tableColumnName;
    }

    private boolean isGenerateTableColumnName(Field field) {
        return field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(ManyToMany.class)
                || field.isAnnotationPresent(Transient.class);
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
        TypeCategory typeCategory = this.typeResolver.getCategory(type);
        columnInfo.setTypeCategory(typeCategory);
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

        for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
            idColumnInfo.addColumnInfo(idColumnInfoComposite);
        }
    }

    private void processGenerateValue(Field field, ColumnInfo columnInfo) {
        GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        if (generatedValue != null) {
            // 校验生成值类型是否为 ValueProcessor 类型
            Class<?>[] generatedValueClassList = generatedValue.value();
            for (Class<?> generatedValueClass : generatedValueClassList) {
                if (!ValueProcessor.class.isAssignableFrom(generatedValueClass)) {
                    throw new MybatisgxException(
                            "字段 %s 值处理类型为 %s，预期为 %s",
                            columnInfo.getJavaColumnName(),
                            generatedValueClass.getName(),
                            ValueProcessor.class.getName()
                    );
                }
            }
            ValueProcessor[] valueProcessors = this.getValueProcessors(generatedValue.value());
            columnInfo.setValueProcessors(valueProcessors);
            columnInfo.setGenerateValue(generatedValue);
        }
    }

    private ValueProcessor[] getValueProcessors(Class<?>[] generatedValues) {
        ValueProcessor[] valueProcessors = new ValueProcessor[generatedValues.length];
        try {
            for (int i = 0; i < generatedValues.length; i++) {
                Class<ValueProcessor> generatedValueClass = (Class<ValueProcessor>) generatedValues[i];
                ValueProcessor valueProcessor = DaoMethodManager.get(generatedValueClass);
                if (valueProcessor == null) {
                    valueProcessor = generatedValueClass.newInstance();
                    DaoMethodManager.register(generatedValueClass);
                }
                valueProcessors[i] = valueProcessor;
            }
        } catch (InstantiationException e) {
            throw new MybatisgxException(e);
        } catch (IllegalAccessException e) {
            throw new MybatisgxException(e);
        }
        return valueProcessors;
    }

    private List<ColumnInfo> getColumnInfoList(ColumnInfo columnInfo, Type type, Map<Type, Class<?>> typeParameterMap) {
        if (type instanceof ParameterizedType) {
            // 处理复杂类型嵌套丢失真实类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Map<Type, Class<?>> childTypeParameterMap = new HashMap<>();
            Map<TypeVariable<?>, Type> typeMap = TypeUtils.getTypeArguments(parameterizedType);
            if (ObjectUtils.isNotEmpty(typeParameterMap)) {
                typeMap.forEach((typeVariable, typeValue) -> childTypeParameterMap.put(typeVariable, typeParameterMap.get(typeValue)));
                Class<?> clazz = (Class<?>) parameterizedType.getRawType();
                return this.processColumnInfo(clazz, columnInfo, childTypeParameterMap);
            } else {
                typeMap.forEach((typeVariable, typeValue) -> childTypeParameterMap.put(typeVariable, (Class<?>) typeValue));
                Class<?> clazz = (Class<?>) parameterizedType.getRawType();
                return this.processColumnInfo(clazz, columnInfo, childTypeParameterMap);
            }
        }
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return this.processColumnInfo(clazz, columnInfo, typeParameterMap);
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
            String tableColumnName = FieldNameUtils.lowerCamelToLowerUnderscore(name);
            // order_column -> orderColumn
            String javaColumnName = FieldNameUtils.lowerUnderscoreToLowerCamel(tableColumnName);

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setJavaColumnName(javaColumnName);
            columnInfo.setDbColumnName(tableColumnName);
            String referencedColumnName = joinColumn.referencedColumnName();
            ForeignKeyInfo foreignKeyColumnInfo = new ForeignKeyInfo(columnInfo, referencedColumnName);
            foreignKeyColumnInfoList.add(foreignKeyColumnInfo);
        }
        return foreignKeyColumnInfoList;
    }

    private void processJavaColumnChain(ColumnInfo parentColumnInfo, ColumnInfo columnInfo) {
        List<ColumnInfo> columnInfoList = this.getJavaColumnNamePathList(parentColumnInfo, columnInfo);
        List<String> javaColumnNamePathList = columnInfoList.stream().map(item -> item.getJavaColumnName()).collect(Collectors.toList());
        columnInfo.setJavaColumnChain(columnInfoList);
        columnInfo.setJavaColumnNamePathList(javaColumnNamePathList);
    }

    private List<ColumnInfo> getJavaColumnNamePathList(ColumnInfo parentColumnInfo, ColumnInfo columnInfo) {
        List<ColumnInfo> javaColumnPathList = new ArrayList();
        if (parentColumnInfo != null && parentColumnInfo.getJavaColumnChain() != null) {
            javaColumnPathList.addAll(parentColumnInfo.getJavaColumnChain());
        }
        javaColumnPathList.add(columnInfo);
        return javaColumnPathList;
    }

    private void processPropertyMethod(Map<String, PropertyDescriptor> getterMethodMap, Map<String, PropertyDescriptor> setterMethodMap, ColumnInfo columnInfo) {
        // 简单类型字段没有生成值注解和关系字段生成 LambdaAccessor 访问器
        if (TypeUtils.typeEquals(columnInfo, ColumnInfo.class, LogicDeleteIdColumnInfo.class) && columnInfo.getGenerateValue() == null) {
            return;
        }

        String javaColumnName = columnInfo.getJavaColumnName();
        PropertyDescriptor getterPropertyDescriptor = getterMethodMap.get(javaColumnName);
        PropertyDescriptor setterPropertyDescriptor = setterMethodMap.get(javaColumnName);
        if (getterPropertyDescriptor == null) {
            throw new MybatisgxException("字段 %s 不存在get方法", javaColumnName);
        }
        if (setterPropertyDescriptor == null) {
            throw new MybatisgxException("字段 %s 不存在set方法", javaColumnName);
        }

        LambdaAccessor lambdaAccessor = new LambdaAccessor();
        lambdaAccessor.setObjectFactory(LambdaAccessorFactory.createObjectFactory(columnInfo.getJavaType()));
        lambdaAccessor.setPropertyGetter(LambdaAccessorFactory.createGetter(getterPropertyDescriptor.getReadMethod()));
        lambdaAccessor.setPropertySetter(LambdaAccessorFactory.createSetter(setterPropertyDescriptor.getWriteMethod()));
        columnInfo.setLambdaAccessor(lambdaAccessor);
    }

    public static class ColumnMap {

        public void process(EntityInfo entityInfo) {
            for (ColumnInfo columnInfo : entityInfo.getColumnInfoList()) {
                this.validatorEntityColumn(entityInfo, columnInfo);
                if (columnInfo.getVersion() != null) {
                    entityInfo.setVersionColumnInfo(columnInfo);
                }
                if (columnInfo.getLogicDelete() != null) {
                    entityInfo.setLogicDeleteColumnInfo(columnInfo);
                }
                if (TypeUtils.typeEquals(columnInfo, LogicDeleteIdColumnInfo.class)) {
                    entityInfo.setLogicDeleteIdColumnInfo(columnInfo);
                }
                if (columnInfo instanceof RelationColumnInfo) {
                    entityInfo.getRelationColumnInfoList().add((RelationColumnInfo) columnInfo);
                }

                this.processIdColumnInfo(entityInfo, columnInfo);
                this.processGenerateValue(entityInfo, columnInfo);

                ColumnInfo tableColumnInfo = this.getTableColumnInfo(columnInfo);
                {
                    if (tableColumnInfo != null) {
                        entityInfo.addTableColumnInfo(columnInfo);
                    }
                    if (tableColumnInfo != null && TypeUtils.typeNotEquals(tableColumnInfo, RelationColumnInfo.class)) {
                        entityInfo.addTableColumnMap(columnInfo.getDbColumnName(), columnInfo.getJavaColumnName());
                    } else {
                        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                        for (ForeignKeyInfo inverseForeignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                            entityInfo.addTableColumnMap(inverseForeignKeyInfo.getColumnInfo().getDbColumnName(), columnInfo.getJavaColumnName());
                        }
                    }
                }
                {
                    if (tableColumnInfo != null && TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
                        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                        for (ForeignKeyInfo inverseForeignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                            ColumnInfo independenceColumnInfo = this.relationColumnInfoToColumnInfo(inverseForeignKeyInfo.getColumnInfo());
                            entityInfo.addColumnMap(independenceColumnInfo.getJavaColumnName(), independenceColumnInfo);
                        }
                    }
                    entityInfo.addColumnMap(columnInfo.getJavaColumnName(), columnInfo);
                }
            }
        }

        /**
         * id字段特殊，如果是联合主键，则自动生成一个id映射到联合主键
         *
         * @param columnInfo
         */
        private void processIdColumnInfo(EntityInfo entityInfo, ColumnInfo columnInfo) {
            if (columnInfo instanceof IdColumnInfo) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                entityInfo.setIdColumnInfo(idColumnInfo);
                entityInfo.addColumnMap("id", idColumnInfo);
                entityInfo.addTableColumnMap("id", idColumnInfo.getJavaColumnName());

                List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                    return;
                }
                for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                    entityInfo.addColumnMap(idColumnInfoComposite.getJavaColumnName(), idColumnInfoComposite);
                    String javaColumnNamePath = StringUtils.join(idColumnInfoComposite.getJavaColumnNamePathList(), ".");
                    entityInfo.addColumnMap(javaColumnNamePath, idColumnInfoComposite);
                    entityInfo.addTableColumnMap(idColumnInfoComposite.getJavaColumnName(), javaColumnNamePath);
                }
            }
        }

        /**
         * 获取表字段
         * 1、字段不存在关联实体为表字段
         * 2、存在关联实体并且是关系维护方才是表字段【多对多关联字段在中间表，所以实体中是不存在表字段的】
         * @param columnInfo
         * @return
         */
        private ColumnInfo getTableColumnInfo(ColumnInfo columnInfo) {
            ColumnInfo tableColumnInfo = null;
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                String mappedBy = relationColumnInfo.getMappedBy();
                if (relationColumnInfo.getManyToMany() == null && StringUtils.isBlank(mappedBy)) {
                    tableColumnInfo = columnInfo;
                }
            } else {
                Transient nonPersistent = columnInfo.getNonPersistent();
                if (nonPersistent == null) {
                    tableColumnInfo = columnInfo;
                }
            }
            return tableColumnInfo;
        }

        private void processGenerateValue(EntityInfo entityInfo, ColumnInfo columnInfo) {
            List<ColumnInfo> generateValueColumnInfoList = new ArrayList<>();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                if (ObjectUtils.isEmpty(columnInfoComposites)) {
                    generateValueColumnInfoList.add(columnInfo);
                } else {
                    for (ColumnInfo compositeColumnInfo : columnInfoComposites) {
                        generateValueColumnInfoList.add(compositeColumnInfo);
                    }
                }
            } else {
                generateValueColumnInfoList.add(columnInfo);
            }
            for (ColumnInfo generateValueColumnInfo : generateValueColumnInfoList) {
                if (generateValueColumnInfo.getGenerateValue() != null) {
                    entityInfo.addGenerateValueColumnInfo(generateValueColumnInfo);
                }
            }
        }

        /**
         * 把关联字段转成普通表字段
         * @param inverseForeignKeyColumnInfo
         * @return
         */
        private ColumnInfo relationColumnInfoToColumnInfo(ColumnInfo inverseForeignKeyColumnInfo) {
            // 解决关联字段单独作为条件查询
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumn(inverseForeignKeyColumnInfo.getColumn());
            columnInfo.setJavaColumnName(inverseForeignKeyColumnInfo.getJavaColumnName());
            columnInfo.setDbColumnName(inverseForeignKeyColumnInfo.getDbColumnName());
            columnInfo.setTypeCategory(TypeCategory.SIMPLE);
            return columnInfo;
        }

        private void validatorEntityColumn(EntityInfo entityInfo, ColumnInfo columnInfo) {
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType == RelationType.ONE_TO_ONE) {
                    Class<?> collectionType = relationColumnInfo.getCollectionType();
                    if (collectionType != null) {
                        throw new MybatisgxException("%s 类中的字段 %s 关系为一对一，字段类型不能使用Set或者List", entityInfo.getClazzName(), relationColumnInfo.getJavaColumnName());
                    }
                }
                if (relationType == RelationType.ONE_TO_MANY) {
                    Class<?> collectionType = relationColumnInfo.getCollectionType();
                    if (collectionType == null || TypeUtils.typeNotEquals(collectionType, List.class, Set.class)) {
                        throw new MybatisgxException("%s 类中的字段 %s 关系为一对多，字段类型不能是对象，需要使用Set或者List", entityInfo.getClazzName(), relationColumnInfo.getJavaColumnName());
                    }
                }
                if (relationType == RelationType.MANY_TO_ONE) {
                    Class<?> collectionType = relationColumnInfo.getCollectionType();
                    if (collectionType != null) {
                        throw new MybatisgxException("%s 类中的字段 %s 关系为多对一，字段类型不能使用Set或者List", entityInfo.getClazzName(), relationColumnInfo.getJavaColumnName());
                    }
                }
                if (relationType == RelationType.MANY_TO_MANY) {
                    Class<?> collectionType = relationColumnInfo.getCollectionType();
                    if (collectionType == null || TypeUtils.typeNotEquals(collectionType, List.class, Set.class)) {
                        throw new MybatisgxException("%s 类中的字段 %s 关系为多对多，字段类型不能是对象，需要使用Set或者List", entityInfo.getClazzName(), relationColumnInfo.getJavaColumnName());
                    }
                }
            }
        }
    }

    public static class ColumnRelation {

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

                            // 复制主键基础信息到关联字段中
                            ColumnInfo columnInfo = inverseForeignKeyColumn.getColumnInfo();
                            this.copyReferencedColumnInfoToColumnInfo(referencedColumnInfo, columnInfo);

                            inverseForeignKeyColumn.setColumnInfo(columnInfo);
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

        private void copyReferencedColumnInfoToColumnInfo(ColumnInfo referencedColumnInfo, ColumnInfo columnInfo) {
            columnInfo.setJavaType(referencedColumnInfo.getJavaType());
            columnInfo.setTypeCategory(referencedColumnInfo.getTypeCategory());
            columnInfo.setDbTypeName(referencedColumnInfo.getDbTypeName());
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
}
