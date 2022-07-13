package com.example.fitnesstracker;

import java.text.DecimalFormat;

public class Calculator {

    DecimalFormat df = new DecimalFormat("#.##");

    public double bmi_calculator(double weight, double height){
        double bmi;
        bmi = (weight/Math.pow(height/100,2));


        return Math.round(bmi * 100.0) / 100.0;
    }

    public double body_fat(double bmi, int age){
        double fat_percent;

        fat_percent = (1.20 * bmi) + (0.23 * age) - 5.4;

        return Math.round(fat_percent * 100.0) / 100.0;
    }

    public double bmr(double weight, double fat_percent){
        double bmr;
        bmr = weight * 0.9 * 24 * (fat_percent);

        return Math.round(bmr * 100.0) / 100.0;
    }
    public double amr(double bmr, double activity){
        double amr;

        amr = (bmr) * activity;

        return Math.round(amr * 100.0) / 100.0;
    }
}
