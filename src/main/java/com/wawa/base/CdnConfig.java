package com.wawa.base;

import com.wawa.AppProperties;
import com.wawa.common.util.MsgDigestUtil;
import com.wawa.model.LiveVideoType;

import java.util.HashMap;
import java.util.Map;


/**
 * 视频流CDN 配置信息
 */
public enum CdnConfig implements Cdn{
    网宿{
        final String PULL_URL =  "rtmp://l.ws.sumeme.com";
        final String PUSH_URL = "rtmp://t.ws.sumeme.com";
        final String HLS_URL = "http://hls.ws.sumeme.com";
        final String FLV_PULL_URL = "http://l.ws.sumeme.com";

        public String getRTMPPushUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+time);
            return PUSH_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+time);
            return PULL_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+ ".flv"+time);
            return FLV_PULL_URL+"/meme/"+roomId + ".flv" +"?k="+wsSecret+"&t="+time;
        }



        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+"_"+user_id+time);
            return PULL_URL+"/meme/"+ roomId + "_" + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+"_"+user_id+time);
            return PUSH_URL+"/meme/"+roomId + "_"  + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_" + user_id +"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return FLV_PULL_URL+"/meme/"+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
        }
    },
    帝联{
        final String PULL_URL =  "rtmp://l.dl.memezhibo.cn";
        final String PUSH_URL = "rtmp://t.dl.memezhibo.cn";
        final String HLS_URL = "http://hls.dl.memezhibo.cn";
        final String FLV_PULL_URL = "http://l.dl.memezhibo.cn";

        public String getRTMPPushUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme1/"+roomId+time);
            return PUSH_URL+"/meme1/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme1/"+roomId+time);
            return PULL_URL+"/meme1/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme1/"+roomId+time);
            return HLS_URL + "/meme1/"+roomId+"/index.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme1/"+roomId+"_"+user_id+time);
            return PULL_URL+"/meme1/"+ roomId + "_" + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme1/"+roomId+"_"+user_id+time);
            return PUSH_URL+"/meme1/"+roomId + "_"  + user_id +"?k="+wsSecret+"&t="+time;
        }
        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+ ".flv"+time);
            return PUSH_URL+"/meme/"+roomId + ".flv" +"?k="+wsSecret+"&t="+time;
        }
        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_" + user_id +"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return FLV_PULL_URL+"/meme/"+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
        }
    },
    ucloud{
        final String PULL_URL =  "rtmp://l.ucloud.sumeme.com";
        final String PUSH_URL = "rtmp://t.ucloud.sumeme.com";
        final String HLS_URL = "http://hls.ucloud.sumeme.com";
        final String FLV_PULL_URL = "http://l.ucloud.sumeme.com";

        public String getRTMPPushUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+time);
            return PUSH_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+time);
            return PULL_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_aac/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_aac/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+"_"+user_id+time);
            return PULL_URL+"/meme/"+ roomId + "_" + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+"_"+user_id+time);
            return PUSH_URL+"/meme/"+roomId + "_"  + user_id +"?k="+wsSecret+"&t="+time;
        }
        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+ ".flv"+time);
            return FLV_PULL_URL+"/meme/"+roomId + ".flv" +"?k="+wsSecret+"&t="+time;
        }
        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_" + user_id +"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return FLV_PULL_URL+"/meme/"+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
        }
    },
    星域{
        final String PULL_URL =  "rtmp://l.xingyu.sumeme.com";
        final String PUSH_URL = "rtmp://t.xingyu.sumeme.com";
        final String HLS_URL = "http://hls.xingyu.sumeme.com";
        final String FLV_PULL_URL = "http://l.dl.memezhibo.cn";
        public String getRTMPPushUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+time);
            return PUSH_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+time);
            return PULL_URL+"/meme/"+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+".m3u8"+time);
            return HLS_URL + "/meme/"+roomId+".m3u8?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+"_"+user_id+time);
            return PULL_URL+"/meme/"+ roomId + "_" + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + "/meme/"+roomId+"_"+user_id+time);
            return PUSH_URL+"/meme/"+roomId + "_"  + user_id +"?k="+wsSecret+"&t="+time;
        }
        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/meme/"+roomId+ ".flv"+time);
            return PUSH_URL+"/meme/"+roomId + ".flv" +"?k="+wsSecret+"&t="+time;
        }
        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return HLS_URL + "/meme/"+roomId+"_" + user_id +"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            return FLV_PULL_URL+"/meme/"+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
        }
    },
    即构{
        private final boolean isTest  = AppProperties.get("api.domain").contains("test.");

        final String PULL_URL =  isTest ? "rtmp://rtmp.dndemo.zego.im/zego/" : "rtmp://rtmp.zego.memeyule.com/memeyule/";
        final String PUSH_URL = isTest ? "rtmp://dndemo.zego.im/zego/" : "rtmp://publish.zego.memeyule.com/memeyule/";
        final String HLS_URL = isTest ? "http://hls.demo.zego.im/livedemo/" : "http://hls.zego.memeyule.com/memeyule/";
        final String FLV_PULL_URL = isTest ? "http://flv.dndemo.zego.im/zego/" :"http://flv.zego.memeyule.com/memeyule/";

        public String getRTMPPushUrl(Integer roomId){
            String time = hexSeconds();
            return PUSH_URL+roomId;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            return PULL_URL+roomId;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            return HLS_URL+roomId+".m3u8";
        }

        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            //return PULL_URL+"/memeyule/"+ roomId + "_" + user_id;
            return PULL_URL+user_id;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            return PUSH_URL+roomId + "_"  + user_id;
        }
        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + "/memeyule/"+roomId+ ".flv"+time);
            return FLV_PULL_URL+roomId + ".flv";
        }
        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            //return HLS_URL + "/meme/"+roomId+"_" + user_id +"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
            return HLS_URL+user_id+"_a/playlist.m3u8?k="+wsSecret+"&t="+time;
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+"/meme/"+roomId+"_a/playlist.m3u8"+time);
            //return FLV_PULL_URL+"/meme/"+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
            return FLV_PULL_URL+user_id+".flv" +"?k="+wsSecret+"&t="+time;
        }
    },
    又拍云{
        final String PULL_URL =  "rtmp://l.up.sumeme.com";
        final String PUSH_URL = "rtmp://t.up.sumeme.com";
        final String HLS_URL = "http://l.up.sumeme.com";
        final String FLV_PULL_URL = "http://l.up.sumeme.com";
        final String SPACE_NAME = "/imeme/";
        final String PUSH_DOMAIN = "t.up.sumeme.com";

        public String getRTMPPushUrl(Integer roomId){
            long valid_ts = System.currentTimeMillis() / 1000 + 5 * 60; // unix时间格式
            String tokenSecret = MsgDigestUtil.MD5.digest2HEX(PUSH_DOMAIN + SPACE_NAME + roomId + valid_ts + push_key);
            return PUSH_URL+SPACE_NAME+roomId +"?domain="+PUSH_DOMAIN+"&valid_ts="+valid_ts+"&token="+tokenSecret;
        }

        public String getRTMPPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + SPACE_NAME+roomId+time);
            return PULL_URL+SPACE_NAME+roomId +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+SPACE_NAME+roomId+".m3u8"+time);
            return HLS_URL + SPACE_NAME+roomId+"_222w.m3u8?k="+wsSecret+"&t="+time; // _222w标示转码
        }

        public String getFLVPullUrl(Integer roomId){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + SPACE_NAME+roomId+ ".flv"+time);
            return FLV_PULL_URL+SPACE_NAME+roomId + ".flv" +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key + SPACE_NAME+roomId+"_"+user_id+time);
            return PULL_URL+SPACE_NAME+ roomId + "_" + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getRTMPPushUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(push_key + SPACE_NAME+roomId+"_"+user_id+time);
            return PUSH_URL+SPACE_NAME+roomId + "_"  + user_id +"?k="+wsSecret+"&t="+time;
        }

        public String getHLSPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+SPACE_NAME+roomId+".m3u8"+time);
            return HLS_URL + SPACE_NAME+roomId+"_" + user_id +"_222w.m3u8?k="+wsSecret+"&t="+time; // _222w标示转码
        }

        public String getFLVPullUrl(Integer roomId, Integer user_id){
            String time = hexSeconds();
            String wsSecret = MsgDigestUtil.MD5.digest2HEX(pull_key+SPACE_NAME+roomId+".flv"+time);
            return FLV_PULL_URL+SPACE_NAME+roomId + "_"  + user_id +".flv" +"?k="+wsSecret+"&t="+time;
        }
    };

    public static String hexSeconds(){
        return Long.toHexString(System.currentTimeMillis()/1000);
    }

    public static final String push_key = "jel0_d3_uwpzq7e1_3q4vkdyeig";
    public static final String pull_key = "f4_d0s3gp_zfir5jr3qwxv19";

    private static final Map<Integer,CdnConfig> cached = new HashMap<>();
    static {
        cached.put(LiveVideoType.网宿.ordinal(), 网宿);
        cached.put(LiveVideoType.帝联.ordinal(), 帝联);
        cached.put(LiveVideoType.UCloud.ordinal(), ucloud);
        cached.put(LiveVideoType.星域.ordinal(), 星域);
        cached.put(LiveVideoType.即构.ordinal(), 即构);
        cached.put(LiveVideoType.又拍云.ordinal(), 又拍云);
    }

    public static CdnConfig from(Integer v_type){
        CdnConfig cdn = cached.get(v_type);
        return cdn == null ? 网宿 : cdn;
    }
}
