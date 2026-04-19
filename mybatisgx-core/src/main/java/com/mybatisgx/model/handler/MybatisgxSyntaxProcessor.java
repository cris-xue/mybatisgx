package com.mybatisgx.model.handler;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
import com.mybatisgx.syntax.MethodNameLexer;
import com.mybatisgx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.*;
import java.util.stream.Collectors;

public class MybatisgxSyntaxProcessor {

    private final List<MybatisgxSyntaxHandler.SyntaxNodeHandler> handlers = Arrays.asList(
            new MybatisgxSyntaxHandler.SelectItemHandler(),
            new MybatisgxSyntaxHandler.BusinessSemanticHandler(),
            new MybatisgxSyntaxHandler.LimitHandler(),
            new MybatisgxSyntaxHandler.WhereClauseHandler(),
            new MybatisgxSyntaxHandler.OrderByHandler()
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

    public MybatisgxSyntaxProcessor() {
        this.handlers.stream()
                .sorted(Comparator.comparingInt(MybatisgxSyntaxHandler.SyntaxNodeHandler::getOrder))
                .collect(Collectors.toList());
    }

    public SqlCommandType getSqlCommandType(String methodName) {
        for (String key : SQL_COMMAND_TYPE_MAP.keySet()) {
            if (methodName.startsWith(key)) {
                return SQL_COMMAND_TYPE_MAP.get(key);
            }
        }
        /*ParseTree node = this.parseMethodName(methodName, ConditionOriginType.METHOD_NAME);
        for (int i = 0; i < node.getChildCount(); i++) {
            SqlCommandType sqlCommandType = mybatisgxSyntaxHandler.getSqlCommandType(node.getChild(i));
            if (sqlCommandType != null) {
                return sqlCommandType;
            }
        }*/
        throw new MybatisgxException("未知的方法类型：%s", methodName);
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType, String methodName) {
        ParseTree parseTree = this.parseMethodName(methodName);
        MybatisgxSyntaxHandler.ParserContext parserContext = new MybatisgxSyntaxHandler.ParserContext(
                entityInfo,
                methodInfo,
                methodParamInfo,
                conditionOriginType,
                methodName
        );
        this.traverseSyntaxTree(parseTree, parserContext);
    }

    private ParseTree parseMethodName(String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        this.addErrorListeners(methodNameLexer, methodNameParser);
        return methodNameParser.sql_statement();
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

    private void traverseSyntaxTree(ParseTree node, MybatisgxSyntaxHandler.ParserContext context) {
        for (MybatisgxSyntaxHandler.SyntaxNodeHandler handler : handlers) {
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
