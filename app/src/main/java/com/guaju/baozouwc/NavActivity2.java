package com.guaju.baozouwc;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-6-23.
 */

public class NavActivity2 extends BaseActivity {
    private List<NaviLatLng> sList;
    private List<NaviLatLng> eList;
    AMapNaviView mAMapNaviView;
    private String navitype;
    private LatLng mylocation;
    private LatLonPoint deslocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_layout);
        initIntentInfo();
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        setmAMapNavi(mAMapNaviView);
        //算路终点坐标
        NaviLatLng mEndLatlng = new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude());
        //算路起点坐标
        NaviLatLng mStartLatlng = new NaviLatLng(mylocation.latitude, mylocation.longitude);
        //存储算路起点的列表
        sList = new ArrayList<NaviLatLng>();
        //存储算路终点的列表
        eList = new ArrayList<NaviLatLng>();
        //获取 AMapNaviView 实例
        mAMapNaviView.setAMapNaviViewListener(this);


    }
    private void initIntentInfo() {
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getBundleExtra("bundle");
            mylocation = bundle.getParcelable("mylocation");
            deslocation = bundle.getParcelable("deslocation");
            navitype = bundle.getString("navitype");
        }
    }
    private void driveNav() {
        /**
         * 方法:
         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         * 说明:
         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         * 注意:
         *      不走高速与高速优先不能同时为true
         *      高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if ("自动".equals(navitype)){
            driveNav();
        }
        if ("驾车".equals(navitype)){
            driveNav();
        }
        if ("骑行".equals(navitype)){
            mAMapNavi.calculateRideRoute(new NaviLatLng(mylocation.latitude, mylocation.longitude), new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude()));
        }
        if ("步行".equals(navitype)){
            mAMapNavi.calculateWalkRoute(new NaviLatLng(mylocation.latitude, mylocation.longitude), new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude()));
        } }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        if ("自动".equals(navitype)){
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }
        if ("驾车".equals(navitype)){
            mAMapNavi.startNavi(NaviType.GPS);
        }
        if ("骑行".equals(navitype)){
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }
        if ("步行".equals(navitype)){
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }
    }
}
