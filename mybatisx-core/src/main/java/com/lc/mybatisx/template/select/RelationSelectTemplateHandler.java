package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
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
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = mapperInfo.getEntityRelationSelectInfoList();
        for (EntityRelationSelectInfo entityRelationSelectInfo : entityRelationSelectInfoList) {
            Map<String, XNode> entityRelationSelectXNodeMap = this.buildSelect(entityRelationSelectInfo);
            if (ObjectUtils.isNotEmpty(entityRelationSelectXNodeMap)) {
                totalXNodeMap.putAll(entityRelationSelectXNodeMap);
            }
        }
        return totalXNodeMap;
    }

    private Map<String, XNode> buildSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        String selectXmlString = this.buildDocumentString(entityRelationSelectInfo);
        Map<String, XNode> entityRelationSelectXNodeMap = new HashMap();
        if (StringUtils.isNotBlank(selectXmlString)) {
            logger.info("auto relation select sql: \n{}", selectXmlString);
            XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/select");
            entityRelationSelectXNodeMap.put(entityRelationSelectInfo.getId(), xNode);
        }
        return entityRelationSelectXNodeMap;
    }

    private String buildDocumentString(EntityRelationSelectInfo entityRelationSelectInfo) {
        String selectSql = this.buildJoinSelect(entityRelationSelectInfo);
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = RelationSelectHelper.buildSelectElement(mapperElement, entityRelationSelectInfo, selectSql);

        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
        ManyToMany manyToMany = relationColumnInfo.getManyToMany();
        if (manyToMany == null) {
            if (relationColumnInfo.getFetchMode() == FetchMode.BATCH) {
                Expression whereCondition = batchRelationSelect.buildOneToOneWhere(entityRelationSelectInfo);
                Element whereElement = RelationSelectHelper.buildWhereElement(selectElement);
                RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
            } else {
                Expression whereCondition = simpleRelationSelect.buildOneToOneWhere(entityRelationSelectInfo);
                RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
            }
        } else {
            if (relationColumnInfo.getFetchMode() == FetchMode.BATCH) {
                Expression whereCondition = batchRelationSelect.buildManyToManyWhere(entityRelationSelectInfo);
                Element whereElement = RelationSelectHelper.buildWhereElement(selectElement);
                RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
            } else {
                Expression whereCondition = simpleRelationSelect.buildManyToManyWhere(entityRelationSelectInfo);
                RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
            }
        }
        return document.asXML();
    }

    private String buildJoinSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        try {
            return selectSqlTemplateHandler.buildSelectSql(entityRelationSelectInfo);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SimpleRelationSelect {

        public Expression buildOneToOneWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
            EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                Expression whereCondition = null;
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), foreignKeyColumnInfo.getDbColumnName());
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
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), referencedColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s}", foreignKeyColumnInfo.getJavaColumnName());
                    whereCondition = RelationSelectHelper.buildWhereConditionExpression(whereCondition, leftEq, rightEq);
                }
                return whereCondition;
            }
        }

        public Expression buildManyToManyWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
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
         * @param middleTableName 中间表名称
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

        public Expression buildOneToOneWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
            EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (mappedByRelationColumnInfo != null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                Expression whereConditionExpression = null;
                // FROM user_detail WHERE user_detail.user_id = #{user.id} or user_detail.user_id = #{user.id}
                for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), foreignKeyColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s.%s}", "item", referencedColumnInfo.getJavaColumnPath());
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                }
                return whereConditionExpression;
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                Expression whereConditionExpression = null;
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), referencedColumnInfo.getDbColumnName());
                    String rightEq = String.format("#{%s.%s.%s}", "item", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnPath());
                    whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
                }
                return whereConditionExpression;
            }
        }

        public Expression buildManyToManyWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
            EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
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
         * @param middleTableName 中间表名称
         * @param foreignKeyColumnInfoList 外键字段列表
         * @return
         */
        public Expression buildManyToManyWhereExpression(String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
            Expression whereConditionExpression = null;
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String leftEq = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
                String rightEq = String.format("#{%s.%s}", "item", referencedColumnInfo.getDbColumnName());
                whereConditionExpression = RelationSelectHelper.buildWhereConditionExpression(whereConditionExpression, leftEq, rightEq);
            }
            return whereConditionExpression;
        }
    }
}