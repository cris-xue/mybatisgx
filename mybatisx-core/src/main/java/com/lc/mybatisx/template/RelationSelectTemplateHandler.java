package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.ManyToOne;
import com.lc.mybatisx.annotation.OneToMany;
import com.lc.mybatisx.annotation.OneToOne;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
import net.sf.jsqlparser.JSQLParserException;
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

public class RelationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RelationSelectTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> totalXNodeMap = new HashMap();
        Map<String, EntityRelationSelectInfo> entityRelationSelectInfoMap = mapperInfo.getEntityRelationSelectInfoMap();
        entityRelationSelectInfoMap.forEach((select, entityRelationSelectInfo) -> {
            String sql = this.buildJoinSelect(entityRelationSelectInfo);
            Map<String, XNode> xNodeMap = this.buildSelect(entityRelationSelectInfo, sql);
            if (ObjectUtils.isNotEmpty(xNodeMap)) {
                totalXNodeMap.putAll(xNodeMap);
            }
        });
        return totalXNodeMap;
    }

    private Map<String, XNode> buildSelect(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Map<String, XNode> xNodeMap = new HashMap();
        // entityRelationSelectInfoList.forEach((entityRelationSelectInfo) -> {
            /*List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.buildSelect(subResultMapAssociationInfoList, sqlMap);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    xNodeMap.putAll(subXNodeMap);
                }
            }*/
        ColumnInfoAnnotationInfo associationEntityInfo = entityRelationSelectInfo.getColumnInfo().getColumnInfoAnnotationInfo();

        OneToOne oneToOne = associationEntityInfo.getOneToOne();
        String selectXmlString = null;
        if (oneToOne != null) {
            selectXmlString = this.buildOneToOne(entityRelationSelectInfo, sql);
        }
        OneToMany oneToMany = associationEntityInfo.getOneToMany();
        if (oneToMany != null) {
            selectXmlString = this.buildOneToOne(entityRelationSelectInfo, sql);
        }
        ManyToOne manyToOne = associationEntityInfo.getManyToOne();
        if (manyToOne != null) {
            selectXmlString = this.buildOneToOne(entityRelationSelectInfo, sql);
        }
        ManyToMany manyToMany = associationEntityInfo.getManyToMany();
        if (manyToMany != null) {
            selectXmlString = this.buildManyToMany(entityRelationSelectInfo, sql);
        }
        if (StringUtils.isNotBlank(selectXmlString)) {
            logger.info("auto relation select sql: {}", selectXmlString);
            XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/select");
            xNodeMap.put(entityRelationSelectInfo.getId(), xNode);
        }
        // });
        /*for (int i = 0; i < resultMapAssociationInfoList.size(); i++) {
            ResultMapAssociationInfo resultMapAssociationInfo = resultMapAssociationInfoList.get(i);
            List<ResultMapAssociationInfo> subResultMapAssociationInfoList = resultMapAssociationInfo.getResultMapAssociationInfoList();
            if (ObjectUtils.isNotEmpty(subResultMapAssociationInfoList)) {
                Map<String, XNode> subXNodeMap = this.buildSelect(subResultMapAssociationInfoList, sqlMap);
                if (ObjectUtils.isNotEmpty(subXNodeMap)) {
                    xNodeMap.putAll(subXNodeMap);
                }
            }
            AssociationEntityInfo associationEntityInfo = resultMapAssociationInfo.getColumnInfo().getAssociationEntityInfo();
            LoadStrategy loadStrategy = associationEntityInfo.getLoadStrategy();
            // 关联表的第一层无法采用关联查询，因为分页没办法处理，在第一层把分页处理之后，后续就可以采用关联查询了
            if (loadStrategy == LoadStrategy.JOIN && resultMapAssociationInfo.getLevel() >= 2) {
                continue;
            }

            SelectSqlTemplateHandler.SelectTable sql = sqlMap.get(resultMapAssociationInfo.getSelect());
            OneToOne oneToOne = associationEntityInfo.getOneToOne();
            String selectXmlString = null;
            if (oneToOne != null) {
                selectXmlString = this.buildOneToOne(resultMapAssociationInfo, sql);
            }
            OneToMany oneToMany = associationEntityInfo.getOneToMany();
            if (oneToMany != null) {
                selectXmlString = this.buildOneToOne(resultMapAssociationInfo, sql);
            }
            ManyToOne manyToOne = associationEntityInfo.getManyToOne();
            if (manyToOne != null) {
                selectXmlString = this.buildOneToOne(resultMapAssociationInfo, sql);
            }
            ManyToMany manyToMany = associationEntityInfo.getManyToMany();
            if (manyToMany != null) {
                selectXmlString = this.buildManyToMany(resultMapAssociationInfo, sql);
            }
            if (StringUtils.isNotBlank(selectXmlString)) {
                logger.info("auto association select sql: {}", selectXmlString);
                XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
                XNode xNode = xPathParser.evalNode("/mapper/select");
                xNodeMap.put(resultMapAssociationInfo.getSelect(), xNode);
            }
        }*/
        return xNodeMap;
    }

    /**
     * select * from user left join user_detail left join user_role left join role where id = #{id}
     *
     * @param entityRelationSelectInfo
     * @return
     */
    private String buildJoinSelect(EntityRelationSelectInfo entityRelationSelectInfo) {
        SelectSqlTemplateHandler selectSqlTemplateHandler = new SelectSqlTemplateHandler();
        try {
            return selectSqlTemplateHandler.buildSelectSql(entityRelationSelectInfo);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildOneToOne(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        EntityInfo queryEntityInfo = entityRelationSelectInfo.getEntityInfo();
        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        ColumnInfoAnnotationInfo columnInfoAnnotationInfo = columnInfo.getColumnInfoAnnotationInfo();

        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        String fetchSize = columnInfo.getColumnInfoAnnotationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }

        this.buildSelectSqlXNode(selectElement, queryEntityInfo, columnInfoAnnotationInfo, sql);

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return selectXmlString;
    }

    private String buildManyToMany(EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");

        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        String fetchSize = columnInfo.getColumnInfoAnnotationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }

        Class<?> javaType = columnInfo.getJavaType();
        ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
        EntityInfo queryEntityInfo = EntityInfoContextHolder.get(javaType);
        this.buildManyToManySelectSqlXNode(selectElement, queryEntityInfo, associationEntityInfo, sql);

        String selectXmlString = document.asXML();
        logger.debug("select: {}", selectXmlString);
        XPathParser xPathParser = XmlUtils.processXml(selectXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return selectXmlString;
    }

    private void buildSelectSqlXNode(Element selectElement, EntityInfo queryEntityInfo, ColumnInfoAnnotationInfo columnInfoAnnotationInfo, String sql) {
        /*selectElement.addText("select");
        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            AssociationEntityInfo queryAssociationEntityInfo = queryColumnInfo.getAssociationEntityInfo();
            if (queryAssociationEntityInfo == null) {
                dbTrimElement.addText(String.format("%s, ", queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    dbTrimElement.addText(String.format("%s, ", foreignKeyColumnInfo.getName()));
                });
            }
        });

        selectElement.addText(String.format("from %s", queryEntityInfo.getTableName()));*/

        selectElement.addText(sql);

        Element whereElement = selectElement.addElement("where");
        Element trimElement = whereElement.addElement("trim");
        trimElement.addAttribute("prefix", "");
        trimElement.addAttribute("suffix", "");
        trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        String mappedBy = columnInfoAnnotationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            ColumnInfoAnnotationInfo targetAssociationEntityInfo = targetColumnInfo.getColumnInfoAnnotationInfo();
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = targetAssociationEntityInfo.getForeignKeyColumnInfoList();
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getName(), "=", foreignKeyColumnInfo.getName());
                trimElement.addText(conditionOp);
            });
        } else {
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnInfoAnnotationInfo.getForeignKeyColumnInfoList();
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(" %s %s %s #{%s}", "and", foreignKeyColumnInfo.getReferencedColumnName(), "=", foreignKeyColumnInfo.getReferencedColumnName());
                trimElement.addText(conditionOp);
            });
        }
    }

    private void buildManyToManySelectSqlXNode(Element selectElement, EntityInfo queryEntityInfo, ColumnInfoAnnotationInfo associationEntityInfo, String sql) {
        /*selectElement.addText("select");
        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            AssociationEntityInfo queryAssociationEntityInfo = queryColumnInfo.getAssociationEntityInfo();
            if (queryAssociationEntityInfo == null) {
                dbTrimElement.addText(String.format("%s.%s, ", queryEntityInfo.getTableName(), queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    dbTrimElement.addText(String.format("%s.%s, ", queryEntityInfo.getTableName(), foreignKeyColumnInfo.getName()));
                });
            }
        });

        this.fromLeftJoin(selectElement, associationEntityInfo, queryEntityInfo, sql);*/

        selectElement.addText(sql);
        this.where(selectElement, associationEntityInfo, queryEntityInfo);
    }

    private void fromLeftJoin(Element selectElement, AssociationEntityInfo associationEntityInfo, EntityInfo queryEntityInfo, String sql) {
        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            ColumnInfoAnnotationInfo targetAssociationEntityInfo = targetColumnInfo.getColumnInfoAnnotationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = targetAssociationEntityInfo.getInverseForeignKeyColumnInfoList();

            if (StringUtils.isNotBlank(sql)) {
                selectElement.addText(sql);
            } else {
                selectElement.addText(String.format("from "));
                selectElement.addText(String.format("%s left join %s on ", targetAssociationEntityInfo.getJoinTable().name(), queryEntityInfo.getTableName()));
                inverseForeignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                    selectElement.addText(
                            String.format(
                                    "%s.%s = %s.%s",
                                    targetAssociationEntityInfo.getJoinTable().name(),
                                    foreignKeyColumnInfo.getName(),
                                    queryEntityInfo.getTableName(),
                                    foreignKeyColumnInfo.getReferencedColumnName()
                            )
                    );
                });
            }
        } else {
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = associationEntityInfo.getForeignKeyColumnInfoList();
            if (StringUtils.isNotBlank(sql)) {
                selectElement.addText(sql);
            } else {
                selectElement.addText(String.format("from "));
                selectElement.addText(String.format("%s left join %s on ", associationEntityInfo.getJoinTable().name(), queryEntityInfo.getTableName()));
                foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                    selectElement.addText(
                            String.format(
                                    "%s.%s = %s.%s",
                                    associationEntityInfo.getJoinTable().name(),
                                    foreignKeyColumnInfo.getName(),
                                    queryEntityInfo.getTableName(),
                                    foreignKeyColumnInfo.getReferencedColumnName()
                            )
                    );
                });
            }
        }
    }

    private void where(Element selectElement, ColumnInfoAnnotationInfo associationEntityInfo, EntityInfo queryEntityInfo) {
        Element whereElement = selectElement.addElement("where");
        Element whereTrimElement = whereElement.addElement("trim");
        whereTrimElement.addAttribute("prefix", "");
        whereTrimElement.addAttribute("suffix", "");
        whereTrimElement.addAttribute("prefixOverrides", "AND|OR|and|or");

        String mappedBy = associationEntityInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo targetColumnInfo = queryEntityInfo.getColumnInfo(mappedBy);
            ColumnInfoAnnotationInfo targetAssociationEntityInfo = targetColumnInfo.getColumnInfoAnnotationInfo();
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = targetAssociationEntityInfo.getForeignKeyColumnInfoList();
            // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
            foreignKeyColumnInfoList.forEach(foreignKeyColumnInfo -> {
                String conditionOp = String.format(
                        " %s %s.%s %s #{%s}",
                        "and",
                        targetAssociationEntityInfo.getJoinTable().name(),
                        foreignKeyColumnInfo.getName(),
                        "=",
                        foreignKeyColumnInfo.getName()
                );
                whereTrimElement.addText(conditionOp);
            });
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = associationEntityInfo.getInverseForeignKeyColumnInfoList();
            // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
            inverseForeignKeyColumnInfoList.forEach(inverseForeignKeyColumnInfo -> {
                String conditionOp = String.format(
                        " %s %s.%s %s #{%s}",
                        "and",
                        associationEntityInfo.getJoinTable().name(),
                        inverseForeignKeyColumnInfo.getName(),
                        "=",
                        inverseForeignKeyColumnInfo.getName()
                );
                whereTrimElement.addText(conditionOp);
            });
        }
    }
}