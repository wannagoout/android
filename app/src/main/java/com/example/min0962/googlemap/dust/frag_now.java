package com.example.min0962.googlemap.dust;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.min0962.googlemap.R;

public class frag_now extends Fragment {
   //public TextView tv;

    public String addrn, callname, staten;
    public double dustval;
    public frag_now(){}
    private OnFragmentInteractionListener mListener;

    public static Fragment newInstance(String param1, String param2, Integer param3) {
        frag_now fragment = new frag_now();
        Bundle args = new Bundle();
        args.putString("address", param1);
        args.putString("location_name", param2);
        args.putInt("dust_value",param3);
        fragment.setArguments(args);
        setall(param1,param2,param3);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            addrn=getArguments().getString("address");
            callname=getArguments().getString("location_name");
            dustval=getArguments().getDouble("dust_value");
            setall(addrn,callname,dustval);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        RelativeLayout layout=(RelativeLayout)inflater.inflate(
                R.layout.frag_nowloca,container,false);
        return layout;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public static void setall(String param1, String param2, double param3){
        TextView tv;    View view=null; ImageView iv;

        tv = (TextView) view.findViewById(R.id.Addr_now);
        tv.setText(param1);
        tv=(TextView)view.findViewById(R.id.name_now);
        tv.setText(param2);
        tv=(TextView)view.findViewById(R.id.dustVal_now);
        tv.setText(param3 + "µg");
        tv=(TextView)view.findViewById(R.id.dustState_now);
        iv=(ImageView)view.findViewById(R.id.dustIC);
        if (param3 <= 30) {
            tv.setText("좋음");
            iv.setImageResource(R.drawable.ic_sgood);
        }else if(param3<=80) {
            tv.setText("보통");
            iv.setImageResource(R.drawable.ic_ssoso);
        }else if(param3<=150){
            tv.setText("나쁨");
            iv.setImageResource(R.drawable.ic_sbad);
        }else if(param3>150){
            tv.setText("매우나쁨");
            iv.setImageResource(R.drawable.ic_svery);
        }
    }
}
