package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.parse.KeywordParse;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.InsertSqlWrapper;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.VersionWrapper;
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
 * @description：一句话描述
 * @date ：2020/7/5 12:56
 */
public class InsertMapperHandler extends AbstractMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertMapperHandler.class);

    private ModelMapperHandler modelMapperHandler;
    private List<InsertSqlWrapper> insertSqlWrapperList;

    public InsertMapperHandler() {
        this.modelMapperHandler = new InsertModelMapperHandler();
        insertSqlWrapperList = new ArrayList<>();
    }

    @Override
    public void init(String namespace, Method method, Type[] daoInterfaceParams) {
        build(namespace, method, daoInterfaceParams);
    }

    private void build(String namespace, Method method, Type[] daoInterfaceParams) {
        InsertSqlWrapper insertSqlWrapper = (InsertSqlWrapper) this.buildSqlWrapper(namespace, method, daoInterfaceParams);

        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> modelClass = modelMapperHandler.getModelClass(method, entityClass);
        List<ModelWrapper> modelWrapperList = modelMapperHandler.buildModelWrapper(modelClass);
        insertSqlWrapper.setModelWrapperList(modelWrapperList);

        List<String> methodKeywordList = KeywordParse.parseMethod(method);
        boolean dynamic = KeywordParse.isDynamic(methodKeywordList);
        insertSqlWrapper.setDynamic(dynamic);

        // 乐观锁
        Field field = ReflectUtils.getField(modelClass, Version.class);
        if (field != null) {
            String javaColumn = field.getName();
            String dbColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn);

            VersionWrapper versionWrapper = new VersionWrapper();
            versionWrapper.setDbColumn(dbColumn);
            versionWrapper.setJavaColumn(javaColumn);

            insertSqlWrapper.setVersionWrapper(versionWrapper);
        }

        this.insertSqlWrapperList.add(insertSqlWrapper);
    }

    @Override
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

    class InsertModelMapperHandler extends ModelMapperHandler {

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
