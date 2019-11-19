package com.example.min0962.googlemap.login;

import android.content.Intent;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.min0962.googlemap.MapsActivity;
import com.example.min0962.googlemap.R;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;


public class address extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Button bt = (Button)findViewById(R.id.button);
        Button bt1 = (Button)findViewById(R.id.button2);
        final TextView tv = (TextView) findViewById(R.id.result);
        final Geocoder geocoder = new Geocoder(this);
        final EditText readAdd = (EditText)findViewById(R.id.ReadAdd);
        mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                String str = readAdd.getText().toString();
                try {
                    list = geocoder.getFromLocationName(str, 30);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0)
                    {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("주소를 입력해주세요");
                    } else {
                        tv.setVisibility(View.INVISIBLE);
                        Address addr = list.get(0);
                        Constants.addr_x = addr.getLatitude();
                        Constants.addr_y = addr.getLongitude();
                        mDbOpenHelper.insertColumn2(Constants.addr_x,Constants.addr_y);
                        mDbOpenHelper.insertColumn(Constants.ID,Constants.PS,Constants.micro, Constants.chomicro);
                        //select_doProcess();
                        String temp = "{\"usrId\""+":"+"\""+Constants.ID+"\""+","+"\"usrPs\""+":"+"\""+Constants.PS+"\""+","+
                                "\"usrToken\""+":"+"\""+Constants.Token+"\""+","+"\"usrSetting\""+":"+"\""+Constants.setting+"\""+","+
                                "\"usrAddrX\""+":"+"\""+Constants.addr_x+"\""+","+"\"usrAddrY\""+":"+"\""+Constants.addr_y+"\""+"}";
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class); //넘어갈 페이지 입력
                        startActivity(intent);
                    }
                }
            }
        });

        bt1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                List<Address> list = null;
                String str = readAdd.getText().toString();
                try{
                    list = geocoder.getFromLocationName(str,30);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    Log.e("test","입출력 오류");
                }
                if(list != null)
                {
                    if(list.size() == 0)
                    {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("주소를 입력해주세요");
                    }
                    else
                    {
                        tv.setVisibility(View.INVISIBLE);
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();
                        String sss = String.format("geo:%f,%f", lat, lon);

                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(sss));
                        startActivity(intent);
                    }
                }
            }


        });
    }
    private void select_doProcess() //서버로 보내는 부분
    {
        HttpClient client = new DefaultHttpClient();
        HttpPost post =new HttpPost("http://ec2-52-78-37-78.ap-northeast-2.compute.amazonaws.com/wannaGoOut/api/user/add");
        ArrayList <NameValuePair> SendData = new ArrayList<NameValuePair>();
        try {
            SendData.add(new BasicNameValuePair("usrId", URLDecoder.decode(Constants.ID, "UTF-8")));
            SendData.add(new BasicNameValuePair("usrPs", URLDecoder.decode(Constants.PS, "UTF-8")));
            SendData.add(new BasicNameValuePair("usrToken", URLDecoder.decode(Constants.Token, "UTF-8")));
            SendData.add(new BasicNameValuePair("usrSetting", URLDecoder.decode(Constants.setting, "UTF-8")));
            SendData.add(new BasicNameValuePair("usrAddrX", URLDecoder.decode("\""+Constants.addr_x+"\"", "UTF-8")));
            SendData.add(new BasicNameValuePair("usrAddrY", URLDecoder.decode("\""+Constants.addr_y+"\"", "UTF-8")));
            post.setEntity(
                    new UrlEncodedFormEntity(
                            SendData, "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            Log.e("Insert log", ex.toString());
        }
        try {
            HttpResponse response = client.execute(post);
            Log.i("insert log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
