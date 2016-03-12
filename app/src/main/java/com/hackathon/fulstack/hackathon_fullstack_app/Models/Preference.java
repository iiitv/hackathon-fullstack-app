package com.hackathon.fulstack.hackathon_fullstack_app.Models;

/**
 * Created by pratyush on 12/3/16.
 */
public class Preference {
    public long pid;
    public long subs_id;
    public String search_param;
    public String link;
    public String refine;

    public Preference(long pid, long subs_id, String search_param, String link, String refine) {
        this.pid = pid;
        this.subs_id = subs_id;
        this.search_param = search_param;
        this.link = link;
        this.refine = refine;
    }
}
