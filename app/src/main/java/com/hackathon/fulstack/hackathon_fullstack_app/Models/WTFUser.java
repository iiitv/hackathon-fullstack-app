package com.hackathon.fulstack.hackathon_fullstack_app.Models;

/**
 * Created by pratyush on 12/3/16.
 */
public class WTFUser {

    public long uid;
    public String uname;
    public String fname, lname, email;

    public WTFUser(long a, String b, String c, String d, String e) {
        uid = a;
        uname = b;
        fname = c;
        lname = d;
        email = e;
    }

}
