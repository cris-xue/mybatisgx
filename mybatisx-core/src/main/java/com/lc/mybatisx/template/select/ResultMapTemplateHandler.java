package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.TypeUtils;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
            if (TypeUtils.typeEquals(resultMapInfo, ResultMapInfo.class) || TypeUtils.typeEquals(resultMapInfo, SimpleNestedResultMapInfo.class)) {
                this.addIdColumnElement(resultMapElement, resultMapInfo.getEntityInfo());
                this.addColumnElement(resultMapElement, resultMapInfo.getTableColumnInfoList());
                this.addRelationColumnElement(resultMapElement, resultMapInfo);
                this.addRelationResultMapElement(resultMapElement, resultMapInfo);
            }
            if (TypeUtils.typeEquals(resultMapInfo, BatchNestedResultMapInfo.class)) {
                this.addIdColumnElement(resultMapElement, resultMapInfo.getEntityInfo());
                this.addRelationColumnElement(resultMapElement, resultMapInfo);
                this.addRelationResultMapElement(resultMapElement, resultMapInfo);
            }
            String resultMapXmlString = document.asXML();
            logger.info("select resultMap: \n{}", resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
        }
        return xNodeMap;
    }

    private void addIdColumnElement(Element resultMapElement, EntityInfo entityInfo) {
        IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
        List<ColumnInfo> composites = idColumnInfo.getComposites();
        if (ObjectUtils.isEmpty(composites)) {
            ResultMapHelper.idColumnElement(resultMapElement, idColumnInfo);
        } else {
            for (ColumnInfo columnInfo : composites) {
                String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName());
                ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                ResultMapHelper.idColumnElement(resultMapElement, composite);
            }
        }
    }

    private void addColumnElement(Element resultMapElement, List<ColumnInfo> tableColumnInfoList) {
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            if (TypeUtils.typeEquals(tableColumnInfo, ColumnInfo.class)) {
                ResultMapHelper.resultColumnElement(resultMapElement, tableColumnInfo);
            }
        }
    }

    /**
     * 添加字段关系节点，主要实现纯对象关系映射
     * <code>
     * <resultMap id="resultMapId" type="User">
     * <id property="id" column="id"/>
     * <id property="code" column="code"/>
     * <column property="userDetail.user.id" column="id"/>
     * <column property="userDetail.user.code" column="code"/>
     * <association property="userDetail" column="{user_id=id,user_code=code}"/>
     * </resultMap>
     * <resultMap id="resultMapId" type="UserDetail">
     * <column property="user.id" column="user_id"/>
     * <column property="user.code" column="user_code"/>
     * <association property="user" column="{user_id=user_id,user_code=user_code}"/>
     * </resultMap>
     * </code>
     *
     * @param resultMapElement
     * @param resultMapInfo
     */
    private void addRelationColumnElement(Element resultMapElement, ResultMapInfo resultMapInfo) {
        EntityInfo resultMapEntityInfo = resultMapInfo.getEntityInfo();
        for (ColumnInfo columnInfo : resultMapEntityInfo.getRelationColumnInfoList()) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            RelationType relationType = relationColumnInfo.getRelationType();
            if (relationType != RelationType.MANY_TO_MANY) {
                if (mappedByRelationColumnInfo == null) {
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                        ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();

                        String javaColumnName = String.format("%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnPath());
                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                        ResultMapHelper.resultColumnElement(resultMapElement, composite);
                    }
                }
            }
        }
    }

    /**
     * 添加结果集关联节点
     *
     * @param resultMapElement
     * @param resultMapInfo
     */
    private void addRelationResultMapElement(Element resultMapElement, ResultMapInfo resultMapInfo) {
        for (ResultMapInfo composite : resultMapInfo.getComposites()) {
            ResultMapInfo.NestedSelect nestedSelect = composite.getNestedSelect();
            // 是否存在独立的 resultMap，如果存在，为子查询，如果不存在，则为join关联查询
            if (nestedSelect != null) {
                this.subSelect(resultMapElement, composite);
            } else {
                Element resultMapRelationElement = this.joinSelect(resultMapElement, composite);
                this.addRelationResultMapElement(resultMapRelationElement, composite);
            }
        }
    }

    private void subSelect(Element resultMapElement, ResultMapInfo resultMapInfo) {
        String column = this.getColumn(resultMapInfo);
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
        RelationType relationType = relationColumnInfo.getRelationType();
        if (relationType == RelationType.ONE_TO_ONE || relationType == RelationType.MANY_TO_ONE) {
            String relationProperty = this.getRelationProperty(resultMapInfo);
            ResultMapHelper.associationColumnElement(resultMapElement, resultMapInfo, column, relationProperty);
        } else if (relationType == RelationType.ONE_TO_MANY || relationType == RelationType.MANY_TO_MANY) {
            ResultMapHelper.collectionColumnElement(resultMapElement, resultMapInfo, column);
        } else {
            throw new RuntimeException(relationColumnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinSelect(Element resultMapElement, ResultMapInfo resultMapInfo) {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
        RelationType relationType = relationColumnInfo.getRelationType();
        if (relationType == RelationType.ONE_TO_ONE || relationType == RelationType.MANY_TO_ONE) {
            Element resultMapRelationElement = ResultMapHelper.joinAssociationColumnElement(resultMapElement, resultMapInfo);
            this.addIdColumnElement(resultMapRelationElement, resultMapInfo.getEntityInfo());
            this.addColumnElement(resultMapRelationElement, resultMapInfo.getTableColumnInfoList());
            return resultMapRelationElement;
        } else if (relationType == RelationType.ONE_TO_MANY || relationType == RelationType.MANY_TO_MANY) {
            Element resultMapCollectionElement = ResultMapHelper.joinCollectionColumnElement(resultMapElement, resultMapInfo);
            this.addIdColumnElement(resultMapCollectionElement, resultMapInfo.getEntityInfo());
            this.addColumnElement(resultMapCollectionElement, resultMapInfo.getTableColumnInfoList());
            return resultMapCollectionElement;
        } else {
            throw new RuntimeException(relationColumnInfo.getJavaType() + "没有关联注解");
        }
    }

    private String getRelationProperty(ResultMapInfo resultMapRelationInfo) {
        Map<String, String> relationProperty = new LinkedHashMap<>();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapRelationInfo.getColumnInfo();
        RelationColumnInfo mappedByColumnRelationInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        if (mappedByColumnRelationInfo == null) {
            // 关系维护方
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                String foreignKeyJavaColumnPath = foreignKeyColumnInfo.getJavaColumnPath();
                String referencedJavaColumnPath = referencedColumnInfo.getJavaColumnPath();
                String left = foreignKeyJavaColumnPath + "." + referencedJavaColumnPath;
                String right = referencedJavaColumnPath;
                relationProperty.put(left, right);
            }
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                String foreignKeyJavaColumnPath = foreignKeyColumnInfo.getJavaColumnPath();
                String referencedJavaColumnPath = referencedColumnInfo.getJavaColumnPath();
                String left = referencedJavaColumnPath;
                String right = foreignKeyJavaColumnPath + "." + referencedJavaColumnPath;
                relationProperty.put(left, right);
            }
        }
        return relationProperty.toString();
    }

    /**
     * 生成关系查询参数，参数名称带有业务属性，如查询用户，参数名为user_id，查询角色，参数名为role_id
     * <code>
     * <association property="user" column="{user_id=user_id}" javaType="com.lc.mybatisx.test.model.entity.User" fetchType="lazy" select="findUser"/>
     * <collection property="roleList" column="{user_id=id}" javaType="java.util.List" ofType="com.lc.mybatisx.test.model.entity.Role" fetchType="lazy" select="findRoleList"/>
     * </code>
     *
     * @param resultMapRelationInfo
     * @return
     */
    private String getColumn(ResultMapInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        ManyToMany manyToMany = relationColumnInfo.getManyToMany();
        FetchMode fetchMode = relationColumnInfo.getFetchMode();
        Map<String, String> column = new HashMap();
        if (fetchMode == FetchMode.BATCH) {
            if (manyToMany == null) {
                if (mappedByRelationColumnInfo != null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    }
                } else {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                        this.addNestedSelectCollection(column, foreignKeyColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                if (mappedByRelationColumnInfo != null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    }
                } else {
                    List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    }
                }
            }
        } else {
            if (manyToMany == null) {
                if (mappedByRelationColumnInfo != null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                        ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                        column.put(foreignKeyColumnInfo.getJavaColumnName(), referencedColumnInfo.getDbColumnNameAlias());
                    }
                } else {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                        column.put(foreignKeyColumnInfo.getJavaColumnName(), foreignKeyColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                if (mappedByRelationColumnInfo != null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                        ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                        column.put(foreignKeyColumnInfo.getJavaColumnName(), referencedColumnInfo.getDbColumnNameAlias());
                    }
                } else {
                    List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        column.put(foreignKeyColumnInfo.getJavaColumnName(), referencedColumnInfo.getDbColumnNameAlias());
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
