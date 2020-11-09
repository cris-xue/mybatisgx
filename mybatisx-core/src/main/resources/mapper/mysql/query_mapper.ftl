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
        <where>
            <#if (querySqlWrapper.whereWrapper)??>
                (
                    <@whereTree ww=querySqlWrapper.whereWrapper linkOp=""/>
                )
            </#if>
            <#if (querySqlWrapper.logicDeleteWrapper)??>
                and ${querySqlWrapper.logicDeleteWrapper.dbColumn} = ${querySqlWrapper.logicDeleteWrapper.notValue}
            </#if>
        </where>
    </select>

</mapper>

<#macro whereTree ww linkOp>
    <#if ww??>
            <if test="${ww.test}">
                ${linkOp} ${ww.sql}
            </if>
            <#if ww.whereWrapper??>
                <@whereTree ww=ww.whereWrapper linkOp=ww.linkOp/>
            </#if>
    </#if>
</#macro>