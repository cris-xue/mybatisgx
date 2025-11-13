package com.mybatisgx.ext.builder;

import com.mybatisgx.annotation.FetchMode;
import com.mybatisgx.ext.mapping.BatchSelectResultMapping;
import com.mybatisgx.template.TemplateHandler;
import org.apache.ibatis.builder.PatchXMLMapperBuilder;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 一句话描述
 * @author ccxuef
 * @date 2025/9/5 18:40
 */
public class MybatisgxXMLMapperBuilder extends PatchXMLMapperBuilder {

    public MybatisgxXMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        super(inputStream, configuration, resource, sqlFragments);
    }

    @Override
    protected void resultMapElements(List<XNode> list) {
        TemplateHandler.builder(builderAssistant).resultMap(list).buildResultMap();
        super.resultMapElements(list);
    }

    @Override
    protected void buildStatementFromContext(List<XNode> list) {
        TemplateHandler.builder(builderAssistant).statement(list).buildStatement();
        super.buildStatementFromContext(list);
    }

    @Override
    protected ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
        ResultMapping resultMapping = super.buildResultMappingFromContext(context, resultType, flags);
        if (FetchMode.BATCH.name().equals(context.getStringAttribute("fetchMode"))) {
            return new BatchSelectResultMapping(resultMapping);
        }
        if (FetchMode.JOIN.name().equals(context.getStringAttribute("fetchMode"))) {
            return new BatchSelectResultMapping(resultMapping);
        }
        return resultMapping;
    }
}
