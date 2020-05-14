package com.liboshuai.framework.base;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Author:boshuai.li
 * Time:2020/5/14   14:59
 * Description: 又返回键的Act
 */
public class BaseBackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 将返回键显示出来
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
