package plat.skytv.client.meet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.utils.ToastUtil;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 主页
 */
public class MainActivity extends BaseUIActivity {

    private static final int PERMISSION_REQ_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermission(Manifest.permission.READ_PHONE_STATE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showTextToast(this, "请求成功");
            } else {
                ToastUtil.showTextToast(this, "请求失败");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
