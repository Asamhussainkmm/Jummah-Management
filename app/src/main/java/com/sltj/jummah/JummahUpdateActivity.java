package com.sltj.jummah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class JummahUpdateActivity extends AppCompatActivity {

    private Spinner spinnerDhaayi, spinnerBranch;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef, branchRef, jummahRef;

    private List<String> exisitingUsers = new ArrayList<>();

    private List<String> exisitingBranch = new ArrayList<>();

    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> branchAdapter;

    private TextView tvName, tvLastJummah, tvLastUpdatedDate, tvBranchName, tvLastDhaayi, tvLastDhaayiDate;
    private Button btnAssign, btnBack;

    private CircleImageView circleImageView;

    private User user;
    private Branch branch;

    private MyLoading myLoading;
    private MyLoading myLoading2;

    private String branchUsername, userUsername;

    private ConstraintLayout clJummahUpdate;

    private TextView tvJummahBranch, tvJummahDhaee;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myLoading = new MyLoading(JummahUpdateActivity.this);
        myLoading2 = new MyLoading(JummahUpdateActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jummah_update);

        Intent intent = getIntent();
        if(intent != null){
            branchUsername = intent.getStringExtra("BRANCH");
            userUsername = intent.getStringExtra("DHAEE");

        } else {
            branchUsername = "null";
            userUsername = "null";
        }

        clJummahUpdate = findViewById(R.id.cljummahupdate);

        spinnerDhaayi = findViewById(R.id.spinnerdhaayi);
        spinnerBranch = findViewById(R.id.spinnerbranch);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");
        branchRef = firebaseDatabase.getReference().child("branch");
        jummahRef = firebaseDatabase.getReference().child("jummah");

        tvName = findViewById(R.id.tvname);
        tvLastJummah = findViewById(R.id.tvlastjummah);
        tvLastUpdatedDate = findViewById(R.id.tvupdateddate);
        circleImageView = findViewById(R.id.circleImageView);

        tvJummahBranch = findViewById(R.id.tvjummahbranch);
        tvJummahDhaee = findViewById(R.id.tvjummahdhaee);

        tvBranchName = findViewById(R.id.tvbranchname);
        tvLastDhaayi = findViewById(R.id.tvlastdhaayi);
        tvLastDhaayiDate = findViewById(R.id.tvlastdhaayidate);

        btnAssign = findViewById(R.id.btnassign);
        btnBack = findViewById(R.id.btnback);

        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        spinnerBranch.setAdapter(branchAdapter);
        spinnerDhaayi.setAdapter(adapter);
        getExistsUsernames();
        getExistsBranchUsernames();


        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
                selectBranch(selectedName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinnerDhaayi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
                selectDhaayi(selectedName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JummahUpdateActivity.this, HomePage.class));
                finish();
            }
        });

        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLoading2.loadingStart();
                // Get current date and calendar instance
                Calendar calendar = Calendar.getInstance();

                // Calculate the difference between current day (Sunday) and Friday (Calendar.FRIDAY = 6)
                int daysUntilFriday = Calendar.FRIDAY - calendar.get(Calendar.DAY_OF_WEEK);

                // If the current day is after Friday, calculate the next Friday
                if (daysUntilFriday < 0) {
                    daysUntilFriday += 7; // Add 7 days to get the next Friday
                }

                // Add days to current date to get the upcoming Friday date
                calendar.add(Calendar.DAY_OF_MONTH, daysUntilFriday);

                // Format the date as "Fri, 22/12/2023"
                SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

//                Toast.makeText(JummahAssignActivity.this, formattedDate, Toast.LENGTH_LONG).show();

                user.setLastJummah(branch.getName());
                user.setLastJummahDate(formattedDate);
                userRef.child(user.getUsername()).setValue(user);
                branch.setLastJummah(user.getName());
                branch.setLastJummahDate(formattedDate);
                branchRef.child(branch.getUsername()).setValue(branch);
                Jummah jummah = new Jummah(branch.getUsername(), user, branch, formattedDate);
                jummahRef.child(branch.getUsername()).setValue(jummah).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            clJummahUpdate.setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        }
                        else {
                            myLoading2.loadingDismiss();
                            Toast.makeText(JummahUpdateActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void selectDhaayi(String username){
        myLoading.loadingStart();
        userRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
//                Toast.makeText(JummahAssignActivity.this, user.getUsername(), Toast.LENGTH_LONG).show();
                tvName.setText(user.getName());
                tvLastJummah.setText(user.getLastJummah());
                tvLastUpdatedDate.setText(user.getLastJummahDate());
                if(user.getProfile().toLowerCase().equals("na")){
                    circleImageView.setImageResource(R.drawable.profile_default);
                }
                else {
                    Glide.with(JummahUpdateActivity.this).load(user.getProfile()).into(circleImageView);
                }
                String nextFriday = getNextFriday();
                if(nextFriday.equals(user.getLastJummahDate())){
                    tvJummahDhaee.setText("Next Jummah: ");
                }
                else{
                    tvJummahDhaee.setText("Last Jummah: ");
                }
                myLoading.loadingDismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
                Toast.makeText(JummahUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectBranch(String username){
        myLoading.loadingStart();
        branchRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branch = snapshot.getValue(Branch.class);
//                Toast.makeText(JummahAssignActivity.this, user.getUsername(), Toast.LENGTH_LONG).show();
                tvBranchName.setText(branch.getName());
                tvLastDhaayi.setText(branch.getLastJummah());
                tvLastDhaayiDate.setText(branch.getLastJummahDate());
                String nextFriday = getNextFriday();
                if(nextFriday.equals(branch.getLastJummahDate())){
                    tvJummahBranch.setText("Next Jummah: ");
                }
                else {
                    tvJummahBranch.setText("Last Jummah: ");
                }
                myLoading.loadingDismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
                Toast.makeText(JummahUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getExistsUsernames(){
        myLoading.loadingStart();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exisitingUsers.clear();
                adapter.clear();
                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    exisitingUsers.add(ds.getKey());
                    if(ds.getKey().equals(userUsername)){
                        spinnerDhaayi.setSelection(i);
                    }
                    i++;
                }
                adapter.addAll(exisitingUsers);
                myLoading.loadingDismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
                Toast.makeText(JummahUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getExistsBranchUsernames(){
        myLoading.loadingStart();
        branchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exisitingBranch.clear();
                branchAdapter.clear();
                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    exisitingBranch.add(ds.getKey());
                    if(ds.getKey().equals(branchUsername)){
                        spinnerBranch.setSelection(i);
                        spinnerBranch.setEnabled(false);
                    }
                    i++;
                }
                branchAdapter.addAll(exisitingBranch);
                myLoading.loadingDismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myLoading.loadingDismiss();
                Toast.makeText(JummahUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getNextFriday(){
        // Get current date and calendar instance
        Calendar calendar = Calendar.getInstance();

        // Calculate the difference between current day (Sunday) and Friday (Calendar.FRIDAY = 6)
        int daysUntilFriday = Calendar.FRIDAY - calendar.get(Calendar.DAY_OF_WEEK);

        // If the current day is after Friday, calculate the next Friday
        if (daysUntilFriday < 0) {
            daysUntilFriday += 7; // Add 7 days to get the next Friday
        }

        // Add days to current date to get the upcoming Friday date
        calendar.add(Calendar.DAY_OF_MONTH, daysUntilFriday);

        // Format the date as "Fri, 22/12/2023"
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        return formattedDate;
    }
}