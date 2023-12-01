package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            "deleteById",
            "updateById",
            "updateByIdSelective",
            "findById",
            "findAll",
            "findList"
    );
    private static InterfaceNodeHandler interfaceNodeHandler = new InterfaceNodeHandler();
    private static MethodNodeHandler methodNodeHandler = new MethodNodeHandler();

    public static void execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        InterfaceNode interfaceNode = interfaceNodeHandler.execute(namespace);
        List<MethodNode> methodNodeList = methodNodeHandler.execute(interfaceNode);
        interfaceNode.setMethodNodeList(methodNodeList);

        for (int i = 0; i < methodNodeList.size(); i++) {
            MethodNode methodNode = methodNodeList.get(i);
            String methodName = methodNode.getName();
            if (simpleMethodList.contains(methodName)) {
                // 生成简单方法
                continue;
            }
            aaa(methodNode.getName());
        }
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

}
