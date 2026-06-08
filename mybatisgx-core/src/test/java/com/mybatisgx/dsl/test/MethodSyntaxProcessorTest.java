package com.mybatisgx.dsl.test;

import com.mybatisgx.dsl.method.MethodSyntaxProcessor;
import com.mybatisgx.dsl.method.model.MethodStatement;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.test.entity.User;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Test;

public class MethodSyntaxProcessorTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    private MethodSyntaxProcessor buildProcessor() {
        return new MethodSyntaxProcessor();
    }

    @Test
    public void test01() {
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
        methodInfo.setMethodName(methodName);

        MethodStatement methodStatement = processor.execute(
                entityInfo,
                methodInfo,
                null
        );
        System.out.println(methodStatement.toMgxql());
    }

    @Test
    public void test02() {
        String methodName = "findCustomTop5ByNameAnd$NameEq$EqOrNameLikeOrderByNameDesc";
        MethodSyntaxProcessor processor = this.buildProcessor();
        SqlCommandType sqlCommandType = processor.getSqlCommandType(methodName);

        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityClass(entityInfo.getClazz());
        mapperInfo.setEntityInfo(entityInfo);

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setSqlCommandType(sqlCommandType);
        methodInfo.setMapperInfo(mapperInfo);
        methodInfo.setMethodName(methodName);

        MethodStatement methodStatement = processor.execute(
                null,
                methodInfo,
                null
        );
        System.out.println(methodStatement.toMgxql());
    }
}
