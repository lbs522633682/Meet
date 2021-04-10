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

    private String userId;

    // 文本内容
    private String text;

    // 图片链接
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

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
