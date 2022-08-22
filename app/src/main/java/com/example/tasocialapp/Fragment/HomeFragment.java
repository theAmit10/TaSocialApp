package com.example.tasocialapp.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.tasocialapp.Adapter.PostAdapter;
import com.example.tasocialapp.Adapter.StoryAdapter;
import com.example.tasocialapp.Model.PostModel;
import com.example.tasocialapp.Model.StoryModel;
import com.example.tasocialapp.Model.UserStories;
import com.example.tasocialapp.R;
import com.example.tasocialapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    RecyclerView storyRv;
    //    RecyclerView dashboardRv;
    ShimmerRecyclerView dashboardRv;

    // used to open gallery
    ActivityResultLauncher<String> galleryLauncher;


//    FragmentHomeBinding binding;

    ArrayList<StoryModel> list;
    ArrayList<PostModel> postList;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    RoundedImageView addStoryImage;

    // used when something is loading
    ProgressDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // this  is for DashboardStory view
        dashboardRv = view.findViewById(R.id.dashboardRV);
        dashboardRv.showShimmerAdapter();

        // binding = FragmentHomeBinding.inflate(inflater,container, false);

        // for dialog
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading...");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        // this is for status story view only
        storyRv = view.findViewById(R.id.storyR);


        //storyRv = binding.storyR;


        list = new ArrayList<>();
//        list.add(new StoryModel(R.mipmap.nature, R.drawable.ic_baseline_online_prediction_24, R.mipmap.user1, "amit"));
//        list.add(new StoryModel(R.mipmap.nature, R.drawable.ic_baseline_online_prediction_24, R.mipmap.user1, "amit"));
//        list.add(new StoryModel(R.mipmap.nature, R.drawable.ic_baseline_online_prediction_24, R.mipmap.user1, "amit"));
//        list.add(new StoryModel(R.mipmap.nature, R.drawable.ic_baseline_online_prediction_24, R.mipmap.user1, "amit"));
//        list.add(new StoryModel(R.mipmap.nature, R.drawable.ic_baseline_online_prediction_24, R.mipmap.user1, "amit"));


        StoryAdapter adapter = new StoryAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);
        storyRv.setAdapter(adapter);


        // for story image


        // adding stories image to the recyclerview

        database.getReference()
                .child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                                StoryModel storyModel = new StoryModel();
                                storyModel.setStoryBy(storySnapshot.getKey());
                                storyModel.setStoryAt(storySnapshot.child("postedBy").getValue(long.class));

                                ArrayList<UserStories> stories = new ArrayList<>();
                                for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()) {
                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                    stories.add(userStories);
                                }
                                storyModel.setStories(stories);
                                list.add(storyModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // this  is for DashboardStory view
//        dashboardRv = view.findViewById(R.id.dashboardRV);
//        dashboardRv = binding.dashboardRV;
        postList = new ArrayList<>();


        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashboardRv.setLayoutManager(layoutManager);
        dashboardRv.setNestedScrollingEnabled(false);
//        dashboardRv.setAdapter(postAdapter);

        // getting data from the database
        database.getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            PostModel postModel = dataSnapshot.getValue(PostModel.class);
                            // setting up post unquie id
                            postModel.setPostId(dataSnapshot.getKey());
                            postList.add(postModel);
                        }
                        dashboardRv.setAdapter(postAdapter);
                        dashboardRv.hideShimmerAdapter();
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        addStoryImage = view.findViewById(R.id.storyImage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });

        // for opening gallery
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        addStoryImage.setImageURI(result);

                        dialog.show();

                        // for storing that image to the storage
                        final StorageReference reference = storage.getReference()
                                .child("stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(new Date().getTime() + "");
                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        StoryModel storyModel = new StoryModel();
                                        storyModel.setStoryAt(new Date().getTime());

                                        database.getReference().child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("postedBy")
                                                .setValue(storyModel.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        UserStories userStories = new UserStories(uri.toString(), storyModel.getStoryAt());

                                                        database.getReference().child("stories")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .child("userStories")
                                                                .push()
                                                                .setValue(userStories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        });

                    }
                });


        return view;
    }
}