package com.lc.mybatisx;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/7 14:05
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisxProperties extends MybatisProperties {

    private String[] daoPackages;

    public String[] getDaoPackages() {
        return daoPackages;
    }

    public void setDaoPackages(String[] daoPackages) {
        this.daoPackages = daoPackages;
    }
}
