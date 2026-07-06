package com.mybatisgx.template;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.template.delete.DeleteTemplateHandler;
import com.mybatisgx.template.insert.InsertTemplateHandler;
import com.mybatisgx.template.select.RelationSelectTemplateHandler;
import com.mybatisgx.template.select.ResultMapTemplateHandler;
import com.mybatisgx.template.select.SelectTemplateHandler;
import com.mybatisgx.template.update.UpdateTemplateHandler;
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

    private static final Map<SqlCommandType, TemplateHandler> TEMPLATE_HANDLER_MAP = new HashMap();

    public StatementTemplateHandler() {
        TEMPLATE_HANDLER_MAP.put(SqlCommandType.INSERT, MybatisgxObjectFactory.get(InsertTemplateHandler.class));
        TEMPLATE_HANDLER_MAP.put(SqlCommandType.DELETE, MybatisgxObjectFactory.get(DeleteTemplateHandler.class));
        TEMPLATE_HANDLER_MAP.put(SqlCommandType.UPDATE, MybatisgxObjectFactory.get(UpdateTemplateHandler.class));
        TEMPLATE_HANDLER_MAP.put(SqlCommandType.SELECT, MybatisgxObjectFactory.get(SelectTemplateHandler.class));
    }

    public MapperTemplateInfo execute(MapperInfo mapperInfo) {
        // 构建curd xnode
        Map<String, XNode> curdXNodeMap = new HashMap(15);
        for (MethodInfo methodInfo : mapperInfo.getMethodInfoList()) {
            if (methodInfo.getSqlCommandType() != null) {
                XNode curdXNode = complexTemplateHandle(methodInfo);
                curdXNodeMap.put(methodInfo.getMethodName(), curdXNode);
            }
        }

        // 构建结果集模板xnode
        ResultMapTemplateHandler resultMapTemplateHandler = new ResultMapTemplateHandler();
        Map<String, XNode> resultMapXNodeMap = resultMapTemplateHandler.execute(mapperInfo);

        // 构建注解式关联查询xnode
        RelationSelectTemplateHandler relationSelectTemplateHandler = new RelationSelectTemplateHandler();
        Map<String, XNode> relationSelectXNodeMap = relationSelectTemplateHandler.execute(mapperInfo);

        MapperTemplateInfo mapperTemplateInfo = new MapperTemplateInfo();
        mapperTemplateInfo.setNamespace(mapperInfo.getNamespace());
        mapperTemplateInfo.setCurdTemplateMap(curdXNodeMap);
        mapperTemplateInfo.setResultMapTemplateMap(resultMapXNodeMap);
        mapperTemplateInfo.setRelationSelectTemplateMap(relationSelectXNodeMap);

        return mapperTemplateInfo;
    }

    private XNode complexTemplateHandle(MethodInfo methodInfo) {
        TemplateHandler templateHandler = TEMPLATE_HANDLER_MAP.get(methodInfo.getSqlCommandType());
        if (templateHandler == null) {
            throw new MybatisgxException("不存在的操作方式");
        }
        String xmlString = templateHandler.execute(methodInfo);
        /*if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT) {
            SelectTemplateHandler selectTemplateHandler = MybatisgxObjectFactory.get(SelectTemplateHandler.class);
            xmlString = selectTemplateHandler.execute(methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            InsertTemplateHandler insertTemplateHandler = MybatisgxObjectFactory.get(InsertTemplateHandler.class);
            xmlString = insertTemplateHandler.execute(methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.DELETE) {
            DeleteTemplateHandler deleteTemplateHandler = MybatisgxObjectFactory.get(DeleteTemplateHandler.class);
            xmlString = deleteTemplateHandler.execute(methodInfo);
        } else if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
            UpdateTemplateHandler updateTemplateHandler = MybatisgxObjectFactory.get(UpdateTemplateHandler.class);
            xmlString = updateTemplateHandler.execute(methodInfo);
        } else {
            throw new MybatisgxException("不存在的操作方式");
        }*/
        logger.debug("{}:\n{}", methodInfo.getMethodName(), xmlString);
        XPathParser xPathParser = XmlUtils.processXml(xmlString);
        return xPathParser.evalNode("/mapper/select|/mapper/insert|/mapper/delete|/mapper/update");
    }
}
