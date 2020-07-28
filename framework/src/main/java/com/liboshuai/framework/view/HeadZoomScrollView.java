package com.liboshuai.framework.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.liboshuai.framework.utils.LogUtils;

/**
 * FileName: HeadZoomScrollView
 * Founder: boshuai.li
 * Profile: 头部拉伸的View
 */
public class HeadZoomScrollView extends ScrollView {

    // 头部view
    private View mZoomView;
    private int mZoomViewWith;
    private int mZoomViewHeight;

    // 是否在滑动
    private boolean isScrolling = false;
    // 第一次按下的坐标
    private float firstPosition;
    // 滑动系数
    private float mScrollRate = 0.3f;
    // 回弹系数
    private float mReplyRate = 0.5f;


    public HeadZoomScrollView(Context context) {
        super(context);
    }

    public HeadZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View childView = getChildAt(0);
        if (childView != null) { // 说明Scrollview有子view
            ViewGroup vg = (ViewGroup) childView; // ScrollView的第一个View必定是一个ViewGroup
            if (vg.getChildAt(0) != null) {
                mZoomView = vg.getChildAt(0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 获取view的宽高
        if (mZoomViewWith <= 0 || mZoomViewHeight <= 0) {
            mZoomViewHeight = mZoomView.getMeasuredHeight();
            mZoomViewWith = mZoomView.getMeasuredWidth();
        }

        // 处理事件
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isScrolling) { // 不在 滑动
                    if (getScrollY() == 0) { // 第一次 没有 滑动， 记录初始位置
                        firstPosition = ev.getY();
                    } else {
                        break;
                    }
                }

                // 计算缩放值 = （当前位置 - 初始位置）* 缩放系数

                int distance = (int) ((ev.getY() - firstPosition) * mScrollRate);

                if (distance < 0) {
                    break;
                }
                isScrolling = true;

                setZoomView(distance);

                break;

            case MotionEvent.ACTION_UP:
                isScrolling = false;

                replyZoomView();
                break;
        }

        return true;
    }

    /**
     * 属性动画 回弹
     */
    private void replyZoomView() {
        int distance = mZoomView.getMeasuredWidth() - mZoomViewWith;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distance, 0)
                .setDuration((long) (distance * mReplyRate));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtils.i("onAnimationUpdate value = " + animation.getAnimatedValue());
                setZoomView((float) animation.getAnimatedValue());
            }
        });

        valueAnimator.start();
    }

    /**
     * 设置缩放的view
     *
     * @param distance
     */
    private void setZoomView(float distance) {
        LogUtils.i("setZoomView distance = " + distance);
        if (mZoomViewWith <= 0 || mZoomViewHeight <= 0) {
            return;
        }

        ViewGroup.LayoutParams lp = mZoomView.getLayoutParams();

        // 重新设置宽高
        lp.width = (int) (mZoomViewWith + distance);

        // 高 = 原来的高 * 变换系数 （= 现在的宽/原始的宽）

        // 变换系数需要使用float类型的参数
        lp.height = (int) (mZoomViewHeight * ((mZoomViewWith + distance) / mZoomViewWith));

        LogUtils.i("setZoomView lp.width = " + lp.width + ", lp.height = " + lp.height);

        // 设置间距 left = - (现在的宽 - 原始的宽)/2
        ((MarginLayoutParams) lp).setMargins(-(lp.width - mZoomViewWith) / 2, 0, 0, 0);

        mZoomView.setLayoutParams(lp);
    }
}
