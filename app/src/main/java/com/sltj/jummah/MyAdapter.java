package com.sltj.jummah;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    List<Jummah> jummahList;

    List<Jummah> filteredList;

    public MyAdapter(Context context, List<Jummah> jummahList) {
        this.context = context;
        this.jummahList = jummahList;
        this.filteredList = new ArrayList<>(jummahList);
    }

    public void filter(String text) {
        if(text != null){
            text = text.toLowerCase(Locale.getDefault());
            filteredList.clear();
            if (text.length() == 0) {
                filteredList.addAll(jummahList);
            } else {
                for (Jummah jummah : jummahList) {
                    if (jummah.getBranch().getName().toLowerCase(Locale.getDefault()).contains(text) || jummah.getUser().getName().toLowerCase(Locale.getDefault()).contains(text)) {
                        filteredList.add(jummah);
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter that the data set has changed
        }
        else {
            text = "";
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Jummah jummah = filteredList.get(position);
        holder.branchName.setText(jummah.getBranch().getName());
        holder.dhaayiName.setText("Name: " + jummah.getUser().getName());
        holder.updatedDate.setText("Jummah Date: " + jummah.getDate());
        holder.dhaayiNumber.setText("Dhaee Contact: " + jummah.getUser().getPhone());
        holder.branchNumber.setText("Branch Contact: " + jummah.getBranch().getPhone());
        if (jummah.getUser().getProfile().toLowerCase().equals("na")) {
            holder.profileUrl.setImageResource(R.drawable.profile_default);
        } else {
            Glide.with(context).load(jummah.getUser().getProfile()).into(holder.profileUrl);
        }

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

        if (formattedDate.equals(jummah.getDate())) {
            holder.giNew.setVisibility(View.VISIBLE);
        } else {
            holder.giNew.setVisibility(View.INVISIBLE);
        }

        if(holder.firebaseUser != null){
            holder.rlJummahCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), JummahUpdateActivity.class);
                    intent.putExtra("BRANCH", jummah.getBranch().getUsername());
                    intent.putExtra("DHAEE", jummah.getUser().getUsername());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
}
