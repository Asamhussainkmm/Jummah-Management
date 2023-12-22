package com.sltj.jummah;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirstActivity extends AppCompatActivity {

    private Button btnJummah, btnDhaee, btnBranch, btnExit;

    private FloatingActionButton btnlogin;

    private FirebaseUser firebaseUser;

    private ImageView ivLogo;

    private TextView tvAdminStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnJummah = findViewById(R.id.btnjummahlist);
        btnDhaee = findViewById(R.id.btndhaayilist);
        btnBranch = findViewById(R.id.btnbranchlist);
        btnExit = findViewById(R.id.btnexit);
        btnlogin = findViewById(R.id.fblogout);
        tvAdminStatus = findViewById(R.id.tvadminstatus);

        buttonNextActivity(btnJummah, HomePage.class);
        buttonNextActivity(btnDhaee, DhayiListActivity.class);
        buttonNextActivity(btnBranch, BranchiListActivity.class);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser == null){
                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(FirstActivity.this, FirstActivity.class));
                    finish();
                }

            }
        });



        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(firebaseUser != null){
            btnlogin.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                btnlogin.setTooltipText("Logout");
            }
            btnlogin.setImageResource(R.drawable.baseline_logout_24);
        }
        else {

            tvAdminStatus.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                btnlogin.setTooltipText("Login");
            }
            btnlogin.setImageResource(R.drawable.baseline_login_24);
        }

        // Set up the back press callback
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(FirstActivity.this)
                        .setMessage("Do you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish(); // Close the app
                            }
                        })
                        .setNegativeButton("No", null) // Dismiss the dialog, do nothing
                        .show();
            }
        });

    }

    public void buttonNextActivity(Button button, Class activity){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, activity));
                finish();
            }
        });
    }
}