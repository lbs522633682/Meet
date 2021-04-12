package com.liboshuai.framework.manager;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.liboshuai.framework.entity.Consts;

/**
 * Author:boshuai.li
 * Time:2021/4/12
 * Description: 地图管理类
 */
public class MapManager {

    private static MapManager mInstnce;
    private GeocodeSearch geocodeSearch;
    private OnGeocodeListener onGeocodeListener;

    private MapManager() {
    }

    public static MapManager getInstance() {
        if (mInstnce == null) {
            synchronized (HttpManager.class) {
                if (mInstnce == null) {
                    mInstnce = new MapManager();
                }
            }
        }
        return mInstnce;
    }

    public void initMap(Context context) {
        geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);
    }

    private GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            // //解析result获取地址描述信息
            if (AMapException.CODE_AMAP_SUCCESS == i) {
                if (onGeocodeListener != null) {
                    onGeocodeListener.poi2address(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                }
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            // //解析result获取坐标信息
            if (AMapException.CODE_AMAP_SUCCESS == i) {
                if (onGeocodeListener != null) {
                    if (geocodeResult.getGeocodeAddressList() != null && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);

                        onGeocodeListener.address2poi(geocodeAddress.getLatLonPoint().getLatitude(),
                                geocodeAddress.getLatLonPoint().getLongitude(),
                                geocodeAddress.getFormatAddress());
                    }
                }
            }
        }
    };

    /**
     * 经纬度 转换 物理地址
     *
     * @param la
     * @param lo
     */
    public MapManager poi2address(double la, double lo) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        LatLonPoint latLonPoint = new LatLonPoint(la, lo);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);
        return mInstnce;
    }

    /**
     * 通过地名查询经纬度
     *
     * @param address
     * @return
     */
    public MapManager address2poi(String address) {

        // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
        GeocodeQuery query = new GeocodeQuery(address, "");

        geocodeSearch.getFromLocationNameAsyn(query);
        return mInstnce;
    }

    /**
     * 获取地图缩略图的url
     * @param la
     * @param lo
     * @return
     */
    public String getMapUrl(double la, double lo) {
        return "https://restapi.amap.com/v3/staticmap?location=" + lo + "," + la + "&zoom=10&size=750*300&markers=mid,,A:" + lo + "," + la + "&key="
                + "f6906f8648ca5ba71836464a0b02374b";
    }

    public void setOnGeocodeListener(OnGeocodeListener onGeocodeListener) {
        this.onGeocodeListener = onGeocodeListener;
    }

    public interface OnGeocodeListener {
        void poi2address(String address);

        void address2poi(double la, double lo, String address);
    }
}
