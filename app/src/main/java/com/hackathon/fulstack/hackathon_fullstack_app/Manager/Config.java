package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

/**
 * Created by pratyush on 12/3/16.
 */
public class Config {
    public static String prefix_url = "http://192.168.1.101:5000";
    public static String login_url = prefix_url + "/api/login";
    public static String register_url = prefix_url + "/api/register";
    public static String all_feed_url = prefix_url + "/api/feed/getmainfeed";
    public static String single_feed_url = prefix_url + "/api/feed/getfeed";
    public static String add_subscription_url = prefix_url + "/api/feed/addsubscription";
}