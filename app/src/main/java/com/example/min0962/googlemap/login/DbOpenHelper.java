package com.example.min0962.googlemap.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String DatabaseName = "DB";
    private static final int Database_Version = 1;
    public static final String SQL_SELECT = "SELECT * FROM " + database.CreateDB.TableName ;


    public static SQLiteDatabase mDB;
    private DataBaseHelper mDBHelper;
    private Context mCtx;

    private class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(database.CreateDB.Create);
            db.execSQL(database.CreateDB.Create2);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
            db.execSQL("drop table if exists " + database.CreateDB.TableName);
            db.execSQL("drop table if exists " + database.CreateDB.TableName2);
            onCreate(db);
        }
    }

        public DbOpenHelper(Context context)
        {
            this.mCtx = context;
        }
        public DbOpenHelper open() throws SQLException {
            mDBHelper = new DataBaseHelper(mCtx, DatabaseName, null, Database_Version);
            mDB = mDBHelper.getWritableDatabase();
            return this;
        }
        public void close()
        {
            mDB.close();
        }
    public long insertColumn(String id, String ps, String micro, String chomicro) {
        ContentValues values = new ContentValues();
        values.put(database.CreateDB.ID, id);
        values.put(database.CreateDB.PS, ps);
        values.put(database.CreateDB.micro, micro);
        values.put(database.CreateDB.chomicro, chomicro);
        return mDB.insert(database.CreateDB.TableName, null, values);
    }
    public long insertColumn2(Double addr_x, Double addr_y) {
        ContentValues values = new ContentValues();
        values.put(database.CreateDB.addr_x, addr_x);
        values.put(database.CreateDB.addr_y, addr_y);
        return mDB.insert(database.CreateDB.TableName2, null, values);
    }

    public boolean updateColumn(long id, String idd, String ps, String micro, String chomicro) {
        ContentValues values = new ContentValues();
        values.put(database.CreateDB.ID, idd);
        values.put(database.CreateDB.PS, ps);
        values.put(database.CreateDB.micro, micro);
        values.put(database.CreateDB.chomicro, chomicro);
        return mDB.update(database.CreateDB.TableName, values, "_id="+id, null) > 0;
    }

    public boolean deleteColumn(long id) {
        return mDB.delete(database.CreateDB.TableName, "_id=" + id, null) > 0;
    }

    public boolean deleteColumn(String number) {
        return mDB.delete(database.CreateDB.TableName, "contact="+number, null) > 0;
    }

    public Cursor getAllColumns() {
        return mDB.query(database.CreateDB.TableName, null, null, null, null, null, null);
    }

    public Cursor getColumn(long id) {
        Cursor c = mDB.query(database.CreateDB.TableName, null,
                "_id="+id, null, null, null, null);
        if (c != null && c.getCount() != 0)
            c.moveToFirst();
        return c;
    }

    public Cursor getMatchName(String id) {
        Cursor c = mDB.rawQuery( "Select * from user where ID" + "'" + id + "'", null);
        return c;
    }



}
