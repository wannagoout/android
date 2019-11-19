package com.example.min0962.googlemap.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.min0962.googlemap.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    EditText editTextID;
    EditText editTextPS;
    TextView textViewID;
    TextView textViewPS;
    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private String str;
    private String strid;
    private String strps;
    private String usrid;
    private String usrps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        editTextID = (EditText) findViewById(R.id.ID);
        editTextPS = (EditText) findViewById(R.id.Password);
        Button button = (Button) findViewById(R.id.Next);
        if(AutoLogin.getID(MainActivity.this).length() != 0 && AutoLogin.getPS(MainActivity.this).length() != 0)
        {
            Intent intent  = new Intent(getApplicationContext(), setting.class);
            str = AutoLogin.getID(MainActivity.this);
            editTextID.setText(str);
            str = AutoLogin.getPS(MainActivity.this);
            editTextPS.setText(str);
            startActivity(intent);
        }
        else
        {

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //String ID = editTextID.getText().toString().trim();
                //String PS = editTextPS.getText().toString().trim();
                //String micro = "80";
                //String chomicro = "35";
                //mDbOpenHelper.insertColumn(ID,PS,micro,chomicro);
                strid = editTextID.getText().toString();
                strps = editTextID.getText().toString();
                if(strid.length() == 0 && strps.length() == 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("아이디와 비밀번호를 입력해주세요.");
                    alert.show();
                }
                else if(strid.length() == 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("아이디를 입력해주세요.");
                    alert.show();
                }
                else if(strps.length() == 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("비밀번호를 입력해주세요.");
                    alert.show();
                }
                else if(strid.length() != 0 && strps.length() != 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("등록되었습니다.");
                    alert.show();
                    Constants.Token = FirebaseInstanceId.getInstance().getToken();
                    Log.d("Token value",Constants.Token);
                    Constants.ID = editTextID.getText().toString().trim();
                    Constants.PS = editTextPS.getText().toString().trim();
                    AutoLogin.setLogin(MainActivity.this,Constants.ID, Constants.PS);
                    Intent intent = new Intent(getApplicationContext(), setting.class); //넘어갈 페이지 정해주기!!
                    startActivity(intent);

                }
            }
        });
    }

}


