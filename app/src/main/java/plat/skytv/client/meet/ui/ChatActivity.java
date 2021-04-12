package plat.skytv.client.meet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.liboshuai.framework.helper.FileHelper;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.manager.MapManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gson.TextBean;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
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
     * 发送文本消息
     *
     * 1. 跳转到聊天界面
     * 2. 实现聊天列表的数据填充
     * 3. 加载聊天的历史记录
     * 4. 实时更新聊天消息
     * 5. 发送一条消息
     */

    /**
     * 发送图片消息
     * 1. 读取相机 相册
     * 2. 发送图片消息
     * 3. 完成适配器填充
     * 4. 完成service的图片接受
     * 5. 通知ui刷新图片
     */

    /**
     * 发送地址的逻辑
     * 1. 获取地址
     * 2. 发送位置消息
     * 不能忘记：
     * 1. 历史消息
     * 2. 适配器
     * 3. 发送消息
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
    private File mImgFile; // 选中 待发送得图片文件
    private int LOCATION_REQUEST_CODE = 1888;

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

        ll_camera.setOnClickListener(this);
        ll_pic.setOnClickListener(this);
        ll_location.setOnClickListener(this);

        mChatAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnMoreBindDataListener<ChatModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(ChatModel model, CommonViewHolder viewHolder, int type, int position) {
                if (ChatModel.TYPE_LEFT_TEXT == model.getType()) { // 文本
                    viewHolder.setText(R.id.tv_left_text, model.getText());
                    viewHolder.setImageUrl(mContext, R.id.iv_left_photo, yourUserPhoto);
                } else if (ChatModel.TYPE_RIGHT_TEXT == model.getType()) { // 右侧 文本
                    viewHolder.setText(R.id.tv_right_text, model.getText());
                    viewHolder.setImageUrl(mContext, R.id.iv_right_photo, myPhoto);
                } else if (ChatModel.TYPE_LEFT_IMG == model.getType()) { // 左侧图片
                    boolean isUrl = true;
                    if (TextUtils.isEmpty(model.getImgUrl())) {
                        isUrl = false;
                        viewHolder.setImageFile(mContext, R.id.iv_left_img, model.getLocalFile());
                    } else {
                        viewHolder.setImageUrl(mContext, R.id.iv_left_img, model.getImgUrl());
                    }
                    viewHolder.setImageUrl(mContext, R.id.iv_left_photo, yourUserPhoto);

                    boolean finalIsUrl = isUrl;
                    viewHolder.getView(R.id.iv_left_img).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImagePreviewActivity.startActivity(mContext, finalIsUrl, finalIsUrl ? model.getImgUrl() : model.getLocalFile().getPath());
                        }
                    });
                } else if (ChatModel.TYPE_RIGHT_IMG == model.getType()) { // 右侧图片
                    boolean isUrl = true;
                    if (TextUtils.isEmpty(model.getImgUrl())) {
                        viewHolder.setImageFile(mContext, R.id.iv_right_img, model.getLocalFile());
                        isUrl = false;
                    } else {
                        viewHolder.setImageUrl(mContext, R.id.iv_right_img, model.getImgUrl());
                    }
                    viewHolder.setImageUrl(mContext, R.id.iv_right_photo, myPhoto);

                    boolean finalIsUrl = isUrl;
                    viewHolder.getView(R.id.iv_right_img).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImagePreviewActivity.startActivity(mContext, finalIsUrl, finalIsUrl ? model.getImgUrl() : model.getLocalFile().getPath());
                        }
                    });
                } else if (ChatModel.TYPE_LEFT_LOCATION == model.getType()) {
                    viewHolder.setImageUrl(mContext, R.id.iv_left_photo, yourUserPhoto);
                    viewHolder.setImageUrl(mContext, R.id.iv_left_location_img, model.getMapUrl());
                    viewHolder.setText(R.id.tv_left_address, model.getAddress());

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocationActivity.startActivity(ChatActivity.this,
                                    false,
                                    model.getLa(),
                                    model.getLo(),
                                    model.getAddress(),
                                    LOCATION_REQUEST_CODE);
                        }
                    });

                } else if (ChatModel.TYPE_RIGHT_LOCATION == model.getType()) {
                    viewHolder.setImageUrl(mContext, R.id.iv_right_photo, myPhoto);
                    viewHolder.setImageUrl(mContext, R.id.iv_right_location_img, model.getMapUrl());
                    viewHolder.setText(R.id.tv_right_address, model.getAddress());
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocationActivity.startActivity(ChatActivity.this,
                                    false,
                                    model.getLa(),
                                    model.getLo(),
                                    model.getAddress(),
                                    LOCATION_REQUEST_CODE);
                        }
                    });
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
                ImageMessage imageMessage = (ImageMessage) message.getContent();
                String imgUrl = imageMessage.getRemoteUri().toString();
                LogUtils.i("parsingListMessage imgUrl = " + imgUrl);
                // 添加到ui 判断是 对方还是自己
                if (!TextUtils.isEmpty(imgUrl)) {
                    if (message.getSenderUserId().equals(yourUserId)) {
                        // 对方，添加到左侧
                        addImage(0, imgUrl);
                    } else {
                        addImage(1, imgUrl);
                    }
                }
            } else if (CloudManager.MSG_LOCATION_NAME.equals(objectName)) {
                LocationMessage locationMessage = (LocationMessage) message.getContent();
                if (message.getSenderUserId().equals(yourUserId)) {
                    // 对方，添加到左侧
                    addLocation(0, locationMessage.getLat(), locationMessage.getLng(), locationMessage.getPoi());
                } else {
                    addLocation(1, locationMessage.getLat(), locationMessage.getLng(), locationMessage.getPoi());
                }
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
            case R.id.ll_camera: // 相机
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.ll_pic: // 相册
                FileHelper.getInstance().toAlbum(this);
                break;
            case R.id.ll_location: // 位置
                LocationActivity.startActivity(this, true, 0, 0, "", LOCATION_REQUEST_CODE);
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
     * 添加文字
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

    /**
     * 添加 图片
     *
     * @param index
     * @param url
     */
    private void addImage(int index, String url) {
        ChatModel model = new ChatModel();
        model.setImgUrl(url);
        if (0 == index) {
            model.setType(ChatModel.TYPE_LEFT_IMG);
        } else {
            model.setType(ChatModel.TYPE_RIGHT_IMG);
        }
        bseAddItem(model);
    }

    /**
     * 添加 图片文件
     *
     * @param index
     * @param file
     */
    private void addImage(int index, File file) {
        ChatModel model = new ChatModel();
        model.setLocalFile(file);
        if (0 == index) {
            model.setType(ChatModel.TYPE_LEFT_IMG);
        } else {
            model.setType(ChatModel.TYPE_RIGHT_IMG);
        }
        bseAddItem(model);
    }

    /**
     * 添加位置信息
     *
     * @param index
     * @param la
     * @param lo
     * @param address
     */
    private void addLocation(int index, double la, double lo, String address) {
        ChatModel model = new ChatModel();
        model.setLa(la);
        model.setLo(lo);
        model.setAddress(address);
        model.setMapUrl(MapManager.getInstance().getMapUrl(la, lo));
        if (0 == index) {
            model.setType(ChatModel.TYPE_LEFT_LOCATION);
        } else {
            model.setType(ChatModel.TYPE_RIGHT_LOCATION);
        }
        bseAddItem(model);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (!event.getUserId().equals(yourUserId)) {
            return;
        }
        switch (event.getType()) {
            case EventManager.FLAG_SEND_TEXT:
                LogUtils.i("ChatActivity event = " + event);
                String text = event.getText();
                addText(0, text);
                break;
            case EventManager.FLAG_SEND_IMAGE:
                LogUtils.i("ChatActivity event = " + event);
                String imgUrl = event.getImgUrl();
                addImage(0, imgUrl);
                break;

            case EventManager.FLAG_SEND_LOCATION:
                LogUtils.i("ChatActivity event = " + event);
                addLocation(0, event.getLa(), event.getLo(), event.getAddress());
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FileHelper.CAMERA_REQUEST_CODE) {
                mImgFile = FileHelper.getInstance().getTempFile();
            } else if (requestCode == FileHelper.ALBUM_REQUEST_CODE) {
                Uri uri = data.getData();
                if (uri != null) {
                    LogUtils.i("onActivityResult path = " + uri.getPath());
                    // 获取图片的真是路径
                    String imgRealPath = FileHelper.getInstance().getRealImgPathFromUri(this, uri);
                    LogUtils.i("onActivityResult imgRealPath = " + imgRealPath);
                    if (!TextUtils.isEmpty(imgRealPath)) {
                        mImgFile = new File(imgRealPath);
                    }
                }
            } else if (LOCATION_REQUEST_CODE == requestCode) {
                double la = data.getDoubleExtra("la", 0);
                double lo = data.getDoubleExtra("lo", 0);
                String address = data.getStringExtra("address");
                LogUtils.i("onActivityResult la = " + la + ", lo = " + lo + ", address = " + address);

                // 发送消息
                addLocation(1, la, lo, address);
                CloudManager.getInstance().sendLocationMessage(yourUserId, la, lo, address);
            }
        }

        // 设置头像
        if (mImgFile != null) {
            // 发送图片消息
            CloudManager.getInstance().sendImageMessage(yourUserId, mImgFile);

            // 刷新自己的列表
            addImage(1, mImgFile);

            mImgFile = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
