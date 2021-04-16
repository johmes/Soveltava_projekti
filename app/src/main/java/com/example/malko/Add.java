package com.example.malko;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String juomanNimi, yhteystiedot, postinumero, kategoria;

    EditText nimiEditText;
    EditText yhteystiedotEditText;
    EditText postinumeroEditText;
    Button lahetaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Spinner kategoriaSpinner = findViewById(R.id.kategoriaSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.kategoriat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategoriaSpinner.setAdapter(adapter);
        kategoriaSpinner.setOnItemSelectedListener(this);

        nimiEditText = (EditText) findViewById(R.id.nimiEditText);
        yhteystiedotEditText = (EditText) findViewById(R.id.yhteystiedotEditText);
        postinumeroEditText = (EditText) findViewById(R.id.postinumeroEditText);

        lahetaButton = (Button) findViewById(R.id.lahetaButton);
        lahetaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                juomanNimi = nimiEditText.getText().toString();
                yhteystiedot = yhteystiedotEditText.getText().toString();
                postinumero = postinumeroEditText.getText().toString();


                showToast(kategoria);
                showToast(juomanNimi);
                showToast(yhteystiedot);
                showToast(postinumero);
            }
        });




        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected

        bottomNavigationView.setSelectedItemId(R.id.add); //menu_navigation.xml

        // Perform itemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        kategoria = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),kategoria , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void showToast(String text){
        Toast.makeText(Add.this, text, Toast.LENGTH_SHORT).show();
    }

}