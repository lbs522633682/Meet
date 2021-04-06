package com.liboshuai.framework.bmob;

import com.liboshuai.framework.utils.JsonUtil;

/**
 * Author:boshuai.li
 * Time:2021/4/6   15:00
 * Description: 朋友
 */
public class Friend {
    // 我自己
    private IMUser user;

    // 朋友
    private IMUser friendUser;

    public IMUser getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(IMUser friendUser) {
        this.friendUser = friendUser;
    }

    public IMUser getUser() {
        return user;
    }

    public void setUser(IMUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }

}
