package com.coastsnap.beachmonitoring.ui.about_us;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.camera.core.CameraX;

import com.coastsnap.beachmonitoring.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Contact> contactList;

    public CustomAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        CircleImageView imgContact;
        TextView nameSocial;
        TextView descriptionSocial;

        Contact contact = contactList.get(i);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row, null);

        }
        imgContact = view.findViewById(R.id.image);
        nameSocial = view.findViewById(R.id.title1);
        descriptionSocial = view.findViewById(R.id.subtitle1);

        imgContact.setImageResource(contact.getImg());
        nameSocial.setText(contact.getSocialMediaName());
        descriptionSocial.setText(contact.getDescription());

        return view;
    }
}
