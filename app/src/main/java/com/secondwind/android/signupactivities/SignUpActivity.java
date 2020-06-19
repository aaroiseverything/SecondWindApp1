package com.secondwind.android.signupactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.secondwind.android.R;
import com.secondwind.android.classes.Member;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    DatabaseReference rref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    private Button signUpButton;
    private String defaultProfilePic;
    private EditText usernameInput, emailInput, pwInput;
    private ArrayList<String> workoutFocus;
    private Member member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        rref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_db_members));
        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        usernameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        pwInput = findViewById(R.id.pwInput);

        signUpButton = findViewById(R.id.authSignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        defaultProfilePic = getString(R.string.default_profile_pic_url);
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pw = pwInput.getText().toString().trim();

        if (username.isEmpty()) {
            usernameInput.setError(getString(R.string.error_msg_username_required));
            usernameInput.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError(getString(R.string.error_msg_email_required));
            emailInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.error_msg_invalid_email));
            emailInput.requestFocus();
            return;
        }
        if (pw.isEmpty()) {
            pwInput.setError(getString(R.string.error_msg_pw_required));
            pwInput.requestFocus();
            return;
        }
        if (pw.length() < 6) {
            pwInput.setError(getString(R.string.error_msg_pw_length));
            pwInput.requestFocus();
            return;
        }

        // check if already have google account
        rref.orderByChild(getString(R.string.firebase_key_email)).equalTo(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // do not allow users to sign up using auth mth if they have already signed up using google account
                        if (dataSnapshot.exists()) {
                            String loginType = dataSnapshot.getChildren().iterator().next().getValue(Member.class).getLoginType();
                            if (loginType != getString(R.string.login_type_auth)) {
                                Toast.makeText(getApplicationContext(), R.string.error_msg_existing_google_account, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            createUser(username, email, pw);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
        );

    }

    private void createUser(String username, String email, String pw) {
        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateProfilePic(); // add to firebase auth
                            addUserToDb(username, email); // add to firebase db
                            editor.putString(getString(R.string.shared_prefs_key_user_photo_url), defaultProfilePic); // add to shared preferences
                            editor.apply();
                            Toast.makeText(getApplicationContext(), R.string.success_msg_registered, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, R.string.error_msg_registered,
                                        Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(SignUpActivity.this, R.string.error_msg_signup_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfilePic() {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(defaultProfilePic))
                .build();
        user.updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    private void addUserToDb(String username, String email) {
        member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setPhotoUrl(defaultProfilePic);
        member.setLoginType(getString(R.string.login_type_auth));
        member.setLastWorkout("0");
//        workoutFocus = new ArrayList<>();
//        workoutFocus.add("Core");
//        workoutFocus.add("Lower");
//        member.setWorkoutFocus(workoutFocus);

        rref.push().setValue(member);
    }

    public void signIn(View v) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
