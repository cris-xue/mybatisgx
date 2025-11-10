package com.mybatisgx.template;

import com.mybatisgx.context.MapperTemplateContextHolder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateHandler {

    private MapperBuilderAssistant builderAssistant;
    private List<XNode> originResultMapXNodeList = null;
    private List<XNode> originStatementXNodeList = null;

    private TemplateHandler(MapperBuilderAssistant builderAssistant) {
        this.builderAssistant = builderAssistant;
    }

    public static TemplateHandler builder(MapperBuilderAssistant builderAssistant) {
        return new TemplateHandler(builderAssistant);
    }

    public TemplateHandler buildResultMap() {
        List<String> resultMapIdList = mergeResultMap(builderAssistant);
        List<XNode> resultMapXNodeList = new ArrayList();
        for (String resultMapId : resultMapIdList) {
            XNode xNode = MapperTemplateContextHolder.getResultMapTemplate(builderAssistant.getCurrentNamespace(), resultMapId);
            resultMapXNodeList.add(xNode);
        }
        originResultMapXNodeList.addAll(resultMapXNodeList);
        return this;
    }

    public TemplateHandler buildStatement() {
        List<String> statementIdList = mergeStatement(builderAssistant);
        List<XNode> statementList = new ArrayList();
        for (String statementId : statementIdList) {
            XNode xNode = MapperTemplateContextHolder.getStatementTemplate(builderAssistant.getCurrentNamespace(), statementId);
            statementList.add(xNode);
        }
        originStatementXNodeList.addAll(statementList);
        return this;
    }

    private List<String> mergeResultMap(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        List<String> resultMapIdList = MapperTemplateContextHolder.getResultMapIdList(namespace);
        Collection<String> mappedStatementNames = builderAssistant.getConfiguration().getResultMapNames();
        return resultMapIdList.stream()
                .filter(resultMapId -> !mappedStatementNames.contains(String.format("%s.%s", namespace, resultMapId)))
                .collect(Collectors.toList());
    }

    /**
     * 已经自定义的方法从解析中移除掉
     *
     * @param builderAssistant
     */
    private List<String> mergeStatement(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        List<String> statementIdList = MapperTemplateContextHolder.getStatementIdList(namespace);
        Collection<String> mappedStatementNames = builderAssistant.getConfiguration().getMappedStatementNames();
        return statementIdList.stream()
                .filter(statementId -> !mappedStatementNames.contains(String.format("%s.%s", namespace, statementId)))
                .collect(Collectors.toList());
    }

    public TemplateHandler resultMap(List<XNode> resultMapXNodeList) {
        this.originResultMapXNodeList = resultMapXNodeList;
        return this;
    }

    public TemplateHandler statement(List<XNode> statementXNodeList) {
        this.originStatementXNodeList = statementXNodeList;
        return this;
    }
}
