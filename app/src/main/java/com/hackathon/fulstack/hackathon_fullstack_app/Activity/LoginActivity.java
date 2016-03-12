package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.AppManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.Config;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.DatabaseManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.WTFUser;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    EditText usernameText;
    EditText passwordText;
    Button login_button;

    ProgressDialog loading_dial;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.log_in_button);
        loading_dial = new ProgressDialog(this);
        loading_dial.setCancelable(false);

        login_button.setOnClickListener(this);
        login_button.setOnKeyListener(this);
    }

    public String get_hash(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.toString().getBytes("UTF-8"));
            byte[] ret = md.digest();
            for (int i = 0; i < ret.length; i++) {
                sb.append(Integer.toString((ret[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void attempt_login() {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        final String phash = password;//get_hash(password);

        if(password.length() < 1 || username.length() < 1) {
            loading_dial.hide();
            Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = new Intent(this, MainActivity.class);

        StringRequest request = new StringRequest(Request.Method.POST, Config.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.i("Login Response",s);
                    JSONObject loginOBJ = new JSONObject(s);
                    if (loginOBJ.getInt("status") == 200) {

                        DatabaseManager.getInstance(getApplicationContext()).add_user(
                                new WTFUser(
                                        loginOBJ.getInt("id"),
                                        loginOBJ.getString("username"),
                                        loginOBJ.getString("firstname"),
                                        loginOBJ.getString("lastname"),
                                        loginOBJ.getString("email")
                                )
                        );

                        ArrayList<Preference> arr = new ArrayList<>();
                        JSONArray jarr = loginOBJ.getJSONArray("subscriptions");

                        for(int i = 0 ; i < jarr.length() ; i ++ ) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            long subs_id = temp.getInt("subsid");
                            String search_param = temp.getString("searchparam");
                            JSONArray jarrinner = temp.getJSONArray("links");
                            for(int j = 0 ; j < jarrinner.length() ; j ++ ) {
                                JSONObject tempinner = (JSONObject) jarrinner.get(i);
                                String link = tempinner.getString("link");
                                long pid = tempinner.getLong("pid");
                                String refine = tempinner.getString("refine");
                                arr.add(
                                        new Preference(
                                                pid,
                                                subs_id,
                                                search_param,
                                                link,
                                                refine
                                        )
                                );
                            }
                        }
                        DatabaseManager.getInstance(getApplicationContext()).add_preferences(arr);
                        session.setLoginStatus(true);
                        session.setUser(username);
                        DatabaseManager.getInstance(getApplicationContext()).get_new_feed_all();
                        loading_dial.hide();
                        startActivity(intent);
                    } else {
                        loading_dial.hide();
                        Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading_dial.hide();
                    Toast.makeText(getApplicationContext(), "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", phash);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppManager.getInstance().addToRequestQueue(request, "login", this);

    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.log_in_button) {
            loading_dial.setMessage("Logging In");
            loading_dial.setIndeterminate(true);
            loading_dial.show();

            attempt_login();
        }
    }

    @Override
    public boolean onKey(View v, int i, KeyEvent event) {
        if(i== KeyEvent.ACTION_DOWN && i== KeyEvent.KEYCODE_ENTER){
            Log.e("Login", "enter from");
            attempt_login();
        }
        return false;
    }
}
