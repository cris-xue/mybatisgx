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

public class ResultMapSubQueryTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapSubQueryTemplateHandler.class);

    public Map<String, XNode> execute(List<ResultMapInfo> resultMapInfoList) {
        Map<String, XNode> xNodeMap = new HashMap();
        resultMapInfoList.forEach(resultMapInfo -> {
            Document document = DocumentHelper.createDocument();
            Element resultMapElement = this.addResultMapElement(document, resultMapInfo);
            this.addColumnElement(resultMapElement, resultMapInfo.getColumnInfoList());
            Map<String, XNode> refResultMapXNodeMap = this.addAssociationElement(resultMapElement, resultMapInfo.getResultMapAssociationInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.debug(resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
            xNodeMap.putAll(refResultMapXNodeMap);
        });
        return xNodeMap;
    }

    /**
     * 子查询只能引用resultMap，不能使用嵌套模式，这里把ResultMapAssociationInfo转成ResultMapInfo
     *
     * @param document
     * @param resultMapAssociationInfo
     * @return
     */
    private Element addResultMapElement(Document document, ResultMapAssociationInfo resultMapAssociationInfo) {
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(resultMapAssociationInfo.getResultMapId());
        resultMapInfo.setTypeName(resultMapAssociationInfo.getTypeName());
        return this.addResultMapElement(document, resultMapInfo);
    }

    private Element addResultMapElement(Document document, ResultMapInfo resultMapInfo) {
        Element mapperElement = document.addElement("mapper");
        Element resultMapElement = mapperElement.addElement("resultMap");
        resultMapElement.addAttribute("id", resultMapInfo.getId());
        resultMapElement.addAttribute("type", resultMapInfo.getTypeName());
        return resultMapElement;
    }


    private void addColumnElement(Element resultMapElement, List<ColumnInfo> columnInfoList) {
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            Id id = columnInfo.getId();
            if (id != null) {
                idColumnElement(resultMapElement, columnInfo);
                continue;
            }
            AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
            if (associationEntityInfo == null) {
                resultColumnElement(resultMapElement, columnInfo);
            }
        }
    }

    private void idColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element element = resultMapElement.addElement("id");
        element.addAttribute("property", columnInfo.getJavaColumnName());
        element.addAttribute("column", columnInfo.getDbColumnName());
        String dbTypeName = columnInfo.getDbTypeName();
        element.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
        element.addAttribute("typeHandler", columnInfo.getTypeHandler());
    }

    private void resultColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element element = resultMapElement.addElement("result");
        element.addAttribute("property", columnInfo.getJavaColumnName());
        element.addAttribute("column", columnInfo.getDbColumnName());
        String dbTypeName = columnInfo.getDbTypeName();
        element.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
        element.addAttribute("typeHandler", columnInfo.getTypeHandler());
    }

    private Map<String, XNode> addAssociationElement(Element resultMapElement, List<ResultMapAssociationInfo> resultMapAssociationInfoList) {
        Map<String, XNode> refResultMapXNodeMap = new HashMap();
        resultMapAssociationInfoList.forEach(resultMapAssociationInfo -> {
            ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
            AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
            if (associationEntityInfo.getLoadStrategy() == LoadStrategy.SUB) {
                this.subQuery(resultMapAssociationInfo, resultMapElement, refResultMapXNodeMap);
            } else if (associationEntityInfo.getLoadStrategy() == LoadStrategy.JOIN) {
                this.joinQuery(resultMapAssociationInfo, resultMapElement, refResultMapXNodeMap);
            } else {
                throw new RuntimeException("LoadStrategy is error");
            }
        });
        return refResultMapXNodeMap;
    }

    private void subQuery(ResultMapAssociationInfo resultMapAssociationInfo, Element resultMapElement, Map<String, XNode> refResultMapXNodeMap) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        Integer associationType = this.getAssociationType(associationEntityInfo);
        if (associationType == 1) {
            this.associationColumnElement(resultMapElement, resultMapAssociationInfo);

            Document document = DocumentHelper.createDocument();
            Element refResultMapElement = this.addResultMapElement(document, resultMapAssociationInfo);
            this.addColumnElement(refResultMapElement, resultMapAssociationInfo.getColumnInfoList());
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.addAssociationElement(refResultMapElement, subResultMapAssociationInfoList);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    refResultMapXNodeMap.putAll(subXNodeMap);
                }
            }

            String resultMapXmlString = XmlUtils.writeString(document);
            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            refResultMapXNodeMap.put(resultMapAssociationInfo.getResultMapId(), xNode);
        } else if (associationType == 2) {
            this.collectionColumnElement(resultMapElement, resultMapAssociationInfo);

            Document document = DocumentHelper.createDocument();
            Element refResultMapElement = this.addResultMapElement(document, resultMapAssociationInfo);
            this.addColumnElement(refResultMapElement, resultMapAssociationInfo.getColumnInfoList());
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.addAssociationElement(refResultMapElement, subResultMapAssociationInfoList);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    refResultMapXNodeMap.putAll(subXNodeMap);
                }
            }

            String resultMapXmlString = XmlUtils.writeString(document);
            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            refResultMapXNodeMap.put(resultMapAssociationInfo.getResultMapId(), xNode);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private void joinQuery(ResultMapAssociationInfo resultMapAssociationInfo, Element resultMapElement, Map<String, XNode> refResultMapXNodeMap) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        Integer associationType = this.getAssociationType(associationEntityInfo);
        if (associationType == 1) {
            Element resultMapAssociationElement = this.joinAssociationColumnElement(resultMapElement, resultMapAssociationInfo);
            this.addColumnElement(resultMapAssociationElement, resultMapAssociationInfo.getColumnInfoList());
            Map<String, XNode> subXNodeMap = this.addAssociationElement(resultMapAssociationElement, resultMapAssociationInfo.getResultMapAssociationInfoList());
            if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                refResultMapXNodeMap.putAll(subXNodeMap);
            }
        } else if (associationType == 2) {
            Element resultMapCollectionElement = this.joinCollectionColumnElement(resultMapElement, resultMapAssociationInfo);
            this.addColumnElement(resultMapCollectionElement, resultMapAssociationInfo.getColumnInfoList());
            Map<String, XNode> subXNodeMap = this.addAssociationElement(resultMapCollectionElement, resultMapAssociationInfo.getResultMapAssociationInfoList());
            if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                refResultMapXNodeMap.putAll(subXNodeMap);
            }
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element associationColumnElement(Element resultMapElement, ResultMapAssociationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", this.getColumn(columnInfo).toString());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapAssociationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
    }

    private Element joinAssociationColumnElement(Element resultMapElement, ResultMapAssociationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    private Element collectionColumnElement(Element resultMapElement, ResultMapAssociationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("collection");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", this.getColumn(columnInfo).toString());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapAssociationElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapAssociationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
    }

    private Element joinCollectionColumnElement(Element resultMapElement, ResultMapAssociationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("collection");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapAssociationElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    private Integer getAssociationType(AssociationEntityInfo associationEntityInfo) {
        OneToOne oneToOne = associationEntityInfo.getOneToOne();
        OneToMany oneToMany = associationEntityInfo.getOneToMany();
        ManyToOne manyToOne = associationEntityInfo.getManyToOne();
        ManyToMany manyToMany = associationEntityInfo.getManyToMany();
        if (oneToOne != null) {
            return 1;
        }
        if (oneToMany != null) {
            return 2;
        }
        if (manyToOne != null) {
            return 1;
        }
        if (manyToMany != null) {
            return 2;
        }
        return null;
    }

    private Map<String, String> getColumn(ColumnInfo columnInfo) {
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        ManyToMany manyToMany = associationEntityInfo.getManyToMany();
        if (manyToMany == null) {
            String mappedBy = associationEntityInfo.getMappedBy();
            Map<String, String> column = new HashMap();
            if (StringUtils.isNotBlank(mappedBy)) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                AssociationEntityInfo mappedByAssociationEntityInfo = mappedByColumnInfo.getAssociationEntityInfo();
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByAssociationEntityInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    column.put(foreignKeyColumnInfo.getName(), foreignKeyColumnInfo.getReferencedColumnName());
                }
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    column.put(foreignKeyColumnInfo.getReferencedColumnName(), foreignKeyColumnInfo.getName());
                }
            }
            return column;
        } else {
            String mappedBy = associationEntityInfo.getMappedBy();
            Map<String, String> column = new HashMap();
            if (StringUtils.isNotBlank(mappedBy)) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                AssociationEntityInfo mappedByAssociationEntityInfo = mappedByColumnInfo.getAssociationEntityInfo();
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByAssociationEntityInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    column.put(foreignKeyColumnInfo.getName(), foreignKeyColumnInfo.getReferencedColumnName());
                }
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = associationEntityInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                }
            }
            return column;
        }
    }
}
