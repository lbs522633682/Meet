package com.liboshuai.framework.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * Author:boshuai.li
 * Time:2020/5/12   16:08
 * Description: 自定义提示对框框
 */
public class DialogView extends Dialog {

    /**
     *
     * @param context
     * @param layout 布局文件
     * @param themeResId 主题 一般透明
     * @param gravity 位置
     */
    public DialogView(Context context, int layout, int themeResId, int gravity) {
        super(context, themeResId);

        setContentView(layout);

        Window window = getWindow();

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        // 默认的提示框 距离左右是有距离的
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = gravity;

        window.setAttributes(layoutParams);
    }

}
