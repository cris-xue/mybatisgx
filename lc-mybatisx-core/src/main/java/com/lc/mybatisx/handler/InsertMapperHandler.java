package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.InsertDao;
import com.lc.mybatisx.wrapper.InsertSqlWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.ibatis.binding.BindingException;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 12:56
 */
public class InsertMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(InsertMapperHandler.class);

    private List<InsertSqlWrapper> insertSqlWrapperList;

    public InsertMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

        List<InsertSqlWrapper> insertSqlWrapperList = new ArrayList<>();
        Method[] methods = daoInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            InsertSqlWrapper insertSqlWrapper = this.buildInsertSqlWrapper(namespace, method, daoInterfaceParams);
            insertSqlWrapperList.add(insertSqlWrapper);
        }

        this.insertSqlWrapperList = insertSqlWrapperList;
        // Map<String, Object> templateData = new HashMap<>();
        // templateData.put("insertSqlWrapperList", insertSqlWrapperList);
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

            // String entity = actualTypeArguments[0].getTypeName();
            // String id = actualTypeArguments[1].getTypeName();
        }

        return null;
    }

    private Class<?> getTypeClass(Type type) {
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) type;
        return parameterizedType.getRawType();
    }

    private InsertSqlWrapper buildInsertSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null && mapperMethod.type() != MethodType.INSERT) {
            return null;
        }

        String methodName = method.getName();
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        String tableName = entityClass.getAnnotation(Table.class).name();
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];

        InsertSqlWrapper insertSqlWrapper = new InsertSqlWrapper();
        insertSqlWrapper.setNamespace(namespace);
        insertSqlWrapper.setMethodName(methodName);
        insertSqlWrapper.setParameterType(entityClass.getName());
        insertSqlWrapper.setTableName(tableName);
        insertSqlWrapper.setResultType(entityClass.getName());

        List<String> dbColumn = new ArrayList<>();
        List<String> entityColumn = new ArrayList<>();
        PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(entityClass);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            String fieldName = propertyDescriptor.getName();
            if ("class".equals(fieldName)) {
                continue;
            }
            dbColumn.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName));
            entityColumn.add(fieldName);
        }
        insertSqlWrapper.setDbColumn(dbColumn);
        insertSqlWrapper.setEntityColumn(entityColumn);

        return insertSqlWrapper;
    }

    /**
     * 自定义方法，主要生成基本sql的XNode节点
     *
     * @param namespace
     * @return
     */
    public static List<XNode> getCurrentNodeData(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoClass = null;
        try {
            daoClass = Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Type[] genericInterfaces = daoClass.getGenericInterfaces();
        if (genericInterfaces.length != 1) {
            // throw new BindingException("绑定异常!");
            return new ArrayList<XNode>();
        }
        Type[] actualTypeArguments = ((ParameterizedTypeImpl) genericInterfaces[0]).getActualTypeArguments();
        String entity = actualTypeArguments[0].getTypeName();
        String id = actualTypeArguments[1].getTypeName();

        Class<?> entityClass = null;
        try {
            entityClass = Class.forName(entity);
        } catch (ClassNotFoundException e) {
            throw new BindingException("绑定异常!", e);
        }
        XNode xmlNode = generateBasicXNode(entityClass, namespace);
        List<XNode> xNodelist = xmlNode.evalNodes("select|insert|update|delete");

        // 如果已经绑定，就不用再次绑定
        Configuration configuration = builderAssistant.getConfiguration();
        xNodelist = xNodelist.stream()
                .filter(i -> !configuration.hasStatement(namespace + "." + i.getStringAttribute("id")))
                .collect(Collectors.toList());
        return xNodelist;
    }

    /**
     * 生成基本的增删改查
     *
     * @param clazz
     * @return
     */
    public static XNode generateBasicXNode(Class<?> clazz, String namespace) {
        InputStream is = null;
        try {
            is = null;// generateBasicMethod(clazz, namespace);

            // 把xml字符串转换成Document
            XPathParser xPathParser = new XPathParser(is);
            return xPathParser.evalNode("/mapper");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<XNode> readTemplate() {
        Reader reader = null;
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mapper/mysql/insert_mapper.ftl");
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
        List<XNode> insertXNodeList = new ArrayList<>();
        insertSqlWrapperList.forEach(insertSqlWrapper -> {
            StringWriter stringWriter = null;
            InputStream is = null;
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("insertSqlWrapper", insertSqlWrapper);

                stringWriter = new StringWriter();
                template.process(templateData, stringWriter);
                String basicMethodXml = stringWriter.toString();
                log.debug(basicMethodXml);

                // 把xml字符串转换成Document
                is = new ByteArrayInputStream(basicMethodXml.getBytes());
                XPathParser xPathParser = new XPathParser(is);
                XNode mapperXNode = xPathParser.evalNode("/mapper");

                List<XNode> insertXNode = mapperXNode.evalNodes("insert");
                insertXNodeList.addAll(insertXNode);
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

        return insertXNodeList;
    }

    /**
     * 获取指定字段
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> superclass = clazz.getSuperclass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {

        }
        try {
            field = superclass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e1) {
            throw e1;
        }
        return field;
    }

    private static PropertyDescriptor[] getBeanPropertyList(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

}
