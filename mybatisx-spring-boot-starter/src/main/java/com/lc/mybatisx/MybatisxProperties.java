package com.lc.mybatisx;

import com.lc.mybatisx.session.MybatisxConfiguration;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MybatisxProperties
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisxProperties extends MybatisProperties {

    @NestedConfigurationProperty
    private MybatisxConfiguration configuration;

    private String[] daoPackages;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MybatisxConfiguration configuration) {
        this.configuration = configuration;
    }

    public String[] getDaoPackages() {
        return daoPackages;
    }

    public void setDaoPackages(String[] daoPackages) {
        this.daoPackages = daoPackages;
    }

}
