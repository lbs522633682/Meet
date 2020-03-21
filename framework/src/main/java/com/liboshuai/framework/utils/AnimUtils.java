package com.liboshuai.framework.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Author:boshuai.li
 * Time:2020/3/16   16:34
 * Description:This is 动画工具类
 */
public class AnimUtils {
    /**
     * 旋转动画
     * @param view
     * @return
     */
    public static ObjectAnimator rotation(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        objectAnimator.setDuration(2 * 1000);
        // 循环播放
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        // 最大次数
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        // 设置一个默认的加速器
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }
}
