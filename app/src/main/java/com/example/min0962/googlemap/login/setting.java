package com.example.min0962.googlemap.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import com.example.min0962.googlemap.R;
import com.example.min0962.googlemap.login.DbOpenHelper;

public class setting extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private String setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Button butN = (Button) findViewById(R.id.normal);
        final Button butB = (Button) findViewById(R.id.baby);
        final Button butC = (Button) findViewById(R.id.custom);
        final TextView text = (TextView) findViewById(R.id.explain);
        Button but = (Button) findViewById(R.id.next);
        butN.setFocusableInTouchMode(true);
        butB.setFocusableInTouchMode(true);
        butC.setFocusableInTouchMode(true);

        butN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setting = "1";
                text.setText("일반에 대한 설명");
                butN.requestFocus();
            }
        });
        butB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setting = "2";
                text.setText("노약자에 대한 설명");
                butB.requestFocus();

            }
        });
        butC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setting = "3";
                text.setText("커스텀에 대한 설명");
                butC.requestFocus();

            }
        });
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), address.class);
                startActivity(intent);

                //mDbOpenHelper.insertColumn(ID,PS,setting);
            }
        });

    }


}
