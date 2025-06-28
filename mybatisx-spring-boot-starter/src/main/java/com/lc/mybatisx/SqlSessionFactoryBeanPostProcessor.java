package com.lc.mybatisx;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.ext.MybatisxConfiguration;
import com.lc.mybatisx.ext.MybatisxXMLMapperBuilder;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import freemarker.template.Template;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBeanPostProcessor.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private String[] packagePatternArray;

    // @Autowired
    private IdGenerateValueHandler<?> idGenerateValueHandler;

    public SqlSessionFactoryBeanPostProcessor(String[] packagePatternArray) {
        this.packagePatternArray = packagePatternArray;
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
            curdMethod(configuration);

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

    private void curdMethod(Configuration configuration) {
        List<Resource> mapperResourceList;
        try {
            mapperResourceList = getMapper();
            for (Resource mapperResource : mapperResourceList) {
                InputStream is = null;
                try {
                    is = mapperResource.getInputStream();
                    MybatisxXMLMapperBuilder xmlMapperBuilder = new MybatisxXMLMapperBuilder(is,
                            configuration, mapperResource.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Resource> getMapper() throws IOException {
        List<Resource> mapperResourceList = new ArrayList<>();

        // 开始扫描dao
        Set<Resource> daoResourceSet = scanClasses(Dao.class);
        for (Resource daoResource : daoResourceSet) {
            ByteArrayInputStream bais = null;
            try {
                ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(daoResource).getClassMetadata();
                String namespace = classMetadata.getClassName();
                String mapperXml = createMapperXml(namespace);

                bais = new ByteArrayInputStream(mapperXml.getBytes());
                Resource resource = new InputStreamResource(bais, namespace);
                mapperResourceList.add(resource);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bais != null) {
                    bais.close();
                }
            }
        }

        return mapperResourceList;
    }

    private String createMapperXml(String namespace) {
        Template template = FreeMarkerUtils.getTemplate("mapper/base.ftl");
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("namespace", namespace);
        String baseXml = FreeMarkerUtils.processTemplate(templateData, template);
        return baseXml;
    }

    private Set<Resource> scanClasses(Class<?> assignableType) throws IOException {
        Set<Resource> classes = new HashSet<>();
        for (String packagePattern : packagePatternArray) {
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(resource);
                    }
                } catch (Throwable e) {
                    LOGGER.warn("Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }
        return classes;
    }

}
