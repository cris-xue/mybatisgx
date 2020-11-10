package com.lc.mybatisx.handler;

import com.lc.mybatisx.parse.KeywordParse;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.*;
import freemarker.template.Template;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
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
    private List<DeleteSqlWrapper> deleteSqlWrapperList;

    public DeleteMapperHandler() {
        this.modelMapperHandler = new DeleteModelMapperHandler();
        deleteSqlWrapperList = new ArrayList<>();
    }

    @Override
    public void init(String namespace, Method method, Type[] daoInterfaceParams) {
        build(namespace, method, daoInterfaceParams);
    }

    private void build(String namespace, Method method, Type[] daoInterfaceParams) {
        DeleteSqlWrapper deleteSqlWrapper = (DeleteSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        deleteSqlWrapper.setModelWrapperList(modelWrapperList);

        List<String> methodKeywordList = KeywordParse.parseMethod(method);
        WhereWrapper whereWrapper = KeywordParse.buildWhereWrapper(method, methodKeywordList, daoInterfaceParams);
        deleteSqlWrapper.setWhereWrapper(whereWrapper);

        boolean dynamic = KeywordParse.isDynamic(methodKeywordList);
        deleteSqlWrapper.setDynamic(dynamic);

        // 乐观锁
        Field field = ReflectUtils.getField(modelClass, Version.class);
        if (field != null) {
            VersionWrapper versionWrapper = new VersionWrapper();
            // versionWrapper.setVersion(true);
            versionWrapper.setDbColumn(field.getName());
            versionWrapper.setJavaColumn(field.getName());

            deleteSqlWrapper.setVersionWrapper(versionWrapper);
        }

        this.deleteSqlWrapperList.add(deleteSqlWrapper);
    }

    @Override
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

        @Override
        protected boolean ignoreField(Field field) {
            Version version = field.getAnnotation(Version.class);
            return version != null || super.ignoreField(field);
        }

    }

}
