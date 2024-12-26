package com.lc.mybatisx;

import com.lc.mybatisx.ext.MybatisxConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MybatisxProperties
 */
@ConfigurationProperties(prefix = MybatisxProperties.MYBATIS_PREFIX)
public class MybatisxProperties extends MybatisProperties {

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
