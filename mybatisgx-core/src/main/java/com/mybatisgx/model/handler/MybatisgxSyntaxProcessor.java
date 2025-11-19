package com.mybatisgx.model.handler;

import com.mybatisgx.syntax.MethodNameLexer;
import com.mybatisgx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MybatisgxSyntaxProcessor {

    private final List<MybatisgxSyntaxNewHandler.SyntaxNodeHandler> handlers;

    public MybatisgxSyntaxProcessor(List<MybatisgxSyntaxNewHandler.SyntaxNodeHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparingInt(MybatisgxSyntaxNewHandler.SyntaxNodeHandler::getOrder))
                .collect(Collectors.toList());
    }

    public void execute(MybatisgxSyntaxNewHandler.ParserContext context) {
        ParseTree parseTree = parseMethodName(context.getMethodName());
        traverseSyntaxTree(parseTree, context);
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
