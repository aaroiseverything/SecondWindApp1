package com.secondwind.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference rref;

    Button signUpButton;
    EditText usernameInput, emailInput, pwInput;
    Member member;

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

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToDb(username, email);

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

    private void addUserToDb(String username, String email) {
        member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setLoginType("auth");

        rref.push().setValue(member);
    }

    public void signIn(View v) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
