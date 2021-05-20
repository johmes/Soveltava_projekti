package com.example.malko.ui.signup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.malko.MainActivity;
import com.example.malko.R;
import com.example.malko.Session;
import com.example.malko.User;
import com.example.malko.ui.login.LoginActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private final String URL = "https://www.luvo.fi/androidApp/signup.php";

    final Calendar myCalendar = Calendar.getInstance();

    private ProgressBar loadingProgressBar;
    private String username;
    private String password;
    private String dob;

    StringRequest stringRequest;
    RequestQueue requestQueue;
    Session session;
    User returnedUser;

    EditText etUsername;
    EditText etPassword;
    EditText etBirthday;
    Button signupButton, loginButton;
    ConstraintLayout signupConstrainlayout;

    public SignupActivity() {
        username = "";
        password = "";
        dob = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException ignored){}

        setContentView(R.layout.activity_signup);

        signupConstrainlayout = findViewById(R.id.signup_constrainlayout);
        etUsername = findViewById(R.id.signup_uname);
        etPassword = findViewById(R.id.signup_password);
        etBirthday = findViewById(R.id.signup_dob);
        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.signup_login_button);
        loadingProgressBar = findViewById(R.id.signup_loading);

        session = new Session(this);

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        // ON CLICK LISTENERS
        etBirthday.setOnClickListener(v -> new DatePickerDialog(SignupActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        signupButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            save(v);
        });
        loginButton.setOnClickListener(v -> startActivity(new Intent( SignupActivity.this, LoginActivity.class)));
    }

    public void save(View view) {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        dob = etBirthday.getText().toString();
        int age = getAge(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        requestQueue = Volley.newRequestQueue(SignupActivity.this);
        if (!username.equals("") && !password.equals("") && !dob.equals("")) {
            if (age >= 18) {
                stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                    try {
                        JSONObject userData = new JSONObject(response);
                        Log.d("Server", response);

                        if (userData.length() == 0) {
                            loadingProgressBar.setVisibility(View.GONE);
                            showErrorMessage("Uh oh! That wasn't supposed to happen...");
                            returnedUser = null;
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            String uid = userData.getString("user_id");
                            String date_created = userData.getString("date_created");

                            returnedUser = new User(uid, this.username, this.password, this.dob, date_created);
                            logUserIn(returnedUser);
                        }
                    } catch (Exception e) {
                        loadingProgressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        Log.e("Server ", e.getMessage());
                        showErrorMessage(response.trim());
                    }
                }, error -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    error.printStackTrace();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                    dialogBuilder.setTitle("Connection Error");
                    dialogBuilder.setMessage("Error occurred while trying to connect server, please try again later.");
                    dialogBuilder.setPositiveButton("OK", null);
                    dialogBuilder.show();
                }) {
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("username", username);
                        data.put("password", password);
                        data.put("dob", dob);
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
                loadingProgressBar.setVisibility(View.GONE);
                showErrorMessage("You are too young");
            }
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            showErrorMessage("Fill in all forms");
        }
    }

    // calculate user age
    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }


    // Updates the birthday edit text hint
    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        etBirthday.setText(sdf.format(myCalendar.getTime()));
    }

    private void showErrorMessage (String message) {
        loadingProgressBar.setVisibility(View.GONE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
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
