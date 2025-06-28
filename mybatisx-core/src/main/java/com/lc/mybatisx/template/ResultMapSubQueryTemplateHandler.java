package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Id;
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

    private Element addResultMapAssociationElement(Element resultMapElement, ResultMapAssociationInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
        String mappedBy = associationEntityInfo.getMappedBy();
        String column;
        if (StringUtils.isNotBlank(mappedBy)) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
            ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
            AssociationEntityInfo mappedByAssociationEntityInfo = mappedByColumnInfo.getAssociationEntityInfo();
            column = mappedByAssociationEntityInfo.getReferencedColumnName();
        } else {
            column = associationEntityInfo.getName();
        }

        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", column);
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", associationEntityInfo.getFetch().toLowerCase());
        resultMapAssociationElement.addAttribute("select", resultMapAssociationInfo.getSelect());
        // resultMapAssociationElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
        return resultMapAssociationElement;
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
            String mappedBy = associationEntityInfo.getMappedBy();
            if (StringUtils.isBlank(mappedBy)) {
                // 在resultMap下增加外键字段
                /*List<ColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
                for (ColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    resultColumnElement(resultMapElement, foreignKeyColumnInfo);
                }*/
            }
            Element resultMapAssociationElement = this.addResultMapAssociationElement(resultMapElement, resultMapAssociationInfo);

            Document document = DocumentHelper.createDocument();
            Element refResultMapElement = this.addResultMapElement(document, resultMapAssociationInfo);
            this.addColumnElement(refResultMapElement, resultMapAssociationInfo.getColumnInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            refResultMapXNodeMap.put(resultMapAssociationInfo.getResultMapId(), xNode);

            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.addAssociationElement(resultMapAssociationElement, subResultMapAssociationInfoList);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    refResultMapXNodeMap.putAll(subXNodeMap);
                }
            }
        });
        return refResultMapXNodeMap;
    }

    /*private void oneToManyCollectionColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Class<?> javaType = columnInfo.getJavaType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(javaType);
        MapperInfo mapperInfo = MapperInfoContextHolder.get(javaType);

        String namespace = mapperInfo.getNamespace();
        Boolean foreignKey = columnInfo.getForeignKey();
        String mappedBy = columnInfo.getMappedBy();

        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getContainerTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());

        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
            JoinColumn joinColumn = mappedByColumnInfo.getJoinColumn();
            resultMapCollectionElement.addAttribute("select",
                    String.format(
                            "%s.find%sBy%s",
                            namespace,
                            entityInfo.getTableEntityClass().getSimpleName(),
                            joinColumn.name()
                    )
            );
            resultMapCollectionElement.addAttribute("column", joinColumn.referencedColumnName());
        }
        if (foreignKey) {
            JoinColumn joinColumn = columnInfo.getJoinColumn();
            resultMapCollectionElement.addAttribute("select",
                    String.format(
                            "%s.find%sBy%s",
                            namespace,
                            entityInfo.getTableEntityClass().getSimpleName(),
                            joinColumn.referencedColumnName()
                    )
            );
            resultMapCollectionElement.addAttribute("column", joinColumn.name());
        }
        resultMapCollectionElement.addAttribute("fetchType", FetchType.LAZY.name());
    }

    private void manyToManyCollectionColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Class<?> javaType = columnInfo.getJavaType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(javaType);
        MapperInfo mapperInfo = MapperInfoContextHolder.get(javaType);

        String namespace = mapperInfo.getNamespace();
        JoinTable joinTable = columnInfo.getJoinTable();

        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getContainerTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());

        if (joinTable == null) {
            String mappedBy = columnInfo.getMappedBy();
            ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
            joinTable = mappedByColumnInfo.getJoinTable();

            String tableName = joinTable.name();
            JoinColumn[] joinColumnList = joinTable.joinColumns();
            JoinColumn[] inverseJoinColumnList = joinTable.inverseJoinColumns();

            resultMapCollectionElement.addAttribute("select",
                    String.format(
                            "%s.find_%s_join_%s_by_%s",
                            namespace,
                            tableName,
                            entityInfo.getTableName(),
                            joinColumnList[0].name()
                    )
            );
            resultMapCollectionElement.addAttribute("column", joinColumnList[0].referencedColumnName());
        } else {
            String tableName = joinTable.name();
            JoinColumn[] joinColumnList = joinTable.joinColumns();
            JoinColumn[] inverseJoinColumnList = joinTable.inverseJoinColumns();
            resultMapCollectionElement.addAttribute("select",
                    String.format(
                            "%s.find_%s_join_%s_by_%s",
                            namespace,
                            tableName,
                            entityInfo.getTableName(),
                            inverseJoinColumnList[0].name()
                    )
            );
            resultMapCollectionElement.addAttribute("column", inverseJoinColumnList[0].referencedColumnName());
        }
        resultMapCollectionElement.addAttribute("fetchType", FetchType.LAZY.name());
    }*/

    /*private void addAssociationElement(Element resultMapElement, List<AssociationTableInfo> associationTableInfoList) {
        associationTableInfoList.forEach(associationTableInfo -> {
            Class<?> joinEntity = associationTableInfo.getJoinEntity();
            TableInfo tableInfo = TableInfoContextHolder.get(joinEntity);
            MapperInfo mapperInfo = MapperInfoContextHolder.get(joinEntity);
            String namespace = mapperInfo.getNamespace();

            Class<?> joinContainerType = associationTableInfo.getJoinContainerType();
            String inverseForeignKey = associationTableInfo.getInverseForeignKey();
            if (StringUtils.isBlank(inverseForeignKey)) {
                if (joinContainerType == associationTableInfo.getJoinEntity()) {
                    // 一对一
                    Element resultMapSelectElement = resultMapElement.addElement("association");
                    resultMapSelectElement.addAttribute("property", associationTableInfo.getJavaColumnName());
                    resultMapSelectElement.addAttribute("javaType", associationTableInfo.getJoinEntity().getName());
                    resultMapSelectElement.addAttribute("select", String.format("%s.find%sBy%s", namespace, joinEntity.getSimpleName(), associationTableInfo.getForeignKey()));
                    resultMapSelectElement.addAttribute("column", "id");
                    resultMapSelectElement.addAttribute("fetchType", associationTableInfo.getFetch().name());
                } else {
                    Element resultMapCollectionElement = resultMapElement.addElement("collection");
                    resultMapCollectionElement.addAttribute("property", associationTableInfo.getJavaColumnName());
                    resultMapCollectionElement.addAttribute("javaType", associationTableInfo.getJoinContainerType().getSimpleName());
                    resultMapCollectionElement.addAttribute("ofType", associationTableInfo.getJoinEntity().getName());
                    resultMapCollectionElement.addAttribute("select", String.format("%s.find%sBy%s", namespace, joinEntity.getSimpleName(), associationTableInfo.getForeignKey()));
                    resultMapCollectionElement.addAttribute("column", "id");
                    resultMapCollectionElement.addAttribute("fetchType", associationTableInfo.getFetch().name());
                }
            } else {
                // 多对多
                // <collection property="roleList" javaType="ArrayList" ofType="com.lc.mybatisx.test.model.entity.Role" column="id"/>
                Element resultMapCollectionElement = resultMapElement.addElement("collection");
                resultMapCollectionElement.addAttribute("property", associationTableInfo.getJavaColumnName());
                resultMapCollectionElement.addAttribute("javaType", associationTableInfo.getJoinContainerType().getSimpleName());
                resultMapCollectionElement.addAttribute("ofType", associationTableInfo.getJoinEntity().getName());
                resultMapCollectionElement.addAttribute("select", String.format("%s.find%sBy%s", namespace, joinEntity.getSimpleName(), inverseForeignKey));
                resultMapCollectionElement.addAttribute("column", "id");
                resultMapCollectionElement.addAttribute("fetchType", associationTableInfo.getFetch().name());
            }
        });
    }*/
}
