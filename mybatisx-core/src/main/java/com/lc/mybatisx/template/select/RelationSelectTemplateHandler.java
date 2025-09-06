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

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        Map<String, EntityRelationSelectInfo> entityRelationSelectInfoMap = mapperInfo.getEntityRelationSelectInfoMap();
        entityRelationSelectInfoMap.forEach((select, entityRelationSelectInfo) -> {
            Map<String, XNode> entityRelationSelectXNodeMap = this.buildSelect(entityRelationSelectInfo);
            if (ObjectUtils.isNotEmpty(entityRelationSelectXNodeMap)) {
                totalXNodeMap.putAll(entityRelationSelectXNodeMap);
            }
        });
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

        ColumnRelationInfo columnRelationInfo = entityRelationSelectInfo.getColumnInfo().getColumnRelationInfo();
        ManyToMany manyToMany = columnRelationInfo.getManyToMany();
        if (manyToMany == null) {
            if (columnRelationInfo.getFetchMode() == FetchMode.BATCH) {
                RelationSelectOrTemplateHandler relationSelectOrTemplateHandler = new RelationSelectOrTemplateHandler();
                Expression whereCondition = relationSelectOrTemplateHandler.buildSelectSqlXNode(entityRelationSelectInfo);
                Element whereElement = RelationSelectHelper.buildWhereElement(selectElement);
                RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
                return document.asXML();
            } else {
                Expression whereCondition = this.buildOneToOneWhere(entityRelationSelectInfo);
                RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
                return document.asXML();
            }
        } else {
            Expression whereCondition = this.buildManyToManyWhere(entityRelationSelectInfo);
            RelationSelectHelper.buildWhereElement(selectElement, whereCondition);
            return document.asXML();
        }
    }

    private Expression buildOneToOneWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        ColumnRelationInfo columnRelationInfo = entityRelationSelectInfo.getColumnInfo().getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                whereCondition = RelationSelectHelper.buildWhereCondition(whereCondition, leftEq, rightEq);
            }
            return whereCondition;
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                whereCondition = RelationSelectHelper.buildWhereCondition(whereCondition, leftEq, rightEq);
            }
            return whereCondition;
        }
    }

    private Expression buildManyToManyWhere(EntityRelationSelectInfo entityRelationSelectInfo) {
        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        String middleTableName = entityRelationSelectInfo.getMiddleTableName();
        ColumnRelationInfo columnRelationInfo = entityRelationSelectInfo.getColumnInfo().getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
            return RelationSelectHelper.buildManyToManyWhere(middleTableName, inverseForeignKeyColumnInfoList);
        } else {
            // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnRelationInfo.getForeignKeyColumnInfoList();
            return RelationSelectHelper.buildManyToManyWhere(middleTableName, foreignKeyColumnInfoList);
        }
    }

    private String buildJoinSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        try {
            return selectSqlTemplateHandler.buildSelectSql(entityRelationSelectInfo);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }
}