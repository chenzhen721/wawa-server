package com.wawa.api.interceptor;

import com.wawa.api.UserWebApi;
import com.wawa.api.Web;
import com.wawa.common.util.KeyUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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