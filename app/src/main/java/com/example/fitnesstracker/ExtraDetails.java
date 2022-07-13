package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ExtraDetails extends AppCompatActivity {

    EditText bmiet,bmret,amret,fatpercentet;
    TextView resulttv;

    String age_str, height_str, weight_str, activity_str,gender_str,goal_str;

    double height,weight,activity;
    int age;

    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_details);

        bmiet = findViewById(R.id.bmiet);
        bmret = findViewById(R.id.bmret);
        amret = findViewById(R.id.amret);
        fatpercentet = findViewById(R.id.fatpercentet);

        age_str = getIntent().getStringExtra("age");
        height_str = getIntent().getStringExtra("height");
        weight_str = getIntent().getStringExtra("weight");
        activity_str = getIntent().getStringExtra("activity");
        gender_str = getIntent().getStringExtra("gender");
        goal_str = getIntent().getStringExtra("goal");

        age = Integer.parseInt(age_str);
        height = Double.parseDouble(height_str);
        weight = Double.parseDouble(weight_str);

        resulttv = findViewById(R.id.resulttv);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Started");
        progressDialog.setCanceledOnTouchOutside(false);

        Button gobtn = findViewById(R.id.gobtn);
        Button prevbtn = findViewById(R.id.prevbtn);

        gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDetails();
            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExtraDetails.this,BodyTracker.class));
            }
        });

        CalculatedDetails(age,height,weight,activity_str);
    }

    private void insertDetails() {
        progressDialog.setMessage("Please Wait\nWhile we update the details");
        progressDialog.show();
        String bmi = bmiet.getText().toString();
        String bmr = bmret.getText().toString();
        String amr = amret.getText().toString();
        String fat = fatpercentet.getText().toString();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Age",""+age_str);
        hashMap.put("Activity",""+activity_str);
        hashMap.put("Height",""+height_str);
        hashMap.put("Current Weight",""+weight_str);
        hashMap.put("Goal Weight",""+goal_str);
        hashMap.put("Gender",""+gender_str);
        hashMap.put("BMI",""+bmi);
        hashMap.put("BMR",""+bmr);
        hashMap.put("AMR",""+amr);
        hashMap.put("FAT",""+fat);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Personal Details").setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        startActivity(new Intent(ExtraDetails.this,HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExtraDetails.this,"Error updating status\nPlease try later",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void CalculatedDetails(int age, double height, double weight, String activity_str) {

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
            Toast.makeText(this,"Please enter the activity level",Toast.LENGTH_SHORT).show();
        }

        Calculator calculator = new Calculator();

        double bmi_result = calculator.bmi_calculator(weight,height);
        double fat_result = calculator.body_fat(bmi_result,age);

        fatpercentet.setText(String.valueOf(fat_result));

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

        if(bmi_result<=18.5){
            resulttv.setTextColor(this.getResources().getColor(R.color.candylight));
            resulttv.setText("According to the result you are Underweight");
        }

        if(bmi_result>18.5 && bmi_result<=24.9){
            resulttv.setTextColor(this.getResources().getColor(R.color.green));
            resulttv.setText("According to the result you are Normal");
        }

        if(bmi_result>=25 && bmi_result<=29.9){
            resulttv.setTextColor(this.getResources().getColor(R.color.yellow));
            resulttv.setText("According to the result you are overweight");
        }

        if(bmi_result>=30){
            resulttv.setTextColor(this.getResources().getColor(R.color.candy));
            resulttv.setText("According to the result you are Obese");
        }

        bmiet.setText(String.valueOf(bmi_result));
        bmret.setText(String.valueOf(bmr_result));
        amret.setText(String.valueOf(amr_result));

    }
}