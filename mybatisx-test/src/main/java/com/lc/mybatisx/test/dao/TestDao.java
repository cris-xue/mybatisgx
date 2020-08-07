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
    int updateByIdAndNameOrUserName(Test test);

    @MapperMethod(type = MethodType.QUERY)
    List<TestDTO> findByPayStatusAndPayStatusXyzAbc(@Param("payStatus") String payStatus, @Param("payStatusXyzAbc") String payStatusXyzAbc);

    @MapperMethod(type = MethodType.QUERY)
    Map<String, Object> findByUsername(@Param("username") String username);

    @MapperMethod(type = MethodType.QUERY)
    List<Map<String, Object>> findByPayStatusLteqAndPayStatus1NotOrXyzAbc1Lt(@Param("payStatus") String payStatus,
                                                                             @Param("payStatus1") String payStatus1,
                                                                             @Param("xyzAbc1") String xyzAbc1);

}
