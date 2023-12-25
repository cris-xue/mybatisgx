package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodNameInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurdTemplateHandler {

    public List<XNode> execute(MapperInfo mapperInfo) {
        List<MethodInfo> methodInfoList = mapperInfo.getMethodInfoList();
        List<XNode> xNodeList = new ArrayList<>(15);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
            if (methodNameInfo == null) {
                XNode xNode = simpleTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            } else {
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            }
        }
        return xNodeList;
    }

    private XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String templatePath = String.format("mapper/mysql/simple_mapper/%s.ftl", methodInfo.getMethodName());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        return generateSql(template, mapperInfo, methodInfo);
    }

    private XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
        String path = methodInfo.getDynamic() ? "mapper/mysql/%s_mapper_dynamic.ftl" : "mapper/mysql/%s_mapper.ftl";
        String templatePath = String.format(path, methodNameInfo.getAction());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        return generateSql(template, mapperInfo, methodInfo);
    }

    private XNode generateSql(Template template, MapperInfo mapperInfo, MethodInfo methodInfo) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("mapperInfo", mapperInfo);
        templateData.put("methodInfo", methodInfo);
        templateData.put("resultMapInfo", mapperInfo.getResultMapInfo());

        String methodXml = FreeMarkerUtils.processTemplate(templateData, template);
        XPathParser xPathParser = XmlUtils.processXml(methodXml);
        XNode xNode = xPathParser.evalNode("/mapper/*");
        return xNode;
    }

}
