package com.example.malko;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.NumericShaper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private final String id;
    private final String title;
    private final String category;
    private final String admin;
    private final String location;
    private final String amount;
    private final String date;
    private final String description;
    private final List<LatLng> locationArrayList = MainActivity.locationArrayList;
    private final List<String> locationNameList = MainActivity.locationNameList;
    private final List<Product> productList = MainActivity.productList;
    private LatLng nameLocation;
    private double distanceTo = 0.0;
    private LatLng latLngUser = MainActivity.latLngUser;
    private boolean expandable;


    public Product(String id, String title, String category, String admin, String location, String amount, String date, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.admin = admin;
        this.location = location;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.expandable = false;

    }


    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
    public String getId() { return id; }


    public String getProductTitle() { return title; }

    public String getCategory() { return category; }

    public String getAdmin() { return admin; }

    public String getLocation() { return location; }

    public String getAmount() { return amount;}

    public String getDate() { return date; }

    public String getDescription() { return description; }

    public LatLng getNameLocation() {
        int nameIndex = locationNameList.indexOf(this.getLocation());
        return locationArrayList.get(nameIndex);
    }

    public Double getDistanceTo() {
        LatLng location = getNameLocation();

        try {
            distanceTo = Math.round(calculateDistance(latLngUser, location)* 10.0) / 10.0;
        } catch (Exception e) {
            distanceTo = 0.0;
        }
        Log.d("Location", latLngUser.toString());
        return distanceTo;
    }

    public double calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371; // radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.parseInt(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.parseInt(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}

