package com.fariha.deshistore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        android.util.Log.d("AdminPagerAdapter", "createFragment called for position: " + position);
        switch (position) {
            case 0:
                android.util.Log.d("AdminPagerAdapter", "Creating UsersFragment");
                return new UsersFragment();
            case 1:
                android.util.Log.d("AdminPagerAdapter", "Creating CompanyVendorsFragment");
                return new CompanyVendorsFragment();
            case 2:
                android.util.Log.d("AdminPagerAdapter", "Creating RetailVendorsFragment");
                return new RetailVendorsFragment();
            case 3:
                android.util.Log.d("AdminPagerAdapter", "Creating ProductApprovalsFragment");
                return new ProductApprovalsFragment();
            default:
                android.util.Log.d("AdminPagerAdapter", "Creating default UsersFragment");
                return new UsersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
