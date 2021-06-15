package com.lc.mybatisx.handler;

import com.lc.mybatisx.parse.KeywordParse;
import com.lc.mybatisx.parse.SqlModel;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.GenericUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import freemarker.template.Template;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：查询处理器
 * @date ：2020/7/6 12:56
 */
public class QueryMapperHandler extends AbstractMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(QueryMapperHandler.class);

    private ModelMapperHandler modelMapperHandler;
    private WhereMapperHandler whereMapperHandler;
    private List<QuerySqlWrapper> querySqlWrapperList;

    public QueryMapperHandler() {
        this.modelMapperHandler = new QueryModelMapperHandler();
        whereMapperHandler = new WhereMapperHandler(KeywordParse.getKeywordMap());
        querySqlWrapperList = new ArrayList<>();
    }

    @Override
    public void init(SqlModel sqlModel, String namespace, Method method, Type[] daoInterfaceParams) {
        this.sqlModel = sqlModel;
        build(namespace, method, daoInterfaceParams);
    }

    private void build(String namespace, Method method, Type[] daoInterfaceParams) {
        QuerySqlWrapper querySqlWrapper = (QuerySqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        querySqlWrapper.setModelWrapperList(modelWrapperList);

        // SqlModel sqlModel = KeywordParse.parseMethod1(method, entityClass);
        // whereMapperHandler.build(methodKeywordList);

        // querySqlWrapper.setWhereWrapper();

        WhereWrapper whereWrapper = KeywordParse.buildWhereWrapper(method, this.sqlModel.getWhere(), daoInterfaceParams, modelWrapperList);
        querySqlWrapper.setWhereWrapper(whereWrapper);

        /*LimitWrapper limitWrapper = KeywordParse.buildLimitWrapper(methodKeywordList);
        querySqlWrapper.setLimitWrapper(limitWrapper);

        OrderWrapper orderWrapper = KeywordParse.buildOrderByWrapper(methodKeywordList);
        querySqlWrapper.setOrderWrapper(orderWrapper);*/

        // boolean dynamic = KeywordParse.isDynamic(methodKeywordList);
        querySqlWrapper.setDynamic(false);

        this.querySqlWrapperList.add(querySqlWrapper);
    }

    @Override
    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/query_mapper.ftl");
        List<XNode> xNodeList = generateQueryMethod(template);
        return xNodeList;
    }

    public List<XNode> generateQueryMethod(Template template) {
        List<XNode> queryXNodeList = new ArrayList<>();
        querySqlWrapperList.forEach(querySqlWrapper -> {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("querySqlWrapper", querySqlWrapper);

            XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
            XNode mapperXNode = xPathParser.evalNode("/mapper");

            List<XNode> queryXNode = mapperXNode.evalNodes("select");
            queryXNodeList.addAll(queryXNode);
        });

        return queryXNodeList;
    }

    @Override
    protected SqlWrapper instanceSqlWrapper() {
        return new QuerySqlWrapper();
    }

    class QueryModelMapperHandler extends ModelMapperHandler {

        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            Type type = GenericUtils.getGenericType(method.getGenericReturnType());

            if (type == Map.class) {
                return entityClass;
            }
            if ("ENTITY".equals(type.getTypeName())) {
                return entityClass;
            }

            if (type instanceof Class) {
                return (Class<?>) type;
            }

            return null;
        }

    }

}
