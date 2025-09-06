package com.lc.mybatisx;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.context.MapperInfoContextHolder;
import com.lc.mybatisx.ext.MybatisxConfiguration;
import com.lc.mybatisx.ext.MybatisxXMLMapperBuilder;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.template.StatementTemplateHandler;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import freemarker.template.Template;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBeanPostProcessor.class);

    private StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler();

    // @Autowired
    private IdGenerateValueHandler<?> idGenerateValueHandler;

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
            statementTemplateHandler.curdMethod(configuration);

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
        try {
            List<Resource> mapperResourceList = this.getMapper();
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
        List<MapperInfo> mapperInfoList = MapperInfoContextHolder.getMapperInfoList();
        for (MapperInfo mapperInfo : mapperInfoList) {
            ByteArrayInputStream bais = null;
            try {
                String namespace = mapperInfo.getNamespace();
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
}
