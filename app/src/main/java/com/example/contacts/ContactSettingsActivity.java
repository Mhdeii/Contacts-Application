package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ContactSettingsActivity extends AppCompatActivity {

    ImageButton listImageButton, mapImageButton, settingsImageButton;
    RadioGroup sortByRadioGroup, sortOrderRadioGroup;
    RadioButton nameRadioButton, cityRadioButton, birthdayRadioButton,
            ascendingRadioButton, descendingRadioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_settings);
        initLayoutComponents();
        NavButtonsInitializer.initNavButtons(listImageButton, mapImageButton, settingsImageButton, this);
        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("ContactListPreferences",
                MODE_PRIVATE).getString("sortField", "contactname");
        String sortOrder = getSharedPreferences("ContactListPreferences",
                MODE_PRIVATE).getString("sortOrder", "ASC");
        if(sortBy.equals("contactname"))
           nameRadioButton.setChecked(true);
       else if (sortBy.equals("city")) {
           cityRadioButton.setChecked(true);
       }
       else
           birthdayRadioButton.setChecked(true);

       if(sortOrder.equals("ASC")){
           ascendingRadioButton.setChecked(true);
       }
       else
           descendingRadioButton.setChecked(true);

    }

    private void initSortOrderClick() {
        sortOrderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(ascendingRadioButton.isChecked()){
                    getSharedPreferences("ContactListPreferences",
                            MODE_PRIVATE).edit().
                            putString("sortOrder", "ASC").apply();
                }
                else{
                    getSharedPreferences("ContactListPreferences",
                            MODE_PRIVATE).edit().
                            putString("sortOrder", "DESC").apply();
                }
            }
        });
    }

    private void initSortByClick() {
        sortByRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(nameRadioButton.isChecked()){
                    getSharedPreferences("ContactListPreferences",
                            MODE_PRIVATE).edit().
                            putString("sortField", "contactname").apply();
                } else if (cityRadioButton.isChecked()) {
                    getSharedPreferences("ContactListPreferences",
                            MODE_PRIVATE).edit().
                            putString("sortField", "city").apply();
                }
                else{
                    getSharedPreferences("ContactListPreferences",
                            MODE_PRIVATE).edit().
                            putString("sortField", "birthday").apply();
                }
            }
        });
    }

    private void initLayoutComponents(){
        listImageButton = findViewById(R.id.imageButtonList);
        mapImageButton = findViewById(R.id.imageButtonMap);
        settingsImageButton = findViewById(R.id.imageButtonSettings);
        settingsImageButton.setEnabled(false);
        sortOrderRadioGroup = findViewById(R.id.radioGroupSortOrder);
        sortByRadioGroup = findViewById(R.id.radioGroupSortBy);
        nameRadioButton = findViewById(R.id.radioName);
        cityRadioButton = findViewById(R.id.radioCity);
        birthdayRadioButton = findViewById(R.id.radioBirthday);
        ascendingRadioButton = findViewById(R.id.radioAscending);
        descendingRadioButton = findViewById(R.id.radioDescending);
    }
}