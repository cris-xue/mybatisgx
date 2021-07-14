package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 15:48
 */
public class SqlMapperHandlerFilter implements MapperHandlerFilter {

    @Override
    public void doFilter(MethodNode methodNode, ParseTree parseTree, MapperHandlerFilterChain filterChain) {
        String me = methodNode.getName();
        filterChain.chain(methodNode, parseTree);
    }

}
