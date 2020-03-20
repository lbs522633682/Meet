package com.liboshuai.framework;

import android.content.Context;

import com.liboshuai.framework.utils.SpUtils;

/**
 * Author:boshuai.li
 * Time:2020/3/16   16:31
 * Description: Framework 入口
 */
public class Framework {
    private static Framework mFramework;

    private Framework() {
    }

    public static Framework getInstance() {

        if (mFramework == null) {
            synchronized (Framework.class) {
                if (mFramework == null) {
                    mFramework = new Framework();
                }
            }
        }
        return mFramework;
    }

    /**
     * 初始化框架 Model
     *
     * @param context
     */
    public void initFramework(Context context) {
        SpUtils.getInstance().initSp(context);
    }
}
