package com.example.tasocialapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tasocialapp.Adapter.NotificationViewAdapter;
import com.example.tasocialapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotificationFragment extends Fragment {

    NotificationViewAdapter notificationViewAdapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    private String[] titles = {"NOTIFICATION", "REQUEST"};

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        viewPager = view.findViewById(R.id.viewPager);


        tabLayout = view.findViewById(R.id.tablayout);


        notificationViewAdapter = new NotificationViewAdapter(this);

        viewPager.setAdapter(notificationViewAdapter);


        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]) )).attach();

        return view;
    }
}