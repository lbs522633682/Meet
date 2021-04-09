package com.liboshuai.framework.event;

import com.liboshuai.framework.utils.JsonUtil;

/**
 * Author:boshuai.li
 * Time:2021/4/6   11:16
 * Description: 事件类
 */
public class MessageEvent {

    /**
     * 消息类型
     */
    private int type;

    private String text;
    private String userId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MessageEvent(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
