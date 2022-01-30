<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${updateSqlWrapper.namespace}">

    <#--动态sql-->
    <#if updateSqlWrapper.dynamic>
    <update id="${updateSqlWrapper.methodName}" <#if (updateSqlWrapper.parameterType??)>parameterType="${updateSqlWrapper.parameterType}"</#if>>
        update ${updateSqlWrapper.tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list updateSqlWrapper.modelWrapperList as mw>
                <if test="${mw.javaColumn} != null">
                    ${mw.dbColumn} = ${r'#{'} ${mw.javaColumn} ${r'}'},
                </if>
            </#list>
            <#if (updateSqlWrapper.versionWrapper)??>
                ${updateSqlWrapper.versionWrapper.sql} + ${updateSqlWrapper.versionWrapper.increment}
            </#if>
        </trim>
        <where>
            <#if (updateSqlWrapper.whereWrapper)??>
                <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                    <@dynamicWhereTree ww=updateSqlWrapper.whereWrapper linkOp=""/>
                </trim>
            </#if>
            <#if (updateSqlWrapper.versionWrapper)??>
                and ${updateSqlWrapper.versionWrapper.sql}
            </#if>
            <#if (updateSqlWrapper.logicDeleteWrapper)??>
                and ${updateSqlWrapper.logicDeleteWrapper.dbColumn} = ${updateSqlWrapper.logicDeleteWrapper.notValue}
            </#if>
        </where>
    </update>
    </#if>

    <#--静态sql-->
    <#if !updateSqlWrapper.dynamic>
    <update id="${updateSqlWrapper.methodName}" <#if (updateSqlWrapper.parameterType??)>parameterType="${updateSqlWrapper.parameterType}"</#if>>
        update ${updateSqlWrapper.tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list updateSqlWrapper.modelWrapperList as mw>
                ${mw.dbColumn} = ${r'#{'} ${mw.javaColumn} ${r'}'},
            </#list>
            <#if (updateSqlWrapper.versionWrapper)??>
                ${updateSqlWrapper.versionWrapper.sql} + ${updateSqlWrapper.versionWrapper.increment}
            </#if>
        </trim>
        <where>
            <#if (updateSqlWrapper.whereWrapper)??>
                <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                    <@staticWhereTree ww=updateSqlWrapper.whereWrapper linkOp=""/>
                </trim>
            </#if>
            <#if (updateSqlWrapper.versionWrapper)??>
                and ${updateSqlWrapper.versionWrapper.sql}
            </#if>
            <#if (updateSqlWrapper.logicDeleteWrapper)??>
                and ${updateSqlWrapper.logicDeleteWrapper.dbColumn} = ${updateSqlWrapper.logicDeleteWrapper.notValue}
            </#if>
        </where>
    </update>
    </#if>

</mapper>

<#macro dynamicWhereTree ww linkOp>
    <#if ww??>
            <if test="${ww.test}">
                ${linkOp} ${ww.sql}
            </if>
            <#if ww.whereWrapper??>
                <@dynamicWhereTree ww=ww.whereWrapper linkOp=ww.whereWrapper.linkOp/>
            </#if>
    </#if>
</#macro>

<#macro staticWhereTree ww linkOp>
    <#if ww??>
                ${linkOp} ${ww.sql}
                <#if ww.whereWrapper??>
                    <@staticWhereTree ww=ww.whereWrapper linkOp=ww.whereWrapper.linkOp/>
                </#if>
    </#if>
</#macro>