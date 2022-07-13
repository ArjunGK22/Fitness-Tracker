package com.example.fitnesstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class BodyTracker extends AppCompatActivity {

    EditText ageet,heightet,weightet,gweightet;
    TextView activityet, genderet;
    Button clearbtn,submitbtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_tracker);

        ageet = findViewById(R.id.ageet);
        activityet = findViewById(R.id.activityet);
        heightet = findViewById(R.id.heightet);
        weightet = findViewById(R.id.weightet);
        gweightet = findViewById(R.id.gweightet);
        genderet = findViewById(R.id.genderet);

        submitbtn = findViewById(R.id.submitbtn);

        firebaseAuth = FirebaseAuth.getInstance();


        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDetails();
            }
        });

        activityet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BodyTracker.this);
                alert.setTitle("").setItems(Items.activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String act = Items.activity[which];
                        activityet.setText(act);
                    }
                }).show();
            }
        });

        genderet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BodyTracker.this);
                alert.setTitle("Gender").setItems(Items.gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String act = Items.gender[which];

                        genderet.setText(act);
                    }
                }).show();
            }
        });

    }

    String age,activity,height,cweight,gweight,gender,bmi;
    private void insertDetails() {
        age = ageet.getText().toString().trim();
        activity = activityet.getText().toString().trim();
        height = heightet.getText().toString().trim();
        cweight = weightet.getText().toString().trim();
        gweight = gweightet.getText().toString().trim();
        gender = genderet.getText().toString().trim();

        if((TextUtils.isEmpty(age)) && (TextUtils.isEmpty(activity)) && (TextUtils.isEmpty(height)) && (TextUtils.isEmpty(cweight)) && (TextUtils.isEmpty(gweight)) && (TextUtils.isEmpty(gender))){
            Toast.makeText(this,"Kindly fill all the parameters",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(age)){
            Toast.makeText(this,"Enter your age",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(activity)){
            Toast.makeText(this,"Please select the activity",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(height)){
            Toast.makeText(this,"Enter your Height",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cweight)){
            Toast.makeText(this,"Enter your current weight",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(gweight)){
            Toast.makeText(this,"Enter the goal weight",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(gender)){
            Toast.makeText(this,"Select your gender",Toast.LENGTH_SHORT).show();
            return;
        }

        sendData();
    }

    private void sendData() {

        Intent intent = new Intent(BodyTracker.this,ExtraDetails.class);
        intent.putExtra("age",age);
        intent.putExtra("weight",cweight);
        intent.putExtra("height",height);
        intent.putExtra("activity",activity);
        intent.putExtra("gender",gender);
        intent.putExtra("goal",gweight);
        startActivity(intent);
        finish();
    }
}