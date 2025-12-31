package com.mybatisgx.template;

import com.mybatisgx.context.MapperInfoContextHolder;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.ext.builder.xml.MybatisgxXMLMapperBuilder;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.template.select.SelectTemplateHandler;
import com.mybatisgx.utils.XmlUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
        for (MethodInfo methodInfo : methodInfoList) {
            SqlCommandType sqlCommandType = methodInfo.getSqlCommandType();
            if (sqlCommandType != null) {
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo);
                xNodeMap.put(methodInfo.getMethodName(), xNode);
            }
        }
        return xNodeMap;
    }

    private XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String xmlString;
        if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT) {
            SelectTemplateHandler selectTemplateHandler = MybatisgxObjectFactory.get(SelectTemplateHandler.class);
            xmlString = selectTemplateHandler.execute(mapperInfo, methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            InsertTemplateHandler insertTemplateHandler = MybatisgxObjectFactory.get(InsertTemplateHandler.class);
            xmlString = insertTemplateHandler.execute(mapperInfo, methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.DELETE) {
            DeleteTemplateHandler deleteTemplateHandler = MybatisgxObjectFactory.get(DeleteTemplateHandler.class);
            xmlString = deleteTemplateHandler.execute(mapperInfo, methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
            UpdateTemplateHandler updateTemplateHandler = MybatisgxObjectFactory.get(UpdateTemplateHandler.class);
            xmlString = updateTemplateHandler.execute(mapperInfo, methodInfo);
        } else {
            throw new MybatisgxException("不存在的操作方式");
        }
        logger.debug("{}:\n{}", methodInfo.getMethodName(), xmlString);
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
        Document document = DocumentHelper.createDocument();
        document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        Element element = document.addElement("mapper");
        element.addAttribute("namespace", namespace);
        return document.asXML();
    }
}
