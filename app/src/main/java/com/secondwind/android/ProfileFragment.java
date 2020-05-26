package com.secondwind.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private TextView userNameView, userEmailView;
    private ImageView profileImage;
    private FirebaseAuth mAuth;

    private Button editProfileBtn;

    private static final int CHOOSE_IMAGE = 101;
    Uri uriProfileImage;
    String profileImageUrl;
    String loginMethod;
    private String userEmail;

    private DatabaseReference rref;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String firebaseKey;


    private Member member;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = this.getContext().getSharedPreferences(getString(R.string.shared_prefs_name), this.getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        rref = FirebaseDatabase.getInstance().getReference().child("Members");
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userNameView = (TextView) view.findViewById(R.id.name);
        userEmailView = (TextView) view.findViewById(R.id.email);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);

        userEmail = sharedPreferences.getString(getString(R.string.shared_prefs_key_email), "");
        loginMethod = sharedPreferences.getString(getString(R.string.shared_prefs_key_login_method), "");

        userNameView.setText(sharedPreferences.getString(getString(R.string.shared_prefs_key_username), ""));
        userEmailView.setText(userEmail);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        //    profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sharedPreferences.getString("loginMethod", "") == "auth") {
//                    showImageChooser();
//                } else {
//                    Toast.makeText(getActivity(), "Unable to change Google Display Image", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        // set photo url
        String profileImageString = sharedPreferences.getString(getString(R.string.shared_prefs_key_user_photo_url), "");
        Uri profileImageUri = Uri.parse(profileImageString);
        try {
            Glide.with(this.getContext()).load(profileImageUri).into(profileImage);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), R.string.error_msg_image_not_found, Toast.LENGTH_LONG).show();
        }
//        if (profileImageString.length() <= 0) {
//            return view;
//        }
        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CHOOSE_IMAGE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
//            uriProfileImage = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriProfileImage);
//                profileImage.setImageBitmap(bitmap);
//                uploadImageToFirebaseStorage();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }


//    private void uploadImageToFirebaseStorage() {
//        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
//
//        if (uriProfileImage != null) {
//            profileImageRef.putFile(uriProfileImage)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            profileImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
//                            saveUserInfo();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//        }
//    }

//    private void saveUserInfo() {
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null && profileImageUrl != null) {
//            // shared preferences
//            editor.putString("userPhotoUrl", profileImageUrl);
//            editor.apply();
//
//            // firebase db
//            rref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(
//                    new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String key;
//                            if (dataSnapshot.exists()) {
//                                member.setPhotoUrl(profileImageUrl);
//                                key = dataSnapshot.getChildren().iterator().next().getKey();
//                                rref.child(key).setValue(member);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                        }
//                    }
//            );
//
//            // firebase auth
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setPhotoUri(Uri.parse(profileImageUrl))
//                    .build();
//            user.updateProfile(profile)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getActivity(), "Profile pic updated", Toast.LENGTH_LONG).show();                            }
//                        }
//                    });
//        }
//    }

    //    public void showImageChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMAGE);
//    }


}

