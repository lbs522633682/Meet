package plat.skytv.client.meet.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseFragment;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.helper.GlideHelper;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.view.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import gson.TextBean;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.adapter.AddFriendAdapter;
import plat.skytv.client.meet.model.ChatRecordModel;

/**
 * Author:boshuai.li
 * Time:2021/4/8   11:47
 * Description: 通话记录
 */
public class ChatRecordFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mAllFriendRefreshLayout;
    private RecyclerView mAllFriendView;
    private View item_empty_view;
    private CommonAdapter<ChatRecordModel> mChatRecordAdapter;

    private List<ChatRecordModel> mList = new ArrayList<>();
    private LoadingView mLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_record, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLoadingView = new LoadingView(getActivity());
        mLoadingView.setLoadingText("正在加载...");
        mAllFriendRefreshLayout = view.findViewById(R.id.mAllFriendRefreshLayout);
        mAllFriendView = view.findViewById(R.id.mAllFriendView);
        item_empty_view = view.findViewById(R.id.item_empty_view);

        mAllFriendRefreshLayout.setOnRefreshListener(this);

        mAllFriendView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllFriendView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mChatRecordAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<ChatRecordModel>() {
            @Override
            public void onBindViewHolder(ChatRecordModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_nickname, model.getNickName());
                viewHolder.setImageUrl(getActivity(), R.id.iv_photo, model.getUrl());
                viewHolder.setText(R.id.tv_content, model.getEndMsg());
                viewHolder.setText(R.id.tv_time, model.getTime());
                if (model.getUnReadSize() == 0) {
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.tv_un_read, model.getUnReadSize() + "");
                }
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_chat_record_item;
            }
        });
        mAllFriendView.setAdapter(mChatRecordAdapter);

        // 查询聊天记录
        queryChatRecord();
    }

    private void queryChatRecord() {
        mLoadingView.show();
        mAllFriendRefreshLayout.setRefreshing(true);
        CloudManager.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                mAllFriendRefreshLayout.setRefreshing(false);

                if (conversations != null && conversations.size() > 0) {
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    for (int i = 0; i < conversations.size(); i++) {
                        Conversation conversation = conversations.get(i);
                        LogUtils.i("queryChatRecord conversation = " + conversation);

                        String id = conversation.getTargetId();
                        BmobManager.getInstance().queryObjectIdUser(id, new FindListener<IMUser>() {
                            @Override
                            public void done(List<IMUser> list, BmobException e) {
                                mLoadingView.hide();
                                LogUtils.i("queryChatRecord queryObjectIdUser list = " + list + ", e = " + e);
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        IMUser imUser = list.get(0);
                                        ChatRecordModel chatRecordModel = new ChatRecordModel();

                                        chatRecordModel.setNickName(imUser.getNickName());
                                        chatRecordModel.setUrl(imUser.getPhoto());
                                        chatRecordModel.setTime(new SimpleDateFormat("HH:mm:ss")
                                                .format(conversation.getReceivedTime()));
                                        // 设置未读信息数
                                        chatRecordModel.setUnReadSize(conversation.getUnreadMessageCount());

                                        // 判断消息类型
                                        String objectName = conversation.getObjectName();
                                        if (CloudManager.MSG_TEXT_NAME.equals(objectName)) { // 文本消息
                                            LogUtils.i("queryChatRecord getLatestMessage = " + conversation.getLatestMessage());
                                            TextMessage textMessage = (TextMessage) conversation.getLatestMessage();
                                            TextBean textBean = JsonUtil.parseObject(textMessage.getContent(), TextBean.class);
                                            if (textBean != null) {
                                                if (CloudManager.TYPE_TEXT.equals(textBean.getType())) {
                                                    chatRecordModel.setEndMsg(textBean.getMsg());
                                                    mList.add(chatRecordModel);
                                                }
                                            }
                                        } else if (CloudManager.MSG_IMAGE_NAME.equals(objectName)) { // 图片消息
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_img));
                                            mList.add(chatRecordModel);
                                        } else if (CloudManager.MSG_LOCATION_NAME.equals(objectName)) { // 位置消息
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_location));
                                            mList.add(chatRecordModel);
                                        } else {
                                            LogUtils.i("queryChatRecord queryObjectIdUser 未知类型消息");
                                        }
                                        if (mList.size() > 0) {
                                            mAllFriendView.setVisibility(View.VISIBLE);
                                            item_empty_view.setVisibility(View.GONE);
                                        } else {
                                            mAllFriendView.setVisibility(View.GONE);
                                            item_empty_view.setVisibility(View.VISIBLE);
                                        }
                                        mChatRecordAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    mLoadingView.hide();
                    mAllFriendView.setVisibility(View.GONE);
                    item_empty_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("queryChatRecord queryObjectIdUser errorCode = " + errorCode);
                mAllFriendRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        /**
         * 下拉刷新会触发本方法
         */
        LogUtils.i("onRefresh enter isRefreshing = " + mAllFriendRefreshLayout.isRefreshing());

        if (mAllFriendRefreshLayout.isRefreshing()) {
            queryChatRecord();
        }
    }
}
