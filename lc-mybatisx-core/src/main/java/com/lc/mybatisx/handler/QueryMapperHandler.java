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
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
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

    private static final Logger log = LoggerFactory.getLogger(QueryMapperHandler.class);

    private ModelMapperHandler modelMapperHandler;
    private ConditionMapperHandler conditionMapperHandler;

    private List<QuerySqlWrapper> querySqlWrapperList;

    public QueryMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
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

            QuerySqlWrapper querySqlWrapper = this.buildInsertSqlWrapper(namespace, method, daoInterfaceParams);
            if (querySqlWrapper == null) {
                continue;
            }
            querySqlWrapperList.add(querySqlWrapper);
        }

        this.querySqlWrapperList = querySqlWrapperList;
    }

    private QuerySqlWrapper buildInsertSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.QUERY) {
            return null;
        }

        QuerySqlWrapper querySqlWrapper = (QuerySqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = this.getModelClass(method, entityClass);
        PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(modelClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(propertyDescriptors);
        querySqlWrapper.setModelWrapperList(modelWrapperList);

        List<WhereWrapper> whereWrapperList = conditionMapperHandler.buildWhereWrapper(method);
        querySqlWrapper.setWhereWrapperList(whereWrapperList);

        return querySqlWrapper;
    }

    private Class<?> getModelClass(Method method, Class<?> entityClass) {
        String methodName = method.getName();
        Class<?> modelClass;
        if ("findById".equals(methodName)) {
            modelClass = entityClass;
        } else if ("findAll".equals(methodName)) {
            modelClass = entityClass;
        } else {
            modelClass = method.getReturnType();
        }

        return modelClass;
    }

    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/query_mapper.ftl");
        List<XNode> xNodeList = generateBasicMethod(template);
        return xNodeList;
    }

    /**
     * 生成基本的增删改查
     *
     * @return
     */
    public List<XNode> generateBasicMethod(Template template) {
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

    private static PropertyDescriptor[] getBeanPropertyList(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

    @Override
    protected SqlWrapper instanceSqlWrapper() {
        return new QuerySqlWrapper();
    }
}
