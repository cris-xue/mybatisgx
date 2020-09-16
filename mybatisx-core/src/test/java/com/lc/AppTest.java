package com.lc;

import com.lc.mybatisx.handler.ConditionMapperHandler;
import com.lc.mybatisx.wrapper.where.LinkOp;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppTest {

    @Test
    public void test01() {
        List<String> methodList = Arrays.asList("findByIdEq", "findTop50ByIdAndName", "findByIdAndName", "findByNameAndAgeEqOrderByNameDesc", "findByNameAndInputTimeBetween", "findByNameGroupByName", "findByPayStatusAndPayStatusXyzAbc", "updateByIdSelective");
        ConditionMapperHandler conditionMapperHandler = new ConditionMapperHandler(null);
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (String method : methodList) {
            List<String> keyList = conditionMapperHandler.parseConditionKeyword(method);
            map.put(method, keyList);
        }
        System.out.println(map);

        map.forEach((k, v) -> {
            // WhereWrapper whereWrapper = Keyword.buildWhereWrapper(v);
            // System.out.println(whereWrapper);
        });
    }

    @Test
    public void testLinkOp() {
        LinkOp linkOp = LinkOp.valueOf("AND");
        System.out.println(linkOp);
    }

}
