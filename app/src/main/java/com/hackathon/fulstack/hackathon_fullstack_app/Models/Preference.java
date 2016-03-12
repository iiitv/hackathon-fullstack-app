package com.hackathon.fulstack.hackathon_fullstack_app.Models;

/**
 * Created by pratyush on 12/3/16.
 */
public class Preference {
    public long pid;
    public String search_param;
    public String link;

    public Preference(long pid, String search_param, String link) {
        this.pid = pid;
        this.search_param = search_param;
        this.link = link;
    }
}
