package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    EditText username;
    EditText password;
    Button login_button;

    ProgressDialog loading_dial;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.log_in_button);
        loading_dial = new ProgressDialog(this);

        login_button.setOnClickListener(this);
        login_button.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
