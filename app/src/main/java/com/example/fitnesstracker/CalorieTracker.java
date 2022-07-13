package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalorieTracker extends AppCompatActivity {

    ImageButton minc,linc,sinc,dinc;
    ImageButton mdec,ldec,sdec,ddec;

    String date_str = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());



    TextView morningtv,lunchtv,snacktv,dinnertv;
    TextView total_caloriestv, consumed_caloriestv;
    Button track_caloriesbtn;

    //Calories
    int morning_cal = 0;
    int lunch_cal = 0;
    int snack_cal = 0;
    int dinner_cal = 0;

    int total_calories = 0; //total calories
    int consumed_calories = 0; //total calories consumed

    //Firebase Auth
    FirebaseAuth firebaseAuth;

    CircularProgressBar circularProgressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_tracker);

        circularProgressBar = findViewById(R.id.progress_circular);

        //Increment Buttons
        minc = findViewById(R.id.mincrement);
        linc = findViewById(R.id.lincrement);
        sinc = findViewById(R.id.sincrement);
        dinc = findViewById(R.id.dincrement);

        //Decrement Buttons
        mdec = findViewById(R.id.mdecrement);
        ldec = findViewById(R.id.ldecrement);
        sdec = findViewById(R.id.sdecrement);
        ddec = findViewById(R.id.ddecrement);

        track_caloriesbtn = findViewById(R.id.trackCaloriesbtn);

        //TextView KCAL
        morningtv = findViewById(R.id.morningtv);
        lunchtv = findViewById(R.id.lunchtv);
        snacktv = findViewById(R.id.snackstv);
        dinnertv = findViewById(R.id.dinnertv);

        //Calories TextView
        total_caloriestv = findViewById(R.id.total_calories);
        consumed_caloriestv = findViewById(R.id.consumed_calories);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /* ************** Increment & Decrement Buttons ************ */

        minc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morning_cal = morning_cal + 10;
                morningtv.setText(""+morning_cal);

                addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
                
            }
        });

        mdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(morning_cal>0){
                    morning_cal = morning_cal - 10;
                    morningtv.setText(morning_cal+ "");
                    addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
                }

                else {}


            }

        });

        linc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lunch_cal += 10;
                lunchtv.setText(lunch_cal+ "");
                addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
            }
        });

        ldec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lunch_cal>0){
                    lunch_cal -= 10;
                    lunchtv.setText(lunch_cal+ "");
                    addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);

                }

                else {}


            }

        });

        sinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack_cal += 10;
                snacktv.setText(snack_cal+ "");
                addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
            }
        });

        sdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(snack_cal>0){
                    snack_cal -= 10;
                    snacktv.setText(snack_cal+ "");
                    addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
                }

                else {}


            }

        });

        dinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dinner_cal = dinner_cal + 10;
                dinnertv.setText(dinner_cal+ "");
                addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
            }
        });

        ddec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dinner_cal>0){
                    dinner_cal -= 10;
                    dinnertv.setText(dinner_cal+ "");
                    addtotalcal(morning_cal,lunch_cal,snack_cal,dinner_cal);
                }

                else {}


            }

        });

        /* ********************************************************** */

        track_caloriesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((morning_cal==0) && (lunch_cal==0) && (snack_cal==0) && (dinner_cal==0) && (consumed_calories==0)){
                    saveCalories();
                }
                else {
                    updateCalories();
                }
            }
        });

        loadDetails();
    }

    private void loadDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            String t_calories = ""+ds.child(date_str).child("Calories_Consumed").child("Total Calories").getValue();
                            String m_calories = ""+ds.child(date_str).child("Calories_Consumed").child("Breakfast").getValue();
                            String l_calories = ""+ds.child(date_str).child("Calories_Consumed").child("Lunch").getValue();
                            String s_calories = ""+ds.child(date_str).child("Calories_Consumed").child("Snacks").getValue();
                            String d_calories = ""+ds.child(date_str).child("Calories_Consumed").child("Dinner").getValue();
                            String tc_calories = ""+ds.child("Personal Details").child("AMR").getValue();

                            if(t_calories.equals("null")){
                                t_calories = "0";
                            }

                            if(m_calories.equals("null")){
                                m_calories = "0";
                            }

                            if(l_calories.equals("null")){
                                l_calories = "0";
                            }

                            if(s_calories.equals("null")){
                                s_calories = "0";
                            }

                            if(d_calories.equals("null")){
                                d_calories = "0";
                            }

                            retrivedata(t_calories,m_calories,l_calories,s_calories,d_calories,tc_calories);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void retrivedata(String t_calories, String m_calories, String l_calories, String s_calories, String d_calories,String tc_calories) {

        double ttcal = Double.parseDouble(tc_calories);
        int totaL_cal = (int) Math.round(ttcal);


        consumed_caloriestv.setText(""+t_calories);
        morningtv.setText(""+m_calories);
        lunchtv.setText(""+l_calories);
        snacktv.setText(""+s_calories);
        dinnertv.setText(""+d_calories);
        total_caloriestv.setText("/"+totaL_cal);

        consumed_calories = Integer.parseInt(t_calories);
        morning_cal = Integer.parseInt(m_calories);
        lunch_cal = Integer.parseInt(l_calories);
        snack_cal = Integer.parseInt(s_calories);
        dinner_cal = Integer.parseInt(d_calories);

        circularProgressBar.setProgressWithAnimation(consumed_calories,1000L);
    }


    private void addtotalcal(Integer morning_cal, Integer lunch_cal, Integer snack_cal, Integer dinner_cal) {

        consumed_calories = morning_cal + lunch_cal + snack_cal + dinner_cal;

        System.out.println(consumed_calories);

        consumed_caloriestv.setText(consumed_calories+ "");
        circularProgressBar.setProgressWithAnimation(consumed_calories,1000L);


    }
    

    private void saveCalories() {


        String consumed_calories_str = consumed_caloriestv.getText().toString();
        String morning_cal = morningtv.getText().toString();
        String lunch_cal = lunchtv.getText().toString();
        String snacks_cal = snacktv.getText().toString();
        String dinner_cal = dinnertv.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("Total Calories",consumed_calories_str);
        hashMap.put("Breakfast",morning_cal);
        hashMap.put("Lunch",lunch_cal);
        hashMap.put("Snacks",snacks_cal);
        hashMap.put("Dinner",dinner_cal);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child(date_str).child("Calories_Consumed").setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CalorieTracker.this,"Calories Intake Tracked",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CalorieTracker.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateCalories() {

        String consumed_calories_str = consumed_caloriestv.getText().toString();
        String morning_cal = morningtv.getText().toString();
        String lunch_cal = lunchtv.getText().toString();
        String snacks_cal = snacktv.getText().toString();
        String dinner_cal = dinnertv.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("Total Calories",consumed_calories_str);
        hashMap.put("Breakfast",morning_cal);
        hashMap.put("Lunch",lunch_cal);
        hashMap.put("Snacks",snacks_cal);
        hashMap.put("Dinner",dinner_cal);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child(date_str).child("Calories_Consumed")
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CalorieTracker.this,"Calories Updated",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CalorieTracker.this,"Unable to Track\n Please try later",Toast.LENGTH_SHORT).show();
                    }
                });




    }
}
