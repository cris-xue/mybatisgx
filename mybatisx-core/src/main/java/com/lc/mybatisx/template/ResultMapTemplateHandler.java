package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.*;
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
            Map<String, XNode> refResultMapXNodeMap = this.addResultMapRelationElement(resultMapElement, mapperInfo, resultMapInfo.getResultMapRelationInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.info("select resultMap: \n{}", resultMapXmlString);

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

    private Map<String, XNode> addResultMapRelationElement(Element resultMapElement, MapperInfo mapperInfo, List<ResultMapRelationInfo> resultMapRelationInfoList) {
        if (ObjectUtils.isEmpty(resultMapRelationInfoList)) {
            return new HashMap();
        }
        Map<String, XNode> refResultMapXNodeMap = new HashMap();
        for (ResultMapRelationInfo resultMapRelationInfo : resultMapRelationInfoList) {
            ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultMapRelationInfo.getEntityClazz());
            if (resultMapInfo != null) {
                this.subQuery(resultMapRelationInfo, resultMapElement);
            } else {
                Element resultMapRelationElement = this.joinQuery(resultMapRelationInfo, resultMapElement);
                List<ResultMapRelationInfo> childrenResultMapRelationInfoList = resultMapRelationInfo.getResultMapRelationInfoList();
                if (ObjectUtils.isNotEmpty(childrenResultMapRelationInfoList)) {
                    this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, childrenResultMapRelationInfoList);
                }
            }
        }
        return refResultMapXNodeMap;
    }

    private void subQuery(ResultMapRelationInfo resultMapRelationInfo, Element resultMapElement) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = columnInfo.getColumnInfoAnnotationInfo();
        Integer associationType = this.getRelationType(columnInfoAnnotationInfo);
        if (associationType == 1) {
            this.associationColumnElement(resultMapElement, resultMapRelationInfo);
        } else if (associationType == 2) {
            this.collectionColumnElement(resultMapElement, resultMapRelationInfo);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinQuery(ResultMapRelationInfo resultMapRelationInfo, Element resultMapElement) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = columnInfo.getColumnInfoAnnotationInfo();
        Integer associationType = this.getRelationType(columnInfoAnnotationInfo);
        if (associationType == 1) {
            Element resultMapRelationElement = this.joinAssociationColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapRelationElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapRelationElement;
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
        resultMapAssociationElement.addAttribute("column", this.getColumn(resultMapRelationInfo));
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
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("column", this.getColumn(resultMapRelationInfo));
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapCollectionElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapCollectionElement.addAttribute("select", resultMapRelationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapCollectionElement;
    }

    private Element joinCollectionColumnElement(Element resultMapElement, ResultMapRelationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        return resultMapCollectionElement;
    }

    private Integer getRelationType(ColumnInfoAnnotationInfo columnInfoAnnotationInfo) {
        OneToOne oneToOne = columnInfoAnnotationInfo.getOneToOne();
        OneToMany oneToMany = columnInfoAnnotationInfo.getOneToMany();
        ManyToOne manyToOne = columnInfoAnnotationInfo.getManyToOne();
        ManyToMany manyToMany = columnInfoAnnotationInfo.getManyToMany();
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

    private String getColumn(ResultMapRelationInfo resultMapRelationInfo) {
        EntityInfo entityInfo = resultMapRelationInfo.getEntityInfo();
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = columnInfo.getColumnInfoAnnotationInfo();
        ManyToMany manyToMany = columnInfoAnnotationInfo.getManyToMany();
        Map<String, String> column = new HashMap();
        if (manyToMany == null) {
            String mappedBy = columnInfoAnnotationInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnInfoAnnotationInfo mappedByColumnInfoAnnotationInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                }
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    column.put(inverseForeignKeyColumnInfo.getReferencedColumnName(), inverseForeignKeyColumnInfo.getName());
                }
            }
        } else {
            String mappedBy = columnInfoAnnotationInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnInfoAnnotationInfo mappedByColumnInfoAnnotationInfo = mappedByColumnInfo.getColumnInfoAnnotationInfo();
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfoAnnotationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                }
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfoAnnotationInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    column.put(foreignKeyColumnInfo.getName(), foreignKeyColumnInfo.getReferencedColumnName());
                }
            }
        }
        return column.toString();
    }
}
