package com.mybatisgx.boot;

import com.mybatisgx.ext.MybatisxConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MybatisxProperties
 */
@ConfigurationProperties(prefix = MybatisgxProperties.MYBATIS_PREFIX)
public class MybatisgxProperties extends MybatisProperties {

    public static final String MYBATIS_PREFIX = "mybatisx";

    @NestedConfigurationProperty
    private MybatisxConfiguration configuration = new MybatisxConfiguration();

    @Override
    public MybatisxConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MybatisxConfiguration configuration) {
        this.configuration = configuration;
        super.setConfiguration(configuration);
    }
}
