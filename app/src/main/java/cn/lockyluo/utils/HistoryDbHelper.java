package cn.lockyluo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LockyLuo on 2018/7/15.
 */

public class HistoryDbHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase database=null;
    public final static String table_name="histories";

    public HistoryDbHelper(Context context) {
        super(context, "history.db", null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
                    if (cursor.getCount()==0) {
                        db.execSQL("create table "+table_name+"(id integer primary key autoincrement,name varchar(200))");
                    }
                } catch (Exception e) {
                    db.execSQL("create table "+table_name+"(id integer primary key autoincrement,name varchar(200))");
                }
            }
        });
        thread.start();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor query(String s){
        database=getReadableDatabase();
        return database.query(table_name,null,null,null,null,null,null);
    }

    public void insert(String s){
        database=getWritableDatabase();
        database.execSQL("insert into "+table_name+"(name) values ('"+s+"')");
        database.close();
    }

    public void del(String s){
        database=getWritableDatabase();
        database.execSQL("delete from "+table_name+" where name='"+s+"'");
        database.close();
    }

    public void deleteAllData(){
        database=getWritableDatabase();
        database.execSQL("delete from "+table_name);
        database.close();
    }

    public void closeDb(){
        if (database!=null){
            database.close();
        }
    }
}
