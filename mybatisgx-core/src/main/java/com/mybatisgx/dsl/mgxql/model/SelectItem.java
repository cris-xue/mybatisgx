package com.mybatisgx.dsl.mgxql.model;

import java.util.List;

/**
 * MGXQL查询项模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SelectItem {

    /**
     * 实体名，如 User
     */
    private String entityName;
    /**
     *
     */
    private List<SelectItemTerm> selectItemTerms;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<SelectItemTerm> getSelectItemTerms() {
        return selectItemTerms;
    }

    public void setSelectItemTerms(List<SelectItemTerm> selectItemTerms) {
        this.selectItemTerms = selectItemTerms;
    }
}
