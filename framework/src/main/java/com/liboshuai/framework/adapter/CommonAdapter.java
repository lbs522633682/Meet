package com.liboshuai.framework.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Author:boshuai.li
 * Time:2020/7/28   16:19
 * Description:万能的adapter
 */
public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    // 数据源 未知类型
    private List<T> mList;

    private OnBindDataListener onBindDataListener;
    private OnMoreBindDataListener onMoreBindDataListener;

    public CommonAdapter(List<T> mList, OnBindDataListener onBindDataListener) {
        this.mList = mList;
        this.onBindDataListener = onBindDataListener;
    }

    public CommonAdapter(List<T> mList, OnMoreBindDataListener onMoreBindDataListener) {
        this.mList = mList;
        this.onMoreBindDataListener = onMoreBindDataListener;
    }

    // 绑定单一类型的数据
    public interface OnBindDataListener<T> {
        void onBindViewHolder(T model, CommonViewHolder viewHolder, int type, int position);

        int getLayoutId(int type);
    }

    // 绑定多类型的数据
    public interface OnMoreBindDataListener<T> extends OnBindDataListener {
        int getItemType(int position);
    }


    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = onBindDataListener.getLayoutId(viewType);
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(parent, layoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBindDataListener.onBindViewHolder(mList.get(position), holder,
                getItemViewType(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        if (onMoreBindDataListener != null) {
            return onMoreBindDataListener.getItemType(position);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
