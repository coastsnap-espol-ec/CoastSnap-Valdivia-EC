package com.coastsnap.beachmonitoring.ui.about_us;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.coastsnap.beachmonitoring.R;

public class AboutUsFragment extends Fragment {

    private AboutUsViewModel aboutUsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutUsViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        return root;
    }
}