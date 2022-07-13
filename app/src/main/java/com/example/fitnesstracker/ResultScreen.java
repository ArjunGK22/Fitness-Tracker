package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ResultScreen extends AppCompatActivity {

    String height_str, weight_str, age_str, activity_str;
    double activity;

    TextView bmitv,totalcaloriestv,bmrtv,fatpercenttv;

    Button updatebtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        bmitv = findViewById(R.id.bmitv);
        totalcaloriestv = findViewById(R.id.totalcaloriestv);
        bmrtv = findViewById(R.id.bmrtv);
        fatpercenttv = findViewById(R.id.fatpercenttv);

        height_str = getIntent().getStringExtra("height");
        weight_str = getIntent().getStringExtra("weight");
        age_str = getIntent().getStringExtra("age");
        activity_str = getIntent().getStringExtra("activity");

        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        if(activity_str.equals("Little or No Activity")){
            activity = 1.2;
        }
        else if(activity_str.equals("Lightly Active")){
            activity = 1.375;
        }
        else if(activity_str.equals("Moderately Active")){
            activity = 1.55;
        }
        else if(activity_str.equals("Very Active")){
            activity = 1.9;
        }
        else{
            Toast.makeText(ResultScreen.this,"Please enter the activity level",Toast.LENGTH_SHORT).show();
        }

        double height = Double.parseDouble(height_str);
        double weight = Double.parseDouble(weight_str);
        int age = Integer.parseInt(age_str);

        Calculator calculator = new Calculator();

        double bmi_result = calculator.bmi_calculator(weight,height);
        double fat_result = calculator.body_fat(bmi_result,age);

        fatpercenttv.setText(String.valueOf(fat_result));

        if((fat_result>=10) && (fat_result<=14)){
            fat_result = 1.0;
        }
        else if((fat_result>=15) && (fat_result<=20)){
            fat_result = 0.95;
        }

        else if((fat_result>=21) && (fat_result<=28)){
            fat_result = 0.90;
        }

        else if(fat_result>=28){
            fat_result = 0.85;
        }
        else{
        }

        double bmr_result = calculator.bmr(weight,fat_result);
        double amr_result = calculator.amr(bmr_result,activity);

        bmitv.setText(String.valueOf(bmi_result));
        bmrtv.setText(String.valueOf(bmr_result));
        totalcaloriestv.setText(String.valueOf(amr_result));

        updatebtn = findViewById(R.id.updatebtn);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

    }

    private void update() {
        String bmi = bmitv.getText().toString().trim();
        String bmr = bmrtv.getText().toString().trim();
        String amr = totalcaloriestv.getText().toString().trim();
        String fat = fatpercenttv.getText().toString().trim();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("BMI",""+bmi);
        hashMap.put("BMR",""+bmr);
        hashMap.put("AMR",""+amr);
        hashMap.put("FAT",""+fat);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Personal Details").updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResultScreen.this,"Details Updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResultScreen.this,HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResultScreen.this,"Error updating status\nPlease try later",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}