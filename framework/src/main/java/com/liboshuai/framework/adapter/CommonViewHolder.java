package com.liboshuai.framework.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

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


}
