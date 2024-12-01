package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ManyToManyInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.TableInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurdTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(CurdTemplateHandler.class);

    public List<XNode> execute(MapperInfo mapperInfo) {
        List<MethodInfo> methodInfoList = mapperInfo.getMethodInfoList();
        List<XNode> xNodeList = new ArrayList<>(15);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            String action = methodInfo.getAction();
            if (StringUtils.isBlank(action)) {
                XNode xNode = simpleTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            } else {
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            }
        }

        /*TableInfo tableInfo = mapperInfo.getTableInfo();
        List<ManyToManyInfo> manyToManyInfoList = tableInfo.getAssociationTableInfoList();
        manyToManyInfoList.forEach(manyToManyInfo -> {
            SelectTemplateHandler selectTemplateHandler = new SelectTemplateHandler();
            return selectTemplateHandler.execute(mapperInfo, methodInfo);
        });*/
        return xNodeList;
    }

    private XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String methodName = methodInfo.getMethodName();
        if ("insert".equals(methodName) || "insertSelective".equals(methodName)) {
            InsertTemplateHandler insertTemplateHandler = new InsertTemplateHandler();
            return insertTemplateHandler.execute(mapperInfo, methodInfo);
        } else if ("updateById".equals(methodName) || "updateByIdSelective".equals(methodName)) {
            UpdateTemplateHandler updateTemplateHandler = new UpdateTemplateHandler();
            return updateTemplateHandler.execute(mapperInfo, methodInfo);
        } else {
            String templatePath = String.format("mapper/mysql/simple_mapper/%s.ftl", methodInfo.getMethodName());
            Template template = FreeMarkerUtils.getTemplate(templatePath);
            return generateSql(template, mapperInfo, methodInfo);
        }
    }

    private XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        if ("select".equals(methodInfo.getAction())) {
            SelectTemplateHandler selectTemplateHandler = new SelectTemplateHandler();
            return selectTemplateHandler.execute(mapperInfo, methodInfo);
        }
        if ("insert".equals(methodInfo.getAction())) {
            InsertTemplateHandler insertTemplateHandler = new InsertTemplateHandler();
            return insertTemplateHandler.execute(mapperInfo, methodInfo);
        }
        if ("delete".equals(methodInfo.getAction())) {
            DeleteTemplateHandler deleteTemplateHandler = new DeleteTemplateHandler();
            return deleteTemplateHandler.execute(mapperInfo, methodInfo);
        }
        if ("update".equals(methodInfo.getAction())) {
            UpdateTemplateHandler updateTemplateHandler = new UpdateTemplateHandler();
            return updateTemplateHandler.execute(mapperInfo, methodInfo);
        }
        throw new RuntimeException("不存在的操作方式");
    }

    private XNode generateSql(Template template, MapperInfo mapperInfo, MethodInfo methodInfo) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("mapperInfo", mapperInfo);
        templateData.put("methodInfo", methodInfo);
        templateData.put("resultMapInfo", methodInfo.getResultMapInfo());

        String methodXml = FreeMarkerUtils.processTemplate(templateData, template);
        XPathParser xPathParser = XmlUtils.processXml(methodXml);
        XNode xNode = xPathParser.evalNode("/mapper/*");
        return xNode;
    }

}
