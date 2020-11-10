package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.parse.KeywordParse;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.*;
import freemarker.template.Template;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Id;
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
 * @description：更新处理器
 * @date ：2020/7/5 12:56
 */
public class UpdateMapperHandler extends AbstractMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMapperHandler.class);

    private ModelMapperHandler modelMapperHandler;
    private List<UpdateSqlWrapper> updateSqlWrapperList;

    public UpdateMapperHandler() {
        this.modelMapperHandler = new UpdateModelMapperHandler();
        updateSqlWrapperList = new ArrayList<>();
    }

    @Override
    public void init(String namespace, Method method, Type[] daoInterfaceParams) {
        build(namespace, method, daoInterfaceParams);
    }

    private void build(String namespace, Method method, Type[] daoInterfaceParams) {
        UpdateSqlWrapper updateSqlWrapper = (UpdateSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        updateSqlWrapper.setModelWrapperList(modelWrapperList);

        List<String> methodKeywordList = KeywordParse.parseMethod(method);
        WhereWrapper whereWrapper = KeywordParse.buildWhereWrapper(method, methodKeywordList, daoInterfaceParams);
        updateSqlWrapper.setWhereWrapper(whereWrapper);

        boolean dynamic = KeywordParse.isDynamic(methodKeywordList);
        updateSqlWrapper.setDynamic(dynamic);

        // 乐观锁
        Field field = ReflectUtils.getField(modelClass, Version.class);
        if (field != null) {
            String javaColumn = field.getName();
            String dbColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn);

            VersionWrapper versionWrapper = new VersionWrapper();
            versionWrapper.setDbColumn(dbColumn);
            versionWrapper.setJavaColumn(javaColumn);

            updateSqlWrapper.setVersionWrapper(versionWrapper);
        }

        this.updateSqlWrapperList.add(updateSqlWrapper);
    }

    @Override
    public List<XNode> readTemplate() {
        Template template = FreeMarkerUtils.getTemplate("mapper/mysql/update_mapper.ftl");
        List<XNode> xNodeList = generateUpdateMethod(template);
        return xNodeList;
    }

    public List<XNode> generateUpdateMethod(Template template) {
        List<XNode> updateXNodeList = new ArrayList<>();
        updateSqlWrapperList.forEach(updateSqlWrapper -> {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("updateSqlWrapper", updateSqlWrapper);

            XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
            XNode mapperXNode = xPathParser.evalNode("/mapper");

            List<XNode> updateXNode = mapperXNode.evalNodes("update");
            updateXNodeList.addAll(updateXNode);
        });

        return updateXNodeList;
    }

    @Override
    protected SqlWrapper instanceSqlWrapper() {
        return this.sqlWrapper = new UpdateSqlWrapper();
    }

    class UpdateModelMapperHandler extends ModelMapperHandler {

        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            return entityClass;
        }

        @Override
        protected boolean ignoreField(Field field) {
            Id id = field.getAnnotation(Id.class);
            Version version = field.getAnnotation(Version.class);
            return id != null || version != null || super.ignoreField(field);
        }

    }

}
