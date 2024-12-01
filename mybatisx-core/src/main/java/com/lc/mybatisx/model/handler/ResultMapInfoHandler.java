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

        String methodName = methodInfo.getMethodName();
        Class<?> resultClass = methodReturnInfo.getType();
        String resultMapId = getResultMapId(resultClass, methodName);
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(resultClass);
        List<ManyToManyInfo> manyToManyInfoList = columnInfoHandler.getAssociationTableInfoList(resultClass);

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(resultMapId);
        resultMapInfo.setType(resultClass.getTypeName());
        resultMapInfo.setColumnInfoList(columnInfoList);
        resultMapInfo.setAssociationTableInfoList(manyToManyInfoList);

        return resultMapInfo;
    }

    protected String getResultMapId(Class<?> entityClass, String methodName) {
        return String.format("%s%sColumnResultMap", methodName, entityClass.getSimpleName());
    }

}
