package com.mybatisgx.dsl.mgxql.model;

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
}
