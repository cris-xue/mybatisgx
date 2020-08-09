<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${updateSqlWrapper.namespace}">

    <update id="${updateSqlWrapper.methodName}" <#if (updateSqlWrapper.parameterType??)>parameterType="${updateSqlWrapper.parameterType}"</#if>>
        update ${updateSqlWrapper.tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list updateSqlWrapper.modelWrapperList as mw>
                <if test="${mw.javaColumn} != null">
                    ${mw.dbColumn} = ${r'#{'} ${mw.javaColumn} ${r'}'},
                </if>
            </#list>
            <#if (updateSqlWrapper.versionWrapper)??>
                <if test="${updateSqlWrapper.versionWrapper.javaColumn} != null">
                    ${updateSqlWrapper.versionWrapper.dbColumn}
                    =
                    ${r'#{'} ${updateSqlWrapper.versionWrapper.javaColumn} ${r'}'} + 1
                </if>
            </#if>
        </trim>
        <where>
            <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                <#if (updateSqlWrapper.whereWrapper)??>
                    <@whereTree ww=updateSqlWrapper.whereWrapper linkOp=""/>
                </#if>
            </trim>
            <#if (updateSqlWrapper.versionWrapper)??>
                <trim prefixOverrides="AND | OR">
                <if test="${updateSqlWrapper.versionWrapper.javaColumn} != null">
                    AND ${updateSqlWrapper.versionWrapper.dbColumn}
                    =
                    ${r'#{'} ${updateSqlWrapper.versionWrapper.javaColumn} ${r'}'}
                </if>
                </trim>
            </#if>
        </where>
    </update>

</mapper>

<#macro whereTree ww linkOp>
    <#if ww??>
        <if test="${ww.value} != null">
            ${linkOp} ${ww.field} ${ww.operation.key} ${r'#{'} ${ww.value} ${r'}'}
        </if>
        <#if ww.whereWrapper??>
            <@whereTree ww=ww.whereWrapper linkOp=ww.linkOp/>
        </#if>
    </#if>
</#macro>