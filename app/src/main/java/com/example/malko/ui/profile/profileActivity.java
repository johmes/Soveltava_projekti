package com.example.malko.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.malko.Add;
import com.example.malko.MainActivity;
import com.example.malko.Preference;
import com.example.malko.Product;
import com.example.malko.R;
import com.example.malko.Session;
import com.example.malko.User;
import com.example.malko.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class profileActivity extends AppCompatActivity {
/*    public static final int ADD_PRODUCT_REQUEST = 1;
    public static final int EDIT_PRODUCT_REQUEST = 2;*/

    public static final String TAG = "profileActivity";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    ProgressBar progressBarRecycler;
    JSONArray products;
    Product product;
    private final String USER_PRODUCT_URL = "https://www.luvo.fi/androidApp/api_products.php";
    Session session;

    TextView usernameHeader;
    TextView errorHeader;
    TextView userJoinedHeader;
    Button editProfileButton;
    Button editButton;
    TextView addFirstHeader;
    Button addFirstButton;
    RecyclerView recyclerView;
    ProfileRecyclerAdapter profileRecyclerAdapter;
    User loggedInUser;
    public static List<Product> productList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new Session(this);

        productList = new ArrayList<>();

        loggedInUser = session.getLoggedInUser();
        progressBarRecycler = findViewById(R.id.progressBarProfile);
        usernameHeader = findViewById(R.id.username_header);
        userJoinedHeader = findViewById(R.id.user_joined_header);
        editProfileButton = findViewById(R.id.editProfileButton);
        editButton = findViewById(R.id.editButton);
        addFirstHeader = findViewById(R.id.add_first_product_header);
        addFirstButton = findViewById(R.id.start_now_button);
        errorHeader = findViewById(R.id.error_header);
        recyclerView = findViewById(R.id.userProductRecyclerView);

        usernameHeader.setText(loggedInUser.getUsername());

        String dayUserJoined = "Joined in " + loggedInUser.getDateCreated();
        userJoinedHeader.setText(dayUserJoined);

        errorHeader.setVisibility(View.GONE);

        editButton.setOnClickListener(v -> showErrorMessage("Edit products"));
        editProfileButton.setOnClickListener(v -> showErrorMessage("Edit Profile"));

        addFirstButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Add.class);
            startActivity(intent);
            finish();
        });

        getUserProducts();

/*        profileRecyclerAdapter.setOnProductLongClickListener(product -> {
            Intent intent = new Intent(profileActivity.this, com.example.malko.EditProductActivity.class);
            intent.putExtra(EditProductActivity.EXTRA_ID, product.getId());
            intent.putExtra(EditProductActivity.EXTRA_TITLE, product.getProductTitle());
            intent.putExtra(EditProductActivity.EXTRA_ADMIN, product.getAdmin());
            intent.putExtra(EditProductActivity.EXTRA_CATEGORY, product.getCategory());
            intent.putExtra(EditProductActivity.EXTRA_LOCATION, product.getCategory());
            intent.putExtra(EditProductActivity.EXTRA_DESCRIPTION, product.getDescription());
            intent.putExtra(EditProductActivity.EXTRA_AMOUNT, product.getAmount());
            startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
        });*/

        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.profile); //menu_navigation.xml


        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.add) {
                startActivity(new Intent(this,
                        Add.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.home) {
                startActivity(new Intent(this,
                        MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return menuItem.getItemId() == R.id.profile;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_top_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu) {

            // Redirect to login activity
            Intent intent = new Intent(this, Preference.class);
            startActivity(intent);
            finish();

            return true;
        } else if(item.getItemId() == R.id.logout_menu) {
            session.clearSession();
            session.setUserLoggedIn(false);

            // Redirect to login activity
            Intent intent = new Intent(profileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showErrorMessage (String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(profileActivity.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
    public void getUserProducts() {
        progressBarRecycler.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest = new StringRequest(Request.Method.POST, USER_PRODUCT_URL,
                response -> {
                    if (response.contains("No result")) {
                        progressBarRecycler.setVisibility(View.GONE);
                        addFirstHeader.setVisibility(View.VISIBLE);
                        addFirstButton.setVisibility(View.VISIBLE);
                        //toastMessage("You have no products added.");
                    } else {
                        try {
                            products = new JSONArray(response);
                        } catch (Exception e) {
                            progressBarRecycler.setVisibility(View.GONE);
                            errorHeader.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                        if (products != null) {
                            if (products.length() == 0) {
                                progressBarRecycler.setVisibility(View.GONE);
                                addFirstHeader.setVisibility(View.VISIBLE);
                                addFirstButton.setVisibility(View.VISIBLE);
                                //toastMessage("You have no products added.");

                            } else {
                                addFirstHeader.setVisibility(View.GONE);
                                addFirstButton.setVisibility(View.GONE);
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject productObject;
                                    try {
                                        productObject = products.getJSONObject(i);

                                        String pid = changeCharset(productObject.getString("p_id"));
                                        String name = changeCharset(productObject.getString("name"));
                                        String category = changeCharset(productObject.getString("category"));
                                        String admin = changeCharset(productObject.getString("admin"));
                                        String location = changeCharset(productObject.getString("location"));
                                        String amount = changeCharset(productObject.getString("amount"));
                                        String date = changeCharset(productObject.getString("date_created"));
                                        String description = changeCharset(productObject.getString("description"));

                                        product = new Product(pid, name, category, admin, location, amount, date, description);

                                    } catch (JSONException e) {
                                        productList = null;

                                        progressBarRecycler.setVisibility(View.GONE);
                                        errorHeader.setVisibility(View.VISIBLE);
                                        e.printStackTrace();
                                    }

                                    try {
                                        productList.add(product);

                                        Collections.reverse(productList);
                                        profileRecyclerAdapter = new ProfileRecyclerAdapter(this, productList);
                                        LinearLayoutManager llm = new LinearLayoutManager(profileActivity.this);
                                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                                        recyclerView.setLayoutManager(llm);
                                        recyclerView.setAdapter(profileRecyclerAdapter);

                                        progressBarRecycler.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        productList = null;
                                        progressBarRecycler.setVisibility(View.GONE);
                                        errorHeader.setVisibility(View.VISIBLE);
                                        e.printStackTrace();
                                    }

                                }

                            }

                        }

                    }


                }, error -> {
                    progressBarRecycler.setVisibility(View.GONE);
                    errorHeader.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                    toastMessage("Something went wrong...");
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("adminProducts", loggedInUser.getUid());
                return data;
            }
        };
        //10000 is the time in milliseconds adn is equal to 10 sec
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        requestQueue.add(stringRequest);
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String changeCharset(String string) {
        return new String(string.getBytes(ISO_8859_1), UTF_8);
    }
}
