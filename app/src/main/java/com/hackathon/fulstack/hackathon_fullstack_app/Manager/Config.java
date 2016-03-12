package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

/**
 * Created by pratyush on 12/3/16.
 */
public class Config {
    public static String prefix_url = "http://10.100.2.81:5123";
    public static String login_url = prefix_url + "/api/login";
    public static String register_url = prefix_url + "/api/register";
    public static String all_feed_url = prefix_url + "/api/feed/getmainfeed";
    public static String single_feed_url = prefix_url + "/api/feed/getfeed";
}