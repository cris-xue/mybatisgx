package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

import java.util.List;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public static void execute(MapperBuilderAssistant builderAssistant, List<XNode> resultMapXNode) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);

        MapperInfo mapperInfo = mapperInfoHandler.execute(daoInterface);
        Class<?> entityClass = mapperInfo.getEntityClass();

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(entityClass.getSimpleName() + "FullColumnResultMap");
        resultMapInfo.setType(entityClass.getTypeName());


        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);

        // resultMapXNode.add();
    }

}
