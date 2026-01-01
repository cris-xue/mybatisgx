package com.mybatisgx.template;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.template.select.SelectTemplateHandler;
import com.mybatisgx.utils.XmlUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StatementTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatementTemplateHandler.class);

    public Map<String, XNode> execute(MapperInfo mapperInfo) {
        Map<String, XNode> xNodeMap = new HashMap(15);
        for (MethodInfo methodInfo : mapperInfo.getMethodInfoList()) {
            if (methodInfo.getSqlCommandType() != null) {
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
}
