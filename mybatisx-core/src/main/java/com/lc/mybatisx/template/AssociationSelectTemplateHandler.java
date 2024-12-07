package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.JoinColumn;
import com.lc.mybatisx.annotation.OneToMany;
import com.lc.mybatisx.annotation.OneToOne;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AssociationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    public List<XNode> execute(MapperInfo mapperInfo, List<MethodInfo> methodInfoList) {
        List<XNode> xNodeList = new ArrayList<>();
        List<ColumnInfo> associationColumnInfoList = mapperInfo.getEntityInfo().getAssociationColumnInfoList();
        associationColumnInfoList.forEach(associationColumnInfo -> {
            XNode xNode = null;
            OneToOne oneToOne = associationColumnInfo.getOneToOne();
            if (oneToOne != null) {
                xNode = buildOneToOne(mapperInfo, mapperInfo.getEntityInfo(), associationColumnInfo);
            }
            OneToMany oneToMany = associationColumnInfo.getOneToMany();
            if (oneToMany != null) {
                // xNode = buildOneToMany(mapperInfo, mapperInfo.getEntityInfo(), associationColumnInfo);
            }
            if (xNode != null) {
                xNodeList.add(xNode);
            }
        });
        return xNodeList;
    }

    private XNode buildOneToOne(MapperInfo mapperInfo, EntityInfo entityInfo, ColumnInfo columnInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(entityInfo.getTableEntityClass());
        String mappedBy = columnInfo.getMappedBy();
        Boolean foreignKey = columnInfo.getForeignKey();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinColumn joinColumn = mappedByColumnInfo.getJoinColumn();
            selectElement.addAttribute("id", String.format("find%sBy%s", entityInfo.getTableEntityClass().getSimpleName(), joinColumn.referencedColumnName()));
            selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", entityInfo.getTableName(), joinColumn.referencedColumnName(), joinColumn.name())
            );
        }
        if (foreignKey) {
            JoinColumn joinColumn = columnInfo.getJoinColumn();
            selectElement.addAttribute("id", String.format("find%sBy%s", entityInfo.getTableEntityClass().getSimpleName(), joinColumn.name()));
            selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", entityInfo.getTableName(), joinColumn.name(), joinColumn.referencedColumnName())
            );
        }
        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private XNode buildOneToMany(MapperInfo mapperInfo, EntityInfo entityInfo, ColumnInfo columnInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(entityInfo.getTableEntityClass());
        String mappedBy = columnInfo.getMappedBy();
        Boolean foreignKey = columnInfo.getForeignKey();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinColumn joinColumn = mappedByColumnInfo.getJoinColumn();
            selectElement.addAttribute("id", String.format("find%sBy%s", entityInfo.getTableEntityClass().getSimpleName(), joinColumn.referencedColumnName()));
            selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", entityInfo.getTableName(), joinColumn.referencedColumnName(), joinColumn.name())
            );
        }
        if (foreignKey) {
            JoinColumn joinColumn = columnInfo.getJoinColumn();
            selectElement.addAttribute("id", String.format("find%sBy%s", entityInfo.getTableEntityClass().getSimpleName(), joinColumn.name()));
            selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", entityInfo.getTableName(), joinColumn.name(), joinColumn.referencedColumnName())
            );
        }
        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private XNode buildSelectXNode(MapperInfo mapperInfo, TableInfo tableInfo, ColumnInfo columnInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        // findUserByRoleId   findUserByOrderId    findUserByUserDetailId
        /*String inverseForeignKey = associationTableInfo.getInverseForeignKey();
        if (StringUtils.isBlank(inverseForeignKey)) {
            selectElement.addAttribute("id", String.format("find%sJoin%sBy%s", mapperInfo.getEntityClass().getSimpleName(), associationTableInfo.getJoinEntity().getSimpleName(), associationTableInfo.getForeignKey()));
            selectElement.addAttribute("resultType", mapperInfo.getEntityClass().getTypeName());
            selectElement.addText(
                    String.format(
                            "select %s.* from %s %s left join %s %s on %s.%s = %s.%s where %s.%s = #{id}",
                            associationTableInfo.getJoinEntity().getSimpleName(),

                            tableInfo.getTableName(),
                            tableInfo.getTableName(),
                            associationTableInfo.getJoinEntity().getSimpleName(),
                            associationTableInfo.getJoinEntity().getSimpleName(),

                            tableInfo.getTableName(),
                            associationTableInfo.getForeignKey(),

                            associationTableInfo.getJoinEntity().getSimpleName(),
                            "id",

                            tableInfo.getTableName(),
                            "aaa"
                    )
            );
        } else {
            // 多对多
            selectElement.addAttribute("id", String.format("find%sJoin%sBy%s", mapperInfo.getEntityClass().getSimpleName(), associationTableInfo.getJoinEntity().getSimpleName(), associationTableInfo.getInverseForeignKey()));
            selectElement.addAttribute("resultType", mapperInfo.getEntityClass().getTypeName());
            selectElement.addText(String.format("%s---%s", mapperInfo.getEntityClass().getSimpleName(), associationTableInfo.getJoinEntity().getSimpleName()));
        }*/

        /**
         *      * select role.* from
         *      * user_role user_role left join role role on user_role.role_id = role.id
         *      * where user.user_id = id
         */
        /*selectElement.addText(
                String.format(
                        "select %s.* from %s %s left join %s %s on %s.%s = %s.%s where %s.%s = #{id}",
                        targetTableInfo.getTableName(),
                        tableInfo.getTableName(),
                        tableInfo.getTableName(),
                        targetTableInfo.getTableName(),
                        targetTableInfo.getTableName(),

                        tableInfo.getTableName(),
                        associationTableInfo.getTargetForeignKey()[0],

                        targetTableInfo.getTableName(),
                        targetTableInfo.getColumnInfo("id").getDbColumnName(),

                        tableInfo.getTableName(),
                        associationTableInfo.getForeignKey()[0]
                )
        );*/

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

}
