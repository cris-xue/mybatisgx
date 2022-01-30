package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 15:15
 */
public class MapperHandlerFilterChain {

    private int pos = 0;
    private MapperHandlerFilter[] filters = new MapperHandlerFilter[]{
            new InsertMapperHandlerFilter(),
            new DeleteMapperHandlerFilter(),
            new UpdateMapperHandlerFilter(),
            new QueryMapperHandlerFilter(),

            new WhereMapperHandlerFilter()
    };

    public void chain(MethodNode methodNode, ParseTree parseTree) {
        if (pos < filters.length) {
            filters[pos++].doFilter(methodNode, parseTree, this);
        }
    }

}
