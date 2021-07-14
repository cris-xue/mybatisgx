package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：薛承城
 * @description：删除处理器
 * @date ：2020/7/5 12:56
 */
public class DeleteMapperHandlerFilter implements MapperHandlerFilter {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMapperHandlerFilter.class);

    @Override
    public void doFilter(MethodNode methodNode, ParseTree parseTree, MapperHandlerFilterChain filterChain) {
        System.out.println("删除");
        filterChain.chain(methodNode, parseTree);
    }

}
