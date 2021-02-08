package com.coastsnap.beachmonitoring.ui.about_us;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.coastsnap.beachmonitoring.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutUsFragment extends Fragment {

    private AboutUsViewModel aboutUsViewModel;
    private String mDescription[] = {"Facebook Description", "Instagram Description", "Twitter Description", "Youtube Description", "Github Description"};
    private int images[] = {R.drawable.facebook_logo, R.drawable.instagram_logo, R.drawable.twitter_logo, R.drawable.youtube_logo, R.drawable.github_logo};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutUsViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        return root;
    }

}