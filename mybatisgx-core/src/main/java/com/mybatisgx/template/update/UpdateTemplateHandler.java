package com.mybatisgx.template.update;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.annotation.Version;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.template.MgxqlSubsetPurity;
import com.mybatisgx.template.MgxqlWhereHandler;
import com.mybatisgx.template.TemplateHandler;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UpdateTemplateHandler implements TemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTemplateHandler.class);

    private SimpleUpdateHandler simpleUpdateHandler = new SimpleUpdateHandler();
    private BatchUpdateHandler batchUpdateHandler = new BatchUpdateHandler();

    @Override
    public String execute(MethodInfo methodInfo) {
        return buildUpdateXNode(methodInfo);
    }

    private String buildUpdateXNode(MethodInfo methodInfo) {
        EntityInfo entityInfo = methodInfo.getMapperInfo().getEntityInfo();

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element updateElement = mapperElement.addElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", entityInfo.getTableName()));

        AbstractUpdateHandler abstractUpdateHandler = this.getAbstractUpdateHandler(methodInfo);
        abstractUpdateHandler.setValue(methodInfo, updateElement);
        abstractUpdateHandler.setWhere(updateElement, entityInfo, methodInfo);
        return document.asXML();
    }

    private AbstractUpdateHandler getAbstractUpdateHandler(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            return batchUpdateHandler;
        } else {
            return simpleUpdateHandler;
        }
    }

    private static abstract class AbstractUpdateHandler {

        protected MgxqlWhereHandler mgxqlWhereHandler = new MgxqlWhereHandler();
        protected MgxsqlScanner mgxsqlScanner = new MgxsqlScanner();

        public void setValue(MethodInfo methodInfo, Element updateElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo);
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new MybatisgxException("%s实体表字段不存在", entityParamInfo.getTypeName());
            }
            StringBuilder setBody = new StringBuilder();
            this.setValue(methodInfo, entityParamInfo, tableColumnInfoList, setBody);
            addSetNodes(updateElement, setBody.toString());
        }

        public void setValue(MethodInfo methodInfo, MethodParamInfo entityParamInfo, List<ColumnInfo> tableColumnInfoList, StringBuilder setBody) {
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setValue(methodInfo, columnInfo, paramValuePathItemList, setBody);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, null, columnInfoComposite);
                            this.setValue(methodInfo, columnInfoComposite, paramValuePathItemList, setBody);
                        }
                    }
                }
                if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                    if (relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY) {
                        continue;
                    }
                    ColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                    if (mappedByRelationColumnInfo == null) {
                        for (ForeignKeyInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                            ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                            ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, relationColumnInfo, referencedColumnInfo);
                            this.setValue(methodInfo, foreignKeyColumnInfo, paramValuePathItemList, setBody);
                        }
                    }
                }
            }
        }

        private void setValue(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, StringBuilder setBody) {
            Version version = columnInfo.getVersion();
            if (version != null) {
                String valuePath = StringUtils.join(paramValuePathItemList, ".");
                setBody.append(columnInfo.getDbColumnName()).append(" = :").append(valuePath).append(" + ").append(version.increment()).append(", ");
                return;
            }

            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                setBody.append(columnInfo.getDbColumnName()).append(" = '").append(logicDelete.show()).append("', ");
                return;
            }

            String valuePath = StringUtils.join(paramValuePathItemList, ".");
            String assignment = columnInfo.getDbColumnName() + " = :" + valuePath + ", ";
            setBody.append("#[").append(assignment).append("]");
        }

        private void addSetNodes(Element updateElement, String setBody) {
            String setSql = "set[" + setBody + "]";
            MgxqlSubsetPurity.assertPure(setSql);
            String setXml = mgxsqlScanner.process(setSql);
            Element root;
            try {
                Document setDocument = DocumentHelper.parseText("<root>" + setXml + "</root>");
                root = setDocument.getRootElement();
            } catch (org.dom4j.DocumentException e) {
                throw new MybatisgxException("mgxql UPDATE SET 渲染：解析 MgxsqlScanner 产出 XML 失败: " + e.getMessage() + " | 原文: " + setXml);
            }
            List<Node> nodes = new ArrayList<Node>(root.content());
            for (Node node : nodes) {
                updateElement.add(node.detach());
            }
        }

        private void setWhere(Element updateElement, EntityInfo entityInfo, MethodInfo methodInfo) {
            if (methodInfo.getMgxqlStatement() != null) {
                WhereClause whereClause = methodInfo.getMgxqlStatement().getWhereClause();
                if (whereClause != null) {
                    this.addWhereNodes(updateElement, entityInfo, methodInfo, whereClause);
                }
            }
        }

        private void addWhereNodes(Element updateElement, EntityInfo entityInfo, MethodInfo methodInfo, WhereClause whereClause) {
            com.mybatisgx.dsl.mgxql.model.MgxqlSourceType sourceType = methodInfo.getMgxqlStatement() != null
                    ? methodInfo.getMgxqlStatement().getMgxqlSourceType() : null;
            boolean autoGuard = sourceType == com.mybatisgx.dsl.mgxql.model.MgxqlSourceType.ENTITY
                    || sourceType == com.mybatisgx.dsl.mgxql.model.MgxqlSourceType.METHOD_NAME;
            String whereSql = mgxqlWhereHandler.renderWhereClause(
                    whereClause.getRootExpression(), null, this.buildExtraWhereCondition(entityInfo, methodInfo), autoGuard);
            if (StringUtils.isBlank(whereSql)) {
                return;
            }
            String whereXml = mgxsqlScanner.process(whereSql);
            Element root;
            try {
                Document whereDocument = DocumentHelper.parseText("<root>" + whereXml + "</root>");
                root = whereDocument.getRootElement();
            } catch (org.dom4j.DocumentException e) {
                throw new MybatisgxException("mgxql UPDATE WHERE 渲染：解析 MgxsqlScanner 产出 XML 失败: " + e.getMessage() + " | 原文: " + whereXml);
            }
            List<Node> nodes = new ArrayList<Node>(root.content());
            for (Node node : nodes) {
                updateElement.add(node.detach());
            }
        }

        private String buildExtraWhereCondition(EntityInfo entityInfo, MethodInfo methodInfo) {
            StringBuilder sb = new StringBuilder();
            if (entityInfo != null && entityInfo.getVersionColumnInfo() != null && methodInfo.getEntityParamInfo() != null) {
                ColumnInfo versionColumnInfo = entityInfo.getVersionColumnInfo();
                List<String> valuePath = new ArrayList<String>();
                if (methodInfo.getBatch()) {
                    valuePath.add(methodInfo.getEntityParamInfo().getBatchItemName());
                }
                valuePath.add(versionColumnInfo.getJavaColumnName());
                sb.append(" and ").append(versionColumnInfo.getDbColumnName()).append(" = :").append(StringUtils.join(valuePath, "."));
            }
            if (entityInfo != null && entityInfo.getLogicDeleteColumnInfo() != null) {
                ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
                LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
                if (logicDelete != null) {
                    sb.append(" and ").append(logicDeleteColumnInfo.getDbColumnName()).append(" = '").append(logicDelete.show()).append("'");
                }
            }
            return sb.toString();
        }

        private List<ColumnInfo> getTableColumnInfoList(MethodParamInfo methodParamInfo) {
            Class<?> entityClass = methodParamInfo.getType();
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            if (entityInfo != null) {
                return entityInfo.getTableColumnInfoList();
            }
            return methodParamInfo.getColumnInfoList();
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo leafColumnInfo) {
            List<String> argValueCommonPathItemList = new ArrayList(5);
            if (columnInfo != null) {
                argValueCommonPathItemList.addAll(columnInfo.getJavaColumnNamePathList());
            }
            if (leafColumnInfo != null) {
                argValueCommonPathItemList.addAll(leafColumnInfo.getJavaColumnNamePathList());
            }
            return argValueCommonPathItemList;
        }
    }

    private static class SimpleUpdateHandler extends AbstractUpdateHandler {

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo leafColumnInfo) {
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getArgValueCommonPathItemList());
            List<String> pathItemList = super.getParamValuePathItemList(methodParamInfo, columnInfo, leafColumnInfo);
            argValueCommonPathItemList.addAll(pathItemList);
            return argValueCommonPathItemList;
        }
    }

    private static class BatchUpdateHandler extends AbstractUpdateHandler {

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo leafColumnInfo) {
            // int updateBatchById(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
            String batchItemName = methodParamInfo.getBatchItemName();
            List<String> argValueCommonPathItemList = Lists.newArrayList(batchItemName);
            List<String> pathItemList = super.getParamValuePathItemList(methodParamInfo, columnInfo, leafColumnInfo);
            argValueCommonPathItemList.addAll(pathItemList);
            return argValueCommonPathItemList;
        }
    }
}
