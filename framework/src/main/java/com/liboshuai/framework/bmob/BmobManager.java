package com.liboshuai.framework.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;

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
     * 初始化Bmob
     *
     * @param context
     */
    public void initBmob(Context context) {
        Bmob.initialize(context, BMOB_SDK_ID);
    }
}
