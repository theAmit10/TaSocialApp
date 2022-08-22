package com.example.tasocialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasocialapp.Model.FollowModel;
import com.example.tasocialapp.Model.NotificationModel;
import com.example.tasocialapp.Model.UserModel;
import com.example.tasocialapp.R;
import com.example.tasocialapp.databinding.AllUsersSampleBinding;
import com.example.tasocialapp.databinding.NotificationTabSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.viewHolder> {

    Context context;

    public AllUsersAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    ArrayList<UserModel> list;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_users_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // here user is that user which are going to be follow
        UserModel user = list.get(position);
        // we are binding data from online database so we have to use picasso
        Picasso.get()
                .load(user.getProfilePhoto())
                .placeholder(R.drawable.ic_no_image_found)
                .into(holder.binding.profile);

        // here comments comments and timeS means name and profession
        holder.binding.name.setText(user.getName());
        holder.binding.timeS.setText(user.getProfession());


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                        .child(user.getUserID())
                                .child("followers")
                                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_bg));
                            holder.binding.followButton.setText("Following");
                            holder.binding.followButton.setTextColor(context.getResources().getColor(R.color.black));
                            holder.binding.followButton.setEnabled(false);
                        }else {
                            // for follow

                            holder.binding.followButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FollowModel followModel = new FollowModel();
                                    followModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    followModel.setFollowedAt(new Date().getTime());



                                    //setting following
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(followModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(user.getUserID())
                                                            .child("followerCount")
                                                            .setValue(user.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_bg));
                                                                    holder.binding.followButton.setText("Following");
                                                                    holder.binding.followButton.setTextColor(context.getResources().getColor(R.color.black));
                                                                    holder.binding.followButton.setEnabled(false);
                                                                    Toast.makeText(context, "You Followed" +user.getName(), Toast.LENGTH_SHORT).show();

                                                                    // for notification
                                                                    NotificationModel notificationModel = new NotificationModel();
                                                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationModel.setNotificationAt(new Date().getTime());
                                                                    notificationModel.setType("follow");

                                                                    // adding notification to the database
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(user.getUserID())
                                                                            .push()
                                                                            .setValue(notificationModel);

                                                                }
                                                            });
                                                }
                                            });

                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        AllUsersSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AllUsersSampleBinding.bind(itemView);
        }
    }
}
