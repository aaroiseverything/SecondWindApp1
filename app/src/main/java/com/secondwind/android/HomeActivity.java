package com.secondwind.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;

    String userName;
    String userEmail;
    String userGoogleId;
    Uri userPhotoUrl;

    Member member;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DatabaseReference rref;

    String firebaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        firebaseKey = sharedPreferences.getString("firebaseKey", "");

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        rref = FirebaseDatabase.getInstance().getReference().child("Members");

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Initialise google variables
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialise Logout btn in menu
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

        // Initialise menu
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
    protected void onStart() {
        super.onStart();
        String loginMethod = sharedPreferences.getString("loginMethod", "");

        switch (loginMethod) {
            case "google":
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
                if (opr.isDone()) {
                    GoogleSignInResult result = opr.get();
                    googleSignInResult(result);
                } else {
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                            googleSignInResult(googleSignInResult);
                        }
                    });
                }
                break;
            case "auth":
                authSignInResult();
                break;
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
    private void googleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // add to shared preferences
            GoogleSignInAccount account = result.getSignInAccount();
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            userGoogleId = account.getId();
            userPhotoUrl = account.getPhotoUrl();

            editor.putString("userName", userName);
            editor.putString("userEmail", userEmail);
            editor.putString("userGoogleId", userGoogleId);
            editor.putString("userPhotoUrl", userPhotoUrl.toString());

            editor.apply();

            // update header
            updateHeader(userName, userEmail, userPhotoUrl);

            // add to firebase users
            member = new Member();
            member.setUsername(userName);
            member.setEmail(userEmail);
            member.setId(userGoogleId);
            member.setPhotoUrl(userPhotoUrl.toString());
            member.setLoginType("google");

            rref.orderByChild("email").equalTo(member.getEmail()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String key;
                            if (dataSnapshot.exists()) {
                                key = dataSnapshot.getChildren().iterator().next().getKey();

                            } else {
                                // new user
                                key = rref.push().getKey();
                            }
                            editor.putString("firebaseKey", key);
                            editor.apply();

                            Map<String, Object> userUpdates = new HashMap<>();

                            userUpdates.put(key + "/username", userName);
                            userUpdates.put(key + "/googleId", userGoogleId);
                            userUpdates.put(key + "/photoUrl", userPhotoUrl.toString());
                            userUpdates.put(key + "/loginType", "google");
                            userUpdates.put(key + "/email", userEmail);

                            rref.updateChildren(userUpdates);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    }
            );

        } else {
            gotoMainActivity();
        }
    }

//    public void updateFirebaseProfiling(String field, String input) {
//        Map<String, Object> userUpdates = new HashMap<>();
//        userUpdates.put(firebaseKey + field, input);
//        rref.updateChildren(userUpdates);
//    }

    private void authSignInResult() {

        // user is already added to db during sign up
        userEmail = sharedPreferences.getString("userEmail", "");

        rref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            member = appleSnapshot.getValue(Member.class);
                            String loginType = member.getLoginType().trim();
                            if (loginType.equals("auth")) {
                                String key = appleSnapshot.getKey();
                                editor.putString("firebaseKey", key);

                                userName = member.getUsername();
                                String userPhotoUrlString = member.getPhotoUrl();
                                editor.putString("userPhotoUrl", userPhotoUrlString);
                                editor.putString("userName", userName);
                                editor.apply();

                                updateHeader(userName, userEmail, Uri.parse(userPhotoUrlString));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
        );
    }


    private void updateHeader(String userName, String userEmail, Uri userPhotoUrl) {
        // add to header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        TextView userNameView = (TextView) header.findViewById(R.id.menu_name);
        TextView userEmailView = (TextView) header.findViewById(R.id.menu_email);
        ImageView userPhotoView = (ImageView) header.findViewById(R.id.menu_photo);

        userNameView.setText(userName);
        userEmailView.setText(userEmail);

        if (userPhotoUrl.toString().length() <= 0) {
            return;
        }

        try {
            Glide.with(this).load(userPhotoUrl).into(userPhotoView);
        } catch (NullPointerException e) {
            Toast.makeText(this, "image not found", Toast.LENGTH_LONG).show();
        }
    }


    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
