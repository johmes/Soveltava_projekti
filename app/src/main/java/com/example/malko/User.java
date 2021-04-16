package com.example.malko;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.malko.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static String uid;
    private String URL = "https://www.luvo.fi/androidApp/getUserData.php";
    private String username;

    public User(String username) {
        uid = "";
        this.username = username;
        getUserData();
    }

    public static String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        User.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void getUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray userData = new JSONArray(response);

                    if (userData.length() == 0) {
                        Log.e("User getUserData: ", "Something went wrong...");
                    } else {
                        for (int i = 0; i < userData.length(); i++) {
                            JSONObject userObject = userData.getJSONObject(i);

                            String uid = userObject.getString("user_id");
                            /*String username = userObject.getString("username");*/

                            setUid(uid);
                            Log.d("User getUserData: ", getUid());
                        }

                    }

                } catch (JSONException e) {
                    Log.e("User getUserData: ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("User getUserData: ", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("username", getUsername());
                return data;
            }
        };
    }

}
