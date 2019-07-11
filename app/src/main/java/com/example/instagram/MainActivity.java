package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameInput = findViewById(R.id.etUsername);
        mPasswordInput = findViewById(R.id.etPassword);
        mLoginButton = findViewById(R.id.btnLogin);
        mSignUpButton = findViewById(R.id.btnSignUp);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mUsernameInput.getText().toString();
                final String password = mPasswordInput.getText().toString();

                login (username, password);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = new ParseUser();

                final String username = mUsernameInput.getText().toString();
                final String password = mPasswordInput.getText().toString();

                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Log.d("SignUpActivity", "Sign Up Success");
                            final Intent intent = new Intent (MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null){
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }
}
