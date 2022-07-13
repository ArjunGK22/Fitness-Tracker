package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BasicInformation extends AppCompatActivity {

    TextView bmitv,weighttv,heighttv,healthtv,fattv,amrtv,goaltv,gendertv;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_information);

        healthtv = findViewById(R.id.healthtv);
        weighttv = findViewById(R.id.weighttv);
        heighttv = findViewById(R.id.heighttv);
        bmitv = findViewById(R.id.bmitv);
        fattv = findViewById(R.id.fattv);
        amrtv = findViewById(R.id.amrtv);
        goaltv = findViewById(R.id.goaltv);
        gendertv = findViewById(R.id.gendertv);

        progressBar = findViewById(R.id.progress);


        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadDetails();

    }

    private void loadDetails() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String height = ""+ds.child("Personal Details").child("Height").getValue();
                            String weight = ""+ds.child("Personal Details").child("Current Weight").getValue();
                            String goalWeight = ""+ds.child("Personal Details").child("Goal Weight").getValue();
                            String bmi = ""+ds.child("Personal Details").child("BMI").getValue();
                            String fat = ""+ds.child("Personal Details").child("FAT").getValue();
                            String amr = ""+ds.child("Personal Details").child("AMR").getValue();
                            String gender = ""+ds.child("Personal Details").child("Gender").getValue();

                            heighttv.setText(height);
                            weighttv.setText(weight);
                            bmitv.setText(bmi);
                            goaltv.setText(goalWeight);
                            fattv.setText(fat);
                            amrtv.setText(amr);
                            gendertv.setText(gender);

                            setData(bmi);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setData(String bmi) {
        double bmi_result = Double.parseDouble(bmi);

        if(bmi_result<=18.5){
            healthtv.setTextColor(this.getResources().getColor(R.color.candylight));
            healthtv.setText("UnderWeight");
        }

        if(bmi_result>18.5 && bmi_result<=24.9){
            healthtv.setTextColor(this.getResources().getColor(R.color.green));
            healthtv.setText("Healthy");
        }

        if(bmi_result>=25 && bmi_result<=29.9){
            healthtv.setTextColor(this.getResources().getColor(R.color.yellow));
            healthtv.setText("OverWeight");
        }

        if(bmi_result>=30){
            healthtv.setTextColor(this.getResources().getColor(R.color.candy));
            healthtv.setText("Obese");
        }

        progressBar.setVisibility(View.INVISIBLE);
    }
}