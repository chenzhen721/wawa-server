# 爱玩直播文档


   * 测试服地址： test-aiapi.memeyule.com
   * 正式服地址： aiapi.memeyule.com



## 用户信息

### 请求地址
    /user/info

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/user/info?access_token=e50e7ffa06e4b62e7dfe3e9fee3066b9](http://test-aiapi.memeyule.com/user/info?access_token=e50e7ffa06e4b62e7dfe3e9fee3066b9)

### 返回值
	{
	  "data": {
	    "_id": 1201543,
	    "pic": "http://test-aiimg.sumeme.com/7/7/1201543_0.jpg?v=1486390833229",
	    "location": "爱玩王国",
	    "nick_name": "程子健",
	    "priv": 2,
	    "finance": {
	      "coin_count": 9630040,
	      "bean_count_total": 8
	    },
	    "mission": {
	      "add_following": 0,
	      "first_award": 1
	    },
	    "star": {
	      "room_id": 1201543
	    }
	  },
	  "exec": 8,
	  "code": 1
	}


## 关注列表

### 请求地址
    /user/following_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求地址
[http://test-aiapi.memeyule.com/user/following_list/1bc05b1465ddb169ae5ee786b258bd64](http://test-aiapi.memeyule.com/user/following_list/1bc05b1465ddb169ae5ee786b258bd64)

### 返回值
    {
    "data": {
        "users": [
            {
                "_id": 1201443,
                "pic": "http://test-aiimg.sumeme.com/35/3/1201443_0.jpg?v=1486390601504",
                "nick_name": "程子健",
                "finance": {
                    "bean_count_total": 1047352
                },
                "star": { }
            },
            {
                "_id": 1203294,
                "pic": "http://q.qlogo.cn/qqapp/1105945342/FFD5590A81D02F6B5E8198CB8D9274A5/100",
                "nick_name": "天仇帅锅",
                "finance": {
                    "bean_count_total": 228903
                },
                "star": { }
            },
            {
                "_id": 1203543,
                "pic": "http://test-aiimg.sumeme.com/23/7/1203543_0.jpg?v=1487056628965",
                "nick_name": "萌新",
                "finance": {
                    "bean_count_total": 401919
                },
                "star": { }
            }
        ],
        "rooms": [
            {
                "_id": 1201443,
                "xy_star_id": 1201443,
                "live": true,
                "visiter_count": 2,
                "found_time": 1484818354501,
                "live_type": 2,
                "timestamp": 1487321156637,
                "position": {
                    "province": "上海",
                    "city": "上海市",
                    "region": "闵行区",
                    "coordinate_x": "0",
                    "coordinate_y": "0"
                },
                "game_id": 1,
                "followers": 11,
                "pic": "http://test-aiimg.sumeme.com/35/3/1201443_0.jpg?v=1486390601504",
                "nick_name": "程子健",
                "finance": {
                    "bean_count_total": 1047352
                },
                "star": { }
            },
            {
                "_id": 1203543,
                "xy_star_id": 1203543,
                "live": false,
                "visiter_count": 0,
                "found_time": 1487139292180,
                "live_type": 2,
                "game_id": 1,
                "timestamp": 1487317705906,
                "position": null,
                "followers": 4,
                "pic": "http://test-aiimg.sumeme.com/23/7/1203543_0.jpg?v=1487056628965",
                "nick_name": "萌新",
                "finance": {
                    "bean_count_total": 401919
                },
                "star": { }
            },
            {
                "_id": 1203294,
                "xy_star_id": 1203294,
                "live": false,
                "visiter_count": 0,
                "found_time": 1486560387619,
                "live_type": 2,
                "game_id": "",
                "timestamp": 1487316244809,
                "position": null,
                "followers": 10,
                "pic": "http://q.qlogo.cn/qqapp/1105945342/FFD5590A81D02F6B5E8198CB8D9274A5/100",
                "nick_name": "天仇帅锅",
                "finance": {
                    "bean_count_total": 228903
                },
                "star": { }
            }
        ],
        "count": 3,
        "lives": 1
    },
    "exec": 4,
    "code": 1
}


## 新手任务:首次领取免费阳光

### 请求地址
    /mission/first_award

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/mission/first_award?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69
](http://test-aiapi.memeyule.com/mission/first_award?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69
)

### 返回值
    {
    "data": {
        "award_flag": false
    },
    "exec": 2,
    "code": 1
	}


## 任务领奖

### 请求地址
    /mission/award

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
mission_id|T|任务id

### 请求示例
[http://test-aiapi.memeyule.com/mission/award?mission_id=first_award&access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69](http://test-aiapi.memeyule.com/mission/award?mission_id=first_award&access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69)

### 返回值
    {
    "exec": 13,
    "code": 1
	}

<font color=red size=2>code:1 代表领取成功， 其他值代表领取失败。领取过再调用也会领取失败</font>


## 新人任务列表

### 请求地址
    /mission/mission_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/mission/mission_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69](http://test-aiapi.memeyule.com/mission/mission_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69)

### 返回值
    {
    "data": [
        {
            "_id": "first_award",
            "total": 1,
            "title": "新用户领取一次免费阳光",
            "level": 1,
            "type": 1,
            "coin_count": 30,
            "complete": 1486968730138
        },
        {
            "_id": "follow_one_star",
            "total": 1,
            "title": "关注1个主播",
            "level": 1,
            "type": 1,
            "coin_count": 10,
            "complete": 0
        },
        {
            "_id": "play_game_once",
            "total": 1,
            "title": "玩一局游戏",
            "level": 1,
            "type": 1,
            "coin_count": 10,
            "complete": 0
        }
    ],
    "exec": 5,
    "code": 1
}


## 日常任务列表

### 请求地址
    /mission/daily_mission_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/mission/daily_mission_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69](http://test-aiapi.memeyule.com/mission/daily_mission_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69)

### 返回值
    {
    "data": [
        {
            "_id": "first_award",
            "total": 1,
            "title": "新用户领取一次免费阳光",
            "level": 1,
            "type": 1,
            "coin_count": 30,
            "complete": 1486968730138
        },
        {
            "_id": "follow_one_star",
            "total": 1,
            "title": "关注1个主播",
            "level": 1,
            "type": 1,
            "coin_count": 10,
            "complete": 0
        },
        {
            "_id": "play_game_once",
            "total": 1,
            "title": "玩一局游戏",
            "level": 1,
            "type": 1,
            "coin_count": 10,
            "complete": 0
        }
    ],
    "exec": 5,
    "code": 1
	}


## 申请主播

### 请求地址
    /user/apply

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
video_path|T|审核视频地址
real_name|T|真实姓名
live_type|T|直播间类型 默认 2 手机

### 请求示例
[http://test-aiapi.memeyule.com/user/apply/d2f9c45e4d7ce4dba2d9bfb27c46ce69?video_path=%2F1201543%2F0117%2F8e47d91561af380afaf92ccb7393a4b9.mp4&live_type=2&sfz=410888888888888888&real_name=%E7%A8%8B%E5%AD%90%E5%81%A5
](http://test-aiapi.memeyule.com/user/apply/d2f9c45e4d7ce4dba2d9bfb27c46ce69?video_path=%2F1201543%2F0117%2F8e47d91561af380afaf92ccb7393a4b9.mp4&live_type=2&sfz=410888888888888888&real_name=%E7%A8%8B%E5%AD%90%E5%81%A5
)

### 返回值
    {"code":1}

----------------------------------------------------------------------------------

## 上传直播间图片

### 请求地址
    /user/upload_app_room_pic

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
id1|T|直播间id

### 请求示例
[http://test-aiapi.memeyule.com/user/upload_app_room_pic/306903ac31019f26a2211332f5b45070/1202563](http://test-aiapi.memeyule.com/user/upload_app_room_pic/306903ac31019f26a2211332f5b45070/1202563)

### 返回值
    {
    "code": 1,
    "data": {
        "pic": "${pic_domain}${filePath}?v=${System.currentTimeMillis()}"
    }
	}

### 备注
<font color=red size=2>该接口是post请求，mediaType=multipart/form-data</font>

## 开播

### 请求地址
    /live/on

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
game_id|T|游戏类型 默认0 是普通直播间
province|F|省
city|F|市
region|F|区
coordinate_x|F|x坐标
coordinate_y|F|y坐标

### 请求示例
[http://test-aiapi.memeyule.com/live/on/c00bf137bebb51e18a49b9666c5ac335/1201863?p=2&live_type=2&ext=eyJtb2RlbCI6ImlQaG9uZSA1cyAoQTE0NTdcL0ExNTE4XC9BMTUyOFwvQTE1MzApIiwib3MiOiIxMC4yIiwiYXBwVmVyc2lvbiI6IjAuMC4xIiwiY2hhbm5lbCI6ImlPU19haXdhbiIsInBsYXRmb3JtIjoiaU9TIn0=&v_type=7&rtmp_url=&hls_url=&flv_url=&game_id=1
](http://test-aiapi.memeyule.com/live/on/c00bf137bebb51e18a49b9666c5ac335/1201863?p=2&live_type=2&ext=eyJtb2RlbCI6ImlQaG9uZSA1cyAoQTE0NTdcL0ExNTE4XC9BMTUyOFwvQTE1MzApIiwib3MiOiIxMC4yIiwiYXBwVmVyc2lvbiI6IjAuMC4xIiwiY2hhbm5lbCI6ImlPU19haXdhbiIsInBsYXRmb3JtIjoiaU9TIn0=&v_type=7&rtmp_url=&hls_url=&flv_url=&game_id=1
)

### 返回值
    {
    "data": {
        "live_id": "1201863_20170213152321",
        "push_url": "rtmp://pili-publish.sumeme.com/memezhibo/1201863?e=1486974201&token=VSJzpYVgw661jH4bSLSgo9yjRtRNrk8b5OBc5krM:iVfmS75AnRKxq00gpdiFOPygpYw="
    },
    "exec": 162,
    "code": 1
	}


## 关播

### 请求地址
    /live/off

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
sort|F|房间按某个维度排序
order|F|排序规则

### 请求示例
[http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863](http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863)

### 返回值
    {
    "code": 1
	}


## 热门列表

### 请求地址
    /public/room_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
sort|F|房间搜索排序规则
order|F|升序/降序

### 请求示例
[http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863](http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863)

### 返回值
    {
    "code": 1
	}


## 游戏列表

### 请求地址
    /game/game_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
game_id|F|游戏id 用于查询单个游戏的信息

### 请求示例
[http://test-aiapi.memeyule.com/game/game_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69](http://test-aiapi.memeyule.com/game/game_list?access_token=d2f9c45e4d7ce4dba2d9bfb27c46ce69)

### 返回值
    {
    "data": [
        {
            "_id": "12",
            "timestamp": 1486622844186,
            "status": true,
            "name": "牛牛",
            "pic_url": "http://test.img.sumeme.com/27/3/1480904096283.jpg"
        }
    ],
    "exec": 2,
    "code": 1
	}

## 关播

### 请求地址
    /live/off

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863](http://test-aiapi.memeyule.com/live/off/c00bf137bebb51e18a49b9666c5ac335/1201863)

### 返回值
    {
    "exec": 80,
    "code": 1
	}

## 礼品列表

### 请求地址
    /show/gift_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|

### 请求示例
[http://test-aiapi.memeyule.com/show/gift_list](http://test-aiapi.memeyule.com/show/gift_list)

### 返回值
    {
    "data": {
        "categories": [
            {
                "_id": 2,
                "vip": false,
                "lucky": false,
                "order": 0,
                "ratio": 0.4,
                "status": true,
                "name": "手机礼物"
            },
            {
                "_id": 3,
                "vip": false,
                "lucky": false,
                "order": 0,
                "ratio": 0.2,
                "status": true,
                "name": "a"
            },
            {
                "_id": 1,
                "vip": false,
                "lucky": false,
                "order": 1,
                "ratio": 0.4,
                "status": true,
                "name": "世界礼物"
            }
        ],
        "gifts": [
            {
                "_id": 1,
                "desc": "",
                "status": true,
                "isNew": true,
                "star": false,
                "is_mark": true,
                "isHot": true,
                "pic_url": "http://test-aiimg.sumeme.com/5/5/1486802774213.png",
                "sale": true,
                "is_all": false,
                "order": 1,
                "ratio": 0.4,
                "name": "棒棒糖",
                "app_swf_url": "",
                "coin_price": 20,
                "pic_pre_url": "",
                "isHide": true
            }
        ]
    },
    "exec": 2,
    "code": 1
	}


## 送礼

### 请求地址
    /room/send_gift

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
id1|T|直播间id
marquee|F|跑道判断标识符
count|F|礼品数量 默认1

### 请求示例
[http://test-aiapi.memeyule.com/room/send_gift/2ac4b189622357417cd2d3c9901c40a2/1201071/180?count=1&user_id=1201071&marquee=yes](http://test-aiapi.memeyule.com/room/send_gift/2ac4b189622357417cd2d3c9901c40a2/1201071/180?count=1&user_id=1201071&marquee=yes)

### 返回值
    {
    "exec": 2,
    "code": 1
	}


## 直播间排行榜

### 请求地址
    /rank/room_user_live

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
id1|T|直播间id
size|F|集合长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/room_user_live/1203294?size=99](http://test-aiapi.memeyule.com/rank/room_user_live/1203294?size=99)

### 返回值
    {
    "data": [
        {
            "_id": 1203303,
            "mm_no": 1203303,
            "pic": "http://img.sumeme.com/22/6/1403510731734.jpg",
            "nick_name": "萌新91670",
            "finance": {
                "coin_spend_total": 0
            },
            "s": "4",
            "coin_spend": 35000,
            "week_spend": 321380
        },
        {
            "_id": 1201863,
            "mm_no": 1201863,
            "pic": "http://test-aiimg.sumeme.com/7/7/1201863_0.jpg?v=1484822208335",
            "nick_name": "苏帕塞亚今",
            "finance": {
                "bean_count_total": 72,
                "coin_spend_total": 44961
            },
            "s": null,
            "coin_spend": 2000
        },
        {
            "_id": 1202614,
            "mm_no": 1202614,
            "pic": "http://q.qlogo.cn/qqapp/1105945342/4FECC46B31277DEFAB075E659CFD75FE/100",
            "nick_name": "James.li",
            "finance": {
                "coin_spend_total": 15,
                "bean_count_total": 34240
            },
            "s": null,
            "coin_spend": 400,
            "week_spend": 87374
        }
    ],
    "exec": 15,
    "code": 1
	}


## 直播间总榜

### 请求地址
    /rank/room_user/total

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
id1|T|直播间id
size|F|集合长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/room_user_total/1203913?size=99](http://test-aiapi.memeyule.com/rank/room_user_total/1203913?size=99)

### 返回值
    {
    "data": [
        {
            "_id": 1203303,
            "mm_no": 1203303,
            "pic": "http://img.sumeme.com/22/6/1403510731734.jpg",
            "nick_name": "萌新91670",
            "finance": {
                "coin_spend_total": 0
            },
            "rank": 1,
            "coin_spend": 37800,
            "week_spend": 321380
        },
        {
            "_id": 1201863,
            "mm_no": 1201863,
            "pic": "http://test-aiimg.sumeme.com/7/7/1201863_0.jpg?v=1484822208335",
            "nick_name": "苏帕塞亚今",
            "finance": {
                "bean_count_total": 72,
                "coin_spend_total": 44961
            },
            "rank": 2,
            "coin_spend": 10420
        },
        {
            "_id": 1203823,
            "mm_no": 1203823,
            "pic": "http://wx.qlogo.cn/mmopen/NactR9nxbVYCouq861eofjQQ0EyNk9RkbhHy6cWicY1QUssJnI3cickXIaOP5vN2fkT0Uq52fuxZn1lpk73jjL4ZmqSCzHDqvib/0",
            "nick_name": "JAmes",
            "finance": { },
            "rank": 3,
            "coin_spend": 8740
        },
        {
            "_id": 1203543,
            "mm_no": 1203543,
            "pic": "http://img.sumeme.com/22/6/1403510731734.jpg",
            "nick_name": "萌新315817/(",
            "finance": {
                "bean_count_total": 139119
            },
            "rank": 4,
            "coin_spend": 4940,
            "week_spend": 311520
        },
        {
            "_id": 1203293,
            "mm_no": 1203293,
            "pic": "http://test-aiimg.sumeme.com/29/5/1203293_0.jpg?v=1486539124812",
            "nick_name": "MU-卓维涵",
            "finance": { },
            "rank": 5,
            "coin_spend": 3300,
            "week_spend": 233620
        },
        {
            "_id": 1203713,
            "mm_no": 1203713,
            "pic": "http://q.qlogo.cn/qqapp/1105945342/25B9FB666553F03F92ACE50D98E255BB/100",
            "nick_name": "花话",
            "finance": { },
            "rank": 6,
            "coin_spend": 30
        }
    ],
    "exec": 16,
    "code": 1,
    "ctime": 1486971714384
	}


## 更新银行卡信息

### 请求地址
    /user/update_bank

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
bank|T|银行信息,
bank_user_name|T|真实姓名
bank_id|T|银行账号
bank_location|T|开户行信息

### 请求示例
[http://test-aiapi.memeyule.com/user/update_bank](http://test-aiapi.memeyule.com/user/update_bank)

### 返回值
    {"code":1}


## 银行信息

### 请求地址
    /user/bank_info

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求参数
[http://test-aiapi.memeyule.com/user/bank_info/c00bf137bebb51e18a49b9666c5ac335](http://test-aiapi.memeyule.com/user/bank_info/c00bf137bebb51e18a49b9666c5ac335)

### 返回值
    {
    "data": {
        "_id": "1201863_1484807551133",
        "real_name": "轩辕",
        "bank_id": "6215454545454",
        "bank_user_name": "掌哈哈哈",
        "bank": "中国农业银行",
        "bank_location": "我q"
    },
    "exec": 2,
    "code": 1
  	}

## 申请提现

### 请求地址
    /live/withdrawl

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/live/withdrawl/c00bf137bebb51e18a49b9666c5ac335](http://test-aiapi.memeyule.com/live/withdrawl/c00bf137bebb51e18a49b9666c5ac335)

### 返回值
    {"code":1}

## 获取下一次领取阳光倒计时

### 请求地址
    /freegift/award_time

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/freegift/award_time/c00bf137bebb51e18a49b9666c5ac335](http://test-aiapi.memeyule.com/freegift/award_time/c00bf137bebb51e18a49b9666c5ac335)

### 返回值
    {
    "data": {
        "count_down": 10000,
        "award_time": 0,
        "total": 6,
        "complete": 2,
        "coin": 10,
        "count_down_time":44289
    },
    "exec": 3400,
    "code": 1
	}

### 备注
<font color=red size=2>
1 进入直播间先调用/mission/daily_mission_list接口 刷新日常任务缓存
2 count_down 代表冷却时间，提供客户端倒计时。award_time 代表下一次领奖时间，0代表可以领奖
</font>


## 领取阳光

### 请求地址
    /freegift/award

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
coin|T|领取阳光数

### 请求示例
[http://test-aiapi.memeyule.com/freegift/award/c00bf137bebb51e18a49b9666c5ac335/1203123?coin=10](http://test-aiapi.memeyule.com/freegift/award/c00bf137bebb51e18a49b9666c5ac335/1203123?coin=10)

### 返回值
    {
    "data": {
        "count_down": 60000,
        "award_time": 1487138697771,
        "total": 6,
        "complete": 0,
        "coin": 20
    },
    "exec": 19,
    "code": 1
	}


## 签到

### 请求地址
    /sign/check

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/sign/check/c00bf137bebb51e18a49b9666c5ac335](http://test-aiapi.memeyule.com/sign/check/c00bf137bebb51e18a49b9666c5ac335)

### 返回值
    {
        "data":{
            "check_in_count":0,
            "to_check_in":true,
            "check_in_award_arr":[
                10,
                20,
                25,
                50,
                55,
                60,
                120
            ]
        },
        "exec":4,
        "code":1
    }


## 签到领奖

### 请求地址
    /sign/award

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/sign/award/c00bf137bebb51e18a49b9666c5ac335](http://test-aiapi.memeyule.com/sign/award/c00bf137bebb51e18a49b9666c5ac335)

### 返回值
	{"exec":37,"code":1}


## 签到删除测试接口

### 请求地址
    /sign/test_remove

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
stime|开始时间|yyyy-MM-dd 删除从stime开始的签到记录
etime|结束时间|yyyy-MM-dd 删除以etime结尾的签到记录，etime不能超过当前日期

### 请求示例
[http://test-aiapi.memeyule.com/sign/test_remove?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02](http://test-aiapi.memeyule.com/sign/test_remove?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02)

### 返回值
	{"exec":37,"code":1}

## 签到插入测试接口

### 请求地址
    /sign/testCheckIn

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
stime|开始时间|yyyy-MM-dd 插入以stime为起始时间的签到记录
etime|结束时间|yyyy-MM-dd 插入以etime为结尾的签到记录，etime不能超过当前日期

### 请求示例
[http://test-aiapi.memeyule.com/sign/testCheckIn?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02](http://test-aiapi.memeyule.com/sign/testCheckIn?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02)

### 返回值
	{"exec":37,"code":1}

## 签到列表测试接口

### 请求地址
    /sign/testSignList

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
stime|开始时间|yyyy-MM-dd 删除从stime开始的签到记录
etime|结束时间|yyyy-MM-dd 删除以etime结尾的签到记录，etime不能超过当前日期

### 请求示例
[http://test-aiapi.memeyule.com/sign//sign/testSignList?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02](http://test-aiapi.memeyule.com/sign//sign/testSignList?access_token=c00bf137bebb51e18a49b9666c5ac335&stime=2017-01-01&etime=2017-03-02)

### 返回值
	{"exec":37,"code":1}


## 请求启动页地址

### 请求地址
    /appload/load_page

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
type|T|类型

### 请求示例
[http://test-aiapi.memeyule.com/appload/load_page?type=6](http://test-aiapi.memeyule.com/appload/load_page?type=6)

### 返回值
    {
        "data": [
            {
                "_id": 102,
                "title": "默认登录页",
                "goto_type": 0,
                "goto_val": "",
                "pic_url": "http://test-aiimg.sumeme.com/34/2/1487647894946.png",
                "stime": 1487658057000,
                "etime": 1519366859000
            }
        ],
        "exec": 4,
        "code": 1
    }
## 游戏直播间游戏日志

### 接口地址
    /game/logs

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
room_id|T|房间id

### 请求示例
[http://test-aiapi.memeyule.com/game/logs?access_token=c80b45e56d1abd9d810dae65abb4c825&room_id=1201393](http://test-aiapi.memeyule.com/game/logs?access_token=c80b45e56d1abd9d810dae65abb4c825&room_id=1201393)

### 返回值
    {
        "data": [
            2,
            2,
            0
        ],
        "exec": 20,
        "code": 1
    }

### 备注
<font color=red>data节点返回一个数组，每个元素是一个map，其中key 0，1，2对应的是牛牛游戏中的 第一个牛，第二个牛，第三个牛。value = 0 的代表赢</font>


## 玩游戏后回调接口

### 请求地址
    /publicgame/callback

### 请求方式
    POST

### 加密方式
    key:9fbbb75ccaf64e1a9a23d5d724a490fc
    MD5(timestamp + nonce_str + key) 转成1进制不区分大小写

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
timestamp|T|时间戳
nonce_str|T|随机数，客户端生成(可以参考腾讯的：生成随机码(nonce_str) 随机生成10000以内的整形数字，编码GBK,并且进行MD5，转成16进制。例如:d4a973e303ec37692cc8923e3148eef7)
data|T|集合
room_id|T|房间id
win|T|游戏赢得玩家集合
lose|T|游戏输的玩家集合

### 请求示例
[test-aiapi.memeyule.com/publicgame/callback](test-aiapi.memeyule.com/publicgame/callback)
```
        {
        	"timestamp":1487044084943,
        	"sign":"c76076ab5aa10aa879565c62a2c3923d",
        	"nonce_str":"037a595e6f4f0576a9efe43154d71c18",
        	"data":
                        {
                            room_id:1201393,
                            win:[1,2,333,4],
                            lose:[5,21,6,7]
                        }
        }
```

### 返回值:
    code = 1 成功
    code = 0 异常
    当code=0时 客户端捕获error_msg节点

### 备注
    data节点的长度不能超过500，否则返回异常

## 管理员关闭直播间

### 接口地址
    /room/off

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
token|T|session
room_id|T|房间名
reason|F|关闭原因
close_time|F|关闭时间，此值是空则默认600,时间单位：秒

### 请求示例
[http://test-aiadmin.memeyule.com/room/off.json](http://test-aiadmin.memeyule.com/room/off.json)

### 返回值
    code:1 成功
    code非1时 解析 msg节点查看错误原因


## 监控获取主播封面和拉流地址

### 接口地址
    /monitor/be_monitored

### 请求参数
    无

### 请求示例
[http://test-aiapi.memeyule.com/monitor/be_monitored](http://test-aiapi.memeyule.com/monitor/be_monitored)
pu
### 返回值
    {
        "data": [
            1206231,
            1206322
        ],
        "exec": 2,
        "code": 1
    }



## 等级直达

### 接口地址
    /freegift/level_up_you_want

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
level|T|等级

### 请求示例
[http://test-aiapi.memeyule.com/freegift/level_up_you_want?access_token=c80b45e56d1abd9d810dae65abb4c825&level=10
](http://test-aiapi.memeyule.com/freegift/level_up_you_want?access_token=c80b45e56d1abd9d810dae65abb4c825&level=10
)

### 返回值
    无

## 增加经验

### 接口地址
    /freegift/add_experience_you_want

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
experience|T|经验

### 请求示例
[http://test-aiapi.memeyule.com/freegift/add_experience_you_want?access_token=c80b45e56d1abd9d810dae65abb4c825&experience=10000](http://test-aiapi.memeyule.com/freegift/add_experience_you_want?access_token=c80b45e56d1abd9d810dae65abb4c825&experience=10000)

### 返回值
    无

## 查询房间信息

### 接口地址
    public/room_by_ids

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
ids|T|下划线"_"拼接的主播id，格式必须正确

### 请求示例
[http://test-aiapi.memeyule.com/public/room_by_ids?ids=1201071_1201543](http://test-aiapi.memeyule.com/public/room_by_ids?ids=1201071_1201543)

### 返回值
    {
        "data": [
            {
                "_id": 1201071,
                "xy_star_id": 1201071,
                "live": false,
                "visiter_count": 0,
                "nick_name": "-",
                "app_pic_url": "http://img.sumeme.com/47/7/1201071_1_200150_1404109323268.jpg?v=314_236_1404109323287",
                "live_id": "",
                "live_type": 2,
                "live_cate": 1,
                "finance": {
                    "bean_count_total": 0
                },
                "L": 0,
                "followers": 0
            },
            {
                "_id": 1201543,
                "xy_star_id": 1201543,
                "live": false,
                "visiter_count": 0,
                "nick_name": "-",
                "live_type": 2,
                "live_id": "",
                "position": null,
                "live_cate": "普通",
                "app_pic_url": "http://test-aiimg.sumeme.com/7/7/1201543_11.jpg?v=1487312970856",
                "game_id": 1,
                "finance": {
                    "bean_count_total": 0
                },
                "L": 0,
                "followers": 0
            }
        ],
        "exec": 46,
        "code": 1
    }

## 获取目前开播的流信息

### 接口地址
    /monitor/live_status

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
room_id|F|房间id 不传则查询所有正在开播超过1分钟的流信息

### 请求示例
[http://test-aiapi.memeyule.com/monitor/live_status?room_id=1201071](http://test-aiapi.memeyule.com/monitor/live_status?room_id=1201071)

### 返回值
    {
        "normal_stream": [
            {
                "bps": 899160,
                "fps": {
                    "audio": 47,
                    "data": 0,
                    "video": 23
                },
                "room_id": "1201739",
                "startAt": 1488774658,
                "clientIP": "110.200.79.221:49497"
            }
        ],
        "bad_stream": [ ],
        "exec": 3073
    }


## 获取主播每次直播时长

### 接口地址
    /monitor/live_history

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
room_id|T|房间id
start|F|默认0 直播开始时间
end|F|默认0 直播结束时间

### 请求示例
[http://test-aiapi.memeyule.com/monitor/live_history?room_id=1201739](http://test-aiapi.memeyule.com/monitor/live_history?room_id=1201739)


### 返回值
    {
        "data":[
            {
                "start":1488774659,
                "end":1488791220
            },
            {
                "start":1488687258,
                "end":1488704300
            },
            {
                "start":1488620306,
                "end":1488631385
            },
            {
                "start":1488600584,
                "end":1488607827
            },
            {
                "start":1488528762,
                "end":1488532866
            },
            {
                "start":1488515156,
                "end":1488527559
            },
            {
                "start":1488514330,
                "end":1488515113
            },
            {
                "start":1488449685,
                "end":1488465432
            },
            {
                "start":1488449358,
                "end":1488449664
            }
        ],
        "exec":76
    }

### 备注
    <font color=red size=2>
    1 start和end如果有值，是一个单位为秒的时间戳，代表搜索的时间范围
    </font>

## 七牛流开播/关播列表

### 接口地址
    /monitor/live_list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
live|F|是否开播 默认true
limit|F|返回结果集，默认0
prefix|F|流名的前缀，默认空
marker|F|

### 请求示例
[http://test-aiapi.memeyule.com/monitor/live_list](http://test-aiapi.memeyule.com/monitor/live_list)


### 返回值
    {
        "data":[
            "1201478",
            "1207024"
        ],
        "exec":5254
    }

### 备注
    <font color=red size=2>
    1 prefix 不要传值，该值挂钩生成推流信息时的前缀，目前爱玩直播没有前缀所以不用传，传了反而查询失败
    2 live是开播状态，false是查询所有流，true则是查询目前正在开播的流
    3 加上 marker 的话，下次就会从limit 之后列举
    </font>

## 获取主播开播历史记录

### 接口地址
    /user/live_log

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/user/live_log?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/user/live_log?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
        "count":14,
        "data":[
            {
                "_id":{
                    "timestamp":1488278666,
                    "date":1488278666000,
                    "time":1488278666000,
                    "new":false,
                    "timeSecond":1488278666,
                    "inc":145516151,
                    "machine":1326236009
                },
                "timestamp":1488278666512,
                "session":{
                    "spend":"45000",
                    "room_id":"1206272",
                    "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                    "_id":"1206272",
                    "nick_name":"萌新590429",
                    "platform":"1",
                    "priv":"2"
                },
                "data":"1206272_20170228184426",
                "type":"live_on",
                "ext":"eyJhcHBWZXJzaW9uIjoiMC4wLjEuMjAxNzAyMjMwMDEiLCJjaGFubmVsIjoiQ2hhbm5lbF8wMDAxIiwibW9kZWwiOiJPUFBPIFI5cy9PUFBPIiwib3MiOiJBUEkgMjMiLCJwbGF0Zm9ybSI6IkFuZHJvaWQifQ==",
                "live_type":2,
                "room":1206272,
                "etime":1488278797187,
                "live_total":{
                    "second":130,
                    "earned":0
                }
            },
            {
                "_id":{
                    "timestamp":1488277558,
                    "date":1488277558000,
                    "time":1488277558000,
                    "new":false,
                    "timeSecond":1488277558,
                    "inc":1869879020,
                    "machine":1326195914
                },
                "timestamp":1488277558635,
                "session":{
                    "platform":"1",
                    "priv":"2",
                    "room_id":"1206272",
                    "nick_name":"萌新590429",
                    "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                    "spend":"45000",
                    "_id":"1206272"
                },
                "data":"1206272_20170228182558",
                "type":"live_on",
                "ext":"eyJhcHBWZXJzaW9uIjoiMC4wLjEuMjAxNzAyMjMwMDEiLCJjaGFubmVsIjoiQ2hhbm5lbF8wMDAxIiwibW9kZWwiOiJPUFBPIFI5cy9PUFBPIiwib3MiOiJBUEkgMjMiLCJwbGF0Zm9ybSI6IkFuZHJvaWQifQ==",
                "live_type":2,
                "room":1206272,
                "etime":1488277565603,
                "status":"LiveStatusCheck",
                "live_total":{
                    "second":6,
                    "earned":0
                }
            }
        ],
        "exec":15,
        "code":1,
        "all_page":3
    }


## 获取主播直播总时长

### 接口地址
    /user/live_total

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/user/live_total?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/user/live_total?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
        "data":{
            "_id":null,
            "earned":0,
            "second":400,
            "day":0
        },
        "exec":56,
        "code":1
    }

## 获取主播每次直播时长

### 接口地址
    /user/live_stat_log

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token

### 请求示例
[http://test-aiapi.memeyule.com/user/live_stat_log?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/user/live_stat_log?access_token=2f8ba046c67e28415276665d60aa8135)


### 返回值
    {
        "count":6,
        "data":[
            {
                "_id":"20170228_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "second":400,
                "pc_second":0,
                "app_second":400,
                "timestamp":1488211200000,
                "followers":0,
                "share_count":0,
                "avg_room_count":1,
                "meme":0
            },
            {
                "_id":"20170302_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "followers":0,
                "share_count":0,
                "avg_room_count":0,
                "meme":0
            },
            {
                "_id":"20170301_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "followers":0,
                "share_count":0,
                "avg_room_count":0,
                "meme":0
            },
            {
                "_id":"20170303_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "followers":0,
                "share_count":0,
                "avg_room_count":0,
                "meme":0
            },
            {
                "_id":"20170304_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "followers":0,
                "share_count":0,
                "avg_room_count":0,
                "meme":0
            },
            {
                "_id":"20170305_1206272",
                "user_id":1206272,
                "earned":0,
                "app_earned":0,
                "pc_earned":0,
                "followers":0,
                "share_count":0,
                "avg_room_count":0,
                "meme":0
            }
        ],
        "exec":133,
        "code":1,
        "all_page":1
    }

## 直播间封面编辑

### 接口地址
    /room/edit_app_pic_url

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|用户token
id1|T|房间id
app_pic_url|T|图片路径

### 请求示例
[http://test-aiapi.memeyule.com/room/edit_app_pic_url/1201997/2f8ba046c67e28415276665d60aa8135?app_pic_url=http:aiimg.sumeme.123123.jpg](http://test-aiapi.memeyule.com/room/edit_app_pic_url/1201997/2f8ba046c67e28415276665d60aa8135?app_pic_url=http:aiimg.sumeme.123123.jpg)

### 返回值
    {
        "code":1
    }

## 用户送礼日排行榜

### 接口地址
    /rank/user_day

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
    [http://test-aiapi.memeyule.com/rank/user_day/100](http://test-aiapi.memeyule.com/rank/user_day/100)

### 返回值
    {
        "data":[
            {
                "_id":1206221,
                "mm_no":1206221,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新710767",
                "finance":{
                    "bean_count_total":4016
                },
                "star":{
                    "room_id":1206221
                },
                "rank":1,
                "num":40
            }
        ],
        "exec":15,
        "code":1
    }

## 用户送礼总榜

### 接口地址
    /rank/user_total

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/user_total/100](http://test-aiapi.memeyule.com/rank/user_total/100)

### 返回值
    {
        "data":[
            {
                "_id":1205861,
                "mm_no":1205861,
                "pic":"http://test-aiimg.sumeme.com/37/5/1205861_0.jpg?v=1488539951006",
                "nick_name":"萌新978748",
                "finance":{
                    "bean_count_total":316624,
                    "coin_spend_total":9922889
                },
                "star":{
                    "room_id":1205861
                },
                "rank":1,
                "num":1242240
            },
            {
                "_id":1205901,
                "mm_no":1205901,
                "pic":"http://test-aiimg.sumeme.com/13/5/1205901_0.jpg?v=1488547086040",
                "nick_name":"萌新023083",
                "finance":{
                    "bean_count_total":227640,
                    "coin_spend_total":45000
                },
                "star":{
                    "room_id":1205901
                },
                "rank":2,
                "num":768100,
                "week_spend":62860
            },
            {
                "_id":1206301,
                "mm_no":1206301,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新385795",
                "finance":{

                },
                "rank":3,
                "num":690850
            },
            {
                "_id":1205847,
                "mm_no":1205847,
                "pic":"http://test-aiimg.sumeme.com/23/7/1205847_0.jpg?v=1487581722680",
                "nick_name":"刘大胆",
                "finance":{
                    "bean_count_total":7048
                },
                "star":{
                    "room_id":1205847
                },
                "rank":4,
                "num":315400
            }
        ],
        "exec":39,
        "code":1
    }

## 主播收礼日排行榜

### 接口地址
    /rank/star_day

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/star_day/50](http://test-aiapi.memeyule.com/rank/star_day/50)

### 返回值
    {
        "data":[
            {
                "_id":1206221,
                "mm_no":1206221,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新710767",
                "finance":{
                    "bean_count_total":4016
                },
                "star":{
                    "room_id":1206221
                },
                "rank":1,
                "num":16,
                "live":false,
                "live_type":2,
                "v_type":7
            }
        ],
        "exec":10,
        "code":1
    }

## 主播收礼总榜

### 请求地址
    /rank/star_total

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/star_total/50](http://test-aiapi.memeyule.com/rank/star_total/50)

### 返回值
    {
        "data":[
            {
                "_id":1206234,
                "mm_no":1206234,
                "pic":"http://test-aiimg.sumeme.com/26/2/1206234_0.jpg?v=1488248197276",
                "nick_name":"萌新962698",
                "finance":{
                    "coin_spend_total":5954000,
                    "bean_count_total":333204
                },
                "star":{
                    "room_id":1206234
                },
                "rank":1,
                "num":333204,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1205861,
                "mm_no":1205861,
                "pic":"http://test-aiimg.sumeme.com/37/5/1205861_0.jpg?v=1488539951006",
                "nick_name":"萌新978748",
                "finance":{
                    "bean_count_total":316624,
                    "coin_spend_total":9922889
                },
                "star":{
                    "room_id":1205861
                },
                "rank":2,
                "num":316624,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1205901,
                "mm_no":1205901,
                "pic":"http://test-aiimg.sumeme.com/13/5/1205901_0.jpg?v=1488547086040",
                "nick_name":"萌新023083",
                "finance":{
                    "bean_count_total":227640,
                    "coin_spend_total":45000
                },
                "star":{
                    "room_id":1205901
                },
                "rank":3,
                "num":227640,
                "live":false,
                "live_type":2,
                "v_type":7,
                "week_spend":62860
            },
            {
                "_id":1206261,
                "mm_no":1206261,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新",
                "finance":{
                    "coin_spend_total":21034000,
                    "bean_count_total":108080
                },
                "star":{
                    "room_id":1206261
                },
                "rank":4,
                "num":108080,
                "live":false,
                "live_type":2,
                "v_type":7
            }
        ],
        "exec":17,
        "code":1
    }

## 游戏赢币日榜单

### 接口地址
    /rank/game_day

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/game_day/50](http://test-aiapi.memeyule.com/rank/game_day/50)

### 返回值
    {
        "data":[
            {
                "_id":1206221,
                "mm_no":1206221,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新710767",
                "finance":{
                    "bean_count_total":4016
                },
                "star":{
                    "room_id":1206221
                },
                "rank":1,
                "num":2550,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1205846,
                "mm_no":1205846,
                "pic":"http://test-aiimg.sumeme.com/22/6/1205846_0.jpg?v=1487574741255",
                "nick_name":"苏帕塞亚金",
                "finance":{
                    "bean_count_total":104256,
                    "coin_spend_total":45840
                },
                "star":{
                    "room_id":1205846
                },
                "rank":2,
                "num":60,
                "live":false,
                "live_type":2,
                "v_type":7
            }
        ],
        "exec":13,
        "code":1
    }

## 游戏赢币总榜单

### 接口地址
    /rank/game_total

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
id1|F|默认10 返回结果集最大长度

### 请求示例
[http://test-aiapi.memeyule.com/rank/game_total/50](http://test-aiapi.memeyule.com/rank/game_total/50)

### 返回值
    {
        "data":[
            {
                "_id":1205901,
                "mm_no":1205901,
                "pic":"http://test-aiimg.sumeme.com/13/5/1205901_0.jpg?v=1488547086040",
                "nick_name":"萌新023083",
                "finance":{
                    "bean_count_total":227640,
                    "coin_spend_total":45000
                },
                "star":{
                    "room_id":1205901
                },
                "rank":1,
                "num":2769930,
                "live":false,
                "live_type":2,
                "v_type":7,
                "week_spend":62860
            },
            {
                "_id":1206234,
                "mm_no":1206234,
                "pic":"http://test-aiimg.sumeme.com/26/2/1206234_0.jpg?v=1488248197276",
                "nick_name":"萌新962698",
                "finance":{
                    "coin_spend_total":5954000,
                    "bean_count_total":333204
                },
                "star":{
                    "room_id":1206234
                },
                "rank":2,
                "num":2504760,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1206272,
                "mm_no":1206272,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新590429",
                "finance":{
                    "coin_spend_total":45000
                },
                "star":{
                    "room_id":1206272
                },
                "rank":3,
                "num":1380930,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1205846,
                "mm_no":1205846,
                "pic":"http://test-aiimg.sumeme.com/22/6/1205846_0.jpg?v=1487574741255",
                "nick_name":"苏帕塞亚金",
                "finance":{
                    "bean_count_total":104256,
                    "coin_spend_total":45840
                },
                "star":{
                    "room_id":1205846
                },
                "rank":4,
                "num":700680,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1205861,
                "mm_no":1205861,
                "pic":"http://test-aiimg.sumeme.com/37/5/1205861_0.jpg?v=1488539951006",
                "nick_name":"萌新978748",
                "finance":{
                    "bean_count_total":316624,
                    "coin_spend_total":9922889
                },
                "star":{
                    "room_id":1205861
                },
                "rank":5,
                "num":542760,
                "live":false,
                "live_type":2,
                "v_type":7
            },
            {
                "_id":1206261,
                "mm_no":1206261,
                "pic":"https://aiimg.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"萌新",
                "finance":{
                    "coin_spend_total":21034000,
                    "bean_count_total":108080
                },
                "star":{
                    "room_id":1206261
                },
                "rank":6,
                "num":312510,
                "live":false,
                "live_type":2,
                "v_type":7
            }
        ],
        "exec":25,
        "code":1
    }

## 商品列表

### 接口地址
    /shop/product_list

### 请求参数
    无

### 请求示例
[http://test-aiapi.memeyule.com/shop/product_list](http://test-aiapi.memeyule.com/shop/product_list)

### 返回值
    {
        "count":1,
        "data":[
            {
                "_id":104,
                "img_url":"http://test-aiimg.sumeme.com/39/7/1490001808423.jpg",
                "timestamp":1490001811614,
                "delivery_type":1,
                "desc":"啊实打实的",
                "order":2,
                "status":true,
                "in_stock":10,
                "name":"手机充值卡30元",
                "money":30,
                "last_modify":1490001811614,
                "price":20
            }
        ],
        "exec":3,
        "code":1,
        "all_page":1
    }

## 快讯

### 接口地址
    /shop/news_list

### 请求参数
    无

### 请求示例
[http://test-aiapi.memeyule.com/shop/news_list](http://test-aiapi.memeyule.com/shop/news_list)

### 返回值
    {
        "count":1,
        "data":[
            {
                "_id":"1209631_1490003092836",
                "last_modify":1490003094609,
                "user_id":1209631,
                "status":4,
                "price":20,
                "item_name":"手机充值卡30元",
                "item_count":1,
                "money":30,
                "product_id":104,
                "timestamp":1490003092836,
                "mobile":"15618040084"
            }
        ],
        "exec":9,
        "code":1,
        "all_page":1
    }

## 下单购买

### 接口地址
    /order/buy

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
mobile|T|手机号
product_id|T|商品id
count|F|购买数量,不传默认1

### 请求示例
[test-aiapi.memeyule.com/order/buy?mobile=15618040084&product_id=102&count=1&access_token=a233a56742fc7407e778da9e944b4864](test-aiapi.memeyule.com/order/buy?mobile=15618040084&product_id=102&count=1&access_token=a233a56742fc7407e778da9e944b4864)

### 返回值
    {
        "exec":192,
        "code":1
    }

## 畅天游订单查询接口

### 接口地址
    /ctu/search

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
order_id|T|订单id

### 请求示例
[test-aiapi.memeyule.com/ctu/search?order_id=1329163_1329163_1489657237912](test-aiapi.memeyule.com/ctu/search?order_id=1329163_1329163_1489657237912)

### 返回值
    {
        "data":"订单支付成功",
        "exec":101,
        "code":1
    }

## 订单日志

### 接口地址
    /order/logs

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
page|F|分页
size|F|一页大小

### 请求示例
[test-aiapi.memeyule.com/order/logs?access_token=a233a56742fc7407e778da9e944b4864](test-aiapi.memeyule.com/order/logs?access_token=a233a56742fc7407e778da9e944b4864)

### 返回值
    {
        "count":3,
        "data":[
            {
                "_id":"1208971_1208971_1489657990200",
                "timestamp":1489657990200,
                "last_modify":1489657990200,
                "user_id":1208971,
                "status":1,
                "cost":150,
                "item_name":"30块手机卡",
                "item_count":1,
                "mobile":"15618040084",
                "money":1000,
                "product_id":102,
                "cny":1000
            },
            {
                "_id":"1208971_1208971_1489657971593",
                "timestamp":1489657971593,
                "last_modify":1489657971593,
                "user_id":1208971,
                "status":1,
                "cost":150,
                "item_name":"30块手机卡",
                "item_count":1,
                "mobile":"15618040084",
                "money":1000,
                "product_id":102,
                "cny":1000
            },
            {
                "_id":"1208971_1208971_1489657632551",
                "timestamp":1489657632551,
                "last_modify":1489657632551,
                "user_id":1208971,
                "status":1,
                "cost":150,
                "item_name":"30块手机卡",
                "item_count":1,
                "mobile":"15618040084",
                "money":1000
            }
        ],
        "exec":4,
        "code":1,
        "all_page":1
    }

## 愚人节用户榜

### 请求地址
    /aprilfool/user_rank

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token

### 请求示例
[http://test-aiapi.memeyule.com/aprilfool/user_rank](http://test-aiapi.memeyule.com/aprilfool/user_rank)

### 返回值
	{
        "data":[
            {
                "_id":1268633,
                "finance":{
                    "bean_count_total":546113,
                    "coin_spend_total":15390521
                },
                "nick_name":"monkey1",
                "pic":"http://test.img.dongting.com/25/1/1268633.jpg?t=1365774949963",
                "rank":1,
                "count":17
            },
            {
                "_id":1329163,
                "mm_no":1329163,
                "pic":"http://img.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"monkey",
                "finance":{
                    "coin_spend_total":0
                },
                "rank":2,
                "count":10
            }
        ],
        "exec":5,
        "code":1
    }

## 愚人节主播榜

### 请求地址
    /aprilfool/star_rank

### 请求参数
无

### 请求示例
[http://test-aiapi.memeyule.com/aprilfool/star_rank](http://test-aiapi.memeyule.com/aprilfool/star_rank)

### 返回值
    {
        "data":[
            {
                "_id":1315027,
                "mm_no":1315027,
                "pic":"http://img.sumeme.com/22/6/1403510731734.jpg",
                "nick_name":"zhubo8827",
                "finance":{
                    "bean_count_total":582365079
                },
                "rank":1,
                "count":108
            }
        ],
        "exec":10,
        "code":1
    }

------------------------------

## 新人红包奖励

### 请求地址
    /redpacket/newcomer_surprise

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/newcomer_surprise?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/redpacket/newcomer_surprise?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
        "data":{
            "award_flag":true,
            "cash_count":23
        },
        "exec":7,
        "code":1
    }

### 备注
    award_flag = true 弹出新人红包领取动画

------------------------------

## 新人领取红包

### 请求地址
    /redpacket/newcomer_award

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/newcomer_award?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/redpacket/newcomer_award?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
            "data":[
                {
                    "coin_count":0,
                    "cash_count":100
                }
            ],
            "exec":10,
            "code":1
        }

### 备注
    coin_count = 领取的阳光数
    cash_count = 领取的金额 单位：分

------------------------------

## 红包列表

### 请求地址
    /redpacket/list

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/list?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/redpacket/list?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
        "data":{
            "lock":[
                {
                    "_id":3,//红包id
                    "cool_down":80, // 冷却时间
                    "award_limit":58,// 每日领取次数
                    "cost_coin_condition":300,// 解锁消费阳光
                    "check_in_condition":0,// 连续签到次数
                    "game_number_condition":0,//每日游戏局数
                    "send_gift_number_condition":0//每日送礼次数
                },
                {
                    "_id":4,
                    "count_down":260,
                    "award_limit":42,
                    "cost_coin_condition":500,
                    "check_in_condition":0,
                    "game_number_condition":0,
                    "send_gift_number_condition":0
                },
                {
                    "_id":5,
                    "count_down":20,
                    "award_limit":89,
                    "cost_coin_condition":0,
                    "check_in_condition":0,
                    "game_number_condition":0,
                    "send_gift_number_condition":0
                },
                {
                    "_id":7,
                    "cool_down":20,
                    "award_limit":89,
                    "cost_coin_condition":0,
                    "check_in_condition":0,
                    "game_number_condition":0,
                    "send_gift_number_condition":0
                }
            ],
            "unlock":[
                {
                    "_id":1
                    "reward_coin_count":2,// 红包现金  单位分
                    "cool_down":10,// 冷却时间
                    "reward_cash_count":1,// 红包阳光
                    "remain_award_time":9645,//剩余时间 毫秒
                    "next_award_time":1492083122555 // 下一次领奖时间
                },
                {
                    "reward_coin_count":1,
                    "cool_down":10,
                    "reward_cash_count":1,
                    "remain_award_time":8053,
                    "next_award_time":1492083121212
                },
                {
                    "reward_coin_count":2,
                    "cool_down":10,
                    "reward_cash_count":1,
                    "remain_award_time":8586,
                    "next_award_time":1492083121913
                },
                {
                    "reward_coin_count":1,
                    "cool_down":10,
                    "reward_cash_count":1,
                    "remain_award_time":8927,
                    "next_award_time":1492083122405
                }
            ]
        },
        "exec":4951,
        "code":1
    }

### 备注
    lock = 锁定的红包
    unlock = 解锁的红包


------------------------------

## 红包获取

### 请求地址
    /redpacket/acquire

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
_id|T|红包id

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/acquire?access_token=2f8ba046c67e28415276665d60aa8135&_id=123123](http://test-aiapi.memeyule.com/redpacket/acquire?access_token=2f8ba046c67e28415276665d60aa8135&_id=123123)

### 返回值
    {
        "data":{
            "_id":"1",
            "reward_coin_count":1,
            "cool_down":10,
            "reward_cash_count":3,
            "next_award_time":1492085584013
        },
        "exec":13,
        "code":1
    }

------------------------------

## 红包解锁

### 请求地址
    /redpacket/unlock

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
_id|T|红包id
coin_count_cost|T|解锁需要的阳光

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/unlock?access_token=2f8ba046c67e28415276665d60aa8135&coin_count_cost=1000&_id=123](http://test-aiapi.memeyule.com/redpacket/unlock?access_token=2f8ba046c67e28415276665d60aa8135&coin_count_cost=1000&_id=123)

### 返回值
    {
            "exec":10,
            "code":1
     }

------------------------------

## 红包提现

### 请求地址
    /redpacket/apply_withdraw

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
amount|T|提现金额

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/apply_withdraw?access_token=2f8ba046c67e28415276665d60aa8135&amount=123](http://test-aiapi.memeyule.com/redpacket/apply_withdraw?access_token=2f8ba046c67e28415276665d60aa8135&amount=123)

### 返回值
    {
            "exec":10,
            "code":1
     }

------------------------------

## 红包兑换

### 请求地址
    /redpacket/exchange

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token
exchange_coin|T|兑换阳光数量

### 请求示例
[http://test-aiapi.memeyule.com/redpacket/exchange?access_token=2f8ba046c67e28415276665d60aa8135&exchange_coin=123](http://test-aiapi.memeyule.com/redpacket/exchange?access_token=2f8ba046c67e28415276665d60aa8135&exchange_coin=123)

### 返回值
    {
            "exec":10,
            "code":1
     }

## 是否绑定openId

### 请求地址
    /user/is_bind_openId

### 请求参数
参数名|是否必传（T/F）|备注
------|----|----------|
access_token|T|token

### 请求示例
[http://test-aiapi.memeyule.com/user/is_bind_openId?access_token=2f8ba046c67e28415276665d60aa8135](http://test-aiapi.memeyule.com/user/is_bind_openId?access_token=2f8ba046c67e28415276665d60aa8135)

### 返回值
    {
            "exec":10,
            "code":1,
            "data":{
                "is_bind_openId":"true"
            }
     }

