package com.mybatisgx.model;

import com.mybatisgx.annotation.*;
import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.beanutils.BeanUtils;
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
     * 表名称别名
     */
    private String tableNameAlias;
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

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public void setTableNameAlias(String tableNameAlias) {
        this.tableNameAlias = tableNameAlias;
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
                Lock lock = columnInfo.getLock();
                if (lock != null) {
                    entityInfo.lockColumnInfo = columnInfo;
                }
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    entityInfo.logicDeleteColumnInfo = columnInfo;
                }
                LogicDeleteId logicDeleteId = columnInfo.getLogicDeleteId();
                if (logicDeleteId != null) {
                    entityInfo.logicDeleteIdColumnInfo = columnInfo;
                }
                GeneratedValueHandler generatedValueHandler = columnInfo.getGenerateValueHandler();
                if (columnInfo instanceof IdColumnInfo || generatedValueHandler != null) {
                    entityInfo.generateValueColumnInfoList.add(columnInfo);
                }

                if (columnInfo instanceof RelationColumnInfo) {
                    entityInfo.relationColumnInfoList.add((RelationColumnInfo) columnInfo);
                }

                this.processIdColumnInfo(columnInfo);

                // 1、字段不存在关联实体为表字段
                // 2、存在关联实体并且是关系维护方才是表字段【多对多关联字段在中间表，所以实体中是不存在表字段的】
                ColumnInfo tableColumnInfo = null;
                if (columnInfo instanceof RelationColumnInfo) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                    ManyToMany manyToMany = relationColumnInfo.getManyToMany();
                    String mappedBy = relationColumnInfo.getMappedBy();
                    if (manyToMany == null && StringUtils.isBlank(mappedBy)) {
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

        public EntityInfo build() {
            return entityInfo;
        }

        /**
         * 自引用实体属性复制
         * @param entityInfo
         * @return
         */
        public Builder selfRefEntityPropertyCopy(EntityInfo entityInfo) {
            this.setTableName(entityInfo.tableName);
            this.setClazz(entityInfo.clazz);
            this.setColumnInfoList(this.cloneColumnInfoList(entityInfo));
            this.setTypeParameterMap(entityInfo.typeParameterMap);
            this.process();
            return this;
        }

        private List<ColumnInfo> cloneColumnInfoList(EntityInfo entityInfo) {
            List<ColumnInfo> columnInfoList = new ArrayList();
            for (ColumnInfo columnInfo : entityInfo.columnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    columnInfoList.add(this.cloneBean(columnInfo));
                }
                if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) this.cloneBean(columnInfo);
                    RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                    if (mappedByRelationColumnInfo != null) {
                        for (ForeignKeyInfo foreignKeyInfo : mappedByRelationColumnInfo.getInverseForeignKeyInfoList()) {
                            ColumnInfo referencedColumnInfo = this.cloneBean(foreignKeyInfo.getReferencedColumnInfo());
                            foreignKeyInfo.setReferencedColumnInfo(referencedColumnInfo);
                        }
                    } else {
                        for (ForeignKeyInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                            ColumnInfo referencedColumnInfo = this.cloneBean(foreignKeyInfo.getReferencedColumnInfo());
                            foreignKeyInfo.setReferencedColumnInfo(referencedColumnInfo);
                        }
                    }
                    columnInfoList.add(relationColumnInfo);
                }
            }
            return columnInfoList;
        }

        private ColumnInfo cloneBean(ColumnInfo columnInfo) {
            try {
                return (ColumnInfo) BeanUtils.cloneBean(columnInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
