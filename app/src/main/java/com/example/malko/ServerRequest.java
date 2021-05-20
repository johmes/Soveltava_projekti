package com.example.malko;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerRequest {
    /** TÄMÄ FILE ON SOVELLUKSEN TOIMISEN KANNALTA TURHA, TARKOITUKSENA OLI TEHDÄ UUSI BACKEND
     * MUTTA EDELLINEN TOIMI PAREMMIN :D */
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "https://www.luvo.fi/androidApp/";
    ProgressBar progressBar;
    Context context;


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void ServerRequests(Context context, int view) {
        this.context = context;
        progressBar = new ProgressBar(context, null, view);
    }

    public void storeUserDataInBg(User user, GetUserCallback userCallback) {
        progressBar.setVisibility(View.VISIBLE);
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void fetchUserDataInBg(User user, GetUserCallback userCallback) {
        progressBar.setVisibility(View.VISIBLE);
        new fetchUserDataAsyncTask(user, userCallback).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContentValues dataSend = new ContentValues();
            dataSend.put("user_id", user.uid);
            dataSend.put("username", user.username);
            dataSend.put("password", user.password);
            dataSend.put("dob", user.dob);
            dataSend.put("date_created", user.date_created);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            userCallback.done(null);

            super.onPostExecute(aVoid);

        }

    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User returnedUser = null;
        User user;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            String URL = SERVER_ADDRESS + "login.php";


            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                try {
                    JSONObject userData = new JSONObject(response);
                    Log.d("Server", response);

                    if (userData.length() == 0) {
                        returnedUser = null;
                    } else {
                        String uid = userData.getString("user_id");
                        String dob = userData.getString("dob");
                        String date_created = userData.getString("date_created");

                        returnedUser = new User(uid, user.username, user.password, dob, date_created);
                    }
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("Server ", e.getMessage());
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Log.e("Server ", error.getMessage());
            }) {
                @NotNull
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("username", user.username);
                    data.put("password", user.password);
                    return data;
                }
            };
            requestQueue.add(stringRequest);

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressBar.setVisibility(View.GONE);
            userCallback.done(returnedUser);

            super.onPostExecute(returnedUser);

        }

    }
}
