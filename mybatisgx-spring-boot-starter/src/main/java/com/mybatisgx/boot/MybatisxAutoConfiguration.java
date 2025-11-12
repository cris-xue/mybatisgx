package com.mybatisgx.boot;

import com.mybatisgx.boot.converter.MetaObjectHandlerConverter;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

/**
 * create by: 薛承城
 * description: CustomMybatisAutoConfiguration是完全使用的spring boot mybatis的源码，仅仅只在98行替换了自定义CustomSqlSessionFactoryBean
 * create time: 2019/5/9 17:54
 */
@Import({MetaObjectHandlerConverter.class, MybatisgxConfiguration.class})
@EnableConfigurationProperties(MybatisxProperties.class)
public class MybatisxAutoConfiguration extends MybatisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisxAutoConfiguration.class);

    public MybatisxAutoConfiguration(MybatisxProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ObjectProvider<TypeHandler[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, typeHandlersProvider, languageDriversProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }

    /*@Override
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactory sqlSessionFactory = super.sqlSessionFactory(dataSource);
        return new MybatisgxSqlSessionFactory(sqlSessionFactory);
    }*/

    /*@Override
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new MybatisxSqlSessionTemplate(sqlSessionFactory);
    }*/
}
