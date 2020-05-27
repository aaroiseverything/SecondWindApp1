package com.secondwind.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.secondwind.android.classes.Member;
import com.secondwind.android.classes.Preferences;
import com.secondwind.android.signupactivities.MainActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.EditProfileBtnAddListener, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    String userName;
    String userEmail;
    String userGoogleId;
    Uri userPhotoUrl;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Member member;
    private SharedPreferences sharedPreferences;
    private NavigationView navigationView;
    SharedPreferences.Editor editor;
    DatabaseReference rref;
    String firebaseKey;
    private String loginMethod;
    private boolean savedInstanceStatePresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialise menu
        navigationView = findViewById(R.id.nav_view);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        loginMethod = sharedPreferences.getString(getString(R.string.shared_prefs_key_login_method), "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // prevent reload during rotation
        if (savedInstanceState != null) {
            savedInstanceStatePresent = true;
            return;
        }

        // initialise other variables
        editor = sharedPreferences.edit();
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");

        // default to Home when started
        Integer landingPointer = sharedPreferences.getInt(getString(R.string.landing_pointer), R.id.nav_home);
        loadFragment(landingPointer);
        navigationView.setCheckedItem(landingPointer);

        savedInstanceStatePresent = false;

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        rref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_db_members));

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
            if (loginMethod.equals(getString(R.string.login_type_auth))) {
                FirebaseAuth.getInstance().signOut();
                editor.clear();
                editor.putString(getString(R.string.shared_prefs_key_loggedin_bool), getString(R.string.loggedin_false));
                editor.apply();
                finish();
                gotoMainActivity();
            } else if (loginMethod.equals(getString(R.string.login_type_google))) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    editor.clear();
                                    editor.putString(getString(R.string.shared_prefs_key_loggedin_bool), getString(R.string.loggedin_false));
                                    editor.apply();
                                    finish();
                                    gotoMainActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            return true;
        });

        //Initialize Fragment  21MayAaron
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.fragment_container, new HomeFragment());
//        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (savedInstanceStatePresent) {
            return;
        }

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

    private void loadFragment(Integer itemId) {
        switch (itemId) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                editor.putInt(getString(R.string.landing_pointer), R.id.nav_home);
                break;
//            case R.id.nav_vid:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new VidFragment()).commit();
//                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                editor.putInt(getString(R.string.landing_pointer), R.id.nav_profile);
                break;
        }
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Integer itemId = menuItem.getItemId();
        loadFragment(itemId);
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
//        android.app.FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            fm.popBackStack();
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // saves user data to shared preferences + add to firebase
    private void googleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // add to shared preferences
            GoogleSignInAccount account = result.getSignInAccount();
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            userGoogleId = account.getId();
            userPhotoUrl = account.getPhotoUrl();

            editor.putString(getString(R.string.shared_prefs_key_username), userName);
            editor.putString(getString(R.string.shared_prefs_key_email), userEmail);
            editor.putString(getString(R.string.shared_prefs_key_google_id), userGoogleId);
            editor.putString(getString(R.string.shared_prefs_key_user_photo_url), userPhotoUrl.toString());

            editor.apply();

            // add to firebase users
            member = new Member();
            member.setUsername(userName);
            member.setEmail(userEmail);
            member.setId(userGoogleId);
            member.setPhotoUrl(userPhotoUrl.toString());
            member.setLoginType(getString(R.string.login_type_google));

            rref.orderByChild(getString(R.string.firebase_key_email)).equalTo(member.getEmail()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String key;
                            if (dataSnapshot.exists()) {
                                key = dataSnapshot.getChildren().iterator().next().getKey();
                                firebaseKey = key;
                                getUserDetailsFromFirebase();
                            } else {
                                // new user
                                key = rref.push().getKey();
                            }
                            editor.putString(getString(R.string.shared_prefs_key_firebasekey), key);
                            editor.apply();

                            Map<String, Object> userUpdates = new HashMap<>();

                            userUpdates.put(key + "/" + getString(R.string.firebase_key_username), userName);
                            userUpdates.put(key + "/" + getString(R.string.firebase_key_google_id), userGoogleId);
                            userUpdates.put(key + "/" + getString(R.string.firebase_key_photo_url), userPhotoUrl.toString());
                            userUpdates.put(key + "/" + getString(R.string.firebase_key_login_type), getString(R.string.login_type_google));
                            userUpdates.put(key + "/" + getString(R.string.firebase_key_email), userEmail);

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

    private void getUserDetailsFromFirebase() {
        Query q = rref.child(firebaseKey).child(getString(R.string.firebase_key_preferences));
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Preferences pref = dataSnapshot.getValue(Preferences.class);
                if (pref == null) {
                    editor.putInt(getString(R.string.shared_prefs_key_preferences_num_of_workouts), 0);
                    editor.putStringSet(getString(R.string.shared_prefs_key_preferences_goals), new HashSet<String>());
                    editor.apply();
                    return;
                }
                editor.putInt(getString(R.string.shared_prefs_key_preferences_num_of_workouts), pref.getNumOfWorkouts());
                Set<String> hSet = new HashSet<String>();
                hSet.addAll(pref.getGoals());
                editor.putStringSet(getString(R.string.shared_prefs_key_preferences_goals), hSet);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void authSignInResult() {

        // user is already added to db during sign up
        userEmail = sharedPreferences.getString(getString(R.string.shared_prefs_key_email), "");
        rref.orderByChild(getString(R.string.firebase_key_email)).equalTo(userEmail).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                            member = appleSnapshot.getValue(Member.class);
                            String loginType = member.getLoginType().trim();
                            if (loginType.equals(getString(R.string.login_type_auth))) {
                                String key = appleSnapshot.getKey();
                                editor.putString(getString(R.string.shared_prefs_key_firebasekey), key);
                                firebaseKey = key;
                                getUserDetailsFromFirebase();
                                userName = member.getUsername();
                                String userPhotoUrlString = member.getPhotoUrl();
                                editor.putString(getString(R.string.shared_prefs_key_user_photo_url), userPhotoUrlString);
                                editor.putString(getString(R.string.shared_prefs_key_username), userName);
                                editor.apply();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
        );
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //21May Aaron
    @Override
    public void onEditProfileBtnSelected() {
        Intent intent = new Intent(this, UpdateProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
