package com.liboshuai.framework.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.utils.AnimUtils;

import plat.skytv.client.framework.R;

/**
 * Author:boshuai.li
 * Time:2020/5/12   17:14
 * Description: 加载中的提示框
 */
public class LoadingView {

    private DialogView mLoadingView;
    private ImageView iv_loding;
    private TextView tv_loding_text;
    private ObjectAnimator mAnim;

    public LoadingView(Context context) {
        mLoadingView = DialogManager.getInstance().initView(context, R.layout.dialog_loading);

        iv_loding = mLoadingView.findViewById(R.id.iv_loding);
        tv_loding_text = mLoadingView.findViewById(R.id.tv_loding_text);

        mAnim = AnimUtils.rotation(iv_loding);
    }

    public void setLoadingText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_loding_text.setText(text);
        }
    }

    public void show() {
        mAnim.start();
        DialogManager.getInstance().show(mLoadingView);
    }

    public void show(String text) {
        mAnim.start();
        setLoadingText(text);
        DialogManager.getInstance().show(mLoadingView);
    }

    public void hide() {
        mAnim.pause();
        DialogManager.getInstance().hide(mLoadingView);
    }
}
