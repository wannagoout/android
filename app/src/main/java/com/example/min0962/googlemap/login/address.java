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

import java.io.IOException;
import java.util.List;

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
                       /* tv.setVisibility(View.INVISIBLE);
                        Address addr = list.get(0);
                        double addr_x = addr.getLatitude();
                        double addr_y = addr.getLongitude();
                        mDbOpenHelper.insertColumn2(addr_x,addr_y);*/
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
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
}
