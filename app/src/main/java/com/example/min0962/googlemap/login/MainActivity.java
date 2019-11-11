package com.example.min0962.googlemap.login;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.min0962.googlemap.R;

public class MainActivity extends AppCompatActivity {

    EditText editTextID;
    EditText editTextPS;
    TextView textViewID;
    TextView textViewPS;
    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;



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
        textViewID = (TextView) findViewById(R.id.textViewid);
        textViewPS = (TextView) findViewById(R.id.textViewps);
        Button button = (Button) findViewById(R.id.Next);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String ID = editTextID.getText().toString().trim();
                String PS = editTextPS.getText().toString().trim();
                String micro = "80";
                String chomicro = "35";
                mDbOpenHelper.insertColumn(ID,PS,micro,chomicro);
                Intent intent = new Intent(getApplicationContext(), setting.class);
                startActivity(intent);
            }
        });
    }

}


