package com.example.malko;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.malko.ui.login.LoginActivity;
import com.example.malko.ui.signup.SignupActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.malko.Session.KEY_LOGIN;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String PRODUCT_URL = "https://www.luvo.fi/androidApp/api_products.php?r=products";

    private static final int REQUEST_CODE_LOCATION = 1;
    public static LatLng latLngUser = new LatLng(0,0);
    public static List<LatLng> locationArrayList;
    public static List<String> locationNameList;
    public static List<Product> productList;
    public ProgressBar progressBarRecycler;
    public ProgressBar progressBarMap;
    public double distanceTo = 0.0;

    // Request products stuff
    public static final String TAG = "MainActivity";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    Database mDatabaseHelper;
    SwipeRefreshLayout mySwipeRefreshLayout;
    User user = LoginActivity.user;
    Session session;

    //Init variable
    GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    MarkerOptions markerOptions;
    Marker yourMarker;
    android.widget.SearchView searchView;
    ImageView closeButton;
    ImageView expandView;
    ImageView noResult;
    TextView usernameGreeting;
    RecyclerView recyclerView;
    ConstraintLayout recyclerViewHeader;
    boolean expanded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expanded = false;

        // Find views
        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerViewHeader = findViewById(R.id.recyclerview_header_border);

        searchView = findViewById(R.id.sv_map);
        closeButton = findViewById(R.id.close_view);
        expandView = findViewById(R.id.expand_view);
        usernameGreeting = findViewById(R.id.username_view);

        progressBarRecycler = findViewById(R.id.progressBar_recyclerView);
        progressBarMap = findViewById(R.id.progressBar_map);
        noResult = findViewById(R.id.no_result);
        mySwipeRefreshLayout = findViewById(R.id.pullToRefresh);
        mDatabaseHelper = new Database(this);

        session = new Session(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(MainActivity.this);

        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Init fused location
        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);



        productList = new ArrayList<>();
        locationArrayList = new ArrayList<>();
        locationNameList = new ArrayList<>();

        getCurrentLocation();
        loadProducts();

        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch ((menuItem.getItemId())){
                case R.id.settings:
                    startActivity(new Intent(this,
                            Preference.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.home:
                    return true;
                case R.id.add:
                    startActivity(new Intent(this,
                            Add.class));
                    overridePendingTransition(0,0);
                    return true;

            }
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Check if there are results in list.
                    // Check if addressList is correct (it contains something)
                    assert addressList != null;
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        Log.d("Location", location);
                    } else {
                        toastMessage("Could not find " + location);
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });


        String json;
        try {
            InputStream input = getAssets().open("kaupunginosien_koordinaatit.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            if (input.read(buffer) == 0) {
                toastMessage("Error opening json file");
            }

            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                double latitude = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                String place = obj.getString("name");

                LatLng latLng = new LatLng(latitude, longitude);

                locationArrayList.add(latLng);
                locationNameList.add(place);
            }
            Log.d("List", locationArrayList.toString());
        } catch (IOException | JSONException e) {
            Log.e("JSONError", String.valueOf(e));
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    loadProducts();
                    Log.i("LOG", "onRefresh called from SwipeRefreshLayout");
                }
        );

        closeButton.setOnClickListener(v -> {
            mySwipeRefreshLayout.getLayoutParams().height = 260;
            closeButton.setVisibility(View.GONE);
            expandView.setVisibility(View.VISIBLE);
        });
        expandView.setOnClickListener(v -> {
            mySwipeRefreshLayout.getLayoutParams().height = 800;
            closeButton.setVisibility(View.VISIBLE);
            expandView.setVisibility(View.GONE);
        });
        recyclerViewHeader.setOnClickListener(v -> {
            if (expanded) {
                mySwipeRefreshLayout.getLayoutParams().height = 260;
                closeButton.setVisibility(View.GONE);
                expandView.setVisibility(View.VISIBLE);
                expanded = false;
            } else {
                mySwipeRefreshLayout.getLayoutParams().height = 800;
                closeButton.setVisibility(View.VISIBLE);
                expandView.setVisibility(View.GONE);
                expanded = true;
            }
        });

    }
        /** EI toiminut jostain syystä?*/
/*    public void onClick(View v) {
        switch(v.getId()){
            case R.id.close_view: *//** collapses main recycler view *//*
                mySwipeRefreshLayout.getLayoutParams().height = 260;
                closeButton.setVisibility(View.GONE);
                expandView.setVisibility(View.VISIBLE);
                break;

            case R.id.expand_view: *//** Expands main recyclerview *//*
                mySwipeRefreshLayout.getLayoutParams().height = 800;
                closeButton.setVisibility(View.VISIBLE);
                expandView.setVisibility(View.GONE);
                break;
            case R.id.recyclerview_header_border: *//** Expands main recyclerview by clicking the header*//*
                if (expanded) {
                    mySwipeRefreshLayout.getLayoutParams().height = 260;
                    closeButton.setVisibility(View.GONE);
                    expandView.setVisibility(View.VISIBLE);
                    expanded = false;
                } else {
                    mySwipeRefreshLayout.getLayoutParams().height = 800;
                    closeButton.setVisibility(View.VISIBLE);
                    expandView.setVisibility(View.GONE);
                    expanded = true;
                }
                break;
        }
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {}

    @Override
    protected void onStart() {
        super.onStart();
        if (!authenticate()) {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            displayUserDetails();
        }
    }

    // check if user is logged in
    private boolean authenticate() {
        return session.getUserloggedIn();
    }

    private void displayUserDetails() {
        User user = session.getLoggedInUser();
        usernameGreeting.setText(String.format("Hello, %s \uD83D\uDE0A", user.getUsername()));
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void loadProducts() {
        progressBarRecycler.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                response -> {
                    try {
                        JSONArray products = new JSONArray(response);
                        if (products.length() == 0) {
                            noResult.setVisibility(View.VISIBLE);
                            toastMessage("No products in this area");

                        } else {
                            for (int i = 0; i < products.length(); i++) {
                                noResult.setVisibility(View.GONE);
                                JSONObject productObject = products.getJSONObject(i);

                                String pid = productObject.getString("p_id");
                                String name = productObject.getString("name");
                                String category = productObject.getString("category");
                                String admin = productObject.getString("admin");
                                String location = productObject.getString("location");
                                String amount = productObject.getString("amount");
                                String date = productObject.getString("date_created");
                                String description = productObject.getString("description");
                                Log.d("Date", date);

                                Product product = new Product(pid, name, category, admin, location, amount, date, description);

                                try {
                                    productList.add(product);
                                    progressBarRecycler.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    Log.d("Error", e.getMessage());
                                    e.printStackTrace();
                                }

                            }
                            Collections.reverse(productList);
                            MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(MainActivity.this, productList);
                            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(llm);
                            recyclerView.setAdapter(mainRecyclerAdapter);
                        }

                    } catch (JSONException e) {
                        progressBarRecycler.setVisibility(View.GONE);
                        e.printStackTrace();
                        Log.e("JSON error", e.getMessage());
                        onStop();
                    }

                }, error -> {
                    Log.e("Error", error.getMessage());
                    toastMessage("That didn't work");
                    onStop();
                });
        //10000 is the time in milliseconds adn is equal to 10 sec
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        requestQueue.add(stringRequest);
    }

    private void getCurrentLocation() {
        progressBarRecycler.setVisibility(View.VISIBLE);
        // Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // When permission granted
            // Init task location
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                // When success
                if (location != null) {
                    // Sync map
                    supportMapFragment.getMapAsync(googleMap -> {

                        progressBarRecycler.setVisibility(View.GONE);
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                        latLngUser = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngUser, 14));

                        markerOptions = new MarkerOptions();
                        mMap = googleMap;

                        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        // position on right bottom
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        rlp.setMargins(0, 200, 10, 0);

                        for (int i = 0; i < productList.size(); i++) {
                            Product product = productList.get(i);
                            markerOptions.position(product.getNameLocation());
                            markerOptions.title(product.getLocation() + " " + product.getDistanceTo() + " km");
                            mMap.addMarker(markerOptions);
                        }

                    });
                }
            });

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("You need to grant this permission for best experience")
                        .setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE_LOCATION))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_LOCATION);
            }
        }


    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}