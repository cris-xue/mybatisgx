package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.TableInfo;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import freemarker.template.Template;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class CURDMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(CURDMapperHandler.class);

    private static List<String> simpleMethodList = Arrays.asList(
            "insert",
            "insertSelective",
            "deleteById"
            /*"updateById",
            "updateByIdSelective",
            "findById",
            "findAll",
            "findList"*/
    );
    private static MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private static TableInfoHandler tableInfoHandler = new TableInfoHandler();
    private static MapperMethodInfoHandler mapperMethodInfoHandler = new MapperMethodInfoHandler();

    public static void execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);

        MapperInfo mapperInfo = mapperInfoHandler.execute(daoInterface);
        TableInfo tableInfo = tableInfoHandler.execute(daoInterface);
        List<MethodInfo> methodInfoList = mapperMethodInfoHandler.execute(mapperInfo, daoInterface);

        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            String methodName = methodInfo.getMethodName();
            if (simpleMethodList.contains(methodName)) {
                // 生成简单方法
                readTemplate(mapperInfo, methodInfo, tableInfo);
                continue;
            }
            // aaa(mapperMethodInfo.getMethodName());
        }
    }

    private static Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void aaa(String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        getKeywordMap(null, sqlStatementContext);
    }

    private static void getKeywordMap(Map<Class<ParseTree>, List<String>> aaaa, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

            if (parseTreeChild instanceof TerminalNodeImpl) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                System.out.println(tokens + "----" + simpleName + "---" + parentSimpleName);
            } else {
                getKeywordMap(aaaa, parseTreeChild);
            }
        }
    }

    public static List<XNode> readTemplate(MapperInfo mapperInfo, MethodInfo methodInfo, TableInfo tableInfo) {
        String templatePath = String.format("mapper/mysql/simple_mapper/%s.ftl", methodInfo.getMethodName());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        List<XNode> xNodeList = generateDeleteMethod(template, mapperInfo, methodInfo, tableInfo);
        return xNodeList;
    }

    public static List<XNode> generateDeleteMethod(Template template, MapperInfo mapperInfo, MethodInfo methodInfo, TableInfo tableInfo) {
        List<XNode> deleteXNodeList = new ArrayList<>();
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("mapperInfo", mapperInfo);
        templateData.put("methodInfo", methodInfo);
        templateData.put("tableInfo", tableInfo);

        XPathParser xPathParser = FreeMarkerUtils.processTemplate(templateData, template);
        System.out.println(xPathParser.toString());

        // XNode mapperXNode = xPathParser.evalNode("/mapper");

        // String expression = deleteSqlWrapper.getVersionQuery() ? "select" : "delete";
        // List<XNode> deleteXNode = mapperXNode.evalNodes(expression);
        // deleteXNodeList.addAll(deleteXNode);

        return deleteXNodeList;
    }

}
