package com.lc;

import com.lc.jpa.JPALexer;
import com.lc.jpa.JPAParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.atn.ATN;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

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

        String sql = "SELECT f FROM Country";
        StringReader stringReader = new StringReader(sql);
        UnbufferedCharStream unbufferedCharStream = new UnbufferedCharStream(stringReader);

        JPALexer jpaLexer = new JPALexer(unbufferedCharStream);
        TokenStream tokens = new CommonTokenStream(jpaLexer);
        JPAParser jpaParser = new JPAParser(tokens);
        jpaParser.select_statement();
    }

}
