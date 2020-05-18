package com.secondwind.android;

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

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference rref;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button signUpButton;
    EditText usernameInput, emailInput, pwInput;
    Member member;

    String defaultProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        rref = FirebaseDatabase.getInstance().getReference().child("Members");

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

        defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/secondwind-android-7708e.appspot.com/o/profilepics%2Fdefault.png?alt=media&token=cb294446-ff32-4c83-9adf-e044f7663443";
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pw = pwInput.getText().toString().trim();

        if (username.isEmpty()) {
            usernameInput.setError("Username is required.");
            usernameInput.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required.");
            emailInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email.");
            emailInput.requestFocus();
            return;
        }
        if (pw.isEmpty()) {
            pwInput.setError("Password is required.");
            pwInput.requestFocus();
            return;
        }
        if (pw.length() < 6) {
            pwInput.setError("Password should be more than 6 characters.");
            pwInput.requestFocus();
            return;
        }

        // check if already have google account
        rref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // do not allow users to sign up using auth mth if they have already signed up using google account
                        if (dataSnapshot.exists()) {
                            String loginType = dataSnapshot.getChildren().iterator().next().getValue(Member.class).getLoginType();
                            if (loginType != "auth") {
                                Toast.makeText(getApplicationContext(), "You have already signed up using your Google Account.", Toast.LENGTH_LONG).show();
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
                            editor.putString("userPhotoUrl", defaultProfilePic); // add to shared preferences
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "Registered successfully.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "You are already registered.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(SignUpActivity.this, "Sign up failed.",
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
        member.setLoginType("auth");

        rref.push().setValue(member);
    }

    public void signIn(View v) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
