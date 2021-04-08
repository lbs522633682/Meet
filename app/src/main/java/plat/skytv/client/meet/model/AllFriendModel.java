package plat.skytv.client.meet.model;

import com.liboshuai.framework.utils.JsonUtil;

/**
 * Author:boshuai.li
 * Time:2021/4/8
 * Description:全部好友得数据模型
 */
public class AllFriendModel {
    private String url;
    private String nickName;
    private String desc;
    // true 男 ，false 女
    private boolean sex;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
