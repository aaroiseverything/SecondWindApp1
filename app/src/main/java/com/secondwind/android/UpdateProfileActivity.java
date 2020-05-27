package com.secondwind.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UpdateProfileActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private CheckBox radioButton1, radioButton2, radioButton3, radioButton4, radioButton5;
    private Button saveButton, cancelButton;
    private ArrayList<String> goals;
    private String firebaseKey;
    private DatabaseReference rref;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Integer numOfWorkouts;
    private TextView seekBarText;
    private View view;
    private NavController navController;
    private ImageView back;

    private void endActivity() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            endActivity();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_db_members));
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");

        mAuth = FirebaseAuth.getInstance();

        // Initalise list of goals
        Set<String> goalsSet = sharedPreferences.getStringSet(getString(R.string.shared_prefs_key_preferences_goals), new HashSet<>());
        goals = new ArrayList<>(goalsSet);

        // Initialise numOfWorkouts
        numOfWorkouts = sharedPreferences.getInt(getString(R.string.shared_prefs_key_preferences_num_of_workouts), 0);

        // Checkboxes
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        radioButton5 = findViewById(R.id.radioButton5);
        CheckBox ar[] = {radioButton1, radioButton2, radioButton3, radioButton4, radioButton5};

        for (CheckBox i : ar) {
            // accessing each element of array
            String x = i.getText().toString();
            if (goals.contains(x)) {
                i.setChecked(true);
            }
        }

        //seekBar
        seekBarText = findViewById(R.id.seekBarText);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(numOfWorkouts);
        seekBarText.setText(String.valueOf(numOfWorkouts));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numOfWorkouts = progress;
                seekBarText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //saveButton
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer count = 0;
                goals.clear();
                for (CheckBox i : ar) {
                    // accessing each element of array
                    String x = i.getText().toString();
                    if (i.isChecked()) {
                        goals.add(x);
                        count += 1;
                    }
                }
                if (count > 3) {
                    Toast.makeText(getApplicationContext(), "Please choose only three goals", Toast.LENGTH_LONG).show();
                    return;
                }

                // shared prefs
                Set<String> hSet = new HashSet<String>();
                hSet.addAll(goals);
                editor.putStringSet(getString(R.string.shared_prefs_key_preferences_goals), hSet);
                editor.putInt(getString(R.string.shared_prefs_key_preferences_num_of_workouts), numOfWorkouts);
                editor.apply();

                // firebase
                Map<String, Object> goalsMap = new HashMap<>();
                goalsMap.put(firebaseKey + getString(R.string.firebase_key_preferences) + getString(R.string.firebase_key_preferences_num_of_workouts), numOfWorkouts);
                goalsMap.put(firebaseKey + getString(R.string.firebase_key_preferences) + getString(R.string.firebase_key_preferences_goals), goals);
                rref.updateChildren(goalsMap);
                goals.clear();

                editor.putInt(getString(R.string.landing_pointer), R.id.nav_profile);
                editor.apply();
                endActivity();
//                navController.navigate(R.id.action_updateProfileFragment_to_profileInnerFragment);
            }
        });
    }
}
