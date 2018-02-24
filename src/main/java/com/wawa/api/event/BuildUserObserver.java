package com.wawa.api.event;

import com.mongodb.DBObject;
import com.wawa.common.util.BusiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 新生产用户
 */
public abstract class BuildUserObserver {

    static final Logger logger = LoggerFactory.getLogger(BuildUserObserver.class);

    private static final List<AfterBuildUserListener> afterListenerList = new ArrayList<>();

    /**
     * 新注册用户信息user
     */
    public static void fireAfterBuildUserEvent(final DBObject user) {
        try {
            for (final AfterBuildUserListener userListener : afterListenerList) {
                BusiExecutor.execute(new Runnable() {
                    public void run() {
                        userListener.fireEvent(user);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("fireAfterBuildUserEvent Exception : {}", e);
        }
    }

    public static void addAfterBuildUserListner(AfterBuildUserListener userListener){
        afterListenerList.add(userListener);
        logger.debug("BuildUserObserver Listener List size >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: {}", afterListenerList.size());
    }
}
