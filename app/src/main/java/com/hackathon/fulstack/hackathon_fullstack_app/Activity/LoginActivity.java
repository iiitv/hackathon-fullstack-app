package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        setContentView(R.layout.activity_main);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.log_in_button);
        loading_dial = new ProgressDialog(this);

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

    public boolean attempt_login() {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        final String phash = get_hash(password);

        if(password.length() < 1 || username.length() < 1)
            return false;

        StringRequest request = new StringRequest(Request.Method.POST, "SomeURL", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject loginOBJ = new JSONObject(s);
                    int error_code = loginOBJ.getInt("status");
                    if (error_code == 0) {

                        //Success

                    } else {
                        loading_dial.hide();
                        Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading_dial.hide();
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
        return true;
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.log_in_button) {
            if ( !attempt_login() ) {
                Toast.makeText(this, "Invalid Login", Toast.LENGTH_SHORT ).show();
            }
            else {
                //take steps
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
