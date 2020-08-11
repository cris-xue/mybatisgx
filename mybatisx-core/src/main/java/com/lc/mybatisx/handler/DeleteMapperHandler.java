package com.lc.mybatisx.handler;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.DeleteDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.*;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Version;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：删除处理器
 * @date ：2020/7/5 12:56
 */
public class DeleteMapperHandler extends AbstractMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMapperHandler.class);

    private ModelMapperHandler modelMapperHandler;
    /*private ModelMapperHandler modelMapperHandler = new ModelMapperHandler() {
        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            return entityClass;
        }
    };*/
    private ConditionMapperHandler conditionMapperHandler;
    /*private ConditionMapperHandler conditionMapperHandler = new ConditionMapperHandler();*/

    private List<DeleteSqlWrapper> deleteSqlWrapperList;

    public DeleteMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        this.modelMapperHandler = new DeleteModelMapperHandler();

        List<String> parseMethodList = new ArrayList<>();
        parseMethodList.add("deleteBy");
        this.conditionMapperHandler = new DeleteConditionMapperHandler(parseMethodList);

        initDeleteSqlWrapper(builderAssistant, namespace);
    }

    private void initDeleteSqlWrapper(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface, DeleteDao.class);

        List<DeleteSqlWrapper> deleteSqlWrapperList = new ArrayList<>();
        Method[] methods = daoInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            DeleteSqlWrapper deleteSqlWrapper = this.buildDeleteSqlWrapper(namespace, method, daoInterfaceParams);
            if (deleteSqlWrapper == null) {
                continue;
            }

            // 自动加入乐观锁查询
            /*VersionWrapper versionWrapper = deleteSqlWrapper.getVersionWrapper();
            if (versionWrapper != null && versionWrapper.getVersion()) {
                DeleteSqlWrapper dw = new DeleteSqlWrapper();
                BeanUtils.copyProperties(deleteSqlWrapper, dw);
                dw.setMethodName("find_" + deleteSqlWrapper.getMethodName() + "_version");
                dw.setVersionQuery(true);
                deleteSqlWrapperList.add(dw);
            }*/

            deleteSqlWrapperList.add(deleteSqlWrapper);
        }

        this.deleteSqlWrapperList = deleteSqlWrapperList;
    }

    private DeleteSqlWrapper buildDeleteSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.DELETE) {
            return null;
        }

        DeleteSqlWrapper deleteSqlWrapper = (DeleteSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        deleteSqlWrapper.setModelWrapperList(modelWrapperList);

        // 构建条件包装器
        WhereWrapper whereWrapper = conditionMapperHandler.buildWhereWrapper(method);
        deleteSqlWrapper.setWhereWrapper(whereWrapper);

        // 乐观锁
        Field field = ReflectUtils.getField(modelClass, Version.class);
        if (field != null) {
            VersionWrapper versionWrapper = new VersionWrapper();
            versionWrapper.setVersion(true);
            versionWrapper.setDbColumn(field.getName());
            versionWrapper.setJavaColumn(field.getName());

            deleteSqlWrapper.setVersionWrapper(versionWrapper);
        }

        return deleteSqlWrapper;
    }

    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/delete_mapper.ftl");
        List<XNode> xNodeList = generateDeleteMethod(template);
        return xNodeList;
    }

    public List<XNode> generateDeleteMethod(Template template) {
        List<XNode> deleteXNodeList = new ArrayList<>();
        deleteSqlWrapperList.forEach(deleteSqlWrapper -> {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("deleteSqlWrapper", deleteSqlWrapper);

            XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
            XNode mapperXNode = xPathParser.evalNode("/mapper");

            String expression = deleteSqlWrapper.getVersionQuery() ? "select" : "delete";
            List<XNode> deleteXNode = mapperXNode.evalNodes(expression);
            deleteXNodeList.addAll(deleteXNode);
        });

        return deleteXNodeList;
    }

    @Override
    protected SqlWrapper instanceSqlWrapper() {
        return new DeleteSqlWrapper();
    }

    class DeleteModelMapperHandler extends ModelMapperHandler {

        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            return entityClass;
        }

    }

    class DeleteConditionMapperHandler extends ConditionMapperHandler {

        public DeleteConditionMapperHandler(List<String> parseMethodList) {
            super(parseMethodList);
        }

    }

}
