package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 实体关系信息处理
 *
 * @author ccxuef
 * @date 2025/7/27 14:31
 */
public class EntityRelationInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRelationInfoHandler.class);

    public EntityRelationInfo execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String action = methodInfo.getAction();
        if (Arrays.asList("insert", "delete", "update").contains(action)) {
            return null;
        }

        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Class<?> resultClass = methodReturnInfo.getType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(resultClass);

        // 解决循环引用问题
        EntityRelationDependencyTree entityRelationDependencyTree = new EntityRelationDependencyTree(null, resultClass);
        EntityRelationInfo entityRelationInfo = this.processRelationColumnInfo(1, entityRelationDependencyTree, entityInfo, null);

        mapperInfo.addEntityRelationInfo(entityRelationInfo);
        return entityRelationInfo;
    }

    private EntityRelationInfo processRelationColumnInfo(
            int level, EntityRelationDependencyTree entityRelationDependencyTree, EntityInfo entityInfo, ColumnInfo columnInfo
    ) {
        if (entityInfo == null) {
            return null;
        }
        EntityRelationInfo entityRelationInfo = new EntityRelationInfo();
        entityRelationInfo.setLevel(level);
        entityRelationInfo.setColumnInfo(columnInfo);
        entityRelationInfo.setEntityInfo(entityInfo);
        this.processTableColumnAliasName(level, entityInfo);

        List<ColumnInfo> relationColumnInfoList = entityInfo.getRelationColumnInfoList();
        for (ColumnInfo relationColumnInfo : relationColumnInfoList) {
            Class<?> javaType = relationColumnInfo.getJavaType();
            Boolean isCycleRef = entityRelationDependencyTree.cycleRefCheck(javaType);
            if (isCycleRef) {
                String pathString = StringUtils.join(entityRelationDependencyTree.getPath(), "->");
                LOGGER.debug("{}->{}存在循环引用，消除循环引用防止无限循环", pathString, javaType);
                continue;
            }
            EntityRelationDependencyTree childrenResultMapDependencyTree = new EntityRelationDependencyTree(entityRelationDependencyTree, javaType);
            EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);
            EntityRelationInfo subEntityRelationInfo = this.processRelationColumnInfo(level + 1, childrenResultMapDependencyTree, relationColumnEntityInfo, relationColumnInfo);
            entityRelationInfo.addEntityRelation(subEntityRelationInfo);
        }
        return entityRelationInfo;
    }

    /**
     * 处理表字段别名
     * @param level
     * @param entityInfo
     */
    private void processTableColumnAliasName(int level, EntityInfo entityInfo) {
        String tableName = entityInfo.getTableName();
        List<ColumnInfo> tableColumnInfoList = entityInfo.getTableColumnInfoList();
        for (int i = 0; i < tableColumnInfoList.size(); i++) {
            ColumnInfo columnInfo = tableColumnInfoList.get(i);
            ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
            if (columnRelationInfo == null) {
                String dbColumnAliasName = String.format("%s_%s_%s_%s_%s", tableName, columnInfo.getDbColumnName(), level, i, RandomUtils.nextInt(0, 9));
                columnInfo.setDbColumnAliasName(dbColumnAliasName);
            } else {
                ManyToMany manyToMany = columnRelationInfo.getManyToMany();
                if (manyToMany == null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        String name = inverseForeignKeyColumnInfo.getName();
                        String nameAlias = String.format("%s_%s_%s_%s_%s", tableName, name, level, i, RandomUtils.nextInt(0, 9));
                        inverseForeignKeyColumnInfo.setNameAlias(nameAlias);
                    }
                } else {
                    List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnRelationInfo.getForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                        String name = foreignKeyColumnInfo.getName();
                        String nameAlias = String.format("%s_%s_%s_%s_%s", tableName, name, level, i, RandomUtils.nextInt(0, 9));
                        foreignKeyColumnInfo.setNameAlias(nameAlias);
                    }
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        String name = inverseForeignKeyColumnInfo.getName();
                        String nameAlias = String.format("%s_%s_%s_%s_%s", tableName, name, level, i, RandomUtils.nextInt(0, 9));
                        inverseForeignKeyColumnInfo.setNameAlias(nameAlias);
                    }
                }
            }
        }
    }

    /**
     * 循环依赖检测器，用于消除循环引用
     * <code>
     * aaaa:
     *   aaaaa:
     *     aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   aaaaa:
     *     aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   bbbbb:
     *     aaaaa: 消除循环依赖
     *     bbbbb: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   ccccc:
     *     aaaaa: 消除循环依赖
     *     bbbbb:
     * </code>
     * @author ccxuef
     * @date 2025/8/8 16:56
     */
    class EntityRelationDependencyTree {

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

        public EntityRelationDependencyTree(EntityRelationDependencyTree entityRelationDependencyTree, Class<?> clazz) {
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

        public Boolean cycleRefCheck(Class<?> subClazz) {
            // 自循环引用是可以允许的
            if (this.clazz == subClazz) {
                return this.depth > 5;
            }
            return this.path.contains(subClazz);
        }
    }
}
