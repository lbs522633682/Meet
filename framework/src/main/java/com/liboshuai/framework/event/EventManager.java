package com.liboshuai.framework.event;

import org.greenrobot.eventbus.EventBus;

/**
 * Author:boshuai.li
 * Time:2021/4/6   11:16
 * Description: EventBus调度类
 */
public class EventManager {

    // 刷新好友列表
    public static final int FLAG_UPDATE_FRIEND_LIST = 1000;

    /**
     * Event 的步骤
     * 1. 注册
     * 2. 声明注册方法
     * 3. 发送事件
     */

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void post(int type) {
        EventBus.getDefault().post(new MessageEvent(type));
    }
}
