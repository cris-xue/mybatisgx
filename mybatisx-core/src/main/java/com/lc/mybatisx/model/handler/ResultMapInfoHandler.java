package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String action = methodInfo.getAction();
        if (Arrays.asList("insert", "delete", "update").contains(action)) {
            return null;
        }

        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Class<?> resultClass = methodReturnInfo.getType();
        String resultMapId = getResultMapId(resultClass);
        EntityInfo entityInfo = EntityInfoContextHolder.get(resultClass);

        List<ColumnInfo> tableColumnInfoList;
        List<ColumnInfo> associationColumnInfoList = null;
        if (entityInfo != null) {
            tableColumnInfoList = entityInfo.getTableColumnInfoList();
            associationColumnInfoList = entityInfo.getAssociationColumnInfoList();
        } else {
            tableColumnInfoList = columnInfoHandler.getColumnInfoList(resultClass);
        }

        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultClass);
        if (resultMapInfo != null) {
            return resultMapInfo.getId();
        }
        Set<Class<?>> associationDependencySet = new HashSet();
        associationDependencySet.add(resultClass);
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = this.processAssociationColumnInfoList(associationDependencySet, associationColumnInfoList);
        resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(resultMapId);
        resultMapInfo.setType(resultClass);
        resultMapInfo.setColumnInfoList(tableColumnInfoList);
        resultMapInfo.setResultMapAssociationInfoList(resultMapAssociationInfoList);
        mapperInfo.setResultMapInfo(resultMapInfo);
        return resultMapInfo.getId();
    }

    private List<ResultMapAssociationInfo> processAssociationColumnInfoList(Set<Class<?>> associationDependencySet, List<ColumnInfo> associationColumnInfoList) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();
        associationColumnInfoList.forEach(associationColumnInfo -> {
            Class<?> javaType = associationColumnInfo.getJavaType();
            if (associationDependencySet.contains(javaType)) {
                return;
            }
            associationDependencySet.add(javaType);
            EntityInfo entityInfo = EntityInfoContextHolder.get(javaType);

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(javaType, associationColumnInfo.getContainerType()));
            resultMapAssociationInfo.setResultMapId(this.getResultMapId(javaType));
            resultMapAssociationInfo.setType(javaType);
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(associationColumnInfo);

            List<ColumnInfo> subAssociationColumnInfoList = entityInfo.getAssociationColumnInfoList();
            if (ObjectUtils.isNotEmpty(subAssociationColumnInfoList)) {
                List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.processAssociationColumnInfoList(associationDependencySet, subAssociationColumnInfoList);
                resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
            }
            resultMapAssociationInfoList.add(resultMapAssociationInfo);
        });
        return resultMapAssociationInfoList;
    }

    protected String getResultMapId(Class<?> entityClass) {
        return String.format("%sResultMap", entityClass.getTypeName().replaceAll("\\.", "_"));
    }

    private String getSelect(Class<?> entityClass, Class<?> containerType) {
        return String.format("find%s%s", entityClass.getSimpleName(), containerType != null ? containerType.getSimpleName() : "");
    }
}
