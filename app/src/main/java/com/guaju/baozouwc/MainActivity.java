package com.guaju.baozouwc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements AMap.InfoWindowAdapter,GeocodeSearch.OnGeocodeSearchListener {
    private static final String TAG = "MainActivity";
    MapView mMapView = null;
    private AMap mAmap;
    private ActionBar actionBar;
    private LatLng latLng;
    private LatLng mylatlng;
    public static final int SHOW_LOCATION = 0;
    private String lastKnowLoc;
    private GeocodeSearch geocoderSearch;
    private String currentPosition;
    private ListView lv;
    private ArrayList<String> list;
    private View v;
    private WindowManager wm;
    private Marker currentMarker;
    private AlertDialog alertDialog;
    private boolean value=true;
    private Intent intent;
    private LatLonPoint latLonPoint;
    boolean innerNavDialogFlag=true;
    private AlertDialog innerNavDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = View.inflate(this, R.layout.navlist, null);
        lv = (ListView) v.findViewById(R.id.ls);

        actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);/

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        mAmap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker!=null&&currentMarker.isInfoWindowShown()){
                    if (alertDialog!=null&&alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    currentMarker.hideInfoWindow();
                }
            }
            private boolean value=true;
        });
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoBottomMargin(-300);


        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.dog);
        latLng = new LatLng(40.03083, 116.446813, true);
        markerOptions.icon(bitmapDescriptor)
                .alpha(0.7f)
                .position(latLng)
                .visible(true)
                .title("red star")
                .snippet("高德地图设置覆盖物 marker样式坐标");
        mAmap.addMarker(markerOptions);
        mAmap.setInfoWindowAdapter(this);
        mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker=marker;
                return false;
            }
        });
        mAmap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                initLv(value);
                if (alertDialog!=null){
                alertDialog.show();
                }
                if (innerNavDialog!=null){
                innerNavDialog.show();
                }
            }
        });
    }

    private void initLv(boolean value) {

        if (!value){
            return;
        }
        if (value){
            this.value=false;
        }
        boolean installqq = isInstallByread("com.tencent.map");
        boolean installnav = isInstallByread("com.autonavi.minimap");
        boolean installbaidu = isInstallByread("com.baidu.BaiduMap");
        list = new ArrayList<String>();
        if (installqq) {
            list.add("腾讯地图");
        } if (installbaidu) {
            list.add("百度地图");
        } if (installnav) {
            list.add("高德地图");
        } else {
            intent = new Intent(MainActivity.this, NavActivity.class);
            initInnerNavDialog(innerNavDialogFlag);
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).equals("腾讯地图")) {
                    //腾讯地图
                    if (mylatlng != null && latLng != null) {
                        // 腾讯地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=" +"&fromcoord=" + mylatlng.latitude + "," + mylatlng.longitude + "&to=" + null + "&tocoord=" + latLng.latitude + "," + latLng.longitude + "&policy=0&referer=appName"));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("百度地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 百度地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + latLng.latitude + "," + latLng.longitude));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("高德地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 高德地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=目的地&dev=0&t=2"));
                        startActivity(naviIntent);
                    }
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(lv);
        alertDialog = builder.create();

    }

    private void initInnerNavDialog(boolean b) {
        if(!b){
            return;
        }
        if (b){
            b=false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final String[] navtypes={"自动","驾车","骑行","步行"};
        builder.setItems(navtypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("mylocation",mylatlng);
                bundle.putParcelable("deslocation",latLonPoint);
                bundle.putString("navitype",navtypes[which]);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        innerNavDialog = builder.create();



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
        getMenuInflater().inflate(R.menu.actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.location:
                MyLocationStyle myLocationStyle;
                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
//                myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                myLocationStyle.showMyLocation(true);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.dog);
                myLocationStyle.myLocationIcon(bitmapDescriptor);
//                myLocationStyle.anchor(0.0f,0.0f);
               // myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
//                myLocationStyle.
                mAmap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                //设置默认定位按钮是否显示，非必需设置。
                mAmap.getUiSettings().setMyLocationButtonEnabled(true);
                mAmap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                mAmap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        mylatlng = new LatLng(latitude, longitude);
                    }
                });
//                Location myLocation = mAmap.getMyLocation();
//                double latitude = myLocation.getLatitude();
//                double longitude = myLocation.getLongitude()  ;
//                mylatlng = new LatLng(latitude, longitude);

                break;
            case R.id.in_door_info:
                //show in door info
                mAmap.showBuildings(true);
                mAmap.showIndoorMap(true);
                 break;
            case R.id.night:
                mAmap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
            case R.id.nav:
                mAmap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
            case R.id.normal:
                mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satellate:
                mAmap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.bus:
                mAmap.setMapType(AMap.MAP_TYPE_BUS);
                break;
            case R.id.getlatlng:
                //构造 GeocodeSearch 对象，并设置监听。
                GeocodeSearch   geocodeSearch = new GeocodeSearch(this);
                geocodeSearch.setOnGeocodeSearchListener(this);
            //通过GeocodeQuery设置查询参数,调用getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
            //address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
                GeocodeQuery query = new GeocodeQuery("南口公园", "010");
                geocodeSearch.getFromLocationNameAsyn(query);


                break;
        }


        return super.onOptionsItemSelected(item);
    }



    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }



    @Override
    public View getInfoWindow(Marker marker) {
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.info_window, null, false);
//        View.inflate(MainActivity.this,R.layout.info_window,null);
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
        Toast.makeText(this, latLonPoint.getLatitude()+"---"+ latLonPoint.getLongitude(), Toast.LENGTH_SHORT).show();
    }

}
