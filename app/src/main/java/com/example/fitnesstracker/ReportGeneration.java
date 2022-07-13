package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportGeneration extends AppCompatActivity {

    TextView date_tv, resulttxt,weighttxt,watertxt,consumedtxt,burnedtxt,datetxt;
    int year, month,day;

    FirebaseAuth firebaseAuth;

    CardView report_card;
    RelativeLayout msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generation);

        date_tv = findViewById(R.id.date_tv);
        resulttxt = findViewById(R.id.resulttxt);
        watertxt = findViewById(R.id.watertxt);
        consumedtxt = findViewById(R.id.consumedtxt);
        burnedtxt = findViewById(R.id.burnedtxt);
        datetxt = findViewById(R.id.datetxt);
        msg = findViewById(R.id.resultMsg);
        report_card = findViewById(R.id.report_card);

        Button reportbtn = findViewById(R.id.reportbtn);

        firebaseAuth  = FirebaseAuth.getInstance();

        ImageButton backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadReport();
            }
        });



        final Calendar calendar = Calendar.getInstance();


        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                Date date = calendar.getTime();

                String date_str = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());
                System.out.println(date_str);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportGeneration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int     i2) {
                      int month = i1;
                      String month_str = String.format("%02d" , month);
                      int mInt = (Integer.parseInt(month_str)) + 1;
                       date_tv.setText(i2+"-"+String.format("%02d" , mInt)+"-"+i);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void loadReport() {

        String date = date_tv.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String calories_consumed = ""+ds.child(date).child("Calories_Consumed").child("Total Calories").getValue();
                            String water_consumed = ""+ds.child(date).child("Water_Consumed").child("Water_Intake").getValue();
                            String cal_burned = ""+ds.child(date).child("Calories_Burned").child("Calories_Burnt").getValue();
                            String max_cal = ""+ds.child("Personal Details").child("AMR").getValue();

                            if((calories_consumed.equals("null")) || (water_consumed.equals("null")) || (cal_burned.equals("null")) || (max_cal.equals("null"))){

                                msg.setVisibility(View.VISIBLE);
                                report_card.setVisibility(View.INVISIBLE);

                            }

                            else {

                                msg.setVisibility(View.INVISIBLE);
                                report_card.setVisibility(View.VISIBLE);

                                int maxCal = (int) Math.round(Double.parseDouble(max_cal));

                                int calCons = (int) Math.round(Double.parseDouble(calories_consumed));
                                int calBurn = (int) Math.round(Double.parseDouble(cal_burned));

                                int result = Math.abs(calCons - calBurn);
                                int result1 = Math.abs(result - maxCal);

                                if(result1>maxCal){
                                    resulttxt.setText("You were Calorie Surplus of "+result1);
                                }
                                else {
                                    resulttxt.setText("You were in Calorie Deficit of "+result1);
                                }

                                datetxt.setText(String.valueOf(date));
                                consumedtxt.setText(calories_consumed+" kcal");
                                burnedtxt.setText(cal_burned+" kcal");
                                watertxt.setText(water_consumed+" glass");

                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReportGeneration.this,""+ error.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
    }
}