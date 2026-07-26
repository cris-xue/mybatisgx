package com.mybatisgx.dsl.mgxsql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql AST 容器型 Unit 抽象基类，持有通用 body 子节点列表。
 * <p>IfUnit、WhenUnit、OtherwiseUnit 继承此类，消除 body 字段重复声明。
 *
 * @author 薛承城
 * @description 容器型 Unit 抽象基类
 * @date 2026/7/25
 */
public abstract class AbstractMgxsqlContainerUnit extends AbstractMgxsqlNode implements MgxsqlUnit {

    protected final List<MgxsqlNode> body = new ArrayList<MgxsqlNode>();

    protected AbstractMgxsqlContainerUnit(int startPosition, int line, int column) {
        super(startPosition, line, column);
    }

    public List<MgxsqlNode> getBody() {
        return body;
    }
}
