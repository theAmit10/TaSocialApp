package com.example.tasocialapp.Adapter;

import static com.example.tasocialapp.R.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasocialapp.CommentTaActivity;
import com.example.tasocialapp.Model.NotificationModel;
import com.example.tasocialapp.Model.UserModel;
import com.example.tasocialapp.R;
import com.example.tasocialapp.databinding.NotificationTabSampleBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationTabAdapter extends RecyclerView.Adapter<NotificationTabAdapter.viewHolder> {

    ArrayList<NotificationModel> list;
    Context context;
//    private final String notificationTitle = "Notifiacation";
//    private final int notificationId = 1;

    public NotificationTabAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout.notification_tab_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel notificationModel = list.get(position);

//        holder.profile.setImageResource(model.getProfile());
//        holder.notiAbout.setText(Html.fromHtml(model.getNotiAbout()));
//        holder.time.setText(model.getTime());
        // addding notification from the database

        String type = notificationModel.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notificationModel.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(drawable.ic_no_image_found)
                                .into(holder.binding.profile);
                        String text = TimeAgo.using(notificationModel.getNotificationAt());
                        holder.binding.time.setText(text);

                        if (type.equals("like")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" + " liked your post"));
                        } else if (type.equals("comment")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" + " commented on your post"));
                        } else {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" + " started following you"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if(!type.equals("follow")) we are doing this because in the follow notification we dont have postid and postedby
                if (!type.equals("follow")) {

                    // if the user open the notification then we are going to change the checkedOpen value false
                    FirebaseDatabase.getInstance().getReference().child("notification")
                            .child(notificationModel.getPostedBy())
                            .child(notificationModel.getNotificationID())
                            .child("checkOpen")
                            .setValue(true);


                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentTaActivity.class);
                    // sending two data to the comment avtivity
                    intent.putExtra("postId", notificationModel.getPostID());
                    intent.putExtra("postedBy", notificationModel.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        Boolean checkOpen = notificationModel.isCheckOpen();

        if (checkOpen == true) {
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
//        ImageView profile;
//        TextView notiAbout, time;

        NotificationTabSampleBinding binding;

        public viewHolder(@NonNull View itemView) {

            super(itemView);

//            profile = itemView.findViewById(id.profile);
//            notiAbout = itemView.findViewById(id.notiAbout);
//            time = itemView.findViewById(id.time);
            binding = NotificationTabSampleBinding.bind(itemView);
        }
    }


}
