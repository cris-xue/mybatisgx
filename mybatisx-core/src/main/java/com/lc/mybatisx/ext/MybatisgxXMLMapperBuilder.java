package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
import com.lc.mybatisx.template.TemplateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一句话描述
 * @author ccxuef
 * @date 2025/9/5 18:40
 */
public class MybatisgxXMLMapperBuilder extends XMLMapperBuilder {

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
        TemplateHandler.builder(builderAssistant).mapper(list).buildStatement();
        super.buildStatementFromContext(list);
    }

    @Override
    protected ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
        ResultMapping resultMapping = super.buildResultMappingFromContext(context, resultType, flags);
        String relationProperty = context.getStringAttribute("relationProperty");
        if (StringUtils.isNotBlank(relationProperty)) {
            return relationPropertyMapping(resultMapping, relationProperty);
        }
        return resultMapping;
    }

    private ResultMapping relationPropertyMapping(ResultMapping resultMapping, String relationProperty) {
        List<BatchSelectResultMapping.RelationPropertyMapping> relationPropertyMappingList = new ArrayList<>();
        relationProperty = relationProperty.replaceAll("\\{|\\}", "");
        String[] relationProperties = StringUtils.split(relationProperty, ",");
        for (String relationColumnMapping : relationProperties) {
            String[] values = StringUtils.split(relationColumnMapping, "=");
            relationPropertyMappingList.add(new BatchSelectResultMapping.RelationPropertyMapping(values[0], values[1]));
        }
        BatchSelectResultMapping batchSelectResultMapping = new BatchSelectResultMapping(resultMapping);
        batchSelectResultMapping.setRelationPropertyMappingList(relationPropertyMappingList);
        return batchSelectResultMapping;
    }
}
