package com.sltj.jummah;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    RecyclerView recyclerView;

    MyLoading myLoading;
    private EditText etSearch;

    private List<Jummah> jummahList = new ArrayList<>();
    private MyAdapter myAdapter;

    private FloatingActionButton btnAddJummah;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        myLoading = new MyLoading(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("jummah");
        recyclerView = (RecyclerView) findViewById(R.id.rvmain);
        etSearch = (EditText)findViewById(R.id.etjummahsearch);
        btnAddJummah = findViewById(R.id.fbaddjummah);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            btnAddJummah.setVisibility(View.VISIBLE);
        }
        else {
            btnAddJummah.setVisibility(View.GONE);
        }

        btnAddJummah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser != null){
                    startActivity(new Intent(HomePage.this, JummahAssignActivity.class));
                }

            }
        });


        collectJummah(jummahList);


//        dhaayiList.add(new Dhaayi("na", "na", "na", 5));


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null){
                    myAdapter.filter(s.toString());
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(HomePage.this, FirstActivity.class));
                finish();
            }
        });


    }

    public void collectJummah(List<Jummah> myJummah){
        myLoading.loadingStart();
        databaseReference.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jummahList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Jummah jummah = ds.getValue(Jummah.class);
                    jummahList.add(jummah);
                }

                Collections.reverse(jummahList);
                myAdapter = new MyAdapter(HomePage.this, jummahList);
                recyclerView.setLayoutManager(new LinearLayoutManager(HomePage.this));
                recyclerView.setAdapter(myAdapter);


                myLoading.loadingDismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
            }
        });
    }


}