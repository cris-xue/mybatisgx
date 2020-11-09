<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${deleteSqlWrapper.namespace}">

    <#if deleteSqlWrapper.logicDeleteWrapper??>
        <update id="${deleteSqlWrapper.methodName}" <#if (deleteSqlWrapper.parameterType??)>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
            update ${deleteSqlWrapper.tableName}
            set ${deleteSqlWrapper.logicDeleteWrapper.dbColumn} = ${deleteSqlWrapper.logicDeleteWrapper.value}
            where
            <#if (deleteSqlWrapper.whereWrapper)??>
                (<@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>)
            </#if>
            and ${deleteSqlWrapper.logicDeleteWrapper.dbColumn} = ${deleteSqlWrapper.logicDeleteWrapper.notValue}
        </update>
    <#else>
        <delete id="${deleteSqlWrapper.methodName}" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
            delete from ${deleteSqlWrapper.tableName}
            <where>
                <#--<trim prefix="(" suffix=")" prefixOverrides="AND | OR">-->
                    <#if (deleteSqlWrapper.whereWrapper)??>
                        <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                    </#if>
                <#--</trim>-->
            </where>
        </delete>
    </#if>

</mapper>

<#macro whereTree ww linkOp>
    <#if ww??>
        <#--<if test="<#list ww.javaColumn as javaColumn>javaColumn != null</#list>">-->
        <#--<if test="${ww.javaColumn} != null">-->
            <#--${linkOp} ${ww.dbColumn} ${ww.operation.key} ${r'#{'} ${ww.javaColumn} ${r'}'}-->
        <#--<if test="${ww.test}">-->
        ${linkOp} ${ww.sql}
            <#--${linkOp} ${ww.dbColumn} ${ww.op} ${r'#{'} ${ww.javaColumn} ${r'}'}-->
        <#--</if>-->
        <#if ww.whereWrapper??>
            <@whereTree ww=ww.whereWrapper linkOp=ww.linkOp/>
        </#if>
    </#if>
</#macro>