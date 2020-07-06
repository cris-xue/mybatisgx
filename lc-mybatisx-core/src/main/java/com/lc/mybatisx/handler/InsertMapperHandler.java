package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.InsertDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.wrapper.InsertSqlWrapper;
import com.lc.mybatisx.wrapper.ModelWrapper;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

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
public class InsertMapperHandler extends AbstractMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(InsertMapperHandler.class);

    private List<InsertSqlWrapper> insertSqlWrapperList;

    public InsertMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface, InsertDao.class);

        List<InsertSqlWrapper> insertSqlWrapperList = new ArrayList<>();
        Method[] methods = daoInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            InsertSqlWrapper insertSqlWrapper = this.buildInsertSqlWrapper(namespace, method, daoInterfaceParams);
            if (insertSqlWrapper == null) {
                continue;
            }
            insertSqlWrapperList.add(insertSqlWrapper);
        }

        this.insertSqlWrapperList = insertSqlWrapperList;
    }

    private InsertSqlWrapper buildInsertSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.INSERT) {
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
        insertSqlWrapper.setModelWrapperList(modelWrapperList);

        return insertSqlWrapper;
    }

    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/insert_mapper.ftl");
        List<XNode> xNodeList = generateBasicMethod(template);
        return xNodeList;
    }

    /**
     * 生成基本的增删改查
     *
     * @return
     */
    public List<XNode> generateBasicMethod(Template template) {
        List<XNode> insertXNodeList = new ArrayList<>();
        insertSqlWrapperList.forEach(insertSqlWrapper -> {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("insertSqlWrapper", insertSqlWrapper);

            XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
            XNode mapperXNode = xPathParser.evalNode("/mapper");

            List<XNode> insertXNode = mapperXNode.evalNodes("insert");
            insertXNodeList.addAll(insertXNode);
        });

        return insertXNodeList;
    }

    private static PropertyDescriptor[] getBeanPropertyList(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

}
