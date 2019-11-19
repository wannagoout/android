package com.example.min0962.googlemap.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AutoLogin {
    static final String ID = "ID";
    static final String PS = "PS";
    static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLogin(Context ctx, String id, String ps)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(ID,id);
        editor.putString(PS,ps);
        editor.commit();
    }
    public static String getID(Context ctx)
    {
        return getSharedPreferences(ctx).getString(ID,"");
    }
    public static String getPS(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PS,"");
    }
    public static void clearLogin(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
