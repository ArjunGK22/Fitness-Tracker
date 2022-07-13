package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameet, emailet,passwordet,confirmpwd;
    Button signupbtn;
    ImageButton backbtn;

    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        nameet = findViewById(R.id.nametv);
        emailet = findViewById(R.id.usernameet);
        passwordet = findViewById(R.id.passwordet);
        confirmpwd = findViewById(R.id.confpass);
        signupbtn = findViewById(R.id.registerbtn);
        backbtn = findViewById(R.id.backbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserData();
            }
        });


    }

    String name, email, password,cpassword;
    private void getUserData() {


        name = nameet.getText().toString().trim();
        email = emailet.getText().toString().trim();
        password = passwordet.getText().toString().trim();
        cpassword = confirmpwd.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter your emailId",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter your Password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(this,"Password cannot be less than 6 characters",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(cpassword)){
            Toast.makeText(this,"Password do not match",Toast.LENGTH_SHORT).show();
            return;
        }


        createuseraccount();

    }

    private void createuseraccount() {
        signupbtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        savedata();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        signupbtn.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void savedata() {

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("Uid",""+firebaseAuth.getUid());
        hashMap.put("Name",""+name);
        hashMap.put("Email",""+email);
        hashMap.put("Password",""+password);
        hashMap.put("Active","true");
        hashMap.put("Type","user");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(RegisterActivity.this,BodyTracker.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });

    }
}