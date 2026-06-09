package com.mybatisgx.dsl.method;

import com.mybatisgx.dsl.method.model.BaseStatement;
import com.mybatisgx.dsl.test.entity.User;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Test;

public class MethodSyntaxProcessorTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    private MethodSyntaxProcessor buildProcessor() {
        return new MethodSyntaxProcessor();
    }

    @Test
    public void testInsert01() {
        String methodName = "insert";

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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }

    @Test
    public void testDelete01() {
        String methodName = "deleteById";

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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }

    @Test
    public void testUpdate01() {
        String methodName = "updateById";

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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }

    @Test
    public void testUpdate02() {
        String methodName = "updateByIdAndStatus";

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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }

    @Test
    public void testSelect01() {
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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }

    @Test
    public void testCount01() {
        String methodName = "countByNameAnd$NameEq$EqOrNameLike";
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

        BaseStatement baseStatement = processor.execute(methodInfo);
        System.out.println(baseStatement.toMgxql());
    }
}
