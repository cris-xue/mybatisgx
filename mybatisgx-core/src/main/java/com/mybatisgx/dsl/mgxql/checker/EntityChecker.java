package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.EntityInfo;

/**
 * 实体存在性校验器
 * <p>
 * 校验FROM子句中的实体名是否已注册，JOIN实体是否存在，并构建别名映射
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class EntityChecker implements MgxqlChecker {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void check(MgxqlStatement statement, CheckerContext context) {
        FromClause fromClause = statement.getFromClause();
        if (fromClause == null) {
            // DELETE/UPDATE 语句无 FromClause，直接确认主实体可用
            if (context.getPrimaryEntityInfo() == null) {
                context.addError("DELETE/UPDATE 语句缺少实体信息");
            }
            return;
        }

        // 校验主实体
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null) {
            EntityInfo entityInfo = this.resolveEntityInfo(primaryEntity.getEntityName(), context);
            if (entityInfo != null && primaryEntity.getAlias() != null) {
                context.registerAlias(primaryEntity.getAlias(), entityInfo);
            }
        }

        // 校验JOIN实体
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                EntityInfo entityInfo = this.resolveEntityInfo(joinEntity.getEntityName(), context);
                if (entityInfo != null && joinEntity.getAlias() != null) {
                    context.registerAlias(joinEntity.getAlias(), entityInfo);
                }
            }
        }
    }

    /**
     * 解析实体名对应的EntityInfo
     */
    private EntityInfo resolveEntityInfo(String entityName, CheckerContext context) {
        // 先从已注册的映射中查找
        EntityInfo entityInfo = context.getEntityInfoByName(entityName);
        if (entityInfo != null) {
            return entityInfo;
        }

        // 从全局EntityInfoContextHolder中查找
        for (Class<?> entityClass : EntityInfoContextHolder.getEntityClassList()) {
            if (entityClass.getSimpleName().equals(entityName)) {
                entityInfo = EntityInfoContextHolder.get(entityClass);
                context.registerEntityName(entityName, entityInfo);
                return entityInfo;
            }
        }

        // 检查主实体（clazzName为全限定名，需用getSimpleName比较）
        EntityInfo primaryEntityInfo = context.getPrimaryEntityInfo();
        if (primaryEntityInfo != null) {
            Class<?> primaryClazz = primaryEntityInfo.getClazz();
            if (primaryClazz != null && primaryClazz.getSimpleName().equals(entityName)) {
                context.registerEntityName(entityName, primaryEntityInfo);
                return primaryEntityInfo;
            }
        }

        context.addError(String.format("实体 '%s' 不存在，请确认实体类是否已注册", entityName));
        return null;
    }
}
