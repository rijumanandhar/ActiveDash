package com.example.activedash.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.activedash.run.RunFragment;
import com.example.activedash.profile.ProfileFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new RunFragment();
        }else{
            return new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
