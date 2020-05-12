package com.liboshuai.framework.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Author:boshuai.li
 * Time:2020/3/23   10:25
 * Description: Bmob的管理类
 */
public class BmobManager {

    private static final String BMOB_SDK_ID = "a3fc2edca9eccc01110110970f1b4091";

    private static BmobManager mBmobManager;

    private BmobManager() {

    }

    public static BmobManager getInstance() {
        if (mBmobManager == null) {
            synchronized (BmobManager.class) {
                if (mBmobManager == null) {
                    mBmobManager = new BmobManager();
                }
            }
        }
        return mBmobManager;
    }

    /**
     * 获取本地已经登录对象
     *
     * @return
     */
    public IMUser getUser() {
        return BmobUser.getCurrentUser(IMUser.class);
    }

    /**
     * 初始化Bmob
     *
     * @param context
     */
    public void initBmob(Context context) {
        Bmob.initialize(context, BMOB_SDK_ID);
    }

    /**
     * @param phone
     * @param listener
     */
    public void requestSMSCode(String phone, QueryListener<Integer> listener) {
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    /**
     * 一键注册或登录:
     *
     * @param phone
     * @param code     验证码
     * @param listener
     */
    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);

    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return BmobUser.isLogin();
    }
}
