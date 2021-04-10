package plat.skytv.client.meet.model;

import com.liboshuai.framework.utils.JsonUtil;

/**
 * Author:boshuai.li
 * Time:2021/4/8
 * Description: 聊天记录的实体类
 */
public class ChatRecordModel {
    /**
     * 头像
     */
    private String url;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 最后一条消息
     */
    private String endMsg;
    /**
     * 时间
     */
    private String time;
    /**
     * 未读条数
     */
    private int unReadSize;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEndMsg() {
        return endMsg;
    }

    public void setEndMsg(String endMsg) {
        this.endMsg = endMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUnReadSize() {
        return unReadSize;
    }

    public void setUnReadSize(int unReadSize) {
        this.unReadSize = unReadSize;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
