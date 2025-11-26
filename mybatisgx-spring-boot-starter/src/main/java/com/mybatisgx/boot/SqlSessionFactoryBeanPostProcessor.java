package com.mybatisgx.boot;

import com.mybatisgx.annotation.handler.IdGenerateValueHandler;
import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBeanPostProcessor.class);

    private MybatisgxContextLoader mybatisgxContextLoader;
    // @Autowired
    private IdGenerateValueHandler<?> idGenerateValueHandler;

    public SqlSessionFactoryBeanPostProcessor(MybatisgxContextLoader mybatisgxContextLoader) {
        this.mybatisgxContextLoader = mybatisgxContextLoader;
    }

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
            if (configuration instanceof MybatisgxConfiguration) {
                MybatisgxConfiguration mybatisgxConfiguration = (MybatisgxConfiguration) configuration;
                mybatisgxConfiguration.setIdGenerateValueHandler(idGenerateValueHandler);
            }

            mybatisgxContextLoader.load();

            StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler((MybatisgxConfiguration) configuration);
            statementTemplateHandler.curdMethod(configuration);
        }
        if (bean instanceof MybatisgxProperties) {
            MybatisgxProperties mybatisxProperties = (MybatisgxProperties) bean;
        }
        if (bean instanceof MybatisgxConfiguration) {
            MybatisgxConfiguration mybatisgxConfiguration = (MybatisgxConfiguration) bean;
        }
        return bean;
    }
}
