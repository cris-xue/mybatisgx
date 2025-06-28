package com.lc.mybatisx;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.context.MapperInfoContextHolder;
import com.lc.mybatisx.context.MapperTemplateContextHolder;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.model.handler.ColumnInfoHandler;
import com.lc.mybatisx.model.handler.EntityInfoHandler;
import com.lc.mybatisx.model.handler.MapperInfoHandler;
import com.lc.mybatisx.template.AssociationSelectTemplateHandler;
import com.lc.mybatisx.template.CurdTemplateHandler;
import com.lc.mybatisx.template.ResultMapSubQueryTemplateHandler;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
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
import java.util.List;
import java.util.Map;

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

            // processEntityAssociation();

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

            processTemplate();

            // 注册bean
            ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addIndexedArgumentValue(0, daoBasePackages);

            GenericBeanDefinition sqlSessionFactoryBeanPostProcessorBeanDefinition = new GenericBeanDefinition();
            sqlSessionFactoryBeanPostProcessorBeanDefinition.setBeanClass(SqlSessionFactoryBeanPostProcessor.class);
            sqlSessionFactoryBeanPostProcessorBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
            registry.registerBeanDefinition("sqlSessionFactoryBeanPostProcessorBeanDefinition", sqlSessionFactoryBeanPostProcessorBeanDefinition);
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
            EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
            EntityInfo entityInfo = entityInfoHandler.execute(clazz);
            EntityInfoContextHolder.set(clazz, entityInfo);
        }
    }

    private void processEntityAssociation() {
        ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
        List<Class<?>> entityClassList = EntityInfoContextHolder.getEntityClassList();
        for (Class<?> entityClass : entityClassList) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            List<ColumnInfo> associationColumnInfoList = entityInfo.getAssociationColumnInfoList();
            associationColumnInfoList.forEach(associationColumnInfo -> {
                List<AssociationTableInfo> associationTableInfoList = columnInfoHandler.getAssociationTableInfoList(associationColumnInfo);
                // entityInfo.setAssociationTableInfoList(associationTableInfoList);
            });
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

    private void processTemplate() {
        CurdTemplateHandler curdTemplateHandler = new CurdTemplateHandler();
        ResultMapSubQueryTemplateHandler resultMapSubQueryTemplateHandler = new ResultMapSubQueryTemplateHandler();
        AssociationSelectTemplateHandler associationSelectTemplateHandler = new AssociationSelectTemplateHandler();
        List<MapperInfo> mapperInfoList = MapperInfoContextHolder.getMapperInfoList();
        mapperInfoList.forEach(mapperInfo -> {
            Map<String, XNode> curdXNodeMap = curdTemplateHandler.execute(mapperInfo);
            Map<String, XNode> resultMapXNodeMap = resultMapSubQueryTemplateHandler.execute(mapperInfo.getResultMapInfoList());
            Map<String, XNode> associationSelectXNodeMap = associationSelectTemplateHandler.execute(mapperInfo);

            MapperTemplateInfo mappingTemplateInfo = new MapperTemplateInfo();
            mappingTemplateInfo.setNamespace(mapperInfo.getNamespace());
            mappingTemplateInfo.setCurdTemplateMap(curdXNodeMap);
            mappingTemplateInfo.setResultMapTemplateMap(resultMapXNodeMap);
            mappingTemplateInfo.setAssociationSelectTemplateMap(associationSelectXNodeMap);
            MapperTemplateContextHolder.set(mappingTemplateInfo);
            logger.info("dao: {} all db operate template: {}", mapperInfo.getNamespace(), mappingTemplateInfo);
        });
    }

    private Map<String, XNode> processAssociationTemplate(MapperInfo mapperInfo) {
        AssociationSelectTemplateHandler associationSelectTemplateHandler = new AssociationSelectTemplateHandler();
        Map<String, XNode> associationSelectXNodeMap = associationSelectTemplateHandler.execute(mapperInfo);
        return associationSelectXNodeMap;

        /*List<AssociationTableInfo> associationTableInfoList = mapperInfo.getEntityInfo().getAssociationColumnInfoList();
        associationTableInfoList.forEach(associationTableInfo -> {
            MapperInfo associationEntityMapperInfo = entityMapperInfoMap.get(associationTableInfo.getJoinEntity());
            List<XNode> xNodeList = daoTemaplteMap.get(associationEntityMapperInfo.getNamespace());
            if (ObjectUtils.isEmpty(xNodeList)) {
                xNodeList = new ArrayList<>();
            }
            List<XNode> associationSelectXNodeList = associationSelectTemplateHandler.execute(associationEntityMapperInfo, null);
            xNodeList.addAll(associationSelectXNodeList);
            daoTemaplteMap.put(associationEntityMapperInfo.getNamespace(), xNodeList);
        });*/
    }
}
