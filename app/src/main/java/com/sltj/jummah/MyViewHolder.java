package com.sltj.jummah;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView branchName, dhaayiName, updatedDate, dhaayiNumber, branchNumber;
    ImageView profileUrl;

    RelativeLayout rlJummahCard;

    FirebaseUser firebaseUser;

    GifImageView giNew;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        branchName = itemView.findViewById(R.id.tvlistbranchname);
        dhaayiName = itemView.findViewById(R.id.tvlistdhaayiname);
        updatedDate = itemView.findViewById(R.id.tvlistupdatedate);
        profileUrl = itemView.findViewById(R.id.ivlistprofile);
        dhaayiNumber = itemView.findViewById(R.id.tvlistdhaayinumber);
        branchNumber = itemView.findViewById(R.id.tvlistbranchphone);
        giNew = itemView.findViewById(R.id.ginew);
        rlJummahCard = itemView.findViewById(R.id.rljummah);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }
}
