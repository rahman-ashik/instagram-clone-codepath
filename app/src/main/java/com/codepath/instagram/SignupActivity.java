package com.codepath.instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etPhone;
    private Button btnSignup2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);




        etUsername = findViewById(R.id.etNewUsername);
        etPassword = findViewById(R.id.etNewPassword);
        etEmail = findViewById(R.id.etNewEmail);
        etPhone = findViewById(R.id.etNewPhone);
        btnSignup2 = findViewById(R.id.btnSignup2);



        btnSignup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                //Set properties
                user.setUsername(etUsername.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.setEmail(etEmail.getText().toString());

                // Set custom properties
                user.put("phone", etPhone.getText().toString());

                // start signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            final Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}