package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pratyush on 12/3/16.
 */
public class SessionManager {

    private static final String KEY = "isLoggedIn";
    private static final String PREF_NAME = "hackathon-fullstack-app";
    private static final String TAG = SessionManager.class.getSimpleName();
    static SharedPreferences.Editor editor;
    static SharedPreferences preferences;
    int Mode = 0;
    Context _context;

    public SessionManager(Context context) {
        this._context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Mode);
        editor = preferences.edit();
    }

    public static void setLoginStatus(boolean state) {
        editor.putBoolean(KEY, state);
        editor.commit();
    }

    public int get_subs_id() {
        return preferences.getInt("subs_id", -1);
    }

    public void set_subs_id(int id) {
        editor.putInt("subs_id", id);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY, false);
    }

    public String getUser() {
        return preferences.getString("user", "");
    }

    public void setUser(String user) {
        editor.putString("user", user);
        editor.commit();
    }

    public int getUserID() {
        return preferences.getInt("userID", -1);
    }

    public void setUserID(int id) {
        editor.putInt("userID", id);
        editor.commit();
    }
}
