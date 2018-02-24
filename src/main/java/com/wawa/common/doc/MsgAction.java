package com.wawa.common.doc;

/**
 * 房间内消息类型
 */
public enum MsgAction {
    UnKnown(null),
    上麦("mic.on"),下麦("mic.off"),邀请上麦("mic.invite"),
    拒绝上麦("mic.refuse"),
    踢人("manage.kick_by"),
    禁言("manage.shutUp_by"),
    设置职位("family.priv"),
    加入家族("family.add"),
    退出家族("family.exit"),
    解散家族("family.terminate"),
    踢出家族("family.kick"),
    家族事件("event.status"),
    //家族事件个人消息("event.status"),
    加入事件("event.join"),
    捐献金币("coin.donate"),
    家族攻击("family.ack"),
    遭受攻击("family.def"),
    使用道具("use.item"),
    领取贡献("donate.reward"),
    个人遭受攻击("user.def"),
    官方消息("official.msg"),
    家族红包("family.reward"),
    现金红包("redpacket.send"),
    钻石红包("diamondpacket.send"),
    抽取现金红包("redpacket.draw"),
    抽取钻石红包("diamondpacket.draw"),
    哥布林事件("goblin.steal"),
    哥布林世界("goblin.open"),
    广播事件("message.broadcast"),
    提升战力事件("vip.prestige"),

    抓娃娃("catchu.type"),
    ;

    private String id;

    MsgAction(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

}
