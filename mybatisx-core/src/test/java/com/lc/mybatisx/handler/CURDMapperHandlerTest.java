package com.lc.mybatisx.handler;

import com.lc.mybatisx.model.handler.CURDMapperHandler;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.Test;

public class CURDMapperHandlerTest {

    @Test
    public void test01() {
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(null, null);

        builderAssistant.setCurrentNamespace("com.lc.mybatisx.handler.dao.UserDao");
        CURDMapperHandler.execute(builderAssistant);
    }

}
