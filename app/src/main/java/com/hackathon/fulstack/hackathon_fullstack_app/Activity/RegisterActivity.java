package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fname, lname, email, username, password, passwordAgain;
    ProgressDialog loading_dial;
    SessionManager session;
    Button register;
    String fnameStr, lnameStr, emailStr, usernameStr, passwordStr, passwordAgainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText) findViewById(R.id.firstName);
        lname = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        passwordAgain = (EditText) findViewById(R.id.passwordAgain);
        loading_dial = new ProgressDialog(this);
        session = new SessionManager(this);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        loading_dial.setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            fnameStr = fname.getText().toString();
            lnameStr = lname.getText().toString();
            emailStr = email.getText().toString();
            usernameStr = username.getText().toString();
            passwordStr = password.getText().toString();
            passwordAgainStr = passwordAgain.getText().toString();
            loading_dial.setIndeterminate(true);
            loading_dial.setMessage("Registering You");
            loading_dial.show();
            attempt_register();
        }
    }

    private void attempt_register() {

        Log.i(
                "Registration fields",
                fnameStr + " " + lnameStr + " " + emailStr + " " + usernameStr + " " + passwordStr + " " + passwordAgainStr
        );

        if (fnameStr.length() < 1 || lnameStr.length() < 1 || emailStr.length() < 1 || usernameStr.length() < 1 || passwordAgainStr.length() < 1 || passwordStr.length() < 1) {
            Toast.makeText(RegisterActivity.this, "Fields can't be empty.", Toast.LENGTH_SHORT).show();
            loading_dial.hide();
            return;
        }

        if (!emailStr.contains("@") || !emailStr.contains(".")) {
            Toast.makeText(RegisterActivity.this, "Invalid E-Mail ID..", Toast.LENGTH_SHORT).show();
            loading_dial.hide();
            return;
        }

        if (!passwordAgainStr.matches(passwordStr)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            loading_dial.hide();
            return;
        }

        final Intent intent = new Intent(this, LoginActivity.class);

        StringRequest request = new StringRequest(Request.Method.POST, Config.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    JSONObject obj = new JSONObject(s);

                    if (obj.getInt("status") == 200) {
                        loading_dial.hide();
                        Toast.makeText(getApplicationContext(), "Registered on WTF", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        loading_dial.hide();
                        Toast.makeText(getApplicationContext(), "Error : " + obj.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Register", " " + volleyError);
                Toast.makeText(getApplicationContext(), (CharSequence) volleyError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", usernameStr);
                params.put("password", passwordStr);
                params.put("fname", fnameStr);
                params.put("lname", lnameStr);
                params.put("email", emailStr);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppManager.getInstance().addToRequestQueue(request, "Register", this);
    }
}
