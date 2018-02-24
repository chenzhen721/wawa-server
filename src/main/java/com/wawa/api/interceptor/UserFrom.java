package com.wawa.api.interceptor;

import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.http.HttpClientUtil;
import com.wawa.common.util.KeyUtils;
import com.wawa.api.UserWebApi;
import com.wawa.api.Web;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

import static com.wawa.common.util.MsgDigestUtil.MD5;

public enum UserFrom implements ThirdUserBuilder {

    来吼("") {
        public _ThirdUser build(String access_token) throws IOException {
            Map _jsonMap = UserWebApi.fetchUser(access_token);
            if (((Number) _jsonMap.get("code")).intValue() != 1) {
                return _ThirdUser.EMPTY_USER;// 认证失败， token无效
            }
            final Map data = (Map) _jsonMap.get("data");
            String name = (String) data.get("nickname");
            if (null == name) {
                name = "wawa";
            }
            _ThirdUser user = new _ThirdUser(data.get("tuid"), name, true);
            user.setProps(data);
            return user;
        }
    },
    ta社区("ta-") {
        private final String SERVICE_ID = "3";
        private final String APP_ID = Web.isTest ? "7dngtifd" : "z49jj8gk";
        private final String APP_SECRET = Web.isTest ? "el5eh8hfc4w1zdsly1tz818a93xzplj2quv" : "ibnmhe91rcn5s2z2nr6hxea667ssvo4098e"; //测试
        private final String USER_API = Web.isTest ? "http://opentest.j.cn/V3/getUserInfo": "http://open.j.cn/V3/getUserInfo";//获取用户信息
        public _ThirdUser build(String access_token) throws IOException {
            String params = Web.mainRedis.opsForValue().get(KeyUtils.USER.onlyToken2id(access_token));
            if (StringUtils.isBlank(params)) {
                return _ThirdUser.EMPTY_USER;
            }
            String[] userInfo = params.split("_");
            if (userInfo.length < 2) {
                return _ThirdUser.EMPTY_USER;
            }
            String time = "" + System.currentTimeMillis();
            Map<String,String> signParams = new TreeMap<>();
            signParams.put("userId", userInfo[0]) ;
            signParams.put("deviceId", userInfo[1]);
            signParams.put("time", time);
            String sign = sign(signParams);
            StringBuffer sb = new StringBuffer();
            sb.append("appId=").append(APP_ID).append("&serviceId=3").append("&deviceId=").append(userInfo[1])
                    .append("&userId=").append(userInfo[0]).append("&time=").append(time).append("&sign=").append(sign);
            String url = USER_API + "?" + sb.toString();
            String resp = HttpClientUtil.get(url, null);
            if (StringUtils.isBlank(resp)) {
                return _ThirdUser.EMPTY_USER;
            }
            Map json = JSONUtil.jsonToMap(resp);
            if ((Integer)json.get("errorCode") != 0 || json.get("data") == null) {
                return _ThirdUser.EMPTY_USER;
            }
            Map data = (Map) json.get("data");
            //pic 头像 nickname 呢称 gender 性别
            String nickname = PREFIX + StringUtils.substring(params, 0, 8);
            if (data.get("nickName") != null) {
                nickname = (String) data.get("nickName");
                nickname = URLDecoder.decode(nickname, "UTF-8");
            }
            if (StringUtils.isBlank(nickname)) {
                nickname = "神秘玩家";
            }
            String tuid = userInfo[0];
            if (data.get("userId") != null) {
                tuid = String.valueOf(data.get("userId"));
            }
            _ThirdUser user = new _ThirdUser(namespace + tuid, nickname);

            String pic = "https://aiimg.sumeme.com/55/7/1511067183927.png";
            if (data.get("headUrl") != null) {
                pic = (String) data.get("headUrl");
            }

            user.setProp("pic", pic);
            user.setProp("third_token", access_token);
            return user;
        }

        private String sign(Map<String, String> params){
            TreeMap<String,String> sortedParams = new TreeMap<>();
            sortedParams.put("appId", APP_ID) ;
            sortedParams.put("securtKey", APP_SECRET);
            sortedParams.put("serviceId", SERVICE_ID);
            Set<String> keySet = params.keySet();
            Iterator<String> itr = keySet.iterator();
            while (itr.hasNext()){
                String key = itr.next();
                String value = params.get(key);
                sortedParams.put(key, value);
            }
            StringBuilder subd = new StringBuilder();
            for(String key:sortedParams.keySet()){
                subd.append(sortedParams.get(key));
            }
            return MD5.digest2HEX(subd.toString());
        }
    },
    微游("wy-") {

        public _ThirdUser build(String access_token) throws IOException {
            String params = Web.mainRedis.opsForValue().get(KeyUtils.USER.onlyToken2id(access_token));
            if (StringUtils.isBlank(params)) {
                return _ThirdUser.EMPTY_USER;
            }

            Map data = JSONUtil.jsonToMap(params);
            if (data == null || data.isEmpty()) {
                return _ThirdUser.EMPTY_USER;
            }

            //pic 头像 nickname 呢称 gender 性别
            String nickname = PREFIX + StringUtils.substring(params, 0, 8);
            if (data.get("nickName") != null) {
                nickname = (String) data.get("nickName");
                nickname = URLDecoder.decode(nickname, "UTF-8");
            }
            if (StringUtils.isBlank(nickname)) {
                nickname = "神秘玩家";
            }
            String tuid = "";
            if (data.get("userID") != null) {
                tuid = String.valueOf(data.get("userID"));
            }
            _ThirdUser user = new _ThirdUser(namespace + tuid, nickname);

            String pic = "https://aiimg.sumeme.com/55/7/1511067183927.png";
            if (data.get("headUrl") != null) {
                pic = (String) data.get("headUrl");
            }

            user.setProp("pic", pic);
            user.setProp("third_token", access_token);
            Map<String, Object> third = new HashMap<>();
            third.put("userID", tuid);
            third.put("channel", data.get("channel"));
            third.put("token", data.get("token"));
            user.setProp("third", third);
            return user;
        }
    },
    么么("mm-") {
        private final String MEME_USER_API = Web.isTest ? "http://test-api.memeyule.com/user/info" : "http://api.memeyule.com/user/info";

        public _ThirdUser build(String access_token) throws IOException {
            String token = Web.mainRedis.opsForValue().get(KeyUtils.USER.onlyToken2id(access_token));

            String resp = HttpClientUtil.get(MEME_USER_API + "?access_token="+token, null, HttpClientUtil.UTF8);
            if (StringUtils.isBlank(resp)) {
                return _ThirdUser.EMPTY_USER;
            }
            Map json = JSONUtil.jsonToMap(resp);
            if (json.get("code") == null || (Integer)json.get("code") != 1 || json.get("data") == null) {
                return _ThirdUser.EMPTY_USER;
            }
            Map data = (Map) json.get("data");
            //pic 头像 nickname 呢称 gender 性别
            String nickname = "神秘玩家";
            if (data.get("nick_name") != null) {
                nickname = (String) data.get("nick_name");
                nickname = URLDecoder.decode(nickname, "UTF-8");
            }
            String tuid = String.valueOf(data.get("_id"));
            /*if (data.get("userId") != null) {
                tuid = data.get("userId"));
            }*/
            _ThirdUser user = new _ThirdUser(namespace + tuid, nickname);

            String pic = "https://aiimg.sumeme.com/55/7/1511067183927.png";
            if (data.get("pic") != null) {
                pic = (String) data.get("pic");
            }
            user.setProp("pic", pic);
            user.setProp("third_token", token);
            return user;
        }
    },
    全民彩("qm-") {
        private final String USER_API =  Web.isTest ? "http://219.143.144.194:1024/lotserver/third/getInfoByToken" : "http://s.qmcai.com/lotserver/third/getInfoByToken";//获取用户信息
        public _ThirdUser build(String access_token) throws IOException {
            String token = Web.mainRedis.opsForValue().get(KeyUtils.USER.onlyToken2id(access_token));

            Map<String, String> params = new HashMap<>();
            params.put("token", token);
            String resp = HttpClientUtil.post(USER_API, params, null);
            if (StringUtils.isBlank(resp)) {
                return _ThirdUser.EMPTY_USER;
            }
            Map json = JSONUtil.jsonToMap(resp);
            if (json.get("errorCode") == null || !"0000".equals(json.get("errorCode")) || json.get("result") == null) {
                return _ThirdUser.EMPTY_USER;
            }
            Map data = (Map) json.get("result");
            //pic 头像 nickname 呢称 gender 性别
            String nickname = "神秘玩家";
            if (data.get("nickName") != null) {
                nickname = (String) data.get("nickName");
                nickname = URLDecoder.decode(nickname, "UTF-8");
            }
            String tuid = String.valueOf(data.get("userNo"));
            /*if (data.get("userId") != null) {
                tuid = data.get("userId"));
            }*/
            _ThirdUser user = new _ThirdUser(namespace + tuid, nickname);

            String pic = "https://img.lezhuale.com/49/1/1500964546481.png";
            if (data.get("headpic") != null && StringUtils.isNotBlank((String) data.get("headpic"))) {
                pic = (String) data.get("headpic");
            }
            user.setProp("pic", pic);
            user.setProp("third_token", token);
            return user;
        }
    }
    ;

    /**
     * 两位字母+ '-'
     */
    public final String namespace;

    private static final Map<String, UserFrom> cached = new HashMap<>();

    static {
        for (UserFrom uf : UserFrom.values()) {
            cached.put(uf.namespace, uf);
        }
    }

    UserFrom(String namespace) {
        this.namespace = namespace;
    }

    public static UserFrom from(String namespace) {
        return cached.get(namespace);
    }


    public _ThirdUser build(String access_token) throws IOException {
        String thirdId = Web.mainRedis.opsForValue().get(KeyUtils.USER.onlyToken2id(access_token));
        if (StringUtils.isBlank(thirdId)) {
            return _ThirdUser.EMPTY_USER;
        }
        _ThirdUser user = new _ThirdUser(namespace + thirdId, "meme" + thirdId);
        user.setProp("pic", "https://img.lezhuale.com/49/1/1500964546481.png");
        return user;
    }

    static final String PREFIX = "AW_";

    public static final char FLAG = '-';
}

interface ThirdUserBuilder {
    _ThirdUser build(String access_token) throws IOException;
}

final class _ThirdUser {

    static final _ThirdUser EMPTY_USER = new _ThirdUser(null, null);


    final Object tuid;
    final String nick_name;
    final Boolean local_user;

    private Map<String, Object> props;

    _ThirdUser(Object tuid, String nick_name) {
        this.tuid = tuid;
        this.nick_name = nick_name;
        this.local_user = Boolean.FALSE;
    }

    _ThirdUser(Object tuid, String nick_name, Boolean local_user) {
        this.tuid = tuid;
        this.nick_name = nick_name;
        this.local_user = local_user;
    }

    Object get(String key) {
        if (null == props) {
            return null;
        }
        return props.get(key);
    }

    void setProps(Map<String, Object> props) {
        this.props = props;
    }

    void setProp(String key, Object value) {
        if (null == props) {
            props = new HashMap<>();
        }
        props.put(key, value);
    }
}