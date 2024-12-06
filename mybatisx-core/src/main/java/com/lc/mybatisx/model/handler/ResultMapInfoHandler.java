package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.*;

import java.util.Arrays;
import java.util.List;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public ResultMapInfo execute(MethodInfo methodInfo, MethodReturnInfo methodReturnInfo) {
        String action = methodInfo.getAction();
        if (Arrays.asList("insert", "delete", "update").contains(action)) {
            return null;
        }

        Class<?> resultClass = methodReturnInfo.getType();
        String resultMapId = getResultMapId(resultClass);
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(resultClass);
        // List<AssociationTableInfo> associationTableInfoList = columnInfoHandler.getAssociationTableInfoList(resultClass);

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(resultMapId);
        resultMapInfo.setType(resultClass);
        resultMapInfo.setColumnInfoList(columnInfoList);
        // resultMapInfo.setAssociationTableInfoList(associationTableInfoList);

        return resultMapInfo;
    }

    protected String getResultMapId(Class<?> entityClass) {
        return String.format("%sResultMap", entityClass.getTypeName().replaceAll("\\.", "_"));
    }

}
