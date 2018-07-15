package cn.lockyluo.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LockyLuo on 2018/7/15.
 */

public class HistoryDbHelper extends SQLiteOpenHelper {
    public HistoryDbHelper(Context context) {
        super(context, "history.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
