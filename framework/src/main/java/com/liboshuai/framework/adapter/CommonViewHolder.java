package com.liboshuai.framework.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:boshuai.li
 * Time:2020/7/28   16:20
 * Description:万能的viewHolder
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {
    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public static CommonViewHolder getViewHolder(ViewGroup parent, int layoutId) {
    }
}
