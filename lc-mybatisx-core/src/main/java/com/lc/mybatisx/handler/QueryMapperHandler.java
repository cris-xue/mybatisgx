package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.InsertDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 12:56
 */
public class QueryMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(QueryMapperHandler.class);

    private List<QuerySqlWrapper> querySqlWrapperList;

    public QueryMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

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

    private Class<?> getDaoInterface(String namespace) {
        Class<?> daoInterface = null;
        try {
            daoInterface = Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return daoInterface;
    }

    private Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
            ParameterizedTypeImpl daoSuperInterfaceClass = (ParameterizedTypeImpl) daoSuperInterfaceType;
            Class<?> daoSuperInterface = daoSuperInterfaceClass.getRawType();
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (daoSuperInterface == InsertDao.class) {
                return daoInterfaceParams;
            }
        }

        return null;
    }

    private QuerySqlWrapper buildInsertSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.QUERY) {
            return null;
        }

        String methodName = method.getName();
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        String tableName = entityClass.getAnnotation(Table.class).name();
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];

        QuerySqlWrapper querySqlWrapper = new QuerySqlWrapper();
        querySqlWrapper.setNamespace(namespace);
        querySqlWrapper.setMethodName(methodName);
        querySqlWrapper.setParameterType(entityClass.getName());
        querySqlWrapper.setTableName(tableName);
        querySqlWrapper.setResultType(entityClass.getName());

        List<ModelWrapper> modelWrapperList = new ArrayList<>();
        Class<?> modelClass = null;
        if ("findById".equals(methodName)) {
            modelClass = entityClass;
        } else if ("findAll".equals(methodName)) {
            modelClass = entityClass;
        } else {
            modelClass = method.getReturnType();
        }
        PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(modelClass);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            ModelWrapper modelWrapper = new ModelWrapper();

            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            String fieldName = propertyDescriptor.getName();
            if ("class".equals(fieldName)) {
                continue;
            }
            modelWrapper.setDbColumn(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName));
            modelWrapper.setEntityColumn(fieldName);
            modelWrapperList.add(modelWrapper);
        }
        querySqlWrapper.setModelWrapperList(modelWrapperList);

        List<WhereWrapper> whereWrapperList = new ArrayList<>();
        if (!"findAll".equals(methodName)) {
            String[] methodNames = methodName.split("By");
            String[] wheres = methodNames[1].split("And|Or");
            for (int i = 0; i < wheres.length; i++) {
                WhereWrapper whereWrapper = new WhereWrapper();
                whereWrapper.setField(wheres[i]);
                whereWrapper.setOp("=");
                whereWrapper.setValue("${" + wheres[i] + "}");
                whereWrapperList.add(whereWrapper);
            }
        }
        querySqlWrapper.setWhereWrapperList(whereWrapperList);

        return querySqlWrapper;
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

}
