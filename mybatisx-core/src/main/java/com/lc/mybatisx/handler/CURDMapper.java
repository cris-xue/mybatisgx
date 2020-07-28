package com.lc.mybatisx.handler;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 12:57
 */
public class CURDMapper {

    public static List<XNode> getNodeList(MapperBuilderAssistant builderAssistant, String namespace) {
        InsertMapperHandler insertMapperHandler = new InsertMapperHandler(builderAssistant, namespace);
        List<XNode> insertList = insertMapperHandler.readTemplate();

        // QueryMapperHandler queryMapperHandler = new QueryMapperHandler(builderAssistant, namespace);
        // List<XNode> queryList = queryMapperHandler.readTemplate();

        UpdateMapperHandler updateMapperHandler = new UpdateMapperHandler(builderAssistant, namespace);
        List<XNode> updateList = updateMapperHandler.readTemplate();

        // DeleteMapperHandler deleteMapperHandler = new DeleteMapperHandler(builderAssistant, namespace);
        // List<XNode> deleteList = deleteMapperHandler.readTemplate();

        List<XNode> curdList = new ArrayList<>();
        curdList.addAll(insertList);
        // curdList.addAll(queryList);
        curdList.addAll(updateList);
        // curdList.addAll(deleteList);
        return curdList;
    }

}

