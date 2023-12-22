package com.sltj.jummah;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

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

public class DhayiListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton btnAddingUser;
    private List<User> userList = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText etSearch;

    private UserAdapter userAdapter;

    private MyLoading myLoading;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhayi_list);
        myLoading = new MyLoading(this);
        myLoading.loadingStart();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        recyclerView = (RecyclerView) findViewById(R.id.rvuserlist);
        btnAddingUser = (FloatingActionButton) findViewById(R.id.fbadduser);
        etSearch = (EditText)findViewById(R.id.etsearch);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        collectDhayi(userList);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(DhayiListActivity.this, FirstActivity.class));
                finish();
            }
        });

        if(firebaseUser != null){
            btnAddingUser.setVisibility(View.VISIBLE);
        }
        else {
            btnAddingUser.setVisibility(View.GONE);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null){
                    userAdapter.filter(s.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnAddingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DhayiListActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

//        Hard coded users
//        userList.add(new User("Nafly", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));
//        userList.add(new User("Razan", "0772489767", "url", "Akkaraippaththu", "14/12/2023"));
//        userList.add(new User("Hisham", "0772489767", "url", "Colombo", "14/12/2023"));
//        userList.add(new User("Rowf", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));
//        userList.add(new User("Saathik", "0772489767", "url", "Sainthamaruthu", "14/12/2023"));




    }

    public void collectDhayi(List<User> myUsers){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUsers.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    myUsers.add(user);
                }

                userAdapter = new UserAdapter(DhayiListActivity.this, myUsers);
                recyclerView.setLayoutManager(new LinearLayoutManager(DhayiListActivity.this));
                recyclerView.setAdapter(userAdapter);
                myLoading.loadingDismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
            }
        });
    }

}