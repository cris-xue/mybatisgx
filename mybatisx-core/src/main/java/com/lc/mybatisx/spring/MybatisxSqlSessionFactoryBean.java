package com.lc.mybatisx.spring;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.mapper.MybatisxXMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * create by: 薛承城
 * description: CustomSqlSessionFactoryBean完全使用spring mybatis源码，仅仅在494行替换了自己的CustomXMLMapperBuilder
 * create time: 2019/5/9 17:54
 */
public class MybatisxSqlSessionFactoryBean extends SqlSessionFactoryBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisxSqlSessionFactoryBean.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    // private Resource[] mapperLocations;
    private static String[] daoPackages;

    /*@Override
    public void setMapperLocations(Resource[] mapperLocations) {
        super.setMapperLocations(mapperLocations);
        this.mapperLocations = mapperLocations;
    }*/

    public static void setDaoPackages(String[] daoPackages) {
        MybatisxSqlSessionFactoryBean.daoPackages = daoPackages;
    }

    private void curdMethod(Configuration configuration) {
        List<Resource> mapperResourceList;
        try {
            mapperResourceList = getMapper();
            for (Resource mapperResource : mapperResourceList) {
                InputStream is = mapperResource.getInputStream();

                MybatisxXMLMapperBuilder xmlMapperBuilder = new MybatisxXMLMapperBuilder(is,
                        configuration, mapperResource.toString(), configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Resource> getMapper() throws IOException {
        List<Resource> mapperResourceList = new ArrayList<>();

        // 开始扫描dao
        Set<Resource> daoResourceSet = scanClasses(daoPackages, Dao.class);
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
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml.concat("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        xml = xml.concat("<mapper namespace=\"" + namespace + "\">\n");
        xml = xml.concat("</mapper>");

        return xml;
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        SqlSessionFactory sqlSessionFactory = super.getObject();

        Configuration configuration = sqlSessionFactory.getConfiguration();
        curdMethod(configuration);

        return sqlSessionFactory;
    }

    private Set<Resource> scanClasses(String[] packagePatternArray, Class<?> assignableType) throws IOException {
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
                    LOGGER.warn(() -> "Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }
        return classes;
    }

}
