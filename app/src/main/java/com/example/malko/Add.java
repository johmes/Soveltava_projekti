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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add extends AppCompatActivity  {

    private static final String PRODUCT_URL = "https://www.luvo.fi/androidApp/addProductJson.php";
    public static final String TAG = "Add";
    Toolbar toolbar;

    public ProgressBar progressBarAddView;
    protected String juomanNimi, yhteystiedot, kaupunginosa, kategoria, amount, productAdmin = "";
    public static List<Product> productList;
    public JSONObject products;
    public Product product;

    public Add() {
/*        this.juomanNimi = "";
        this.yhteystiedot = "";
        this.kategoria = "";
        this.kaupunginosa = "";
        this.amount = "";*/
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

    public String getKategoria() { return kategoria; }

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

/*
        kategoriaSpinner.setOnItemSelectedListener(this);
        kaupunginosaSpinner.setOnItemSelectedListener(this);
        amountSpinnner.setOnItemSelectedListener(this);
*/

        progressBarAddView = findViewById(R.id.progressBar_addView);

        User user = session.getLoggedInUser();

        // INPUT
        nimiEditText = findViewById(R.id.nimiEditText);
        yhteystiedotEditText = findViewById(R.id.yhteystiedotText);
        productAdmin = user.getUid();

        // SUBMIT BUTTON
        lahetaButton = findViewById(R.id.lahetaButton);
        lahetaButton.setOnClickListener(v -> addProduct());

        // Set focus on nimi
        nimiEditText.requestFocus();


        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.add); //menu_navigation.xml


        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.profile) {
                startActivity(new Intent(this,
                        com.example.malko.ui.profile.profileActivity.class));
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

/*    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.editKategoriaSpinner) {
            setKategoria(kategoriaSpinner.getSelectedItem().toString().trim());
        } else if (parent.getId() == R.id.editkKaupunginosaSpinner) {
            setKaupunginosa(kaupunginosaSpinner.getSelectedItem().toString().trim());
        } else if (parent.getId() == R.id.editAmountSpinner) {
            //amount = parent.getItemAtPosition(position).toString().trim();
            setAmount(amountSpinnner.getSelectedItem().toString().trim());
        }
    }*/

/*    @Override
    public void onNothingSelected(AdapterView<?> parent) {}*/


    private void showToast() {
        Toast.makeText(Add.this, "Submitted", Toast.LENGTH_SHORT).show();
    }

    private String uniqueId() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;
        SecureRandom random = new SecureRandom();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void addProduct() {
        juomanNimi = nimiEditText.getText().toString().trim();
        yhteystiedot = yhteystiedotEditText.getText().toString().trim();
        kategoria = kategoriaSpinner.getSelectedItem().toString().trim();
        kaupunginosa = kaupunginosaSpinner.getSelectedItem().toString().trim();
        amount = amountSpinnner.getSelectedItem().toString().trim();

        progressBarAddView.setVisibility(View.VISIBLE);

        if (productAdmin != null) {
            if (amount != null || kategoria != null || kaupunginosa != null || juomanNimi != null || yhteystiedot != null) {

                requestQueue = Volley.newRequestQueue(this);
                stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                        response -> {
                            try {
                                products = new JSONObject(response);

                            } catch (Exception e) {
                                e.printStackTrace();
                                showErrorMessage(response);
                                progressBarAddView.setVisibility(View.GONE);
                            }
                            if (products != null) {
                                if (products.length() == 0) {
                                    Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
                                    progressBarAddView.setVisibility(View.GONE);
                                } else {
                                    progressBarAddView.setVisibility(View.GONE);
                                    Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Add.this, Add.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }, error -> {
                    progressBarAddView.setVisibility(View.GONE);
                    error.printStackTrace();
                    showErrorMessage("Something wrong happened...");
                }) {
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("p_id", uniqueId());
                        data.put("name", getJuomanNimi());
                        data.put("category", kategoria);
                        data.put("admin", getProductAdmin());
                        data.put("location", kaupunginosa);
                        data.put("amount", amount);
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