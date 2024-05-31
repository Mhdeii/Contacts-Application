package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class NavButtonsInitializer {

    public static void initNavButtons(ImageButton list, ImageButton map, ImageButton settings, Context context) {
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        context, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        context, ContactMapActivity.class);
                if(context instanceof MainActivity){
                    MainActivity activity = (MainActivity) context;
                    if(activity.currentContact.getContactID() == -1){
                        Toast.makeText(context,
                                "The contact must be saved before viewing the map",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        intent.putExtra("contactId",
                                activity.currentContact.getContactID());
                    }
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        context, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }
}
