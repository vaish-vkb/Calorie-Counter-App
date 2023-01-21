package com.example.mycalories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AddFood extends AppCompatActivity {
    Button search, back;
    EditText fooditem, quantity;
    SQLiteDatabase db;
    String food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_food);
        search = findViewById(R.id.searchb);
        back = findViewById(R.id.backb);
        fooditem = findViewById(R.id.foodet);
        quantity = findViewById(R.id.quantityet);
        db = openOrCreateDatabase("CALORIES", MODE_PRIVATE, null);

        Intent i1 = getIntent();
        int availcalories = i1.getIntExtra("AvailableCalories",0);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food = fooditem.getText().toString();
                String q = quantity.getText().toString();
                if (!food.equals("") && !q.equals("")) {
                    Float quant = Float.parseFloat(q);
                    Cursor c = db.rawQuery("SELECT * FROM FOODCALORIES WHERE FOODITEM = '" + food + "'", null);
                    if(c.getCount()==0){
                        AlertDialog.Builder ad = new AlertDialog.Builder(AddFood.this);
                        ad.setMessage("Sorry, this food item does not exist in our database. Would you like to add the item?");
                        ad.setTitle("Could not find item");
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent i2 = new Intent(AddFood.this, DBAddFood.class);
                                i2.putExtra("FoodName", food);
                                startActivityForResult(i2,2);
                            }
                        });
                        AlertDialog a = ad.create();
                        a.show();
                    }

                    else{
                        String foodname;
                        int calories;
                        c.moveToFirst();
                        do{
                            foodname = c.getString(0);
                            calories = c.getInt(1);
                        }while(c.moveToNext());

                        float totalcal = quant * calories;
                        AlertDialog.Builder ad = new AlertDialog.Builder(AddFood.this);
                        ad.setTitle("Information");
                        ad.setMessage("Do you want to add " + foodname + "\nTotal calories: " + totalcal);
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int newcal = 0;
                                if((availcalories - (int)totalcal) >= 0 ){
                                    newcal = availcalories - (int)totalcal;
                                }
                                Intent i3 = new Intent();
                                i3.putExtra("totalconsumed", (int) totalcal);
                                i3.putExtra("availcalories", newcal);
                                setResult(4,i3);
                                finish();
                            }
                        });
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog a = ad.create();
                        a.show();
                    }
                } else {
                    if(q.equals(""))
                        Toast.makeText(AddFood.this, "Please enter the quantity", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(AddFood.this, "Please enter the food item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i4 = new Intent();
                i4.putExtra("Availcalories", availcalories);
                setResult(5, i4);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==3){
            int cal =data.getIntExtra("Calories",0);
            db.execSQL("INSERT INTO FOODCALORIES VALUES ('" + food + "'," + cal + ")");
            Toast.makeText(this, "Item successfully added!", Toast.LENGTH_SHORT).show();
        }
    }
}