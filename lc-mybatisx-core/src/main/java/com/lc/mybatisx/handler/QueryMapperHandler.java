package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.InsertDao;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.io.*;
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
        PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(entityClass);
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
        WhereWrapper whereWrapper = new WhereWrapper();
        whereWrapper.setField("id");
        whereWrapper.setOp("=");
        whereWrapper.setValue("1");
        whereWrapperList.add(whereWrapper);
        querySqlWrapper.setWhereWrapperList(whereWrapperList);

        return querySqlWrapper;
    }

    public List<XNode> readTemplate() {
        Reader reader = null;
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mapper/mysql/query_mapper.ftl");
            reader = new InputStreamReader(classPathResource.getInputStream());
            Template template = new Template("mapper", reader, null, "utf-8");

            List<XNode> xNodeList = generateBasicMethod(template);

            return xNodeList;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 生成基本的增删改查
     *
     * @return
     */
    public List<XNode> generateBasicMethod(Template template) {
        List<XNode> queryXNodeList = new ArrayList<>();
        querySqlWrapperList.forEach(querySqlWrapper -> {
            StringWriter stringWriter = null;
            InputStream is = null;
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("querySqlWrapper", querySqlWrapper);

                stringWriter = new StringWriter();
                template.process(templateData, stringWriter);
                String basicMethodXml = stringWriter.toString();
                log.debug(basicMethodXml);

                // 把xml字符串转换成Document
                is = new ByteArrayInputStream(basicMethodXml.getBytes());
                XPathParser xPathParser = new XPathParser(is);
                XNode mapperXNode = xPathParser.evalNode("/mapper");

                List<XNode> queryXNode = mapperXNode.evalNodes("select");
                queryXNodeList.addAll(queryXNode);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stringWriter != null) {
                        stringWriter.flush();
                    }
                    if (stringWriter != null) {
                        stringWriter.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        return queryXNodeList;
    }

    private static PropertyDescriptor[] getBeanPropertyList(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

}
