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

public class AssociationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssociationSelectTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        List<ResultMapInfo> resultMapInfoList = mapperInfo.getResultMapInfoList();
        resultMapInfoList.forEach(resultMapInfo -> {
            Map<String, XNode> xNodeMap = this.buildOneToOne(resultMapInfo.getResultMapAssociationInfoList());
            if (ObjectUtils.isNotEmpty(xNodeMap)) {
                totalXNodeMap.putAll(xNodeMap);
            }
            /*resultMapAssociationInfoList.forEach(resultMapAssociationInfo -> {
                ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
                List<XNode> xNodeList = null;
                OneToOne oneToOne = columnInfo.getAssociationEntityInfo().getOneToOne();
                if (oneToOne != null) {
                    xNodeList = buildOneToOne(mapperInfo, resultMapInfo, mapperInfo.getEntityInfo(), columnInfo);
                }
            });*/
        });


        /*List<ColumnInfo> associationColumnInfoList = mapperInfo.getEntityInfo().getAssociationColumnInfoList();
        associationColumnInfoList.forEach(associationColumnInfo -> {
            XNode xNode = null;
            OneToOne oneToOne = associationColumnInfo.getAssociationEntityInfo().getOneToOne();
            if (oneToOne != null) {
                xNode = buildOneToOne(mapperInfo, mapperInfo.getEntityInfo(), associationColumnInfo);
            }
            OneToMany oneToMany = associationColumnInfo.getAssociationEntityInfo().getOneToMany();
            if (oneToMany != null) {
                // xNode = buildOneToMany(mapperInfo, mapperInfo.getEntityInfo(), associationColumnInfo);
            }
            if (xNode != null) {
                xNodeList.add(xNode);
            }
        });*/
        return totalXNodeMap;
    }

    private Map<String, XNode> buildOneToOne(MapperInfo mapperInfo, ResultMapInfo resultMapInfo, EntityInfo entityInfo, ColumnInfo columnInfo) {
        Map<String, XNode> xNodeMap = this.buildOneToOne(resultMapInfo.getResultMapAssociationInfoList());
        return xNodeMap;
        /*Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        String mappedBy = columnInfo.getAssociationEntityInfo().getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinColumn joinColumn = mappedByColumnInfo.getAssociationEntityInfo().getJoinColumn();
            selectElement.addAttribute("id", String.format("find%sBy%s", entityInfo.getTableEntityClass().getSimpleName(), joinColumn.referencedColumnName()));
            selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", entityInfo.getTableName(), joinColumn.referencedColumnName(), joinColumn.name())
            );
        } else {
            JoinColumn joinColumn = columnInfo.getAssociationEntityInfo().getJoinColumn();
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
        return xNode;*/
    }

    private Map<String, XNode> buildOneToOne(List<ResultMapAssociationInfo> resultMapAssociationInfoList) {
        Map<String, XNode> xNodeMap = new HashMap();
        for (int i = 0; i < resultMapAssociationInfoList.size(); i++) {
            ResultMapAssociationInfo resultMapAssociationInfo = resultMapAssociationInfoList.get(i);
            AssociationEntityInfo associationEntityInfo = resultMapAssociationInfo.getColumnInfo().getAssociationEntityInfo();
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.buildOneToOne(subResultMapAssociationInfoList);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    xNodeMap.putAll(subXNodeMap);
                }
            }
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
        String mappedBy = columnInfo.getAssociationEntityInfo().getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", associationEntityInfo.getTableName(), foreignKeyColumnInfoList.get(0).getName(), foreignKeyColumnInfoList.get(0).getReferencedColumnName())
            );
        } else {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", associationEntityInfo.getTableName(), foreignKeyColumnInfoList.get(0).getReferencedColumnName(), foreignKeyColumnInfoList.get(0).getName())
            );
        }

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
        String mappedBy = columnInfo.getAssociationEntityInfo().getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinTable joinTable = mappedByColumnInfo.getAssociationEntityInfo().getJoinTable();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getInverseForeignKeyColumnInfoList();
            // select role.* from user_role left join role on user_role.role_id = role.id where user_role.user_id = #{userId}
            selectElement.addText(
                    String.format(
                            "select * from %s left join %s on(%s.%s=%s.%s) where %s = #{%s}",
                            joinTable.name(),
                            associationEntityInfo.getTableName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName(),
                            associationEntityInfo.getTableName(),
                            inverseForeignKeyColumnInfoList.get(0).getName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName()
                    )
            );
        } else {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            JoinTable joinTable = columnInfo.getAssociationEntityInfo().getJoinTable();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            selectElement.addAttribute("resultMap", resultMapAssociationInfo.getResultMapId());
            String fetchSize = columnInfo.getAssociationEntityInfo().getFetchSize();
            if (StringUtils.isNotBlank(fetchSize)) {
                selectElement.addAttribute("fetchSize", fetchSize);
            }
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnInfo.getAssociationEntityInfo().getInverseForeignKeyColumnInfoList();
            // select user.* from user_role left join user on user_role.user_id = user.id where user_role.role_id = ?
            selectElement.addText(
                    String.format(
                            "select * from %s left join %s on(%s.%s=%s.%s) where %s = #{%s}",
                            joinTable.name(),
                            associationEntityInfo.getTableName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName(),
                            associationEntityInfo.getTableName(),
                            inverseForeignKeyColumnInfoList.get(0).getName(),
                            joinTable.name(),
                            foreignKeyColumnInfoList.get(0).getName()
                    )
            );
        }

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private XNode buildOneToMany(ResultMapAssociationInfo resultMapAssociationInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
        String mappedBy = columnInfo.getAssociationEntityInfo().getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            ColumnInfo mappedByColumnInfo = associationEntityInfo.getColumnInfo(mappedBy);
            JoinColumn joinColumn = mappedByColumnInfo.getAssociationEntityInfo().getJoinColumn();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            // selectElement.addAttribute("resultMap", resultMapAssociationInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", associationEntityInfo.getTableName(), joinColumn.referencedColumnName(), joinColumn.name())
            );
        } else {
            Class<?> javaType = columnInfo.getJavaType();
            EntityInfo associationEntityInfo = EntityInfoContextHolder.get(javaType);
            JoinColumn joinColumn = columnInfo.getAssociationEntityInfo().getJoinColumn();
            selectElement.addAttribute("id", resultMapAssociationInfo.getSelect());
            // selectElement.addAttribute("resultMap", resultMapInfo.getId());
            selectElement.addAttribute("fetchType", "lazy");
            selectElement.addText(
                    String.format(
                            "select * from %s where %s = #{%s}", associationEntityInfo.getTableName(), joinColumn.name(), joinColumn.referencedColumnName())
            );
        }
        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    /*private XNode buildOneToMany(MapperInfo mapperInfo, EntityInfo entityInfo, ColumnInfo columnInfo) {
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
    }*/

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