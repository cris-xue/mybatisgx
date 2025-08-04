package com.lc.mybatisx.context;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MapperTemplateInfo;
import com.lc.mybatisx.model.handler.EntityInfoHandler;
import com.lc.mybatisx.model.handler.MapperInfoHandler;
import com.lc.mybatisx.template.CurdTemplateHandler;
import com.lc.mybatisx.template.RelationSelectTemplateHandler;
import com.lc.mybatisx.template.ResultMapTemplateHandler;
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
public class MybatisxContextLoader {

    private static final Logger logger = LoggerFactory.getLogger(MybatisxContextLoader.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    public void load(String[] entityBasePackages, String[] daoBasePackages, List<Resource> repositoryResourceList) {
        for (String entityBasePackage : entityBasePackages) {
            this.processEntity(entityBasePackage);
        }

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
            Entity entity = clazz.getAnnotation(Entity.class);
            if (entity == null) {
                continue;
            }
            EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
            EntityInfo entityInfo = entityInfoHandler.execute(clazz);
            EntityInfoContextHolder.set(clazz, entityInfo);
        }
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
            MapperInfoHandler mapperInfoHandler = new MapperInfoHandler();
            MapperInfo mapperInfo = mapperInfoHandler.execute(clazz);
            MapperInfoContextHolder.set(clazz, mapperInfo);
            MapperInfoContextHolder.set(mapperInfo.getEntityClass(), mapperInfo);
        }
    }

    private void processTemplate() {
        CurdTemplateHandler curdTemplateHandler = new CurdTemplateHandler();
        ResultMapTemplateHandler resultMapTemplateHandler = new ResultMapTemplateHandler();
        RelationSelectTemplateHandler relationSelectTemplateHandler = new RelationSelectTemplateHandler();
        List<MapperInfo> mapperInfoList = MapperInfoContextHolder.getMapperInfoList();
        mapperInfoList.forEach(mapperInfo -> {
            Map<String, XNode> curdXNodeMap = curdTemplateHandler.execute(mapperInfo);
            Map<String, XNode> resultMapXNodeMap = resultMapTemplateHandler.execute(mapperInfo);
            Map<String, XNode> relationSelectXNodeMap = relationSelectTemplateHandler.execute(mapperInfo);

            MapperTemplateInfo mappingTemplateInfo = new MapperTemplateInfo();
            mappingTemplateInfo.setNamespace(mapperInfo.getNamespace());
            mappingTemplateInfo.setCurdTemplateMap(curdXNodeMap);
            mappingTemplateInfo.setResultMapTemplateMap(resultMapXNodeMap);
            mappingTemplateInfo.setAssociationSelectTemplateMap(relationSelectXNodeMap);
            MapperTemplateContextHolder.set(mappingTemplateInfo);
        });
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
}
