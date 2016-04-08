package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.DatabaseManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.RecyclerViewAdapter;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerViewAdapter mAdapter;
    String LOG_TAG = "RecylerListFeeds";
    EditText text;
    Context context;
    Button button;
    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        prog = new ProgressDialog(this);
        prog.setIndeterminate(true);
        prog.setCancelable(false);
        prog.setMessage("Adding preference");
        text = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.buttonAdd);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerPrefs);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        button.setOnClickListener(this);
    }

    protected void onResume() {
        super.onResume();
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }


    private ArrayList<Preference> getDataSet() {
        return DatabaseManager.getInstance(this).get_preferences();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        if (id == R.id.buttonAdd) {
            prog.show();
            StringRequest request = new StringRequest(Request.Method.POST, Config.add_subscription_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.i("Add subs response", s);
                    prog.hide();
                    try {
                        JSONObject main = new JSONObject(s);

                        if (main.getInt("code") != 200) {
                            Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Success, restart app to continue.", Toast.LENGTH_LONG).show();
                            SessionManager.setLoginStatus(false);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Add subscription ", " " + volleyError);
                    prog.hide();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    SessionManager session = new SessionManager(context);
                    params.put("id", String.valueOf(session.getUserID()));
                    params.put("password", session.getPass());
                    params.put("searchparam", text.getText().toString());
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppManager.getInstance().addToRequestQueue(request, "login", this);

        }

    }
}