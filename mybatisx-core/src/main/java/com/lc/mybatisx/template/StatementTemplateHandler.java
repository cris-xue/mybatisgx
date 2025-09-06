package com.lc.mybatisx.template;

import com.lc.mybatisx.context.MapperInfoContextHolder;
import com.lc.mybatisx.ext.MybatisgxXMLMapperBuilder;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.template.select.SelectTemplateHandler;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatementTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        List<MethodInfo> methodInfoList = mapperInfo.getMethodInfoList();
        Map<String, XNode> xNodeMap = new HashMap(15);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            String action = methodInfo.getAction();
            if (StringUtils.isNotBlank(action)) {
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo);
                xNodeMap.put(methodInfo.getMethodName(), xNode);
            }
        }
        return xNodeMap;
    }

    private XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String xmlString;
        if ("select".equals(methodInfo.getAction())) {
            SelectTemplateHandler selectTemplateHandler = new SelectTemplateHandler();
            xmlString = selectTemplateHandler.execute(mapperInfo, methodInfo);
        } else if ("insert".equals(methodInfo.getAction())) {
            InsertTemplateHandler insertTemplateHandler = new InsertTemplateHandler();
            xmlString = insertTemplateHandler.execute(mapperInfo, methodInfo);
        } else if ("delete".equals(methodInfo.getAction())) {
            DeleteTemplateHandler deleteTemplateHandler = new DeleteTemplateHandler();
            xmlString = deleteTemplateHandler.execute(mapperInfo, methodInfo);
        } else if ("update".equals(methodInfo.getAction())) {
            UpdateTemplateHandler updateTemplateHandler = new UpdateTemplateHandler();
            xmlString = updateTemplateHandler.execute(mapperInfo, methodInfo);
        } else {
            throw new RuntimeException("不存在的操作方式");
        }
        logger.info("{}:\n{}", methodInfo.getMethodName(), xmlString);
        XPathParser xPathParser = XmlUtils.processXml(xmlString);
        return xPathParser.evalNode("/mapper/select|/mapper/insert|/mapper/delete|/mapper/update");
    }

    public void curdMethod(Configuration configuration) {
        try {
            List<Resource> mapperResourceList = this.getMapper();
            for (Resource mapperResource : mapperResourceList) {
                InputStream inputStream = null;
                try {
                    inputStream = mapperResource.getInputStream();
                    MybatisgxXMLMapperBuilder xmlMapperBuilder = new MybatisgxXMLMapperBuilder(
                            inputStream, configuration, mapperResource.toString(), configuration.getSqlFragments()
                    );
                    xmlMapperBuilder.parse();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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
                logger.error(e.getMessage(), e);
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
        return FreeMarkerUtils.processTemplate(templateData, template);
    }
}
