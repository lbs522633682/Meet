package com.liboshuai.framework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.List;

import plat.skytv.client.framework.R;

/**
 * Author:boshuai.li
 * Time:2020/5/13   16:20
 * Description:This is CloudTagAdapter
 */
public class CloudTagAdapter extends TagsAdapter {

    private List<String> mList;
    private Context mContext;
    private LayoutInflater inflater;

    public CloudTagAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override

    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.layout_star_view_item, null);
        TextView tv_star_name = view.findViewById(R.id.tv_star_name);
        ImageView iv_star_icon = view.findViewById(R.id.iv_star_icon);

        tv_star_name.setText(mList.get(position));
        switch (position % 10) {
            case 0:
                iv_star_icon.setImageResource(R.drawable.img_star_icon_1);
                break;
            case 1:
                iv_star_icon.setImageResource(R.drawable.img_star_icon_2);
                break;
            case 2:
                iv_star_icon.setImageResource(R.drawable.img_star_icon_3);
                break;
            default:
                iv_star_icon.setImageResource(R.drawable.img_star_icon_4);
                break;
        }

        return view;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        // tag主题发生变化时的回调
    }
}
