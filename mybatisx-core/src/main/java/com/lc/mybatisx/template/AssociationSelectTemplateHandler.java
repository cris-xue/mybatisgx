package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
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

public class AssociationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssociationSelectTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        List<ResultMapInfo> resultMapInfoList = mapperInfo.getResultMapInfoList();
        resultMapInfoList.forEach(resultMapInfo -> {
            Map<String, XNode> xNodeMap = this.buildOneToOne(resultMapInfo.getResultMapAssociationInfoList());
            if (ObjectUtils.isNotEmpty(xNodeMap)) {
                totalXNodeMap.putAll(xNodeMap);
            }
        });
        return totalXNodeMap;
    }

    private Map<String, XNode> buildOneToOne(List<ResultMapAssociationInfo> resultMapAssociationInfoList) {
        Map<String, XNode> xNodeMap = new HashMap();
        for (int i = 0; i < resultMapAssociationInfoList.size(); i++) {
            ResultMapAssociationInfo resultMapAssociationInfo = resultMapAssociationInfoList.get(i);
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.buildOneToOne(subResultMapAssociationInfoList);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    xNodeMap.putAll(subXNodeMap);
                }
            }
            AssociationEntityInfo associationEntityInfo = resultMapAssociationInfo.getColumnInfo().getAssociationEntityInfo();
            OneToOne oneToOne = associationEntityInfo.getOneToOne();
            XNode xNode = null;
            if (oneToOne != null) {
                xNode = this.buildOneToOne(resultMapAssociationInfo);
            }
            OneToMany oneToMany = associationEntityInfo.getOneToMany();
            if (oneToMany != null) {
                xNode = this.buildOneToOne(resultMapAssociationInfo);
            }
            ManyToOne manyToOne = associationEntityInfo.getManyToOne();
            if (manyToOne != null) {
                xNode = this.buildOneToOne(resultMapAssociationInfo);
            }
            ManyToMany manyToMany = associationEntityInfo.getManyToMany();
            if (manyToMany != null) {
                xNode = this.buildManyToMany(resultMapAssociationInfo);
            }
            if (xNode != null) {
                xNodeMap.put(resultMapAssociationInfo.getSelect(), xNode);
            }
        }
        return xNodeMap;
    }

    private XNode buildOneToOne(ResultMapAssociationInfo resultMapAssociationInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }

            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo targetEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo targetColumnInfo = targetEntityInfo.getColumnInfo(mappedBy);
            AssociationEntityInfo targetAssociationEntityInfo = targetColumnInfo.getAssociationEntityInfo();
            this.buildSelectSqlXNode(selectElement, mappedBy, targetEntityInfo, targetAssociationEntityInfo);
        } else {
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }

            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo targetEntityInfo = EntityInfoContextHolder.get(javaType);
            this.buildSelectSqlXNode(selectElement, mappedBy, targetEntityInfo, associationEntityInfo);
        }

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private XNode buildManyToMany(ResultMapAssociationInfo resultMapAssociationInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        String mappedBy = columnInfo.getAssociationEntityInfo().getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinTable joinTable = mappedByColumnInfo.getAssociationEntityInfo().getJoinTable();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getInverseForeignKeyColumnInfoList();
            // select role.* from user_role left join role on user_role.role_id = role.id where user_role.user_id = #{userId}
            selectElement.addText(
                    String.format(
                            "select * from %s left join %s on(%s.%s=%s.%s) where %s = #{%s}",
                            joinTable.name(),
                            associationEntityInfo.getTableName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName(),
                            associationEntityInfo.getTableName(),
                            inverseForeignKeyColumnInfoList.get(0).getName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName()
                    )
            );
        } else {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            JoinTable joinTable = columnInfo.getAssociationEntityInfo().getJoinTable();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnInfo.getAssociationEntityInfo().getInverseForeignKeyColumnInfoList();
            // select user.* from user_role left join user on user_role.user_id = user.id where user_role.role_id = ?
            selectElement.addText(
                    String.format(
                            "select * from %s left join %s on(%s.%s=%s.%s) where %s = #{%s}",
                            joinTable.name(),
                            associationEntityInfo.getTableName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName(),
                            associationEntityInfo.getTableName(),
                            inverseForeignKeyColumnInfoList.get(0).getName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName()
                    )
            );
        }

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private XNode buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", methodInfo.getResultMapId());

        // this.buildSelectSqlXNode(selectElement, mapperInfo, methodInfo);

        String insertXmlString = document.asXML();
        logger.debug(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        return xPathParser.evalNode("/mapper/select");
    }

    private void buildSelectSqlXNode(Element selectElement, String mappedBy, EntityInfo queryEntityInfo, AssociationEntityInfo associationEntityInfo) {
        selectElement.addText("select");
        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            AssociationEntityInfo queryAssociationEntityInfo = queryColumnInfo.getAssociationEntityInfo();
            if (queryAssociationEntityInfo == null) {
                dbTrimElement.addText(String.format("%s, ", queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    dbTrimElement.addText(String.format("%s, ", foreignKeyColumnInfo.getName()));
                });
            }
        });

        selectElement.addText(String.format("from %s", queryEntityInfo.getTableName()));

        Element whereElement = selectElement.addElement("where");
        Element trimElement = whereElement.addElement("trim");
        trimElement.addAttribute("prefix", "");
        trimElement.addAttribute("suffix", "");
        trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
        if (StringUtils.isNotBlank(mappedBy)) {
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getName(), "=", foreignKeyColumnInfo.getName());
                trimElement.addText(conditionOp);
            });
        } else {
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getReferencedColumnName(), "=", foreignKeyColumnInfo.getReferencedColumnName());
                trimElement.addText(conditionOp);
            });
        }
    }
}