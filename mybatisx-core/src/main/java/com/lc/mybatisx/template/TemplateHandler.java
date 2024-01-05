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

    private XNode resultMapXNode = null;
    private List<XNode> mapperXNodeList = null;

    public static TemplateHandler build() {
        return new TemplateHandler();
    }

    public TemplateHandler execute(MapperBuilderAssistant builderAssistant) {
        MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
        MapperInfo mapperInfo = mapperInfoHandler.execute(builderAssistant);
        mergeMethod(builderAssistant, mapperInfo);

        this.mapperXNodeList = curdTemplateHandler.execute(mapperInfo);
        this.resultMapXNode = resultMapTemplateHandler.execute(mapperInfo.getResultMapInfo());
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

    public void mergeResultMap(List<XNode> resultMapXNode) {
        resultMapXNode.add(this.resultMapXNode);
    }

    public void mergeMapper(List<XNode> curdXNode) {
        curdXNode.addAll(this.mapperXNodeList);
    }

}
