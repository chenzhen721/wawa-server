package com.wawa.common.util;


import com.wawa.base.ext.RestExtension;
import com.wawa.model.User;
import groovy.transform.CompileStatic;

/**
 * 约定的key 值
 */
@CompileStatic
public abstract class KeyUtils {
    public static byte[] serializer(String string) {
        return RestExtension.asBytes(string);
    }

    private static final String SPLIT_CHAR = ":";

    public static String accessToken(String token) {
        return "token:" + token;
    }

    public static String user_online_list() {
        return "im:online";
    }

    public static class USER {

        public static final String USER = "user:";

        public static String vip(Object uid) {
            return USER + uid + ":vip";
        }

        public static String authCode(Object uid) {
            return USER + uid + ":auth";
        }

        public static String token(Object uid) {
            return USER + uid + SPLIT_CHAR + User.access_token;
        }

        public static String following(Object uid) {
            return USER + uid + SPLIT_CHAR + User.following;
        }

        public static String blackClient(String uid) {
            return "uidblack:" + uid;
        }

        public static String onlyToken2id(String token) {
            return "ot2id:" + token;
        }

    }

    /**
     * 房间
     */
    public static class ROOM {
        public static final String ROOM = "room:";

        @Deprecated
        public static String shutupSet(Object roomId) {
            return ROOM + roomId + ":shutup:set";
        }

        @Deprecated
        public static String kickSet(Object roomId) {
            return ROOM + roomId + ":kick:set";
        }

        public static String users(Object roomId) {
            return ROOM + roomId + ":users";
        }

    }

}
