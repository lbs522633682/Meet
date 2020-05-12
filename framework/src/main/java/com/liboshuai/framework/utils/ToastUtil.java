package com.liboshuai.framework.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by boshuai.li on 2020/4/17.
 */

public class ToastUtil {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showTextToastById(Context context, int resId) {
        String content = context.getResources().getString(resId);
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }

        toast.show();
    }

    @SuppressLint("ShowToast")
    public static void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @SuppressLint("ShowToast")
    public static void showTextLongToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @SuppressLint("ShowToast")
    public static void showTextToastCenter(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
