package com.mybatisgx.model.handler.test;

import com.mybatisgx.dsl.method.model.MgxqlContext;
import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.method.MethodSyntaxProcessor;
import com.mybatisgx.model.handler.test.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Test;

import java.util.List;

public class MethodSyntaxProcessorTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    private MethodSyntaxProcessor buildProcessor() {
        return new MethodSyntaxProcessor();
    }

    @Test
    public void test01() {
        // 测试方法名中存在错误语法
        String methodName = "findCustomTop5ByNameAnd$NameEq$EqOrderByNameDesc";

        MethodSyntaxProcessor processor = this.buildProcessor();
        SqlCommandType sqlCommandType = processor.getSqlCommandType(methodName);

        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityClass(entityInfo.getClazz());
        mapperInfo.setEntityInfo(entityInfo);
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setSqlCommandType(sqlCommandType);
        methodInfo.setMapperInfo(mapperInfo);

        MgxqlContext mgxqlContext = processor.execute(
                entityInfo,
                methodInfo,
                null,
                methodName
        );

        SelectItemType selectItemType = mgxqlContext.getSelectItemType();
        String from = mgxqlContext.getFrom();
        List<String> whereList = mgxqlContext.getWhereList();
        List<String> orderByList = mgxqlContext.getOrderByList();
        List<String> limitList = mgxqlContext.getLimitList();

        String mgxql = String.format(
                "%s %s %s %s %s %s",
                sqlCommandType.name(),
                selectItemType.name(),
                from,
                StringUtils.join(whereList, " "),
                StringUtils.join(orderByList, " "),
                StringUtils.join(limitList, " ")
        );
        System.out.println(mgxql);
    }

    @Test
    public void test02() {
        // 测试方法名中存在错误语法
        MethodSyntaxProcessor processor = this.buildProcessor();
        MgxqlContext mgxqlContext = processor.execute(
                null,
                null,
                null,
                "findCustomTop5ByNameAnd$NameEq$EqOrderByNameDesc"
        );

        // SqlCommandType sqlCommandType = mgxqlContext.getSqlCommandType();
        SelectItemType selectItemType = mgxqlContext.getSelectItemType();
        List<String> whereList = mgxqlContext.getWhereList();
        List<String> orderByList = mgxqlContext.getOrderByList();
        List<String> limitList = mgxqlContext.getLimitList();

        String mgxql = String.format(
                "%s %s %s %s",
                // sqlCommandType.name(),
                selectItemType.name(),
                StringUtils.join(whereList, " "),
                StringUtils.join(orderByList, " "),
                StringUtils.join(limitList, " ")
        );
        System.out.println(mgxql);
    }
}
