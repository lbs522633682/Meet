package com.liboshuai.framework.helper;


import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Author:boshuai.li
 * Time:2021/4/14
 * Description: WindowManager 工具类
 */
public class WindowHelper {

    private static WindowHelper mInstance;
    private WindowManager mWindowManager;
    private Context mContext;
    private WindowManager.LayoutParams layoutParams;

    private WindowHelper() {

    }

    public static WindowHelper getInstance() {
        if (mInstance == null) {
            synchronized (WindowHelper.class) {
                if (mInstance == null) {
                    mInstance = new WindowHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化Window
     * @param context
     */
    public void initWindow(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mContext = context;
        layoutParams = new WindowManager.LayoutParams();
        // 设置宽高
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        // 设置标志位
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        // 设置格式 系统选择支持半透明的格式
        layoutParams.format = PixelFormat.TRANSLUCENT;
        // 设置位置
        layoutParams.gravity = Gravity.CENTER;
        // 设置类型
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }


    /**
     * 获取view
     * @param layoutId
     */
    public View getView(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    /**
     * 显示窗口
     * @param view
     */
    public void showView(View view) {
        if (view != null) {
            if (view.getParent() == null) { // 需要判断 这个view 有没有父布局
                mWindowManager.addView(view, layoutParams);
            }
        }
    }

    /**
     * 隐藏窗口
     * @param view
     */
    public void hideView(View view) {
        if (view != null) {
            if (view.getParent() != null) { // 需要判断 这个view 有没有父布局
                mWindowManager.removeView(view);
            }
        }
    }
}
