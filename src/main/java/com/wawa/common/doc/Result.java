package com.wawa.common.doc;

/**
 * 接口请求返回状态码
 */
public interface Result {
    public static final IMessageCode success = ResultCode.build(1, "success");
    public static final IMessageCode error = ResultCode.build(0, "error");
    public static final IMessageCode 余额不足 = ResultCode.build(30001, "余额不足");
    public static final IMessageCode 权限不足 = ResultCode.build(30002, "权限不足");
    public static final IMessageCode 丢失必需参数 = ResultCode.build(30003, "丢失必需参数");
    public static final IMessageCode 签名错误 = ResultCode.build(30004, "签名错误");
    public static final IMessageCode ID重复 = ResultCode.build(30005, "ID重复");
    public static final IMessageCode 机器非空闲 = ResultCode.build(30006, "机器非空闲");
    public static final IMessageCode 参数错误 = ResultCode.build(30007, "参数错误");

}

