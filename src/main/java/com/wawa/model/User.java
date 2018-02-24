package com.wawa.model;


/**
 * 用户相关内容
 */
public interface User {


    String access_token = "access_token" ;
    /**
     * 用户关注
     */
    String following = "following";
    /**
     * 关闭开播通知
     */
    String closeNotice = "close_notice";
     /**
     * 用户粉丝
     */
    String followers = "followers";
    /**
     * 用户好友
     */
    String friends = "friends";
    //经验
    String Exp = "exp";
    //等级
    String Level = "level";
    //背包
    String bag = "bag";

    interface VIP {
        String vip  = "vip"; // 2...
        String vip_expires = "vip_expires"; // timestamp
        String vip_hiding = "vip_hiding"; // 0 1

        String vip_normal  = "vip_normal"; //1...
        String vip_expires_normal = "vip_expires_normal"; // timestamp
        String vip_hiding_normal = "vip_hiding_normal"; // 0 1

        Integer HIGH_LEVEL = 2;
        Integer NORMAL_LEVEL = 2;//TODO 2016/11/17 合并为高级vip
        Integer TMP_LEVEL = -1; //TODO 2016/11/17 合并为高级vip试用vip


        // VIP to shutup or forbid erverday.
        Integer MANAGE_LIMIT = 3;
        Integer SHUTUP_LIMIT = 4;
        Integer KICK_LIMIT = 4;
    }


}
