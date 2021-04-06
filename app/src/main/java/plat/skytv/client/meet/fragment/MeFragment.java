package plat.skytv.client.meet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liboshuai.framework.base.BaseFragment;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.helper.GlideHelper;

import de.hdodenhof.circleimageview.CircleImageView;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.ui.NewFriendActivity;

/**
 * Author:boshuai.li
 * Time:2020/5/13   11:47
 * Description: 我的
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView iv_me_photo;
    private TextView tv_nickname;
    private TextView tv_server_status;
    private LinearLayout ll_me_info;
    private LinearLayout ll_new_friend;
    private LinearLayout ll_private_set;
    private LinearLayout ll_share;
    private LinearLayout ll_notice;
    private LinearLayout ll_setting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        iv_me_photo = view.findViewById(R.id.iv_me_photo);
        tv_nickname = view.findViewById(R.id.tv_nickname);
        tv_server_status = view.findViewById(R.id.tv_server_status);
        ll_notice = view.findViewById(R.id.ll_notice);

        ll_me_info = view.findViewById(R.id.ll_me_info);
        ll_new_friend = view.findViewById(R.id.ll_new_friend);
        ll_private_set = view.findViewById(R.id.ll_private_set);
        ll_share = view.findViewById(R.id.ll_share);
        ll_setting = view.findViewById(R.id.ll_setting);

        ll_me_info.setOnClickListener(this);
        ll_new_friend.setOnClickListener(this);
        ll_private_set.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_setting.setOnClickListener(this);


        loadMeInfo();
    }

    /**
     * 加载个人信息
     */
    private void loadMeInfo() {
        IMUser user = BmobManager.getInstance().getUser();

        GlideHelper.loadUrl(getContext(), user.getPhoto(), iv_me_photo);

        tv_nickname.setText(user.getNickName());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_me_info:
                break;
            case R.id.ll_new_friend:
                NewFriendActivity.startActivity(getActivity());
                break;
            case R.id.ll_private_set:
                break;
            case R.id.ll_share:
                break;
            case R.id.ll_setting:
                break;
        }
    }
}
