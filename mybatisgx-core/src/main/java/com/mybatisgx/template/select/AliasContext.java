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
 * 别名上下文：构建并查询 MGXQL 用户别名 → 注解树节点 / FROM 实体 映射，
 * 封装无树时回退用户别名的约定。
 * <p>
 * 统一提供：
 * <ul>
 *   <li>{@link #resolveTableAlias(String)}：MGXQL 别名 → SQL 表别名</li>
 *   <li>{@link #getNode(String)}：MGXQL 别名（或实体名）→ 关系树节点</li>
 *   <li>{@link #getNode(FromEntity)}：FROM 实体 → 关系树节点（自动处理无别名场景）</li>
 *   <li>{@link #getFromEntity(String)}：MGXQL 别名 → FROM 实体</li>
 * </ul>
 */
public class AliasContext {

    private final Map<String, ColumnEntityRelation> aliasNodeMap;
    private final Map<String, FromEntity> fromEntityMap;
    private final String mainTableName;
    private final String mainTableAlias;
    private final ColumnEntityRelation rootRelation;
    private final FromClause fromClause;

    private AliasContext(Map<String, ColumnEntityRelation> aliasNodeMap, Map<String, FromEntity> fromEntityMap, String mainTableName, String mainTableAlias, ColumnEntityRelation rootRelation, FromClause fromClause) {
        this.aliasNodeMap = aliasNodeMap;
        this.fromEntityMap = fromEntityMap;
        this.mainTableName = mainTableName;
        this.mainTableAlias = mainTableAlias;
        this.rootRelation = rootRelation;
        this.fromClause = fromClause;
    }

    public static AliasContext build(SelectStatement selectStatement, ColumnEntityRelation rootRelation) {
        FromClause fromClause = selectStatement.getFromClause();
        Map<String, ColumnEntityRelation> aliasNodeMap;
        if (rootRelation != null) {
            aliasNodeMap = buildAliasTreeNodeMap(fromClause, rootRelation);
        } else {
            aliasNodeMap = buildAliasMapNoTree(fromClause);
        }
        Map<String, FromEntity> fromEntityMap = buildFromEntityMap(fromClause);
        FromEntity primaryEntity = fromClause != null ? fromClause.getPrimaryEntity() : null;
        String mainTableName = (primaryEntity != null && primaryEntity.getEntityInfo() != null) ? primaryEntity.getEntityInfo().getTableName() : null;
        String mainTableAlias = (primaryEntity != null) ? primaryEntity.getAlias() : null;
        return new AliasContext(aliasNodeMap, fromEntityMap, mainTableName, mainTableAlias, rootRelation, fromClause);
    }

    /**
     * 将 MGXQL 用户别名解析为 SQL 表别名。
     * <p>
     * 从 aliasNodeMap 查找对应树节点，若节点非 null 且 tableNameAlias 非空则返回 tableNameAlias，
     * 否则回退返回原始 mgxqlAlias。参数为 null 时返回 null。
     *
     * @param mgxqlAlias MGXQL 用户声明的纯别名（如 "u"、"r"）
     * @return SQL 表别名（如 "user_1_1"），查不到时回退返回原始别名
     */
    public String resolveTableAlias(String mgxqlAlias) {
        if (mgxqlAlias == null) {
            return null;
        }
        ColumnEntityRelation node = this.aliasNodeMap.get(mgxqlAlias);
        if (node != null && StringUtils.isNotBlank(node.getTableNameAlias())) {
            return node.getTableNameAlias();
        }
        return mgxqlAlias;
    }

    /**
     * 将 MGXQL FROM 实体解析为 SQL 表别名。
     * <p>
     * 从 FromEntity 提取 MGXQL 别名（alias 非空用 alias，否则用 entityName），
     * 再委托 {@link #resolveTableAlias(String)} 完成解析。
     *
     * @param fromEntity FROM 实体（主实体或 JOIN 实体）
     * @return SQL 表别名（如 "user_1_1"），查不到时回退返回原始别名；fromEntity 为 null 时返回 null
     */
    public String resolveTableAlias(FromEntity fromEntity) {
        if (fromEntity == null) {
            return null;
        }
        return resolveTableAlias(resolveKey(fromEntity));
    }

    /**
     * 根据纯别名获取关系树节点。
     *
     * @param alias MGXQL 用户声明的纯别名（如 "u"、"r"）
     * @return 对应的 ColumnEntityRelation 节点，查不到时返回 null
     */
    public ColumnEntityRelation getNode(String alias) {
        return this.aliasNodeMap.get(alias);
    }

    /**
     * 根据 FROM 实体获取关系树节点，自动处理无别名场景。
     * <p>
     * 优先用 alias 查找；若 alias 为空则用 entityName 查找。
     *
     * @param fromEntity FROM 实体
     * @return 对应的 ColumnEntityRelation 节点，查不到时返回 null
     */
    public ColumnEntityRelation getNode(FromEntity fromEntity) {
        if (fromEntity == null) {
            return null;
        }
        String key = resolveKey(fromEntity);
        return this.aliasNodeMap.get(key);
    }

    /**
     * 根据纯别名获取 FROM 实体（主实体或 JOIN 实体）。
     *
     * @param alias MGXQL 用户声明的纯别名（如 "u"、"r"）
     * @return 对应的 FromEntity，查不到时返回 null
     */
    public FromEntity getFromEntity(String alias) {
        return this.fromEntityMap.get(alias);
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
     * 解析映射 key：alias 非空时用 alias，否则用 entityName。
     */
    private static String resolveKey(FromEntity fromEntity) {
        return StringUtils.isNotBlank(fromEntity.getAlias()) ? fromEntity.getAlias() : fromEntity.getEntityName();
    }

    /**
     * 构建 key → FromEntity 映射（主实体 + JOIN 实体）。
     * <p>
     * 当实体无别名时，使用 entityName 作为 key。
     */
    private static Map<String, FromEntity> buildFromEntityMap(FromClause fromClause) {
        Map<String, FromEntity> map = new LinkedHashMap<>();
        if (fromClause == null) {
            return map;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null) {
            map.put(resolveKey(primaryEntity), primaryEntity);
        }
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                map.put(resolveKey(joinEntity), joinEntity);
            }
        }
        return map;
    }

    /**
     * 建立 MGXQL 用户别名（或实体名）→ 注解树节点 映射。
     * <p>
     * 主实体按 entityInfo.clazz 在关系树中查找匹配节点；各 JOIN 实体沿 onLeftAlias
     * 找到左实体树节点，在其 composites 中按 relationColumnInfo 身份（优先）或
     * entityInfo.clazz 匹配对应子节点。匹配不到时保留 null（渲染回退到用户别名，
     * 该实体不在 result map 中）。
     * <p>
     * 当实体无别名时，使用 entityName 作为 key，确保单表查询（如 select * from User）
     * 仍可通过 entityName 找到树节点。
     */
    private static Map<String, ColumnEntityRelation> buildAliasTreeNodeMap(FromClause fromClause, ColumnEntityRelation rootRelation) {
        Map<String, ColumnEntityRelation> aliasNodeMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasNodeMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null) {
            ColumnEntityRelation primaryNode = null;
            if (primaryEntity.getEntityInfo() != null) {
                primaryNode = findTreeNodeByClazz(rootRelation, primaryEntity.getEntityInfo().getClazz());
            }
            aliasNodeMap.put(resolveKey(primaryEntity), primaryNode);
        }
        if (fromClause.getJoinEntities() == null) {
            return aliasNodeMap;
        }
        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            ColumnEntityRelation rightNode = null;
            String leftAlias = joinEntity.getOnLeftAlias();
            ColumnEntityRelation leftNode = StringUtils.isNotBlank(leftAlias) ? aliasNodeMap.get(leftAlias) : null;
            if (leftNode != null) {
                rightNode = findChildTreeNode(leftNode, joinEntity);
            }
            if (rightNode == null && joinEntity.getEntityInfo() != null) {
                rightNode = findTreeNodeByClazz(rootRelation, joinEntity.getEntityInfo().getClazz());
            }
            aliasNodeMap.put(resolveKey(joinEntity), rightNode);
        }
        return aliasNodeMap;
    }

    /**
     * 无注解树场景（聚合等）的别名映射：所有别名映射到 null，
     * {@link #resolveTableAlias} 回退到 MGXQL 用户别名。
     */
    private static Map<String, ColumnEntityRelation> buildAliasMapNoTree(FromClause fromClause) {
        Map<String, ColumnEntityRelation> aliasNodeMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasNodeMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null) {
            aliasNodeMap.put(resolveKey(primaryEntity), null);
        }
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                aliasNodeMap.put(resolveKey(joinEntity), null);
            }
        }
        return aliasNodeMap;
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
