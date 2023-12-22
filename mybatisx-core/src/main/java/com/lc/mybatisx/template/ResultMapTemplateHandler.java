package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;

import java.util.HashMap;
import java.util.Map;

public class ResultMapTemplateHandler {

    public XNode execute(ResultMapInfo resultMapInfo) {
        Template template = FreeMarkerUtils.getTemplate("mapper/result_map.ftl");
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("resultMapInfo", resultMapInfo);
        String resultMapXml = FreeMarkerUtils.processTemplate(templateData, template);

        XPathParser xPathParser = XmlUtils.processXml(resultMapXml);
        XNode xNode = xPathParser.evalNode("/mapper/resultMap");
        return xNode;
    }

}
