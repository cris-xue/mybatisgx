package com.mybatisgx.ext.session;

import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;

import java.util.List;
import java.util.Map;

/**
 * MyBatisGX 扩展 Configuration 能力接口
 *
 * <p>供 {@code MybatisgxConfiguration} 与 MyBatis-Plus 共存时的 {@code MybatisgxPlusConfiguration} 共同实现，
 * 使运行时处理器（ResultSetHandler、参数值处理器等）不依赖具体 Configuration 类型。</p>
 *
 * <p>除 MyBatisGX 特有方法外，还声明处理器所需的部分原生 {@code Configuration} 方法
 * （{@code hasStatement}、{@code getDatabaseId}、{@code getSqlFragments}），实现类继承
 * {@code org.apache.ibatis.session.Configuration} 后自动满足。</p>
 *
 * @author ccxuef
 * @description MyBatisGX 扩展 Configuration 能力接口
 * @date 2026/8/15
 */
public interface MybatisgxConfigurationAware {

    EntityInfo getEntityInfo(Class<?> clazz);

    List<Class<?>> getEntityClassList();

    void addEntityInfo(EntityInfo entityInfo);

    MethodInfo getMethodInfo(MappedStatement ms);

    MethodInfo getMethodInfo(String msId);

    void addMethodInfo(MethodInfo methodInfo);

    boolean hasStatement(String statementName);

    String getDatabaseId();

    Map<String, XNode> getSqlFragments();

    MappedStatement getMappedStatement(String id);
}
