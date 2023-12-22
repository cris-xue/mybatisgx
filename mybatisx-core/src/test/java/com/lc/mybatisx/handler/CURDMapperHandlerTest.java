package com.lc.mybatisx.handler;

import com.lc.mybatisx.model.handler.XmlMapperHandler;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class CURDMapperHandlerTest {

    @Test
    public void test01() {
        MapperBuilderAssistant mapperBuilderAssistant = Mockito.mock(MapperBuilderAssistant.class);
        Mockito.when(mapperBuilderAssistant.getCurrentNamespace()).thenReturn("com.lc.mybatisx.handler.dao.UserDao");
        // mapperBuilderAssistant.setCurrentNamespace("com.lc.mybatisx.handler.dao.UserDao");
        XmlMapperHandler.build().execute(mapperBuilderAssistant, new ArrayList<>());
    }

}
