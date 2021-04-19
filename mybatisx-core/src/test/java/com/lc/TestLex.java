package com.lc;

import antlr.ParseTree;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ToolANTLRLexer;
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
        // https://www.jianshu.com/p/21f2afca65e8

        // https://github.com/antlr/grammars-v4/blob/master/jpa/JPA.g4
        String s = "query(std::map .find(x) == y): bla";
        ANTLRInputStream input = new ANTLRInputStream();
        TokenStream tokens = new CommonTokenStream(new ActionSplitter(input));

        ActionSplitter parser = new ActionSplitter(tokens);
        ParseTree tree = parser.query();
        System.out.println(tree.toStringTree());
    }

}
