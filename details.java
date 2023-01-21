package com.example.mycalories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class details extends AppCompatActivity {
    EditText nameet, ageet, heightet, weightet;
    RadioGroup rg;
    RadioButton malerb, femalerb;
    Button next;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_details);

        nameet = findViewById(R.id.nameet);
        ageet = findViewById(R.id.ageet);
        heightet = findViewById(R.id.heightet);
        weightet = findViewById(R.id.weightet);
        rg = findViewById(R.id.genderrg);
        next = findViewById(R.id.nextb);
        malerb = findViewById(R.id.malerb);
        femalerb = findViewById(R.id.femalerb);
        sp = getSharedPreferences("details",MODE_PRIVATE);

        String spname = sp.getString("Name", "");
        int spage = sp.getInt("Age",0);
        int spheight = sp.getInt("Height",0);
        int spweight = sp.getInt("Weight",0);
        String spgender = sp.getString("Gender","");

        if(!spname.equals(""))
            nameet.setText(spname);
        if(spage!=0)
            ageet.setText(spage+"");
        if(spheight!=0)
            heightet.setText(spheight+"");
        if(spweight!=0)
            weightet.setText(spweight+"");
        if(spgender == "Male")
            malerb.setChecked(true);
        if(spgender == "Female")
            femalerb.setChecked(true);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "",gender="";
                int age = 0;
                float height = 0, weight = 0, bmr = 0;

                int id = rg.getCheckedRadioButtonId();
                if(id!=-1){
                    if(id==R.id.malerb)
                        gender = "Male";
                    else
                        gender = "Female";
                }

                name = nameet.getText().toString();
                String sage="", sheight="", sweight="";
                sage = ageet.getText().toString();
                sheight = heightet.getText().toString();
                sweight = weightet.getText().toString();


                if(!sage.equals(""))
                    age = Integer.parseInt(sage);
                if(!sheight.equals(""))
                    height = Float.parseFloat(sheight);
                if(!sweight.equals(""))
                    weight = Float.parseFloat(sweight);

                if(name.equals("") || age == 0 || height == 0 || weight == 0 || gender.equals("")){
                    Toast.makeText(details.this, "Please fill in all the details", Toast.LENGTH_LONG).show();
                }
                else{
                    if(gender == "Male"){
                        bmr = (float) ((((10 * weight) + (6.25 * height)) - (5 * age)) + 5);
                    }
                    if(gender == "Female") {
                        bmr = (float) ((((10 * weight) + (6.25 * height)) - (5 * age)) - 161);
                    }
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("Name", name);
                    ed.putInt("Age", age);
                    ed.putInt("Height", (int) height);
                    ed.putInt("Weight", (int) weight);
                    if(gender == "Male")
                        ed.putString("Gender","Male");
                    else
                        ed.putString("Gender", "Female");
                    ed.commit();

                    int intbmr = (int) bmr;

                    Intent i = new Intent(details.this, TotalCalories.class);
                    i.putExtra("BMR", intbmr);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}