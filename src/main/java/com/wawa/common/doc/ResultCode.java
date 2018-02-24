package com.wawa.common.doc;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态码
 */
public class ResultCode{
    public static Map<Integer, String> messageCodes = new HashMap<>();

    public static IMessageCode build(final Integer code, final String msg){
        IMessageCode msgCode = new IMessageCode() {
            public int getCode() {
                return code;
            }
            public String getMessage() {
                return msg;
            }
            public String toJsonString() {
                return "{\"code\":"+code+"}";
            }

            @Override
            public int hashCode() {
                return code;
            }
        };
        messageCodes.put(code, msg);
        return msgCode;
    }
}
