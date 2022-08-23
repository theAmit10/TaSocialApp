package com.example.tasocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tasocialapp.Adapter.CommentAdapter;
import com.example.tasocialapp.Model.CommentModel;
import com.example.tasocialapp.Model.NotificationModel;
import com.example.tasocialapp.Model.PostModel;
import com.example.tasocialapp.Model.UserModel;

import com.example.tasocialapp.databinding.ActivityCommentTaBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class CommentTaActivity extends AppCompatActivity {

    ActivityCommentTaBinding binding;
    Intent intent;
    String postId;
    String postedBy;

    FirebaseAuth auth;
    FirebaseDatabase database;

    ArrayList<CommentModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentTaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // creating Intent Object to get the data which are comming from the intent
        intent = getIntent();

        // setting toolbar for comment activity
        setSupportActionBar(binding.toolbar2);
        CommentTaActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getting two data
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel postModel = snapshot.getValue(PostModel.class);
                        Picasso.get()
                                .load(postModel.getPostImage())
                                .placeholder(R.drawable.ic_no_image_found)
                                .into(binding.commentPostImage);
                        binding.postDescription.setText(postModel.getPostDescription());
                        binding.like.setText(postModel.getPostLike()+"");
                        binding.comment.setText(postModel.getCommentCount()+"");



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference()
                .child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(R.drawable.ic_no_image_found)
                                .into(binding.profile);
                        // in the below case comments is actually a name
                        binding.name.setText(userModel.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // for post comment
        binding.commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentModel commentModel = new CommentModel();
                commentModel.setCommentBody(binding.commentTexts.getText().toString());
                commentModel.setCommentedBy(FirebaseAuth.getInstance().getUid());
                commentModel.setCommentedAt(new Date().getTime());

                // crateting a comment section in the database
                // and storing data in the database
                database.getReference().child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // getting total number of comments
                                database.getReference().child("posts")
                                        .child(postId)
                                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if(snapshot.exists()){
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentCount")
                                                        .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                binding.commentTexts.setText("");
                                                                Toast.makeText(CommentTaActivity.this, "Commented Successfully...", Toast.LENGTH_SHORT).show();


                                                                // for notification

                                                                NotificationModel notificationModel = new NotificationModel();
                                                                notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notificationModel.setNotificationAt(new Date().getTime());
                                                                notificationModel.setPostID(postId);
                                                                notificationModel.setPostedBy(postedBy);
                                                                notificationModel.setType("comment");

                                                                // for storing database
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notificationModel);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });


        //  setting up adapter
        CommentAdapter commentAdapter = new CommentAdapter(this,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commentRV.setLayoutManager(linearLayoutManager);
        binding.commentRV.setAdapter(commentAdapter);


        // now getting comments
        database.getReference().child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            CommentModel commentModel = dataSnapshot.getValue(CommentModel.class);
                            list.add(commentModel);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





    }


    // to close comment activity from the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}