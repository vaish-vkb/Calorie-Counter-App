package com.example.mycalories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TotalCalories extends AppCompatActivity {
    ProgressBar p;
    EditText totalcalories, availablecalories, totalcaloriesconsumed;
    SharedPreferences calories;
    int availcalories;
    Button add;
    Button search;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_total_calories);
        Intent i1 = getIntent();
        int bmr = i1.getIntExtra("BMR", 0);
        calories = getSharedPreferences("Calories", MODE_PRIVATE);
        totalcaloriesconsumed = findViewById(R.id.consumedet);
        search = findViewById(R.id.searchfood);

        db = openOrCreateDatabase("CALORIES", MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS FOODCALORIES");
        db.execSQL("CREATE TABLE IF NOT EXISTS FOODCALORIES(FOODITEM VARCHAR(45) PRIMARY KEY, CALORIES INT)");

        InputStream inStream = TotalCalories.this.getResources().openRawResource(R.raw.calories_csv);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length != 6) {
                    continue;
                }
                ContentValues cv = new ContentValues(3);
                cv.put("FOODITEM", columns[5].trim());
                cv.put("CALORIES", columns[3].trim());

                db.insert("FOODCALORIES", null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        int totalconsumed = calories.getInt("TotalConsumed",0);
        totalcaloriesconsumed.setText(totalconsumed+"");
        totalcalories = findViewById(R.id.totalcalorieset);
        availablecalories = findViewById(R.id.caloriesavailableet);
        add = findViewById(R.id.addfood);

        totalcalories.setText(bmr+"");
        p = findViewById(R.id.progressBar);
        p.setMax(bmr);

        availcalories = calories.getInt("Available", bmr);
        p.setProgress(availcalories);
        availablecalories.setText(availcalories+"");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int avail = Integer.parseInt(availablecalories.getText().toString());
                Intent i2 = new Intent(TotalCalories.this, AddFood.class);
                i2.putExtra("AvailableCalories", avail);
                startActivityForResult(i2, 1);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int avail = Integer.parseInt(availablecalories.getText().toString());
                Intent i3 = new Intent(TotalCalories.this, SearchFood.class);
                i3.putExtra("AvailableCalories", avail);
                startActivityForResult(i3, 6);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        availablecalories = findViewById(R.id.caloriesavailableet);
        int newcal = 0;
        int add = 0;
        if(resultCode==4 || resultCode==5){
            if(requestCode==1 && resultCode==4){
                newcal = data.getIntExtra("availcalories", 0);
                add = data.getIntExtra("totalconsumed",0);
            }
            if(requestCode==1 && resultCode==5){
                newcal = data.getIntExtra("Availcalories", 0);
            }
            int total = 0;
            if(!totalcaloriesconsumed.getText().toString().equals("")) {
                total = Integer.parseInt(totalcaloriesconsumed.getText().toString());
            }
            total = total + add;
            SharedPreferences.Editor ed = calories.edit();
            ed.putInt("Available", newcal);
            ed.putInt("TotalConsumed", total);
            ed.commit();
            p.setProgress(newcal);
            availablecalories.setText(newcal+"");
            totalcaloriesconsumed.setText(total+"");

            totalcalories = findViewById(R.id.totalcalorieset);
            int totalcal = Integer.parseInt(totalcalories.getText().toString());
            if(total > totalcal ){
                int diff = total - totalcal;
                AlertDialog.Builder ad = new AlertDialog.Builder(TotalCalories.this);
                ad.setTitle("DISCLAIMER");
                ad.setMessage("You have exceeded you recommended daily calorie intake by " + diff  + "kcal");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog a = ad.create();
                a.show();
            }
        }
        if(resultCode==7){
            newcal = data.getIntExtra("AvailableCalories", 0);
            p.setProgress(newcal);
        }
    }
}