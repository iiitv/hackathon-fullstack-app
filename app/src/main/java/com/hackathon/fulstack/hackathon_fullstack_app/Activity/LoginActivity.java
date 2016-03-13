package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText usernameText;
    EditText passwordText;
    Button login_button, register_button;

    ProgressDialog loading_dial;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.log_in_button);
        register_button = (Button) findViewById(R.id.registration);
        loading_dial = new ProgressDialog(this);
        loading_dial.setCancelable(false);
        session = new SessionManager(this);

        login_button.setOnClickListener(this);
        login_button.setOnKeyListener(this);
        register_button.setOnClickListener(this);
    }

    public void attempt_login() {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        final String phash = password;

        if (password.length() < 1 || username.length() < 1) {
            loading_dial.hide();
            Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = new Intent(this, MainActivity.class);

        StringRequest request = new StringRequest(Request.Method.POST, Config.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.i("Login Response", s);
                    JSONObject loginOBJ = new JSONObject(s);
                    if (loginOBJ.getInt("code") == 200) {

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

                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject temp = (JSONObject) jarr.get(i);
                            long subs_id = temp.getInt("subsid");
                            String search_param = temp.getString("searchparam");
                            JSONArray jarrinner = temp.getJSONArray("links");
                            for (int j = 0; j < jarrinner.length(); j++) {
                                JSONObject tempinner = (JSONObject) jarrinner.get(i);
                                String link = tempinner.getString("url");
                                long pid = tempinner.getLong("pid");
                                String refine = tempinner.getString("name");
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
                        SessionManager.setLoginStatus(true);
                        session.setUser(username);
                        session.setUserID(loginOBJ.getInt("id"));
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
        if (v.getId() == R.id.log_in_button) {
            loading_dial.setMessage("Logging In");
            loading_dial.setIndeterminate(true);
            loading_dial.show();

            attempt_login();
        }

        if (v.getId() == R.id.registration) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

    @Override
    public boolean onKey(View v, int i, KeyEvent event) {
        if (i == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
            Log.e("Login", "enter from");
            attempt_login();
        }
        return false;
    }
}
