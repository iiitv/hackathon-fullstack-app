package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hackathon.fulstack.hackathon_fullstack_app.Models.Feed;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by pratyush on 12/3/16.
 */
public class DatabaseManager extends SQLiteOpenHelper{

    private static DatabaseManager instance;
    private static final String log = "Database Manager";

    public DatabaseManager(Context context) {
        super(context, "masterDB", null, 1);
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public void IntiateDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        rolldown_tables(db);
    }

    private void rolldown_tables(SQLiteDatabase db) {

        String sql = "create table if not exists userinfo (" +
                "uid primary key not null," +
                "uname text not null," +
                "fname text not null," +
                "lname text," +
                "email text noy null" +
                ");" ;
        db.execSQL(sql);
        Log.i(log, "Created table userinfo");

        sql = "create table if not exists preferences (" +
                "pid integer primary key not null," +
                "search_param text not null," +
                "link text not null," +
                "refined text not null" +
                ");" ;
        db.execSQL(sql);
        Log.i(log, "Created table preferences");

        sql = "create table if not exists cache (" +
                "src text not null," +
                "type int not null," +
                "content text," +
                "img_url text," +
                "pid integer references preference(pid)," +
                "url text not null," +
                "pub_time text not null" +
                ");" ;
        db.execSQL(sql);
        Log.i(log, "Created table cache");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        rolldown_tables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    Preference get_preference(long pid) {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from preferences where pid = " + pid + ";";
        Cursor c = db.rawQuery(sql,null);

        String search_param, link ;
        c.moveToFirst();

        search_param = c.getString(c.getColumnIndex("search_param"));
        link = c.getString(c.getColumnIndex("link"));

        return new Preference(pid, search_param, link);

    }

    ArrayList<Feed> get_feeds_for_search_parameter(String search_param) {
        ArrayList<Feed> ret = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select cache.*  from preference natural join cache where preference.search_param = " + search_param + " order by date(pub_time) desc;";
        Cursor c = db.rawQuery(sql, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            ret.add(
                    new Feed(
                            c.getString(c.getColumnIndex("src")),
                            c.getInt(c.getColumnIndex("type")),
                            c.getString(c.getColumnIndex("content")),
                            c.getString(c.getColumnIndex("img_url")),
                            c.getLong(c.getColumnIndex("pid")),
                            c.getString(c.getColumnIndex("url")),
                            c.getString(c.getColumnIndex("pub_time"))
                    )
            );
        }

        return ret;
    }

}