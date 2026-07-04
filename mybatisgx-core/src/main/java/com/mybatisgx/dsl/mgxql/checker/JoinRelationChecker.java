package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
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
public class JoinRelationChecker implements MgxqlSemanticChecker {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof SelectStatement;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, CheckerContext context) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;

        FromClause fromClause = selectStatement.getFromClause();
        if (fromClause == null || fromClause.getJoinEntities() == null || fromClause.getJoinEntities().isEmpty()) {
            return;
        }

        EntityInfo primaryEntityInfo = context.getPrimaryEntityInfo();
        if (primaryEntityInfo == null) {
            return;
        }

        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            // ON 子句主动声明 join 双方别名：onLeftAlias 为已定义实体，onRightAlias 为当前 JOIN 实体
            String leftAlias = joinEntity.getOnLeftAlias();
            String rightAlias = joinEntity.getOnRightAlias();
            if (leftAlias == null || rightAlias == null) {
                // 未声明 ON 双方，由 OnAliasChecker 报错；此处跳过
                continue;
            }

            EntityInfo leftEntityInfo = context.getEntityInfoByAlias(leftAlias);
            EntityInfo rightEntityInfo = context.getEntityInfoByAlias(rightAlias);
            if (leftEntityInfo == null || rightEntityInfo == null) {
                // 实体不存在时由EntityChecker处理
                continue;
            }

            RelationColumnInfo relation = this.findRelation(leftEntityInfo, rightEntityInfo);
            if (relation == null) {
                context.addError(String.format(
                        "实体 '%s' 与实体 '%s' 之间不存在关联关系，无法执行 LEFT JOIN",
                        leftEntityInfo.getClazz().getSimpleName(), rightEntityInfo.getClazz().getSimpleName()));
            } else {
                joinEntity.setRelationColumnInfo(relation);
            }
        }
    }

    /**
     * 查找两个实体之间的关联关系（双向查找）
     */
    private RelationColumnInfo findRelation(EntityInfo entity1, EntityInfo entity2) {
        RelationColumnInfo relation = this.findRelationDirection(entity1, entity2);
        if (relation != null) {
            return relation;
        }
        return this.findRelationDirection(entity2, entity1);
    }

    /**
     * 查找 entity1 是否有关联字段指向 entity2
     */
    private RelationColumnInfo findRelationDirection(EntityInfo entity1, EntityInfo entity2) {
        List<RelationColumnInfo> relations = entity1.getRelationColumnInfoList();
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        for (RelationColumnInfo relation : relations) {
            if (relation.getJavaType() != null && relation.getJavaType().equals(entity2.getClazz())) {
                return relation;
            }
        }
        return null;
    }
}
