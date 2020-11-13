<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${querySqlWrapper.namespace}">

    <#--动态sql-->
    <#if querySqlWrapper.dynamic>
    <select id="${querySqlWrapper.methodName}" resultType="${querySqlWrapper.resultType}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list querySqlWrapper.modelWrapperList as mw>
                ${mw.dbColumn} as ${mw.javaColumn},
            </#list>
        </trim>
        from ${querySqlWrapper.tableName}
        <where>
            <#if (querySqlWrapper.whereWrapper)??>
                <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                    <@dynamicWhereTree ww=querySqlWrapper.whereWrapper/>
                </trim>
            </#if>
            <#if (querySqlWrapper.logicDeleteWrapper)??>
                and ${querySqlWrapper.logicDeleteWrapper.dbColumn} = ${querySqlWrapper.logicDeleteWrapper.notValue}
            </#if>
        </where>
        <#if (querySqlWrapper.limitWrapper)??>
            ${querySqlWrapper.limitWrapper.sql}
        </#if>
    </select>
    </#if>

    <#--静态sql-->
    <#if !querySqlWrapper.dynamic>
        <select id="${querySqlWrapper.methodName}" resultType="${querySqlWrapper.resultType}">
            select
            <trim prefix="" suffix="" suffixOverrides=",">
                <#list querySqlWrapper.modelWrapperList as mw>
                    ${mw.dbColumn} as ${mw.javaColumn},
                </#list>
            </trim>
            from ${querySqlWrapper.tableName}
            <where>
                <#if (querySqlWrapper.whereWrapper)??>
                    <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                        <@staticWhereTree ww=querySqlWrapper.whereWrapper/>
                    </trim>
                </#if>
                <#if (querySqlWrapper.logicDeleteWrapper)??>
                    and ${querySqlWrapper.logicDeleteWrapper.dbColumn} = ${querySqlWrapper.logicDeleteWrapper.notValue}
                </#if>
            </where>
            <#if (querySqlWrapper.limitWrapper)??>
                ${querySqlWrapper.limitWrapper.sql}
            </#if>
        </select>
    </#if>

</mapper>

<#macro dynamicWhereTree ww>
    <#if ww??>
                    <if test="${ww.test}">
                        ${ww.linkOp} ${ww.sql}
                    </if>
                    <#if ww.whereWrapper??>
                        <@dynamicWhereTree ww=ww.whereWrapper/>
                    </#if>
    </#if>
</#macro>

<#macro staticWhereTree ww>
    <#if ww??>
                    ${ww.linkOp} ${ww.sql}
                    <#if ww.whereWrapper??>
                        <@staticWhereTree ww=ww.whereWrapper/>
                    </#if>
    </#if>
</#macro>