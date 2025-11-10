package com.mybatisgx;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/7 10:27
 */
public class MybatisxExcludeAutoConfigFilter implements AutoConfigurationImportFilter, EnvironmentAware {

    private static Logger logger = LoggerFactory.getLogger(MybatisxExcludeAutoConfigFilter.class);

    private final Set<String> EXCLUDE_AUTOCONFIG = new HashSet<>(
            Arrays.asList(MybatisAutoConfiguration.class.getName())
    );

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = !EXCLUDE_AUTOCONFIG.contains(autoConfigurationClasses[i]);
        }
        return matches;
    }

    @Override
    public void setEnvironment(Environment environment) {

    }

}
