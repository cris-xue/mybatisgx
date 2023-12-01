<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

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

</mapper>