package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pratyush on 12/3/16.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String log = "Database Manager";
    private static DatabaseManager instance;
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
        db.close();
    }

    private void rolldown_tables(SQLiteDatabase db) {

        String sql = "create table if not exists userinfo (" +
                "uid primary key not null," +
                "uname text not null," +
                "fname text not null," +
                "lname text," +
                "email text not null" +
                ");";
        db.execSQL(sql);
        Log.i(log, "Created table userinfo");

        sql = "create table if not exists preferences (" +
                "pid integer primary key not null," +
                "subs_id integer not null," +
                "search_param text not null," +
                "link text not null," +
                "refined text not null" +
                ");";
        db.execSQL(sql);
        Log.i(log, "Created table preferences");

        sql = "create table if not exists cache (" +
                "src text not null," +
                "content text," +
                "img_url text," +
                "pid integer references preference(pid)," +
                "url text not null," +
                "pub_time text not null" +
                ");";
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

        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from preferences where pid = " + pid + ";";
        Cursor c = db.rawQuery(sql, null);

        String search_param, link, refine;
        c.moveToFirst();

        Long subs_id = c.getLong(c.getColumnIndex("subs_id"));
        search_param = c.getString(c.getColumnIndex("search_param"));
        link = c.getString(c.getColumnIndex("link"));
        refine = c.getString(c.getColumnIndex("refine"));
        c.close();
        db.close();

        return new Preference(pid, subs_id, search_param, link, refine);

    }

    ArrayList<Feed> get_feeds_for_subs_id(int subs_id) {
        ArrayList<Feed> ret = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

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
        c.close();
        db.close();

        return ret;
    }

    public void add_preferences(ArrayList<Preference> arr) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete from preferences;\n";

        for (int i = 0; i < arr.size(); i++) {
            Preference temp = arr.get(i);
            sql = sql +
                    "insert into preferences values(" + temp.pid + "," + temp.subs_id + "," + temp.search_param + "," + temp.link + "," + temp.refine + ");\n";
        }

        db.execSQL(sql);
        db.close();
    }

    public void get_new_feed_all() {
        StringRequest request = new StringRequest(Request.Method.POST, Config.all_feed_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                SQLiteDatabase db = getWritableDatabase();

                db.execSQL("delete from cache;");

                try {
                    JSONObject response = new JSONObject(s);

                    if (response.getInt("status") == 0) {
                        JSONArray jarr = response.getJSONArray("feeds");

                        String sql = "";

                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            sql = sql +
                                    "insert into cache values(" +
                                    temp.getString("network") + "," +
                                    temp.getString("content") + ",";
                            if (temp.has("imgurl"))
                                sql = sql + temp.getString("imgurl");
                            else
                                sql = sql + "";
                            sql = sql + "," +
                                    temp.getInt("pid") + "," +
                                    temp.getString("url") + "," +
                                    temp.getString("pubtime") +
                                    ");";

                        }

                        db.close();
                    } else
                        Log.e("Database Manager", "Unable to fetch feeds");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Database Manager", "Unable to fetch feeds");
                } finally {
                    db.close();
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
                params.put("username", session.getUser());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppManager.getInstance().addToRequestQueue(request, "allfeedupdate", this.context);
    }

    public void get_new_feeds(final int subs_ids) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.single_feed_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                SQLiteDatabase db = getWritableDatabase();

                String sql = "delete from cache where pid in (select pid from preferences where subsid = " + subs_ids + ");";

                try {
                    JSONObject response = new JSONObject(s);

                    if (response.getInt("status") == 200) {

                        db.execSQL(sql);

                        JSONArray jarr = response.getJSONArray("feeds");

                        sql = "";

                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            sql = sql +
                                    "insert into cache values(" +
                                    temp.getString("network") + "," +
                                    temp.getString("content") + ",";
                            if (temp.has("imgurl"))
                                sql = sql + temp.getString("imgurl");
                            else
                                sql = sql + "";
                            sql = sql + "," +
                                    temp.getInt("pid") + "," +
                                    temp.getString("url") + "," +
                                    temp.getString("pubtime") +
                                    ");";
                        }

                        db.execSQL(sql);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    db.close();
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
                params.put("username", session.getUser());
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
        SQLiteDatabase db = getWritableDatabase();

        String sql = "delete from userinfo;\n" +
                "insert into userinfo values (" + wtfUser.uid + "," + wtfUser.uname + "," + wtfUser.fname + "," + wtfUser.uname + "," + wtfUser.email + ");";

        db.execSQL(sql);
        db.close();
    }

    public void add_dummy_data() {
        SQLiteDatabase db = getWritableDatabase();

        long res = 0;
        res += db.delete("preferences", null, null);
        res += db.delete("userinfo", null, null);
        res += db.delete("cache", null, null);
        Log.i("Rows deletes", res + "");

        ContentValues contentValues = new ContentValues();
        res = 0;

        contentValues.put("pid", 1);
        contentValues.put("subs_id", 1);
        contentValues.put("search_param", "search1");
        contentValues.put("link", "link11");
        contentValues.put("refined", "refined11");
        res += db.insert("preferences", null, contentValues);

        contentValues.put("pid", 2);
        contentValues.put("subs_id", 1);
        contentValues.put("search_param", "search1");
        contentValues.put("link", "link12");
        contentValues.put("refined", "refined12");
        res += db.insert("preferences", null, contentValues);

        contentValues.put("pid", 3);
        contentValues.put("subs_id", 2);
        contentValues.put("search_param", "search2");
        contentValues.put("link", "link21");
        contentValues.put("refined", "refined21");
        res += db.insert("preferences", null, contentValues);

        contentValues.put("pid", 4);
        contentValues.put("subs_id", 2);
        contentValues.put("search_param", "search2");
        contentValues.put("link", "link22");
        contentValues.put("refined", "refined22");
        res += db.insert("preferences", null, contentValues);

        contentValues.put("pid", 4);
        contentValues.put("subs_id", 3);
        contentValues.put("search_param", "search3");
        contentValues.put("link", "link22");
        contentValues.put("refined", "refined22");
        res += db.insert("preferences", null, contentValues);

        contentValues.put("uid", 1);
        contentValues.put("uname", "test");
        contentValues.put("fname", "Test");
        contentValues.put("lname", "user");
        contentValues.put("email", "no@no.no");
        res += db.insert("userinfo", null, contentValues);

        String sql =
                "insert into cache values(fb, content1, iu1, 1, u1, \'2016-03-13 10:10:10 \');\n" +
                "insert into cache values(tw, content2, iu2, 1, u2, \'2016-03-13 10:10:12 \');\n" +
                        "insert into cache values(yt, content3, iu3, 1, u3, \'2016-03-13 10:10:13 \');\n" +
                        "insert into cache values(fb, content4, iu4, 2, u4, \'2016-03-13 10:10:14 \');\n" +
                        "insert into cache values(tw, content5, iu5, 2, u5, \'2016-03-13 10:10:15 \');\n" +
                        "insert into cache values(fb, content6, iu6, 3, u6, \'2016-03-13 10:10:16 \');\n" +
                        "insert into cache values(fb, content7, iu7, 4, u7, \'2016-03-13 10:10:17 \');\n";

        db.execSQL(sql);

        db.close();
    }

    public Map<String, Long> get_preference_names() {
        Map<String, Long> prefs = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select distinct search_param, subs_id from preferences; ";
        Cursor c = db.rawQuery(sql, null);

        Log.i("Rows selected", "" + c.getCount());

        c.moveToFirst();
        do {
            prefs.put(c.getString(c.getColumnIndex("search_param")), new Long(c.getLong(c.getColumnIndex("subs_id"))));
        } while (c.moveToNext());

        c.close();
        db.close();
        return prefs;
    }
}