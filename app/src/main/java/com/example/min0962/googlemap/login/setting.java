package com.example.min0962.googlemap.login;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.min0962.googlemap.R;

public class setting extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private String setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Button butN = (Button) findViewById(R.id.normal);
        Button butB = (Button) findViewById(R.id.baby);
        Button butC = (Button) findViewById(R.id.custom);
        Button but = (Button) findViewById(R.id.next);

        butN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), normal.class);
                startActivity(intent);
                setting = "1";

            }
        });
        butB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), baby.class);
                startActivity(intent);
                setting = "2";

            }
        });
        butC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), custom.class);
                startActivity(intent);
                setting = "3";

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
