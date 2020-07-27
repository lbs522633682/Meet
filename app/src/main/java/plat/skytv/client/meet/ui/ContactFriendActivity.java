package plat.skytv.client.meet.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/7/27   16:47
 * Description: 从通讯录导入
 */
public class ContactFriendActivity extends BaseBackActivity {

    private RecyclerView mContactView;
    private Map<String, String> mContactsMap = new HashMap<>();

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

        loadContacts();

        loadUsers();
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
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);

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
}