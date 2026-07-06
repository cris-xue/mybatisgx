package com.mybatisgx.template.select;

import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectItemClauseBuilder {

    private Table buildFromItem(String mainTableName, String mainTableNameAlias) {
        Table mainTable = new Table(mainTableName);
        if (StringUtils.isNotBlank(mainTableNameAlias)) {
            mainTable.setAlias(new Alias(mainTableNameAlias));
        }
        return mainTable;
    }

    /**
     * 构建查询字段列
     *
     * @param columnEntityRelation
     * @param entityInfo
     * @param isBatch
     * @return
     */
    public List<SelectItem<?>> buildSelectItemList(ColumnEntityRelation columnEntityRelation, EntityInfo entityInfo, Boolean isBatch) {
        List<SelectItem<?>> selectItemList = new ArrayList();
        Table table = new Table(columnEntityRelation.getTableNameAlias());
        // 添加非外键表字段
        for (ColumnInfo columnInfo : entityInfo.getTableColumnInfoList()) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                List<ColumnInfo> compositeList = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(compositeList)) {
                    SelectItem<?> selectItem = this.getSelectItem(table, idColumnInfo.getDbColumnName(), idColumnInfo.getTableColumnNameAlias(columnEntityRelation));
                    selectItemList.add(selectItem);
                } else {
                    for (ColumnInfo composite : compositeList) {
                        SelectItem<?> selectItem = this.getSelectItem(table, composite.getDbColumnName(), composite.getTableColumnNameAlias(columnEntityRelation));
                        selectItemList.add(selectItem);
                    }
                }
            }
            // 批量结果集节点是不需要查询字段的，只需要查询出主键最终能够合并数据即可
            if (!isBatch && TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
                SelectItem<?> selectItem = this.getSelectItem(table, columnInfo.getDbColumnName(), columnInfo.getTableColumnNameAlias(columnEntityRelation));
                selectItemList.add(selectItem);
            }
        }
        // 添加外键表字段
        for (RelationColumnInfo relationColumnInfo : entityInfo.getRelationColumnInfoList()) {
            RelationType relationType = relationColumnInfo.getRelationType();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (relationType != RelationType.MANY_TO_MANY && mappedByRelationColumnInfo == null) {
                // 只有一对一、一对多、多对一的时候关联字段才需要作为表字段。多对多存在中间表，关联字段在中间中表，不需要作为实体表字段
                List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    SelectItem<?> selectItem = this.getSelectItem(table, foreignKeyColumnInfo.getDbColumnName(), foreignKeyColumnInfo.getTableColumnNameAlias(columnEntityRelation));
                    selectItemList.add(selectItem);
                }
            }
        }
        return selectItemList;
    }

    private SelectItem<Column> getSelectItem(Table table, String columnName, String columnNameAlias) {
        SelectItem<Column> selectItem = new SelectItem();
        Column column = new Column(table, columnName);
        selectItem.withExpression(column);
        Alias alias = new Alias(columnNameAlias);
        selectItem.setAlias(alias);
        return selectItem;
    }
}
