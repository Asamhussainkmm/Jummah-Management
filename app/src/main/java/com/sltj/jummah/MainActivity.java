package com.sltj.jummah;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText etPassword, etEmail;
    private Button btnSignIn;

    private FirebaseAuth firebaseAuth;

    private MyLoading myLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLoading = new MyLoading(this);

        etEmail = (EditText) findViewById(R.id.etemail);
        etPassword = (EditText) findViewById(R.id.etpassword);
        btnSignIn = (Button) findViewById(R.id.btnsignin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim().toLowerCase();
                String password = etPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Required");
                }
                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Required");
                }
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    signIn(email, password);
                }
            }
        });

        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(MainActivity.this, FirstActivity.class));
                finish();
            }
        });
    }

    private void signIn(String email, String password){
        myLoading.loadingStart();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null){
                                Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        myLoading.loadingDismiss();
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(MainActivity.this, FirstActivity.class);
            startActivity(intent);
            finish();
        }
    }
}