package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodNameInfo;
import com.lc.mybatisx.model.TableInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import freemarker.template.Template;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class CURDMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(CURDMapperHandler.class);

    private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private static TableInfoHandler tableInfoHandler = new TableInfoHandler();
    private static MethodInfoHandler methodInfoHandler = new MethodInfoHandler();

    public static List<XNode> execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);

        MapperInfo mapperInfo = mapperInfoHandler.execute(daoInterface);
        TableInfo tableInfo = tableInfoHandler.execute(daoInterface);
        List<MethodInfo> methodInfoList = methodInfoHandler.execute(mapperInfo, tableInfo, daoInterface);

        List<XNode> xNodeList = new ArrayList<>(15);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
            if (methodNameInfo == null) {
                XNode xNode = simpleTemplateHandle(mapperInfo, methodInfo, tableInfo);
                xNodeList.add(xNode);
            } else {
                complexTemplateHandle(mapperInfo, methodInfo, tableInfo);
            }
        }
        return xNodeList;
    }

    private static Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo, TableInfo tableInfo) {
        String templatePath = String.format("mapper/mysql/simple_mapper/%s.ftl", methodInfo.getMethodName());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        return generateSql(template, mapperInfo, methodInfo, tableInfo);
    }

    public static XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo, TableInfo tableInfo) {
        MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
        String templatePath = String.format("mapper/mysql/%s_mapper.ftl", methodNameInfo.getAction());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        return generateSql(template, mapperInfo, methodInfo, tableInfo);
    }

    public static XNode generateSql(Template template, MapperInfo mapperInfo, MethodInfo methodInfo, TableInfo tableInfo) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("mapperInfo", mapperInfo);
        templateData.put("methodInfo", methodInfo);
        templateData.put("tableInfo", tableInfo);

        XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
        System.out.println(xPathParser.toString());

        XNode xNode = xPathParser.evalNode("/mapper/*");

        // String expression = deleteSqlWrapper.getVersionQuery() ? "select" : "delete";
        // List<XNode> deleteXNode = mapperXNode.evalNodes(expression);
        // deleteXNodeList.addAll(deleteXNode);

        return xNode;
    }

}
