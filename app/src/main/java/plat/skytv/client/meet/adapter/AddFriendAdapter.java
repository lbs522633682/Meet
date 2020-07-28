package plat.skytv.client.meet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.helper.GlideHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.model.AddFriendModel;

/**
 * Author:boshuai.li
 * Time:2020/5/22   15:48
 * Description: 多type的 添加好友 适配器
 */
public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;

    private Context mContext;
    private List<AddFriendModel> mList;
    private LayoutInflater inflater;

    private OnclickListener onclickListener;

    public AddFriendAdapter(Context mContext, List<AddFriendModel> mList) {
        this.mContext = mContext;
        this.mList = mList;

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnclickListener(OnclickListener onclickListener) {
        this.onclickListener = onclickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(inflater.inflate(R.layout.layout_search_title_item, null));
        } else if (viewType == TYPE_CONTENT) {
            return new ContentViewHolder(inflater.inflate(R.layout.layout_search_user_item, null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AddFriendModel addFriendModel = mList.get(position);
        if (addFriendModel.getType() == TYPE_TITLE) {
            ((TitleViewHolder) holder).tv_title.setText(addFriendModel.getTitle());
        } else if (addFriendModel.getType() == TYPE_CONTENT) {
            // 设置头像
            GlideHelper.loadUrl(mContext, addFriendModel.getPhoto(), ((ContentViewHolder) holder).iv_photo);

            // 性别
            ((ContentViewHolder) holder).iv_sex.setImageResource(
                    addFriendModel.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);

            // 年龄
            ((ContentViewHolder) holder).tv_age.setText(addFriendModel.getAge() + "");

            // 昵称
            ((ContentViewHolder) holder).tv_nickname.setText(addFriendModel.getNickName());

            //  描述
            ((ContentViewHolder) holder).tv_desc.setText(addFriendModel.getDesc());

            if (addFriendModel.isContact()) { // 是联系人 显示 姓名 电话
                ((ContentViewHolder) holder).ll_contact_info.setVisibility(View.VISIBLE);
                ((ContentViewHolder) holder).tv_contact_name.setText(addFriendModel.getContactName());
                ((ContentViewHolder) holder).tv_contact_phone.setText(addFriendModel.getContactPhone());
            } else {
                ((ContentViewHolder) holder).ll_contact_info.setVisibility(View.GONE);
            }
        }

        // 点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListener != null) {
                    onclickListener.Onclick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView iv_photo;
        private ImageView iv_sex;
        private TextView tv_nickname;
        private TextView tv_age;
        private TextView tv_contact_name;
        private TextView tv_contact_phone;
        private LinearLayout ll_contact_info;
        private TextView tv_desc;

        public ContentViewHolder(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);

            iv_sex = itemView.findViewById(R.id.iv_sex);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_age = itemView.findViewById(R.id.tv_age);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            tv_contact_phone = itemView.findViewById(R.id.tv_contact_phone);
            ll_contact_info = itemView.findViewById(R.id.ll_contact_info);
            tv_desc = itemView.findViewById(R.id.tv_desc);

        }
    }

    public interface OnclickListener {
        void Onclick(int position);
    }

}
