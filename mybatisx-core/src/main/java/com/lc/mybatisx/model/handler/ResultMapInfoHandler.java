package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public static ResultMapInfoHandler build() {
        return new ResultMapInfoHandler();
    }

    public void execute(MapperBuilderAssistant builderAssistant, List<XNode> resultMapXNode) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);

        MapperInfo mapperInfo = mapperInfoHandler.execute(daoInterface);
        Class<?> entityClass = mapperInfo.getEntityClass();
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(getResultMap(entityClass));
        resultMapInfo.setType(entityClass.getTypeName());
        resultMapInfo.setColumnInfoList(columnInfoList);

        XNode xNode = getResultMapXNode(resultMapInfo);
        resultMapXNode.add(xNode);
    }

    private XNode getResultMapXNode(ResultMapInfo resultMapInfo) {
        Template template = FreeMarkerUtils.getTemplate("mapper/result_map.ftl");
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("resultMapInfo", resultMapInfo);
        String resultMapXml = FreeMarkerUtils.processTemplate(templateData, template);

        XPathParser xPathParser = XmlUtils.processXml(resultMapXml);
        XNode xNode = xPathParser.evalNode("/mapper/resultMap");
        return xNode;
    }

}
