package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.model.EntityInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FROM 实体别名上下文：仅负责 MGXQL 别名 → {@link FromEntity}/{@link JoinEntity} 的映射，
 * 用于 SELECT 列展开时直接获取 FROM 实体的 {@link EntityInfo}。
 * <p>
 * 与 {@link AliasContext}（服务于 JOIN/ON 渲染）职责分离：SELECT 列展开不依赖投影树节点匹配。
 */
public class FromAliasContext {

    private final Map<String, FromEntity> fromEntityMap;
    private final FromClause fromClause;

    private FromAliasContext(Map<String, FromEntity> fromEntityMap, FromClause fromClause) {
        this.fromEntityMap = fromEntityMap;
        this.fromClause = fromClause;
    }

    public static FromAliasContext build(FromClause fromClause) {
        Map<String, FromEntity> map = new LinkedHashMap<>();
        if (fromClause == null) {
            return new FromAliasContext(map, fromClause);
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null && StringUtils.isNotBlank(primaryEntity.getAlias())) {
            map.put(primaryEntity.getAlias(), primaryEntity);
        }
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                if (StringUtils.isNotBlank(joinEntity.getAlias())) {
                    map.put(joinEntity.getAlias(), joinEntity);
                }
            }
        }
        return new FromAliasContext(map, fromClause);
    }

    public FromEntity getFromEntity(String alias) {
        return this.fromEntityMap.get(alias);
    }

    public FromClause getFromClause() {
        return this.fromClause;
    }
}