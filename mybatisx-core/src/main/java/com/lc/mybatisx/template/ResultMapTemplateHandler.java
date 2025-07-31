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

public class ResultMapTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> xNodeMap = new HashMap();
        List<ResultMapInfo> resultMapInfoList = mapperInfo.getResultMapInfoList();
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            Document document = DocumentHelper.createDocument();
            Element resultMapElement = this.addResultMapElement(document, resultMapInfo);
            this.addColumnElement(resultMapElement, resultMapInfo.getTableColumnInfoList());
            Map<String, XNode> refResultMapXNodeMap = this.addQueryElement(resultMapElement, mapperInfo, resultMapInfo.getResultMapRelationInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.debug(resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
            xNodeMap.putAll(refResultMapXNodeMap);
        }
        return xNodeMap;
    }

    private Element addResultMapElement(Document document, ResultMapInfo resultMapInfo) {
        Element mapperElement = document.addElement("mapper");
        Element resultMapElement = mapperElement.addElement("resultMap");
        resultMapElement.addAttribute("id", resultMapInfo.getId());
        resultMapElement.addAttribute("type", resultMapInfo.getEntityClazzName());
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
            ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
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

    private Map<String, XNode> addQueryElement(Element resultMapElement, MapperInfo mapperInfo, List<ResultMapRelationInfo> resultMapRelationInfoList) {
        if (ObjectUtils.isEmpty(resultMapRelationInfoList)) {
            return new HashMap();
        }
        Map<String, XNode> refResultMapXNodeMap = new HashMap();
        for (ResultMapRelationInfo resultMapRelationInfo : resultMapRelationInfoList) {
            ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultMapRelationInfo.getEntityClazz());
            if (resultMapInfo != null) {
                this.subQuery(resultMapRelationInfo, mapperInfo, resultMapElement, refResultMapXNodeMap);
            } else {
                Element resultMapRelationElement = this.joinQuery(resultMapRelationInfo, mapperInfo, resultMapElement, refResultMapXNodeMap);
                List<ResultMapRelationInfo> childrenResultMapRelationInfoList = resultMapRelationInfo.getResultMapRelationInfoList();
                if (ObjectUtils.isNotEmpty(childrenResultMapRelationInfoList)) {
                    this.addQueryElement(resultMapRelationElement, mapperInfo, childrenResultMapRelationInfoList);
                }
            }
        }
        return refResultMapXNodeMap;
    }

    private void subQuery(ResultMapRelationInfo resultMapRelationInfo, MapperInfo mapperInfo, Element resultMapElement, Map<String, XNode> refResultMapXNodeMap) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        Integer associationType = this.getAssociationType(associationEntityInfo);
        if (associationType == 1) {
            this.associationColumnElement(resultMapElement, resultMapRelationInfo);
        } else if (associationType == 2) {
            this.collectionColumnElement(resultMapElement, resultMapRelationInfo);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinQuery(ResultMapRelationInfo resultMapRelationInfo, MapperInfo mapperInfo, Element resultMapElement, Map<String, XNode> refResultMapXNodeMap) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        Integer associationType = this.getAssociationType(associationEntityInfo);
        if (associationType == 1) {
            Element resultMapAssociationElement = this.joinAssociationColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapAssociationElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapAssociationElement;
        } else if (associationType == 2) {
            Element resultMapCollectionElement = this.joinCollectionColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapCollectionElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapCollectionElement;
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element associationColumnElement(Element resultMapElement, ResultMapRelationInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", this.getColumn(columnInfo).toString());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapRelationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
    }

    private Element joinAssociationColumnElement(Element resultMapElement, ResultMapRelationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    private Element collectionColumnElement(Element resultMapElement, ResultMapRelationInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("collection");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", this.getColumn(columnInfo).toString());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapAssociationElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapRelationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
    }

    private Element joinCollectionColumnElement(Element resultMapElement, ResultMapRelationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("collection");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapAssociationElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    private Integer getAssociationType(ColumnInfoAnnotationInfo associationEntityInfo) {
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
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        ManyToMany manyToMany = associationEntityInfo.getManyToMany();
        if (manyToMany == null) {
            String mappedBy = associationEntityInfo.getMappedBy();
            Map<String, String> column = new HashMap();
            if (StringUtils.isNotBlank(mappedBy)) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnInfoAnnotationInfo mappedByAssociationEntityInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
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
                ColumnInfoAnnotationInfo mappedByAssociationEntityInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
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
