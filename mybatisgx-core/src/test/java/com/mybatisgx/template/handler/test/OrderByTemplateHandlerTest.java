package com.mybatisgx.template.handler.test;

import com.mybatisgx.model.SelectOrderByInfo;
import com.mybatisgx.template.select.OrderByTemplateHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class OrderByTemplateHandlerTest {

    @Test
    public void test() {
        SelectOrderByInfo selectOrderByInfo = new SelectOrderByInfo("name", "asc");
        SelectOrderByInfo selectOrderByInfo1 = new SelectOrderByInfo("name", "desc");
        OrderByTemplateHandler orderByTemplateHandler = new OrderByTemplateHandler();
        String orderBySql = orderByTemplateHandler.execute(Arrays.asList(selectOrderByInfo, selectOrderByInfo1));
        Assert.assertEquals(" ORDER BY name asc, name desc", orderBySql);
    }
}
