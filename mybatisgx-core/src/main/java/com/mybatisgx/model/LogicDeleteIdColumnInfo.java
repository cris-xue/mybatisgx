package com.mybatisgx.model;

import com.mybatisgx.annotation.LogicDeleteId;

/**
 * 逻辑删除id字段信息信息
 *
 * @author ccxuef
 * @date 2025/8/9 15:54
 */
public class LogicDeleteIdColumnInfo extends ColumnInfo {

    /**
     * 主键
     */
    private LogicDeleteId logicDeleteId;

    @Override
    public LogicDeleteId getLogicDeleteId() {
        return logicDeleteId;
    }

    @Override
    public void setLogicDeleteId(LogicDeleteId logicDeleteId) {
        this.logicDeleteId = logicDeleteId;
    }
}
