<?xml version="1.0" encoding="UTF-8" ?>
<#--<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">-->
<mapper namespace="${deleteSqlWrapper.namespace}">

    <delete id="${deleteSqlWrapper.methodName}" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
        delete from ${deleteSqlWrapper.tableName}
        <#--<#if (deleteSqlWrapper.whereWrapper)??>
            <where>
                <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
            </where>
        </#if>-->
        <where>
            <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                <#if (deleteSqlWrapper.whereWrapper)??>
                    <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                </#if>
            </trim>
            <#if (deleteSqlWrapper.versionWrapper)??>
                <if test="${deleteSqlWrapper.versionWrapper.javaColumn} != null">
                    AND ${deleteSqlWrapper.versionWrapper.dbColumn}
                    =
                    ${r'#{'} ${deleteSqlWrapper.versionWrapper.javaColumn} ${r'}'},
                </if>
            </#if>
        </where>
    </delete>

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
<#--<#macro whereTree ww>
    <#if ww??>
        ${ww.field} ${ww.operation.key} ${r'#{'} ${ww.value} ${r'}'},
        <#if ww.whereWrapper??>
            ${ww.linkOp} <@whereTree ww=ww.whereWrapper/>
        </#if>
    </#if>
</#macro>-->