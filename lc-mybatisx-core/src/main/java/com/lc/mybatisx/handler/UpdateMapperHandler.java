package com.lc.mybatisx.handler;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.QueryDao;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.UpdateSqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
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
 * @description：更新处理器
 * @date ：2020/7/5 12:56
 */
public class UpdateMapperHandler extends AbstractMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateMapperHandler.class);

    private ModelMapperHandler modelMapperHandler = new ModelMapperHandler() {
        @Override
        public Class<?> getModelClass(Method method, Class<?> entityClass) {
            return entityClass;
        }
    };
    private ConditionMapperHandler conditionMapperHandler = new ConditionMapperHandler();

    private List<UpdateSqlWrapper> updateSqlWrapperList;

    public UpdateMapperHandler(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface, QueryDao.class);

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

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        updateSqlWrapper.setModelWrapperList(modelWrapperList);

        List<WhereWrapper> whereWrapperList = conditionMapperHandler.buildWhereWrapper(method);
        updateSqlWrapper.setWhereWrapperList(whereWrapperList);

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
        return new UpdateSqlWrapper();
    }
}
