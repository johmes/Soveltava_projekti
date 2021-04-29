package com.example.malko;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String PRODUCT_URL = "https://www.luvo.fi/androidApp/addProduct.php";
    public static final String TAG = "Add";
    Toolbar toolbar;

    public ProgressBar progressBarAddView;
    protected String juomanNimi, yhteystiedot, kaupunginosa, kategoria, amount, productAdmin;
    public static List<Product> productList;
    public JSONArray products;
    public Product product;

    public Add() {
        this.juomanNimi = "";
        this.yhteystiedot = "";
        this.kategoria = "Olut";
        this.kaupunginosa = "keskusta";
        this.amount = "1";
    }

    public String getJuomanNimi() {
        return juomanNimi;
    }

    public String getYhteystiedot() {
        return yhteystiedot;
    }

    public String getKaupunginosa() {
        return kaupunginosa;
    }

    public String getKategoria() {
        return kategoria;
    }

    public String getProductAdmin() {
        return productAdmin;
    }

    public String getAmount() { return amount; }

    public void setJuomanNimi(String juomanNimi) {
        this.juomanNimi = juomanNimi;
    }

    public void setYhteystiedot(String yhteystiedot) {
        this.yhteystiedot = yhteystiedot;
    }

    public void setKaupunginosa(String kaupunginosa) {
        this.kaupunginosa = kaupunginosa;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    RequestQueue requestQueue;
    StringRequest stringRequest;
    Session session;

    EditText nimiEditText;
    EditText yhteystiedotEditText;
    Spinner kaupunginosaSpinner;
    Spinner kategoriaSpinner;
    Spinner amountSpinnner;
    Button lahetaButton;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);



        // SPINNERIT

        kategoriaSpinner = findViewById(R.id.kategoriaSpinner);
        kaupunginosaSpinner = findViewById(R.id.kaupunginosaSpinner);
        amountSpinnner = findViewById(R.id.amountSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.kategoriat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterKaupunginosat = ArrayAdapter.createFromResource(this, R.array.kaupunginosat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterAmount = ArrayAdapter.createFromResource(this, R.array.amount, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterKaupunginosat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAmount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        session = new Session(this);

        kategoriaSpinner.setAdapter(adapter);
        kaupunginosaSpinner.setAdapter(adapterKaupunginosat);
        amountSpinnner.setAdapter(adapterAmount);

        kategoriaSpinner.setOnItemSelectedListener(this);
        kaupunginosaSpinner.setOnItemSelectedListener(this);
        amountSpinnner.setOnItemSelectedListener(this);

        progressBarAddView = findViewById(R.id.progressBar_addView);

        User user = session.getLoggedInUser();

        // INPUT
        nimiEditText = findViewById(R.id.nimiEditText);
        yhteystiedotEditText = findViewById(R.id.yhteystiedotEditText);
        productAdmin = user.getUid();

        // SUBMIT BUTTON
        lahetaButton = findViewById(R.id.lahetaButton);
        lahetaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        // Set focus on nimi
        nimiEditText.requestFocus();


        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.add); //menu_navigation.xml


        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.settings) {
                startActivity(new Intent(this,
                        Preference.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.home) {
                startActivity(new Intent(this,
                        MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return menuItem.getItemId() == R.id.add;
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.kategoriaSpinner) {
            setKategoria(parent.getItemAtPosition(position).toString().trim());
        } else if (parent.getId() == R.id.kaupunginosaSpinner) {
            setKaupunginosa(parent.getItemAtPosition(position).toString().trim());
        } else if (parent.getId() == R.id.amountSpinner) {
            setAmount(parent.getItemAtPosition(position).toString().trim());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void showToast() {
        Toast.makeText(Add.this, "Submitted", Toast.LENGTH_SHORT).show();
    }

    private String uniqueId() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;
        SecureRandom random = new SecureRandom();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private void addProduct() {
        juomanNimi = nimiEditText.getText().toString().trim();
        yhteystiedot = yhteystiedotEditText.getText().toString().trim();

        progressBarAddView.setVisibility(View.VISIBLE);

        if (productAdmin != null) {
            if (amount != null || kategoria != null || kaupunginosa != null || juomanNimi != null || yhteystiedot != null) {

                requestQueue = Volley.newRequestQueue(this);
                stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                        response -> {
                            if (response.contains("Success")) {
                                Log.d(TAG, "Product submitted");
                                showToast();
                                Log.d("Details", getJuomanNimi() + ", " + getKategoria() + ", " + getKaupunginosa() + ", " + getAmount() + ", " + getYhteystiedot() + ", " + getProductAdmin());
                                progressBarAddView.setVisibility(View.GONE);
                                Intent intent = new Intent(Add.this, Add.class);
                                startActivity(intent);
                                finish();

                            } else {
                                progressBarAddView.setVisibility(View.GONE);
                                Log.d(TAG, response);
                                showErrorMessage(response.trim());
                            }
/*                            try {
                                products = new JSONArray(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Add error", response);
                                progressBarAddView.setVisibility(View.GONE);
                            }
                            if (products != null) {
                                if (products.length() == 0) {
                                    Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
                                    progressBarAddView.setVisibility(View.GONE);
                                } else {
                                    for (int i = 0; i < products.length(); i++) {
                                        JSONObject productObject;
                                        try {
                                            productObject = products.getJSONObject(i);

                                            String pid = productObject.getString("p_id");
                                            String name = productObject.getString("name");
                                            String category = productObject.getString("category");
                                            String admin = productObject.getString("admin");
                                            String location = productObject.getString("location");
                                            String amount = productObject.getString("amount");
                                            String date = productObject.getString("date_created");
                                            String description = productObject.getString("description");
                                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            productList = null;
                                            e.printStackTrace();
                                            progressBarAddView.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }*/

                        }, error -> {
                    progressBarAddView.setVisibility(View.GONE);
                    error.printStackTrace();
                    showErrorMessage("Something wrong happened...");
                }) {
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> data = new HashMap<>();
                        /*data.put("p_id", uniqueId());*/
                        data.put("name", getJuomanNimi());
                        data.put("category", getKategoria());
                        data.put("admin", getProductAdmin());
                        data.put("location", getKaupunginosa());
                        data.put("amount", getAmount());
                        data.put("description", getYhteystiedot());
                        return data;
                    }
                };
                stringRequest.setTag(TAG);
                requestQueue.add(stringRequest);
            } else {
                progressBarAddView.setVisibility(View.GONE);
                showErrorMessage("Fill in all forms");
            }
        } else {
            Log.e(TAG, "Product admin is null");
            showErrorMessage("Something went wrong, try again later...");
        }

    }

    private void showErrorMessage(String message) {
        progressBarAddView.setVisibility(View.GONE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Add.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}