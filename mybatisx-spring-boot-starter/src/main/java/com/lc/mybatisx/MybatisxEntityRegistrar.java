package com.lc.mybatisx;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.context.MapperInfoContextHolder;
import com.lc.mybatisx.context.TableInfoContextHolder;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.TableInfo;
import com.lc.mybatisx.model.handler.EntityInfoHandler;
import com.lc.mybatisx.model.handler.MapperInfoHandler;
import com.lc.mybatisx.model.handler.TableInfoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/1/8 18:06
 */
public class MybatisxEntityRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(MybatisxEntityRegistrar.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(MybatisxScan.class.getName()));

        if (annotationAttributes != null) {
            String[] entityBasePackages = (String[]) annotationAttributes.get("entityBasePackages");
            for (String entityBasePackage : entityBasePackages) {
                try {
                    this.processEntity(entityBasePackage);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            String[] daoBasePackages = (String[]) annotationAttributes.get("daoBasePackages");
            for (String daoBasePackage : daoBasePackages) {
                try {
                    this.processDao(daoBasePackage);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void processEntity(String basePackage) throws IOException, ClassNotFoundException {
        basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replace('.', '/') + "/**.class";
        // classpath*:/com/iss/dtg/ftc/dao/**.class
        ClassUtils.convertClassNameToResourcePath(basePackage).concat("/**.class");
        Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(basePackage);

        for (Resource resource : resources) {
            ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
            Class<?> clazz = Class.forName(classMetadata.getClassName());

            Entity entity = clazz.getAnnotation(Entity.class);
            if (entity == null) {
                continue;
            }
            TableInfoHandler tableInfoHandler = new TableInfoHandler();
            TableInfo tableInfo = tableInfoHandler.execute(clazz);
            TableInfoContextHolder.set(clazz, tableInfo);

            EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
            EntityInfo entityInfo = entityInfoHandler.execute(clazz);
            EntityInfoContextHolder.set(clazz, entityInfo);
        }
    }

    private void processDao(String basePackage) throws IOException, ClassNotFoundException {
        basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replace('.', '/') + "/**.class";
        // classpath*:/com/iss/dtg/ftc/dao/**.class
        ClassUtils.convertClassNameToResourcePath(basePackage).concat("/**.class");
        Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(basePackage);

        for (Resource resource : resources) {
            ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
            Class<?> clazz = Class.forName(classMetadata.getClassName());
            Repository repository = clazz.getAnnotation(Repository.class);
            if (repository == null) {
                continue;
            }
            MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
            MapperInfo mapperInfo = mapperInfoHandler.execute(clazz);
            MapperInfoContextHolder.set(clazz, mapperInfo);
            MapperInfoContextHolder.set(mapperInfo.getEntityClass(), mapperInfo);
        }
    }

}
