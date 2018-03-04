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
        String[] params = query.split("&");
        for(int i = 0; i < params.length; i++) {//todo
        /*if (params.length % 2 != 0) {
            return map;
        }*/
            String kv = params[i];
            String key = kv.split("=")[0];
            String value = kv.split("=")[1];
            map.put(key, value);
        }
        return map;
    }


}
