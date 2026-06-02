package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.RelationColumnInfo;

import java.util.List;

/**
 * JOIN关系校验器
 * <p>
 * 校验JOIN实体与主实体之间是否存在关联关系
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class JoinRelationChecker implements MgxqlChecker {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void check(MgxqlStatement statement, CheckerContext context) {
        FromClause fromClause = statement.getFromClause();
        if (fromClause == null || fromClause.getJoinEntities() == null || fromClause.getJoinEntities().isEmpty()) {
            return;
        }

        EntityInfo primaryEntityInfo = context.getPrimaryEntityInfo();
        if (primaryEntityInfo == null) {
            return;
        }

        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            EntityInfo joinEntityInfo = context.getEntityInfoByAlias(joinEntity.getAlias());
            if (joinEntityInfo == null) {
                // 实体不存在时由EntityChecker处理
                continue;
            }

            if (!this.hasRelation(primaryEntityInfo, joinEntityInfo)) {
                context.addError(String.format(
                        "实体 '%s' 与实体 '%s' 之间不存在关联关系，无法执行 LEFT JOIN",
                        primaryEntityInfo.getClazz().getSimpleName(), joinEntityInfo.getClazz().getSimpleName()));
            }
        }
    }

    /**
     * 检查两个实体之间是否存在关联关系（双向检查）
     */
    private boolean hasRelation(EntityInfo entity1, EntityInfo entity2) {
        return this.checkRelationDirection(entity1, entity2) || this.checkRelationDirection(entity2, entity1);
    }

    /**
     * 检查entity1是否有关联字段指向entity2
     */
    private boolean checkRelationDirection(EntityInfo entity1, EntityInfo entity2) {
        List<RelationColumnInfo> relations = entity1.getRelationColumnInfoList();
        if (relations == null || relations.isEmpty()) {
            return false;
        }
        for (RelationColumnInfo relation : relations) {
            if (relation.getJavaType() != null && relation.getJavaType().equals(entity2.getClazz())) {
                return true;
            }
        }
        return false;
    }
}
