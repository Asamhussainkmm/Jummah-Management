package com.sltj.jummah;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    CircleImageView ciProfile;
    TextView tvName, tvPhone, tvBranch, tvBranchDate, tvJummah;
    RelativeLayout rlItem;

    FirebaseUser firebaseUser;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ciProfile = itemView.findViewById(R.id.cvprofile);
        tvBranch = itemView.findViewById(R.id.tvbranch);
        tvName = itemView.findViewById(R.id.tvname);
        tvPhone = itemView.findViewById(R.id.tvphone);
        tvJummah = itemView.findViewById(R.id.tvjummah);
        tvBranchDate = itemView.findViewById(R.id.tvbranchdate);
        rlItem = itemView.findViewById(R.id.rlitem);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
