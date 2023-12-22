package com.sltj.jummah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private EditText etUsername, etPhone, etName;
    private Button btnAdd, btnBack;
    private CircleImageView ciProfile;

    private MyLoading myLoading;

    List<String> existsUsers = new ArrayList<>();

    private Uri imageUri;
    private StorageReference storageProfileImgRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        myLoading = new MyLoading(this);
        myLoading.loadingStart();

        etUsername = findViewById(R.id.etusername);
        etPhone = findViewById(R.id.etphone);
        etName = findViewById(R.id.etname);
        ciProfile = findViewById(R.id.circleImageView);

        btnAdd = findViewById(R.id.btnadduser);
        btnBack = findViewById(R.id.btnbackfromprofile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");

        storageProfileImgRef = FirebaseStorage.getInstance().getReference().child("profile");

        getExistsUsernames();

        ciProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickIntent = new Intent(Intent.ACTION_PICK);
                imagePickIntent.setType("image/*");
                startActivityForResult(imagePickIntent, 1);
            }
        });

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
                String profile = "NA";
                String lastJummah = "NA";
                String lastJummahDate = "NA";


                if(TextUtils.isEmpty(username)){
                    etUsername.setError("Required");

                }if(TextUtils.isEmpty(name)){
                    etName.setError("Required");
                }


                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(name)){
                    myLoading.loadingStart();
                    if(TextUtils.isEmpty(phone)){
                        phone = "NA";
                    }

                    if(existsUsers.contains(username)){
                        myLoading.loadingDismiss();
                        Toast.makeText(getApplicationContext(), "username already exists!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    User user = new User(name, phone, profile, lastJummah, lastJummahDate, username, password);

                    userRef.child(username).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        myLoading.loadingDismiss();
                                        uploadProfileImage(user);

//                                        goToDhayiListPage();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void getExistsUsernames(){
        myLoading.loadingStart();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
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
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imguri = data.getData();
            imageUri = imguri;
            ciProfile.setImageURI(imageUri);
        }
    }

    public void uploadProfileImage(User user){
        if(imageUri != null){
            myLoading.loadingStart();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

            }
            catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte [] data = baos.toByteArray();
            StorageReference imgRef = storageProfileImgRef.child(user.getUsername());
            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    myLoading.loadingDismiss();

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userRef.child(user.getUsername()).child("profile").setValue(uri.toString());
                            Toast.makeText(getApplicationContext(), "User Added Successfully", Toast.LENGTH_LONG).show();
                            myLoading.loadingDismiss();
                            goToDhayiListPage();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            myLoading.loadingDismiss();
                        }
                    });

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "User Added Successfully", Toast.LENGTH_LONG).show();
            myLoading.loadingDismiss();
            goToDhayiListPage();
        }
    }
}