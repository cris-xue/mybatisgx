package com.mybatisgx.context;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MapperTemplateInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.MapperInfoHandler;
import com.mybatisgx.template.StatementTemplateHandler;
import com.mybatisgx.template.select.RelationSelectTemplateHandler;
import com.mybatisgx.template.select.ResultMapTemplateHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * mybatisx上下文加载器
 *
 * @author ccxuef
 * @date 2025/7/3 12:17
 */
public class MybatisgxContextLoader {

    private static final Logger logger = LoggerFactory.getLogger(MybatisgxContextLoader.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private static final EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
    private static final MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
    private String[] entityBasePackages;
    private String[] daoBasePackages;
    private List<Resource> repositoryResourceList;
    private StatementTemplateHandler statementTemplateHandler;
    private MybatisgxConfiguration configuration;

    public MybatisgxContextLoader(String[] entityBasePackages, String[] daoBasePackages, List<Resource> repositoryResourceList) {
        this.entityBasePackages = entityBasePackages;
        this.daoBasePackages = daoBasePackages;
        this.repositoryResourceList = repositoryResourceList;
        this.statementTemplateHandler = new StatementTemplateHandler(configuration);
    }

    public void load() {
        for (String entityBasePackage : entityBasePackages) {
            this.processEntity(entityBasePackage);
        }
        this.processEntityRelation();

        List<Resource> totalResourceList = new ArrayList();
        for (String daoBasePackage : daoBasePackages) {
            List<Resource> resourceList = this.getDaoResourceList(daoBasePackage);
            totalResourceList.addAll(resourceList);
        }
        if (ObjectUtils.isNotEmpty(repositoryResourceList)) {
            totalResourceList.addAll(repositoryResourceList);
        }

        this.processDao(totalResourceList);
        this.processTemplate();
    }

    private void processEntity(String basePackage) {
        Resource[] resources = this.getResources(basePackage);
        for (Resource resource : resources) {
            Class<?> clazz = this.getResourceClass(resource);
            this.processEntity(clazz);
        }
    }

    public void processEntityClass(List<Class<?>> clazzList) {
        this.removeEntityInfo();
        for (Class<?> clazz : clazzList) {
            this.processEntity(clazz);
        }
        this.processEntityRelation();
    }

    private void processEntity(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null) {
            return;
        }
        EntityInfo entityInfo = entityInfoHandler.execute(clazz);
        EntityInfoContextHolder.set(clazz, entityInfo);
    }

    private void processEntityRelation() {
        entityInfoHandler.processColumnRelation();
    }

    private List<Resource> getDaoResourceList(String basePackage) {
        Resource[] resources = this.getResources(basePackage);
        List<Resource> resourceList = new ArrayList();
        for (Resource resource : resources) {
            Class<?> clazz = this.getResourceClass(resource);
            Class<?> daoType = Dao.class;
            Mapper mapper = clazz.getAnnotation(Mapper.class);
            if (daoType.isAssignableFrom(clazz) && mapper != null) {
                resourceList.add(resource);
            }
        }
        return resourceList;
    }

    private void processDao(List<Resource> resourceList) {
        for (Resource resource : resourceList) {
            Class<?> clazz = this.getResourceClass(resource);
            this.processDao(clazz);
        }
    }

    public void processDaoClass(List<Class<?>> clazzList) {
        this.removeDaoInfo();
        for (Class<?> clazz : clazzList) {
            this.processDao(clazz);
        }
    }

    private void processDao(Class<?> clazz) {
        MapperInfo mapperInfo = mapperInfoHandler.execute(clazz);
        MapperInfoContextHolder.set(clazz, mapperInfo);
    }

    public void removeEntityInfo() {
        EntityInfoContextHolder.remove();
    }

    public void removeDaoInfo() {
        MapperInfoContextHolder.remove();
        MapperTemplateContextHolder.remove();
        MethodInfoContextHolder.remove();
    }

    public void processTemplate() {
        ResultMapTemplateHandler resultMapTemplateHandler = new ResultMapTemplateHandler();
        RelationSelectTemplateHandler relationSelectTemplateHandler = new RelationSelectTemplateHandler();
        List<MapperInfo> mapperInfoList = MapperInfoContextHolder.getMapperInfoList();
        for (MapperInfo mapperInfo : mapperInfoList) {
            Map<String, XNode> curdXNodeMap = statementTemplateHandler.execute(mapperInfo);
            Map<String, XNode> resultMapXNodeMap = resultMapTemplateHandler.execute(mapperInfo);
            Map<String, XNode> relationSelectXNodeMap = relationSelectTemplateHandler.execute(mapperInfo);

            MapperTemplateInfo mappingTemplateInfo = new MapperTemplateInfo();
            mappingTemplateInfo.setNamespace(mapperInfo.getNamespace());
            mappingTemplateInfo.setCurdTemplateMap(curdXNodeMap);
            mappingTemplateInfo.setResultMapTemplateMap(resultMapXNodeMap);
            mappingTemplateInfo.setAssociationSelectTemplateMap(relationSelectXNodeMap);
            MapperTemplateContextHolder.set(mappingTemplateInfo);
        }
    }

    private Resource[] getResources(String basePackage) {
        try {
            basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replace('.', '/') + "/**.class";
            ClassUtils.convertClassNameToResourcePath(basePackage).concat("/**.class");
            return RESOURCE_PATTERN_RESOLVER.getResources(basePackage);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private Class<?> getResourceClass(Resource resource) {
        try {
            ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
            return Class.forName(classMetadata.getClassName());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void setConfiguration(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
    }
}
