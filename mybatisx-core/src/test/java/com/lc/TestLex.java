package com.lc;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/4/13 12:08
 */
public class TestLex {

    @Test
    public void test01() throws IOException {
        String s = "query(std::map .find(x) == y): bla";
        ANTLRInputStream input = new ANTLRInputStream();
        TokenStream tokens = new CommonTokenStream(new pqlcLexer(input));

        pqlcParser parser = new pqlcParser(tokens);
        ParseTree tree = parser.query();
        System.out.println(tree.toStringTree());
    }

}
