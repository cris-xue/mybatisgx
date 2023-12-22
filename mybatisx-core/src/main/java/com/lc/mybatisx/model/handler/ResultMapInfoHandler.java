package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ResultMapInfo;

import java.util.List;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public ResultMapInfo execute(Class<?> entityClass) {
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(getResultMap(entityClass));
        resultMapInfo.setType(entityClass.getTypeName());
        resultMapInfo.setColumnInfoList(columnInfoList);

        return resultMapInfo;
    }

}
