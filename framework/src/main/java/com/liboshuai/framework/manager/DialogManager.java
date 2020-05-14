package com.liboshuai.framework.manager;

import android.content.Context;
import android.view.Gravity;

import com.liboshuai.framework.view.DialogView;

import plat.skytv.client.framework.R;

/**
 * Author:boshuai.li
 * Time:2020/5/12   16:16
 * Description: 提示框管理类
 */
public class DialogManager {

    private static DialogManager mDialogManager;

    private DialogManager() {
    }

    public static DialogManager getInstance() {

        if (mDialogManager == null) {
            synchronized (DialogManager.class) {
                if (mDialogManager == null) {
                    mDialogManager = new DialogManager();
                }
            }
        }

        return mDialogManager;
    }

    public DialogView initView(Context context, int layout) {
        return new DialogView(context, layout, R.style.Theme_Dialog, Gravity.CENTER);
    }

    /**
     *
     * @param context
     * @param layout
     * @param gravity Gravity.CENTER Gravity.BOTTOM Gravity.TOP
     * @return
     */
    public DialogView initView(Context context, int layout, int gravity) {
        return new DialogView(context, layout, R.style.Theme_Dialog, gravity);
    }

    public void show(DialogView dialogView) {
        if (dialogView != null) {
            if (!dialogView.isShowing()) {
                dialogView.show();
            }
        }
    }

    public void hide(DialogView dialogView) {
        if (dialogView != null) {
            if (dialogView.isShowing()) {
                dialogView.dismiss();
            }
        }
    }
}
