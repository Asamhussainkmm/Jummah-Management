package com.sltj.jummah;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BranchDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private EditText etUsername, etPhone, etName;
    private Button btnAdd, btnBack, btnDelete;

    private String mUsername, mPassword, mName, mPhone, mDhaee, mJummahDate;

    private boolean isEnabled = false;


    private MyLoading myLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_details);
        myLoading = new MyLoading(this);
        myLoading.loadingStart();

        Intent intent = getIntent();
        if(intent != null){
            mUsername = intent.getStringExtra("USERNAME");
            mPassword = intent.getStringExtra("PASSWORD");
            mName = intent.getStringExtra("NAME");
            mPhone = intent.getStringExtra("PHONE");
            mDhaee = intent.getStringExtra("DHAEE");
            mJummahDate = intent.getStringExtra("JUMMAH_DATE");

        }

        etUsername = findViewById(R.id.etusername);
        etPhone = findViewById(R.id.etphone);
        etName = findViewById(R.id.etname);

        btnAdd = findViewById(R.id.btnadduser);
        btnBack = findViewById(R.id.btnbackfromprofile);
        btnDelete = findViewById(R.id.btndelete);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("branch");

        etUsername.setText(mUsername);
        etUsername.setEnabled(false);
        etName.setText(mName);
        etPhone.setText(mPhone);


        etName.setEnabled(false);
        etPhone.setEnabled(false);

        disabledTextColor(etName);
        disabledTextColor(etPhone);
        disabledTextColor(etUsername);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDhayiListPage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEnabled){
                    isEnabled = true;
                    etName.setEnabled(true);
                    etPhone.setEnabled(true);
                    btnAdd.setText("Update");
                    btnDelete.setEnabled(false);
                    btnDelete.setBackground(getResources().getDrawable(R.drawable.disabled_button_background, getTheme()));
                    enabledTextColor(etName);
                    enabledTextColor(etPhone);


                }
                else {
                    disabledTextColor(etName);
                    disabledTextColor(etPhone);
                    isEnabled = false;
                    etName.setEnabled(false);
                    etPhone.setEnabled(false);
                    btnAdd.setText("Edit");
                    btnDelete.setBackground(getResources().getDrawable(R.drawable.button_background, getTheme()));
                    btnDelete.setEnabled(true);
                    updateUser();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(mUsername);
            }
        });

        myLoading.loadingDismiss();
    }

    public void goToDhayiListPage(){
        onBackPressed();
    }

    public void deleteUser(String username){
        userRef.child(username).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Branch Deleted Successfully", Toast.LENGTH_LONG).show();
                goToDhayiListPage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(){
        String username = etUsername.getText().toString().trim().toLowerCase();
        String name = etName.getText().toString().trim().toUpperCase();
        String phone = etPhone.getText().toString().trim();
        String password = "NA";
        String lastJummah = mDhaee;
        String lastJummahDate = mJummahDate;


        if(TextUtils.isEmpty(username)){
            etUsername.setError("Required");

        }if(TextUtils.isEmpty(name)){
            etName.setError("Required");
        }

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(name)){
            if(TextUtils.isEmpty(phone)){
                phone = "NA";
            }


            Branch user = new Branch(name, phone, lastJummah, lastJummahDate, username, password);
            userRef.child(username).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Branch Updated Successfully", Toast.LENGTH_LONG).show();
                                getOnBackPressedDispatcher().onBackPressed();
                            }
                        }
                    });
        }
    }

    public void disabledTextColor(EditText editText){
        editText.setTextColor(getResources().getColor(R.color.other_color2, getTheme()));
    }

    public void enabledTextColor(EditText editText){
        editText.setTextColor(getResources().getColor(R.color.text_color, getTheme()));
    }


}