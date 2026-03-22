package com.mybatisgx.boot;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MybatisxProperties
 */
@ConfigurationProperties(prefix = MybatisgxProperties.MYBATIS_PREFIX)
public class MybatisgxProperties extends MybatisProperties {

    public static final String MYBATIS_PREFIX = "mybatisgx";

    @NestedConfigurationProperty
    private MybatisgxConfiguration configuration = new MybatisgxConfiguration();

    @Override
    public MybatisgxConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
        super.setConfiguration(configuration);
    }
}
