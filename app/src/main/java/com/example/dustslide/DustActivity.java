package com.example.dustslide;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DustActivity extends AppCompatActivity {
    double longitude; //경도
    double latitude;  //위도
    protected LocationManager lm;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled =false;
    boolean isPermission=false;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private static List<String> addr_geo = null;
    private static List<String> addr_name = null;
    private static List<Integer> dust_val;
    private static List<String> dust_state;

    private gpsInfo gps;
    JSONObject item=new JSONObject();

    private ViewPager mpager;

    class JSLocation{
        double x; double y;
    }
    JSLocation nowlocation= new JSLocation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust);
        mpager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter adapter = new pagerAdapter(getSupportFragmentManager());
        //위치정보
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        final Geocoder geocoder = new Geocoder(this);
        LocationListener locationListener_dust = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gps=new gpsInfo(DustActivity.this);
                if(gps.isGetLocation()){
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    //String tmp = getAddr(longitude,latitude);

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

       //////////////db주소값
 //       addr_name.add(//db에서 가져옴)
        try{//배열에 있는 수만큼 JSON 배열을 만들어 jobject를 만들고 값을 넣는다.
            JSONArray jArray=new JSONArray();
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

        for(int i=0;i<LocaList.size();i++){
            String trash = getAddr(LocaList.get(i).x,LocaList.get(i).y);
            if(i==0)
                addr_name.add("현재 위치");
            else{
                //addr_name.add(//db의 이름);
            }
        }
        //fragment 생성 // 함수로 바꾸자 - 새로 db 추가될때
        frag_now Nfrag = new frag_now();
        adapter.addItem(Nfrag);
        frag_fst Ffrag = new frag_fst();
        adapter.addItem(Ffrag);

        //첫페이지 설정

        mpager.setAdapter(adapter);
        mpager.setOffscreenPageLimit(0);//캐싱할 프래그먼트 수
//        mpager.setCurrentItem(0);
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
/*        if (mpager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mpager.setCurrentItem(mpager.getCurrentItem() - 1);
        }*/
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
    public void sendSer(JSONArray jsonArray){
        new Thread(new Runnable() {
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
        }).start();
    }
}
