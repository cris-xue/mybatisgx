package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

public class LogicDeleteGenerateValueHandler implements GenerateValueHandler<Integer> {

    @Override
    public Integer next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        LogicDelete logicDelete = columnInfo.getLogicDelete();
        if (logicDelete == null) {
            return (Integer) originalValue;
        }
        if (sqlCommandType == SqlCommandType.INSERT) {
            return Integer.valueOf(logicDelete.show());
        } else if (sqlCommandType == SqlCommandType.DELETE) {
            return Integer.valueOf(logicDelete.hide());
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            return Integer.valueOf(logicDelete.show());
        } else if (sqlCommandType == SqlCommandType.SELECT) {
            return Integer.valueOf(logicDelete.show());
        }
        throw new UnsupportedOperationException("暂未实现");
    }

}
