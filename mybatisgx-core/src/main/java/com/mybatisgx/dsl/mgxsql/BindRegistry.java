package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.exception.MybatisgxException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * mgxsql {@code #bind} 名字注册表：承接重构前 {@code MgxsqlParser.declaredBinds} + {@code checkBindReference}
 * 的共享状态，供 scope 层（{@code MgxsqlScopeParser}）与 body 层（{@code MgxsqlBodyParser}）共用。
 * <p>状态流：每 {@code #bind[name = expr]} 经 {@link #putAndCheckDup} 登记并校验同名重复；
 * 每处 {@code $var} 引用经 {@link #checkBindReference} 校验「声明先于引用」。逻辑零变更，
 * 仅由 {@code MgxsqlParser} 的实例字段提取为独立类。
 *
 * @author 薛承城
 * @description mgxsql bind 名字注册表（跨层共享状态）
 * @date 2026/7/27
 */
public class BindRegistry {

    /** 显式 {@code #bind} 声明记录：name → 声明位置（同一 select/update 作用域内）。 */
    private final Map<String, Integer> declaredBinds = new LinkedHashMap();

    /**
     * 登记 {@code #bind} 名字，校验同作用域内重复。
     * <p>报错文本逐字节等价约束：scope 层与 body 层原重复异常的位置信息格式不同（scope 用
     * {@code ctx.getPositionInfo()}，body 用 {@code "位置: " + start}），故模板固定为不含「位置:」
     * 前缀的 {@code "...重复，%s"}，由调用方在 {@code posInfo} 中决定是否带「位置:」前缀。
     *
     * @param name    bind 名字（已校验为合法标识符）
     * @param pos     声明位置（存入声明记录，用于后续引用校验的存在性判断）
     * @param posInfo 已格式化的位置信息字符串（用于重复异常消息填充）
     */
    void putAndCheckDup(String name, int pos, String posInfo) {
        if (declaredBinds.containsKey(name)) {
            throw new MybatisgxException("mgxsql 语法错误: #bind 的 name '%s' 在同一作用域内重复，%s", name, posInfo);
        }
        declaredBinds.put(name, pos);
    }

    /**
     * 校验 {@code $var} 引用已有前置 {@code #bind} 声明。
     */
    void checkBindReference(String varName, String posInfo) {
        if (!declaredBinds.containsKey(varName)) {
            throw new MybatisgxException("mgxsql 语法错误: $%s 引用先于声明（缺少前置 #bind[%s = ...]），%s", varName, varName, posInfo);
        }
    }
}
