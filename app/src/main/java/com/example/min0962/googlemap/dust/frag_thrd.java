package com.example.min0962.googlemap.dust;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.min0962.googlemap.R;

public class frag_thrd extends Fragment {
    TextView tv;
    View view=null;
    public frag_thrd(){}
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
/*        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.frag_thrdloca,container,false);
        return rootView;*/
        RelativeLayout layout=(RelativeLayout)inflater.inflate(
                R.layout.frag_nowloca,container,false);
        return layout;
    }
    public void getData(String name, String addr, int val) {
        tv = (TextView) view.findViewById(R.id.name_now);
        tv.setText(name);
        tv = (TextView) view.findViewById(R.id.Addr_now);
        tv.setText(addr);
        tv = (TextView) view.findViewById(R.id.dustVal_now);
        tv.setText(val + "㎍/m³");
        tv = (TextView) view.findViewById(R.id.dustState_now);
        if (val <= 30) {
            tv.setText("좋음");
        }else if(val<=80) {
            tv.setText("보통");
        }else if(val<=150){
            tv.setText("나쁨");
        }else if(val>150){
            tv.setText("매우나쁨");
        }
    }
}
