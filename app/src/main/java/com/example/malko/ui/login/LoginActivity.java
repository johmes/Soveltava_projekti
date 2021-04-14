package com.example.malko.ui.login;

import android.app.Activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.malko.MainActivity;
import com.example.malko.R;
import com.example.malko.ui.signup.SignupActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;
    private String URL = "https://www.luvo.fi/androidApp/login.php";
    Button loginButton;
    Button signupButton;
    EditText usernameEditText;
    EditText passwordEditText;
    ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_login);

        ConstraintLayout loginConstrainlayout = findViewById(R.id.login_constrainlayout);

        username = "";
        password = "";
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.login_takeToSignup);
        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loadingProgressBar = findViewById(R.id.login_loading);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(v);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( LoginActivity.this, SignupActivity.class));
            }
        });
    }

    public void login(View view) {
        username = usernameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        if (!username.equals("") && !password.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("Success")) {
                        Log.d("Response", response);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        loadingProgressBar.setVisibility(View.GONE);
                        finish();
                    } else if (response.contains("Error logging in")) {
                        Log.d("Response", response);
                        Toast.makeText(LoginActivity.this, "Check username and password", Toast.LENGTH_SHORT).show();
                        loadingProgressBar.setVisibility(View.GONE);
                    } else {
                        Log.d("Response", response);
                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Virhe volley: " + error.toString().trim(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("username", username);
                    data.put("password", password);
                    return data;
                }
            };
            //10000 is the time in milliseconds adn is equal to 10 sec
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(LoginActivity.this, "Fill in all forms", Toast.LENGTH_SHORT).show();
            loadingProgressBar.setVisibility(View.GONE);
        }
    }
}