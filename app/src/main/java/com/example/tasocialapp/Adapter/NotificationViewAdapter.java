package com.example.tasocialapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasocialapp.Fragment.NotificationFragment;
import com.example.tasocialapp.Fragment.NotificationTabFragment;
import com.example.tasocialapp.Fragment.RequestTabFragment;

public class NotificationViewAdapter extends FragmentStateAdapter {

    private String[] titles = {"NOTIFICATION", "REQUEST"};

    public NotificationViewAdapter(@NonNull NotificationFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new NotificationTabFragment();
            case 1:
                return new RequestTabFragment();
        }
        return new NotificationTabFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


}
