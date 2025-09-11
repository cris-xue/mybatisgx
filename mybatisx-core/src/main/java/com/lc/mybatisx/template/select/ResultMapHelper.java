package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.ManyToOne;
import com.lc.mybatisx.annotation.OneToMany;
import com.lc.mybatisx.annotation.OneToOne;
import com.lc.mybatisx.model.*;
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

    public static void resultColumnElement(Element resultMapElement, ForeignKeyColumnInfo foreignKeyColumnInfo) {
        ColumnInfo columnInfo = new ColumnInfo();
        /*columnInfo.setDbColumnName(foreignKeyColumnInfo.getName());
        columnInfo.setDbColumnNameAlias(foreignKeyColumnInfo.getNameAlias());*/
        resultColumnElement(resultMapElement, columnInfo);
    }

    public static void resultColumnElement(Element resultMapElement, ColumnInfo columnInfo) {
        Element resultColumnElement = resultMapElement.addElement("result");
        columnElement(resultColumnElement, columnInfo);
    }

    public static void columnElement(Element columnElement, ColumnInfo columnInfo) {
        columnElement.addAttribute("property", columnInfo.getJavaColumnName());
        columnElement.addAttribute("column", columnInfo.getDbColumnNameAlias());
        String dbTypeName = columnInfo.getDbTypeName();
        columnElement.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
        columnElement.addAttribute("typeHandler", columnInfo.getTypeHandler());
    }

    public static Element associationColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo, String column) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", column);
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", relationColumnInfo.getFetchType());
        resultMapAssociationElement.addAttribute("select", resultMapRelationInfo.getSelect());
        resultMapAssociationElement.addAttribute("relationProperty", "{id=userId}");
        return resultMapAssociationElement;
    }

    public static Element joinAssociationColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        return resultMapAssociationElement;
    }

    public static Element collectionColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo, String column) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("column", column);
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapCollectionElement.addAttribute("fetchType", relationColumnInfo.getFetchType());
        resultMapCollectionElement.addAttribute("select", resultMapRelationInfo.getSelect());
        resultMapCollectionElement.addAttribute("relationProperty", "{id=userId}");
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

    public static Integer getRelationType(RelationColumnInfo relationColumnInfo) {
        OneToOne oneToOne = relationColumnInfo.getOneToOne();
        OneToMany oneToMany = relationColumnInfo.getOneToMany();
        ManyToOne manyToOne = relationColumnInfo.getManyToOne();
        ManyToMany manyToMany = relationColumnInfo.getManyToMany();
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
}
