package com.example.malko;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class profile2 extends AppCompatActivity {
    private Button preferenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        //buttoni
        preferenceButton = findViewById(R.id.preferenceButton);
        preferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferences();
            }
        });

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.bottomNavigationView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home Selected

        bottomNavigationView.setSelectedItemId(R.id.settings); //menu_navigation.xml

        // Perform itemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch ((menuItem.getItemId())){
                    case R.id.settings:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(),
                                Add.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }
    public void openPreferences(){
        Intent intent = new Intent(this, )
    }
}