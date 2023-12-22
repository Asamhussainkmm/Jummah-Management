package com.sltj.jummah;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    Context context;
    List<User> userList = new ArrayList<>();

    List<User> filteredList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);
    }

    public void filter(String text) {
        if(text != null){
            text = text.toLowerCase(Locale.getDefault());
            filteredList.clear();
            if (text.length() == 0) {
                filteredList.addAll(userList);
            } else {
                for (User user : userList) {
                    if (user.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                        filteredList.add(user);
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter that the data set has changed
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = filteredList.get(position);
        if(user.getProfile().toLowerCase().equals("na")){
            holder.ciProfile.setImageResource(R.drawable.profile_default);
        }
        else {
            Glide.with(context).load(user.getProfile()).into(holder.ciProfile);
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

        if (formattedDate.equals(user.getLastJummahDate())) {
            holder.tvJummah.setText("Next Jummah: ");
        } else {
            holder.tvJummah.setText("Last Jummah: ");
        }

        holder.tvName.setText(user.getName());
        holder.tvPhone.setText("Contact: " + user.getPhone());
        holder.tvBranch.setText(user.getLastJummah());
        holder.tvBranchDate.setText(user.getLastJummahDate());

        if(holder.firebaseUser != null){
            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), UserDetailsActivity.class);
                    intent.putExtra("USERNAME", user.getUsername());
                    intent.putExtra("PASSWORD", user.getPassword());
                    intent.putExtra("NAME", user.getName());
                    intent.putExtra("PHONE", user.getPhone());
                    intent.putExtra("PROFILE", user.getProfile());
                    intent.putExtra("JUMMAH", user.getLastJummah());
                    intent.putExtra("JUMMAH_DATE", user.getLastJummahDate());
                    context.startActivity(intent);

//                Toast.makeText(context, user.getUsername(), Toast.LENGTH_LONG).show();

                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


}
