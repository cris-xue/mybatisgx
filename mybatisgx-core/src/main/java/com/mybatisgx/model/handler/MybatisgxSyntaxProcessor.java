package com.mybatisgx.model.handler;

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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MybatisgxSyntaxProcessor {

    private final List<MybatisgxSyntaxNewHandler.SyntaxNodeHandler> handlers = Arrays.asList(
            new MybatisgxSyntaxNewHandler.SqlCommandTypeHandler(),
            new MybatisgxSyntaxNewHandler.SelectItemHandler(),
            new MybatisgxSyntaxNewHandler.WhereClauseHandler(),
            new MybatisgxSyntaxNewHandler.OrderByHandler()
    );

    public MybatisgxSyntaxProcessor() {
        this.handlers.stream()
                .sorted(Comparator.comparingInt(MybatisgxSyntaxNewHandler.SyntaxNodeHandler::getOrder))
                .collect(Collectors.toList());
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType, String methodName) {
        ParseTree parseTree = this.parseMethodName(methodName);
        MybatisgxSyntaxNewHandler.ParserContext parserContext = new MybatisgxSyntaxNewHandler.ParserContext(
                entityInfo,
                methodInfo,
                methodParamInfo,
                conditionOriginType,
                methodName
        );
        traverseSyntaxTree(parseTree, parserContext);
    }

    private ParseTree parseMethodName(String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        return methodNameParser.sql_statement();
    }

    private void traverseSyntaxTree(ParseTree node, MybatisgxSyntaxNewHandler.ParserContext context) {
        for (MybatisgxSyntaxNewHandler.SyntaxNodeHandler handler : handlers) {
            if (handler.support(node)) {
                handler.handle(node, context);
                break;
            }
        }

        // 递归处理子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            traverseSyntaxTree(node.getChild(i), context);
        }
    }
}
