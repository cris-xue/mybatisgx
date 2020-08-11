package com.lc.mybatisx.handler;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.QueryDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
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
    /*private ModelMapperHandler modelMapperHandler = new ModelMapperHandler() {
        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            String methodName = method.getName();
            if ("findById".equals(methodName)) {
                return entityClass;
            } else if ("findAll".equals(methodName)) {
                return entityClass;
            } else {
                Type type = method.getGenericReturnType();

                Class<?> clazz = getGenericType(type, null, entityClass);
                return clazz;
            }
        }
    };*/
    private ConditionMapperHandler conditionMapperHandler;
    // private ConditionMapperHandler conditionMapperHandler = new ConditionMapperHandler();

    private List<QuerySqlWrapper> querySqlWrapperList;

    public QueryMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        initQuerySqlWrapper(builderAssistant, namespace);

        this.modelMapperHandler = new QueryModelMapperHandler();

        List<String> parseMethodList = new ArrayList<>();
        parseMethodList.add("findTop10By");
        parseMethodList.add("findBy");
        parseMethodList.add("findAll");
        parseMethodList.add("find");
        this.conditionMapperHandler = new QueryConditionMapperHandler(parseMethodList);
    }

    private void initQuerySqlWrapper(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface, QueryDao.class);

        List<QuerySqlWrapper> querySqlWrapperList = new ArrayList<>();
        Method[] methods = daoInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            QuerySqlWrapper querySqlWrapper = this.buildQuerySqlWrapper(namespace, method, daoInterfaceParams);
            if (querySqlWrapper == null) {
                continue;
            }
            querySqlWrapperList.add(querySqlWrapper);
        }

        this.querySqlWrapperList = querySqlWrapperList;
    }

    private QuerySqlWrapper buildQuerySqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.QUERY) {
            return null;
        }

        QuerySqlWrapper querySqlWrapper = (QuerySqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        querySqlWrapper.setModelWrapperList(modelWrapperList);

        WhereWrapper whereWrapper = conditionMapperHandler.buildWhereWrapper(method);
        querySqlWrapper.setWhereWrapper(whereWrapper);

        return querySqlWrapper;
    }

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
            String methodName = method.getName();
            if ("findById".equals(methodName)) {
                return entityClass;
            } else if ("findAll".equals(methodName)) {
                return entityClass;
            } else {
                Type type = method.getGenericReturnType();

                Class<?> clazz = getGenericType(type, null, entityClass);
                return clazz;
            }
        }

    }

    class QueryConditionMapperHandler extends ConditionMapperHandler {

        public QueryConditionMapperHandler(List<String> parseMethodList) {
            super(parseMethodList);
        }
    }

}
