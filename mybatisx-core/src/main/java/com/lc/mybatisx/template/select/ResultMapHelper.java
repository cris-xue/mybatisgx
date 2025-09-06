package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.ManyToOne;
import com.lc.mybatisx.annotation.OneToMany;
import com.lc.mybatisx.annotation.OneToOne;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ColumnRelationInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import org.dom4j.Element;

public class ResultMapHelper {

    public static Integer getRelationType(ColumnRelationInfo columnRelationInfo) {
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

    public static Element associationColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo, String column) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Element resultMapAssociationElement = resultMapElement.addElement("association");
        resultMapAssociationElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapAssociationElement.addAttribute("column", column);
        resultMapAssociationElement.addAttribute("javaType", columnInfo.getJavaTypeName());
        resultMapAssociationElement.addAttribute("fetchType", columnRelationInfo.getFetchType());
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
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Element resultMapCollectionElement = resultMapElement.addElement("collection");
        resultMapCollectionElement.addAttribute("property", columnInfo.getJavaColumnName());
        resultMapCollectionElement.addAttribute("column", column);
        resultMapCollectionElement.addAttribute("javaType", columnInfo.getCollectionTypeName());
        resultMapCollectionElement.addAttribute("ofType", columnInfo.getJavaTypeName());
        resultMapCollectionElement.addAttribute("fetchType", columnRelationInfo.getFetchType());
        resultMapCollectionElement.addAttribute("select", resultMapRelationInfo.getSelect());
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
