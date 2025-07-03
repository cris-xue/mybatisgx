package com.lc.mybatisx;

import com.lc.mybatisx.context.MybatisxContextLoader;
import com.lc.mybatisx.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/1/8 18:06
 */
public class MybatisxRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(MybatisxRegistrar.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(MybatisxScan.class.getName()));
        MybatisxContextLoader mybatisxContextLoader = new MybatisxContextLoader();
        if (annotationAttributes != null) {
            String[] entityBasePackages = (String[]) annotationAttributes.get("entityBasePackages");
            String[] daoBasePackages = (String[]) annotationAttributes.get("daoBasePackages");
            List<Resource> resourceList = this.getDaoResourceList(daoBasePackages);
            mybatisxContextLoader.load(entityBasePackages, daoBasePackages, resourceList);

            GenericBeanDefinition sqlSessionFactoryBeanPostProcessorBeanDefinition = new GenericBeanDefinition();
            sqlSessionFactoryBeanPostProcessorBeanDefinition.setBeanClass(SqlSessionFactoryBeanPostProcessor.class);
            registry.registerBeanDefinition("sqlSessionFactoryBeanPostProcessorBeanDefinition", sqlSessionFactoryBeanPostProcessorBeanDefinition);
        }
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
            logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return resourceList;
    }
}
