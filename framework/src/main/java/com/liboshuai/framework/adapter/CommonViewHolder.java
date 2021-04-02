package com.liboshuai.framework.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.helper.GlideHelper;

/**
 * Author:boshuai.li
 * Time:2020/7/28   16:20
 * Description:万能的viewHolder
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mContentView;

    /**
     * 获取 CommonViewHolder 实体
     *
     * @param parent
     * @param layoutId
     * @return
     */
    public static CommonViewHolder getViewHolder(ViewGroup parent, int layoutId) {
        return new CommonViewHolder(View.inflate(parent.getContext(), layoutId, null));
    }

    public CommonViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        mContentView = itemView;
    }

    /**
     * 提供 外部的访问view的方法
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (mViews.get(viewId) == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T) view;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     * @return
     */
    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片链接
     *
     * @param context
     * @param viewId
     * @param url
     * @return
     */
    public CommonViewHolder setImageUrl(Context context, int viewId, String url) {
        ImageView iv = getView(viewId);
        GlideHelper.loadUrl(context, url, iv);
        return this;
    }

    /**
     * 设置图片的资源id
     *
     * @param viewId
     * @param resId
     * @return
     */
    public CommonViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    /**
     * 设置view的显示隐藏
     * @param viewId
     * @param visibility One of {@link #View.VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     * @return
     */
    public CommonViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public CommonViewHolder setBackgroundColor(int viewId, int bgColor) {
        View view = getView(viewId);
        view.setBackgroundColor(bgColor);
        return this;
    }
}
