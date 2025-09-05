package com.lc.mybatisx.ext;

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
public class MybatisgxXMLMapperBuilder extends XMLMapperBuilder {

    public MybatisgxXMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        super(inputStream, configuration, resource, sqlFragments);
    }

    @Override
    protected void resultMapElements(List<XNode> list) {
        super.resultMapElements(list);
    }

    @Override
    protected void buildStatementFromContext(List<XNode> list) {
        super.buildStatementFromContext(list);
    }

    @Override
    protected ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
        return super.buildResultMappingFromContext(context, resultType, flags);
    }
}
