package com.mybatisgx.boundary.jointable;

import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.ext.builder.xml.MybatisgxXMLConfigBuilder;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JoinTableTest {

    @Test
    public void test() {
        MybatisgxConfiguration configuration;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mybatis-config.xml");
            MybatisgxXMLConfigBuilder xmlConfigBuilder = new MybatisgxXMLConfigBuilder(classPathResource.getInputStream());
            configuration = (MybatisgxConfiguration) xmlConfigBuilder.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MybatisgxContextLoader mybatisgxContextLoader = new MybatisgxContextLoader(
                new String[]{"com.mybatisgx.boundary.jointable"},
                new String[]{"com.mybatisgx.boundary.jointable"},
                null,
                null,
                configuration
        );
        try {
            mybatisgxContextLoader.load();
        } catch (Exception e) {
            assertEquals("testEntity1List字段为多对多关系，不能直接使用 JoinColumn 或 JoinColumns，请通过 JoinTable 定义关联", e.getMessage());
        }
    }
}
