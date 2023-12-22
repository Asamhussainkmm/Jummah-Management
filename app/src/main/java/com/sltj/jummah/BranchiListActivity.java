package com.sltj.jummah;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BranchiListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton btnAddingUser;
    private List<Branch> userList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText etSearch;

    private BranchAdapter branchAdapter;

    private MyLoading myLoading;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);

        myLoading = new MyLoading(this);
        myLoading.loadingStart();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("branch");
        recyclerView = (RecyclerView) findViewById(R.id.rvuserlist);
        btnAddingUser = (FloatingActionButton) findViewById(R.id.fbadduser);
        etSearch = (EditText)findViewById(R.id.etsearch);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            btnAddingUser.setVisibility(View.VISIBLE);
        }
        else {
            btnAddingUser.setVisibility(View.GONE);
        }

        collectDhayi(userList);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null){
                    branchAdapter.filter(s.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnAddingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BranchiListActivity.this, BranchProfileActivity.class);
                startActivity(intent);
            }
        });

//        Hard coded users
//        userList.add(new User("Nafly", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));
//        userList.add(new User("Razan", "0772489767", "url", "Akkaraippaththu", "14/12/2023"));
//        userList.add(new User("Hisham", "0772489767", "url", "Colombo", "14/12/2023"));
//        userList.add(new User("Rowf", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));
//        userList.add(new User("Saathik", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(BranchiListActivity.this, FirstActivity.class));
                finish();
            }
        });


    }

    public void collectDhayi(List<Branch> myUsers){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUsers.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Branch branch = ds.getValue(Branch.class);
                    myUsers.add(branch);
                }

                branchAdapter = new BranchAdapter(BranchiListActivity.this, myUsers);
                recyclerView.setLayoutManager(new LinearLayoutManager(BranchiListActivity.this));
                recyclerView.setAdapter(branchAdapter);
                myLoading.loadingDismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
            }
        });
    }


}