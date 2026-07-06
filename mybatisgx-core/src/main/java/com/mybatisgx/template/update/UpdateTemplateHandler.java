package com.mybatisgx.template.update;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.annotation.Version;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.template.MgxqlWhereTemplateHandler;
import com.mybatisgx.template.MybatisXmlHelper;
import com.mybatisgx.template.TemplateHandler;
import com.mybatisgx.template.XmlCompiler;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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

        Element setTrimElement = MybatisXmlHelper.buildTrimElement("set", "", ",");
        updateElement.add(setTrimElement);

        AbstractUpdateHandler abstractUpdateHandler = this.getAbstractUpdateHandler(methodInfo);
        abstractUpdateHandler.setValue(methodInfo, setTrimElement);
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

        protected MgxqlWhereTemplateHandler mgxqlWhereTemplateHandler = new MgxqlWhereTemplateHandler();

        public void setValue(MethodInfo methodInfo, Element setTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo);
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new MybatisgxException("%s实体表字段不存在", entityParamInfo.getTypeName());
            }
            this.setValue(methodInfo, entityParamInfo, tableColumnInfoList, setTrimElement);
            if (!methodInfo.getDynamic()) {
                XmlCompiler.trim(setTrimElement);
            }
        }

        public void setValue(MethodInfo methodInfo, MethodParamInfo entityParamInfo, List<ColumnInfo> tableColumnInfoList, Element setTrimElement) {
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setValue(methodInfo, columnInfo, paramValuePathItemList, setTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, null, columnInfoComposite);
                            this.setValue(methodInfo, columnInfoComposite, paramValuePathItemList, setTrimElement);
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
                            this.setValue(methodInfo, foreignKeyColumnInfo, paramValuePathItemList, setTrimElement);
                        }
                    }
                }
            }
        }

        private void setValue(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, Element trimElement) {
            Version version = columnInfo.getVersion();
            if (version != null) {
                String valuePath = StringUtils.join(paramValuePathItemList, ".");
                String columnValueExpression = String.format("%s = #{%s} + %s, ", columnInfo.getDbColumnName(), valuePath, version.increment());
                trimElement.addText(columnValueExpression);
                return;
            }

            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                String columnValueExpression = String.format("%s = '%s', ", columnInfo.getDbColumnName(), logicDelete.show());
                trimElement.addText(columnValueExpression);
                return;
            }

            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String valueExpression = MybatisXmlHelper.getValueExpression(paramValuePathItemList, columnInfo);
            Element trimOrIfElement = MybatisXmlHelper.buildTrimOrIfElement(methodInfo, columnInfo, trimElement, testExpression);
            trimOrIfElement.addText(String.format("%s = %s", columnInfo.getDbColumnName(), valueExpression));
        }

        private void setWhere(Element updateElement, EntityInfo entityInfo, MethodInfo methodInfo) {
            Element whereElement = null;
            if (methodInfo.getMgxqlStatement() != null) {
                WhereClause whereClause = methodInfo.getMgxqlStatement().getWhereClause();
                if (whereClause != null) {
                    whereElement = mgxqlWhereTemplateHandler.execute(entityInfo, methodInfo, whereClause.getRootExpression());
                }
            }
            updateElement.add(whereElement);
            if (!methodInfo.getDynamic()) {
                XmlCompiler.where(whereElement);
            }
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
