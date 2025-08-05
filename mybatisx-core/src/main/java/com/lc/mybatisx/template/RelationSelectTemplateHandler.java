package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
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

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        Map<String, EntityRelationSelectInfo> entityRelationSelectInfoMap = mapperInfo.getEntityRelationSelectInfoMap();
        entityRelationSelectInfoMap.forEach((select, entityRelationSelectInfo) -> {
            String sql = this.buildJoinSelect(entityRelationSelectInfo);
            Map<String, XNode> entityRelationSelectXNodeMap = this.buildSelect(entityRelationSelectInfo, sql);
            if (ObjectUtils.isNotEmpty(entityRelationSelectXNodeMap)) {
                totalXNodeMap.putAll(entityRelationSelectXNodeMap);
            }
        });
        return totalXNodeMap;
    }

    private Map<String, XNode> buildSelect(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo();
        String selectXmlString;
        ManyToMany manyToMany = columnInfoAnnotationInfo.getManyToMany();
        if (manyToMany == null) {
            selectXmlString = this.buildOneToOne(entityRelationSelectInfo, sql);
        } else {
            selectXmlString = this.buildManyToMany(entityRelationSelectInfo, sql);
        }
        Map<String, XNode> entityRelationSelectXNodeMap = new HashMap();
        if (StringUtils.isNotBlank(selectXmlString)) {
            logger.info("auto relation select sql: \n{}", selectXmlString);
            XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/select");
            entityRelationSelectXNodeMap.put(entityRelationSelectInfo.getId(), xNode);
        }
        return entityRelationSelectXNodeMap;
    }

    /**
     * select * from user left join user_detail left join user_role left join role where id = #{id}
     *
     * @param entityRelationSelectInfo
     * @return
     */
    private String buildJoinSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        SelectSqlTemplateHandler selectSqlTemplateHandler = new SelectSqlTemplateHandler();
        try {
            return selectSqlTemplateHandler.buildSelectSql(entityRelationSelectInfo);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildOneToOne(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        String fetchSize = columnInfo.getColumnInfoAnnotationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }
        this.buildSelectSqlXNode(selectElement, entityRelationSelectInfo, sql);
        return document.asXML();
    }

    private String buildManyToMany(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        String fetchSize = columnInfo.getColumnInfoAnnotationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }
        this.buildManyToManySelectSqlXNode(selectElement, entityRelationSelectInfo, sql);
        return document.asXML();
    }

    private void buildSelectSqlXNode(Element selectElement, EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        selectElement.addText(sql);
        Element whereElement = selectElement.addElement("where");
        Element trimElement = whereElement.addElement("trim");
        trimElement.addAttribute("prefix", "");
        trimElement.addAttribute("suffix", "");
        trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo();
        String mappedBy = columnInfoAnnotationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnInfoAnnotationInfo mappedByColumnInfoAnnotationInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s}", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            trimElement.addText(whereCondition.toString());
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s}", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            trimElement.addText(whereCondition.toString());
        }
    }

    private void buildManyToManySelectSqlXNode(Element selectElement, EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        selectElement.addText(sql);
        Element whereElement = selectElement.addElement("where");
        Element whereTrimElement = whereElement.addElement("trim");
        whereTrimElement.addAttribute("prefix", "");
        whereTrimElement.addAttribute("suffix", "");
        whereTrimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        String middleTableName = entityRelationSelectInfo.getMiddleTableName();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo();
        String mappedBy = columnInfoAnnotationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnInfoAnnotationInfo mappedByColumnInfoAnnotationInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", middleTableName, inverseForeignKeyColumnInfo.getName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s}", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            whereTrimElement.addText(whereCondition.toString());
        } else {
            // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfoAnnotationInfo.getForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getName());
                String rightEq = foreignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s}", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            whereTrimElement.addText(whereCondition.toString());
        }
    }
}