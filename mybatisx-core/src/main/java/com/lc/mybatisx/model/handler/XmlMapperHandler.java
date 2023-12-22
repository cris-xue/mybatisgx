package com.lc.mybatisx.model.handler;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class XmlMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(XmlMapperHandler.class);

    private static ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();
    private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();

    private XNode resultMapXNode = null;
    private List<XNode> mapperXNodeList = null;

    public static XmlMapperHandler build() {
        return new XmlMapperHandler();
    }

    public XmlMapperHandler execute(MapperBuilderAssistant builderAssistant) {
        // this.resultMapXNode = resultMapInfoHandler.execute(builderAssistant);
        // this.mapperXNodeList = mapperInfoHandler.execute(builderAssistant);
        return this;
    }

    public void mergeResultMap(List<XNode> resultMapXNode) {
        resultMapXNode.add(this.resultMapXNode);
    }

    public void mergeMapper(List<XNode> curdXNode) {
        curdXNode.addAll(this.mapperXNodeList);
    }

    /*private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private static TableInfoHandler tableInfoHandler = new TableInfoHandler();
    private static MethodInfoHandler methodInfoHandler = new MethodInfoHandler();

    public static XmlMapperHandler build() {
        return new XmlMapperHandler();
    }

    public void execute(MapperBuilderAssistant builderAssistant) {
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
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo, tableInfo);
                xNodeList.add(xNode);
            }
        }
    }

    public void execute(MapperBuilderAssistant builderAssistant, List<XNode> curdXNode) {
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
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo, tableInfo);
                xNodeList.add(xNode);
            }
        }
        curdXNode.addAll(xNodeList);
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

        String methodXml = FreeMarkerUtils.processTemplate(templateData, template);
        XPathParser xPathParser = XmlUtils.processXml(methodXml);
        XNode xNode = xPathParser.evalNode("/mapper/*");

        // String expression = deleteSqlWrapper.getVersionQuery() ? "select" : "delete";
        // List<XNode> deleteXNode = mapperXNode.evalNodes(expression);
        // deleteXNodeList.addAll(deleteXNode);

        return xNode;
    }*/

}
