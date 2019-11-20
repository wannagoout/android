package com.example.min0962.googlemap.dust;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.min0962.googlemap.GpsInfo;
import com.example.min0962.googlemap.MapsActivity;
import com.example.min0962.googlemap.R;
import com.example.min0962.googlemap.login.Constants;
import com.example.min0962.googlemap.login.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DustActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    double longitude; //경도
    double latitude;  //위도
    protected LocationManager lm;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled =false;
    boolean isPermission=false;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private static ArrayList<String> addr_geo = new ArrayList<>();
    private static ArrayList<String> addr_name = new ArrayList<>();
    private static List<Double> dust_val;
    private static ArrayList<String> dust_state;
    public ArrayList<JSLocation> LocaList = new ArrayList<>();
    private GpsInfo gps;
    JSONObject item=new JSONObject();
    Gson gson=new Gson();

    private ViewPager mpager;

    JSLocation nowlocation= new JSLocation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust);
        //navigationview setting
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //pager set
        mpager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter adapter = new pagerAdapter(getSupportFragmentManager());
        //위치정보
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        frag_now Nfrag = new frag_now();
        adapter.addItem(Nfrag);

        //location get
        final Geocoder geocoder = new Geocoder(this);
        LocationListener locationListener_dust = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gps=new GpsInfo(DustActivity.this);
                if (gps.isGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    nowlocation.x=latitude;
                    nowlocation.y=longitude;

                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert(); //gps사용허가할 수 있는 창을 만들어주고 허가하면 서비스한다
                    if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        Toast.makeText(
                                getApplicationContext(),
                                "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                                Toast.LENGTH_LONG).show();
                        nowlocation.x=latitude;
                        nowlocation.y=longitude;
                    }
                }

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        LocaList.add(nowlocation);
//        shared에서 x, y 값 가져와서 지오코딩 및 JSON 변환
        //x,y 를 갖는 LocaList에 추가하고 db수만큼 추가 최대3




        JSONArray jArray=new JSONArray();
        //////////////db주소값
        //       addr_name.add(//db에서 가져옴)
        try{//배열에 있는 수만큼 JSON 배열을 만들어 jobject를 만들고 값을 넣는다.

            for(int i=0;i<LocaList.size();i++){
                JSONObject obj = new JSONObject();
                obj.put("x",LocaList.get(i).x);
                obj.put("y",LocaList.get(i).y);
                jArray.put(obj);
                //  String trash = getAddr(LocaList.get(i).x,LocaList.get(i).y);
//                Log.d()
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }
        //서버와 통신
        sendget(jArray);

        for(int i=0;i<LocaList.size();i++){
            String trash = getAddr(LocaList.get(i).x,LocaList.get(i).y);
            if(i==0) {
                addr_name.add("현재 위치");
            } else{
                //addr_name.add(//db의 이름);
            }
        }

        //fragment 생성 // 함수로 바꾸자 - 새로 db 추가될때
/*        frag_now Nfrag = new frag_now();
        adapter.addItem(Nfrag);*/
        frag_fst Ffrag = new frag_fst();
        adapter.addItem(Ffrag);

        //첫페이지 설정
        /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new NowFrag()).commit();*/
        navigationView.setCheckedItem(R.id.dust_now);
        mpager.setAdapter(adapter);
        mpager.setOffscreenPageLimit(0);//캐싱할 프래그먼트 수
        mpager.setCurrentItem(0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isGPSEnabled = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isNetworkEnabled = true;
        }
/*
        if (isNetworkEnabled&&isGPSEnabled) {
            isPermission = true;
        }*/
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavigationView nv= findViewById(R.id.nav_view);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dust_map) {
            Intent intent=new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
            nv.setCheckedItem(R.id.dust_now);
        } else if (id == R.id.dust_now) {
            /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NowFrag()).commit();*/
        } else if (id == R.id.dust_set) {
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            nv.setCheckedItem(R.id.dust_now);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //페이지 전환
    private class pagerAdapter extends FragmentStatePagerAdapter{
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public pagerAdapter(FragmentManager fm){
            super(fm);
        }
        public void addItem(Fragment item){
            items.add(item);
        }

        @Override
        public Fragment getItem(int i) {
            return items.get(i);
/*            switch (i){
                case 0:
                    frag_now frnow = new frag_now();
                    return frnow;
                case 1:
                    frag_fst fst = new frag_fst();
  //                  fst.getData("nownowtest",addr_geo.get(0),58);
                    return new frag_fst();
                case 2:
                    return new frag_scnd();
                case 3:
                    return new frag_thrd();

                default :
                    return null;
            }*/
        }
        @Override
        public int getCount(){
            return items.size();
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public String getAddr( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geoco = new Geocoder(this, Locale.getDefault());
        List<Address> addr_lists;
        try {
            // address=geoco.getFromLocation(latitude,longitude,1);
            addr_lists = geoco.getFromLocation(  latitude,longitude,1);

        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addr_lists == null || addr_lists.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        String tmp=addr_lists.get(0).getAddressLine(0);
        addr_geo.add(tmp);
/*        StringBuilder sb = new StringBuilder();
//        sb.append(addr_lists.get(0).getAdminArea()+" ");
        sb.append(addr_lists.get(0).getLocality() + " ");
        sb.append(addr_lists.get(0).getThoroughfare());*/
        return tmp;
    }

    public void sendget (JSONArray jsonArray){
        RequestQueue postReqeustQueue = Volley.newRequestQueue(this);
        StringRequest postStringRequest = new StringRequest(Request.Method.POST, "http://ec2-52-78-37-78.ap-northeast-2.compute.amazonaws.com/wannaGoOut/api/dust",
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        dataparsing(response);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "값을 받아 올 수 없습니다.", Toast.LENGTH_LONG).show();
                Log.d("server error","["+error.getMessage()+"]");
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                for(int i=0;i<LocaList.size();i++) {
                    params.put("x", String.valueOf(LocaList.get(i).x));
                    params.put("y", String.valueOf(LocaList.get(i).y));
                    String sendto=gson.toJson(LocaList);
                }
                return params;
            }
        };
        postReqeustQueue.add(postStringRequest);
    }
    public void dataparsing(String str){
        DustRes dustRes = gson.fromJson(str,DustRes.class); //Json으로 받아온걸 StationListResult 객체로 바꿔주라

        int cnt=dustRes.reslist.size();

        //x,y값을 gpsList에 넣는 String이다.
        List<JSLocation> gpsArray=new ArrayList<JSLocation>();
        // 위에꺼 안되면 이걸로 해보자 ArrayList<GpsLists> a=new ArrayList<GpsLists>();
        double micro_num = Double.parseDouble(Constants.setting);


        for(int i=0; i<cnt;i++)
        {

            String pname=dustRes.reslist.get(i).name;
            double px=dustRes.reslist.get(i).x_location_info;
            double py=dustRes.reslist.get(i).y_location_info;

            double pdust=dustRes.reslist.get(i).dust;
            String stringdust= Double.toString(pdust);

            LatLng tempStation = new LatLng(px , py); //측정소 위치값
            dust_val.add(pdust);

        }
        //String gps_results=gson.toJson(new GpsListClass(gpsArray));
    }

}
