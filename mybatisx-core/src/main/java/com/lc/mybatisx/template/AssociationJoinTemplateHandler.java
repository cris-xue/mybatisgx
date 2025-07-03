package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.ManyToOne;
import com.lc.mybatisx.annotation.OneToMany;
import com.lc.mybatisx.annotation.OneToOne;
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

public class AssociationJoinTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssociationJoinTemplateHandler.class);

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
        selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
        selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }

        Class<?> javaType = columnInfo.getJavaType();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        EntityInfo queryEntityInfo = EntityInfoContextHolder.get(javaType);
        this.buildSelectSqlXNode(selectElement, queryEntityInfo, associationEntityInfo);

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
        selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
        selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }

        Class<?> javaType = columnInfo.getJavaType();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        EntityInfo queryEntityInfo = EntityInfoContextHolder.get(javaType);
        this.buildManyToManySelectSqlXNode(selectElement, queryEntityInfo, associationEntityInfo);

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private void buildSelectSqlXNode(Element selectElement, EntityInfo queryEntityInfo, AssociationEntityInfo associationEntityInfo) {
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

        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            AssociationEntityInfo targetAssociationEntityInfo = targetColumnInfo.getAssociationEntityInfo();
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = targetAssociationEntityInfo.getForeignKeyColumnInfoList();
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getName(), "=", foreignKeyColumnInfo.getName());
                trimElement.addText(conditionOp);
            });
        } else {
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getReferencedColumnName(), "=", foreignKeyColumnInfo.getReferencedColumnName());
                trimElement.addText(conditionOp);
            });
        }
    }

    private void buildManyToManySelectSqlXNode(Element selectElement, EntityInfo queryEntityInfo, AssociationEntityInfo associationEntityInfo) {
        selectElement.addText("select");
        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            AssociationEntityInfo queryAssociationEntityInfo = queryColumnInfo.getAssociationEntityInfo();
            if (queryAssociationEntityInfo == null) {
                dbTrimElement.addText(String.format("%s.%s, ", queryEntityInfo.getTableName(), queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    dbTrimElement.addText(String.format("%s.%s, ", queryEntityInfo.getTableName(), foreignKeyColumnInfo.getName()));
                });
            }
        });

        this.fromLeftJoin(selectElement, associationEntityInfo, queryEntityInfo);
        this.where(selectElement, associationEntityInfo, queryEntityInfo);
    }

    private void fromLeftJoin(Element selectElement, AssociationEntityInfo associationEntityInfo, EntityInfo queryEntityInfo) {
        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            AssociationEntityInfo targetAssociationEntityInfo = targetColumnInfo.getAssociationEntityInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = targetAssociationEntityInfo.getInverseForeignKeyColumnInfoList();
            selectElement.addText(String.format("from "));
            selectElement.addText(String.format("%s left join %s on ", targetAssociationEntityInfo.getJoinTable().name(), queryEntityInfo.getTableName()));
            inverseForeignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                selectElement.addText(
                        String.format(
                                "%s.%s = %s.%s",
                                targetAssociationEntityInfo.getJoinTable().name(),
                                foreignKeyColumnInfo.getName(),
                                queryEntityInfo.getTableName(),
                                foreignKeyColumnInfo.getReferencedColumnName()
                        )
                );
            });
        } else {
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
            selectElement.addText(String.format("from "));
            selectElement.addText(String.format("%s left join %s on ", associationEntityInfo.getJoinTable().name(), queryEntityInfo.getTableName()));
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                selectElement.addText(
                        String.format(
                                "%s.%s = %s.%s",
                                associationEntityInfo.getJoinTable().name(),
                                foreignKeyColumnInfo.getName(),
                                queryEntityInfo.getTableName(),
                                foreignKeyColumnInfo.getReferencedColumnName()
                        )
                );
            });
        }
    }

    private void where(Element selectElement, AssociationEntityInfo associationEntityInfo, EntityInfo queryEntityInfo) {
        Element whereElement = selectElement.addElement("where");
        Element whereTrimElement = whereElement.addElement("trim");
        whereTrimElement.addAttribute("prefix", "");
        whereTrimElement.addAttribute("suffix", "");
        whereTrimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            AssociationEntityInfo targetAssociationEntityInfo = targetColumnInfo.getAssociationEntityInfo();
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = targetAssociationEntityInfo.getForeignKeyColumnInfoList();
            // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(
                        " %s %s.%s %s #{%s}",
                        "and",
                        targetAssociationEntityInfo.getJoinTable().name(),
                        foreignKeyColumnInfo.getName(),
                        "=",
                        foreignKeyColumnInfo.getName()
                );
                whereTrimElement.addText(conditionOp);
            });
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = associationEntityInfo.getInverseForeignKeyColumnInfoList();
            // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
            inverseForeignKeyColumnInfoList.forEach(inverseForeignKeyColumnInfo -> {
                String conditionOp = String.format(
                        " %s %s.%s %s #{%s}",
                        "and",
                        associationEntityInfo.getJoinTable().name(),
                        inverseForeignKeyColumnInfo.getName(),
                        "=",
                        inverseForeignKeyColumnInfo.getName()
                );
                whereTrimElement.addText(conditionOp);
            });
        }
    }
}