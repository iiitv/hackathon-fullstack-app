package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by pratyush on 12/3/16.
 */
public class SessionManager {

    SQLiteDatabase db;
    public SessionManager(){
        db = new SQLiteDatabase();
    }

}
