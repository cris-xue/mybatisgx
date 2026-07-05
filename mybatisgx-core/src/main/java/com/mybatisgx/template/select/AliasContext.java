package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.EntityProjectionInfo;
import com.mybatisgx.model.RelationColumnInfo;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 别名上下文：构建并查询 MGXQL 用户别名 → 注解树节点 映射，封装无树时回退用户别名的约定。
 * <p>
 * 主表名/别名与树根关系也在准备阶段一并收敛，渲染器不再直接访问 aliasMap 或 rootRelation。
 */
public class AliasContext {

    private final Map<String, ColumnEntityRelation> aliasMap;
    private final Map<String, ColumnEntityRelation> pureAliasMap;
    private final String mainTableName;
    private final String mainTableAlias;
    private final ColumnEntityRelation rootRelation;
    private final FromClause fromClause;

    private AliasContext(Map<String, ColumnEntityRelation> aliasMap, Map<String, ColumnEntityRelation> pureAliasMap, String mainTableName, String mainTableAlias, ColumnEntityRelation rootRelation, FromClause fromClause) {
        this.aliasMap = aliasMap;
        this.pureAliasMap = pureAliasMap;
        this.mainTableName = mainTableName;
        this.mainTableAlias = mainTableAlias;
        this.rootRelation = rootRelation;
        this.fromClause = fromClause;
    }

    public static AliasContext build(SelectStatement selectStatement, ColumnEntityRelation rootRelation) {
        FromClause fromClause = selectStatement.getFromClause();
        Map<String, ColumnEntityRelation> aliasMap;
        Map<String, ColumnEntityRelation> pureAliasMap = new LinkedHashMap<>();
        if (rootRelation != null) {
            aliasMap = buildAliasTreeNodeMap(fromClause, rootRelation, pureAliasMap);
        } else {
            aliasMap = buildAliasMapNoTree(fromClause, pureAliasMap);
        }
        FromEntity primaryEntity = fromClause != null ? fromClause.getPrimaryEntity() : null;
        String mainTableName = (primaryEntity != null && primaryEntity.getEntityInfo() != null) ? primaryEntity.getEntityInfo().getTableName() : null;
        String mainTableAlias = (primaryEntity != null) ? primaryEntity.getAlias() : null;
        return new AliasContext(aliasMap, pureAliasMap, mainTableName, mainTableAlias, rootRelation, fromClause);
    }

    /**
     * 返回 MGXQL 用户声明的表别名。树节点的 tableNameAlias 仅用于 result map 列别名，不出现在 SQL 表引用中。
     */
    public String resolveTableAlias(String mgxqlAlias) {
        return this.resolveDbTableAlias(mgxqlAlias);
    }

    /**
     * 将 MGXQL 用户别名解析为数据库真实表别名（tableNameAlias）。
     * <p>
     * 从 pureAliasMap 查找对应树节点，若节点非 null 且 tableNameAlias 非空则返回 tableNameAlias，
     * 否则回退返回原始 mgxqlAlias。参数为 null 时返回 null。
     *
     * @param mgxqlAlias MGXQL 用户声明的纯别名（如 "u"、"r"）
     * @return 数据库真实表别名（如 "user_1_1"），查不到时回退返回原始别名
     */
    public String resolveDbTableAlias(String mgxqlAlias) {
        if (mgxqlAlias == null) {
            return null;
        }
        ColumnEntityRelation node = this.pureAliasMap.get(mgxqlAlias);
        if (node != null && StringUtils.isNotBlank(node.getTableNameAlias())) {
            return node.getTableNameAlias();
        }
        return mgxqlAlias;
    }

    public ColumnEntityRelation getNode(String alias) {
        return this.aliasMap.get(alias);
    }

    public String getMainTableName() {
        return this.mainTableName;
    }

    public String getMainTableAlias() {
        return this.mainTableAlias;
    }

    ColumnEntityRelation getRootRelation() {
        return this.rootRelation;
    }

    FromClause getFromClause() {
        return this.fromClause;
    }

    /**
     * 建立 MGXQL 用户别名 → 注解树节点 映射。
     * <p>
     * 主实体按 entityInfo.clazz 在关系树中查找匹配节点；各 JOIN 实体沿 onLeftAlias
     * 找到左实体树节点，在其 composites 中按 relationColumnInfo 身份（优先）或
     * entityInfo.clazz 匹配对应子节点。匹配不到时保留 null（渲染回退到用户别名，
     * 该实体不在 result map 中）。
     */
    private static Map<String, ColumnEntityRelation> buildAliasTreeNodeMap(FromClause fromClause, ColumnEntityRelation rootRelation, Map<String, ColumnEntityRelation> pureAliasMap) {
        Map<String, ColumnEntityRelation> aliasMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null) {
            ColumnEntityRelation primaryNode = null;
            if (primaryEntity.getEntityInfo() != null) {
                primaryNode = findTreeNodeByClazz(rootRelation, primaryEntity.getEntityInfo().getClazz());
            }
            String entityKey = String.format("%s%s", primaryEntity.getEntityName(), primaryEntity.getAlias());
            aliasMap.put(entityKey, primaryNode);
            if (StringUtils.isNotBlank(primaryEntity.getAlias())) {
                pureAliasMap.put(primaryEntity.getAlias(), primaryNode);
            }
        }
        if (fromClause.getJoinEntities() == null) {
            return aliasMap;
        }
        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            if (StringUtils.isBlank(joinEntity.getAlias())) {
                continue;
            }
            ColumnEntityRelation rightNode = null;
            String leftAlias = joinEntity.getOnLeftAlias();
            ColumnEntityRelation leftNode = StringUtils.isNotBlank(leftAlias) ? aliasMap.get(leftAlias) : null;
            if (leftNode != null) {
                rightNode = findChildTreeNode(leftNode, joinEntity);
            }
            if (rightNode == null && joinEntity.getEntityInfo() != null) {
                rightNode = findTreeNodeByClazz(rootRelation, joinEntity.getEntityInfo().getClazz());
            }
            String entityKey = String.format("%s%s", joinEntity.getEntityName(), joinEntity.getAlias());
            aliasMap.put(entityKey, rightNode);
            pureAliasMap.put(joinEntity.getAlias(), rightNode);
        }
        return aliasMap;
    }

    /**
     * 无注解树场景（聚合等）的别名映射：所有别名映射到 null，
     * {@link #resolveTableAlias} 回退到 MGXQL 用户别名。
     */
    private static Map<String, ColumnEntityRelation> buildAliasMapNoTree(FromClause fromClause, Map<String, ColumnEntityRelation> pureAliasMap) {
        Map<String, ColumnEntityRelation> aliasMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null && StringUtils.isNotBlank(primaryEntity.getAlias())) {
            aliasMap.put(primaryEntity.getAlias(), null);
            pureAliasMap.put(primaryEntity.getAlias(), null);
        }
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                if (StringUtils.isNotBlank(joinEntity.getAlias())) {
                    aliasMap.put(joinEntity.getAlias(), null);
                    pureAliasMap.put(joinEntity.getAlias(), null);
                }
            }
        }
        return aliasMap;
    }

    private static ColumnEntityRelation findChildTreeNode(ColumnEntityRelation leftNode, JoinEntity joinEntity) {
        if (leftNode == null || leftNode.getComposites() == null) {
            return null;
        }
        RelationColumnInfo bound = joinEntity.getRelationColumnInfo();
        // 优先身份匹配：findRelation 在左实体关系列表命中时，与树子节点 columnInfo 为同一对象
        if (bound != null) {
            for (Object child : leftNode.getComposites()) {
                ColumnEntityRelation childNode = (ColumnEntityRelation) child;
                if (childNode.getColumnInfo() == bound) {
                    return childNode;
                }
            }
        }
        // 回退：按实体类型匹配
        if (joinEntity.getEntityInfo() != null) {
            Class<?> joinClazz = joinEntity.getEntityInfo().getClazz();
            for (Object child : leftNode.getComposites()) {
                ColumnEntityRelation childNode = (ColumnEntityRelation) child;
                Class<?> entityClass;
                if (childNode.getEntityInfo() != null && TypeUtils.typeEquals(childNode.getEntityInfo(), EntityProjectionInfo.class)) {
                    EntityProjectionInfo entityProjectionInfo = (EntityProjectionInfo) childNode.getEntityInfo();
                    entityClass = entityProjectionInfo.getEntityClass();
                } else {
                    entityClass = childNode.getEntityInfo().getClazz();
                }
                if (childNode.getEntityInfo() != null && joinClazz.equals(entityClass)) {
                    return childNode;
                }
            }
        }
        return null;
    }

    private static ColumnEntityRelation findTreeNodeByClazz(ColumnEntityRelation node, Class<?> clazz) {
        if (node == null || clazz == null) {
            return null;
        }
        Class<?> entityClass;
        if (node.getEntityInfo() != null && TypeUtils.typeEquals(node.getEntityInfo(), EntityProjectionInfo.class)) {
            EntityProjectionInfo entityProjectionInfo = (EntityProjectionInfo) node.getEntityInfo();
            entityClass = entityProjectionInfo.getEntityClass();
        } else {
            entityClass = node.getEntityInfo().getClazz();
        }
        if (node.getEntityInfo() != null && clazz.equals(entityClass)) {
            return node;
        }
        if (node.getComposites() != null) {
            for (Object child : node.getComposites()) {
                ColumnEntityRelation found = findTreeNodeByClazz((ColumnEntityRelation) child, clazz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}