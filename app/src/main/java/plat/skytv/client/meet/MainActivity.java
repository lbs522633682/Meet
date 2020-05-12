package plat.skytv.client.meet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import java.util.List;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 主页
 */
public class MainActivity extends BaseUIActivity {

    private static final int PERMISSION_REQ_CODE = 1000;
    private static final int PERMISSION_REQ_WINDOW_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPerssions();
    }

    private void requestPerssions() {

         if (!checkWindowPermissions()) {
            requestWindowPermissions(PERMISSION_REQ_WINDOW_CODE);
        }

        request(PERMISSION_REQ_CODE, new OnPermissionResult() {
            @Override
            public void OnSuccess() {
                LogUtils.i("requestPerssions OnSuccess");
            }

            @Override
            public void OnFail(List<String> mNotAllowPerList) {
                LogUtils.i("requestPerssions OnFail mNotAllowPerList = " + mNotAllowPerList.toString());
            }
        });
    }


}
