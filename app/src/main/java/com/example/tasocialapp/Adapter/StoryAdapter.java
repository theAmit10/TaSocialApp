package com.example.tasocialapp.Adapter;

import static com.example.tasocialapp.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasocialapp.Model.StoryModel;
import com.example.tasocialapp.Model.UserModel;
import com.example.tasocialapp.Model.UserStories;
import com.example.tasocialapp.R;
import com.example.tasocialapp.databinding.StoryRvDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {

    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout.story_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        StoryModel storyModel = list.get(position);
//        holder.storyImage.setImageResource(model.getStory());
//        holder.profileImage.setImageResource(model.getProfile());
//        holder.storyType.setImageResource(model.getStorytype());
//        holder.name.setText(model.getUsername());

        // adding story image
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(drawable.ic_no_image_found)
                                .into(holder.binding.storyImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if (storyModel.getStories().size() > 0) {
            // getting the last image
            UserStories lastStory = storyModel.getStories().get(storyModel.getStories().size() - 1);

            Picasso.get()
                    .load(lastStory.getImage())
                    .into(holder.binding.storyImage);
            holder.binding.statusCircle.setPortionsCount(storyModel.getStories().size());

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(storyModel.getStoryBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(userModel.getProfilePhoto())
                                    .placeholder(drawable.ic_no_image_found)
                                    .into(holder.binding.profile);
                            holder.binding.username.setText(userModel.getName());

                            // setting on click listener to see stories
                            holder.binding.storyImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStories stories : storyModel.getStories()) {
                                        myStories.add(new MyStory(
                                                stories.getImage()
                                        ));
                                    }

                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(userModel.getName()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(userModel.getProfilePhoto()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();


                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        StoryRvDesignBinding binding;
//        ImageView storyImage, profileImage , storyType;
//        TextView name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

//            storyImage = itemView.findViewById(id.story);
//            profileImage = itemView.findViewById(id.profile);
//            storyType = itemView.findViewById(id.storytype);
//            name = itemView.findViewById(id.username);
            binding = StoryRvDesignBinding.bind(itemView);
        }
    }
}
