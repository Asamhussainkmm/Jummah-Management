package com.sltj.jummah;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BranchProfileActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private EditText etUsername, etPhone, etName;
    private Button btnAdd, btnBack;

    List<String> existsUsers = new ArrayList<>();

    private MyLoading myLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_profile);

        myLoading = new MyLoading(this);
        myLoading.loadingStart();

        etUsername = findViewById(R.id.etusername);
        etPhone = findViewById(R.id.etphone);
        etName = findViewById(R.id.etname);

        btnAdd = findViewById(R.id.btnadduser);
        btnBack = findViewById(R.id.btnbackfromprofile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("branch");

        getExistsUsernames();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDhayiListPage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim().toLowerCase();
                String password = "NA";
                String name = etName.getText().toString().trim().toUpperCase();
                String phone = etPhone.getText().toString().trim();
                String lastJummah = "NA";
                String lastJummahDate = "NA";


                if(TextUtils.isEmpty(username)){
                    etUsername.setError("Required");

                }if(TextUtils.isEmpty(name)){
                    etName.setError("Required");
                }


                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)){
                    if(TextUtils.isEmpty(phone)){
                        phone = "NA";
                    }

                    if(existsUsers.contains(username)){
                        Toast.makeText(getApplicationContext(), "Branch username already exists!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Branch user = new Branch(name, phone, lastJummah, lastJummahDate, username, password);
                    userRef.child(username).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        myLoading.loadingDismiss();
                                        Toast.makeText(getApplicationContext(), "Branch Added Successfully", Toast.LENGTH_SHORT).show();
                                        goToDhayiListPage();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void getExistsUsernames(){

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("branch");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                existsUsers.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    existsUsers.add(ds.getKey());
                }
                myLoading.loadingDismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
            }
        });

    }

    public void goToDhayiListPage(){
        getOnBackPressedDispatcher().onBackPressed();
    }
}