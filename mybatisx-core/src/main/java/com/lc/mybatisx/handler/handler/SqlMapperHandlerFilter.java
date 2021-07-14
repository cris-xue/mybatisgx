package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 15:48
 */
public class SqlMapperHandlerFilter implements MapperHandlerFilter {

    @Override
    public void doFilter(MethodNode methodNode, ParseTree parseTree, MapperHandlerFilterChain filterChain) {
        if (parseTree instanceof MethodNameParser.Sql_statementContext) {

        }

        String me = methodNode.getName();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            parseTree.getChild(i);
        }

        filterChain.chain(methodNode, parseTree);
    }

}
