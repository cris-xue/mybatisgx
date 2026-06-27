package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.model.RelationColumnInfo;

/**
 * MGXQL JOIN实体模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class JoinEntity extends FromEntity {

    /**
     * JOIN类型：LEFT等
     */
    private JoinType joinType;

    private String onLeftAlias;

    private String onRightAlias;
    /**
     * 语义校验阶段绑定的关联关系元数据，ON 条件由其外键信息自动推导
     */
    private RelationColumnInfo relationColumnInfo;

    public JoinEntity() {
    }

    public JoinEntity(String entityName, String alias, JoinType joinType) {
        super(entityName, alias);
        this.joinType = joinType;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getOnLeftAlias() {
        return onLeftAlias;
    }

    public void setOnLeftAlias(String onLeftAlias) {
        this.onLeftAlias = onLeftAlias;
    }

    public String getOnRightAlias() {
        return onRightAlias;
    }

    public void setOnRightAlias(String onRightAlias) {
        this.onRightAlias = onRightAlias;
    }

    public RelationColumnInfo getRelationColumnInfo() {
        return relationColumnInfo;
    }

    public void setRelationColumnInfo(RelationColumnInfo relationColumnInfo) {
        this.relationColumnInfo = relationColumnInfo;
    }
}
