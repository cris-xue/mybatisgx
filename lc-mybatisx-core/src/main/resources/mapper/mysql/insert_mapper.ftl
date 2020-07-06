<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${insertSqlWrapper.namespace}">

    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${insertSqlWrapper.parameterType}">
        insert into ${insertSqlWrapper.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.dbColumn as dbc>
                <if test="${dbc} != null">
                    ${dbc},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.entityColumn as enc>
                <if test="${enc} != null">
                    ${r'#{'} ${enc} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

</mapper>