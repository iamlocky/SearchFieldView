package cn.lockyluo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by LockyLuo on 2018/7/15.
 */

public class HistoryDbHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase database = null;
    public final static String db_name = "history.db";
    public final static String table_name = "histories";
    private static final String TAG = "HistoryDbHelper";
    private Context context;

    public HistoryDbHelper(Context context) {
        super(context, db_name, null, 1);
        this.context = context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        final String sql_create_table = "create table " + table_name + "(id integer primary key autoincrement,name varchar(200))";
        db.execSQL(sql_create_table);
        Log.d(TAG, "onCreate: " + sql_create_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor query(String s) {
        String selection;
        String[] args;
        if (TextUtils.isEmpty(s))
        {
            args = null;
            selection=null;
        }
        else
        {
            args = new String[]{s + "%"};
            selection="name like ?";
        }
        database = getWritableDatabase();
        return database.query(table_name, null,selection , args, null, null, null);
    }

    public void insert(String s) {
        database = getWritableDatabase();
        database.execSQL("insert into " + table_name + "(name) values ('" + s + "')");
        database.close();
    }

    public void del(String s) {
        database = getWritableDatabase();
        database.execSQL("delete from " + table_name + " where name='" + s + "'");
        database.close();
    }

    public void delDatabase() {
        File file = new File(context.getDatabasePath(db_name).getPath());
        if (file.exists()) {
            file.delete();
        } else {
            Log.d(TAG, "delDatabase: file is null");
        }
    }

    public void deleteAllData() {
        database = getWritableDatabase();
        database.execSQL("delete from " + table_name);
        database.close();
    }

    public void closeDb() {
        if (database != null) {
            database.close();
        }
    }
}
