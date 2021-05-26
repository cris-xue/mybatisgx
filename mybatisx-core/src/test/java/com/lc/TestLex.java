package com.lc;

import com.lc.mybatisx.syntax.*;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.Test;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/4/13 12:08
 */
public class TestLex {

    @Test
    public void test01() throws IOException {
        // https://www.jianshu.com/p/21f2afca65e8

        // https://github.com/antlr/grammars-v4/blob/master/jpa/JPA.g4
        String s = "query(std::map .find(x) == y): bla";
        ANTLRInputStream input = new ANTLRInputStream();
        // TokenStream tokens = new CommonTokenStream(new ActionSplitter(input));

        /*ActionSplitter parser = new ActionSplitter(tokens);
        ParseTree tree = parser.query();
        System.out.println(tree.toStringTree());*/
    }

    @Test
    public void testJpa() {
        // CodePointBuffer codePointBuffer = CodePointBuffer.builder(1024).build();
        // CodePointCharStream codePointCharStream = CodePointCharStream.fromBuffer(codePointBuffer);

        // StringReader stringReader = new StringReader(jpaMethodName);
        // UnbufferedCharStream unbufferedCharStream = new UnbufferedCharStream(stringReader);

        String jpaMethodName = "findByIdAndName";
        CodePointBuffer.withChars(CharBuffer.wrap(jpaMethodName));
        CodePointBuffer codePointBuffer = CodePointBuffer.builder(1024).build();
        CodePointCharStream codePointCharStream = CodePointCharStream.fromBuffer(codePointBuffer);

        MethodNameLexer methodNameLexer = new MethodNameLexer(codePointCharStream);
        TokenStream tokens = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(tokens);

        // new MethodNameParser.Ql_statementContext();
        ParseTree aaaa = methodNameParser.ql_statement();
        String a = methodNameParser.ql_statement().getPayload().getText();

        methodNameParser.select_clause().getPayload();

        System.out.println("asdf");
    }

    @Test
    public void test03() throws IOException {
        String jpaMethodName = "findByIdAndName";
        CodePointBuffer.withChars(CharBuffer.wrap(jpaMethodName));
        CodePointBuffer codePointBuffer = CodePointBuffer.builder(1024).build();
        CodePointCharStream codePointCharStream = CodePointCharStream.fromBuffer(codePointBuffer);

        MethodNameLexer methodNameLexer = new MethodNameLexer(codePointCharStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        MethodNameParser.Select_statementContext aaaa = methodNameParser.select_statement();
        System.out.println(aaaa.toStringTree(methodNameParser));

        MethodNameListener listener = new MethodNameBaseListener();
        ParseTreeWalker.DEFAULT.walk(listener, aaaa);

        /*MethodNameBaseListener methodNameBaseListener = new MethodNameBaseListener();
        methodNameParser.ql_statement().enterRule(methodNameBaseListener);*/
        // MethodNameBaseVisitor methodNameBaseVisitor = new MethodNameBaseVisitor();
        // methodNameParser.select_clause().accept(methodNameBaseVisitor);

        System.out.println("adfads");

        /*new MethodNameBaseListener().p
        SomeClass someClass = new MethodNameBaseListener().parse(parser.classDeclaration());
        Gson gson = new Gson();
        System.out.println(gson.toJson(someClass));
        System.out.println("======================================\n visitor方式");
        // visitor方式遍历
        SomeLanguageParser parser2 = getParseTree(code);
        SomeClass someClass1 = new SomeLangVisitorParser().parse(parser2.classDeclaration());
        System.out.println(gson.toJson(someClass1));*/
    }

    @Test
    public void test05() {
        CharStream input = CharStreams.fromString("findByIdLtAndNameEqGroupByIdOrderByNameAscIdDesc");
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        MethodNameVisitor methodNameVisitor = new MethodNameBaseVisitor();

        ParseTree qlStatementContext = methodNameParser.ql_statement();
        aaa(null, qlStatementContext);
        System.out.println("aaaaaa");
    }

    private void aaa(Map<Class<ParseTree>, List<String>> aaaa, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();

            if (parseTreeChild instanceof TerminalNodeImpl) {
                System.out.println(tokens + "----" + parseTreeChild.getClass().getName());
            }
            aaa(aaaa, parseTreeChild);
        }
    }

}
