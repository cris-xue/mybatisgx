package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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

        List<ColumnInfo> relationColumnInfoList = entityInfo.getRelationColumnInfoList();
        for (ColumnInfo relationColumnInfo : relationColumnInfoList) {
            Class<?> javaType = relationColumnInfo.getJavaType();
            Boolean isCycleRef = entityRelationDependencyTree.cycleRefCheck(javaType);
            if (isCycleRef) {
                String pathString = StringUtils.join(entityRelationDependencyTree.getPath(), "->");
                LOGGER.info("{}->{}存在循环引用，消除循环引用防止无限循环", pathString, javaType);
                continue;
            }
            EntityRelationDependencyTree childrenResultMapDependencyTree = new EntityRelationDependencyTree(entityRelationDependencyTree, javaType);
            EntityInfo relationColumnEntityInfo = EntityInfoContextHolder.get(javaType);
            EntityRelationInfo subEntityRelationInfo = this.processRelationColumnInfo(level + 1, childrenResultMapDependencyTree, relationColumnEntityInfo, relationColumnInfo);
            entityRelationInfo.addEntityRelation(subEntityRelationInfo);
        }
        return entityRelationInfo;
    }
}
