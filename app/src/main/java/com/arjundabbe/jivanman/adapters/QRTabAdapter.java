package com.arjundabbe.jivanman.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.arjundabbe.jivanman.ui.fragments.MyCodeFragment;
import com.arjundabbe.jivanman.ui.fragments.ScanCodeFragment;

public class QRTabAdapter extends FragmentStateAdapter {

    public QRTabAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new MyCodeFragment() : new ScanCodeFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
