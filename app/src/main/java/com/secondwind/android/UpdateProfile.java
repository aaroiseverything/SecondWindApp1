package com.secondwind.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import androidx.fragment.app.Fragment;

public class UpdateProfile extends AppCompatActivity {
    SeekBar seekBar;
    RadioButton radioButton, radioButton2,radioButton3,radioButton4;
    Button saveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
    }
}
