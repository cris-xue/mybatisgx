package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL语句模型，作为语法解析的中间表示，最终会转换成MyBatis XML
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class ModifyStatement extends MgxqlStatement {

    private ModifyEntity modifyEntity;

    public ModifyEntity getModifyEntity() {
        return modifyEntity;
    }

    public void setModifyEntity(ModifyEntity modifyEntity) {
        this.modifyEntity = modifyEntity;
    }
}
