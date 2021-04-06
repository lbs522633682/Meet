package com.liboshuai.framework.db;

import org.litepal.crud.LitePalSupport;

/**
 * Author:boshuai.li
 * Time:2021/4/6   10:25
 * Description: Litepal 本地存储数据类
 */
public class NewFriend extends LitePalSupport {

    public static final int STATUS_NOT_CONFIRM = -1;
    public static final int STATUS_AGREE = 0;
    public static final int STATUS_REFUSED = 1;


    // 留言
    private String msg;
    private String userId;
    private long saveTime;
    // 状态：-1 待确认 0 同意 1 拒绝
    private int isAgree = STATUS_NOT_CONFIRM;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(int isAgree) {
        this.isAgree = isAgree;
    }

    @Override
    public String toString() {
        return "NewFriend{" +
                "msg='" + msg + '\'' +
                ", userId='" + userId + '\'' +
                ", saveTime='" + saveTime + '\'' +
                ", isAgree=" + isAgree +
                '}';
    }
}
