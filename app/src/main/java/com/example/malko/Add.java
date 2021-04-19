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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String PRODUCT_URL = "https://www.luvo.fi/androidApp/addProduct.php";
    public static final String TAG = "Add";

    public ProgressBar progressBarAddView;
    protected String juomanNimi, yhteystiedot, kaupunginosa, kategoria, amount, productAdmin;

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

    public String getAmount() {
        return amount;
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.kategoriat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterKaupunginosat = ArrayAdapter.createFromResource(this,R.array.kaupunginosat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterAmount = ArrayAdapter.createFromResource(this,R.array.amount, android.R.layout.simple_spinner_item);

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
        juomanNimi = "";
        yhteystiedot = "";
        kategoria = "Olut";
        kaupunginosa = "keskusta";
        amount = "1";
        productAdmin = user.getUid();

        // SUBMIT BUTTON
        lahetaButton = findViewById(R.id.lahetaButton);
        lahetaButton.setOnClickListener(this::addProduct);




        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.add); //menu_navigation.xml


        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.settings) {
                startActivity(new Intent(this,
                        Preference.class));
                overridePendingTransition(0,0);
                return true;
            } else if (menuItem.getItemId() == R.id.home) {
                startActivity(new Intent(this,
                        MainActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else return menuItem.getItemId() == R.id.add;
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.kategoriaSpinner:
                kategoria = parent.getItemAtPosition(position).toString().trim();

            case R.id.kaupunginosaSpinner:
                kaupunginosa = parent.getItemAtPosition(position).toString().trim();
            case R.id.amountSpinner:
                amount = parent.getItemAtPosition(position).toString().trim();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showToast(){
        Toast.makeText(Add.this, "Submitted", Toast.LENGTH_SHORT).show();
    }


    private void addProduct(View view) {
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

                        }, error -> {
                    progressBarAddView.setVisibility(View.GONE);
                    Log.e(TAG, error.getMessage());
                    showErrorMessage("Something goody happened...");
                }){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("name", getJuomanNimi());
                        data.put("category", getKategoria());
                        data.put("admin", getProductAdmin());
                        data.put("location", getKaupunginosa());
                        data.put("amount", getAmount());
                        data.put("description", getYhteystiedot());
                        return data;
                    }
                };
                // Set the tag on the request.
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
    private void showErrorMessage (String message) {
        progressBarAddView.setVisibility(View.GONE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Add.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}