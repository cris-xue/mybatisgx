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
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.modelWrapperList as mw>
                <if test="${mw.javaColumn} != null">
                    ${r'#{'} ${mw.javaColumn} ${r'}'},
                </if>
            </#list>
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
            </trim>
            <trim prefix="values (" suffix=")" suffixOverrides=",">
                <#list insertSqlWrapper.modelWrapperList as mw>
                    ${r'#{'} ${mw.javaColumn} ${r'}'},
                </#list>
            </trim>
        </insert>
    </#if>

</mapper>