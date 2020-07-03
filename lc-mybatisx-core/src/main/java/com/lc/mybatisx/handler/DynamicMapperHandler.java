package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
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
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * create by: 薛承城
 * description: 动态Mapper处理器，CustomXMLMapperBuilder中会调用该方法
 * create time: 2019/5/9 11:05
 */
public class DynamicMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(DynamicMapperHandler.class);

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
//            throw new BindingException("绑定异常!");
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
        XNode xmlNode = generateBasicMethod(entityClass, namespace);
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
    public static XNode generateBasicMethod(Class<?> clazz, String namespace) {
        Reader reader = null;
        StringWriter stringWriter = null;
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("test/mapper.ftl");
            reader = new InputStreamReader(classPathResource.getInputStream());
            Template template = new Template("mapper", reader, null, "utf-8");

            Map<String, Object> data = new HashMap<>();
            data.put("namespace", namespace);
            data.put("parameterType", clazz.getName());
            data.put("resultType", clazz.getName());
            data.put("tableName", clazz.getAnnotation(Table.class).name());

            Map<String, Object> columnMap = new LinkedHashMap<>();
            PropertyDescriptor[] propertyDescriptors = getBeanPropertyList(clazz);
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                String fieldName = propertyDescriptor.getName();
                if ("class".equals(fieldName)) {
                    continue;
                }
                columnMap.put(fieldName, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName));
            }
            data.put("columnMap", columnMap);

            stringWriter = new StringWriter();
            template.process(data, stringWriter);
            String basicMethodXml = stringWriter.toString();
            log.debug(basicMethodXml);
            inputStream = new ByteArrayInputStream(basicMethodXml.getBytes());

            // 把xml字符串转换成Document
            XPathParser xPathParser = new XPathParser(inputStream);
            return xPathParser.evalNode("/mapper");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (stringWriter != null) {
                    stringWriter.flush();
                }
                if (stringWriter != null) {
                    stringWriter.close();
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
