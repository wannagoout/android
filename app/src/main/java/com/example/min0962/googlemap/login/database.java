package com.example.min0962.googlemap.login;

import android.provider.BaseColumns;

public class database {
    public static final class CreateDB implements BaseColumns
    {
        public static final String ID = "ID";
        public static final String PS = "PS";
        public static final String micro = "micro";
        public static final String chomicro = "chomicro";
        public static final String TableName = "user";
        public static final String TableName2 = "address";
        public static final String addr_x = "addr_x";
        public static final String addr_y = "addr_y";
        public static final String Create =
                "create table if not exists " + TableName+ "("
                        + ID+ "text "
                        + PS+ "text "
                        + micro+ "text "
                        + chomicro+ "text );";
        public static final String Create2 =
                "create table if not exists " + TableName2+ "("
                        + addr_x+ "text "
                        + addr_y+ "text );";
    }
}
