package com.mybatisgx.template;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.context.EntityInfoContextHolder;
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

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return simpleTemplateHandle(mapperInfo, methodInfo);
    }

    private String simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildInsertSelectiveXNode(mapperInfo, methodInfo);
    }

    private String buildInsertSelectiveXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        AbstractInsertHandler insertHandler = this.getInsertHandler(methodInfo);

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", methodInfo.getMethodName());
        String keyProperty = insertHandler.getKeyProperty(methodInfo);
        insertElement.addAttribute("keyProperty", keyProperty);
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getEntityInfo().getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        insertHandler.setColumn(methodInfo, dbTrimElement);

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");

        insertHandler.setValue(methodInfo, javaTrimElement);

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
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setColumn(methodInfo, columnInfo, paramValuePathItemList, dbTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, columnInfoComposite);
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
                        for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                            ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                            ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                            List<ColumnInfo> referencedColumnInfoComposites = referencedColumnInfo.getComposites();
                            if (ObjectUtils.isEmpty(referencedColumnInfoComposites)) {
                                List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, null, relationColumnInfo);
                                this.setColumn(methodInfo, foreignKeyColumnInfo, paramValuePathItemList, dbTrimElement);
                            } else {
                                ColumnInfo referencedColumnInfoComposite = referencedColumnInfo.getComposites().get(0);
                                List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, referencedColumnInfoComposite, relationColumnInfo);
                                this.setColumn(methodInfo, foreignKeyColumnInfo, paramValuePathItemList, dbTrimElement);
                            }
                        }
                    }
                }
            }
        }

        private void setColumn(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, Element dbTrimElement) {
            String dbColumnName = columnInfo.getDbColumnName();
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("%s,", dbColumnName);
                dbTrimElement.addText(javaColumn);
                return;
            }
            String testExpression = this.getTestExpression(paramValuePathItemList);
            Element trimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), dbTrimElement, testExpression);
            String dbColumn = String.format("%s,", dbColumnName);
            trimOrIfElement.addText(dbColumn);
        }

        public void setValue(MethodInfo methodInfo, Element javaTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new RuntimeException("实体表字段不存在");
            }
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        this.setValue(methodInfo, columnInfo, paramValuePathItemList, javaTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, columnInfoComposite);
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
                        for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                            ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                            if (TypeUtils.typeEquals(referencedColumnInfo, IdColumnInfo.class)) {
                                List<ColumnInfo> referencedColumnComposites = referencedColumnInfo.getComposites();
                                if (ObjectUtils.isEmpty(referencedColumnComposites)) {
                                    List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, null, relationColumnInfo);
                                    this.setValue(methodInfo, referencedColumnInfo, paramValuePathItemList, javaTrimElement);
                                } else {
                                    for (ColumnInfo referencedColumnComposite : referencedColumnComposites) {
                                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, referencedColumnComposite, relationColumnInfo);
                                        this.setValue(methodInfo, referencedColumnComposite, paramValuePathItemList, javaTrimElement);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private void setValue(MethodInfo methodInfo, ColumnInfo columnInfo, List<String> paramValuePathItemList, Element javaTrimElement) {
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(columnInfo));
                javaTrimElement.addText(javaColumn);
                return;
            }

            String testExpression = this.getTestExpression(paramValuePathItemList);
            Element javaTrimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), javaTrimElement, testExpression);

            String paramValuePath = StringUtils.join(paramValuePathItemList, ".");
            String javaColumn = String.format("#{%s%s},", paramValuePath, buildTypeHandler(columnInfo));
            javaTrimOrIfElement.addText(javaColumn);
        }

        private Element buildTrimOrIfElement(Boolean dynamic, Element parentElement, String testExpression) {
            if (dynamic) {
                Element ifElement = parentElement.addElement("if");
                ifElement.addAttribute("test", testExpression);
                return ifElement;
            }
            return parentElement;
        }

        protected String buildTypeHandler(ColumnInfo columnInfo) {
            String typeHandler = columnInfo.getTypeHandler();
            if (StringUtils.isNotBlank(typeHandler)) {
                return String.format(", typeHandler=%s", typeHandler);
            }
            return "";
        }

        protected String getTestExpression(List<String> pathItemList) {
            String[] paths = pathItemList.toArray(new String[pathItemList.size()]);
            if (paths.length == 1) {
                return String.format("%1$s != null", paths);
            }
            if (paths.length == 2) {
                return String.format("%1$s != null and %1$s.%2$s != null", paths);
            }
            if (paths.length == 3) {
                return String.format("%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null", paths);
            }
            return "";
        }

        protected List<ColumnInfo> getTableColumnInfoList(Class<?> entityClass) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            return entityInfo.getTableColumnInfoList();
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.getParamValuePathItemList(methodParamInfo, columnInfo, columnInfoComposite, null);
        }

        abstract protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo);
    }

    private static class SimpleInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<String> keyPropertyList = new ArrayList();
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
            IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
            List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
            if (ObjectUtils.isEmpty(idColumnComposites)) {
                String keyProperty = String.format("%s", idColumnInfo.getJavaColumnName());
                keyPropertyList.add(keyProperty);
            } else {
                for (ColumnInfo idColumnComposite : idColumnComposites) {
                    String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), idColumnComposite.getJavaColumnName());
                    String keyProperty = String.format("%s", javaColumnName);
                    keyPropertyList.add(keyProperty);
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo) {
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getArgValueCommonPathItemList());
            if (relationColumnInfo != null) {
                argValueCommonPathItemList.add(relationColumnInfo.getJavaColumnName());
            }
            if (columnInfo != null) {
                argValueCommonPathItemList.add(columnInfo.getJavaColumnName());
            }
            if (columnInfoComposite != null) {
                argValueCommonPathItemList.add(columnInfoComposite.getJavaColumnName());
            }
            return argValueCommonPathItemList;
        }
    }

    private static class BatchInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<String> keyPropertyList = new ArrayList();
            if (entityParamInfo.getBatchData()) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
                IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
                List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(idColumnComposites)) {
                    String keyProperty = String.format("%s.%s", entityParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName());
                    keyPropertyList.add(keyProperty);
                } else {
                    for (ColumnInfo idColumnComposite : idColumnComposites) {
                        String keyProperty = String.format("%s.%s.%s", entityParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName(), idColumnComposite.getJavaColumnName());
                        keyPropertyList.add(keyProperty);
                    }
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo) {
            // int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
            String batchItemName = methodParamInfo.getBatchItemName();
            List<String> argValueCommonPathItemList = Lists.newArrayList(batchItemName);
            if (relationColumnInfo != null) {
                argValueCommonPathItemList.add(relationColumnInfo.getJavaColumnName());
            }
            if (columnInfo != null) {
                argValueCommonPathItemList.add(columnInfo.getJavaColumnName());
            }
            if (columnInfoComposite != null) {
                argValueCommonPathItemList.add(columnInfoComposite.getJavaColumnName());
            }
            return argValueCommonPathItemList;
        }
    }
}