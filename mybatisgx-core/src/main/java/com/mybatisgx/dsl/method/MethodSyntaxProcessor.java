package com.mybatisgx.dsl.method;

import com.mybatisgx.dsl.method.model.MethodStatement;
import com.mybatisgx.dsl.method.syntax.MethodNameLexer;
import com.mybatisgx.dsl.method.syntax.MethodNameParser;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
import com.mybatisgx.model.handler.MethodSyntaxErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.*;
import java.util.stream.Collectors;

public class MethodSyntaxProcessor {

    private final List<MethodSyntaxHandler.SyntaxNodeHandler> handlers = Arrays.asList(
            // new MethodSyntaxHandler.SelectStatementHandler(),
            new MethodSyntaxHandler.SelectItemHandler(),
            new MethodSyntaxHandler.SelectFromHandler(),
            new MethodSyntaxHandler.BusinessSemanticHandler(),
            new MethodSyntaxHandler.LimitHandler(),
            new MethodSyntaxHandler.WhereClauseHandler(),
            new MethodSyntaxHandler.OrderByHandler()
    );
    private final static Map<String, SqlCommandType> SQL_COMMAND_TYPE_MAP = new HashMap<>();

    static {
        SQL_COMMAND_TYPE_MAP.put("insert", SqlCommandType.INSERT);
        SQL_COMMAND_TYPE_MAP.put("add", SqlCommandType.INSERT);
        SQL_COMMAND_TYPE_MAP.put("delete", SqlCommandType.DELETE);
        SQL_COMMAND_TYPE_MAP.put("remove", SqlCommandType.DELETE);
        SQL_COMMAND_TYPE_MAP.put("update", SqlCommandType.UPDATE);
        SQL_COMMAND_TYPE_MAP.put("modify", SqlCommandType.UPDATE);
        SQL_COMMAND_TYPE_MAP.put("find", SqlCommandType.SELECT);
        SQL_COMMAND_TYPE_MAP.put("get", SqlCommandType.SELECT);
        SQL_COMMAND_TYPE_MAP.put("select", SqlCommandType.SELECT);
        SQL_COMMAND_TYPE_MAP.put("query", SqlCommandType.SELECT);
        SQL_COMMAND_TYPE_MAP.put("count", SqlCommandType.SELECT);
    }

    public MethodSyntaxProcessor() {
        this.handlers.stream()
                .sorted(Comparator.comparingInt(MethodSyntaxHandler.SyntaxNodeHandler::getOrder))
                .collect(Collectors.toList());
    }

    public SqlCommandType getSqlCommandType(String methodName) {
        for (String key : SQL_COMMAND_TYPE_MAP.keySet()) {
            if (methodName.startsWith(key)) {
                return SQL_COMMAND_TYPE_MAP.get(key);
            }
        }
        throw new MybatisgxException("未知的方法类型：%s", methodName);
    }

    public MethodStatement execute(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo) {
        String methodName = methodInfo.getMethodName();
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(tokens);
        this.addErrorListeners(methodNameLexer, methodNameParser);
        ParseTree parseTree = methodNameParser.sql_statement();

        MethodStatement methodStatement = new MethodStatement();
        methodStatement.setSqlCommandType(methodInfo.getSqlCommandType());
        MethodSyntaxHandler.ParserContext parserContext = new MethodSyntaxHandler.ParserContext(
                tokens,
                parseTree,
                entityInfo,
                methodStatement,
                methodInfo,
                methodParamInfo,
                null,
                methodName
        );
        this.traverseSyntaxTree(parseTree, parserContext);
        return methodStatement;
    }

    /**
     * 添加错误监听
     *
     * @param methodNameLexer
     * @param methodNameParser
     */
    private void addErrorListeners(MethodNameLexer methodNameLexer, MethodNameParser methodNameParser) {
        methodNameLexer.removeErrorListeners();
        methodNameParser.removeErrorListeners();
        MethodSyntaxErrorListener methodSyntaxErrorListener = new MethodSyntaxErrorListener();
        methodNameLexer.addErrorListener(methodSyntaxErrorListener);
        methodNameParser.addErrorListener(methodSyntaxErrorListener);
    }

    private void traverseSyntaxTree(ParseTree node, MethodSyntaxHandler.ParserContext context) {
        for (MethodSyntaxHandler.SyntaxNodeHandler handler : handlers) {
            if (handler.support(node)) {
                handler.handle(node, context);
                break;
            }
        }
        // 递归处理子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            this.traverseSyntaxTree(node.getChild(i), context);
        }
    }
}
