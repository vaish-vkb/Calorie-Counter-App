package com.example.mycalories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchFood extends AppCompatActivity {
    EditText food;
    Button search, back;
    ListView lv;
    ArrayList<String> foodarr;
    ArrayAdapter<String> adp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search_food);

        food = findViewById(R.id.searchfoodet);
        search = findViewById(R.id.searchfoodb);
        back = findViewById(R.id.back);
        lv = findViewById(R.id.lv);
        db = openOrCreateDatabase("CALORIES", MODE_PRIVATE, null);

        Intent i1 = getIntent();
        int availcalories = i1.getIntExtra("AvailableCalories", 0);

        foodarr = new ArrayList<>();
        adp = new ArrayAdapter<>(SearchFood.this, android.R.layout.simple_list_item_1, foodarr);
        lv.setAdapter(adp);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent();
                i2.putExtra("AvailableCalories", availcalories);
                setResult(7,i2);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sfood = food.getText().toString();
                if(sfood.equals("")){
                    Toast.makeText(SearchFood.this, "Please enter a food item", Toast.LENGTH_SHORT).show();
                }
                else{
                    Cursor c = db.rawQuery("SELECT * FROM FOODCALORIES WHERE FOODITEM LIKE '%" + sfood + "%'", null );
                    if(c.getCount()==0){
                        Toast.makeText(SearchFood.this, "Sorry, no such item exists!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        c.moveToFirst();
                        do{
                            String foodname = c.getString(0);
                            int calories = c.getInt(1);
                            String all = "Food: " + foodname + "\nCalories: " + calories;
                            foodarr.add(all);
                            adp.notifyDataSetChanged();
                        }while(c.moveToNext());
                    }
                }
            }
        });
    }
}