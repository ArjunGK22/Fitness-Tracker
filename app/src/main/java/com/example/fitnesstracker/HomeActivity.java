package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    CardView bmi_card,track_calories_card,track_water_card,step_counter_card;
    FirebaseAuth firebaseAuth;
    TextView calories_consumedtv,calories_burnedtv,water_intaketv;
    ImageView profilebtn;

    String date_str = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profilebtn = findViewById(R.id.profilebtn);
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ProfileScreen.class));
            }
        });

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(HomeActivity.this,"Permssion granted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(HomeActivity.this,"Permssion not granted",Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(HomeActivity.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.ACTIVITY_RECOGNITION)
                .check();


        bmi_card = findViewById(R.id.bmi_card);
        track_calories_card = findViewById(R.id.track_calories_card);
        track_water_card = findViewById(R.id.track_water_card);
        step_counter_card = findViewById(R.id.step_counter_card);

        calories_consumedtv = findViewById(R.id.calories_consumedtv);
        calories_burnedtv = findViewById(R.id.calories_burnedtv);
        water_intaketv = findViewById(R.id.water_intaketv);

        bmi_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,MacroCalculator.class));
            }
        });

        track_calories_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,CalorieTracker.class));
            }
        });

        track_water_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,WaterTracker.class));
            }
        });

        step_counter_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,StepsCounter.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        loadDetails();

    }

    private void loadDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String calories_consumed = ""+ds.child(date_str).child("Calories_Consumed").child("Total Calories").getValue();
                            String water_consumed = ""+ds.child(date_str).child("Water_Consumed").child("Water_Intake").getValue();
                            String cal_burned = ""+ds.child(date_str).child("Calories_Burned").child("Calories_Burnt").getValue();

                            if(calories_consumed.equals("null")){
                                calories_consumed = "0";
                            }
                            if(water_consumed.equals("null")){
                                water_consumed="0";
                            }
                            if(cal_burned.equals("null")){
                                cal_burned = "0";
                            }

                            calories_consumedtv.setText(calories_consumed);
                            water_intaketv.setText(water_consumed);
                            calories_burnedtv.setText(cal_burned);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}