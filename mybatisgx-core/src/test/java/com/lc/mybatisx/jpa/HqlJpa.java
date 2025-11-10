package com.lc.mybatisx.jpa;

import org.junit.Test;

public class HqlJpa {

    @Test
    public void test01() {
        // hql解析组件用的是antlr3    需要针对4做修改
        /*CharStream charStream = CharStreams.fromString("");
        MethodNameLexer methodNameLexer = new HQLLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);*/

        // HQLParser hqlParser = new HQLParser((TokenStream) commonStream);
    }

}
