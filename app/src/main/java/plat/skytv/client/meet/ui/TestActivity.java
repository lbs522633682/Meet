package plat.skytv.client.meet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.liboshuai.framework.bmob.MyData;
import com.liboshuai.framework.utils.LogUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/3/23   10:43
 * Description: 测试方法的Act
 */
public class TestActivity extends Activity implements View.OnClickListener {

    private Button btn_add;
    private Button btn_delete;
    private Button btn_update;
    private Button btn_query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_query = (Button) findViewById(R.id.btn_query);

        btn_add.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                MyData data = new MyData();
                data.setName("张三");
                data.setSex(1);
                data.save(new SaveListener<String>() {
                    @Override
                    public void done(String objId, BmobException e) {
                        if (e == null) { // 26898be5fd
                            LogUtils.i("ADD SUCCESS, objId= " + objId);
                        }
                    }
                });
                break;
            case R.id.btn_delete:
                MyData data1 = new MyData();
                data1.delete("64abd3a8d6", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtils.i("delete success");
                        }
                    }
                });
                break;
            case R.id.btn_update:
                // Tips:更新名字的时候，会将sex一同更新
                MyData data2 = new MyData();
                data2.setName("lisi");
                data2.update("26898be5fd", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtils.i("update success");
                        }
                    }
                });
                break;
            case R.id.btn_query:
                // 3.全部查询
                BmobQuery<MyData> categoryBmobQuery = new BmobQuery<>();
                categoryBmobQuery.findObjects(new FindListener<MyData>() {
                    @Override
                    public void done(List<MyData> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                LogUtils.i("findObjects size = " + list.size());
                            }
                        } else {
                            Log.e("BMOB", e.toString());
                        }
                    }
                });

                // 2.条件查询
                /*BmobQuery<MyData> categoryBmobQuery = new BmobQuery<>();
                categoryBmobQuery.addWhereEqualTo("name", "张三");
                categoryBmobQuery.findObjects(new FindListener<MyData>() {
                    @Override
                    public void done(List<MyData> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                LogUtils.i("findObjects size = " + list.size());
                            }
                        } else {
                            Log.e("BMOB", e.toString());
                        }
                    }
                });*/
                // 1.根据唯一标识查询
                /*BmobQuery<MyData> bmobQuery = new BmobQuery<MyData>();
                bmobQuery.getObject("26898be5fd", new QueryListener<MyData>() {
                    @Override
                    public void done(MyData myData, BmobException e) {
                        if (e == null) {
                            LogUtils.i("result = " + myData.toString());
                        }
                    }
                });*/
                break;
        }
    }
}
