package com.wawa.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class StringHelper {

    public static Map<String, String> parseUri(String query) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(query)) {
            return map;
        }
        String[] params = query.split("=");
        if (params.length % 2 != 0) {
            return map;
        }
        for(int i = 0; i < params.length; i+=2) {
            String key = params[i];
            String value = params[i+1];
            map.put(key, value);
        }
        return map;
    }


}
