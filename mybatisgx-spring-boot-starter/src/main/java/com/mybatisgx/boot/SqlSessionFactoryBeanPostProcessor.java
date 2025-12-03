package com.mybatisgx.boot;

import com.mybatisgx.api.IdGeneratedValueHandler;
import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBeanPostProcessor.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private final String[] entityBasePackages;
    private final String[] daoBasePackages;
    // @Autowired
    private IdGeneratedValueHandler<?> idGenerateValueHandler;

    public SqlSessionFactoryBeanPostProcessor(String[] entityBasePackages, String[] daoBasePackages) {
        this.entityBasePackages = entityBasePackages;
        this.daoBasePackages = daoBasePackages;
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

                List<Resource> resourceList = this.getDaoResourceList(daoBasePackages);
                MybatisgxContextLoader mybatisgxContextLoader = new MybatisgxContextLoader(entityBasePackages, daoBasePackages, resourceList, mybatisgxConfiguration);
                mybatisgxContextLoader.load();

                StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler((MybatisgxConfiguration) configuration);
                statementTemplateHandler.curdMethod(configuration);
            }
        }
        if (bean instanceof MybatisgxProperties) {
            MybatisgxProperties mybatisxProperties = (MybatisgxProperties) bean;
        }
        if (bean instanceof MybatisgxConfiguration) {
            MybatisgxConfiguration mybatisgxConfiguration = (MybatisgxConfiguration) bean;
        }
        return bean;
    }

    private List<Resource> getDaoResourceList(String[] basePackages) {
        List<Resource> resourceList = new ArrayList();
        try {
            for (String basePackage : basePackages) {
                basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replace('.', '/') + "/**.class";
                ClassUtils.convertClassNameToResourcePath(basePackage).concat("/**.class");
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(basePackage);
                for (Resource resource : resources) {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Class.forName(classMetadata.getClassName());
                    Class<?> daoType = Dao.class;
                    Repository repository = clazz.getAnnotation(Repository.class);
                    if (daoType.isAssignableFrom(clazz) && repository != null) {
                        resourceList.add(resource);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return resourceList;
    }
}
