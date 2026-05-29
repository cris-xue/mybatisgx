package com.mybatisgx.utils;

import java.util.List;

public class ArrayUtils {

    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[0]);
    }
}
