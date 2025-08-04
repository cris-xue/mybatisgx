package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurdTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(CurdTemplateHandler.class);

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
}
