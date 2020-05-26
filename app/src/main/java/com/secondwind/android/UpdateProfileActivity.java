package com.secondwind.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private RadioButton radioButton1, radioButton2,radioButton3,radioButton4, radioButton5;
    private Button saveButton;
    //Update profile variables
    private List<String> goals;
    private String firebaseKey;
    private DatabaseReference rref;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        rref = FirebaseDatabase.getInstance().getReference().child("Members");
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");

        mAuth = FirebaseAuth.getInstance();

        //Initalise List of Goals
        goals = new ArrayList<>();
        //Radio Buttons
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        radioButton5 = (RadioButton) findViewById(R.id.radioButton5);
        //seekBar
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        final Integer[] workouts = new Integer[1];
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                workouts[0] = progressChangedValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //saveButton
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String r1 = radioButton1.getText().toString();
                String r2 = radioButton2.getText().toString();
                String r3 = radioButton3.getText().toString();
                String r4 = radioButton4.getText().toString();
                String r5 = radioButton5.getText().toString();
                if (radioButton1.isChecked()) {
                    goals.add(r1);
                }
                if (radioButton2.isChecked()) {
                    goals.add(r2);
                }
                if (radioButton3.isChecked()) {
                    goals.add(r3);
                }
                if (radioButton4.isChecked()) {
                    goals.add(r4);
                }
                if (radioButton5.isChecked()) {
                    goals.add(r5);
                }
                updateFirebaseProfiling("Goals", goals);
                goals.clear();
                rref.child(firebaseKey).child("numWorkouts").setValue(workouts[0]);

            }
        });
    }


    public void updateFirebaseProfiling(String field, List<String> input) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(firebaseKey + "/" + field, input);
        rref.updateChildren(userUpdates);
    }


}
