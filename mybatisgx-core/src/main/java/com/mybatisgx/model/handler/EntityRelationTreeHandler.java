package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.JoinTable;
import com.mybatisgx.annotation.ManyToMany;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 实体关系信息处理
 *
 * @author ccxuef
 * @date 2025/7/27 14:31
 */
public class EntityRelationTreeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRelationTreeHandler.class);

    private static final ColumnInfoHandler.ColumnMap columnMapHandler = new ColumnInfoHandler.ColumnMap();
    private static final ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private static final TableColumnNameAlias tableColumnNameAlias = new TableColumnNameAlias();

    public void execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        SqlCommandType sqlCommandType = methodInfo.getSqlCommandType();
        if (Arrays.asList(SqlCommandType.INSERT, SqlCommandType.DELETE, SqlCommandType.UPDATE).contains(sqlCommandType)) {
            return;
        }

        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        EntityInfo entityInfo = selectStatement.getFromClause().getPrimaryEntity().getEntityInfo();

        // 1、创建实体关系树   2、解决循环引用问题
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(entityInfo.getClazz());
        if (entityRelationTree == null) {
            EntityRelationDependencyTree entityRelationDependencyTree = EntityRelationDependencyTree.build(null, entityInfo.getClazz());
            entityRelationTree = this.buildEntityRelationTree(null, entityInfo, entityRelationDependencyTree, 1, 1);
            mapperInfo.addEntityRelationTree(entityRelationTree);
        }
        selectStatement.setMgxqlEntityRelationTree(entityRelationTree);

        // 处理返回非根实体的情况，例如：UserDao中的方法返回Role
        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Entity entity = methodReturnInfo.getType().getAnnotation(Entity.class);
        if (entity != null && methodReturnInfo.getType() != entityRelationTree.getEntityClazz()) {
            EntityRelationTree childEntityRelationTree = this.findByEntityClass(entityRelationTree, methodReturnInfo.getType());
            mapperInfo.addEntityRelationTree(childEntityRelationTree);
        }

        // 处理返回dto投影的情况
        if (entity == null && methodReturnInfo.getClassCategory() != TypeCategory.SIMPLE) {
            EntityProjection entityProjection = new EntityProjection();
            EntityRelationTree entityProjectionRelationTree = entityProjection.execute(mapperInfo, methodInfo, entityRelationTree);
            mapperInfo.addEntityRelationTree(entityProjectionRelationTree);
        }
    }

    private EntityRelationTree findByEntityClass(EntityRelationTree entityRelationTree, Class<?> clazz) {
        List<EntityRelationTree> composites = entityRelationTree.getComposites();
        for (EntityRelationTree composite : composites) {
            if (clazz != composite.getEntityClazz()) {
                return this.findByEntityClass(composite, clazz);
            }
            return composite;
        }
        return null;
    }

    private static class EntityProjection {

        /**
         * 创建投影实体关系树
         * @param mapperInfo
         * @param methodInfo
         */
        public EntityRelationTree execute(MapperInfo mapperInfo, MethodInfo methodInfo, EntityRelationTree mgxqlEntityRelationTree) {
            MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
            Class<?> resultClass = methodReturnInfo.getType();

            List<JoinPathNode> pathChain = this.buildJoinPathChain(methodInfo);
            if (ObjectUtils.isNotEmpty(pathChain)) {
                // MGXQL 路径：递归构建多层投影树
                EntityInfo rootEntityInfo = pathChain.get(0).entityInfo;
                EntityRelationTree entityRelationTree = this.buildProjectionEntityRelationTree(resultClass, rootEntityInfo, null, pathChain, 0, mgxqlEntityRelationTree, mgxqlEntityRelationTree);
                return entityRelationTree;
            } else {
                // 非 MGXQL 路径：保留现有扁平构建逻辑
                List<ColumnInfo> entityColumnInfoList = new ArrayList<>();
                for (ColumnInfo columnInfo : methodReturnInfo.getColumnInfoList()) {
                    ColumnInfo entityColumnInfo = mapperInfo.getEntityInfo().getColumnInfo(columnInfo.getJavaColumnName());
                    if (entityColumnInfo != null) {
                        entityColumnInfoList.add(entityColumnInfo);
                    }
                }

                EntityProjectionInfo entityProjectionInfo = (EntityProjectionInfo) EntityInfo.Builder.of(new EntityProjectionInfo())
                        .setClazz(resultClass)
                        .setTableName(mapperInfo.getTableName())
                        .setColumnInfoList(entityColumnInfoList)
                        .build();
                entityProjectionInfo.setEntityClass(mapperInfo.getEntityInfo().getClazz());
                columnMapHandler.process(entityProjectionInfo);

                String tableNameAlias = tableColumnNameAlias.process(1, 1, entityProjectionInfo);
                EntityRelationTree entityRelationTree = new EntityRelationTree();
                entityRelationTree.setTableNameAlias(tableNameAlias);
                entityRelationTree.setLevel(1);
                entityRelationTree.setIndex(1);
                entityRelationTree.setEntityInfo(entityProjectionInfo);

                return entityRelationTree;
            }
        }

        /**
         * 从 MethodInfo 的 MGXQL SelectStatement 构建路径链
         *
         * @param methodInfo 方法信息
         * @return 路径链，若非 SelectStatement 则返回空列表
         */
        private List<JoinPathNode> buildJoinPathChain(MethodInfo methodInfo) {
            MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
            if (!(mgxqlStatement instanceof SelectStatement)) {
                return Collections.emptyList();
            }
            SelectStatement selectStatement = (SelectStatement) mgxqlStatement;
            if (selectStatement.getFromClause() == null) {
                return Collections.emptyList();
            }
            List<JoinPathNode> pathChain = new ArrayList<>();
            // 首节点：主实体，relationColumnInfo 为 null
            FromEntity primaryEntity = selectStatement.getFromClause().getPrimaryEntity();
            if (primaryEntity == null || primaryEntity.getEntityInfo() == null) {
                return Collections.emptyList();
            }
            pathChain.add(new JoinPathNode(primaryEntity.getEntityInfo(), null));
            // 遍历 JOIN 实体，每个节点的 relationColumnInfo 取自 JoinEntity
            List<JoinEntity> joinEntities = selectStatement.getFromClause().getJoinEntities();
            for (JoinEntity joinEntity : joinEntities) {
                if (joinEntity.getEntityInfo() != null) {
                    pathChain.add(new JoinPathNode(joinEntity.getEntityInfo(), joinEntity.getRelationColumnInfo()));
                }
            }
            return pathChain;
        }

        /**
         * 递归构建多层投影实体关系树
         *
         * @param projectionClass       投影 DTO 类
         * @param currentEntityInfo     当前路径链上匹配到的实体
         * @param relationColInfo       关系字段（根节点为 null）
         * @param pathChain             完整路径链
         * @param pathIndex             当前在路径链上的位置
         * @param mgxqlEntityRelationTree 完整实体关系树（全量）
         * @param currentFullTreeNode   当前对应的完整树节点，用于获取 level/index/tableNameAlias/middleEntityInfo
         * @return 实体关系树节点
         */
        private EntityRelationTree buildProjectionEntityRelationTree(Class<?> projectionClass, EntityInfo currentEntityInfo, RelationColumnInfo relationColInfo, List<JoinPathNode> pathChain, int pathIndex, EntityRelationTree mgxqlEntityRelationTree, EntityRelationTree currentFullTreeNode) {
            // 获取投影 DTO 的 ColumnInfo 列表
            List<ColumnInfo> dtoColumnInfoList = columnInfoHandler.getColumnInfoList(projectionClass, currentEntityInfo.getTypeParameterMap());
            List<ColumnInfo> replacedColumnInfoList = new ArrayList<>();
            List<CompositeFieldEntry> compositeFields = new ArrayList<>();

            for (ColumnInfo dtoColumnInfo : dtoColumnInfoList) {
                String javaColumnName = dtoColumnInfo.getJavaColumnName();
                // 普通字段匹配：在当前实体上查找
                ColumnInfo entityColumnInfo = currentEntityInfo.getColumnInfo(javaColumnName);
                if (entityColumnInfo != null && !(entityColumnInfo instanceof RelationColumnInfo)) {
                    replacedColumnInfoList.add(entityColumnInfo);
                    continue;
                }
                // 复合字段处理：匹配失败且为复合类型时，沿路径链搜索
                if (this.isCompositeField(dtoColumnInfo)) {
                    MatchResult matchResult = this.searchDownPath(pathChain, javaColumnName, pathIndex);
                    if (matchResult != null) {
                        compositeFields.add(new CompositeFieldEntry(dtoColumnInfo, matchResult));
                    } else {
                        LOGGER.debug("投影 DTO {} 的复合字段 {} 在路径链中未匹配，已忽略", projectionClass.getSimpleName(), javaColumnName);
                    }
                } else {
                    LOGGER.debug("投影 DTO {} 的字段 {} 在实体 {} 上未匹配，已忽略", projectionClass.getSimpleName(), javaColumnName, currentEntityInfo.getClazzName());
                }
            }

            // 构建合成 EntityInfo
            EntityProjectionInfo entityProjectionInfo = (EntityProjectionInfo) EntityInfo.Builder.of(new EntityProjectionInfo())
                    .setClazz(projectionClass)
                    .setTableName(currentEntityInfo.getTableName())
                    .setColumnInfoList(replacedColumnInfoList)
                    .build();
            entityProjectionInfo.setEntityClass(currentEntityInfo.getClazz());
            columnMapHandler.process(entityProjectionInfo);

            // 从完整树节点获取 level/index/tableNameAlias/middleEntityInfo
            int level = currentFullTreeNode != null ? currentFullTreeNode.getLevel() : 1;
            int index = currentFullTreeNode != null ? currentFullTreeNode.getIndex() : 1;
            String tableNameAlias = tableColumnNameAlias.process(level, index, entityProjectionInfo);

            // 构建 EntityRelationTree 节点
            EntityRelationTree entityRelationTree = new EntityRelationTree();
            entityRelationTree.setLevel(level);
            entityRelationTree.setIndex(index);
            entityRelationTree.setTableNameAlias(tableNameAlias);
            entityRelationTree.setColumnInfo(relationColInfo);
            entityRelationTree.setMiddleEntityInfo(currentFullTreeNode != null ? currentFullTreeNode.getMiddleEntityInfo() : null);
            entityRelationTree.setEntityInfo(entityProjectionInfo);

            // 对关系字段递归调用
            for (CompositeFieldEntry entry : compositeFields) {
                MatchResult matchResult = entry.matchResult;
                Class<?> nestedDtoClass = entry.dtoColumnInfo.getJavaType();
                EntityInfo targetEntityInfo = pathChain.get(matchResult.pathIndex).entityInfo;
                // 从完整树中查找匹配的子节点
                EntityRelationTree childFullTreeNode = this.findChildInFullTree(mgxqlEntityRelationTree, matchResult.relationColumnInfo);
                EntityRelationTree subTree = this.buildProjectionEntityRelationTree(
                        nestedDtoClass, targetEntityInfo, matchResult.relationColumnInfo,
                        pathChain, matchResult.pathIndex, mgxqlEntityRelationTree, childFullTreeNode);
                if (subTree != null) {
                    entityRelationTree.addComposites(subTree);
                }
            }

            return entityRelationTree;
        }

        /**
         * 在完整实体关系树中查找匹配的子节点
         * <p>
         * 优先按 relationColumnInfo 身份匹配（同一对象引用），身份匹配失败时按 javaColumnName 回退匹配
         *
         * @param fullTreeRoot       完整实体关系树根节点
         * @param relationColumnInfo 关系字段
         * @return 匹配到的完整树节点，未找到返回 null
         */
        private EntityRelationTree findChildInFullTree(EntityRelationTree fullTreeRoot, RelationColumnInfo relationColumnInfo) {
            if (fullTreeRoot == null || relationColumnInfo == null) {
                return null;
            }
            // 优先身份匹配
            EntityRelationTree found = this.findChildByRelationIdentity(fullTreeRoot, relationColumnInfo);
            if (found != null) {
                return found;
            }
            // 回退：按 javaColumnName 匹配
            return this.findChildByRelationName(fullTreeRoot, relationColumnInfo.getJavaColumnName());
        }

        /**
         * 递归搜索完整树中 columnInfo 与指定 relationColumnInfo 为同一对象引用的节点
         */
        private EntityRelationTree findChildByRelationIdentity(EntityRelationTree node, RelationColumnInfo relationColumnInfo) {
            if (node == null) {
                return null;
            }
            if (node.getColumnInfo() == relationColumnInfo) {
                return node;
            }
            if (node.getComposites() != null) {
                for (EntityRelationTree child : node.getComposites()) {
                    EntityRelationTree found = this.findChildByRelationIdentity(child, relationColumnInfo);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        /**
         * 递归搜索完整树中 columnInfo.javaColumnName 与指定字段名匹配的节点
         */
        private EntityRelationTree findChildByRelationName(EntityRelationTree node, String javaColumnName) {
            if (node == null || javaColumnName == null) {
                return null;
            }
            if (node.getColumnInfo() != null && javaColumnName.equals(node.getColumnInfo().getJavaColumnName())) {
                return node;
            }
            if (node.getComposites() != null) {
                for (EntityRelationTree child : node.getComposites()) {
                    EntityRelationTree found = this.findChildByRelationName(child, javaColumnName);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        /**
         * 判断字段是否为复合类型（非基本类型、非包装类、非 String、非集合、非 Map、非 Number/Date 等标准类型）
         *
         * @param columnInfo 字段信息
         * @return true 表示为复合类型
         */
        private boolean isCompositeField(ColumnInfo columnInfo) {
            Class<?> javaType = columnInfo.getJavaType();
            if (javaType == null) {
                return false;
            }
            // 基本类型 / 包装类 / String / Number / Date / java.time
            if (javaType.isPrimitive() || Number.class.isAssignableFrom(javaType)
                    || CharSequence.class.isAssignableFrom(javaType) || Boolean.class == javaType
                    || Date.class.isAssignableFrom(javaType) || javaType.getName().startsWith("java.time")) {
                return false;
            }
            // 枚举
            if (javaType.isEnum()) {
                return false;
            }
            // 集合 / Map / 数组
            if (Collection.class.isAssignableFrom(javaType) || Map.class.isAssignableFrom(javaType) || javaType.isArray()) {
                return false;
            }
            return true;
        }

        /**
         * 沿路径链从 startIndex 开始搜索关系字段
         *
         * @param pathChain  路径链
         * @param fieldName  字段名
         * @param startIndex 起始索引
         * @return 匹配结果，未找到返回 null
         */
        private MatchResult searchDownPath(List<JoinPathNode> pathChain, String fieldName, int startIndex) {
            for (int i = startIndex; i < pathChain.size(); i++) {
                EntityInfo entityInfo = pathChain.get(i).entityInfo;
                List<RelationColumnInfo> relationColumnInfoList = entityInfo.getRelationColumnInfoList();
                for (RelationColumnInfo relationColumnInfo : relationColumnInfoList) {
                    if (fieldName.equals(relationColumnInfo.getJavaColumnName())) {
                        // 验证目标实体在路径链的下一节点存在且类型匹配
                        if (i + 1 < pathChain.size()) {
                            Class<?> targetEntityClass = relationColumnInfo.getJavaType();
                            Class<?> nextNodeEntityClass = pathChain.get(i + 1).entityInfo.getClazz();
                            if (targetEntityClass != null && targetEntityClass.equals(nextNodeEntityClass)) {
                                return new MatchResult(entityInfo, relationColumnInfo, i + 1);
                            }
                        }
                    }
                }
            }
            return null;
        }

        /**
         * 路径链节点，持有实体信息和到下一节点的关联关系
         */
        private static class JoinPathNode {
            final EntityInfo entityInfo;
            final RelationColumnInfo relationColumnInfo;

            JoinPathNode(EntityInfo entityInfo, RelationColumnInfo relationColumnInfo) {
                this.entityInfo = entityInfo;
                this.relationColumnInfo = relationColumnInfo;
            }
        }

        /**
         * 沿路径链搜索的匹配结果
         */
        private static class MatchResult {
            final EntityInfo sourceEntityInfo;
            final RelationColumnInfo relationColumnInfo;
            final int pathIndex;

            MatchResult(EntityInfo sourceEntityInfo, RelationColumnInfo relationColumnInfo, int pathIndex) {
                this.sourceEntityInfo = sourceEntityInfo;
                this.relationColumnInfo = relationColumnInfo;
                this.pathIndex = pathIndex;
            }
        }

        /**
         * 复合字段条目，记录 DTO 字段信息和匹配结果
         */
        private static class CompositeFieldEntry {
            final ColumnInfo dtoColumnInfo;
            final MatchResult matchResult;

            CompositeFieldEntry(ColumnInfo dtoColumnInfo, MatchResult matchResult) {
                this.dtoColumnInfo = dtoColumnInfo;
                this.matchResult = matchResult;
            }
        }
    }

    private EntityRelationTree buildEntityRelationTree(ColumnInfo columnInfo, EntityInfo entityInfo, EntityRelationDependencyTree entityRelationDependencyTree, int level, int index) {
        if (entityInfo == null) {
            return null;
        }
        String tableNameAlias = this.tableColumnNameAlias.process(level, index, entityInfo);
        MiddleEntityInfo middleEntityInfo = this.buildMiddleEntityInfo(columnInfo);
        EntityRelationTree entityRelationTree = new EntityRelationTree();
        entityRelationTree.setLevel(level);
        entityRelationTree.setIndex(index);
        entityRelationTree.setTableNameAlias(tableNameAlias);
        entityRelationTree.setColumnInfo(columnInfo);
        entityRelationTree.setMiddleEntityInfo(middleEntityInfo);
        entityRelationTree.setEntityInfo(entityInfo);

        List<RelationColumnInfo> relationColumnInfoList = entityInfo.getRelationColumnInfoList();
        for (int i = 0; i < relationColumnInfoList.size(); i++) {
            RelationColumnInfo relationColumnInfo = relationColumnInfoList.get(i);
            Class<?> relationColumnJavaType = relationColumnInfo.getJavaType();
            Boolean isCycleRef = entityRelationDependencyTree.cycleRefCheck(relationColumnJavaType);
            if (isCycleRef) {
                String pathString = StringUtils.join(entityRelationDependencyTree.getPath(), "->");
                LOGGER.debug("{}->{}存在循环引用，消除循环引用防止无限循环", pathString, relationColumnJavaType);
                continue;
            }
            Boolean isSelfRef = entityRelationDependencyTree.selfRefCheck(relationColumnJavaType);
            if (isSelfRef && relationColumnInfo.getRelationType() == RelationType.MANY_TO_ONE) {
                LOGGER.debug("自引用忽略父引用");
                continue;
            }
            EntityRelationDependencyTree childrenResultMapDependencyTree = EntityRelationDependencyTree.build(entityRelationDependencyTree, relationColumnJavaType);
            EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(relationColumnJavaType);
            EntityRelationTree subEntityRelationTree = this.buildEntityRelationTree(relationColumnInfo, relationColumnEntityInfo, childrenResultMapDependencyTree, level + 1, i + 1);
            if (subEntityRelationTree != null) {
                entityRelationTree.addComposites(subEntityRelationTree);
            }
        }
        return entityRelationTree;
    }

    private MiddleEntityInfo buildMiddleEntityInfo(ColumnInfo columnInfo) {
        if (columnInfo == null) {
            return null;
        }
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
        RelationType relationType = relationColumnInfo.getRelationType();
        if (relationType != RelationType.MANY_TO_MANY) {
            return null;
        }

        String tableName;
        List<ColumnInfo> leftColumnInfoList = new ArrayList();
        List<ColumnInfo> rightColumnInfoList = new ArrayList();
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        if (mappedByRelationColumnInfo != null) {
            JoinTable joinTable = mappedByRelationColumnInfo.getJoinTable();
            tableName = joinTable.name();

            List<ForeignKeyInfo> foreignKeyColumnInfoList = mappedByRelationColumnInfo.getForeignKeyInfoList();
            for (ForeignKeyInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                rightColumnInfoList.add(foreignKeyColumnInfo.getColumnInfo());
            }

            List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = mappedByRelationColumnInfo.getInverseForeignKeyInfoList();
            for (ForeignKeyInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                leftColumnInfoList.add(inverseForeignKeyColumnInfo.getColumnInfo());
            }
        } else {
            JoinTable joinTable = relationColumnInfo.getJoinTable();
            tableName = joinTable.name();

            List<ForeignKeyInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyInfoList();
            for (ForeignKeyInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                leftColumnInfoList.add(foreignKeyColumnInfo.getColumnInfo());
            }

            List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
            for (ForeignKeyInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                rightColumnInfoList.add(inverseForeignKeyColumnInfo.getColumnInfo());
            }
        }

        MiddleEntityInfo middleEntityInfo = new MiddleEntityInfo();
        middleEntityInfo.setTableName(tableName);
        middleEntityInfo.setLeftColumnInfoList(leftColumnInfoList);
        middleEntityInfo.setRightColumnInfoList(rightColumnInfoList);
        return middleEntityInfo;
    }

    /**
     * 表字段别名
     *
     * @author ccxuef
     * @date 2025/8/9 15:11
     */
    private static class TableColumnNameAlias {

        /**
         * 处理表字段别名
         *
         * @param level
         * @param entityInfo
         */
        private String process(int level, int index, EntityInfo entityInfo) {
            String tableNameAlias = String.format("%s_%s_%s", entityInfo.getTableName(), level, index);
            List<ColumnInfo> tableColumnInfoList = entityInfo.getTableColumnInfoList();
            for (int i = 0; i < tableColumnInfoList.size(); i++) {
                ColumnInfo columnInfo = tableColumnInfoList.get(i);
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                    IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                    List<ColumnInfo> columnInfoComposites = idColumnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        String dbColumnName = columnInfo.getDbColumnName();
                        String dbColumnNameAlias = this.buildTableColumnNameAlias(dbColumnName, i);
                        columnInfo.setDbColumnNameAlias(dbColumnNameAlias);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            String dbColumnName = columnInfoComposite.getDbColumnName();
                            String dbColumnNameAlias = this.buildTableColumnNameAlias(dbColumnName, i);
                            columnInfoComposite.setDbColumnNameAlias(dbColumnNameAlias);
                        }
                    }
                }
                if (TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
                    String dbColumnName = columnInfo.getDbColumnName();
                    String dbColumnNameAlias = this.buildTableColumnNameAlias(dbColumnName, i);
                    columnInfo.setDbColumnNameAlias(dbColumnNameAlias);
                }
                if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                    ManyToMany manyToMany = relationColumnInfo.getManyToMany();
                    if (manyToMany == null) {
                        List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
                        this.processForeignKeyColumnAlias(inverseForeignKeyColumnInfoList, i);
                    } else {
                        List<ForeignKeyInfo> foreignKeyColumnInfoList = relationColumnInfo.getForeignKeyInfoList();
                        this.processForeignKeyColumnAlias(foreignKeyColumnInfoList, i);

                        List<ForeignKeyInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyInfoList();
                        this.processForeignKeyColumnAlias(inverseForeignKeyColumnInfoList, i);
                    }
                }
            }
            return tableNameAlias;
        }

        /**
         * 处理外键列别名
         *
         * @param foreignKeyColumnInfoList
         * @param columnIndex
         */
        private void processForeignKeyColumnAlias(List<ForeignKeyInfo> foreignKeyColumnInfoList, int columnIndex) {
            for (ForeignKeyInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                ColumnInfo columnInfo = foreignKeyColumnInfo.getColumnInfo();
                String dbColumnName = columnInfo.getDbColumnName();
                String dbColumnNameAlias = this.buildTableColumnNameAlias(dbColumnName, columnIndex);
                columnInfo.setDbColumnNameAlias(dbColumnNameAlias);
            }
        }

        private String buildTableColumnNameAlias(String name, int columnIndex) {
            int randomIndex = RandomUtils.nextInt(0, 9);
            return String.format("%s_%s_%s", columnIndex, randomIndex, name);
        }
    }

    /**
     * 循环依赖检测器，用于消除循环引用
     * <code>
     * aaaa:
     * aaaaa:
     * aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     * aaaaa:
     * aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     * bbbbb:
     * aaaaa: 消除循环依赖
     * bbbbb: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     * ccccc:
     * aaaaa: 消除循环依赖
     * bbbbb:
     * </code>
     *
     * @author ccxuef
     * @date 2025/8/8 16:56
     */
    private static class EntityRelationDependencyTree {

        /**
         * 自依赖检测最大深度
         */
        private static final int MAX_DEPTH = 3;
        /**
         * 父类
         */
        private Class<?> parent;
        /**
         * 当前类
         */
        private Class<?> clazz;
        /**
         * 当前深度【只有自引用才计算深度】
         */
        private int depth = 1;
        /**
         * 树路径
         */
        private Set<Class<?>> path = new LinkedHashSet<>(10);

        private EntityRelationDependencyTree(EntityRelationDependencyTree entityRelationDependencyTree, Class<?> clazz) {
            this.clazz = clazz;
            if (entityRelationDependencyTree != null) {
                this.parent = entityRelationDependencyTree.getClazz();
                this.path.addAll(entityRelationDependencyTree.getPath());
                if (this.parent == clazz) {
                    this.depth = entityRelationDependencyTree.depth + 1;
                }
            }
            this.path.add(clazz);
        }

        public Class<?> getParent() {
            return parent;
        }

        public void setParent(Class<?> parent) {
            this.parent = parent;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Set<Class<?>> getPath() {
            return path;
        }

        public void setPath(Set<Class<?>> path) {
            this.path = path;
        }

        /**
         * 自循环引用是可以允许的
         *
         * @param subClazz
         * @return
         */
        public Boolean cycleRefCheck(Class<?> subClazz) {
            if (this.clazz == subClazz) {
                return this.depth >= MAX_DEPTH;
            }
            return this.path.contains(subClazz);
        }

        /**
         * 自引用检查
         *
         * @param subClazz
         * @return
         */
        public Boolean selfRefCheck(Class<?> subClazz) {
            return this.clazz == subClazz;
        }

        public static EntityRelationDependencyTree build(EntityRelationDependencyTree entityRelationDependencyTree, Class<?> clazz) {
            return new EntityRelationDependencyTree(entityRelationDependencyTree, clazz);
        }
    }
}
