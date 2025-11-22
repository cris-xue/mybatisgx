package com.mybatisgx.template.select;

import com.mybatisgx.model.SelectOrderByInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class OrderByTemplateHandler {

    public String execute(List<SelectOrderByInfo> selectOrderByInfoList) {
        List<String> orderByList = new ArrayList<>();
        for (SelectOrderByInfo selectOrderByInfo : selectOrderByInfoList) {
            String orderBy = String.format("%s %s", selectOrderByInfo.getColumn(), selectOrderByInfo.getDirection());
            orderByList.add(orderBy);
        }
        return String.format(" ORDER BY %s", StringUtils.join(orderByList, ", "));
    }
}
