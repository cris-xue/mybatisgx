package com.mybatisgx.boot;

import com.mybatisgx.annotation.handler.IdGenerateValueHandler;
import com.mybatisgx.ext.MybatisxConfiguration;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBeanPostProcessor.class);

    private StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler();

    // @Autowired
    private IdGenerateValueHandler<?> idGenerateValueHandler;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            LOGGER.info("SqlSessionFactoryBean 初始化前的逻辑");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            LOGGER.info("SqlSessionFactoryBean 初始化完成");
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;
            Configuration configuration = sqlSessionFactory.getConfiguration();
            statementTemplateHandler.curdMethod(configuration);

            if (configuration instanceof MybatisxConfiguration) {
                MybatisxConfiguration mybatisxConfiguration = (MybatisxConfiguration) configuration;
                mybatisxConfiguration.setIdGenerateValueHandler(idGenerateValueHandler);
            }
        }
        if (bean instanceof MybatisxProperties) {
            MybatisxProperties mybatisxProperties = (MybatisxProperties) bean;
        }
        if (bean instanceof MybatisxConfiguration) {
            MybatisxConfiguration mybatisxConfiguration = (MybatisxConfiguration) bean;
        }
        return bean;
    }
}
