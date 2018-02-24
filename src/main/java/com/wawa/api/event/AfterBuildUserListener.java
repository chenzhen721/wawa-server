package com.wawa.api.event;

import com.mongodb.DBObject;

/**
 * 新注册用户完成后监听器
 */
public interface AfterBuildUserListener {
    public abstract void fireEvent(DBObject user);
}
