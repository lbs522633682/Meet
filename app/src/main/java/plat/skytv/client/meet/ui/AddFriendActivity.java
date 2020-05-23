package plat.skytv.client.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.adapter.AddFriendAdapter;
import plat.skytv.client.meet.model.AddFriendModel;

/**
 * Author:boshuai.li
 * Time:2020/5/15   16:47
 * Description: 添加搜索好友页面
 */
public class AddFriendActivity extends BaseBackActivity implements View.OnClickListener {


    private LinearLayout ll_to_contact;
    private EditText et_phone;
    private ImageView iv_search;
    private RecyclerView mSearchResultView;
    private View include_empty_view;

    private AddFriendAdapter mAddFriendAdapter;
    private List<AddFriendModel> mList = new ArrayList<>();

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AddFriendActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        ll_to_contact = (LinearLayout) findViewById(R.id.ll_to_contact);
        et_phone = (EditText) findViewById(R.id.et_phone);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        mSearchResultView = (RecyclerView) findViewById(R.id.mSearchResultView);
        include_empty_view = findViewById(R.id.include_empty_view);

        iv_search.setOnClickListener(this);
        ll_to_contact.setOnClickListener(this);

        mSearchResultView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAddFriendAdapter = new AddFriendAdapter(this, mList);
        mSearchResultView.setAdapter(mAddFriendAdapter);

        mAddFriendAdapter.setOnclickListener(new AddFriendAdapter.OnclickListener() {
            @Override
            public void Onclick(int position) {
                ToastUtil.showTextToast(AddFriendActivity.this, "show position = " + position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                queryFriendByPhone();
                break;
            case R.id.ll_to_contact:
                break;
        }
    }

    /**
     * 查询好友
     * 11510603000
     */
    private void queryFriendByPhone() {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "phone不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String mobilePhoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
        LogUtils.i("queryFriendByPhone mobilePhoneNumber = " + mobilePhoneNumber);
        if (phone.equals(mobilePhoneNumber)) {
            ToastUtil.showTextToast(this, "不能查询自己");
            return;
        }

        BmobManager.getInstance().queryPhoneUser(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) { // 查询成功
                    if (list != null) {
                        IMUser imUser = list.get(0);
                        LogUtils.i("queryFriendByPhone imUser = " + imUser);

                        include_empty_view.setVisibility(View.GONE);
                        mSearchResultView.setVisibility(View.VISIBLE);

                        // 每次查询有数据的话  就清空一下
                        mList.clear();

                        addTitle("查询结果");
                        addContent(imUser);

                        mAddFriendAdapter.notifyDataSetChanged();

                        // 推荐好友

                        pushUser();
                    }
                } else {
                    LogUtils.e("queryFriendByPhone err = " + e);
                    // 显示空的view
                    include_empty_view.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.GONE);
                }
            }
        });

        /**
         * {"age":21,
         * "birthday":"1993-9-28",
         * "constellation":"天蝎座",
         * "desc":"我要依偎在你怀里，做你一人的猫。",
         * "hobby":"卖萌",
         * "nickName":"我迷了鹿",
         * "photo":"http:\/\/b-ssl.duitang.com\/uploads\/item\/201707\/15\/20170715095610_vRHVW.thumb.700_0.jpeg",
         * "sex":false,
         * "status":"热恋",
         * "tokenNickName":"我迷了鹿",
         * "tokenPhoto":"http:\/\/b-ssl.duitang.com\/uploads\/item\/201707\/15\/20170715095610_vRHVW.thumb.700_0.jpeg",
         * "mobilePhoneNumber":"12510603000",
         * "mobilePhoneNumberVerified":true,
         * "username":"12510603000",
         * "_c_":"IMUser",
         * "createdAt":"2020-05-22 15:39:48",
         * "objectId":"55645b896a",
         * "updatedAt":"2020-05-22 15:39:48"}
         */
    }

    /**
     * 推荐好友
     */
    private void pushUser() {

        BmobManager.getInstance().queryAllUser(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    // 查询成功，显示前100个

                    if (list != null && list.size() > 0) {
                        int num = Math.min(list.size(), 100);

                        addTitle("推荐好友");
                        String mobilePhoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();

                        for (int i = 0; i < num; i++) {
                            // 不能把自己推荐给自己
                            IMUser imUser = list.get(i);
                            if (mobilePhoneNumber.equals(imUser.getMobilePhoneNumber())) {
                                continue;
                            }
                            addContent(imUser);
                        }

                        mAddFriendAdapter.notifyDataSetChanged();
                    }
                } else {
                    LogUtils.i("pushUser err = " + e);
                }
            }
        });
    }


    private void addContent(IMUser imUser) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendAdapter.TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setAge(imUser.getAge());
        model.setDesc(imUser.getDesc());
        model.setPhoto(imUser.getPhoto());
        model.setNickName(imUser.getNickName());
        model.setSex(model.isSex());
        mList.add(model);

    }

    private void addTitle(String title) {
        AddFriendModel addFriendModel = new AddFriendModel();
        addFriendModel.setType(AddFriendAdapter.TYPE_TITLE);
        addFriendModel.setTitle(title);
        mList.add(addFriendModel);

    }
}
