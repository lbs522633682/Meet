package com.liboshuai.framework.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

/**
 * Author:boshuai.li
 * Time:2020/3/17   11:42
 * Description: 沉浸式状态栏  基于anroid5.0开发适配
 */
public class SystemUI {
    public static void fixSystemUI(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 获取最顶层的view
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

            // activity.getSupportActionBar().hide();
        }

    }
}
