package com.example.malko.ui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.malko.Database;
import com.example.malko.MainActivity;
import com.example.malko.R;
import com.example.malko.Session;
import com.example.malko.User;
import com.example.malko.ui.signup.SignupActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static User user;
    private String username;
    private String password;
    private ProgressBar loadingProgressBar;
    private final String URL = "https://www.luvo.fi/androidApp/login.php";

    Button loginButton;
    Button signupButton;
    TextInputLayout usernameInputLayout;
    TextInputLayout passwordInputLayout;
    Database mDatabaseHelper;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ConstraintLayout loginConstrainlayout;
    User returnedUser = null;

    Session session;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginConstrainlayout = findViewById(R.id.login_constrainlayout);

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException ignored){}

        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.login_takeToSignup);
        usernameInputLayout = findViewById(R.id.login_uname);
        passwordInputLayout = findViewById(R.id.login_password);
        loadingProgressBar = findViewById(R.id.login_progressBar);

        mDatabaseHelper = new Database(this);
        session = new Session(this);

        username = "";
        password = "";


        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            login(v);
        });

        signupButton.setOnClickListener(v -> startActivity(new Intent( LoginActivity.this, SignupActivity.class)));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(View view) {
        username = usernameInputLayout.getEditText().getText().toString().trim();
        password = passwordInputLayout.getEditText().getText().toString().trim();

        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        if (!username.equals("") && !password.equals("")) {

            stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                try {
                    JSONObject userData = new JSONObject(response);
                    Log.d("Server", response);

                    if (userData.length() == 0) {
                        returnedUser = null;
                    } else {
                        String uid = userData.getString("user_id");
                        String dob = userData.getString("dob");
                        String password = userData.getString("password");
                        String date_created = userData.getString("date_created");

                        returnedUser = new User(uid, username, password, dob, date_created);
                        logUserIn(returnedUser);
                        returnedUser.setDateCreated(date_created);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    loadingProgressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("Server ", e.getMessage());
                    if (response.trim().equals("Error logging in")) {
                        showErrorMessage("Please check your username and password");
                        usernameInputLayout.setError("Please check your username and password");
                    } else {
                        showErrorMessage(response.trim());
                    }
                }

            }, error -> {
                loadingProgressBar.setVisibility(View.GONE);
                error.printStackTrace();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                dialogBuilder.setTitle("Connection Error");
                dialogBuilder.setMessage("Error occurred while trying to connect server, please try again later.");
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();

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
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } else {
            showErrorMessage("Fill in all forms");
        }
    }

/*    @RequiresApi(api = Build.VERSION_CODES.P)
    private void authenticate(User user) {
        ServerRequests serverRequests = new ServerRequests(this, R.id.login_progressBar);
        serverRequests.fetchUserDataInBg(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage("Incorrect user details");
                } else {
                    logUserIn(returnedUser);
                }
            }
        });

    }*/
    private void showErrorMessage (String message) {
        loadingProgressBar.setVisibility(View.GONE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        session.storeUserData(returnedUser);
        session.setUserLoggedIn(true);

        startActivity(new Intent(this, MainActivity.class));
    }
}