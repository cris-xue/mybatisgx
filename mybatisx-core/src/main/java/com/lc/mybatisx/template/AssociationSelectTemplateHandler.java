package com.lc.mybatisx.template;

import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssociationSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    public List<XNode> execute(MapperInfo mapperInfo, List<MethodInfo> methodInfoList) {
        Map<String, AssociationTableInfo> associationTableInfoMap = new LinkedHashMap<>();
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            ResultMapInfo resultMapInfo = methodInfo.getResultMapInfo();
            if (resultMapInfo == null) {
                continue;
            }
            List<AssociationTableInfo> associationTableInfoList = resultMapInfo.getAssociationTableInfoList();
            associationTableInfoList.forEach(associationTableInfo -> {
                associationTableInfoMap.put(associationTableInfo.getSelect(), associationTableInfo);
            });
        }

        List<XNode> xNodeList = new ArrayList<>();
        associationTableInfoMap.forEach((k, associationTableInfo) -> {
            XNode xNode = buildSelectXNode(associationTableInfo);
            xNodeList.add(xNode);
        });
        return xNodeList;
    }

    private XNode buildSelectXNode(AssociationTableInfo associationTableInfo) {
        TableInfo tableInfo = TableInfoContextHolder.get(associationTableInfo.getAssociationEntityClass());
        TableInfo targetTableInfo = TableInfoContextHolder.get(associationTableInfo.getTargetEntityClass());

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", associationTableInfo.getSelect());
        selectElement.addAttribute("resultType", associationTableInfo.getTargetEntityClassName());
        /**
         *      * select role.* from
         *      * user_role user_role left join role role on user_role.role_id = role.id
         *      * where user.user_id = id
         */
        selectElement.addText(
                String.format(
                        "select %s.* from %s %s left join %s %s on %s.%s = %s.%s where %s.%s = #{id}",
                        targetTableInfo.getTableName(),
                        tableInfo.getTableName(),
                        tableInfo.getTableName(),
                        targetTableInfo.getTableName(),
                        targetTableInfo.getTableName(),

                        tableInfo.getTableName(),
                        associationTableInfo.getTargetForeignKey()[0],

                        targetTableInfo.getTableName(),
                        targetTableInfo.getColumnInfo("id").getDbColumnName(),

                        tableInfo.getTableName(),
                        associationTableInfo.getForeignKey()[0]
                )
        );

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

}
