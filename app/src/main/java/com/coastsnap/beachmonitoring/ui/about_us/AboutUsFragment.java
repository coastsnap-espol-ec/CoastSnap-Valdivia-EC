package com.coastsnap.beachmonitoring.ui.about_us;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    private ListView listViewContact;
    private List<Contact> listContact;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutUsViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listViewContact = view.findViewById(R.id.listViewID);

        CustomAdapter adapter = new CustomAdapter(getActivity(), getData());
        listViewContact.setAdapter(adapter);

        listViewContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact c = listContact.get(i);
                Toast.makeText(getActivity(), c.getSocialMediaName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Contact> getData() {
        listContact = new ArrayList<>();
        listContact.add(new Contact(1, R.drawable.facebook_logo, "Facebook", "Facebook Page"));
        listContact.add(new Contact(2, R.drawable.instagram_logo, "Instagram", "Instagram Page"));
        listContact.add(new Contact(3, R.drawable.twitter_logo, "Twitter", "Twitter Page"));
        listContact.add(new Contact(4, R.drawable.youtube_logo, "YouTube", "YouTube Page"));
        listContact.add(new Contact(5, R.drawable.github_logo, "GitHub", "GitHub Page"));

        return listContact;
    }
}