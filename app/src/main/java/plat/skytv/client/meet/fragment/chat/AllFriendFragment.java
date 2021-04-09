package plat.skytv.client.meet.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseFragment;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.Friend;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.model.AllFriendModel;
import plat.skytv.client.meet.ui.UserInfoActivity;

/**
 * Author:boshuai.li
 * Time:2021/4/8   11:47
 * Description: 所有联系人
 */
public class AllFriendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mAllFriendRefreshLayout;
    private RecyclerView mAllFriendView;
    private View item_empty_view;
    private CommonAdapter<AllFriendModel> mAllFriendAdapter;

    private List<AllFriendModel> mList = new ArrayList<>();
    private LoadingView mLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_record, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLoadingView = new LoadingView(getActivity());
        mLoadingView.setLoadingText("正在查询...");
        mAllFriendRefreshLayout = view.findViewById(R.id.mAllFriendRefreshLayout);
        mAllFriendView = view.findViewById(R.id.mAllFriendView);
        item_empty_view = view.findViewById(R.id.item_empty_view);

        mAllFriendRefreshLayout.setOnRefreshListener(this);

        mAllFriendView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllFriendView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mAllFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<AllFriendModel>() {
            @Override
            public void onBindViewHolder(AllFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImageUrl(getActivity(), R.id.iv_photo, model.getUrl());
                viewHolder.setImageResource(R.id.iv_sex, model.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                viewHolder.setText(R.id.tv_nickname, model.getNickName());
                viewHolder.setText(R.id.tv_desc, model.getDesc());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.startActivity(getActivity(), model.getUserId());
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_all_friend_item;
            }
        });
        mAllFriendView.setAdapter(mAllFriendAdapter);

        queryMyFriends();
    }

    /**
     * 查询所有好友
     */
    private void queryMyFriends() {
        LogUtils.i("queryMyFriends enter");
        mLoadingView.show();
        mAllFriendRefreshLayout.setRefreshing(true);
        BmobManager.getInstance().queryMyFriend(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                mAllFriendRefreshLayout.setRefreshing(false);
                if (e != null) {
                    mLoadingView.hide();
                    item_empty_view.setVisibility(View.VISIBLE);
                    mAllFriendView.setVisibility(View.GONE);
                    return;
                }
                if (list == null || list.size() == 0) {
                    mLoadingView.hide();
                    item_empty_view.setVisibility(View.VISIBLE);
                    mAllFriendView.setVisibility(View.GONE);
                    return;
                }

                LogUtils.i("queryMyFriends enter list = " + list);

                item_empty_view.setVisibility(View.GONE);
                mAllFriendView.setVisibility(View.VISIBLE);

                if (mList.size() > 0) {
                    mList.clear();
                }

                for (int i = 0; i < list.size(); i++) {
                    String objectId = list.get(i).getFriendUser().getObjectId();
                    BmobManager.getInstance().queryObjectIdUser(objectId, new FindListener<IMUser>() {
                        @Override
                        public void done(List<IMUser> list, BmobException e) {
                            mLoadingView.hide();
                            if (e != null) {
                                return;
                            }
                            if (list == null || list.size() == 0) {
                                return;
                            }


                            IMUser imUser = list.get(0);
                            LogUtils.i("queryMyFriends enter imUser = " + imUser);
                            AllFriendModel allFriendModel = new AllFriendModel();
                            allFriendModel.setDesc("签名：" + imUser.getDesc());
                            allFriendModel.setNickName(imUser.getNickName());
                            allFriendModel.setSex(imUser.isSex());
                            allFriendModel.setUrl(imUser.getPhoto());
                            allFriendModel.setUserId(imUser.getObjectId());
                            mList.add(allFriendModel);
                            mAllFriendAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        if (mAllFriendRefreshLayout.isRefreshing()) {
            queryMyFriends();
        }
    }
}
