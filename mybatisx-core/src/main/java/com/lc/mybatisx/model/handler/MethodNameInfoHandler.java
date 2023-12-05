package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.List;

public class MethodNameInfoHandler {

    public List<String> execute(String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        List<String> tokens = new ArrayList<>();
        getTokens(tokens, sqlStatementContext);
        return tokens;
    }

    private void getTokens(List<String> tokens, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

            if (parseTreeChild instanceof TerminalNodeImpl) {
                tokens.add(token);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                tokens.add(token);
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                getTokens(tokens, parseTreeChild);
            }
        }
    }

}
