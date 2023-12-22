package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.handler.MapperInfoHandler;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;

import java.util.List;

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

        this.mapperXNodeList = curdTemplateHandler.execute(mapperInfo, mapperInfo.getMethodInfoList(), mapperInfo.getTableInfo());
        this.resultMapXNode = resultMapTemplateHandler.execute(mapperInfo.getResultMapInfo());
        return this;
    }

    public void mergeResultMap(List<XNode> resultMapXNode) {
        resultMapXNode.add(this.resultMapXNode);
    }

    public void mergeMapper(List<XNode> curdXNode) {
        curdXNode.addAll(this.mapperXNodeList);
    }

}
