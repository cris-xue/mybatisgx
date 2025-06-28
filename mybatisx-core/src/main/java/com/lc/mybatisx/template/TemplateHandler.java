package com.lc.mybatisx.template;

import com.lc.mybatisx.context.MapperTemplateContextHolder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateHandler {

    private CurdTemplateHandler curdTemplateHandler = new CurdTemplateHandler();
    private ResultMapTemplateHandler resultMapTemplateHandler = new ResultMapTemplateHandler();

    private MapperBuilderAssistant builderAssistant;
    private List<XNode> originResultMapXNodeList = null;
    private List<XNode> originMapperXNodeList = null;

    private TemplateHandler(MapperBuilderAssistant builderAssistant) {
        this.builderAssistant = builderAssistant;
    }

    public static TemplateHandler builder(MapperBuilderAssistant builderAssistant) {
        return new TemplateHandler(builderAssistant);
    }

    public TemplateHandler build() {
        /*MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
        MapperInfo mapperInfo = mapperInfoHandler.execute(builderAssistant);*/
        List<String> actionIdList = mergeMethod(builderAssistant);
        List<XNode> actionXNodeList = new ArrayList();
        actionIdList.forEach(actionId -> {
            XNode xNode = MapperTemplateContextHolder.getCurdTemplate(builderAssistant.getCurrentNamespace(), actionId);
            actionXNodeList.add(xNode);
        });

        List<String> resultMapIdList = mergeResultMap(builderAssistant);
        List<XNode> resultMapXNodeList = new ArrayList();
        resultMapIdList.forEach(resultMapId -> {
            XNode xNode = MapperTemplateContextHolder.getResultMapTemplate(builderAssistant.getCurrentNamespace(), resultMapId);
            resultMapXNodeList.add(xNode);
        });

        /*List<XNode> mapperXNodeList = curdTemplateHandler.execute(mapperInfo);
        List<XNode> resultMapXNodeList = resultMapTemplateHandler.execute(mapperInfo.getResultMapInfoList());*/
        merge(resultMapXNodeList, actionXNodeList);
        return this;
    }

    /**
     * 已经自定义的方法从解析中移除掉
     *
     * @param builderAssistant
     */
    private List<String> mergeMethod(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        List<String> actionIdList = MapperTemplateContextHolder.getActionIdList(namespace);
        Collection<String> mappedStatementNames = builderAssistant.getConfiguration().getMappedStatementNames();
        return actionIdList.stream()
                .filter(actionId -> !mappedStatementNames.contains(String.format("%s.%s", namespace, actionId)))
                .collect(Collectors.toList());
    }

    private List<String> mergeResultMap(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        List<String> resultMapIdList = MapperTemplateContextHolder.getResultMapIdList(namespace);
        Collection<String> mappedStatementNames = builderAssistant.getConfiguration().getResultMapNames();
        return resultMapIdList.stream()
                .filter(resultMapId -> !mappedStatementNames.contains(String.format("%s.%s", namespace, resultMapId)))
                .collect(Collectors.toList());
    }

    public TemplateHandler resultMap(List<XNode> resultMapXNodeList) {
        this.originResultMapXNodeList = resultMapXNodeList;
        return this;
    }

    public TemplateHandler mapper(List<XNode> curdXNodeList) {
        this.originMapperXNodeList = curdXNodeList;
        return this;
    }

    private void merge(List<XNode> resultMapXNodeList, List<XNode> mapperXNodeList) {
        originResultMapXNodeList.addAll(resultMapXNodeList);
        originMapperXNodeList.addAll(mapperXNodeList);
    }

}
