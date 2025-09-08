package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.ManyToMany;
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
            Element resultMapElement = ResultMapHelper.addResultMapElement(document, resultMapInfo);
            this.addColumnElement(resultMapElement, resultMapInfo.getTableColumnInfoList());
            this.addColumnRelationElement(resultMapElement, resultMapInfo);
            this.addResultMapRelationElement(resultMapElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
            String resultMapXmlString = document.asXML();
            logger.info("select resultMap: \n{}", resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
        }
        return xNodeMap;
    }

    private void addColumnElement(Element resultMapElement, List<ColumnInfo> tableColumnInfoList) {
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            Id id = tableColumnInfo.getId();
            ColumnRelationInfo columnRelationInfo = tableColumnInfo.getColumnRelationInfo();
            if (id != null) {
                ResultMapHelper.idColumnElement(resultMapElement, tableColumnInfo);
            } else if (columnRelationInfo == null) {
                ResultMapHelper.resultColumnElement(resultMapElement, tableColumnInfo);
            }
        }
    }

    /**
     * <code>
     *     <resultMap id="resultMapId" type="User">
     *         <id property="id" column="id"/>
     *         <id property="code" column="code"/>
     *         <column property="userDetail.user.id" column="id"/>
     *         <column property="userDetail.user.code" column="code"/>
     *         <association property="userDetail" column="{user_id=id,user_code=code}"/>
     *     </resultMap>
     *     <resultMap id="resultMapId" type="UserDetail">
     *         <column property="user.id" column="user_id"/>
     *         <column property="user.code" column="user_code"/>
     *         <association property="user" column="{user_id=user_id,user_code=user_code}"/>
     *     </resultMap>
     * </code>
     * @param resultMapElement
     * @param columnRelationInfo
     */
    private void addColumnRelationElement(Element resultMapElement, ResultMapInfo resultMapInfo, ColumnInfo columnInfo, ColumnRelationInfo columnRelationInfo) {
        ManyToMany manyToMany = columnRelationInfo.getManyToMany();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (manyToMany == null && StringUtils.isBlank(mappedBy)) {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                ResultMapHelper.resultColumnElement(resultMapElement, inverseForeignKeyColumnInfo);
            }
        }
    }

    private void addColumnRelationElement(Element resultMapElement, ResultMapInfo resultMapInfo) {
        EntityInfo resultMapEntityInfo = resultMapInfo.getEntityInfo();
        List<ColumnInfo> relationColumnInfoList = resultMapEntityInfo.getRelationColumnInfoList();
        for (ColumnInfo relationColumnInfo : relationColumnInfoList) {
            ColumnRelationInfo columnRelationInfo = relationColumnInfo.getColumnRelationInfo();
            ManyToMany manyToMany = columnRelationInfo.getManyToMany();
            if (manyToMany == null) {
                String mappedBy = columnRelationInfo.getMappedBy();
                if (StringUtils.isBlank(mappedBy)) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo subColumnInfo = new ColumnInfo();
                        subColumnInfo.setJavaColumnName(relationColumnInfo.getJavaColumnName() + ".id");
                        subColumnInfo.setDbColumnName(inverseForeignKeyColumnInfo.getName());
                        subColumnInfo.setDbColumnNameAlias(inverseForeignKeyColumnInfo.getNameAlias());
                        ResultMapHelper.resultColumnElement(resultMapElement, subColumnInfo);
                    }
                } else {
                    EntityInfo entityInfo = EntityInfoContextHolder.get(relationColumnInfo.getJavaType());
                    ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                    ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo subColumnInfo = new ColumnInfo();
                        subColumnInfo.setJavaColumnName(relationColumnInfo.getJavaColumnName() + ".user.id");
                        ColumnInfo referenceColumnInfo = resultMapEntityInfo.getDbColumnInfo(inverseForeignKeyColumnInfo.getReferencedColumnName());
                        subColumnInfo.setDbColumnName(referenceColumnInfo.getDbColumnName());
                        subColumnInfo.setDbColumnNameAlias(referenceColumnInfo.getDbColumnNameAlias());
                        ResultMapHelper.resultColumnElement(resultMapElement, subColumnInfo);
                    }
                }
            } else {

            }
        }
    }

    private void addResultMapRelationElement(Element resultMapElement, MapperInfo mapperInfo, EntityInfo resultMapEntityInfo, List<ResultMapInfo> resultMapInfoList) {
        if (ObjectUtils.isEmpty(resultMapInfoList)) {
            return;
        }
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            // TODO 这里判断是否存在子查询还可以优化
            ResultMapInfo existResultMapInfo = mapperInfo.getResultMapInfo(resultMapInfo.getEntityClazz());
            if (existResultMapInfo != null) {
                this.subSelect(resultMapElement, resultMapEntityInfo, resultMapInfo);
            } else {
                Element resultMapRelationElement = this.joinSelect(resultMapInfo, resultMapElement);
                List<ResultMapInfo> childrenResultMapInfoList = resultMapInfo.getResultMapInfoList();
                if (ObjectUtils.isNotEmpty(childrenResultMapInfoList)) {
                    this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, resultMapInfo.getEntityInfo(), childrenResultMapInfoList);
                }
            }
        }
    }

    private void subSelect(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapInfo) {
        ColumnInfo columnInfo = resultMapInfo.getColumnInfo();
        ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        Integer relationType = this.getRelationType(columnRelationInfo);
        if (relationType == 1) {
            this.associationColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else if (relationType == 2) {
            this.collectionColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinSelect(ResultMapInfo resultMapRelationInfo, Element resultMapElement) {
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
        String column = this.getColumn(parentEntityInfo, resultMapRelationInfo);
        return ResultMapHelper.associationColumnElement(resultMapElement, parentEntityInfo, resultMapRelationInfo, column);
    }

    private Element joinAssociationColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        return ResultMapHelper.joinAssociationColumnElement(resultMapElement, resultMapAssociationInfo);
    }

    private Element collectionColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo) {
        String column = this.getColumn(parentEntityInfo, resultMapRelationInfo);
        return ResultMapHelper.collectionColumnElement(resultMapElement, parentEntityInfo, resultMapRelationInfo, column);
    }

    private Element joinCollectionColumnElement(Element resultMapElement, ResultMapInfo resultMapAssociationInfo) {
        return ResultMapHelper.joinCollectionColumnElement(resultMapElement, resultMapAssociationInfo);
    }

    private Integer getRelationType(ColumnRelationInfo columnRelationInfo) {
        return ResultMapHelper.getRelationType(columnRelationInfo);
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
        FetchMode fetchMode = columnRelationInfo.getFetchMode();
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
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, parentEntityColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(inverseForeignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, inverseForeignKeyColumnInfo.getNameAlias());
                    } else {
                        column.put(inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getNameAlias());
                    }
                }
            }
        } else {
            String mappedBy = columnRelationInfo.getMappedBy();
            if (StringUtils.isNotBlank(mappedBy)) {
                ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(inverseForeignKeyColumnInfo.getReferencedColumnName());
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, parentEntityColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(inverseForeignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnRelationInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                    ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(foreignKeyColumnInfo.getReferencedColumnName());
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, parentEntityColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(foreignKeyColumnInfo.getName(), parentEntityColumnInfo.getDbColumnNameAlias());
                    }
                }
            }
        }
        return column.toString();
    }

    private void addNestedSelectCollection(Map<String, String> column, String nestedSelectCollectionValue) {
        column.put("nested_select_collection", nestedSelectCollectionValue);
    }
}
