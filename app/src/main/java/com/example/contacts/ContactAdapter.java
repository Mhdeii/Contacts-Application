package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter {

    boolean isDeleting;
    Context context;
    private ArrayList<Contact> contactData;
    private View.OnClickListener onItemClickListener;

    public ContactAdapter(ArrayList <Contact> contacts, Context context) {
        this.context = context;
        contactData = contacts;
        onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder)
                        v.getTag();
                int position = holder.getAdapterPosition();
                int contactId = contactData.get(position).getContactID();
                Intent intent = new Intent(context,
                        MainActivity.class);
                intent.putExtra("contactId", contactId);
                context.startActivity(intent);
            }
        };
    }

    public void setDeleting(boolean deleting) {
        isDeleting = deleting;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.complex_list_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContactViewHolder contactVH = (ContactViewHolder) holder;
        contactVH.getContactTextView().
                setText(contactData.get(position).getContactName());
        contactVH.getPhoneTextView().
                setText(contactData.get(position).getPhoneNumber());
        if (isDeleting) {
            contactVH.getDeleteImageButton().setVisibility(View.VISIBLE);
            contactVH.getDeleteImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(holder.getAdapterPosition());
                }
            });
        } else {
            contactVH.getDeleteImageButton().setVisibility(View.INVISIBLE);
        }
    }

    private void deleteItem(int position) {
        ContactDataSource ds = new ContactDataSource(context);
        try {
            ds.open();
            int contactId = contactData.get(position).getContactID();
            boolean didDelete = ds.deleteContact(contactId);
            ds.close();
            if (didDelete) {
                contactData.remove(position);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Delete Failed", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Delete Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return contactData.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, phoneTextView;
        ImageButton deleteImageButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewContactName);
            phoneTextView = itemView.findViewById(R.id.textViewPhoneNumber);
            deleteImageButton = itemView.findViewById(R.id.imageButtonDelete);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }

        public TextView getContactTextView() {
            return nameTextView;
        }

        public TextView getPhoneTextView() {
            return phoneTextView;
        }

        public ImageButton getDeleteImageButton() {
            return deleteImageButton;
        }
    }
}
