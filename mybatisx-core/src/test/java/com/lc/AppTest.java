package com.lc;

import com.lc.mybatisx.handler.ConditionMapperHandler;
import com.lc.mybatisx.wrapper.WhereWrapper;
import com.lc.mybatisx.wrapper.where.Keyword;
import org.junit.Test;

import java.util.*;

public class AppTest {

    private List<String> keywork = Arrays.asList("findBy", "findTop", "update", "Selective");

    @Test
    public void test01() {
        List<String> methodList = Arrays.asList("findTop50ByIdAndName", "findByIdAndName", "findByNameAndAgeEqOrderByNameDesc", "findByNameAndInputTimeBetween", "findByNameGroupByName", "findByPayStatusAndPayStatusXyzAbc", "updateByIdSelective");
        ConditionMapperHandler conditionMapperHandler = new ConditionMapperHandler(null);
        Map<String, List<String>> map = new HashMap<>();
        for (String method : methodList) {
            List<String> keyList = conditionMapperHandler.parseConditionKeyword(method);
            map.put(method, keyList);
        }
        System.out.println(map);

        List<String> aaa = new ArrayList<>();
        map.forEach((k, v) -> {
            WhereWrapper whereWrapper = Keyword.buildWhereWrapper(v);
            System.out.println(whereWrapper);
        });
    }

}
