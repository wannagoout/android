package com.example.min0962.googlemap.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
        final TextView Cs = (TextView) findViewById(R.id.Custom);
        Button but = (Button) findViewById(R.id.next);
        butN.setFocusableInTouchMode(true);
        butB.setFocusableInTouchMode(true);
        butC.setFocusableInTouchMode(true);
        Constants.setting = "0";

        butN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Constants.setting = "80";
                text.setText("일반에 대한 설명");
                butN.requestFocus();
                Cs.setVisibility(View.INVISIBLE);
            }
        });
        butB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Constants.setting = "50";
                text.setText("노약자에 대한 설명");
                butB.requestFocus();
                Cs.setVisibility(View.INVISIBLE);

            }
        });
        butC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                text.setText("숫자를 입력해주세요");
                butC.requestFocus();
                Constants.setting = "1";
                Cs.setVisibility(View.VISIBLE);

            }
        });
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Constants.setting == "0")
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(setting.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("하나 이상의 버튼을 선택해주세요.");
                    alert.show();
                }
                else
                {
                    String str;
                    str = Cs.getText().toString();
                    if(Constants.setting == "1") {
                        if (str.length() == 0) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(setting.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage("값을 입력해주세요.");
                            alert.show();

                        } else {
                            Constants.setting = Cs.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), address.class);
                            startActivity(intent);
                        }

                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), address.class);
                        startActivity(intent);
                    }

                }

            }
        });

    }


}