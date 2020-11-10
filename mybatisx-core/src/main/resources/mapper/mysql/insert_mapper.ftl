<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${insertSqlWrapper.namespace}">

    <#--动态sql-->
    <#if insertSqlWrapper.dynamic>
    <insert id="${insertSqlWrapper.methodName}" keyProperty="id" useGeneratedKeys="true" parameterType="${insertSqlWrapper.parameterType}">
        insert into ${insertSqlWrapper.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.modelWrapperList as mw>
                <if test="${mw.javaColumn} != null">
                    ${mw.dbColumn},
                </if>
            </#list>
            <#if insertSqlWrapper.logicDeleteWrapper??>
                ${insertSqlWrapper.logicDeleteWrapper.dbColumn},
            </#if>
            <#if (insertSqlWrapper.versionWrapper)??>
                ${insertSqlWrapper.versionWrapper.dbColumn},
            </#if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.modelWrapperList as mw>
                <if test="${mw.javaColumn} != null">
                    ${r'#{'} ${mw.javaColumn} ${r'}'},
                </if>
            </#list>
            <#if insertSqlWrapper.logicDeleteWrapper??>
                ${insertSqlWrapper.logicDeleteWrapper.notValue},
            </#if>
            <#if (insertSqlWrapper.versionWrapper)??>
                ${insertSqlWrapper.versionWrapper.initValue}
            </#if>
        </trim>
    </insert>
    </#if>

    <#--静态sql-->
    <#if !insertSqlWrapper.dynamic>
        <insert id="${insertSqlWrapper.methodName}" keyProperty="id" useGeneratedKeys="true" parameterType="${insertSqlWrapper.parameterType}">
            insert into ${insertSqlWrapper.tableName}
            <trim prefix="(" suffix=")" suffixOverrides=",">
                <#list insertSqlWrapper.modelWrapperList as mw>
                    ${mw.dbColumn},
                </#list>
                <#if insertSqlWrapper.logicDeleteWrapper??>
                    ${insertSqlWrapper.logicDeleteWrapper.dbColumn},
                </#if>
                <#if (insertSqlWrapper.versionWrapper)??>
                    ${insertSqlWrapper.versionWrapper.dbColumn},
                </#if>
            </trim>
            <trim prefix="values (" suffix=")" suffixOverrides=",">
                <#list insertSqlWrapper.modelWrapperList as mw>
                    ${r'#{'} ${mw.javaColumn} ${r'}'},
                </#list>
                <#if insertSqlWrapper.logicDeleteWrapper??>
                    ${insertSqlWrapper.logicDeleteWrapper.notValue},
                </#if>
                <#if (insertSqlWrapper.versionWrapper)??>
                    ${insertSqlWrapper.versionWrapper.initValue}
                </#if>
            </trim>
        </insert>
    </#if>

</mapper>