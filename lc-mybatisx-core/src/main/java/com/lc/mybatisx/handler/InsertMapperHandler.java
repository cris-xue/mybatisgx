package com.lc.mybatisx.handler;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.InsertDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.wrapper.InsertSqlWrapper;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
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
 * @description：一句话描述
 * @date ：2020/7/6 12:56
 */
public class InsertMapperHandler extends AbstractMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(InsertMapperHandler.class);

    private ModelMapperHandler modelMapperHandler = new ModelMapperHandler() {
        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            return null;
        }
    };
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

        InsertSqlWrapper insertSqlWrapper = (InsertSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(entityClass);
        insertSqlWrapper.setModelWrapperList(modelWrapperList);

        return insertSqlWrapper;
    }

    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/insert_mapper.ftl");
        List<XNode> xNodeList = generateInsertMethod(template);
        return xNodeList;
    }
    
    public List<XNode> generateInsertMethod(Template template) {
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

    @Override
    protected SqlWrapper instanceSqlWrapper() {
        return new InsertSqlWrapper();
    }
}
