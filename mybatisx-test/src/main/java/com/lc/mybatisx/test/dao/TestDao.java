package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.dto.TestDTO;
import com.lc.mybatisx.test.model.entity.Test;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TestDao extends SimpleDao<Test, Long> {

    @MapperMethod(type = MethodType.UPDATE)
    int updateByIdAndNameOrUsername(@Param("id") Long id, @Param("name") String name, @Param("username") String username);

    @MapperMethod(type = MethodType.QUERY)
    List<TestDTO> findByPayStatusAndPayStatusXyzAbc(@Param("payStatus") String payStatus, @Param("payStatusXyzAbc") String payStatusXyzAbc);

    @MapperMethod(type = MethodType.QUERY)
    Map<String, Object> findByUsername(@Param("username") String username);

    @MapperMethod(type = MethodType.QUERY)
    List<Map<String, Object>> findByPayStatusAndPayStatusXyzAbc1(@Param("payStatus") String payStatus, @Param("payStatusXyzAbc1") String payStatusXyzAbc1);

}
