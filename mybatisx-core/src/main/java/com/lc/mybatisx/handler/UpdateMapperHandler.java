package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
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

import javax.persistence.Id;
import javax.persistence.Version;
import java.lang.reflect.*;
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

    private UpdateMapperHandler updateMapperHandler = this;

    private ModelMapperHandler modelMapperHandler;

    private ConditionMapperHandler conditionMapperHandler;

    private List<UpdateSqlWrapper> updateSqlWrapperList;

    public UpdateMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        this.modelMapperHandler = new UpdateModelMapperHandler();

        List<String> parseMethodList = new ArrayList<>();
        parseMethodList.add("updateBy");
        this.conditionMapperHandler = new UpdateConditionMapperHandler(parseMethodList);

        initUpdateSqlWrapper(builderAssistant, namespace);
    }

    private void initUpdateSqlWrapper(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

        List<UpdateSqlWrapper> updateSqlWrapperList = new ArrayList<>();
        Method[] methods = daoInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            UpdateSqlWrapper updateSqlWrapper = this.buildUpdateSqlWrapper(namespace, method, daoInterfaceParams);
            if (updateSqlWrapper == null) {
                continue;
            }
            updateSqlWrapperList.add(updateSqlWrapper);
        }

        this.updateSqlWrapperList = updateSqlWrapperList;
    }

    private UpdateSqlWrapper buildUpdateSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        MapperMethod mapperMethod = method.getAnnotation(MapperMethod.class);
        if (mapperMethod == null || mapperMethod.type() != MethodType.UPDATE) {
            return null;
        }

        UpdateSqlWrapper updateSqlWrapper = (UpdateSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        // 获取方法是否需要动态sql
        updateSqlWrapper.setDynamic(mapperMethod.dynamic());

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        updateSqlWrapper.setModelWrapperList(modelWrapperList);

        // 构建条件包装器
        WhereWrapper whereWrapper = conditionMapperHandler.buildWhereWrapper(method);
        updateSqlWrapper.setWhereWrapper(whereWrapper);

        // 乐观锁
        Field field = ReflectUtils.getField(modelClass, Version.class);
        if (field != null) {
            String javaColumn = field.getName();
            String dbColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn);

            VersionWrapper versionWrapper = new VersionWrapper();
            versionWrapper.setVersion(true);
            versionWrapper.setDbColumn(dbColumn);
            versionWrapper.setJavaColumn(javaColumn);

            updateSqlWrapper.setVersionWrapper(versionWrapper);
        }

        return updateSqlWrapper;
    }

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

    /**
     * 更新条件处理器
     */
    class UpdateConditionMapperHandler extends ConditionMapperHandler {

        public UpdateConditionMapperHandler(List<String> parseMethodList) {
            super(parseMethodList);
        }

        @Override
        protected String getParamName(String methodField, Parameter parameter) {
            if (parameter == null) {
                return null;
            }

            Class<?> parameterClass = null;

            SqlWrapper sqlWrapper = updateMapperHandler.sqlWrapper;
            Type type = parameter.getParameterizedType();
            if (type instanceof TypeVariable<?>) {
                TypeVariable typeVariable = (TypeVariable) type;
                String name = typeVariable.getName();
                if ("ENTITY".equals(name)) {
                    try {
                        parameterClass = Class.forName(sqlWrapper.getParameterType());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                parameterClass = parameter.getType();
            }

            methodField = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodField);
            Field classField = ReflectUtils.getField(parameterClass, methodField);

            return classField.getName();
        }

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
            return id != null || version != null;
        }

    }

}
