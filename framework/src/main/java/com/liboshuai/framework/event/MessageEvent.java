package com.liboshuai.framework.event;

import android.view.SurfaceView;

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

    // 位置消息
    private double la;
    private double lo;
    private String address;

    // 相机消息
    private SurfaceView surfaceView;

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
