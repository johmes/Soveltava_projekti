package com.example.malko;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    //https://www.luvo.fi/androidApp/api_products.php
    private static final String PRODUCT_URL = "http://192.168.43.117/api_products.php";
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final String TAG = "Task";
    public static LatLng latLngUser = new LatLng(0,0);
    public List<LatLng> locationArrayList;
    public List<String> locationNameList;
    public List<Product> productList;
    public ProgressBar progressBarRecycler;
    public ProgressBar progressBarMap;
    public double distanceTo = 0.0;

    //Init variable
    GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    MarkerOptions markerOptions;
    Marker yourMarker;
    android.widget.SearchView searchView;
    ImageView closeButton;
    ImageView expandView;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        recyclerView = findViewById(R.id.main_recyclerview);
        searchView = findViewById(R.id.sv_map);
        closeButton = findViewById(R.id.close_view);
        expandView = findViewById(R.id.expand_view);
        progressBarRecycler = findViewById(R.id.progressBar_recyclerView);
        progressBarMap = findViewById(R.id.progressBar_map);
        SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.pullToRefresh);

        searchView.setBackgroundColor(Color.WHITE);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        //Init fused location
        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        supportMapFragment.getMapAsync(MainActivity.this);

        productList = new ArrayList<>();
        locationArrayList = new ArrayList<>();
        locationNameList = new ArrayList<>();


        getCurrentLocation();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    Log.d("Location", location);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        closeButton.setOnClickListener(v -> {
            mySwipeRefreshLayout.getLayoutParams().height = 240;
            closeButton.setVisibility(View.GONE);
            expandView.setVisibility(View.VISIBLE);
        });
        expandView.setOnClickListener(v ->  {
            mySwipeRefreshLayout.getLayoutParams().height = 800;
            closeButton.setVisibility(View.VISIBLE);
            expandView.setVisibility(View.GONE);
        });

        String json;
        try {
            InputStream input = getAssets().open("kaupunginosien_koordinaatit.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

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
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadProducts();
                        Log.i("LOG", "onRefresh called from SwipeRefreshLayout");
                    }
                }
        );


    }
    private void loadProducts() {
        progressBarRecycler.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                response -> {
                    try {
                        JSONArray products = new JSONArray(response);

                        for (int i = 0; i<products.length(); i++) {
                            JSONObject productObject = products.getJSONObject(i);

                            String pid = productObject.getString("p_id");
                            String name = productObject.getString("name");
                            String category = productObject.getString("category");
                            String admin = productObject.getString("admin");
                            String location = productObject.getString("location");
                            int amount = productObject.getInt("amount");
                            String date = productObject.getString("date_created");
                            String description = productObject.getString("description");

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("Error", e.getMessage());
                    }
                }, error -> {
                    Log.e("Error", error.getMessage());
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBarRecycler.setVisibility(View.GONE);

                });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest);
    }



    private void getCurrentLocation() {
        // Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // When permission denied
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE_LOCATION);
        } else {
            // When permission granted
            // Init task location
            progressBarRecycler.setVisibility(View.VISIBLE);
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // When success
                    if (location != null) {
                        // Sync map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

                            @SuppressLint("MissingPermission")
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                progressBarRecycler.setVisibility(View.GONE);
                                googleMap.setMyLocationEnabled(true);
                                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap = googleMap;



                                latLngUser = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngUser, 14));

                                mMap = googleMap;
                                markerOptions = new MarkerOptions();

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

                                //markerOptions.position(location);
                                //markerOptions.title(product.getLocation() + " " + distanceTo + " km");
                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(R.drawable.ic_drink_marker));
                                //mMap.addMarker(markerOptions);
                                // below lin is use to zoom our camera on map.
/*                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
                                    // below line is use to move our camera to the specific location.
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));*/


                            }
                        });
                    }
                }
            });

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
/*    public class Marker {
        public Marker(  ) {

        }
    }*/

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        *//*super.onRequestPermissionsResult(requestCode, permissions, grantResults);*//*
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When permission granted
                // Call method
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    // Open fragment
/*        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();*/

    // Open fragment
        /*BottomNavigationView navView = findViewById(R.id.nav_view);
         Passing each menu ID as a set of Ids because each
         menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
               R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
               .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/

}