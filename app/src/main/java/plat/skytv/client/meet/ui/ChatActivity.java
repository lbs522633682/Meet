package plat.skytv.client.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.event.EventManager;
import com.liboshuai.framework.event.MessageEvent;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gson.TextBean;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.model.ChatModel;

/**
 * Author:boshuai.li
 * Time:2021/4/9
 * Description:聊天界面
 */
public class ChatActivity extends BaseBackActivity implements View.OnClickListener {

    /**
     * 1. 跳转到聊天界面
     * 2. 实现聊天列表的数据填充
     * 3. 加载聊天的历史记录
     * 4. 实时更新聊天消息
     * 5. 发送一条消息
     */

    /**
     * @param context
     * @param userId
     * @param userName
     * @param userPhoto
     */
    public static void startActivity(Context context, String userId, String userName, String userPhoto) {
        Intent intent = new Intent(context, ChatActivity.class);

        intent.putExtra(Consts.INTENT_USER_ID, userId);
        intent.putExtra(Consts.INTENT_USER_NAME, userName);
        intent.putExtra(Consts.INTENT_USER_PHOTO, userPhoto);
        context.startActivity(intent);
    }

    // 消息列表
    private RecyclerView mChatView;
    // 消息编辑框
    private EditText et_input_msg;
    // 消息发送按钮
    private Button btn_send_msg;

    private LinearLayout ll_voice;
    private LinearLayout ll_camera;
    private LinearLayout ll_pic;
    private LinearLayout ll_location;

    /**
     * 对方得信息
     */
    private String yourUserId;
    private String yourUserName;
    private String yourUserPhoto;

    /**
     * 我自己得信息
     */
    private String myPhoto;

    private List<ChatModel> mList = new ArrayList<>();
    private CommonAdapter<ChatModel> mChatAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        initView();
    }

    private void initView() {
        mChatView = findViewById(R.id.mChatView);
        et_input_msg = findViewById(R.id.et_input_msg);
        btn_send_msg = findViewById(R.id.btn_send_msg);
        ll_voice = findViewById(R.id.ll_voice);
        ll_camera = findViewById(R.id.ll_camera);
        ll_pic = findViewById(R.id.ll_pic);
        ll_location = findViewById(R.id.ll_location);

        btn_send_msg.setOnClickListener(this);

        mChatAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnMoreBindDataListener<ChatModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(ChatModel model, CommonViewHolder viewHolder, int type, int position) {
                if (ChatModel.TYPE_LEFT_TEXT == model.getType()) {
                    viewHolder.setText(R.id.tv_left_text, model.getText());
                    viewHolder.setImageUrl(mContext, R.id.iv_left_photo, yourUserPhoto);
                } else if (ChatModel.TYPE_RIGHT_TEXT == model.getType()) {
                    viewHolder.setText(R.id.tv_right_text, model.getText());
                    viewHolder.setImageUrl(mContext, R.id.iv_right_photo, myPhoto);
                } else if (ChatModel.TYPE_LEFT_IMG == model.getType()) {
                } else if (ChatModel.TYPE_RIGHT_IMG == model.getType()) {
                } else if (ChatModel.TYPE_LEFT_LOCATION == model.getType()) {
                } else if (ChatModel.TYPE_RIGHT_LOCATION == model.getType()) {
                }
            }

            @Override
            public int getLayoutId(int type) {
                if (ChatModel.TYPE_LEFT_TEXT == type) {
                    return R.layout.layout_chat_left_text;
                } else if (ChatModel.TYPE_RIGHT_TEXT == type) {
                    return R.layout.layout_chat_right_text;
                } else if (ChatModel.TYPE_LEFT_IMG == type) {
                    return R.layout.layout_chat_left_img;
                } else if (ChatModel.TYPE_RIGHT_IMG == type) {
                    return R.layout.layout_chat_right_img;
                } else if (ChatModel.TYPE_LEFT_LOCATION == type) {
                    return R.layout.layout_chat_left_location;
                } else if (ChatModel.TYPE_RIGHT_LOCATION == type) {
                    return R.layout.layout_chat_right_location;
                }
                return 0;
            }
        });

        mChatView.setLayoutManager(new LinearLayoutManager(this));
        mChatView.setAdapter(mChatAdapter);

        loadUserInfo();

        queryMessage();
    }

    /**
     * 查询消息
     */
    private void queryMessage() {
        CloudManager.getInstance().getHistoryMessages(yourUserId, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    parsingListMessage(messages);
                } else {
                    // 本地没有消息记录
                    queryRemoteMessage();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("queryMessage onError errorCode = " + errorCode);
            }
        });
    }

    /**
     * 解析历史记录
     *
     * @param messages
     */
    private void parsingListMessage(List<Message> messages) {
        // 倒序
        Collections.reverse(messages);
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            String objectName = message.getObjectName();

            if (CloudManager.MSG_TEXT_NAME.equals(objectName)) {
                TextMessage textMessage = (TextMessage) message.getContent();
                String content = textMessage.getContent();
                TextBean textBean = JsonUtil.parseObject(content, TextBean.class);
                if (textBean != null) {
                    if (CloudManager.TYPE_TEXT.equals(textBean.getType())) {
                        // 添加到ui 判断是 对方还是自己
                        if (message.getSenderUserId().equals(yourUserId)) {
                            // 对方，添加到左侧
                            addText(0, textBean.getMsg());
                        } else {
                            addText(1, textBean.getMsg());
                        }
                    }
                }
            } else if (CloudManager.MSG_IMAGE_NAME.equals(objectName)) {

            } else if (CloudManager.MSG_LOCATION_NAME.equals(objectName)) {

            }
        }
    }

    /**
     * 从服务器查询消息记录
     */
    private void queryRemoteMessage() {
        CloudManager.getInstance().getRemoteHistoryMessages(yourUserId, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    parsingListMessage(messages);
                } else {
                    // 远程消息列表为空
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("queryRemoteMessage onError errorCode = " + errorCode);
            }
        });
    }

    /**
     * 加载聊天人得信息
     */
    private void loadUserInfo() {
        yourUserId = getIntent().getStringExtra(Consts.INTENT_USER_ID);
        yourUserName = getIntent().getStringExtra(Consts.INTENT_USER_NAME);
        yourUserPhoto = getIntent().getStringExtra(Consts.INTENT_USER_PHOTO);

        myPhoto = BmobManager.getInstance().getUser().getPhoto();

        if (!TextUtils.isEmpty(yourUserName)) {
            // 设置标题
            getSupportActionBar().setTitle(yourUserName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_msg:
                String msg = et_input_msg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                LogUtils.i("sendMsg msg = " + msg);

                // 发送消息
                CloudManager.getInstance().sendTextMessage(msg, CloudManager.TYPE_TEXT, yourUserId);

                // 自己发送，添加ui到右侧
                addText(1, msg);
                // 发送完 清空内容
                et_input_msg.setText("");
                break;
        }
    }

    /**
     * 添加数据得基类
     *
     * @param model
     */
    private void bseAddItem(ChatModel model) {
        mList.add(model);
        mChatAdapter.notifyDataSetChanged();
        // 同时需要滑动到底部
        mChatView.scrollToPosition(mList.size() - 1);
    }

    /**
     * 添加左侧文字
     *
     * @param text
     * @param index 0 左侧 1 右侧
     */
    private void addText(int index, String text) {
        ChatModel model = new ChatModel();
        model.setText(text);
        if (0 == index) {
            model.setType(ChatModel.TYPE_LEFT_TEXT);
        } else {
            model.setType(ChatModel.TYPE_RIGHT_TEXT);
        }
        bseAddItem(model);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == EventManager.FLAG_SEND_TEXT) {
            LogUtils.i("ChatActivity event = " + event);
            String text = event.getText();
            if (event.getUserId().equals(yourUserId)) {
                addText(0, text);
            } else {
                addText(1, text);
            }
        }
    }
}
