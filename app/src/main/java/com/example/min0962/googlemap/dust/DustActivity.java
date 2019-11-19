package com.example.min0962.googlemap.dust;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.drawerlayout.widget.*;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.min0962.googlemap.GpsInfo;
import com.example.min0962.googlemap.R;

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
    private static List<Integer> dust_val;
    private static List<String> dust_state;

    private GpsInfo gps;
    JSONObject item=new JSONObject();

    private ViewPager mpager;

    class JSLocation{
        double x; double y;
    }//x, y값 저장할 객체
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
                if(gps.isGetLocation()){
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    String tmp = getAddr(longitude,latitude);
                    TextView tv=(TextView)findViewById(R.id.Addr_now);
                    tv.setText(tmp);
                    nowlocation.x=latitude;
                    nowlocation.y=longitude;
//DB랑 합쳐서 JSON 형태로 보내기
                }else{
                    gps.showSettingsAlert();
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

//        DB에서 x, y 값 가져와서 지오코딩 및 JSON 변환
        //x,y 를 갖는 LocaList에 추가하고 db수만큼 추가 최대3


        ArrayList<JSLocation> LocaList = new ArrayList<>();
        LocaList.add(nowlocation);
        JSLocation db1 = new JSLocation();
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
            /*Intent intent=new Intent(getApplicationContext(), MyActivity.class);
            startActivity(intent);
            nv.setCheckedItem(R.id.dust_now);*/
        } else if (id == R.id.dust_now) {
            /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NowFrag()).commit();*/
        } else if (id == R.id.dust_set) {
            /*Intent intent=new Intent(getApplicationContext(), MyActivity.class);
            startActivity(intent);
            nv.setCheckedItem(R.id.dust_now);*/
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
        String tmp=addr_lists.get(0).getLocality() + " " + addr_lists.get(0).getThoroughfare();
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

                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", "Jay");
                params.put("password", "1234");
                return params;
            }
        };
        postReqeustQueue.add(postStringRequest);
    }
    public void sendSer(JSONArray jsonArray){
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiURL = "http://ec2-52-78-37-78.ap-northeast-2.compute.amazonaws.com/wannaGoOut/api/dust";
                    URL url = new URL(apiURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    BufferedReader br = null;
                    String json=null;
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    //받아옴
                    String inputdata;
                    StringBuffer respon=new StringBuffer();
                    while((inputdata = br.readLine())!=null){
                        respon.append(inputdata);
                    }
                    br.close();
                    json = respon.toString();

                    if(json==null)
                        return;
                    JSONObject addobj = new JSONObject(json);
                    JSONArray dataArray = addobj.getJSONArray("gpsList");
                    //대괄호 분리
                    JSONObject area1Object = (JSONObject) addobj.get("x");
                    JSONObject area2Object = (JSONObject) addobj.get("y");

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();*/
    }
}
