package com.example.tasocialapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tasocialapp.Adapter.NotificationTabAdapter;
import com.example.tasocialapp.Adapter.NotificationViewAdapter;
import com.example.tasocialapp.Model.NotificationModel;
import com.example.tasocialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationTabFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NotificationModel> list;
    FirebaseDatabase database;

    public NotificationTabFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_tab, container, false);

        recyclerView = view.findViewById(R.id.notificationTabRV);

        list = new ArrayList<>();
//        list.add(new NotificationModel(R.mipmap.user4,"<b>Tara Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.user3,"<b>Kabir Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.user2,"<b>komal Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.user1,"<b>Anamika Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.nature2,"<b>Shivam Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.nature,"<b>Bhagwan Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.user4,"<b>Money Singh</b> Mention you in a post", "just now"));
//        list.add(new NotificationModel(R.mipmap.user4,"<b>chiller Singh</b> Mention you in a post", "just now"));

        NotificationTabAdapter adapter = new NotificationTabAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            NotificationModel notificationModel = dataSnapshot.getValue(NotificationModel.class);
                            notificationModel.setNotificationID(dataSnapshot.getKey());
                            list.add(notificationModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





        return view;
    }
}