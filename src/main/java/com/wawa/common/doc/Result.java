package com.wawa.common.doc;

/**
 * 接口请求返回状态码
 */
public interface Result {
    public static final IMessageCode success = ResultCode.build(1, "success");
    public static final IMessageCode error = ResultCode.build(0, "error");
    public static final IMessageCode 余额不足 = ResultCode.build(30412, "余额不足");
    public static final IMessageCode 权限不足 = ResultCode.build(30413, "权限不足");
    public static final IMessageCode 丢失必需参数 = ResultCode.build(30406, "丢失必需参数");
    public static final IMessageCode TOKEN无效 = ResultCode.build(30405, "ACCESS_TOKEN无效");
    public static final IMessageCode 用户经验不足 = ResultCode.build(30420, "用户经验不足");
    public static final IMessageCode 用户未绑定手机号 = ResultCode.build(30414, "用户未绑定手机号");
    public static final IMessageCode 生成支付订单失败 = ResultCode.build(30415, "生成支付订单失败");
    public static final IMessageCode 非法图片 = ResultCode.build(30510, "非法图片");
    public static final IMessageCode 邀请码无效 = ResultCode.build(30610, "邀请码无效");
    public static final IMessageCode 已添加邀请码 = ResultCode.build(30611, "已添加邀请码");
    public static final IMessageCode 管理员禁用 = ResultCode.build(30612, "该功能已被管理员禁用");
    public static final IMessageCode 活动未开始 = ResultCode.build(30415, "活动未开始");
    public static final IMessageCode 活动已结束 = ResultCode.build(30416, "活动已结束");

}

