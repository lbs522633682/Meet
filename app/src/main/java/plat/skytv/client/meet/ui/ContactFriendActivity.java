package plat.skytv.client.meet.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import plat.skytv.client.meet.R;
import plat.skytv.client.meet.adapter.AddFriendAdapter;
import plat.skytv.client.meet.model.AddFriendModel;

import static plat.skytv.client.meet.ui.AddFriendActivity.TYPE_CONTENT;
import static plat.skytv.client.meet.ui.AddFriendActivity.TYPE_TITLE;

/**
 * Author:boshuai.li
 * Time:2020/7/27   16:47
 * Description: 从通讯录导入
 */
public class ContactFriendActivity extends BaseBackActivity {

    private RecyclerView mContactView;
    private Map<String, String> mContactsMap = new HashMap<>();
    private List<AddFriendModel> mList = new ArrayList<>();
    private CommonAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_friend);
        initView();
    }

    private void initView() {
        mContactView = (RecyclerView) findViewById(R.id.mContactView);
        mContactView.setLayoutManager(new LinearLayoutManager(this));
        mContactView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //adapter = new AddFriendAdapter(this, mList);
        adapter = new CommonAdapter<AddFriendModel>(mList, new CommonAdapter.OnMoreBindDataListener<AddFriendModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(AddFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                // 设置头像
                viewHolder.setImageUrl(ContactFriendActivity.this, R.id.iv_photo, model.getPhoto());

                // 性别
                viewHolder.setImageResource(R.id.iv_sex, model.isSex()
                        ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);

                // 年龄
                viewHolder.setText(R.id.tv_age, model.getAge() + "");

                // 昵称
                viewHolder.setText(R.id.tv_nickname, model.getNickName());

                //  描述
                viewHolder.setText(R.id.tv_desc, model.getDesc());

                if (model.isContact()) { // 是联系人 显示 姓名 电话
                    viewHolder.setVisibility(R.id.ll_contact_info, View.VISIBLE);
                    viewHolder.setText(R.id.tv_contact_name, model.getContactName());
                    viewHolder.setText(R.id.tv_contact_phone, model.getContactPhone());

                } else {
                    viewHolder.setVisibility(R.id.ll_contact_info, View.GONE);
                }

                // 点击事件
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.startActivity(ContactFriendActivity.this, model.getUserId());
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                if (type == TYPE_TITLE) {
                    return R.layout.layout_search_title_item;
                } else if (type == TYPE_CONTENT) {
                    return R.layout.layout_search_user_item;
                }
                return 0;
            }
        });
        /*adapter.setOnclickListener(new AddFriendAdapter.OnclickListener() {
            @Override
            public void Onclick(int position) {
                // TODO 从通讯录 导入的 点击事件
                ToastUtil.showTextToast(ContactFriendActivity.this, "position = " + position);
            }
        });*/
        loadContacts();

        loadUsers();

        mContactView.setAdapter(adapter);
    }

    /**
     * 加载用户
     */
    private void loadUsers() {
        if (mContactsMap.size() > 0) {
            for (Map.Entry<String, String> entry : mContactsMap.entrySet()) {
                BmobManager.getInstance().queryPhoneUser(entry.getValue(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                IMUser imUser = list.get(0);
                                addContent(imUser, entry.getKey(), entry.getValue());
                            }
                        } else {
                            LogUtils.i("loadUsers queryPhoneUser error = " + e.toString());
                            ToastUtil.showTextToast(ContactFriendActivity.this, "Error = " + e.toString());
                        }
                    }
                });
            }
        }

    }

    private void loadContacts() {
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // android 8.0
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
            String name;
            String phone;
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phone = phone.replace(" ", "").replace("-", "");

                LogUtils.i("loadContacts name = " + name + ", phone = " + phone);

                mContactsMap.put(name, phone);
            }
        } else {
            //ToastUtil.showTextLongToast(this, "系统小于8.0， 不支持导入");
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            cursor = getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String name;
            String phone;
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phone = phone.replace(" ", "").replace("-", "");

                LogUtils.i("loadContacts name = " + name + ", phone = " + phone);

                mContactsMap.put(name, phone);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

    }

    private void addContent(IMUser imUser, String contactName, String contactPhone) {
        AddFriendModel model = new AddFriendModel();
        model.setType(AddFriendAdapter.TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setAge(imUser.getAge());
        model.setDesc(imUser.getDesc());
        model.setPhoto(imUser.getPhoto());
        model.setNickName(imUser.getNickName());
        model.setSex(model.isSex());

        model.setContact(true);
        model.setContactName(contactName);
        model.setContactPhone(contactPhone);

        mList.add(model);

        adapter.notifyDataSetChanged();

    }
}