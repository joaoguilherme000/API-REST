package com.atividade.mycity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProblemasFragment();
            case 1:
                return new ResolvidosFragment();
            default:
                return new ProblemasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}