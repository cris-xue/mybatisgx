<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${querySqlWrapper.namespace}">

    <select id="${querySqlWrapper.methodName}" resultType="${querySqlWrapper.resultType}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list querySqlWrapper.modelWrapperList as mw>
                ${mw.dbColumn} as ${mw.javaColumn},
            </#list>
        </trim>
        from ${querySqlWrapper.tableName}
        <#if (querySqlWrapper.whereWrapper)??>
            <where>
                <@whereTree ww=querySqlWrapper.whereWrapper linkOp=""/>
            </where>
        </#if>
    </select>

</mapper>

<#macro whereTree ww linkOp>
    <#if ww??>
        <if test="${ww.javaColumn} != null">
            ${linkOp} ${ww.dbColumn} ${ww.operation.key} ${r'#{'} ${ww.javaColumn} ${r'}'}
        </if>
        <#if ww.whereWrapper??>
            <@whereTree ww=ww.whereWrapper linkOp=ww.linkOp/>
        </#if>
    </#if>
</#macro>