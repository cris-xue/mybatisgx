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
                <if test="${deleteSqlWrapper.versionWrapper.javaColumn} != null">
                    AND ${deleteSqlWrapper.versionWrapper.dbColumn}
                    =
                    ${r'#{'} ${deleteSqlWrapper.versionWrapper.javaColumn} ${r'}'},
                </if>
            </#if>
        </where>
    </delete>

    <#--查询乐观锁的版本号-->
    <select id="find_${deleteSqlWrapper.methodName}_version" <#if deleteSqlWrapper.parameterType??>parameterType="${deleteSqlWrapper.parameterType}"</#if>>
        select version from ${deleteSqlWrapper.tableName}
        <where>
            <trim prefix="(" suffix=")" prefixOverrides="AND | OR">
                <#if (deleteSqlWrapper.whereWrapper)??>
                    <@whereTree ww=deleteSqlWrapper.whereWrapper linkOp=""/>
                </#if>
            </trim>
        </where>
    </select>

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
