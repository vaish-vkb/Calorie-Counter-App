package com.example.mycalories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DBAddFood extends AppCompatActivity {
    EditText foodet, calories;
    Button addb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dbadd_food);
        foodet = findViewById(R.id.foodet2);
        calories = findViewById(R.id.calorieset);
        addb = findViewById(R.id.addb);

        Intent i1 = getIntent();
        String food = i1.getStringExtra("FoodName");
        foodet.setText(food);
        addb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newfood = foodet.getText().toString();
                int cal = 0;
                if(!((calories.getText().toString()).equals("")))
                    cal = Integer.parseInt(calories.getText().toString());

                if(((calories.getText().toString()).equals(""))){
                    Toast.makeText(DBAddFood.this, "Please enter the calories", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent  i2 = new Intent();
                    i2.putExtra("Calories", cal);
                    setResult(3,i2);
                    finish();
                }
            }
        });
    }
}