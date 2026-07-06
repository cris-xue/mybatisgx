package com.mybatisgx.template.select;

import com.mybatisgx.annotation.ManyToMany;
import com.mybatisgx.model.*;
import com.mybatisgx.template.MybatisgxSqlBuilder;
import com.mybatisgx.template.RelationSelectHelper;
import com.mybatisgx.utils.TypeUtils;
import com.mybatisgx.utils.XmlUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联查询模板处理
 * @author 薛承城
 * @date 2025/7/31 21:03
 */
public class RelationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RelationSelectTemplateHandler.class);

    private RelationSelectColumn relationSelectColumn = new RelationSelectColumn();
    private SimpleRelationSelect simpleRelationSelect = new SimpleRelationSelect();
    private BatchRelationSelect batchRelationSelect = new BatchRelationSelect();

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        List<ResultMapInfo> resultMapInfoList = mapperInfo.getResultMapInfoList();
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            ResultMapInfo.NestedSelect nestedSelect = resultMapInfo.getNestedSelect();
            if (nestedSelect == null) {
                continue;
            }
            Map<String, XNode> entityRelationSelectXNodeMap = this.buildSelect(resultMapInfo);
            if (ObjectUtils.isNotEmpty(entityRelationSelectXNodeMap)) {
                totalXNodeMap.putAll(entityRelationSelectXNodeMap);
            }
        }
        return totalXNodeMap;
    }

    private Map<String, XNode> buildSelect(ResultMapInfo resultMapInfo) {
        String selectXmlString = this.buildSelectString(resultMapInfo);
        Map<String, XNode> entityRelationSelectXNodeMap = new HashMap();
        if (StringUtils.isNotBlank(selectXmlString)) {
            logger.debug("auto relation select sql: \n{}", selectXmlString);
            XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/select");
            entityRelationSelectXNodeMap.put(resultMapInfo.getId(), xNode);
        }
        return entityRelationSelectXNodeMap;
    }

    private String buildSelectString(ResultMapInfo resultMapInfo) {
        String selectSql = this.buildJoinSelect(resultMapInfo);
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = RelationSelectHelper.buildSelectElement(mapperElement, resultMapInfo, selectSql);

        if (TypeUtils.typeEquals(resultMapInfo, ResultMapInfo.class, SimpleNestedResultMapInfo.class)) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                Expression whereCondition = simpleRelationSelect.buildOneToOneWhere(resultMapInfo);
                RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
            } else {
                Expression whereCondition = simpleRelationSelect.buildManyToManyWhere(resultMapInfo);
                RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
            }
        }
        if (TypeUtils.typeEquals(resultMapInfo, BatchNestedResultMapInfo.class)) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                Expression whereCondition = batchRelationSelect.buildOneToOneWhere(resultMapInfo);
                Element whereElement = RelationSelectHelper.buildWhereElement(selectElement);
                RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
            } else {
                Expression whereCondition = batchRelationSelect.buildManyToManyWhere(resultMapInfo);
                Element whereElement = RelationSelectHelper.buildWhereElement(selectElement);
                RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
            }
        }
        return document.asXML();
    }

    private String buildJoinSelect(ResultMapInfo resultMapInfo) {
        return relationSelectColumn.buildComplexSelectSql(resultMapInfo);
    }

    /**
     * 查询sql模板处理
     *
     * @author ccxuef
     * @date 2025/9/6 14:05
     */
    public static class RelationSelectColumn {

        private static final Logger logger = LoggerFactory.getLogger(RelationSelectColumn.class);
        private SelectItemClauseBuilder selectItemClauseBuilder = new SelectItemClauseBuilder();
        private SelectFromJoinClauseBuilder selectFromJoinClauseBuilder = new SelectFromJoinClauseBuilder();

        /**
         * 构建关联查询
         * <code>
         * select * from user_role left join role on user_role.user_id = role.id
         * </code>
         *
         * @param entityRelationSelectInfo
         * @return
         * @throws JSQLParserException
         */
        public String buildComplexSelectSql(ResultMapInfo entityRelationSelectInfo) {
            List<RelationSelectTemplateHandler.EntityContext> entityContextList = this.getEntityInfoList(entityRelationSelectInfo);
            PlainSelect plainSelect = this.buildMainSelect(entityContextList);
            if (TypeUtils.typeEquals(entityRelationSelectInfo, ResultMapInfo.class)) {
                selectFromJoinClauseBuilder.buildFromItem(plainSelect, entityRelationSelectInfo.getTableName(), entityRelationSelectInfo.getTableNameAlias());
                this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
            }
            if (TypeUtils.typeEquals(entityRelationSelectInfo, SimpleNestedResultMapInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
                RelationType relationType = relationColumnInfo.getRelationType();
                String mainTableName;
                String mainTableNameAlias = "";
                if (relationType == RelationType.MANY_TO_MANY) {
                    mainTableName = entityRelationSelectInfo.getMiddleTableName();
                } else {
                    mainTableName = entityRelationSelectInfo.getTableName();
                    mainTableNameAlias = entityRelationSelectInfo.getTableNameAlias();
                }
                selectFromJoinClauseBuilder.buildFromItem(plainSelect, mainTableName, mainTableNameAlias);
                this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
            }
            if (TypeUtils.typeEquals(entityRelationSelectInfo, BatchNestedResultMapInfo.class)) {
                selectFromJoinClauseBuilder.buildFromItem(plainSelect, entityRelationSelectInfo.getTableName(), entityRelationSelectInfo.getTableNameAlias());
                this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
            }
            return plainSelect.toString();
        }


        /**
         * 构建主表查询，如：select * from user_role
         *
         * @param entityContextList
         * @return
         */
        private PlainSelect buildMainSelect(List<RelationSelectTemplateHandler.EntityContext> entityContextList) {
            PlainSelect plainSelect = new PlainSelect();
            for (RelationSelectTemplateHandler.EntityContext entityContext : entityContextList) {
                List<SelectItem<?>> selectItemList = selectItemClauseBuilder.buildSelectItemList(
                        entityContext.getColumnEntityRelation(),
                        entityContext.getEntityInfo(),
                        entityContext.getBatch()
                );
                plainSelect.addSelectItems(selectItemList);
            }
            return plainSelect;
        }

        private List<RelationSelectTemplateHandler.EntityContext> getEntityInfoList(ResultMapInfo resultMapInfo) {
            List<RelationSelectTemplateHandler.EntityContext> entityContextList = new ArrayList();
            EntityInfo entityInfo = resultMapInfo.getEntityInfo();
            if (entityInfo != null) {
                Boolean isBatch = TypeUtils.typeEquals(resultMapInfo, BatchNestedResultMapInfo.class);
                entityContextList.add(new RelationSelectTemplateHandler.EntityContext(resultMapInfo, entityInfo, isBatch));
            }
            for (ResultMapInfo composite : resultMapInfo.getComposites()) {
                List<RelationSelectTemplateHandler.EntityContext> childrenEntityContextList = this.getEntityInfoList(composite);
                if (ObjectUtils.isNotEmpty(childrenEntityContextList)) {
                    entityContextList.addAll(childrenEntityContextList);
                }
            }
            return entityContextList;
        }

        /**
         * 构建join查询sql【一对一、一对多、多对一、多对多】
         *
         * @param plainSelect
         * @param leftEntityRelationSelectInfo
         * @param rightEntityRelationSelectInfoList
         */
        private void buildLeftJoinOn(PlainSelect plainSelect, ResultMapInfo leftEntityRelationSelectInfo, List<ResultMapInfo> rightEntityRelationSelectInfoList) {
            MiddleEntityInfo middleEntityInfo = leftEntityRelationSelectInfo.getMiddleEntityInfo();
            if (middleEntityInfo != null) {
                Boolean leftManyToMany = leftEntityRelationSelectInfo.isManyToMany();
                if (leftManyToMany) {
                    // 左表是多对多的处理【user_role left join role on user_role.user_id = role.id】
                    String middleTableName = leftEntityRelationSelectInfo.getMiddleTableName();
                    String entityTableName = leftEntityRelationSelectInfo.getTableName();
                    String entityTableNameAlias = leftEntityRelationSelectInfo.getTableNameAlias();
                    Join join = selectFromJoinClauseBuilder.buildLeftJoin(entityTableName, entityTableNameAlias);

                    List<ForeignKeyInfo> foreignKeyColumnInfoList;
                    if (leftEntityRelationSelectInfo.isMappedBy()) {
                        foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                    } else {
                        foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                    }
                    this.buildMiddleTableOnEntityTable(middleTableName, entityTableNameAlias, foreignKeyColumnInfoList, join);
                    plainSelect.addJoins(join);
                }
            }
            for (ResultMapInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
                ResultMapInfo.NestedSelect nestedSelect = rightEntityRelationSelectInfo.getNestedSelect();
                if (nestedSelect != null) {
                    continue;
                }
                Boolean rightManyToMany = rightEntityRelationSelectInfo.isManyToMany();
                if (rightManyToMany) {
                    // 右表是多对多的处理【role left join role_menu on role.id = role_menu.role_id】
                    String entityTableNameAlias = leftEntityRelationSelectInfo.getTableNameAlias();
                    String middleTableName = rightEntityRelationSelectInfo.getMiddleTableName();
                    Join join = selectFromJoinClauseBuilder.buildLeftJoin(middleTableName, null);

                    List<ForeignKeyInfo> foreignKeyColumnInfoList;
                    if (rightEntityRelationSelectInfo.isMappedBy()) {
                        foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                    } else {
                        foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                    }
                    this.buildEntityTableOnMiddleTable(entityTableNameAlias, middleTableName, foreignKeyColumnInfoList, join);
                    plainSelect.addJoins(join);
                } else {
                    // 一对一、一对多、多对一的处理【user left join user_detail on user.id = user_detail.user_id】
                    String leftEntityTableName = leftEntityRelationSelectInfo.getTableName();
                    String rightEntityTableName = rightEntityRelationSelectInfo.getTableName();
                    String rightEntityTableNameAlias = rightEntityRelationSelectInfo.getTableNameAlias();
                    Join join = selectFromJoinClauseBuilder.buildLeftJoin(rightEntityTableName, rightEntityTableNameAlias);

                    this.buildEntityTableOnEntityTable(leftEntityRelationSelectInfo, rightEntityRelationSelectInfo, join);
                    plainSelect.addJoins(join);
                }
                this.buildLeftJoinOn(plainSelect, rightEntityRelationSelectInfo, rightEntityRelationSelectInfo.getComposites());
            }
        }

        private void buildEntityTableOnEntityTable(ResultMapInfo leftEntityRelationSelectInfo, ResultMapInfo rightEntityRelationSelectInfo, Join join) {
            List<Expression> onExpressionList = new ArrayList<>();
            String leftEntityTableNameAlias = leftEntityRelationSelectInfo.getTableNameAlias();
            String rightEntityTableNameAlias = rightEntityRelationSelectInfo.getTableNameAlias();
            List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
            if (rightEntityRelationSelectInfo.isMappedBy()) {
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    if (ObjectUtils.isNotEmpty(referencedColumnInfo.getComposites())) {
                        referencedColumnInfo = referencedColumnInfo.getComposites().get(0);
                    }
                    String leftExpression = String.format("%s.%s", leftEntityTableNameAlias, referencedColumnInfo.getDbColumnName());
                    String rightExpression = String.format("%s.%s", rightEntityTableNameAlias, foreignKeyColumnInfo.getDbColumnName());
                    EqualsTo onCondition = MybatisgxSqlBuilder.eq(leftExpression, rightExpression);
                    onExpressionList.add(onCondition);
                }
            } else {
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    if (ObjectUtils.isNotEmpty(referencedColumnInfo.getComposites())) {
                        referencedColumnInfo = referencedColumnInfo.getComposites().get(0);
                    }
                    String leftExpression = String.format("%s.%s", leftEntityTableNameAlias, foreignKeyColumnInfo.getDbColumnName());
                    String rightExpression = String.format("%s.%s", rightEntityTableNameAlias, referencedColumnInfo.getDbColumnName());
                    EqualsTo onCondition = MybatisgxSqlBuilder.eq(leftExpression, rightExpression);
                    onExpressionList.add(onCondition);
                }
            }
            join.addOnExpression(selectFromJoinClauseBuilder.combineAnd(onExpressionList));
        }

        private void buildEntityTableOnMiddleTable(String entityTableNameAlias, String middleTableName, List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
            List<Expression> onExpressionList = new ArrayList<>();
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", entityTableNameAlias, referencedColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
                EqualsTo onCondition = MybatisgxSqlBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
            join.addOnExpression(selectFromJoinClauseBuilder.combineAnd(onExpressionList));
        }

        private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableNameAlias, List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
            List<Expression> onExpressionList = new ArrayList<>();
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", entityTableNameAlias, referencedColumnInfo.getDbColumnName());
                EqualsTo onCondition = MybatisgxSqlBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
            join.addOnExpression(selectFromJoinClauseBuilder.combineAnd(onExpressionList));
        }
    }

    private abstract static class AbstractRelationSelect {

        protected String getRightEq(ColumnInfo columnInfo) {
            List<String> pathList = new ArrayList<>();
            pathList.add("item");
            pathList.addAll(columnInfo.getJavaColumnNamePathList());
            return String.format("#{%s}", StringUtils.join(pathList, "."));
        }
    }

    private static class SimpleRelationSelect extends AbstractRelationSelect {

        public Expression buildOneToOneWhere(ResultMapInfo entityRelationSelectInfo) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                Expression whereCondition = null;
                for (ForeignKeyInfo inverseForeignKeyColumnInfo : mappedByRelationColumnInfo.getInverseForeignKeyInfoList()) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    String leftEq = String.format("%s.%s", entityRelationSelectInfo.getTableNameAlias(), foreignKeyColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s}", foreignKeyColumnInfo.getJavaColumnName());
                    whereCondition = RelationSelectHelper.buildWhereConditionExpression(whereCondition, leftEq, rightEq);
                }
                return whereCondition;
            } else {
                Expression whereCondition = null;
                for (ForeignKeyInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                    if (ObjectUtils.isNotEmpty(referencedColumnInfo.getComposites())) {
                        referencedColumnInfo = referencedColumnInfo.getComposites().get(0);
                    }
                    String leftEq = String.format("%s.%s", entityRelationSelectInfo.getTableNameAlias(), referencedColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s}", foreignKeyColumnInfo.getJavaColumnName());
                    whereCondition = RelationSelectHelper.buildWhereConditionExpression(whereCondition, leftEq, rightEq);
                }
                return whereCondition;
            }
        }

        public Expression buildManyToManyWhere(ResultMapInfo entityRelationSelectInfo) {
            String middleTableName = entityRelationSelectInfo.getMiddleTableName();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
                List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyInfoList();
                return this.buildManyToManyWhereExpression(middleTableName, inverseForeignKeyColumnInfoList);
            } else {
                // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
                List<ForeignKeyInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyInfoList();
                return this.buildManyToManyWhereExpression(middleTableName, foreignKeyColumnInfoList);
            }
        }

        /**
         * 将表达式添加到条件树
         *
         * @param middleTableName          中间表名称
         * @param foreignKeyColumnInfoList 外键字段列表
         * @return
         */
        public Expression buildManyToManyWhereExpression(String middleTableName, List<ForeignKeyInfo> foreignKeyColumnInfoList) {
            Expression whereCondition = null;
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                String leftEq = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
                String rightEq = String.format("#{%s}", foreignKeyColumnInfo.getJavaColumnName());
                whereCondition = RelationSelectHelper.buildWhereConditionExpression(whereCondition, leftEq, rightEq);
            }
            return whereCondition;
        }
    }

    private static class BatchRelationSelect extends AbstractRelationSelect {

        public Expression buildOneToOneWhere(ResultMapInfo resultMapInfo) {
            EntityInfo relationEntityInfo = resultMapInfo.getEntityInfo();
            IdColumnInfo idColumnInfo = relationEntityInfo.getIdColumnInfo();
            List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
            if (relationColumnInfo.getMappedByRelationColumnInfo() != null) {
                Expression whereConditionExpression = null;
                if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                    String leftEq = String.format("%s.%s", resultMapInfo.getTableNameAlias(), idColumnInfo.getDbColumnName());
                    String rightEq = this.getRightEq(idColumnInfo);
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                } else {
                    for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                        String leftEq = String.format("%s.%s", resultMapInfo.getTableNameAlias(), idColumnInfoComposite.getDbColumnName());
                        String rightEq = this.getRightEq(idColumnInfoComposite);
                        whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                    }
                }
                return whereConditionExpression;
            } else {
                Expression whereConditionExpression = null;
                if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                    String leftEq = String.format("%s.%s", resultMapInfo.getTableNameAlias(), idColumnInfo.getDbColumnName());
                    String rightEq = this.getRightEq(idColumnInfo);
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                } else {
                    for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                        String leftEq = String.format("%s.%s", resultMapInfo.getTableNameAlias(), idColumnInfoComposite.getDbColumnName());
                        String rightEq = this.getRightEq(idColumnInfoComposite);
                        whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                    }
                }
                return whereConditionExpression;
            }
        }

        public Expression buildManyToManyWhere(ResultMapInfo entityRelationSelectInfo) {
            EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
            IdColumnInfo idColumnInfo = relationEntityInfo.getIdColumnInfo();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            if (relationColumnInfo.getMappedByRelationColumnInfo() != null) {
                return this.buildManyToManyWhereExpression(entityRelationSelectInfo.getTableNameAlias(), idColumnInfo);
            } else {
                return this.buildManyToManyWhereExpression(entityRelationSelectInfo.getTableNameAlias(), idColumnInfo);
            }
        }

        public Expression buildManyToManyWhereExpression(String leftTableNameAlias, IdColumnInfo idColumnInfo) {
            List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
            Expression whereConditionExpression = null;
            if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                String leftEq = String.format("%s.%s", leftTableNameAlias, idColumnInfo.getDbColumnName());
                String rightEq = this.getRightEq(idColumnInfo);
                whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
            } else {
                for (ColumnInfo idColumnComposite : idColumnInfoComposites) {
                    String leftEq = String.format("%s.%s", leftTableNameAlias, idColumnComposite.getDbColumnName());
                    String rightEq = this.getRightEq(idColumnComposite);
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                }
            }
            return whereConditionExpression;
        }
    }

    private static class EntityContext {

        private ColumnEntityRelation columnEntityRelation;

        private EntityInfo entityInfo;

        private Boolean isBatch;

        public EntityContext(ColumnEntityRelation columnEntityRelation, EntityInfo entityInfo, Boolean isBatch) {
            this.columnEntityRelation = columnEntityRelation;
            this.entityInfo = entityInfo;
            this.isBatch = isBatch;
        }

        public ColumnEntityRelation getColumnEntityRelation() {
            return columnEntityRelation;
        }

        public EntityInfo getEntityInfo() {
            return entityInfo;
        }

        public Boolean getBatch() {
            return isBatch;
        }
    }
}