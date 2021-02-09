package com.coastsnap.beachmonitoring.ui.about_us;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.coastsnap.beachmonitoring.R;
import java.util.ArrayList;
import java.util.List;

public class AboutUsFragment extends Fragment {

    private List<Contact> listContact;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AboutUsViewModel aboutUsViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView listViewContact = view.findViewById(R.id.listViewID);

        CustomAdapter adapter = new CustomAdapter(getActivity(), getData());
        listViewContact.setAdapter(adapter);

        listViewContact.setOnItemClickListener((adapterView, view1, i, l) -> {
            Contact c = listContact.get(i);
            //Toast.makeText(getActivity(), c.getSocialMediaName(), Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse(c.getUriSocialMedia());
            Intent toWebsite = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(toWebsite);
        });
    }

    private List<Contact> getData() {
        listContact = new ArrayList<>();
        listContact.add(new Contact(1, R.drawable.facebook_logo, "Facebook", "Facebook Page", "https://www.facebook.com/coastsnap"));
        listContact.add(new Contact(2, R.drawable.instagram_logo, "Instagram", "Instagram Page", "https://www.instagram.com/espol_coastsnap_ec/"));
        listContact.add(new Contact(3, R.drawable.twitter_logo, "Twitter", "Twitter Page", "https://twitter.com/espol_coastsnap"));
        listContact.add(new Contact(4, R.drawable.youtube_logo, "YouTube", "YouTube Page", "https://www.youtube.com/user/mitchellharley"));
        listContact.add(new Contact(5, R.drawable.github_logo, "GitHub", "GitHub Page", "https://github.com/coastsnap-espol-ec/CoastSnap-Valdivia-EC"));

        return listContact;
    }
}