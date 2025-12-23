package com.mybatisgx.model;

import com.mybatisgx.annotation.Transient;
import com.mybatisgx.api.ValueProcessor;
import com.mybatisgx.context.DaoMethodManager;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:34
 */
public class EntityInfo {

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 实体类型
     */
    private Class<?> clazz;
    /**
     * 实体类型名称
     */
    private String clazzName;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * java字段映射字段信息，userName={userName=1}
     */
    private Map<String, ColumnInfo> columnInfoMap = new LinkedHashMap();
    /**
     * 数据库字段和java字段映射信息，如：user_name=userName
     */
    private Map<String, String> tableColumnInfoMap = new LinkedHashMap();
    /**
     * id字段
     */
    private IdColumnInfo idColumnInfo;
    /**
     * 生成值字段列表
     */
    private List<ColumnInfo> generateValueColumnInfoList = new ArrayList<>();
    /**
     * 表字段信息
     */
    private List<ColumnInfo> tableColumnInfoList = new ArrayList<>();
    /**
     * 逻辑删除
     */
    private ColumnInfo logicDeleteColumnInfo;
    /**
     * 逻辑删除id字段
     */
    private ColumnInfo logicDeleteIdColumnInfo;
    /**
     * 乐观锁
     */
    private ColumnInfo lockColumnInfo;
    /**
     * 关系字段信息
     */
    private List<RelationColumnInfo> relationColumnInfoList = new ArrayList<>();
    /**
     * 实体类泛型参数类型映射
     */
    private Map<Type, Class<?>> typeParameterMap;

    public String getTableName() {
        return tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getClazzName() {
        return clazzName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.columnInfoMap.get(javaColumnName);
    }

    public ColumnInfo getDbColumnInfo(String dbColumnName) {
        String javaColumnName = this.tableColumnInfoMap.get(dbColumnName);
        return this.getColumnInfo(javaColumnName);
    }

    public IdColumnInfo getIdColumnInfo() {
        return idColumnInfo;
    }

    public List<ColumnInfo> getGenerateValueColumnInfoList() {
        return generateValueColumnInfoList;
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return tableColumnInfoList;
    }

    public ColumnInfo getLogicDeleteColumnInfo() {
        return logicDeleteColumnInfo;
    }

    public ColumnInfo getLogicDeleteIdColumnInfo() {
        return logicDeleteIdColumnInfo;
    }

    public ColumnInfo getLockColumnInfo() {
        return lockColumnInfo;
    }

    public List<RelationColumnInfo> getRelationColumnInfoList() {
        return relationColumnInfoList;
    }

    public Map<Type, Class<?>> getTypeParameterMap() {
        return typeParameterMap;
    }

    public static class Builder {

        private EntityInfo entityInfo;

        public Builder() {
            this.entityInfo = new EntityInfo();
        }

        public Builder setTableName(String tableName) {
            this.entityInfo.tableName = tableName;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            this.entityInfo.clazz = clazz;
            this.entityInfo.clazzName = clazz.getName();
            return this;
        }

        public Builder setColumnInfoList(List<ColumnInfo> columnInfoList) {
            this.entityInfo.columnInfoList = columnInfoList;
            return this;
        }

        public Builder setTypeParameterMap(Map<Type, Class<?>> typeParameterMap) {
            this.entityInfo.typeParameterMap = typeParameterMap;
            return this;
        }

        public Builder process() {
            for (ColumnInfo columnInfo : this.entityInfo.columnInfoList) {
                if (columnInfo.getLock() != null) {
                    entityInfo.lockColumnInfo = columnInfo;
                }
                if (columnInfo.getLogicDelete() != null) {
                    entityInfo.logicDeleteColumnInfo = columnInfo;
                }
                if (TypeUtils.typeEquals(columnInfo, LogicDeleteIdColumnInfo.class)) {
                    entityInfo.logicDeleteIdColumnInfo = columnInfo;
                }

                this.processGenerateValue(columnInfo);

                if (columnInfo instanceof RelationColumnInfo) {
                    entityInfo.relationColumnInfoList.add((RelationColumnInfo) columnInfo);
                }

                this.processIdColumnInfo(columnInfo);

                // 1、字段不存在关联实体为表字段
                // 2、存在关联实体并且是关系维护方才是表字段【多对多关联字段在中间表，所以实体中是不存在表字段的】
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
                if (tableColumnInfo != null) {
                    entityInfo.tableColumnInfoList.add(columnInfo);
                    entityInfo.tableColumnInfoMap.put(columnInfo.getDbColumnName(), columnInfo.getJavaColumnName());
                }
                entityInfo.columnInfoMap.put(columnInfo.getJavaColumnName(), columnInfo);
            }
            return this;
        }

        /**
         * id字段特殊，如果是联合主键，则自动生成一个id映射到联合主键
         *
         * @param columnInfo
         */
        private void processIdColumnInfo(ColumnInfo columnInfo) {
            if (columnInfo instanceof IdColumnInfo) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                entityInfo.idColumnInfo = idColumnInfo;
                entityInfo.columnInfoMap.put("id", idColumnInfo);
                entityInfo.tableColumnInfoMap.put("id", idColumnInfo.getJavaColumnName());

                List<ColumnInfo> composites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(composites)) {
                    return;
                }
                for (ColumnInfo composite : composites) {
                    IdColumnInfo wrapIdColumnInfo = new IdColumnInfo();
                    wrapIdColumnInfo.setJavaColumnName(idColumnInfo.getJavaColumnName());
                    wrapIdColumnInfo.setDbColumnName(idColumnInfo.getDbColumnName());
                    wrapIdColumnInfo.setEmbeddedId(idColumnInfo.getEmbeddedId());
                    wrapIdColumnInfo.setComposites(Arrays.asList(composite));
                    entityInfo.columnInfoMap.put(composite.getJavaColumnName(), wrapIdColumnInfo);

                    String wrapJavaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), composite.getJavaColumnName());
                    entityInfo.columnInfoMap.put(wrapJavaColumnName, wrapIdColumnInfo);
                }
            }
        }

        private void processGenerateValue(ColumnInfo columnInfo) {
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
                    entityInfo.generateValueColumnInfoList.add(generateValueColumnInfo);
                    DaoMethodManager.register((Class<ValueProcessor>[]) generateValueColumnInfo.getGenerateValue().value());
                }
            }
        }

        public EntityInfo build() {
            return entityInfo;
        }
    }
}
