package com.liboshuai.framework.db;

import com.liboshuai.framework.bmob.Friend;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Author:boshuai.li
 * Time:2021/4/6   10:25
 * Description: 本地数据库帮助类
 */
public class LitepalHelper {

    private static LitepalHelper mLitepal;

    private LitepalHelper() {

    }

    public static LitepalHelper getInstance() {
        if (mLitepal == null) {
            synchronized (LitepalHelper.class) {
                if (mLitepal == null) {
                    mLitepal = new LitepalHelper();
                }
            }
        }
        return mLitepal;
    }

    /**
     * 保存基类
     *
     * @param litePalSupport
     */
    private void baseSave(LitePalSupport litePalSupport) {
        litePalSupport.save();
    }

    /**
     * 保存新朋友
     *
     * @param msg
     * @param userId
     */
    public void saveNewFriend(String msg, String userId) {
        NewFriend newFriend = new NewFriend();
        newFriend.setUserId(userId);
        newFriend.setMsg(msg);
        newFriend.setSaveTime(System.currentTimeMillis());
        newFriend.setIsAgree(-1);
        baseSave(newFriend);
    }

    /**
     * 查询的基类
     *
     * @param cls
     * @return
     */
    private List<? extends LitePalSupport> baseQuery(Class cls) {
        return LitePal.findAll(cls);
    }

    /**
     * 查询新朋友
     *
     * @return
     */
    public List<NewFriend> queryNewFriend() {
        return (List<NewFriend>) baseQuery(NewFriend.class);
    }

    /**
     * 数据库更新 状态
     * @param userId
     * @param isAgree
     */
    public void updateNewFriend(String userId, int isAgree) {
        NewFriend newFriend = new NewFriend();
        newFriend.setIsAgree(isAgree);
        newFriend.updateAll("userId = ?", userId);
    }

}
