package com.secondwind.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    String userName;
    String userEmail;
    String userId;
    Uri userPhotoUrl;

    Member member;

//    TextView userNameView,userEmailView,userIdView;
//    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationView navigationView = findViewById(R.id.nav_view);
//        userNameView=(TextView)findViewById(R.id.name);
//        userEmailView=(TextView)findViewById(R.id.email);
//        userIdView=(TextView)findViewById(R.id.userId);
//        profileImage=(ImageView)findViewById(R.id.profileImage);

        // sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        navigationView.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(menuItem -> {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                gotoMainActivity();
                            } else {
                                Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            return true;
        });

        // menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_vid:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new VidFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // saves user data to shared preferences + show in nav header + add to firebase
    private void handleSignInResult(GoogleSignInResult result) {

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (result.isSuccess()) {

            // add to shared preferences
            GoogleSignInAccount account = result.getSignInAccount();
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            userId = account.getId();
            userPhotoUrl = account.getPhotoUrl();

            editor.putString("userName", userName);
            editor.putString("userEmail", userEmail);
            editor.putString("userId", userId);
            editor.putString("userPhotoUrl", userPhotoUrl.toString());

            editor.apply();

            // add to header
            NavigationView navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            TextView userNameView = (TextView) header.findViewById(R.id.menu_name);
            TextView userEmailView = (TextView) header.findViewById(R.id.menu_email);
            ImageView userPhotoView = (ImageView) header.findViewById(R.id.menu_photo);

            userNameView.setText(userName);
            userEmailView.setText(userEmail);

            try {
                Glide.with(this).load(userPhotoUrl).into(userPhotoView);
            } catch (NullPointerException e) {
                Toast.makeText(this, "image not found", Toast.LENGTH_LONG).show();
            }

            // add to firebase
            DatabaseReference rref;
            rref = FirebaseDatabase.getInstance().getReference().child("Members");

            member = new Member();
            member.setName(userName);
            member.setEmail(userEmail);
            member.setId(userId);
            member.setUri(userPhotoUrl);

            // adds new member to db if does not already exists, updates user data if exists
            rref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String key;
                            if (dataSnapshot.exists()) {
                                key = dataSnapshot.getChildren().iterator().next().getKey();
                            } else {
                                key = rref.push().getKey();
                            }
                            rref.child(key).setValue(member);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    }
            );
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
        } else {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
}
