package com.mybatisgx.template.select;

import com.mybatisgx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import com.mybatisgx.utils.XmlUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RelationSelectTemplateHandler.class);

    private SelectSqlTemplateHandler selectSqlTemplateHandler = new SelectSqlTemplateHandler();
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
            logger.info("auto relation select sql: \n{}", selectXmlString);
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

        if (TypeUtils.typeEquals(resultMapInfo, ResultMapInfo.class) || TypeUtils.typeEquals(resultMapInfo, SimpleNestedResultMapInfo.class)) {
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
        try {
            return selectSqlTemplateHandler.buildSelectSql(resultMapInfo);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SimpleRelationSelect {

        public Expression buildOneToOneWhere(ResultMapInfo entityRelationSelectInfo) {
            EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                Expression whereCondition = null;
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), foreignKeyColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s}", foreignKeyColumnInfo.getJavaColumnName());
                    whereCondition = RelationSelectHelper.buildWhereConditionExpression(whereCondition, leftEq, rightEq);
                }
                return whereCondition;
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                Expression whereCondition = null;
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), referencedColumnInfo.getDbColumnName());
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
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                return this.buildManyToManyWhereExpression(middleTableName, inverseForeignKeyColumnInfoList);
            } else {
                // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyColumnInfoList();
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
        public Expression buildManyToManyWhereExpression(String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
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

    private static class BatchRelationSelect {

        public Expression buildOneToOneWhere(ResultMapInfo resultMapInfo) {
            EntityInfo relationEntityInfo = resultMapInfo.getEntityInfo();
            IdColumnInfo idColumnInfo = relationEntityInfo.getIdColumnInfo();
            List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                Expression whereConditionExpression = null;
                if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), idColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s.%s}", "item", idColumnInfo.getJavaColumnName());
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                } else {
                    for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                        String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), idColumnInfoComposite.getDbColumnName());
                        String rightEq = String.format("#{%s.%s.%s}", "item", idColumnInfo.getJavaColumnName(), idColumnInfoComposite.getJavaColumnName());
                        whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                    }
                }
                return whereConditionExpression;
            } else {
                Expression whereConditionExpression = null;
                if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), idColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s.%s}", "item", idColumnInfo.getJavaColumnName());
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                } else {
                    for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                        String leftEq = String.format("%s.%s", relationEntityInfo.getTableNameAlias(), idColumnInfoComposite.getDbColumnName());
                        String rightEq = String.format("#{%s.%s.%s}", "item", idColumnInfo.getJavaColumnName(), idColumnInfoComposite.getJavaColumnName());
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
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                return this.buildManyToManyWhereExpression(relationEntityInfo.getTableNameAlias(), idColumnInfo);
            } else {
                return this.buildManyToManyWhereExpression(relationEntityInfo.getTableNameAlias(), idColumnInfo);
            }
        }

        public Expression buildManyToManyWhereExpression(String leftTableNameAlias, IdColumnInfo idColumnInfo) {
            List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
            Expression whereConditionExpression = null;
            if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                String leftEq = String.format("%s.%s", leftTableNameAlias, idColumnInfo.getDbColumnName());
                String rightEq = String.format("#{%s.%s}", "item", idColumnInfo.getDbColumnName());
                whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
            } else {
                for (ColumnInfo idColumnComposite : idColumnInfoComposites) {

                }
            }
            return whereConditionExpression;
        }
    }
}