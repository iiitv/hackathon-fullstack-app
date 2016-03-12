package com.hackathon.fulstack.hackathon_fullstack_app.Models;

/**
 * Created by pratyush on 12/3/16.
 */
public class Feed {

    String src;
    int post_type;
    String content;
    String image_url;
    long pid;
    String url;
    String time;

    public Feed(String src, int post_type, String content, String image_url, long pid, String url, String time) {
        this.src = src;
        this.post_type = post_type;
        this.content = content;
        this.image_url = image_url;
        this.pid = pid;
        this.url = url;
        this.time = time;
    }

    public Feed(String src, int post_type, String content, long pid, String url, String time) {
        this.src = src;
        this.post_type = post_type;
        this.content = content;
        this.image_url = "";
        this.pid = pid;
        this.url = url;
        this.time = time;
    }

}
