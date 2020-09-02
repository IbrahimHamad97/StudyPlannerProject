package com.cmps312.studyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OptionsActivity extends AppCompatActivity {
    RadioGroup languageRG;
    ToggleButton darkThemeBtn;
    Switch notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        languageRG = findViewById(R.id.languageRG);
        darkThemeBtn = findViewById(R.id.darkThemeToggleButton);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);

        languageRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        darkThemeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(OptionsActivity.this, "Dark theme is on",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(OptionsActivity.this, "Dark theme is off",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(OptionsActivity.this, "on",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(OptionsActivity.this, "off",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
