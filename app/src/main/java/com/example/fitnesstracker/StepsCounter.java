package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StepsCounter extends AppCompatActivity implements SensorEventListener{

    FirebaseAuth firebaseAuth;

    TextView total_stepstv, caloriestv, distancetv;
    CircularProgressBar circularProgressBar;
    Button track_bcal_btn;

    SensorManager sensorManager;
    Sensor mStepCounter;
    private boolean isCounterSensorPresent;

    int total_steps = 0;

    String date_str = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_counter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        total_stepstv = findViewById(R.id.total_stepstv);
        caloriestv = findViewById(R.id.caloriestv);
        distancetv = findViewById(R.id.distancetv);
        circularProgressBar = findViewById(R.id.progress_circular);

        track_bcal_btn = findViewById(R.id.track_bcal_btn);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        firebaseAuth = FirebaseAuth.getInstance();

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        }
        else{
            Toast.makeText(this,"Sensor not found",Toast.LENGTH_SHORT).show();
            isCounterSensorPresent = false;
        }

        track_bcal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFirebase();
            }
        });

        ImageButton backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    private void saveToFirebase() {

        String cal_burned = caloriestv.getText().toString();
        String dis_covered = distancetv.getText().toString();
        String steps_taken = total_stepstv.getText().toString();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Total Steps",""+steps_taken);
        hashMap.put("Calories_Burnt",""+cal_burned);
        hashMap.put("Distance",""+dis_covered);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child(date_str).child("Calories_Burned").setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StepsCounter.this,"Tracked calories burned",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StepsCounter.this,"Unable to track\nPlease try later",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == mStepCounter){
            total_steps =  (int) sensorEvent.values[0];
            setData(total_steps);
        }

    }

    private void setData(int total_steps) {
        total_stepstv.setText(String.valueOf(total_steps));

        double calories = Math.round((total_steps * 0.45) * 100.0) / 100.0;
        double getDistance = getDist(total_steps);

        caloriestv.setText(String.valueOf(calories));
        distancetv.setText(String.valueOf(getDistance));
        circularProgressBar.setProgress(total_steps);
    }

    private  double getDist(int total_steps){
        double f = total_steps * 2.5;
        double d = f/3.281;
        double fdist = (d / 1000);

        return Math.round(fdist * 100.0) / 100.0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null)
            sensorManager.registerListener(this,mStepCounter,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null)
            sensorManager.unregisterListener(this,mStepCounter);
    }
}