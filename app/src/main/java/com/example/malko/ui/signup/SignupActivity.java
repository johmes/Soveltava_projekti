package com.example.malko.ui.signup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.example.malko.User;
import com.example.malko.ui.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.media.CamcorderProfile.get;

public class SignupActivity extends AppCompatActivity {
    private String URL = "https://www.luvo.fi/androidApp/signup.php";
    EditText etUsername;
    EditText etPassword;
    EditText etBirthday;
    Button signupButton, loginButton;
    private ProgressBar loadingProgressBar;
    private String username;
    private String password;
    private String dob;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_signup);

        ConstraintLayout signupConstrainlayout = findViewById(R.id.signup_constrainlayout);
        etUsername = findViewById(R.id.signup_uname);
        etPassword = findViewById(R.id.signup_password);
        etBirthday = findViewById(R.id.signup_dob);
        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.signup_login_button);
        loadingProgressBar = findViewById(R.id.signup_loading);
        username = "";
        password = "";
        dob = "";

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        // ON CLICK LISTENERS
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignupActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                save(v);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SignupActivity.this, LoginActivity.class));
            }
        });
    }

    public void save(View view) {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        dob = etBirthday.getText().toString().trim();
        int age = getAge(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        if (!username.equals("") && !password.equals("") && !dob.equals("")) {
            if (age >= 18) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If everything goes well
                        if (response.contains("Success")) {
                            loadingProgressBar.setVisibility(View.GONE);

                            // Set login session true
                            User.setLogin(getApplicationContext());

                            // Go to home page
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        // If there is problems
                        } else if (response.contains("Error Processing Request")) {
                            loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(SignupActivity.this, "Uh oh! Error occurred...", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(SignupActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("username", username);
                        data.put("password", password);
                        data.put("dob", dob);
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
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "You need to be 18", Toast.LENGTH_SHORT).show();
            }
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(SignupActivity.this, "Fill in all forms", Toast.LENGTH_SHORT).show();

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

        Integer ageInt = new Integer(age);
        int ageS = ageInt;

        return ageS;
    }


    // Updates the birthday edit text hint
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        etBirthday.setText(sdf.format(myCalendar.getTime()));
    }
}
