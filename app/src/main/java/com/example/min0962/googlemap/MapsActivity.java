package com.example.min0962.googlemap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.min0962.googlemap.incheon.IncheonActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.min0962.googlemap.login.Constants.micro;

//import androidx.annotation.NonNull;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener
, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener{

    private GoogleMap mMap;

    LocationManager locationManager;
    double mLatitude;  //위도
    double mLongitude; //경도
    private GpsInfo gps;
    private GoogleApiClient mGoogleApiClient = null;


    TextView tv_mylocate;
    TextView textViewJson;
    TextView temp;
    private Marker previousMarker = null;

    Gson gson=new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //지오코딩 - 함수로연결햇음

        //디바이스 토큰 값 확인-----------
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("IDService","device token : "+token);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //----------------------------



        //-------------------------------------------
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("IDService","device token : "+token);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //-------------------------------------------

        tv_mylocate=(TextView) findViewById(R.id.tv_myLocate);
        textViewJson=(TextView) findViewById(R.id.textViewJson);


        temp=(TextView) findViewById(R.id.see_tv);

        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            //    temp.setVisibility(View.VISIBLE);


            }
        });

        Button button_inchoen =(Button) findViewById(R.id.incheon_bnt);
        button_inchoen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IncheonActivity.class);
                startActivity(intent);

            }
        });
        //Location Manager
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //마시멜로 이상이면 권한 요청하기
        if(Build.VERSION.SDK_INT >= 23){
            //권한이 없는 경우
            if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
            }
            //권한이 있는 경우
            else{
                requestMyLocation();
            }
        }
        //마시멜로 아래
        else{
            requestMyLocation();
        }

        gps = new GpsInfo(MapsActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
            mLatitude=latitude;
            mLongitude=longitude;
            String mylocate="위도"+mLatitude+"경도"+mLongitude;
            tv_mylocate.setText(mylocate);

        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert(); //gps사용허가할 수 있는 창을 만들어주고 허가하면 서비스한다
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                Toast.makeText(
                        getApplicationContext(),
                        "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                        Toast.LENGTH_LONG).show();
                mLatitude=latitude;
                mLongitude=longitude;
            }
        }

/*
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
*/
    }
    public void sendRequest(){
        // String url="http://www.google.co.kr";
        String url=getResources().getString(R.string.stations_url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        println("string request들어가기전 \n");


      //  StringRequest request2=new StringRequest(){}

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                      //  println("응답 ->"+response);
                     //   Log.d("Response response", response.toString());
                        println("응답 -> \n");
                        processResponse(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러"+error.getMessage());
                        Log.d("Network Fail ","reason "+error.toString());

                        //Null이면, 받아온값이 없다는것. url이 잘못된 경우가 많음
                        //또는 와이파이가 연결되지않은 경우
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }
                }
        ) {
            //만약 post방식으로 할려면, 이거 써야됌
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                return params;
            }
        };//응답을 문자열로 받아서 넣어달라


        //error.toString()으로 timeout되서 volley가 서버연동 못하는걸 알게됬음..하 ㅎ미들어 슈발
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(

                20000 ,

                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);//이전결과가 잇더라도 새로요청해서 받는다.
        requestQueue.add(request);
        println("요청 보냄");//요청보냇다는걸 먼저보내고, 응답을 받으면 Listener가 호출이되고 그때 응답처리해준다
    }
    public void processResponse(String response){

        Toast.makeText(
                getApplicationContext(),
                "processResponse입니다",
                Toast.LENGTH_LONG).show();


        //gson여기서 초기에 선언을 해주었었음Z
        //얘는 전체를 받아오는거고,
        StationListResult stationList = gson.fromJson(response,StationListResult.class); //Json으로 받아온걸 StationListResult 객체로 바꿔주라
        Toast.makeText(
                getApplicationContext(),
                "gson.fromJson 파싱은 했다",
                Toast.LENGTH_LONG).show();

        if(stationList!=null){
            int countStation = 0;
            countStation=stationList.location.size();
            println("전체 측정소의 개수는"+countStation+"\n");
        }

        //x,y값을 gpsList에 넣는 String이다.
        List<GpsLists> gpsArray=new ArrayList<GpsLists>();
        // 위에꺼 안되면 이걸로 해보자 ArrayList<GpsLists> a=new ArrayList<GpsLists>();

        MarkerOptions marker = new MarkerOptions();

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);






        for(int i=0; i<stationList.location.size();i++)
        {

            String pname=stationList.location.get(i).name;
            double px=stationList.location.get(i).x_location_info;
            double py=stationList.location.get(i).y_location_info;




            double pdust=stationList.location  .get(i).dust;
            String stringdust= Double.toString(pdust);

            LatLng tempStation = new LatLng(px , py); //측정소 위치값
            Toast.makeText(
                    getApplicationContext(),
                    "Micro는"+micro,
                    Toast.LENGTH_LONG).show();




            if(pname.equals("미세먼지 측정기기"))
            {
                marker.position(new LatLng(px, py)).title(pname).snippet(stringdust).alpha(0.5f).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
//                mMap.addMarker(new MarkerOptions().position(tempStation).title(pname).snippet(stringdust).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }

 //           else{
   //             if(pdust> micro) //미세먼지수치가 높은경우
     //               mMap.addMarker(new MarkerOptions().position(tempStation).title(pname).snippet(stringdust).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
       //         else //미세먼지수치가 낮은경
         //           mMap.addMarker(new MarkerOptions().position(tempStation).title(pname).snippet(stringdust).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


//            }


            gpsArray.add(new GpsLists(px,py));


        }
        String gps_results=gson.toJson(new GpsListClass(gpsArray));

    //    println("수집한 gps값들 : \n"+gps_results + "\n");



    }

    public void println(String data){
        textViewJson.append(data+"\n");
    }

    //다시 앱을 키면 다시 위치값을 받아오고 최신위치에 해당하는 미세먼지 데이터를 받아온다.
    @Override
    protected void onStart() {
        super.onStart();
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.46 ,128.00)));

        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
            mLatitude=latitude;
            mLongitude=longitude;
            String mylocate="위도"+mLatitude+"경도"+mLongitude;
           // tv_mylocate.setText(mylocate);



            //지오코딩
            final Geocoder geocoder = new Geocoder(this);
            List<Address> list = null;
            try {
                double d1 = mLatitude;
                double d2 = mLongitude;

                list = geocoder.getFromLocation(
                        d1, // 위도
                        d2, // 경도
                        10); // 얻어올 값의 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (list != null) {
                if (list.size()==0) {
                    tv_mylocate.setText("해당되는 주소 정보는 없습니다");
                } else {
                    tv_mylocate.setText(list.get(0).getAddressLine(0));
                }
            }


        }



    }


    //나의 위치 요청
    public void requestMyLocation(){
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //요청
   //     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    //    Toast.makeText(getApplicationContext(), "새로 받아온다", Toast.LENGTH_LONG).show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings setting=mMap.getUiSettings();
        setting.setMyLocationButtonEnabled(true);
        setting.setCompassEnabled(true);
        setting.setZoomControlsEnabled(true);
        setting.setMapToolbarEnabled(true);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        //권한을 먼저 확인해주어야 현재위치버튼을 활성화할 수 있다.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            setting.setMyLocationButtonEnabled(true);
        }
        else
            Toast.makeText(getApplicationContext(), "현재위치 접근권한이 없습니다.", Toast.LENGTH_LONG).show();



        //현재 나의 위치
        LatLng myPosition = new LatLng(mLatitude , mLongitude);
        mMap.addMarker(new MarkerOptions().position(myPosition).
                title("내 위치").snippet("My Position").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

        //정가운데 카메라뷰, 정가운데
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.46 ,128.00)));

        // 구글지도(지구) 에서의 zoom 레벨은 1~23 까지 가능합니다.
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        mMap.animateCamera(zoom); //지도 확대 축소 가능함
       // moveCamera 는 바로 변경하지만,
        // animateCamera() 는 근거리에선 부드럽게 변경합니다

        // marker 표시 - 관측소로 하면 될듯
        // market 의 위치, 타이틀, 짧은설명 추가 가능.

        //함수 안에서 onMarkerListener호출해
        createMarker();


    }
/*    private void addMarker(){ //마커 추가하는 함수
        mMap.addMarker()
    }
*/
    //GpS값이 바뀔때 사용하는 위치정보 구하기 리스너
    LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

        //일단 잠깐 해본다
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        //나의 위치를 한번만 가져오기 위해

    /*    if(gps.isGPSEnabled && gps.isNetworkEnabled){

            //시스템 위치마저 허용되어있을경우
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

        }

*/
        locationManager.removeUpdates(locationListener);

        mLatitude = location.getLatitude();   //위도
        mLongitude = location.getLongitude(); //경도
        Toast.makeText(getApplicationContext(), mLatitude + " " + mLongitude, Toast.LENGTH_LONG).show();



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("gps", "onStatusChanged"); }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }


};




    //정보창 클릭 리스너
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getApplicationContext(), "정보창을 클릭했군요", Toast.LENGTH_LONG).show();

    }

    public void createMarker(){

        //marker.tag를 나중에 미세먼지로 불러오고, 그걸 누르면 자세히 보여주는 식으로 할 수 있다.


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // 마커클릭 이벤트 처리
        // GoogleMap 에 마커클릭 이벤트 설정 가능.

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 마커 클릭시 호출되는 콜백 메서드
        Toast.makeText(getApplicationContext(),
                marker.getTitle() + marker.getPosition().latitude+", "+marker.getPosition().longitude+" 클릭했음"
                , Toast.LENGTH_SHORT).show();
      //  marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
      //  String locAddress = marker.getTitle();
       // fillTextViews(locAddress);
        if (previousMarker != null) {
            previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        previousMarker = marker;


        return false;
    }

    @Override
    public boolean hasWindowFocus() {
        return super.hasWindowFocus();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}
