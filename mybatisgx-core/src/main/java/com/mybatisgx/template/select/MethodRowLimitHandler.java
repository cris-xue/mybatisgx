package com.mybatisgx.template.select;

import com.mybatisgx.model.MethodRowLimitInfo;

import java.util.List;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2026/5/30 16:37
 */
public interface MethodRowLimitHandler {

    void apply(List<Object> selectXmlItemList, MethodRowLimitInfo methodRowLimitInfo);
}
