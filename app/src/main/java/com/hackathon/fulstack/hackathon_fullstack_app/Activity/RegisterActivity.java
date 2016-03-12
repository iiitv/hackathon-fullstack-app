package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

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

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.register) {
            fnameStr = fname.getText().toString();
            lnameStr = lname.getText().toString();
            emailStr = email.getText().toString();
            usernameStr = username.getText().toString();
            passwordStr = password.getText().toString();
            passwordAgainStr = passwordAgain.getText().toString();
            attempt_register();
        }
    }

    private void attempt_register() {
        if(fnameStr.length()<1 || lnameStr.length()<1 || emailStr.length()<1 || usernameStr.length()<1 || passwordAgainStr.length()<1 || passwordStr.length()<1) {
            Toast.makeText(this, "Fields can't be empty.", Toast.LENGTH_SHORT);
            return;
        }

        if(!emailStr.contains("@") || !emailStr.contains(".")) {
            Toast.makeText(this, "Invalid E-Mail ID..", Toast.LENGTH_SHORT);
            return;
        }

        if(passwordAgainStr.matches(passwordStr)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT);
            return;
        }



    }
}
