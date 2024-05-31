package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    ImageButton listImageButton, mapImageButton, settingsImageButton, newContactImageButton;
    RecyclerView conatctsRecyclerView;
    SwitchMaterial deleteSwitch;
    ContactAdapter adapter;
    ArrayList<Contact> contactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initLayoutComponents();
        NavButtonsInitializer.initNavButtons(listImageButton, mapImageButton, settingsImageButton, this);
        initRecyclerView();
        initDeleteSwitch();
        initNewContactImageButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initNewContactImageButton() {
        newContactImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDeleteSwitch() {
        deleteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setDeleting(isChecked);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        ContactDataSource dataSource = new ContactDataSource(this);
        try {
            String sortField = getSharedPreferences(
                    "ContactListPreferences", Context.MODE_PRIVATE)
                    .getString("sortField", "contactname");
            String sortOrder = getSharedPreferences(
                    "ContactListPreferences", Context.MODE_PRIVATE)
                    .getString("sortOrder", "ASC");
            dataSource.open();
            contactData = dataSource.getAllContacts(sortField, sortOrder);
            dataSource.close();
            if(contactData.size() > 0) {
                conatctsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ContactAdapter(contactData, this);
                conatctsRecyclerView.setAdapter(adapter);
            }
            else{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error Retrieving Data"
                    , Toast.LENGTH_LONG).show();
        }
    }

    private void initLayoutComponents() {
        listImageButton = findViewById(R.id.imageButtonList);
        mapImageButton = findViewById(R.id.imageButtonMap);
        settingsImageButton = findViewById(R.id.imageButtonSettings);
        listImageButton.setEnabled(false);
        conatctsRecyclerView = findViewById(R.id.rvContact);
        deleteSwitch = findViewById(R.id.switchDelete);
        newContactImageButton = findViewById(R.id.imageButtonNewContact);
    }
}