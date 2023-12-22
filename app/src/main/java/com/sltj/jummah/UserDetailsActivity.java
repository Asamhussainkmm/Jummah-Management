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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private EditText etUsername, etPhone, etName;
    private Button btnAdd, btnBack, btnDelete;

    private CircleImageView ciProfile;

    MyLoading myLoading;

    private Uri imageUri;
    private StorageReference storageProfileImgRef;

    private String mUsername, mPassword, mName, mPhone, mProfile, mJummah, mJummahDate;

    private boolean isEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        myLoading = new MyLoading(this);

        Intent intent = getIntent();
        if(intent != null){
            mUsername = intent.getStringExtra("USERNAME");
            mPassword = intent.getStringExtra("PASSWORD");
            mName = intent.getStringExtra("NAME");
            mPhone = intent.getStringExtra("PHONE");
            mProfile = intent.getStringExtra("PROFILE");
            mJummah = intent.getStringExtra("JUMMAH");
            mJummahDate = intent.getStringExtra("JUMMAH_DATE");

        }

        etUsername = findViewById(R.id.etusername);
        etPhone = findViewById(R.id.etphone);
        etName = findViewById(R.id.etname);

        ciProfile = findViewById(R.id.circleImageView);

        btnAdd = findViewById(R.id.btnadduser);
        btnBack = findViewById(R.id.btnbackfromprofile);
        btnDelete = findViewById(R.id.btndelete);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");


        storageProfileImgRef = FirebaseStorage.getInstance().getReference().child("profile");

        if(mProfile.toLowerCase().equals("na")){
            ciProfile.setImageResource(R.drawable.profile_default);
        }
        else {
            Glide.with(UserDetailsActivity.this).load(mProfile).into(ciProfile);
        }


        etUsername.setText(mUsername);
        etUsername.setEnabled(false);
        etName.setText(mName);
        etPhone.setText(mPhone);

        etName.setEnabled(false);
        disabledTextColor(etName);
        etPhone.setEnabled(false);
        disabledTextColor(etPhone);
        ciProfile.setEnabled(false);
        btnDelete.setBackground(getResources().getDrawable(R.drawable.button_background, getTheme()));


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
                    enabledTextColor(etName);
                    etPhone.setEnabled(true);
                    enabledTextColor(etPhone);
                    ciProfile.setEnabled(true);
                    btnAdd.setText("Update");
                    btnDelete.setEnabled(false);
                    btnDelete.setBackground(getResources().getDrawable(R.drawable.disabled_button_background, getTheme()));

                }
                else {
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

        ciProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickIntent = new Intent(Intent.ACTION_PICK);
                imagePickIntent.setType("image/*");
                startActivityForResult(imagePickIntent, 1);
            }
        });
    }

    public void goToDhayiListPage(){
        onBackPressed();
    }

    public void deleteUser(String username){
        myLoading.loadingStart();
        userRef.child(username).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myLoading.loadingDismiss();
                Toast.makeText(getApplicationContext(), "User Deleted Successfully", Toast.LENGTH_LONG).show();
                goToDhayiListPage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myLoading.loadingDismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateUser(){
        String username = etUsername.getText().toString().trim().toLowerCase();
        String password = "NA";
        String name = etName.getText().toString().trim().toUpperCase();
        String phone = etPhone.getText().toString().trim();
        String profile = mProfile;
        String lastJummah = mJummah;
        String lastJummahDate = mJummahDate;


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


            User user = new User(name, phone,profile, lastJummah, lastJummahDate, username, password);
            userRef.child(username).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                isEnabled = false;
                                etName.setEnabled(false);
                                etPhone.setEnabled(false);
                                disabledTextColor(etName);
                                disabledTextColor(etPhone);
                                ciProfile.setEnabled(false);
                                btnAdd.setText("Edit");
                                btnDelete.setEnabled(true);
                                btnDelete.setBackground(getResources().getDrawable(R.drawable.button_background, getTheme()));
                                myLoading.loadingDismiss();
                                uploadProfileImage(user);
                                Toast.makeText(getApplicationContext(), "User Updated Successfully", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                myLoading.loadingDismiss();
                            }
                        }
                    });
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
                    Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imguri = data.getData();
            imageUri = imguri;
            ciProfile.setImageURI(imageUri);
        }
    }

    public void disabledTextColor(EditText editText){
        editText.setTextColor(getResources().getColor(R.color.other_color2, getTheme()));
    }

    public void enabledTextColor(EditText editText){
        editText.setTextColor(getResources().getColor(R.color.text_color, getTheme()));
    }

}