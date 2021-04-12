package plat.skytv.client.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.manager.MapManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;
import com.liboshuai.framework.view.DialogView;
import com.liboshuai.framework.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2021/4/12
 * Description: 定位的UI界面
 */
public class LocationActivity extends BaseBackActivity implements View.OnClickListener, PoiSearch.OnPoiSearchListener {

    /**
     * @param activity
     * @param isShow
     * @param la
     * @param lo
     * @param address
     * @param resultCode
     */
    public static void startActivity(Activity activity, boolean isShow, double la, double lo, String address, int resultCode) {
        Intent intent = new Intent(activity, LocationActivity.class);
        intent.putExtra(Consts.INTENT_MENU_SHOW, isShow);
        intent.putExtra("la", la);
        intent.putExtra("lo", lo);
        intent.putExtra("address", address);
        activity.startActivityForResult(intent, resultCode);
    }

    private EditText et_search;
    private ImageView iv_poi;
    private MapView mMapView;

    //初始化地图控制器对象
    private AMap aMap;

    private boolean isShow; // 控制是否显示菜单

    private DialogView mPoiView;
    private RecyclerView mConstellationnView;

    private CommonAdapter<PoiItem> mPoiAdapter;
    private List<PoiItem> mList = new ArrayList<>();
    private LoadingView loadingView;

    private double mLa;
    private double mLo;
    private String mAddress;

    private int mItemPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        initPoiView();
        initView(savedInstanceState);
    }

    private void initPoiView() {
        mPoiView = DialogManager.getInstance().initView(this, R.layout.dialog_select_constellation, Gravity.BOTTOM);
        mPoiView.setCancelable(false);
        mConstellationnView = mPoiView.findViewById(R.id.mConstellationnView);
        TextView tv_cancel = mPoiView.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().hide(mPoiView);
            }
        });

        mConstellationnView.setLayoutManager(new LinearLayoutManager(this));
        mConstellationnView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mPoiAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<PoiItem>() {
            @Override
            public void onBindViewHolder(PoiItem model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_age_text, model.toString());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogManager.getInstance().hide(mPoiView);

                        mItemPos = position;

                        /**
                         * 已知条件：
                         * 1. 建筑名称，需要转换成经纬度
                         */
                        MapManager.getInstance().address2poi(model.toString()).setOnGeocodeListener(new MapManager.OnGeocodeListener() {
                            @Override
                            public void poi2address(String address) {

                            }

                            @Override
                            public void address2poi(double la, double lo, String address) {
                                LogUtils.i("address2poi la = " + la + ", lo = " + lo + ", address = " + address);

                                mLa = la;
                                mLo = lo;
                                mAddress = address;
                                updatePoi(la, lo, address);
                            }
                        });
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_me_age_item;
            }
        });
        mConstellationnView.setAdapter(mPoiAdapter);
    }

    private void initView(Bundle savedInstanceState) {

        loadingView = new LoadingView(this);

        loadingView.setLoadingText("正在搜索...");

        et_search = findViewById(R.id.et_search);
        iv_poi = findViewById(R.id.iv_poi);
        mMapView = findViewById(R.id.mMapView);

        mMapView.onCreate(savedInstanceState);

        iv_poi.setOnClickListener(this);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        // 缩放
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        isShow = getIntent().getBooleanExtra(Consts.INTENT_MENU_SHOW, false);

        if (!isShow) { // 如果不展示，则作为展示类地图
            mLa = getIntent().getDoubleExtra("la", 0);
            mLo = getIntent().getDoubleExtra("lo", 0);
            mAddress = getIntent().getStringExtra("address");


            updatePoi(mLa, mLo, mAddress);
        }

        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LogUtils.i("onMyLocationChange " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getExtras().get("desc"));
            }
        });
    }

    /**
     * 更新位置
     *
     * @param la
     * @param lo
     * @param address
     */
    private void updatePoi(double la, double lo, String address) {
        LogUtils.i("updatePoi enter la = " + la + ", lo = " + ", address = " + address);

        aMap.setMyLocationEnabled(true);

        supportInvalidateOptionsMenu();

        // 展示位置
        LatLng latLng = new LatLng(la, lo);
        aMap.clear();

        aMap.addMarker(new MarkerOptions().position(latLng).title("位置").snippet(address));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_poi:
                String searchKey = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(searchKey)) {
                    return;
                }
                poiSearch(searchKey);
                break;
        }
    }

    /**
     * @param keyWords 搜索关键字
     */
    private void poiSearch(String keyWords) {
        PoiSearch.Query query = new PoiSearch.Query(keyWords, "");
        PoiSearch poiSearch = new PoiSearch(this, query);

        poiSearch.setOnPoiSearchListener(this);
        loadingView.show();
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isShow) {
            getMenuInflater().inflate(R.menu.location_menu, menu);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtils.i("onOptionsItemSelected enter");
        if (item.getItemId() == R.id.menu_send) {
            Intent intent = new Intent();
            LogUtils.i("onOptionsItemSelected mItemPos = " + mItemPos + ", la = " + mLa + ", mlo = " + mLo + ", maddress = " + mAddress);
            if (mItemPos == -1) {
                intent.putExtra("la", aMap.getMyLocation().getLatitude());
                intent.putExtra("lo", aMap.getMyLocation().getLongitude());
                Object desc = aMap.getMyLocation().getExtras().get("desc");
                if (desc != null) {
                    intent.putExtra("address", desc.toString());
                }
            } else {
                intent.putExtra("la", mLa);
                intent.putExtra("lo", mLo);
                intent.putExtra("address", mAddress);
            }

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        // 通过回调接口 onPoiSearched 解析返回的结果，将查询到的 POI 以绘制点的方式显示在地图上
        LogUtils.i("onPoiSearched poiResult = " + poiResult);
        loadingView.hide();

        if (mList.size() > 0) {
            mList.clear();
        }

        if (poiResult.getPois().size() > 0) {
            mList.addAll(poiResult.getPois());
            mPoiAdapter.notifyDataSetChanged();
            DialogManager.getInstance().show(mPoiView);
        } else {
            ToastUtil.showTextToast(this, "未搜寻到结果");
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        // //获取PoiItem获取POI的详细信息
        loadingView.hide();
    }
}
