package com.mybatisgx.model.handler;

import com.mybatisgx.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MgxqlResultMapInfoHandler extends ResultMapInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MgxqlResultMapInfoHandler.class);

    private MgxqlEntityRelationResultMap relationResultMap = new MgxqlEntityRelationResultMap();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        SqlCommandType sqlCommandType = methodInfo.getSqlCommandType();
        if (Arrays.asList(SqlCommandType.INSERT, SqlCommandType.DELETE, SqlCommandType.UPDATE).contains(sqlCommandType)) {
            return null;
        }

        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Class<?> resultClass = methodReturnInfo.getType();
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(resultClass);

        // 处理结果集
        ResultMapContext resultMapContext = this.relationResultMap.buildResultMapInfo(entityRelationTree);
        ResultMapInfo resultMapInfo = resultMapContext.getResultMapInfo();
        List<ResultMapInfo> resultMapInfoList = resultMapContext.getResultMapInfoList();

        String resultMapId = resultMapInfo.getId();
        ResultMapInfo existResultMapInfo = mapperInfo.getResultMapInfo(resultMapId);
        if (existResultMapInfo != null) {
            return existResultMapInfo.getId();
        }

        mapperInfo.setResultMapInfoList(resultMapInfoList);
        return resultMapInfo.getId();
    }

    public static class MgxqlEntityRelationResultMap extends EntityRelationResultMap {

        /**
         * 构建结果集关联信息
         *
         * @param resultMapContext 结果集信息列表
         * @return
         */
        @Override
        protected List<ResultMapInfo> buildRelationResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree entityRelationTree) {
            List<ResultMapInfo> resultMapInfoList = new ArrayList();
            for (EntityRelationTree childEntityRelationTree : entityRelationTree.getComposites()) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childEntityRelationTree.getColumnInfo();

                // 构建一个内嵌查询结果集，内嵌查询结果集为空，不进行关联查询
                ResultMapInfo nestedResultMapInfo = this.buildNestedSelectResultMapInfo(entityRelationTree, childEntityRelationTree, relationColumnInfo);
                if (nestedResultMapInfo != null) {
                    resultMapInfoList.add(nestedResultMapInfo);
                }

                this.buildJoinResultMapInfo(resultMapContext, nestedResultMapInfo, childEntityRelationTree);
            }
            return resultMapInfoList;
        }

        private ResultMapInfo buildNestedSelectResultMapInfo(EntityRelationTree parentEntityRelationTree, EntityRelationTree childEntityRelationTree, RelationColumnInfo relationColumnInfo) {
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            this.copyProperties(childEntityRelationTree, resultMapInfo);
            resultMapInfo.setColumnInfo(childEntityRelationTree.getColumnInfo());
            resultMapInfo.setMiddleEntityInfo(childEntityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(childEntityRelationTree.getEntityInfo());
            return resultMapInfo;
        }

        private void buildJoinResultMapInfo(ResultMapContext resultMapContext, ResultMapInfo nestedResultMapInfo, EntityRelationTree childEntityRelationTree) {
            List<ResultMapInfo> childResultMapRelationInfoList = this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree);
            nestedResultMapInfo.setComposites(childResultMapRelationInfoList);
        }

        private void copyProperties(EntityRelationTree source, ResultMapInfo target) {
            target.setLevel(source.getLevel());
            target.setIndex(source.getIndex());
            target.setTableNameAlias(source.getTableNameAlias());
        }

        @Override
        protected String getResultMapId(List<ResultMapInfo> resultMapInfoList) {
            List<String> classNameList = resultMapInfoList.stream()
                    .map(resultMapInfo -> {
                        String clazzName = resultMapInfo.getEntityInfo().getClazzName().replaceAll("\\.", "_");
                        return StringUtils.join(Arrays.asList(clazzName, resultMapInfo.getLevel(), resultMapInfo.getIndex()), "_");
                    })
                    .collect(Collectors.toList());
            return StringUtils.join(classNameList, "_mgxql_join_") + "_ResultMap";
        }
    }
}
