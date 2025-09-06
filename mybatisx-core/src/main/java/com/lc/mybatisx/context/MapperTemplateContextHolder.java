package com.lc.mybatisx.context;

import com.lc.mybatisx.model.MapperTemplateInfo;
import org.apache.ibatis.parsing.XNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapperTemplateContextHolder {

    private static final Map<String, MapperTemplateInfo> MAPPER_TEMPLATE_MAP = new ConcurrentHashMap();

    public static void set(MapperTemplateInfo mapperTemplateInfo) {
        MAPPER_TEMPLATE_MAP.put(mapperTemplateInfo.getNamespace(), mapperTemplateInfo);
    }

    public static List<String> getResultMapIdList(String namespace) {
        Set<String> resultMapIdSet = MAPPER_TEMPLATE_MAP.get(namespace).getResultMapTemplateMap().keySet();
        return new ArrayList(resultMapIdSet);
    }

    public static List<String> getStatementIdList(String namespace) {
        Set<String> singleTableStatementIdSet = MAPPER_TEMPLATE_MAP.get(namespace).getCurdTemplateMap().keySet();
        Set<String> relationTableSelectStatementIdSet = MAPPER_TEMPLATE_MAP.get(namespace).getAssociationSelectTemplateMap().keySet();
        List<String> totalActionIdList = new ArrayList(20);
        totalActionIdList.addAll(singleTableStatementIdSet);
        totalActionIdList.addAll(relationTableSelectStatementIdSet);
        return totalActionIdList;
    }

    public static XNode getResultMapTemplate(String namespace, String id) {
        MapperTemplateInfo mapperTemplateInfo = MAPPER_TEMPLATE_MAP.get(namespace);
        Map<String, XNode> idXNodeMap = mapperTemplateInfo.getResultMapTemplateMap();
        return idXNodeMap.get(id);
    }

    public static XNode getStatementTemplate(String namespace, String id) {
        MapperTemplateInfo mapperTemplateInfo = MAPPER_TEMPLATE_MAP.get(namespace);
        Map<String, XNode> idSimpleXNodeMap = mapperTemplateInfo.getCurdTemplateMap();
        Map<String, XNode> idSubSelectXNodeMap = mapperTemplateInfo.getAssociationSelectTemplateMap();
        idSimpleXNodeMap.putAll(idSubSelectXNodeMap);
        return idSimpleXNodeMap.get(id);
    }

    public static void remove() {
        MAPPER_TEMPLATE_MAP.clear();
    }
}
