package com.lc.mybatisx.template.select;

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

public class ResultMapOrTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapOrTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> xNodeMap = new HashMap();
        List<ResultMapInfo> resultMapInfoList = mapperInfo.getResultMapInfoList();
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            Document document = DocumentHelper.createDocument();
            Element resultMapElement = this.addResultMapElement(document, resultMapInfo);
            this.addColumnElement(resultMapElement, resultMapInfo.getTableColumnInfoList());
            this.addResultMapRelationElement(resultMapElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.info("select resultMap: \n{}", resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
            // xNodeMap.putAll(refResultMapXNodeMap);
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
            ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
            if (columnRelationInfo == null) {
                resultColumnElement(resultMapElement, columnInfo);
            } else {
                ManyToMany manyToMany = columnRelationInfo.getManyToMany();
                String mappedBy = columnRelationInfo.getMappedBy();
                if (manyToMany == null && StringUtils.isBlank(mappedBy)) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        this.resultColumnElement(resultMapElement, inverseForeignKeyColumnInfo);
                    }
                }
            }
        }
    }

    private void idColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element idColumnElement = resultMapElement.addElement("id");
        this.columnElement(idColumnElement, columnInfo);
    }

    private void resultColumnElement(Element resultMapElement, ForeignKeyColumnInfo foreignKeyColumnInfo) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setDbColumnName(foreignKeyColumnInfo.getName());
        columnInfo.setDbColumnNameAlias(foreignKeyColumnInfo.getNameAlias());
        this.resultColumnElement(resultMapElement, columnInfo);
    }

    private void resultColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element resultColumnElement = resultMapElement.addElement("result");
        this.columnElement(resultColumnElement, columnInfo);
    }

    private void columnElement(Element columnElement, ColumnInfo columnInfo) {
        columnElement.addAttribute("property", columnInfo.getJavaColumnName());
        columnElement.addAttribute("column", columnInfo.getDbColumnNameAlias());
        String dbTypeName = columnInfo.getDbTypeName();
        columnElement.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
        columnElement.addAttribute("typeHandler", columnInfo.getTypeHandler());
    }

    private void addResultMapRelationElement(Element resultMapElement, MapperInfo mapperInfo, EntityInfo parentEntityInfo, List<ResultMapInfo> resultMapInfoList) {
        if (ObjectUtils.isEmpty(resultMapInfoList)) {
            return;
        }
        // Map<String, XNode> refResultMapXNodeMap = new HashMap();
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            ResultMapInfo existResultMapInfo = mapperInfo.getResultMapInfo(resultMapInfo.getEntityClazz());
            if (existResultMapInfo != null) {
                this.subQuery(resultMapElement, parentEntityInfo, resultMapInfo);
            } else {
                Element resultMapRelationElement = this.joinQuery(resultMapInfo, resultMapElement);
                List<ResultMapInfo> childrenResultMapInfoList = resultMapInfo.getResultMapInfoList();
                if (ObjectUtils.isNotEmpty(childrenResultMapInfoList)) {
                    this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, resultMapInfo.getEntityInfo(), childrenResultMapInfoList);
                }
            }
        }
        // return refResultMapXNodeMap;
    }

    private void subQuery(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapInfo) {
        ColumnInfo columnInfo = resultMapInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Integer associationType = this.getRelationType(columnRelationInfo);
        if (associationType == 1) {
            this.associationColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else if (associationType == 2) {
            this.collectionColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinQuery(ResultMapInfo resultMapRelationInfo, Element resultMapElement) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnRelationInfo columnInfoAnnotationInfo = columnInfo.getColumnRelationInfo();
        Integer relationType = this.getRelationType(columnInfoAnnotationInfo);
        if (relationType == 1) {
            Element resultMapRelationElement = this.joinAssociationColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapRelationElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapRelationElement;
        } else if (relationType == 2) {
            Element resultMapCollectionElement = this.joinCollectionColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapCollectionElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapCollectionElement;
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element associationColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("collection");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", this.getColumn(parentEntityInfo, resultMapRelationInfo));
        resultMapAssociationElement.addAttribute("javaType", List.class.getName());
        resultMapAssociationElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", columnRelationInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapRelationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
    }

    private Element joinAssociationColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    private Element collectionColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("column", this.getColumn(parentEntityInfo, resultMapRelationInfo));
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapCollectionElement.addAttribute("fetchType", columnRelationInfo.getFetch().toLowerCase());
        resultMapCollectionElement.addAttribute("select", resultMapRelationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapCollectionElement;
    }

    private Element joinCollectionColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        return resultMapCollectionElement;
    }

    private Integer getRelationType(ColumnRelationInfo columnRelationInfo) {
        OneToOne oneToOne = columnRelationInfo.getOneToOne();
        OneToMany oneToMany = columnRelationInfo.getOneToMany();
        ManyToOne manyToOne = columnRelationInfo.getManyToOne();
        ManyToMany manyToMany = columnRelationInfo.getManyToMany();
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

    /**
     * 生成关系查询参数，参数名称带有业务属性，如查询用户，参数名为user_id，查询角色，参数名为role_id
     * <code>
     *     <association property="user" column="{user_id=user_id}" javaType="com.lc.mybatisx.test.model.entity.User" fetchType="lazy" select="findUser"/>
     *     <collection property="roleList" column="{user_id=id}" javaType="java.util.List" ofType="com.lc.mybatisx.test.model.entity.Role" fetchType="lazy" select="findRoleList"/>
     * </code>
     * @param resultMapRelationInfo
     * @return
     */
    private String getColumn(EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo) {
        EntityInfo entityInfo = resultMapRelationInfo.getEntityInfo();
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        ManyToMany manyToMany = columnRelationInfo.getManyToMany();
        Map<String, String> column = new HashMap();
        if (manyToMany == null) {
            String mappedBy = columnRelationInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(inverseForeignKeyColumnInfo.getReferencedColumnName());
                    // column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                    column.put(inverseForeignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    column.put("nested_select_collection", parentEntityColumnInfo.getDbColumnNameAlias());
                }
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo inverseForeignKeyRefColumnInfo = entityInfo.getDbColumnInfo(inverseForeignKeyColumnInfo.getReferencedColumnName());
                    // column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getName());
                    column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getNameAlias());
                    column.put("nested_select_collection", inverseForeignKeyColumnInfo.getNameAlias());
                }
            }
        } else {
            String mappedBy = columnRelationInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    // column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                    ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(inverseForeignKeyColumnInfo.getReferencedColumnName());
                    column.put(inverseForeignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    column.put("nested_select_collection", parentEntityColumnInfo.getDbColumnNameAlias());
                }
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnRelationInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    // column.put(foreignKeyColumnInfo.getName(), foreignKeyColumnInfo.getReferencedColumnName());
                    ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(foreignKeyColumnInfo.getReferencedColumnName());
                    column.put(foreignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    column.put("nested_select_collection", parentEntityColumnInfo.getDbColumnNameAlias());
                }
            }
        }
        return column.toString();
    }
}
