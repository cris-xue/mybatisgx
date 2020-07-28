<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${deleteSqlWrapper.namespace}">

    <delete id="${deleteSqlWrapper.methodName}" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
        delete from ${deleteSqlWrapper.tableName}
        <#if (deleteSqlWrapper.whereWrapperList?size > 0)>
            <where>
                <#list deleteSqlWrapper.whereWrapperList as ww>
                    ${ww.field} ${ww.op} ${ww.value}
                </#list>
            </where>
        </#if>
        <#--where id = ${r'#{id}'}-->
    </delete>

</mapper>

<#macro whereTree ww>
    <#if ww??>
        ${ww.field} ${ww.operation} ${ww.value}
        <#if ww.whereWrapper??>
            ${ww.linkOp} <@whereTree ww=ww.whereWrapper/>
        </#if>
    </#if>
</#macro>