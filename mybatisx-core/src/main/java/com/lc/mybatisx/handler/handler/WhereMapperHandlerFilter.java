package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import org.antlr.v4.runtime.tree.ParseTree;

public class WhereMapperHandlerFilter implements MapperHandlerFilter {

    @Override
    public void doFilter(MethodNode methodNode, ParseTree parseTree, MapperHandlerFilterChain filterChain) {
        System.out.println("条件");
        filterChain.chain(methodNode, parseTree);
    }
}
