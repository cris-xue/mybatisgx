<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${parameterType}">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${columnMap[key]},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${r'#{'} ${key} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

    <insert id="insertSelective" keyProperty="id" useGeneratedKeys="true" parameterType="${parameterType}">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${columnMap[key]},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${r'#{'} ${key} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

    <delete id="deleteById" parameterType="java.lang.Long">
        update ${tableName} set delete_flag=1 where id = ${r'#{id}'}
        <#if columnMap['version']??>
            and version=${r'#{version'}${r'}'}
        </#if>
    </delete>

    <update id="updateById" parameterType="${parameterType}">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list columnMap?keys as key>
                <#if (key == 'updateVersion')>
                    ${columnMap[key]} = ${columnMap[key]}+1,
                <#elseif (key != 'id')>
                    <if test="${key} != null">
                        ${columnMap[key]} = ${r'#{'} ${key} ${r'}'},
                    </if>
                </#if>
            </#list>
        </trim>
        where id = ${r'#{id}'}
        <#if columnMap['updateVersion']??>
            and update_version=${r'#{updateVersion'}${r'}'}
        </#if>
    </update>

    <update id="updateByIdSelective" parameterType="${parameterType}">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list columnMap?keys as key>
                <#if (key == 'updateVersion')>
                    ${columnMap[key]} = ${columnMap[key]}+1,
                <#elseif (key != 'id')>
                    <if test="${key} != null">
                        ${columnMap[key]} = ${r'#{'} ${key} ${r'}'},
                    </if>
                </#if>
            </#list>
        </trim>
        where id = ${r'#{id}'}
        <#if columnMap['updateVersion']??>
            and update_version=${r'#{updateVersion'}${r'}'}
        </#if>
    </update>

    <select id="findById" parameterType="java.lang.Long" resultType="${resultType}">
        select * from ${tableName} where id = ${r'#{id}'}
        <#if columnMap['deleteFlag']??>
            AND delete_flag = 2
        </#if>
    </select>

    <select id="findAll" resultType="${resultType}">
        select * from ${tableName}
    </select>

    <select id="findList" resultType="${resultType}">
        select * from ${tableName}
        <trim prefix="WHERE" prefixOverrides="AND">
            <#list columnMap?keys as key>
            	<#if (key == 'name')>
                    <if test="${key} != null">
	                    AND ${columnMap[key]} like concat('%',concat( ${r'#{'} ${key} ${r'}'} ,'%')) 
	                </if>
                <#else>
                    <if test="${key} != null">
	                    AND ${columnMap[key]} = ${r'#{'} ${key} ${r'}'}
	                </if>
                </#if>
            </#list>
        </trim>
    </select>

</mapper>