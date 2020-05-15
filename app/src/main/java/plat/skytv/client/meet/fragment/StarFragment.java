package plat.skytv.client.meet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liboshuai.framework.adapter.CloudTagAdapter;
import com.liboshuai.framework.base.BaseFragment;
import com.liboshuai.framework.utils.ToastUtil;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.List;

import plat.skytv.client.meet.R;
import plat.skytv.client.meet.ui.AddFriendActivity;

/**
 * Author:boshuai.li
 * Time:2020/5/13   11:47
 * Description: 星球
 */
public class StarFragment extends BaseFragment implements View.OnClickListener {

    private ImageView iv_camera;
    private ImageView iv_add;

    // 3D星球View
    private TagCloudView mCloudView;

    private TextView tv_star_title;
    private TextView tv_connect_status;
    private TextView tv_random;
    private LinearLayout ll_random;
    private TextView tv_soul;
    private LinearLayout ll_soul;
    private TextView tv_fate;
    private LinearLayout ll_fate;
    private TextView tv_love;
    private LinearLayout ll_love;

    private List<String> mStarList = new ArrayList<>();
    private CloudTagAdapter cloudTagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_star_title = (TextView) view.findViewById(R.id.tv_star_title);
        iv_camera = (ImageView) view.findViewById(R.id.iv_camera);
        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        tv_connect_status = (TextView) view.findViewById(R.id.tv_connect_status);
        mCloudView = (TagCloudView) view.findViewById(R.id.mCloudView);
        tv_random = (TextView) view.findViewById(R.id.tv_random);
        ll_random = (LinearLayout) view.findViewById(R.id.ll_random);
        tv_soul = (TextView) view.findViewById(R.id.tv_soul);
        ll_soul = (LinearLayout) view.findViewById(R.id.ll_soul);
        tv_fate = (TextView) view.findViewById(R.id.tv_fate);
        ll_fate = (LinearLayout) view.findViewById(R.id.ll_fate);
        tv_love = (TextView) view.findViewById(R.id.tv_love);
        ll_love = (LinearLayout) view.findViewById(R.id.ll_love);

        iv_add.setOnClickListener(this);
        iv_camera.setOnClickListener(this);


        for (int i = 0; i < 100; i++) {
            mStarList.add("star_" + i);
        }
        cloudTagAdapter = new CloudTagAdapter(mStarList, getActivity());
        mCloudView.setAdapter(cloudTagAdapter);
        mCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                ToastUtil.showTextToast(getActivity(), "position = " + position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                AddFriendActivity.startActivity(getActivity());
                break;
            case R.id.iv_camera:
                break;
        }
    }
}
