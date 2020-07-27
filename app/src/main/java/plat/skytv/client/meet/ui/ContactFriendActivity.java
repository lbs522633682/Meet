package plat.skytv.client.meet.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.base.BaseBackActivity;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/7/27   16:47
 * Description: 从通讯录导入
 */
public class ContactFriendActivity extends BaseBackActivity {

    private RecyclerView mContactView;

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
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null);

        String name;
        String phone;
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
    }
}