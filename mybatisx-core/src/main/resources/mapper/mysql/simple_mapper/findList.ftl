<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

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