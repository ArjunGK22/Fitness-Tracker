package com.example.fitnesstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FitnessInformation extends AppCompatActivity {

    EditText activityet,heightet,weightet,bmiet,bmret,amret,fatpercentet,ageet;

    TextView bmi,bmr,amr,fat;

    Button calculatebtn, updatebtn;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_information);

        firebaseAuth = FirebaseAuth.getInstance();

        activityet = findViewById(R.id.activityet);
        heightet = findViewById(R.id.heightet);
        weightet = findViewById(R.id.weightet);
        bmiet = findViewById(R.id.bmiet);
        bmret = findViewById(R.id.bmret);
        amret = findViewById(R.id.amret);
        fatpercentet = findViewById(R.id.fatpercentet);
        ageet = findViewById(R.id.ageet);

        calculatebtn = findViewById(R.id.calculatebtn);
        updatebtn = findViewById(R.id.updatebtn);

        bmiet.setVisibility(View.INVISIBLE);
        bmret.setVisibility(View.INVISIBLE);
        amret.setVisibility(View.INVISIBLE);
        fatpercentet.setVisibility(View.INVISIBLE);

        bmi = findViewById(R.id.bmi);
        bmr = findViewById(R.id.bmr);
        amr = findViewById(R.id.amr);
        fat = findViewById(R.id.fat);

        bmi.setVisibility(View.INVISIBLE);
        bmr.setVisibility(View.INVISIBLE);
        amr.setVisibility(View.INVISIBLE);
        fat.setVisibility(View.INVISIBLE);

        calculatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        activityet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(FitnessInformation.this);
                alert.setTitle("").setItems(Items.activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String act = Items.activity[which];

                        activityet.setText(act);
                    }
                }).show();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

    }

    private void insertData() {

        String bmi_str = bmiet.getText().toString().trim();
        String bmr_str = bmret.getText().toString().trim();
        String amr_str = amret.getText().toString().trim();
        String fat_str = fatpercentet.getText().toString().trim();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("BMI",""+bmi_str);
        hashMap.put("BMR",""+bmr_str);
        hashMap.put("AMR",""+amr_str);
        hashMap.put("Fat Percent",""+fat_str);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Personal Details").updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FitnessInformation.this,"Details Updated",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    double height, weight,activity;
    int age;
    private void getData() {

        String activity_str = activityet.getText().toString();
        String weight_str = weightet.getText().toString();
        String height_str = heightet.getText().toString();
        String age_str = ageet.getText().toString();

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

        height = Double.parseDouble(height_str);
        weight = Double.parseDouble(weight_str);
        age = Integer.parseInt(age_str);

        System.out.println(height);
        System.out.println(weight);
        System.out.println(age);
        System.out.println(activity);

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

        setData(bmi_result,fat_result,bmr_result,amr_result);

    }

    private void setData(double bmi_result, double fat_result, double bmr_result, double amr_result) {
        bmiet.setVisibility(View.VISIBLE);
        bmret.setVisibility(View.VISIBLE);
        amret.setVisibility(View.VISIBLE);
        fatpercentet.setVisibility(View.VISIBLE);

        bmi.setVisibility(View.VISIBLE);
        bmr.setVisibility(View.VISIBLE);
        amr.setVisibility(View.VISIBLE);
        fat.setVisibility(View.VISIBLE);


        bmiet.setText(String.valueOf(bmi_result));
        bmret.setText(String.valueOf(bmr_result));
        amret.setText(String.valueOf(amr_result));


    }


}