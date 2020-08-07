<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="${insertSqlWrapper.namespace}">

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

</mapper>