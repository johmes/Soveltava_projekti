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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String PRODUCT_URL = "https://www.luvo.fi/androidApp/addProduct.php";
    public static final String TAG = "Add";
    public ProgressBar progressBarAddView;
    protected String juomanNimi, yhteystiedot, kaupunginosa, kategoria, amount,productAdmin;


    public String getYhteystiedot() {
        return yhteystiedot;
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

    public void setProductAdmin(String productAdmin) {
        this.productAdmin = productAdmin;
    }

    public void setJuomanNimi(String juomanNimi) {
        this.juomanNimi = juomanNimi;
    }

    public String getKaupunginosa() {
        return kaupunginosa;
    }

    public String getKategoria() {
        return kategoria;
    }

    public String getAmount() {
        return amount;
    }

    public String getProductAdmin() {
        return productAdmin;
    }

    RequestQueue requestQueue;
    StringRequest stringRequest;

    EditText nimiEditText;
    EditText yhteystiedotEditText;
    Spinner kaupunginosaSpinner;
    Spinner kategoriaSpinner;
    Spinner amountSpinner;
    Button lahetaButton;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // SPINNERIT

        kategoriaSpinner = findViewById(R.id.kategoriaSpinner);
        kaupunginosaSpinner = findViewById(R.id.kaupunginosaSpinner);
        amountSpinner = findViewById(R.id.amountSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.kategoriat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterKaupunginosat = ArrayAdapter.createFromResource(this,R.array.kaupunginosat, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterAmount = ArrayAdapter.createFromResource(this,R.array.amount, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterKaupunginosat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAmount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        kategoriaSpinner.setAdapter(adapter);
        kaupunginosaSpinner.setAdapter(adapterKaupunginosat);
        amountSpinner.setAdapter(adapterAmount);

        kategoriaSpinner.setOnItemSelectedListener(this);
        kaupunginosaSpinner.setOnItemSelectedListener(this);
        amountSpinner.setOnItemSelectedListener(this);

        progressBarAddView = findViewById(R.id.progressBar_addView);

        // INPUT
        nimiEditText = findViewById(R.id.nimiEditText);
        yhteystiedotEditText = findViewById(R.id.yhteystiedotEditText);
        juomanNimi = "";
        yhteystiedot = "";
        kategoria = "";
        kaupunginosa = "";
        amount = "";
        productAdmin = "ar6f54jklng90";

        // SUBMIT BUTTON
        lahetaButton = findViewById(R.id.lahetaButton);
        lahetaButton.setOnClickListener(this::addProduct);




        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.add); //menu_navigation.xml


        // Perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch ((menuItem.getItemId())){
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(),
                            Preference.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.add:

                    return true;

            }
            return false;
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.kategoriaSpinner:
                kategoria = parent.getItemAtPosition(position).toString();

            case R.id.kaupunginosaSpinner:
                kaupunginosa = parent.getItemAtPosition(position).toString();
            case R.id.amountSpinner:
                amount = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addProduct(View view) {

        juomanNimi = nimiEditText.getText().toString();
        yhteystiedot = yhteystiedotEditText.getText().toString();

        progressBarAddView.setVisibility(View.VISIBLE);

        if (!productAdmin.equals("")) {

            if (!juomanNimi.equals("") && !amount.equals("") && !kategoria.equals("")
                    && !kaupunginosa.equals("") && !yhteystiedot.equals("")) {

                requestQueue = Volley.newRequestQueue(this);
                stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                        response -> {
                            if (response.contains("Success")) {
                                Log.d(TAG, "Product submitted");
                                showToast("Submitted");
                                progressBarAddView.setVisibility(View.GONE);
                                Intent intent = new Intent(Add.this, Add.class);
                                startActivity(intent);
                                finish();

                            } else {
                                progressBarAddView.setVisibility(View.GONE);
                                Log.d(TAG, response);
                                showToast(response);
                            }

                        }, error -> {
                    progressBarAddView.setVisibility(View.GONE);
                    Log.e(TAG, error.getMessage());
                    showToast("Something goody happened...");

                }){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("name", juomanNimi);
                        data.put("category", kategoria);
                        data.put("admin", productAdmin);
                        data.put("location", kaupunginosa);
                        data.put("amount", amount);
                        data.put("description", yhteystiedot);
                        return data;
                    }
                };

                // Set the tag on the request.
                stringRequest.setTag(TAG);
                requestQueue.add(stringRequest);

            } else {
                progressBarAddView.setVisibility(View.GONE);
                Log.d("Details", juomanNimi + ", " + kategoria + ", " + kaupunginosa + ", " + yhteystiedot);
                showToast("Fill in all forms");
            }

        } else {
            Log.e(TAG, "Admin id is null.");
            showToast("An error occurred");
        }
    }

    public void showToast(String text){
        Toast.makeText(Add.this, text, Toast.LENGTH_SHORT).show();
    }
    public static ArrayList inputChekkaus(String juomanNimiTest, String yhteystiedot, String kaupunginosa,
                                          String kategoria, String amount, String productAdmin ){

        ArrayList<String> inputit = new ArrayList<String>();
        Add add = new Add();
        add.setJuomanNimi(juomanNimiTest);
        add.setYhteystiedot(yhteystiedot);
        add.setKategoria(kategoria);
        add.setAmount(amount);
        add.setKaupunginosa(kaupunginosa);
        add.setProductAdmin(productAdmin);

        inputit.add(add.getJuomanNimi());
        inputit.add(add.getYhteystiedot());
        inputit.add(add.getKaupunginosa());
        inputit.add(add.getKategoria());
        inputit.add(add.getAmount());
        inputit.add(add.getProductAdmin());



        return inputit  ;
    }

    public static String juomanNimiChekkaus(String juomanNimiTest){

        Add add = new Add();
        add.setJuomanNimi(juomanNimiTest);

        return add.getJuomanNimi();
    }
    public String getJuomanNimi() {
        return juomanNimi;
    }



}







