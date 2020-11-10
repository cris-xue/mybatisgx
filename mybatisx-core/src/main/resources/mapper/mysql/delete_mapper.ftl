<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${deleteSqlWrapper.namespace}">

    <#if deleteSqlWrapper.logicDeleteWrapper??>
        <update id="${deleteSqlWrapper.methodName}" <#if (deleteSqlWrapper.parameterType??)>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
            update ${deleteSqlWrapper.tableName}
            <trim prefix="SET" suffixOverrides=",">
                ${deleteSqlWrapper.logicDeleteWrapper.dbColumn} = ${deleteSqlWrapper.logicDeleteWrapper.value},
                <#if (deleteSqlWrapper.versionWrapper)??>
                    and ${deleteSqlWrapper.versionWrapper.sql} + ${deleteSqlWrapper.versionWrapper.increment}
                </#if>
            </trim>
            <where>
                <#if (deleteSqlWrapper.whereWrapper)??>
                    <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                        <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                    </trim>
                </#if>
                <#if (deleteSqlWrapper.versionWrapper)??>
                    and ${deleteSqlWrapper.versionWrapper.sql}
                </#if>
                <#if (deleteSqlWrapper.logicDeleteWrapper)??>
                    and ${deleteSqlWrapper.logicDeleteWrapper.dbColumn} = ${deleteSqlWrapper.logicDeleteWrapper.notValue}
                </#if>
            </where>
        </update>
    <#else>
        <delete id="${deleteSqlWrapper.methodName}" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
            delete from ${deleteSqlWrapper.tableName}
            <where>
                <#if (deleteSqlWrapper.whereWrapper)??>
                    <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                </#if>
            </where>
        </delete>
    </#if>

</mapper>

<#macro whereTree ww linkOp>
    <#if ww??>
            ${linkOp} ${ww.sql}
            <#if ww.whereWrapper??>
                <@whereTree ww=ww.whereWrapper linkOp=ww.linkOp/>
            </#if>
    </#if>
</#macro>