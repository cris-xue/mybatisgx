package com.mybatisgx.template.select;

import com.mybatisgx.model.SelectPageInfo;

/**
 * 分页模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class LimitTemplateHandler {

    public String execute(SelectPageInfo selectPageInfo) {
        return String.format(" limit %s %s", selectPageInfo.getIndex(), selectPageInfo.getSize());
    }
}
