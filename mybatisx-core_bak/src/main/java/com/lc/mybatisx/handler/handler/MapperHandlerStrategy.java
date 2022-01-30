package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.syntax.MethodNameParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 16:13
 */
public class MapperHandlerStrategy {

    private static Map<Class<?>, Object> MapperHandler = new HashMap<>();

    static {
        MapperHandler.put(MethodNameParser.Insert_statementContext.class, new WhereMapperHandlerFilter());
        MapperHandler.put(MethodNameParser.Where_clauseContext.class, new WhereMapperHandlerFilter());
    }

}
