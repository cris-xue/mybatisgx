package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.JoinTable;
import com.lc.mybatisx.annotation.LoadStrategy;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectSqlTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectSqlTemplateHandler.class);

    /**
     * @param entityRelationSelectInfo
     * @return
     * @throws JSQLParserException
     */
    public String buildSelectSql(EntityRelationSelectInfo entityRelationSelectInfo) throws JSQLParserException {
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = entityRelationSelectInfo.getEntityRelationSelectInfoList();
        PlainSelect plainSelect = this.buildMainSelect(entityRelationSelectInfo);
        if (ObjectUtils.isNotEmpty(entityRelationSelectInfoList)) {
            this.buildLeftJoinSelect(plainSelect, entityRelationSelectInfo, entityRelationSelectInfoList);
        }
        return plainSelect.toString();
    }

    private PlainSelect buildMainSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        PlainSelect plainSelect = new PlainSelect();
        EntityInfo queryEntityInfo = entityRelationSelectInfo.getEntityInfo();
        plainSelect.addSelectItems(this.getSelectItemList(queryEntityInfo));
        Table mainTable = new Table(queryEntityInfo.getTableName());
        plainSelect.setFromItem(mainTable);
        return plainSelect;
    }

    private void buildLeftJoinSelect(PlainSelect plainSelect, EntityRelationSelectInfo leftEntityRelationSelectInfo, List<EntityRelationSelectInfo> rightEntityRelationSelectInfoList) {
        EntityInfo leftEntityInfo = leftEntityRelationSelectInfo.getEntityInfo();
        String leftTableName = leftEntityInfo.getTableName();
        List<Join> joinList = new ArrayList();
        for (EntityRelationSelectInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
            ColumnInfo columnInfo = rightEntityRelationSelectInfo.getColumnInfo();
            EntityInfo rightEntityInfo = rightEntityRelationSelectInfo.getEntityInfo();
            String rightTableName = rightEntityInfo.getTableName();
            Join join = new Join();
            join.setLeft(true);
            join.setRightItem(new Table(rightTableName));
            join.setOnExpressions(this.buildJoinOn(leftEntityRelationSelectInfo, rightEntityRelationSelectInfo));
            joinList.add(join);
            this.buildLeftJoinSelect(plainSelect, rightEntityRelationSelectInfo, rightEntityRelationSelectInfo.getEntityRelationSelectInfoList());
        }
        plainSelect.addJoins(joinList);
    }

    private void processAssociation(String tableName, EntityRelationSelectInfo entityRelationSelectInfo) throws JSQLParserException {
        List<Join> joinList;
        ManyToMany manyToMany = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getManyToMany();
        if (manyToMany == null) {
            // FROM user
            joinList = this.buildOneToOneLeftJoin(tableName, entityRelationSelectInfo.getEntityRelationSelectInfoList());
        } else {
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList;
            String mappedBy = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getMappedBy();
            JoinTable joinTable;
            if (StringUtils.isNotBlank(mappedBy)) {
                Class<?> columnClass = entityRelationSelectInfo.getColumnInfo().getJavaType();
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnClass);
                ColumnInfo mappedColumnInfo = entityInfo.getColumnInfo(mappedBy);
                joinTable = mappedColumnInfo.getColumnInfoAnnotationInfo().getJoinTable();
                foreignKeyColumnInfoList = mappedColumnInfo.getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
                inverseForeignKeyColumnInfoList = mappedColumnInfo.getColumnInfoAnnotationInfo().getInverseForeignKeyColumnInfoList();
            } else {
                joinTable = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getJoinTable();
                foreignKeyColumnInfoList = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
                inverseForeignKeyColumnInfoList = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getInverseForeignKeyColumnInfoList();
            }
            Table mainTable = new Table(joinTable.name());
            // select * from user
            // select * from user_role left join role on user_role.role_id = role.id left join role_menu on role.id = role_menu.role_id
            joinList = this.buildManyToManyLeftJoin(mainTable.getName(), entityRelationSelectInfo.getEntityRelationSelectInfoList());
        }
    }

    /**
     * 并不是配置了加载策略为join就会采用join，而是需要使用level判断当前是否需要join，第一层的ResultMap是不能join的。
     * 第一层的ResultMap就开始join会导致在分页场景下会出现数据重复。
     *
     * @param resultMapAssociationInfoList
     * @return
     */
    private void getSelectTable(Map<String, SelectTable> joinTableGroup, List<ResultMapAssociationInfo> resultMapAssociationInfoList, SelectTable parentSelectTable) {
        for (ResultMapAssociationInfo resultMapAssociationInfo : resultMapAssociationInfoList) {
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            ColumnInfoAnnotationInfo associationEntityInfo = resultMapAssociationInfo.getColumnInfo().getColumnInfoAnnotationInfo();

            SelectTable selectTable = new SelectTable();
            selectTable.setResultMapAssociationInfo(resultMapAssociationInfo);
            LoadStrategy loadStrategy = associationEntityInfo.getLoadStrategy();
            Integer level = resultMapAssociationInfo.getLevel();
            if (loadStrategy == LoadStrategy.SUB || (loadStrategy == LoadStrategy.JOIN && level == 1)) {
                ManyToMany manyToMany = associationEntityInfo.getManyToMany();
                if (manyToMany != null) {
                    SelectTable middleSelectTable = new SelectTable();
                    middleSelectTable.setJoinTable(associationEntityInfo.getJoinTable());
                    middleSelectTable.setResultMapAssociationInfo(resultMapAssociationInfo);
                    List<SelectTable> middleSelectTableChildren = middleSelectTable.getChildren();
                    middleSelectTableChildren.add(selectTable);

                    joinTableGroup.put(resultMapAssociationInfo.getSelect(), middleSelectTable);
                } else {
                    joinTableGroup.put(resultMapAssociationInfo.getSelect(), selectTable);
                }
            } else if (loadStrategy == LoadStrategy.JOIN && level >= 2) {
                if (parentSelectTable != null) {
                    ManyToMany manyToMany = associationEntityInfo.getManyToMany();
                    if (manyToMany != null) {
                        String mappedBy = associationEntityInfo.getMappedBy();
                        if (StringUtils.isNotBlank(mappedBy)) {

                        } else {

                        }

                        SelectTable middleSelectTable = new SelectTable();
                        middleSelectTable.setJoinTable(associationEntityInfo.getJoinTable());
                        middleSelectTable.setResultMapAssociationInfo(resultMapAssociationInfo);
                        List<SelectTable> middleSelectTableChildren = middleSelectTable.getChildren();
                        middleSelectTableChildren.add(selectTable);

                        List<SelectTable> children = parentSelectTable.getChildren();
                        children.add(middleSelectTable);
                    } else {
                        List<SelectTable> children = parentSelectTable.getChildren();
                        children.add(selectTable);
                    }
                }
            } else {
                throw new RuntimeException("未实现的加载策略");
            }
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                this.getSelectTable(joinTableGroup, subResultMapAssociationInfoList, selectTable);
            }
        }
    }

    private List<SelectItem<?>> getSelectItemList(EntityInfo queryEntityInfo) {
        List<SelectItem<?>> list = new ArrayList<>();
        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            ColumnInfoAnnotationInfo queryAssociationEntityInfo = queryColumnInfo.getColumnInfoAnnotationInfo();
            if (queryAssociationEntityInfo == null) {
                list.add(this.getSelectItem(queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    list.add(this.getSelectItem(foreignKeyColumnInfo.getName()));
                });
            }
        });
        return list;
    }

    private SelectItem<Column> getSelectItem(String columnName) {
        SelectItem<Column> selectItem = new SelectItem();
        Column column = new Column(columnName);
        selectItem.withExpression(column);
        return selectItem;
    }

    /**
     * 处理一对一、一对多、多对一的关系
     *
     * @param tableName
     * @param entityRelationSelectInfoList
     * @return
     * @throws JSQLParserException
     */
    private List<Join> buildOneToOneLeftJoin(String tableName, List<EntityRelationSelectInfo> entityRelationSelectInfoList) throws JSQLParserException {
        List<Join> joinList = new ArrayList<>();
        for (int i = 0; i < entityRelationSelectInfoList.size(); i++) {
            EntityRelationSelectInfo resultMapRelationInfo = entityRelationSelectInfoList.get(i);
            ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
            ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
            String mappedBy = associationEntityInfo.getMappedBy();

            String joinTableName;
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
            if (StringUtils.isNotBlank(mappedBy)) {
                // select * from user left join user_detail on user.id = user_detail.user_id
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                joinTableName = entityInfo.getTableName();
                foreignKeyColumnInfoList = mappedByColumnInfo.getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
            } else {
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                joinTableName = entityInfo.getTableName();
                foreignKeyColumnInfoList = columnInfo.getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
            }

            Join join = new Join();
            join.setLeft(true);
            join.setRightItem(new Table(joinTableName));
            join.setOnExpressions(this.buildLeftJoinOn(tableName, joinTableName, foreignKeyColumnInfoList));
            joinList.add(join);

            List<EntityRelationSelectInfo> subSelectTableList = resultMapRelationInfo.getEntityRelationSelectInfoList();
            if (ObjectUtils.isNotEmpty(subSelectTableList)) {
                List<Join> subJoinList = this.buildOneToOneLeftJoin(joinTableName, subSelectTableList);
                if (ObjectUtils.isNotEmpty(subJoinList)) {
                    joinList.addAll(subJoinList);
                }
            }
        }
        return joinList;
    }

    private List<Join> buildManyToManyLeftJoin(String tableName, List<EntityRelationSelectInfo> entityRelationSelectInfoList) throws JSQLParserException {
        List<Join> joinList = new ArrayList<>();
        for (int i = 0; i < entityRelationSelectInfoList.size(); i++) {
            EntityRelationSelectInfo resultMapRelationInfo = entityRelationSelectInfoList.get(i);
            ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
            ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();

            // 处理多对多的关系
            String mappedBy = associationEntityInfo.getMappedBy();

            String joinTableName;
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
            if (StringUtils.isNotBlank(mappedBy)) {
                // select * from user left join user_detail on user.id = user_detail.user_id
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                joinTableName = entityInfo.getTableName();
                foreignKeyColumnInfoList = mappedByColumnInfo.getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
            } else {
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                joinTableName = entityInfo.getTableName();
                foreignKeyColumnInfoList = columnInfo.getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
            }

            Join join = new Join();
            join.setLeft(true);
            join.setRightItem(new Table(joinTableName));
            join.setOnExpressions(this.buildLeftJoinOn(tableName, joinTableName, foreignKeyColumnInfoList));
            joinList.add(join);

            List<EntityRelationSelectInfo> subSelectTableList = resultMapRelationInfo.getEntityRelationSelectInfoList();
            if (ObjectUtils.isNotEmpty(subSelectTableList)) {
                List<Join> subJoinList = this.buildManyToManyLeftJoin(null, subSelectTableList);
                if (ObjectUtils.isNotEmpty(subJoinList)) {
                    joinList.addAll(subJoinList);
                }
            }
        }
        return joinList;
    }

    private List<Expression> buildJoinOn(EntityRelationSelectInfo leftEntityRelationSelectInfo, EntityRelationSelectInfo rightEntityRelationSelectInfo) {
        try {
            EntityInfo leftEntityInfo = leftEntityRelationSelectInfo.getEntityInfo();
            ColumnInfoAnnotationInfo columnInfoAnnotationInfo = leftEntityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo();
            EntityInfo rightEntityInfo = rightEntityRelationSelectInfo.getEntityInfo();
            ManyToMany manyToMany = columnInfoAnnotationInfo.getManyToMany();
            if (manyToMany != null) {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfoAnnotationInfo.getForeignKeyColumnInfoList();
                String mappedBy = columnInfoAnnotationInfo.getMappedBy();
                if (StringUtils.isNotEmpty(mappedBy)) {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
                }
                return this.buildLeftJoinOn(leftEntityInfo.getTableName(), rightEntityInfo.getTableName(), foreignKeyColumnInfoList);
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfoAnnotationInfo.getForeignKeyColumnInfoList();
                String mappedBy = columnInfoAnnotationInfo.getMappedBy();
                if (StringUtils.isNotEmpty(mappedBy)) {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo().getForeignKeyColumnInfoList();
                }
                return this.buildLeftJoinOn(leftEntityInfo.getTableName(), rightEntityInfo.getTableName(), foreignKeyColumnInfoList);
            }
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Expression> buildLeftJoinOn(String leftTableName, String rightTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) throws JSQLParserException {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            EqualsTo onCondition = new EqualsTo(
                    CCJSqlParserUtil.parseExpression(leftTableName + "." + foreignKeyColumnInfo.getReferencedColumnName()),
                    CCJSqlParserUtil.parseExpression(rightTableName + "." + foreignKeyColumnInfo.getName())
            );
            onExpressionList.add(onCondition);
        }
        return onExpressionList;
    }

    class SelectTable {

        private JoinTable joinTable;

        private ResultMapAssociationInfo resultMapAssociationInfo;

        private List<SelectTable> children = new ArrayList();

        private String sql;

        public JoinTable getJoinTable() {
            return joinTable;
        }

        public void setJoinTable(JoinTable joinTable) {
            this.joinTable = joinTable;
        }

        public ResultMapAssociationInfo getResultMapAssociationInfo() {
            return resultMapAssociationInfo;
        }

        public void setResultMapAssociationInfo(ResultMapAssociationInfo resultMapAssociationInfo) {
            this.resultMapAssociationInfo = resultMapAssociationInfo;
        }

        public List<SelectTable> getChildren() {
            return children;
        }

        public void setChildren(List<SelectTable> children) {
            this.children = children;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }
}