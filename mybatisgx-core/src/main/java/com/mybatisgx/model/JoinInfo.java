package com.mybatisgx.model;

import com.mybatisgx.dsl.mgxql.model.JoinType;

/**
 * JOIN 节点，继承 EntityRefInfo 复用 entityName / alias 字段
 *
 * @author 薛承城
 * @date 2026/6/12
 */
public class JoinInfo extends EntityRefInfo {

    private JoinType joinType;

    private String onLeftAlias;

    private String onRightAlias;

    public JoinInfo() {
    }

    public JoinInfo(String entityName, String alias, JoinType joinType, String onLeftAlias, String onRightAlias) {
        super(entityName, alias);
        this.joinType = joinType;
        this.onLeftAlias = onLeftAlias;
        this.onRightAlias = onRightAlias;
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
}
