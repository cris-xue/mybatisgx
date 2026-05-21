package com.mybatisgx.template;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.annotation.Version;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
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

public class InsertTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertTemplateHandler.class);

    SimpleInsertHandler simpleInsertHandler = new SimpleInsertHandler();
    BatchInsertHandler batchInsertHandler = new BatchInsertHandler();

    public String execute(MethodInfo methodInfo) {
        return simpleTemplateHandle(methodInfo);
    }

    private String simpleTemplateHandle(MethodInfo methodInfo) {
        return buildInsertSelectiveXNode(methodInfo);
    }

    private String buildInsertSelectiveXNode(MethodInfo methodInfo) {
        AbstractInsertHandler insertHandler = this.getInsertHandler(methodInfo);
        EntityInfo entityInfo = methodInfo.getMapperInfo().getEntityInfo();

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", methodInfo.getMethodName());
        insertElement.addAttribute("keyProperty", insertHandler.getKeyProperty(methodInfo));
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", entityInfo.getTableName()));

        Element dbTrimElement = MybatisXmlHelper.buildTrimElement("(", ")", ",");
        insertHandler.setColumn(methodInfo, dbTrimElement);
        if (methodInfo.getDynamic()) {
            insertElement.add(dbTrimElement);
        } else {
            String dbTrimElementString = String.format(" %s", XmlCompiler.compile(dbTrimElement));
            insertElement.addText(dbTrimElementString);
        }

        Element javaTrimElement = MybatisXmlHelper.buildTrimElement("values (", ")", ",");
        insertHandler.setValue(methodInfo, javaTrimElement);
        if (methodInfo.getDynamic()) {
            insertElement.add(javaTrimElement);
        } else {
            String javaTrimElementString = String.format(" %s", XmlCompiler.compile(javaTrimElement));
            insertElement.addText(javaTrimElementString);
        }

        return document.asXML();
    }

    private AbstractInsertHandler getInsertHandler(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            return batchInsertHandler;
        } else {
            return simpleInsertHandler;
        }
    }

    private static abstract class AbstractInsertHandler {

        abstract String getKeyProperty(MethodInfo methodInfo);

        public void setColumn(MethodInfo methodInfo, Element dbTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class, LogicDeleteIdColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setColumn(methodInfo, columnInfo, paramValuePathItemList, dbTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, null, columnInfoComposite);
                            this.setColumn(methodInfo, columnInfoComposite, paramValuePathItemList, dbTrimElement);
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
                            this.setColumn(methodInfo, foreignKeyColumnInfo, paramValuePathItemList, dbTrimElement);
                        }
                    }
                }
            }
        }

        private void setColumn(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, Element dbTrimElement) {
            String dbColumnName = columnInfo.getDbColumnName();
            String columnExpression = String.format("%s,", dbColumnName);
            if (columnInfo.getLogicDelete() != null) {
                dbTrimElement.addText(columnExpression);
                return;
            }
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            Element trimOrIfElement = MybatisXmlHelper.buildTrimOrIfElement(methodInfo, columnInfo, dbTrimElement, testExpression);
            trimOrIfElement.addText(columnExpression);
        }

        public void setValue(MethodInfo methodInfo, Element javaTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new MybatisgxException("实体表字段不存在");
            }
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class, LogicDeleteIdColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setValue(methodInfo, columnInfo, paramValuePathItemList, javaTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, null, columnInfoComposite);
                            this.setValue(methodInfo, columnInfoComposite, paramValuePathItemList, javaTrimElement);
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
                            ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, relationColumnInfo, referencedColumnInfo);
                            this.setValue(methodInfo, referencedColumnInfo, paramValuePathItemList, javaTrimElement);
                        }
                    }
                }
            }
        }

        private void setValue(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, Element trimElement) {
            Version version = columnInfo.getVersion();
            if (version != null) {
                String valueExpression = String.format("'%s'%s,", version.initValue(), buildTypeHandler(columnInfo));
                trimElement.addText(valueExpression);
                return;
            }
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                String valueExpression = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(columnInfo));
                trimElement.addText(valueExpression);
                return;
            }
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String valueExpression = MybatisXmlHelper.getValueExpression(paramValuePathItemList, columnInfo);
            Element trimOrIfElement = MybatisXmlHelper.buildTrimOrIfElement(methodInfo, columnInfo, trimElement, testExpression);
            trimOrIfElement.addText(valueExpression);
        }

        protected String buildTypeHandler(ColumnInfo columnInfo) {
            String typeHandler = columnInfo.getTypeHandler();
            if (StringUtils.isNotBlank(typeHandler)) {
                return String.format(", typeHandler=%s", typeHandler);
            }
            return "";
        }

        protected List<ColumnInfo> getTableColumnInfoList(Class<?> entityClass) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            return entityInfo.getTableColumnInfoList();
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

    private static class SimpleInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            List<String> keyPropertyList = new ArrayList();
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
            IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
            List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
            if (ObjectUtils.isEmpty(idColumnComposites)) {
                List<String> argValeueCommonPathItemList = Lists.newArrayList(entityParamInfo.getArgValueCommonPathItemList());
                argValeueCommonPathItemList.add(idColumnInfo.getJavaColumnName());
                String keyProperty = StringUtils.join(argValeueCommonPathItemList, ".");
                keyPropertyList.add(keyProperty);
            } else {
                for (ColumnInfo idColumnComposite : idColumnComposites) {
                    List<String> argValeueCommonPathItemList = Lists.newArrayList(entityParamInfo.getArgValueCommonPathItemList());
                    argValeueCommonPathItemList.addAll(idColumnComposite.getJavaColumnNamePathList());
                    String keyProperty = StringUtils.join(argValeueCommonPathItemList, ".");
                    keyPropertyList.add(keyProperty);
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo leafColumnInfo) {
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getArgValueCommonPathItemList());
            List<String> pathItemList = super.getParamValuePathItemList(methodParamInfo, columnInfo, leafColumnInfo);
            argValueCommonPathItemList.addAll(pathItemList);
            return argValueCommonPathItemList;
        }
    }

    private static class BatchInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            List<String> keyPropertyList = new ArrayList();
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            if (entityParamInfo.getBatchData()) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
                IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
                List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(idColumnComposites)) {
                    List<String> argValeueCommonPathItemList = Lists.newArrayList(entityParamInfo.getBatchItemName());
                    argValeueCommonPathItemList.add(idColumnInfo.getJavaColumnName());
                    String keyProperty = StringUtils.join(argValeueCommonPathItemList, ".");
                    keyPropertyList.add(keyProperty);
                } else {
                    for (ColumnInfo idColumnComposite : idColumnComposites) {
                        List<String> argValeueCommonPathItemList = Lists.newArrayList(entityParamInfo.getBatchItemName());
                        argValeueCommonPathItemList.addAll(idColumnComposite.getJavaColumnNamePathList());
                        String keyProperty = StringUtils.join(argValeueCommonPathItemList, ".");
                        keyPropertyList.add(keyProperty);
                    }
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo leafColumnInfo) {
            // int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
            String batchItemName = methodParamInfo.getBatchItemName();
            List<String> argValueCommonPathItemList = Lists.newArrayList(batchItemName);
            List<String> pathItemList = super.getParamValuePathItemList(methodParamInfo, columnInfo, leafColumnInfo);
            argValueCommonPathItemList.addAll(pathItemList);
            return argValueCommonPathItemList;
        }
    }
}