<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">
    <#--<resultMap id="${dtoName}ResultMap" type="${dtoType}">
    <#list columnList as column>
        <#if column_index = 0>
            <id column="id" property="id"/>
        <#else>
            <result column="${column.nativeColumn}" property="${column.convertColumn}"/>
        </#if>
    </#list>
</resultMap>-->

    <#--<resultMap id="${entityName}ResultMap" type="${entityType}">
        <#list columnList as column>
            <#if column_index = 0>
                <id column="id" property="id"/>
            <#else>
                <result column="${column.nativeColumn}" property="${column.convertColumn}"/>
            </#if>
        </#list>
    </resultMap>-->

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

    <#--
    <delete id="deleteById" parameterType="java.lang.Long">
        update ${tableName} set delete_flag=1 where id = ${r'#{id}'}
        <#if columnMap['version']??>
            and version=${r'#{version'}${r'}'}
        </#if>
    </delete>
    -->

    <update id="deleteByIdAndVersion">
        <#if columnMap['deleteFlag']??>
            update ${tableName}
	        set delete_flag = 1, update_version = ${r'#{updateVersion'}${r'}'} + 1
	        where id = ${r'#{id}'} and update_version = ${r'#{updateVersion'}${r'}'}
        <#else>
            delete from ${tableName} where id = ${r'#{id}'}
        </#if>        
    </update>

    <#--
    <delete id="delete" parameterType="${parameterType}">
        <#if columnMap['deleteFlag']??>
            update ${tableName} set delete_flag=1
            <#if columnMap['version']??>
                , version=${r'#{version'}${r'}'}+1
            </#if>
            where id = ${r'#{id}'}
        <#else>
            delete from ${tableName} where id = ${r'#{id}'}
        </#if>
    </delete>
    -->

    <select id="findById" parameterType="java.lang.Long" resultType="${resultType}">
        select * from ${tableName} where id = ${r'#{id}'}
        <#if columnMap['deleteFlag']??>
            AND delete_flag = 2
        </#if>
    </select>

    <select id="findCondition" resultType="${resultType}">
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

    <update id="batchUpdate" parameterType="java.util.List">
        <foreach collection="list" item="item" index="index" open="" close="" separator=";">
            update ${tableName}
            <trim prefix="SET" suffixOverrides=",">
                <#list columnMap?keys as key>
                    <#if (key == 'updateVersion')>
                        ${columnMap[key]} = ${columnMap[key]}+1,
                    <#elseif (key != 'id')>
                        <if test="item.${key} != null">
                            ${columnMap[key]} = ${r'#{item.'}${key} ${r'}'},
                        </if>
                    </#if>
                </#list>
            </trim>
            where id = ${r'#{item.id}'}
            <#if columnMap['updateVersion']??>
                and update_version=${r'#{item.updateVersion'}${r'}'}
            </#if>
        </foreach>
    </update>
</mapper>