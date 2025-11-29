package com.mybatisgx.template.select;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.RelationColumnInfo;
import com.mybatisgx.model.ResultMapInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

public class ResultMapHelper {

    public static Element addResultMapElement(Document document, ResultMapInfo resultMapInfo) {
        Element mapperElement = document.addElement("mapper");
        Element resultMapElement = mapperElement.addElement("resultMap");
        resultMapElement.addAttribute("id", resultMapInfo.getId());
        resultMapElement.addAttribute("type", resultMapInfo.getEntityClazzName());
        return resultMapElement;
    }

    public static void idColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element idColumnElement = resultMapElement.addElement("id");
        columnElement(idColumnElement, columnInfo);
    }

    public static void resultColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element resultColumnElement = resultMapElement.addElement("result");
        columnElement(resultColumnElement, columnInfo);
    }

    public static void columnElement(Element columnElement, ColumnInfo columnInfo) {
        columnElement.addAttribute("property", columnInfo.getJavaColumnName());
        columnElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        columnElement.addAttribute("column", columnInfo.getDbColumnNameAlias());
        String dbTypeName = columnInfo.getDbTypeName();
        columnElement.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
        columnElement.addAttribute("typeHandler", columnInfo.getTypeHandler());
    }

    public static Element associationColumnElement(Element resultMapElement, ResultMapInfo resultMapRelationInfo, String column) {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapRelationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", relationColumnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", column);
        resultMapAssociationElement.addAttribute("javaType", relationColumnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", relationColumnInfo.getFetchType());
        resultMapAssociationElement.addAttribute("select", resultMapRelationInfo.getNestedSelectId());
        resultMapAssociationElement.addAttribute("fetchMode", relationColumnInfo.getFetchMode().name());
        return resultMapAssociationElement;
    }

    public static Element joinAssociationColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    public static Element collectionColumnElement(Element resultMapElement, ResultMapInfo resultMapRelationInfo, String column) {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapRelationInfo.getColumnInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", relationColumnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("column", column);
        resultMapCollectionElement.addAttribute("javaType", relationColumnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", relationColumnInfo.getJavaTypeName());
        resultMapCollectionElement.addAttribute("fetchType", relationColumnInfo.getFetchType());
        resultMapCollectionElement.addAttribute("select", resultMapRelationInfo.getNestedSelectId());
        resultMapCollectionElement.addAttribute("fetchMode", relationColumnInfo.getFetchMode().name());
        // resultMapCollectionElement.addAttribute("relationProperty", "{id=userId}");
        return resultMapCollectionElement;
    }

    public static Element joinCollectionColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        return resultMapCollectionElement;
    }
}
