package com.secondwind.android;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    TextView userNameView, userEmailView, userIdView;
    ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameView = (TextView) view.findViewById(R.id.name);
        userEmailView = (TextView) view.findViewById(R.id.email);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("sharedPrefs", this.getContext().MODE_PRIVATE);

        userNameView.setText(sharedPreferences.getString("userName", ""));
        userEmailView.setText(sharedPreferences.getString("userEmail", ""));

        Uri profileImageUri = Uri.parse(sharedPreferences.getString("userPhotoUrl", ""));

        try {
            Glide.with(this.getContext()).load(profileImageUri).into(profileImage);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "image not found", Toast.LENGTH_LONG).show();
        }

        return view;
    }
}