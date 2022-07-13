package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView weighttv, heighttv, bmitv,nametv;

    CardView basic_card, basic_card2;
    Button logoutbtn;
    
    ProgressDialog p1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);


        p1 = new ProgressDialog(this);
        progressBar = findViewById(R.id.progress);

        basic_card = findViewById(R.id.basic_card);
        basic_card2 = findViewById(R.id.basic_card2);
        logoutbtn = findViewById(R.id.logoutbtn);


        weighttv = findViewById(R.id.weighttv);
        heighttv = findViewById(R.id.heighttv);
        bmitv = findViewById(R.id.bmitv);
        nametv = findViewById(R.id.nametv);

        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton homebtn = findViewById(R.id.homebtn);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this,HomeActivity.class));
                finish();

            }
        });

        basic_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this,BasicInformation.class));
            }
        });

        basic_card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this,ReportGeneration.class));
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
                builder.setTitle("LOG OUT")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makemeoffline();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).show();
            }
        });

        loadDetails();
    }

    private void makemeoffline() {
        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("Active","false");

        //updating the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        checkuser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p1.dismiss();
                        Toast.makeText(ProfileScreen.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();



                    }
                });

    }

    private void checkuser() {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user==null){
                startActivity(new Intent(ProfileScreen.this,LoginActivity.class));
                finish();
            }
            else{
                loadDetails();
            }
    }

    private void loadDetails() {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String weight = ""+ds.child("Personal Details").child("Current Weight").getValue();
                            String height = ""+ds.child("Personal Details").child("Height").getValue();
                            String bmi = ""+ds.child("Personal Details").child("BMI").getValue();
                            String name = ""+ds.child("Name").getValue();

                            weighttv.setText(String.valueOf(weight));
                            heighttv.setText(String.valueOf(height));
                            bmitv.setText(String.valueOf(bmi));
                            nametv.setText(String.valueOf(name));

                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}