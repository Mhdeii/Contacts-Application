package com.example.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactDataSource {
    SQLiteDatabase database;
    ContactDBHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("contactname", c.getContactName());
            initialValues.put("streetaddress", c.getStreetAddress());
            initialValues.put("city", c.getCity());
            initialValues.put("state", c.getState());
            initialValues.put("zipcode", c.getZipcode());
            initialValues.put("phonenumber", c.getPhoneNumber());
            initialValues.put("cellnumber", c.getCellNumber());
            initialValues.put("email", c.getEMail());
            initialValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));
            if (c.getContactPhoto() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getContactPhoto().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                initialValues.put("contactphoto", photo);
            }
            didSucceed = database.insert("contact", null, initialValues) > 0;
        } catch (Exception e) {
            Log.d("My Database", "Something went wrong!");
        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            long rowID = c.getContactID();
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("contactname", c.getContactName());
            updatedValues.put("streetaddress", c.getStreetAddress());
            updatedValues.put("city", c.getCity());
            updatedValues.put("state", c.getState());
            updatedValues.put("zipcode", c.getZipcode());
            updatedValues.put("phonenumber", c.getPhoneNumber());
            updatedValues.put("cellnumber", c.getCellNumber());
            updatedValues.put("email", c.getEMail());
            updatedValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));
            if (c.getContactPhoto() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getContactPhoto().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                updatedValues.put("contactphoto", photo);
            }
            didSucceed = database.update("contact", updatedValues, "_id = " + rowID, null) > 0;
        } catch (Exception ignored) {
        }
        return didSucceed;
    }

    public int getLastContact() {
        int lastId;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    public ArrayList<String> getContactNames() {
        ArrayList<String> names = new ArrayList<>();
        try {
            String query = "select contactname from contact";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                names.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            names = new ArrayList<>();
        }
        return names;
    }

    public ArrayList<Contact> getAllContacts(String sortField, String sortOrder) {
        String query = "Select * from contact ORDER BY " + sortField + " " + sortOrder;
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Contact c = new Contact();
                c.setContactID(cursor.getInt(0));
                c.setContactName(cursor.getString(1));
                c.setStreetAddress(cursor.getString(2));
                c.setCity(cursor.getString(3));
                c.setState(cursor.getString(4));
                c.setZipcode(cursor.getString(5));
                c.setPhoneNumber(cursor.getString(6));
                cursor.moveToNext();
                contacts.add(c);
            }
            cursor.close();
        } catch (Exception e) {
            contacts = new ArrayList<>();
        }
        return contacts;
    }

    public Contact getSpecificContact(int id) {
        Contact c = new Contact();
        String query = "SELECT * FROM contact WHERE _id = " + id;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            c.setContactID(cursor.getInt(0));
            c.setContactName(cursor.getString(1));
            c.setStreetAddress(cursor.getString(2));
            c.setCity(cursor.getString(3));
            c.setState(cursor.getString(4));
            c.setZipcode(cursor.getString(5));
            c.setPhoneNumber(cursor.getString(6));
            c.setCellNumber(cursor.getString(7));
            c.setEMail(cursor.getString(8));
            Calendar birthday = Calendar.getInstance();
            birthday.setTimeInMillis(Long.parseLong(cursor.getString(9)));
            c.setBirthday(birthday);
            byte[] photo = cursor.getBlob(10);
            if (photo != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(photo);
                Bitmap contactPhoto = BitmapFactory.decodeStream(bais);
                c.setContactPhoto(contactPhoto);
            }
        }
        cursor.close();
        return c;
    }

    public boolean deleteContact(int id) {
        boolean deleted;
        try {
            deleted = database.delete("contact", "_id=" + id, null) > 0;
        } catch (Exception e) {
            deleted = false;
        }
        return deleted;
    }

}
