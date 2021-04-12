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

    // 发送文本消息
    public static final int FLAG_SEND_TEXT = 1001;

    // 发送图片消息
    public static final int FLAG_SEND_IMAGE = 1002;

    // 发送位置消息
    public static final int FLAG_SEND_LOCATION = 1003;
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

    public static void post(MessageEvent messageEvent) {
        EventBus.getDefault().post(messageEvent);
    }
}
