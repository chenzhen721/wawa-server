package com.wawa.web;

/**
 * 视频流cdn
 */
public interface Cdn {
    public String getRTMPPushUrl(Integer roomId);
    public String getRTMPPullUrl(Integer roomId);
    public String getFLVPullUrl(Integer roomId);
    public String getFLVPullUrl(Integer roomId, Integer user_id);
    public String getHLSPullUrl(Integer roomId);
    public String getHLSPullUrl(Integer roomId, Integer user_id);
    public String getRTMPPushUrl(Integer roomId, Integer user_id);
    public String getRTMPPullUrl(Integer roomId, Integer user_id);

    /*
    public String getRTMPPushUrl(Integer roomId);
    */
}