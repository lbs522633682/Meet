package plat.skytv.client.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.Friend;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.helper.GlideHelper;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;
import com.liboshuai.framework.view.DialogView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.model.UserInfoModel;

/**
 * Author:boshuai.li
 * Time:2021/4/2   16:47
 * Description: 用户信息页面
 */
public class UserInfoActivity extends BaseBackActivity implements View.OnClickListener {

    /**
     * 1. 根据传递的id，查询用户信息，并展示
     *      -- 普通信息
     *      -- recycleview 宫格信息
     * 2. 建立好友关系模型， 与我有关系的是好友
     *      1. 在我的好友列表中
     *      2. 同意了我的好友申请 BmobObject
     *      3. 查询所有的Frend表，其中user列对应的都是我的朋友
     * 3. 实现添加好友提示框
     * 4. 发送添加好友消息
     *      1. 自定义消息类型
     *      2. 自定义协议
     *      发送文本消息，Content，对文本进行处理，增加一个json，定义一个标记来显示
     * 5. 接收 添加好友消息
     */


    /**
     * @param context
     * @param userId
     */
    public static void startActivity(Context context, String userId) {
        LogUtils.i("UserInfoActivity userid = " + userId);
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Consts.INTENT_USER_ID, userId);
        context.startActivity(intent);
    }

    private DialogView mAddFriendDialog;
    private TextView tv_cancel;
    private TextView tv_add_friend;
    private EditText et_msg;

    private RelativeLayout ll_back = null;
    private CircleImageView iv_user_photo = null;
    private TextView tv_nickname = null;
    private TextView tv_desc = null;
    private RecyclerView mUserInfoView = null;
    private Button btn_add_friend = null;
    private LinearLayout ll_is_friend = null;
    private Button btn_chat = null;
    private Button btn_audio_chat = null;
    private Button btn_video_chat = null;
    //个人信息颜色
    private int[] mColor = {0x881E90FF, 0x8800FF7F, 0x88FFD700, 0x88FF6347, 0x88F08080, 0x8840E0D0};

    private CommonAdapter<UserInfoModel> mUserInfoAdapter;
    private List<UserInfoModel> mList;

    private String userId = "";

    // 当前正在展示得用户
    private IMUser imUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();
    }

    private void initView() {

        initAddFriendDialog();

        userId = getIntent().getStringExtra(Consts.INTENT_USER_ID);

        ll_back = findViewById(R.id.ll_back);
        iv_user_photo = findViewById(R.id.iv_user_photo);
        tv_nickname = findViewById(R.id.tv_nickname);
        tv_desc = findViewById(R.id.tv_desc);
        mUserInfoView = findViewById(R.id.mUserInfoView);
        ll_is_friend = findViewById(R.id.ll_is_friend);

        btn_add_friend = findViewById(R.id.btn_add_friend);
        btn_chat = findViewById(R.id.btn_chat);
        btn_audio_chat = findViewById(R.id.btn_audio_chat);
        btn_video_chat = findViewById(R.id.btn_video_chat);

        btn_add_friend.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_audio_chat.setOnClickListener(this);
        btn_video_chat.setOnClickListener(this);

        mList = new ArrayList<>();

        mUserInfoAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<UserInfoModel>() {
            @Override
            public void onBindViewHolder(UserInfoModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_type, model.getContent());
                viewHolder.setText(R.id.tv_content, model.getTitle());
                viewHolder.getView(R.id.ll_bg).setBackgroundColor(model.getBgColor());
                //viewHolder.setBackgroundColor(R.id.ll_bg, model.getBgColor());
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_user_info_item;
            }
        });

        mUserInfoView.setLayoutManager(new GridLayoutManager(UserInfoActivity.this, 3));
        mUserInfoView.setAdapter(mUserInfoAdapter);

        queryUserInfo();
    }


    // 创建一个添加好友的提示框
    private void initAddFriendDialog() {
        mAddFriendDialog = DialogManager.getInstance().initView(this, R.layout.dialog_send_friend);
        tv_cancel = mAddFriendDialog.findViewById(R.id.tv_cancel);
        tv_add_friend = mAddFriendDialog.findViewById(R.id.tv_add_friend);
        et_msg = mAddFriendDialog.findViewById(R.id.et_msg);

        tv_add_friend.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    private void queryUserInfo() {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        // 查询用户信息
        BmobManager.getInstance().queryObjectIdUser(userId, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        imUser = list.get(0);
                        updateUserInfo(imUser);
                    }
                }
            }
        });

        // 查询我的好友
        BmobManager.getInstance().queryMyFriend(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Friend friend = list.get(i);
                            LogUtils.i("queryAllFriend friend = " + friend);
                            // 如果 查询对象中的 userid == 我的userid，就是我的朋友
                            if (friend.getFriendUser().getObjectId().equals(userId)) {
                                // 是好友关系
                                btn_add_friend.setVisibility(View.GONE);
                                ll_is_friend.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });

    }

    private void updateUserInfo(IMUser imUser) {
        // 设置头像
        GlideHelper.loadUrl(UserInfoActivity.this, imUser.getPhoto(), iv_user_photo);

        tv_nickname.setText(imUser.getNickName());
        tv_desc.setText(imUser.getDesc());

        // 填充recycleview数据 性别 年龄 生日 爱好 单身状态
        addUserInfoModel(mColor[0], getString(R.string.text_me_info_sex), imUser.isSex() ? getString(R.string.text_me_info_boy) : getString(R.string.text_me_info_girl));
        addUserInfoModel(mColor[1], getString(R.string.text_me_info_age), imUser.getAge() + getString(R.string.text_search_age));
        addUserInfoModel(mColor[2], getString(R.string.text_me_info_birthday), imUser.getBirthday());
        addUserInfoModel(mColor[3], getString(R.string.text_me_info_constellation), imUser.getConstellation());
        addUserInfoModel(mColor[4], getString(R.string.text_me_info_hobby), imUser.getHobby());
        addUserInfoModel(mColor[5], getString(R.string.text_me_info_status), imUser.getStatus());

        mUserInfoAdapter.notifyDataSetChanged();
    }

    private void addUserInfoModel(int bgColor, String title, String content) {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setBgColor(bgColor);
        userInfoModel.setContent(content);
        userInfoModel.setTitle(title);
        mList.add(userInfoModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_friend:
                DialogManager.getInstance().show(mAddFriendDialog);
                break;
            case R.id.btn_chat:
                if (imUser != null) {
                    ChatActivity.startActivity(this, imUser.getObjectId(), imUser.getNickName(), imUser.getPhoto());
                }
                break;
            case R.id.btn_audio_chat:

                break;
            case R.id.btn_video_chat:

                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mAddFriendDialog);
                break;
            case R.id.tv_add_friend:
                String msg = et_msg.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    msg = "你好，我是：" + BmobManager.getInstance().getUser().getNickName();
                }
                DialogManager.getInstance().hide(mAddFriendDialog);
                CloudManager.getInstance().sendTextMessage(msg, CloudManager.TYPE_ADD_FRIEND, userId);
                ToastUtil.showTextToast(this, "消息发送成功");
                break;
        }
    }
}

