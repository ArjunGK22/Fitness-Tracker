package com.example.fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MacroCalculator extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner activity_spinner;
    SeekBar height_seek,weight_seek;
    TextView heighttv,weighttv;
    EditText ageet;

    Button calculate_btn;

    String activity_str,age_str,height_str,weight_str;
    double activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_calculator);

        calculate_btn = findViewById(R.id.calculatebtn);

        height_seek = findViewById(R.id.height_seek);
        weight_seek = findViewById(R.id.weight_seek);

        heighttv = findViewById(R.id.heighttv);
        weighttv = findViewById(R.id.weighttv);

        ageet = findViewById(R.id.ageet);

        activity_spinner = findViewById(R.id.activity_spinner);
        activity_spinner.setOnItemSelectedListener(this);

        ImageButton backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        height_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                heighttv.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        weight_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                weighttv.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        calculate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                height_str = heighttv.getText().toString();
                weight_str = weighttv.getText().toString();
                age_str = ageet.getText().toString();

                if((age_str.isEmpty()) || (height_str.isEmpty()) || (weight_str.isEmpty()) || (activity_str.isEmpty())){
                    Toast.makeText(MacroCalculator.this,"Please fill the required parameters",Toast.LENGTH_SHORT).show();
                }

                else{
                    sendData();
                }


            }
        });
    }

    private void sendData() {
        Intent intent = new Intent(MacroCalculator.this,ResultScreen.class);
        intent.putExtra("height",height_str);
        intent.putExtra("weight",weight_str);
        intent.putExtra("activity",activity_str);
        intent.putExtra("age",age_str);

        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        activity_str = adapterView.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}