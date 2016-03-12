package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Feed;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.WTFUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pratyush on 12/3/16.
 */
public class DatabaseManager extends SQLiteOpenHelper{

    private static DatabaseManager instance;
    private static final String log = "Database Manager";
    SessionManager session;
    Context context;

    public DatabaseManager(Context context) {
        super(context, "masterDB", null, 1);
        this.context = context;
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
                "subs_id integer not null," +
                "search_param text not null," +
                "link text not null," +
                "refined text not null" +
                ");" ;
        db.execSQL(sql);
        Log.i(log, "Created table preferences");

        sql = "create table if not exists cache (" +
                "src text not null," +
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

        String search_param, link, refine;
        c.moveToFirst();

        Long subs_id = c.getLong(c.getColumnIndex("subs_id"));
        search_param = c.getString(c.getColumnIndex("search_param"));
        link = c.getString(c.getColumnIndex("link"));
        refine = c.getString(c.getColumnIndex("refine"));

        return new Preference(pid, subs_id, search_param, link, refine);

    }

    ArrayList<Feed> get_feeds_for_subs_id(int subs_id) {
        ArrayList<Feed> ret = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select cache.*  from preference natural join cache where preference.search_param = " + subs_id + " order by date(pub_time) desc;";
        Cursor c = db.rawQuery(sql, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            ret.add(
                    new Feed(
                            c.getString(c.getColumnIndex("src")),
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

    public void add_preferences(ArrayList<Preference> arr) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from preferences;\n";

        for(int i = 0 ; i < arr.size() ; i ++ ) {
            Preference temp = arr.get(i);
            sql = sql +
                    "insert into preferences values(" + temp.pid + "," + temp.subs_id + "," + temp.search_param + "," + temp.link + "," + temp.refine + ");\n";
        }

        db.execSQL(sql);
    }

    public void get_new_feed_all() {
        StringRequest request = new StringRequest(Request.Method.POST, Config.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                SQLiteDatabase db = getWritableDatabase();

                db.execSQL("delete from cache;");

                try {
                    JSONObject response = new JSONObject(s);

                    if(response.getInt("status") == 0) {
                        JSONArray jarr = response.getJSONArray("feeds");

                        String sql = "";

                        for(int i = 0 ; i < jarr.length() ; i ++ ) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            sql = sql +
                                    "insert into cache values(" +
                                    temp.getString("network") + "," +
                                    temp.getString("content") + "," ;
                            if (temp.has("imgurl") )
                                sql = sql + temp.getString("imgurl");
                            else
                                sql = sql + "";
                            sql = sql + "," +
                                    temp.getInt("pid") + "," +
                                    temp.getString("url") + "," +
                                    temp.getString("pubtime") +
                                    ");" ;

                        }

                        db.execSQL(sql);
                    }
                    else
                        Log.e("Database Manager", "Unable to fetch feeds");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Database Manager", "Unable to fetch feeds");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Login", " " + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",session.getUser());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppManager.getInstance().addToRequestQueue(request, "allfeedupdate", this.context);
    }

    public void get_new_feeds(final int subs_ids) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                SQLiteDatabase db = getWritableDatabase();

                String sql = "delete from cache where pid in (select pid from preferences where subsid = " + subs_ids + ");" ;

                try {
                    JSONObject response = new JSONObject(s);

                    if( response.getInt("status") == 200 ) {

                        db.execSQL(sql);

                        JSONArray jarr = response.getJSONArray("feeds");

                        sql = "";

                        for(int i = 0 ; i < jarr.length() ; i ++ ) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            sql = sql +
                                    "insert into cache values(" +
                                    temp.getString("network") + "," +
                                    temp.getString("content") + "," ;
                            if (temp.has("imgurl") )
                                sql = sql + temp.getString("imgurl");
                            else
                                sql = sql + "";
                            sql = sql + "," +
                                    temp.getInt("pid") + "," +
                                    temp.getString("url") + "," +
                                    temp.getString("pubtime") +
                                    ");" ;
                        }

                        db.execSQL(sql);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Login", " " + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",session.getUser());
                params.put("subsid", String.valueOf(subs_ids));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppManager.getInstance().addToRequestQueue(request, "singlefeedupdate", this.context);
    }

    public void add_user(WTFUser wtfUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "delete from userinfo;\n" +
                "insert into userinfo values (" + wtfUser.uid + "," + wtfUser.uname + "," + wtfUser.fname + "," + wtfUser.uname + "," + wtfUser.email + ");" ;

        db.execSQL(sql);
    }
}