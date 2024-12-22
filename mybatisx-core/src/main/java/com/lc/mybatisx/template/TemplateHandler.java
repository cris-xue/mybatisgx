package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.handler.MapperInfoHandler;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

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
        MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
        MapperInfo mapperInfo = mapperInfoHandler.execute(builderAssistant);
        mergeMethod(builderAssistant, mapperInfo);

        List<XNode> mapperXNodeList = curdTemplateHandler.execute(mapperInfo);
        List<XNode> resultMapXNodeList = resultMapTemplateHandler.execute(mapperInfo.getResultMapInfoMap());
        merge(resultMapXNodeList, mapperXNodeList);
        return this;
    }

    /**
     * 已经自定义的方法从解析中移除掉
     *
     * @param builderAssistant
     * @param mapperInfo
     */
    private void mergeMethod(MapperBuilderAssistant builderAssistant, MapperInfo mapperInfo) {
        Collection<String> mappedStatementNames = builderAssistant.getConfiguration().getMappedStatementNames();
        List<MethodInfo> methodInfoList = mapperInfo.getMethodInfoList().stream()
                .filter(o -> !mappedStatementNames.contains(String.format("%s.%s", mapperInfo.getNamespace(), o.getMethodName())))
                .collect(Collectors.toList());
        mapperInfo.setMethodInfoList(methodInfoList);
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
