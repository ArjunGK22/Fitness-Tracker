package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class WaterTracker extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    TextView glasstv,r1,r2;
    Button track_waterbtn;

    ImageButton incre_glass, decre_glass,backbtn;

    LinearLayout result_layout;

    int total_glass = 0;

    CircularProgressBar circularProgressBar ;

    String date_str = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);

        glasstv = findViewById(R.id.glasstv);
        track_waterbtn = findViewById(R.id.track_waterbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        incre_glass = findViewById(R.id.incre_water);
        decre_glass = findViewById(R.id.decre_water);
        backbtn = findViewById(R.id.backbtn);

        result_layout = findViewById(R.id.result_layout);

        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);

        circularProgressBar = findViewById(R.id.progress_circular);



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });



        incre_glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_glass++;
                glasstv.setText(total_glass+"");
                circularProgressBar.setProgressWithAnimation(total_glass,1000L);
                callText();
            }
        });

        decre_glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_glass>0){
                    total_glass--;
                    glasstv.setText(total_glass+"");
                    circularProgressBar.setProgressWithAnimation(total_glass,1000L);
                    callText();

                }


            }
        });



        track_waterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_glass==0){
                    saveToDB();
                }
                else {
                    updateDB();

                }

            }
        });

        loadDetails();
    }

    private void callText() {

        int glass = Integer.parseInt(glasstv.getText().toString());

        if(glass == 0){
            result_layout.setVisibility(View.INVISIBLE);
            return;
        }

        if(glass == 1){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Bottoms up!");
            r2.setText("Gulp! And it's gone. Get your next\n glass in 60 minutes");
            return;
        }
        if(glass == 2){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Awesome!");
            r2.setText("Drink your next glass of water\n after 60 minutes");
            return;
        }
        if(glass == 3){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("You Rock!");
            r2.setText("Stay hydrated! Your next glass of\n water in due in 60 minutes");
            return;
        }
        if(glass == 4){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Way to go!");
            r2.setText("Every glass of water counts.\nTrack another glass in 60 minutes");
            return;
        }
        if(glass == 5){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Half-way there!");
            r2.setText("Have your next glass of water\nafter 60 minutes");
            return;
        }
        if(glass == 6){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Water is good!");
            r2.setText("Say goodbye to your thirst with\nwith another glass in 60 minutes");
            return;
        }
        if(glass == 7){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Way to go!");
            r2.setText("Every glass of water counts.\nTrack another glass in 60 minutes");
            return;
        }
        if(glass == 8){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("You Rock!");
            r2.setText("Stay hydrated! Your next glass of\n water in due in 60 minutes");
            return;
        }
        if(glass == 9){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Bottoms up!");
            r2.setText("Gulp! And it's gone. Get your next\n glass in 60 minutes");
            return;
        }
        if(glass == 10){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("Drink up!");
            r2.setText("Take your next glass in 60 minutes");
            return;
        }
        if(glass == 11){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("You Rock!");
            r2.setText("Stay hydrated! Your next glass of\n water in due in 60 minutes");
            return;
        }
        if(glass == 12){
            result_layout.setVisibility(View.VISIBLE);
            r1.setText("You made it!");
            r2.setText("Great work on meeting your target");
            return;
        }
    }

    private void loadDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String total_glass_str = ""+ds.child(date_str).child("Water_Consumed").child("Water_Intake").getValue();

                            if(total_glass_str.equals("null")){
                                Toast.makeText(WaterTracker.this,"Track Water Consumed",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                total_glass = Integer.parseInt(total_glass_str);
                                glasstv.setText(total_glass_str+"");
                                circularProgressBar.setProgressWithAnimation(total_glass,1000L);
                                callText();

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateDB() {

        String total_glass = glasstv.getText().toString().trim();

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("Water_Intake",""+total_glass);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child(date_str).child("Water_Consumed").updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WaterTracker.this,"Water Updated",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WaterTracker.this,"Error in Tracking\nPlease try later",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToDB() {

        String total_glass = glasstv.getText().toString().trim();

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("Water_Intake",""+total_glass);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child(date_str).child("Water_Consumed").setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WaterTracker.this,"Water Tracked",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WaterTracker.this,"Error in Tracking\nPlease try later",Toast.LENGTH_SHORT).show();
                    }
                });


    }
}