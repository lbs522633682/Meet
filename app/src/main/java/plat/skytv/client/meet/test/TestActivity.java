package plat.skytv.client.meet.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liboshuai.framework.bmob.MyData;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.HttpManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.view.TouchPictureV;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import plat.skytv.client.meet.R;

public class TestActivity extends Activity implements View.OnClickListener {

    private TouchPictureV mTouchPictureV;
    private Button btn_add;
    private Button btn_delete;
    private Button btn_modify;
    private Button btn_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        initView();
    }

    private void initView() {
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_query = (Button) findViewById(R.id.btn_query);

        btn_add.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_modify.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }


    /**
     * Get 请求测试
     */
    public void testOKHttpManager() {

        // 需要在onDestroy 时调用 disposable.dispose();
        Disposable disposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            // 获取服务端 响应
            String resp = HttpManager.getInstance().startRequest("", TestActivity.this, null);
            emitter.onNext(resp);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(s -> parsingpResp(s));

    }

    /**
     * 解析 服务端返回的数据
     * @param respStr
     */
    public void parsingpResp(String respStr) {

    }

    /**
     * POST 请求测试
     */
    public void testOKHttpManager2() {

        // 需要在onDestroy 时调用 disposable.dispose();
        Disposable disposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            HashMap<String, String> map = new HashMap<>();
            // String 类型
            map.put("key1", "value1");
            // int类型
            int value2 = 3;
            map.put("key2", value2 + "");

            // 获取服务端 响应
            String resp = HttpManager.getInstance().startRequest("", TestActivity.this, map);
            emitter.onNext(resp);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(s -> parsingpResp2(s));

    }

    /**
     * 解析 服务端返回的数据
     * @param respStr
     */
    public void parsingpResp2(String respStr) {

    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                MyData myData = new MyData();
                myData.setName("老王");
                myData.setSex(1);
                myData.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            // f212395f08  14c76a3441
                            LogUtils.i("ADD SUCCESS");
                        }
                    }
                });
                break;
            case R.id.btn_delete:
                MyData laowang = new MyData();
                laowang.setObjectId("f212395f08");
                laowang.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtils.i("DELETE SUCCESS");
                        }
                    }
                });
                break;
            case R.id.btn_modify:
                // 不仅修改了名字字段，还将性别字段修改了
                MyData myData1 = new MyData();
                myData1.setName("oldKing");
                myData1.update("14c76a3441", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtils.i("Update Success");
                        }
                    }
                });

                break;
            case R.id.btn_query:
                BmobQuery<MyData> objectBmobQuery = new BmobQuery<>();
                // 2.条件查询
                objectBmobQuery.addWhereEqualTo("name", "oldKing");
                objectBmobQuery.findObjects(new FindListener<MyData>() {
                    @Override
                    public void done(List<MyData> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                for (MyData data : list) {
                                    LogUtils.i("findObjects name = " + data.getName() + ", sex = " + data.getSex());
                                }
                            }
                        } else {
                            LogUtils.e(e.toString());
                        }
                    }
                });
                // 1.通过id查询
                /*objectBmobQuery.getObject("14c76a3441", new QueryListener<MyData>() {
                    @Override
                    public void done(MyData myData, BmobException e) {
                        if (e == null) {
                            LogUtils.i("BmobQuery name = " + myData.getName() + ", sex = " + myData.getSex());
                        } else {
                            LogUtils.e(e.toString());
                        }
                    }
                });*/
                break;
        }
    }
}
