package plat.skytv.client.meet;

import android.os.Bundle;

import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.utils.ToastUtil;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 主页
 */
public class MainActivity extends BaseUIActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IMUser user = BmobManager.getInstance().getUser();

        ToastUtil.showTextToast(this, user.getMobilePhoneNumber());

    }

}
