package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.TypeUtils;
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
            if (TypeUtils.typeEquals(resultMapInfo, ManyToManyBatchSelectResultMapInfo.class)) {
                ManyToManyBatchSelectResultMapInfo manyToManyBatchSelectResultMapInfo = (ManyToManyBatchSelectResultMapInfo) resultMapInfo;
                this.buildResultMap(document, mapperInfo, manyToManyBatchSelectResultMapInfo.getManyToManyBatchSelectResultMapInfo());
            }
            if (TypeUtils.typeEquals(resultMapInfo, ResultMapInfo.class)) {
                this.buildResultMap(document, mapperInfo, resultMapInfo);
            }
            String resultMapXmlString = document.asXML();
            logger.info("select resultMap: \n{}", resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeMap.put(resultMapInfo.getId(), xNode);
        }
        return xNodeMap;
    }

    private void buildResultMap(Document document, MapperInfo mapperInfo, ResultMapInfo resultMapInfo) {
        Element resultMapElement = ResultMapHelper.addResultMapElement(document, resultMapInfo);
        this.addIdColumnElement(resultMapElement, resultMapInfo.getEntityInfo());
        this.addColumnElement(resultMapElement, resultMapInfo.getTableColumnInfoList());
        this.addRelationColumnElement(resultMapElement, resultMapInfo);
        this.addResultMapRelationElement(resultMapElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
    }

    private void addIdColumnElement(Element resultMapElement, EntityInfo entityInfo) {
        IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
        List<ColumnInfo> columnList = idColumnInfo.getColumnInfoList();
        if (ObjectUtils.isEmpty(columnList)) {
            ResultMapHelper.idColumnElement(resultMapElement, idColumnInfo);
        } else {
            for (ColumnInfo columnInfo : columnList) {
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
     * @param resultMapInfo
     */
    private void addRelationColumnElement(Element resultMapElement, ResultMapInfo resultMapInfo) {
        EntityInfo resultMapEntityInfo = resultMapInfo.getEntityInfo();
        for (ColumnInfo columnInfo : resultMapEntityInfo.getRelationColumnInfoList()) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                if (mappedByRelationColumnInfo == null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                        ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();

                        String javaColumnName = String.format("%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnPath());
                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                        ResultMapHelper.resultColumnElement(resultMapElement, composite);
                    }
                } else {
                    /*EntityInfo entityInfo = EntityInfoContextHolder.get(relationColumnInfo.getJavaType());
                    ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);
                    ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                        String javaColumnName = String.format("%s.%s.%s", relationColumnInfo.getJavaColumnName(), mappedByColumnInfo.getJavaColumnName(), referencedColumnName);
                        ColumnInfo referenceColumnInfo = resultMapEntityInfo.getDbColumnInfo(referencedColumnName);

                        ColumnInfo subColumnInfo = new ColumnInfo();
                        subColumnInfo.setJavaColumnName(javaColumnName);
                        subColumnInfo.setDbColumnName(referenceColumnInfo.getDbColumnName());
                        subColumnInfo.setDbColumnNameAlias(referenceColumnInfo.getDbColumnNameAlias());
                        ResultMapHelper.resultColumnElement(resultMapElement, subColumnInfo);
                    }*/
                }
            } else {
                if (mappedByRelationColumnInfo == null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {

                    }
                } else {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {

                    }
                }
            }
        }
    }

    /**
     * 添加结果集关联节点
     * @param resultMapElement
     * @param mapperInfo
     * @param resultMapEntityInfo
     * @param resultMapInfoList
     */
    private void addResultMapRelationElement(Element resultMapElement, MapperInfo mapperInfo, EntityInfo resultMapEntityInfo, List<ResultMapInfo> resultMapInfoList) {
        if (ObjectUtils.isEmpty(resultMapInfoList)) {
            return;
        }
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
            if (relationColumnInfo.getFetchMode() == FetchMode.SELECT) {
                this.subSelect(resultMapElement, resultMapEntityInfo, resultMapInfo);
            }
            if (relationColumnInfo.getFetchMode() == FetchMode.BATCH) {
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType == RelationType.ONE_TO_ONE || relationType == RelationType.MANY_TO_ONE) {
                    this.subSelect(resultMapElement, resultMapEntityInfo, resultMapInfo);
                }
                if (relationType == RelationType.ONE_TO_MANY || relationType == RelationType.MANY_TO_MANY) {
                    String nestedSelectId = resultMapInfo.getNestedSelectId();
                    if (StringUtils.isNotBlank(nestedSelectId)) {
                        this.subSelect(resultMapElement, resultMapEntityInfo, resultMapInfo);
                    } else {
                        Element resultMapRelationElement = this.joinSelect(resultMapElement, resultMapInfo);
                        this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
                    }
                }
            }
            if (relationColumnInfo.getFetchMode() == FetchMode.JOIN) {
                Element resultMapRelationElement = this.joinSelect(resultMapElement, resultMapInfo);
                this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
            }

            // 是否存在独立的 resultMap，如果存在，为子查询，如果不存在，则为join关联查询
            /*ResultMapInfo existIndependenceResultMapInfo = mapperInfo.getResultMapInfo(resultMapInfo.getEntityClazz());
            if (existIndependenceResultMapInfo != null) {
                this.subSelect(resultMapElement, resultMapEntityInfo, resultMapInfo);
            } else {
                Element resultMapRelationElement = this.joinSelect(resultMapElement, resultMapInfo);
                this.addResultMapRelationElement(resultMapRelationElement, mapperInfo, resultMapInfo.getEntityInfo(), resultMapInfo.getResultMapInfoList());
            }*/
        }
    }

    private void subSelect(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapInfo) {
        ColumnInfo columnInfo = resultMapInfo.getColumnInfo();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        RelationType relationType = relationColumnInfo.getRelationType();
        if (relationType == RelationType.ONE_TO_ONE || relationType == RelationType.MANY_TO_ONE) {
            this.associationColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else if (relationType == RelationType.ONE_TO_MANY || relationType == RelationType.MANY_TO_MANY) {
            this.collectionColumnElement(resultMapElement, parentEntityInfo, resultMapInfo);
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element joinSelect(Element resultMapElement, ResultMapInfo resultMapRelationInfo) {
        ColumnInfo columnInfo = resultMapRelationInfo.getColumnInfo();
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        RelationType relationType = relationColumnInfo.getRelationType();
        if (relationType == RelationType.ONE_TO_ONE || relationType == RelationType.MANY_TO_ONE) {
            Element resultMapRelationElement = this.joinAssociationColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapRelationElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapRelationElement;
        } else if (relationType == RelationType.ONE_TO_MANY || relationType == RelationType.MANY_TO_MANY) {
            Element resultMapCollectionElement = this.joinCollectionColumnElement(resultMapElement, resultMapRelationInfo);
            this.addColumnElement(resultMapCollectionElement, resultMapRelationInfo.getTableColumnInfoList());
            return resultMapCollectionElement;
        } else {
            throw new RuntimeException(columnInfo.getJavaType() + "没有关联注解");
        }
    }

    private Element associationColumnElement(Element resultMapElement, EntityInfo parentEntityInfo, ResultMapInfo resultMapRelationInfo) {
        String column = this.getColumn(parentEntityInfo, resultMapRelationInfo);
        Map<String, String> leftRightMap = new LinkedHashMap<>();
        List<ColumnInfo> relationColumnInfoList = parentEntityInfo.getRelationColumnInfoList();
        for (ColumnInfo columnInfo : relationColumnInfoList) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
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
                    leftRightMap.put(left, right);
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
                    leftRightMap.put(left, right);
                }
            }
        }
        return ResultMapHelper.associationColumnElement(resultMapElement, resultMapRelationInfo, column, leftRightMap.toString());
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
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        FetchMode fetchMode = relationColumnInfo.getFetchMode();
        ManyToMany manyToMany = relationColumnInfo.getManyToMany();
        Map<String, String> column = new HashMap();
        if (manyToMany == null) {
            if (mappedByRelationColumnInfo != null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    // ColumnInfo parentEntityColumnInfo = parentEntityInfo.getColumnInfo(inverseForeignKeyInfo.getReferencedColumnName());
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(foreignKeyColumnInfo.getDbColumnName(), referencedColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, foreignKeyColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(foreignKeyColumnInfo.getDbColumnName(), foreignKeyColumnInfo.getDbColumnNameAlias());
                    }
                }
            }
        } else {
            if (mappedByRelationColumnInfo != null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(foreignKeyColumnInfo.getDbColumnName(), referencedColumnInfo.getDbColumnNameAlias());
                    }
                }
            } else {
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                    if (fetchMode == FetchMode.BATCH) {
                        this.addNestedSelectCollection(column, referencedColumnInfo.getDbColumnNameAlias());
                    } else {
                        column.put(foreignKeyColumnInfo.getDbColumnName(), referencedColumnInfo.getDbColumnNameAlias());
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
