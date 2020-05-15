package plat.skytv.client.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.base.BaseBackActivity;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/5/15   16:47
 * Description: 添加搜索好友页面
 */
public class AddFriendActivity extends BaseBackActivity {


    private LinearLayout ll_to_contact;
    private EditText et_phone;
    private ImageView iv_search;
    private RecyclerView mSearchResultView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AddFriendActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
    }

    private void initView() {
        ll_to_contact = (LinearLayout) findViewById(R.id.ll_to_contact);
        et_phone = (EditText) findViewById(R.id.et_phone);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        mSearchResultView = (RecyclerView) findViewById(R.id.mSearchResultView);
    }

    private void submit() {
        // validate
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "phone不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
