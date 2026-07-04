package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一绑定结果模型，被 WHERE 和 HAVING 域的条件节点共同引用。
 * 绑定阶段完成所有推导，模板渲染阶段直接消费。
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class BoundParam {

    /**
     * 参数绑定形态
     */
    private ParamKind kind;

    /**
     * 绑定条目列表
     */
    private List<BoundParamEntry> entries = new ArrayList<>();

    /**
     * 集合参数信息（IN / BETWEEN 操作时使用）
     */
    private CollectionInfo collectionInfo;

    /**
     * 比较运算符
     */
    private ComparisonOperator operator;

    /**
     * NOT 修饰符（用于 not in、not like 等）
     */
    private ComparisonOperator notOperator;

    public BoundParam() {
    }

    public BoundParam(ParamKind kind) {
        this.kind = kind;
    }

    public ParamKind getKind() {
        return kind;
    }

    public void setKind(ParamKind kind) {
        this.kind = kind;
    }

    public List<BoundParamEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BoundParamEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(BoundParamEntry entry) {
        this.entries.add(entry);
    }

    public CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

    public void setCollectionInfo(CollectionInfo collectionInfo) {
        this.collectionInfo = collectionInfo;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public ComparisonOperator getNotOperator() {
        return notOperator;
    }

    public void setNotOperator(ComparisonOperator notOperator) {
        this.notOperator = notOperator;
    }
}
