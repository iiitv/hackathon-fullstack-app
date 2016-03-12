package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by pratyush on 12/3/16.
 */
public class SessionManager {

    private static final String KEY="isLoggedIn";
    private static final String PREF_NAME="collegare";
    private static final String TAG=SessionManager.class.getSimpleName();
    static SharedPreferences.Editor editor;
    static SharedPreferences preferences;
    int Mode=0;
    Context _context;

    public SessionManager(Context context)
    {
        this._context=context;
        preferences=context.getSharedPreferences(PREF_NAME,Mode);
        editor=preferences.edit();
    }

    public static void setLoginStatus(boolean state)
    {
        editor.putBoolean(KEY,state);
        editor.commit();
    }

    public SessionManager(){

    }

}
