package com.secondwind.android.signupactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.secondwind.android.HomeActivity;
import com.secondwind.android.R;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SignInButton googleSignInButton;
    Button authSignInButton;
    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    EditText emailInput, pwInput;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        emailInput = findViewById(R.id.emailInput);
        pwInput = findViewById(R.id.pwInput);

        authSignInButton = findViewById(R.id.authSignInButton);
        authSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void userLogin() {
        String email = emailInput.getText().toString().trim();
        String pw = pwInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError(getString(R.string.error_msg_email_required));
            emailInput.requestFocus();
            return;
        }
        if (pw.isEmpty()) {
            pwInput.setError(getString(R.string.error_msg_pw_required));
            pwInput.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    editor.putString(getString(R.string.shared_prefs_key_login_method), getString(R.string.login_type_auth));
                    editor.putString(getString(R.string.shared_prefs_key_email), email);
                    editor.apply();
                    gotoHome();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleSignInResult(result);
        }
    }

    private void googleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            editor.putString(getString(R.string.shared_prefs_key_login_method), getString(R.string.login_type_google));
            editor.apply();
            gotoHome();
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_msg_google_signin_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedIn = sharedPreferences.getString(getString(R.string.shared_prefs_key_loggedin_bool), "");
        if (loggedIn.equals("true")) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private void gotoHome() {
        editor.putString(getString(R.string.shared_prefs_key_loggedin_bool), getString(R.string.loggedin_true));
        editor.apply();
        finish();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void goToSignUp(View v) {
        finish();
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
