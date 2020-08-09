<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${deleteSqlWrapper.namespace}">

    <delete id="${deleteSqlWrapper.methodName}" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
        delete from ${deleteSqlWrapper.tableName}
        <where>
            <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                <#if (deleteSqlWrapper.whereWrapper)??>
                    <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                </#if>
            </trim>
            <#if (deleteSqlWrapper.versionWrapper)??>
                <trim prefixOverrides="AND | OR">
                <if test="${deleteSqlWrapper.versionWrapper.javaColumn} != null">
                    AND ${deleteSqlWrapper.versionWrapper.dbColumn}
                    =
                    ${r'#{'} ${deleteSqlWrapper.versionWrapper.javaColumn} ${r'}'}
                </if>
                </trim>
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