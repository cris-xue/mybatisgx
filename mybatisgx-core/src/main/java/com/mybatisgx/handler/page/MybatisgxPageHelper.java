package com.mybatisgx.handler.page;

import com.github.pagehelper.PageHelper;
import com.mybatisgx.dao.Page;
import com.mybatisgx.dao.Pageable;

import java.util.List;
import java.util.function.Supplier;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/13 19:45
 */
public class MybatisgxPageHelper {

    public static <T> T startPage(Pageable pageable, T target) {
        return target;
    }

    public static <T> Page<T> startPage(Pageable pageable, Supplier<List<T>> target) {
        com.github.pagehelper.Page page = PageHelper.startPage(pageable.getPageNo(), pageable.getPageSize());
        List<T> list = target.get();
        return new Page<>(page.size(), list);
    }
}
