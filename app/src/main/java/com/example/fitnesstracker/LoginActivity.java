package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    TextView signuptv;
    EditText usernameet,passwordet;
    Button loginbtn;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        usernameet = findViewById(R.id.usernameet);
        passwordet = findViewById(R.id.passwordet);
        loginbtn = findViewById(R.id.loginbtn);
        signuptv = findViewById(R.id.signuptv);

        progressBar = findViewById(R.id.progress);
        firebaseAuth = FirebaseAuth.getInstance();

        signuptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
    }

    private void checkUser() {
        String username = usernameet.getText().toString().trim();
        String password = passwordet.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            Toast.makeText(LoginActivity.this,"Invalid email format",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Enter the password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(username)){
            Toast.makeText(LoginActivity.this,"Enter the username",Toast.LENGTH_SHORT).show();
            return;
        }

        loginbtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(username,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateStatus();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginbtn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,"Invalid Username and password",Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void updateStatus() {
        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("Active","true");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
