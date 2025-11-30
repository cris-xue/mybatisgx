package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.JoinTable;
import com.mybatisgx.annotation.ManyToMany;
import com.mybatisgx.context.EntityInfoContextHolder;
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

    private TableColumnNameAlias tableColumnNameAlias = new TableColumnNameAlias();

    public EntityRelationTree execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        SqlCommandType sqlCommandType = methodInfo.getSqlCommandType();
        if (Arrays.asList(SqlCommandType.INSERT, SqlCommandType.DELETE, SqlCommandType.UPDATE).contains(sqlCommandType)) {
            return null;
        }

        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Class<?> resultClass = methodReturnInfo.getType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(resultClass);

        // 解决循环引用问题
        EntityRelationDependencyTree entityRelationDependencyTree = EntityRelationDependencyTree.build(null, resultClass);
        EntityRelationTree entityRelationTree = this.buildEntityRelationTree(null, entityInfo, entityRelationDependencyTree, 1, 1);

        mapperInfo.addEntityRelationTree(entityRelationTree);
        return entityRelationTree;
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
