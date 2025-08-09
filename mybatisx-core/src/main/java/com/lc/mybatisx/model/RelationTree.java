package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.ManyToMany;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 关系树模型
 * @author ccxuef
 * @date 2025/8/9 18:03
 */
public class RelationTree {

    /**
     * 关系字段，如当前类为role，关系字段为user中的List<Role>字段
     */
    private ColumnInfo columnInfo;
    /**
     * 关联实体，如实体为user、role。如第一层为user，则entityInfo为user,跟entityInfo有关系的实体为entityRelationList中的entityInfo
     */
    private EntityInfo entityInfo;

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    /**
     * 当前实体是否是关系维护方
     * @return true：是关系维护方 false：关系被维护方
     */
    public Boolean getMappedBy() {
        String mappedBy = this.columnInfo.getColumnRelationInfo().getMappedBy();
        if (StringUtils.isBlank(mappedBy)) {
            return false;
        } else {
            ColumnInfo mappedByColumnInfo = this.entityInfo.getColumnInfo(mappedBy);
            if (mappedByColumnInfo != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是多对多关系
     * @return true: 表示是多对多关系 false: 表示不是多对多关系
     */
    public Boolean getManyToMany() {
        ManyToMany manyToMany = this.columnInfo.getColumnRelationInfo().getManyToMany();
        return manyToMany != null;
    }

    public List<ForeignKeyColumnInfo> getForeignKeyColumnInfoList() {
        if (this.getMappedBy()) {
            ColumnRelationInfo columnRelationInfo = this.columnInfo.getColumnRelationInfo();
            ColumnInfo mappedByColumnInfo = this.entityInfo.getColumnInfo(columnRelationInfo.getMappedBy());
            return mappedByColumnInfo.getColumnRelationInfo().getForeignKeyColumnInfoList();
        } else {
            return this.columnInfo.getColumnRelationInfo().getForeignKeyColumnInfoList();
        }
    }

    public List<ForeignKeyColumnInfo> getInverseForeignKeyColumnInfoList() {
        if (this.getMappedBy()) {
            ColumnRelationInfo columnRelationInfo = this.columnInfo.getColumnRelationInfo();
            ColumnInfo mappedByColumnInfo = this.entityInfo.getColumnInfo(columnRelationInfo.getMappedBy());
            return mappedByColumnInfo.getColumnRelationInfo().getInverseForeignKeyColumnInfoList();
        } else {
            return this.columnInfo.getColumnRelationInfo().getInverseForeignKeyColumnInfoList();
        }
    }
}
