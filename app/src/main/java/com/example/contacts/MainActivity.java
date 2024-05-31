package com.example.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;

    Contact currentContact;
    ImageButton listImageButton, mapImageButton, settingsImageButton, saveButton, cameraImageButton;
    Button changeBirthdayButton;
    ToggleButton editToggle;
    EditText nameEditText, addressEditText, cityEditText,
            stateEditText, zipEditText, homeEditText,
            cellEditText, emaiEditText;
    TextView birthdayText;
    ImageView contactImage;
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Intent data = result.getData();
                                Bitmap photo = (Bitmap) data.getExtras().get("data");
                                float density = MainActivity.this.getResources().getDisplayMetrics().density;
                                int dp = 140;
                                int pixels = (int) ((dp * density) + 0.5);
                                Bitmap scaledPhoto = Bitmap.createScaledBitmap(
                                        photo, pixels, pixels, true);
                                contactImage.setImageBitmap(scaledPhoto);
                                currentContact.setContactPhoto(scaledPhoto);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayoutComponents();
        NavButtonsInitializer.initNavButtons(listImageButton, mapImageButton, settingsImageButton, this);
        initToggleButton();
        setForEditing(false);
        initChangeBirthdayButton();
        initContact();
        initTextChangedEvents();
        initSaveButton();
        initCallFunction();
        initCameraButton();
    }

    private void initCameraButton() {
        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            MainActivity.this, Manifest.permission.CAMERA)) {
                        Snackbar.make(findViewById(R.id.activity_main),
                                        "The app needs permission to take photo",
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("MainActivity Camera permission", "");
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                                    }
                                }).show();
                    } else {
                        Log.d("MainActivity Camera permission", "");
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    }
                }
            }
        });
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    private void initCallFunction() {
        homeEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });
        cellEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getCellNumber());
                return false;
            }
        });
    }

    private void checkPhonePermission(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CALL_PHONE)) {
                Snackbar.make(findViewById(R.id.activity_main),
                                "Contacts App requires this permission to place a call from the app.",
                                Snackbar.LENGTH_INDEFINITE).
                        setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        PERMISSION_REQUEST_PHONE);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        PERMISSION_REQUEST_PHONE);
            }
        } else {
            callContact(phoneNumber);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You may now make phone calls from this app",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "You will not be able to make phone calls from this app",
                            Toast.LENGTH_LONG).show();
                }
                break;
            }
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this,
                            "You will not be able to save contact photo from this app",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    private void initContact() {
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            currentContact = new Contact();
        } else {
            int currentId = extra.getInt("contactId");
            ContactDataSource ds = new ContactDataSource(this);
            try {
                ds.open();
                currentContact = ds.getSpecificContact(currentId);
                ds.close();
                nameEditText.setText(currentContact.getContactName());
                stateEditText.setText(currentContact.getState());
                addressEditText.setText(currentContact.getStreetAddress());
                zipEditText.setText(currentContact.getZipcode());
                cityEditText.setText(currentContact.getCity());
                emaiEditText.setText(currentContact.getEMail());
                homeEditText.setText(currentContact.getPhoneNumber());
                cellEditText.setText(currentContact.getCellNumber());
                birthdayText.setText(DateFormat.format("dd/MM/yyyy",
                        currentContact.getBirthday().getTimeInMillis()).toString());
                if (currentContact.getContactPhoto() != null) {
                    contactImage.setImageBitmap(currentContact.getContactPhoto());
                } else {
                    contactImage.setImageResource(R.drawable.person);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error Loading Data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wasSuccessful = false;
                ContactDataSource dataSource = new ContactDataSource(MainActivity.this);
                try {
                    dataSource.open();
                    if (currentContact.getContactID() == -1) {
                        wasSuccessful = dataSource.insertContact(currentContact);
                        if (wasSuccessful) {
                            int newId = dataSource.getLastContact();
                            currentContact.setContactID(newId);
                        }
                    } else {
                        wasSuccessful = dataSource.updateContact(currentContact);
                    }
                } catch (Exception ignored) {
                }
                if (wasSuccessful) {
                    editToggle.toggle();
                    setForEditing(false);
                }
            }
        });
    }

    private void initTextChangedEvents() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", nameEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("on", nameEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", nameEditText.getText().toString());
                currentContact.setContactName(nameEditText.getText().toString());
            }
        });
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(addressEditText.getText().toString());
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(cityEditText.getText().toString());
            }
        });
        stateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(stateEditText.getText().toString());
            }
        });
        zipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipcode(zipEditText.getText().toString());
            }
        });
        homeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(homeEditText.getText().toString());
            }
        });
        cellEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(cellEditText.getText().toString());
            }
        });
        emaiEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setEMail(emaiEditText.getText().toString());
            }
        });
        homeEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        cellEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void initToggleButton() {
        editToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setForEditing(editToggle.isChecked());
            }
        });
    }

    private void setForEditing(boolean checked) {
        nameEditText.setEnabled(checked);
        addressEditText.setEnabled(checked);
        cityEditText.setEnabled(checked);
        stateEditText.setEnabled(checked);
        zipEditText.setEnabled(checked);
        //homeEditText.setEnabled(checked);
        //cellEditText.setEnabled(checked);
        emaiEditText.setEnabled(checked);
        changeBirthdayButton.setEnabled(checked);
        saveButton.setEnabled(checked);
        cameraImageButton.setEnabled(checked);
        if (checked) {
            nameEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(nameEditText, 0);
            cellEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            homeEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            cellEditText.setInputType(InputType.TYPE_NULL);
            homeEditText.setInputType(InputType.TYPE_NULL);
        }
    }

    private void initChangeBirthdayButton() {
        changeBirthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog dialog = new DatePickerDialog();
                dialog.show(fm, "DatePicker");
            }
        });
    }

    private void initLayoutComponents() {
        listImageButton = findViewById(R.id.imageButtonList);
        mapImageButton = findViewById(R.id.imageButtonMap);
        settingsImageButton = findViewById(R.id.imageButtonSettings);
        editToggle = findViewById(R.id.toggleButtonEdit);
        nameEditText = findViewById(R.id.editName);
        addressEditText = findViewById(R.id.editAddress);
        cityEditText = findViewById(R.id.editCity);
        stateEditText = findViewById(R.id.editState);
        zipEditText = findViewById(R.id.editZip);
        homeEditText = findViewById(R.id.editHome);
        cellEditText = findViewById(R.id.editCell);
        emaiEditText = findViewById(R.id.editEmail);
        changeBirthdayButton = findViewById(R.id.btnBirthday);
        saveButton = findViewById(R.id.imageButtonSave);
        birthdayText = findViewById(R.id.textViewBirthday);
        cameraImageButton = findViewById(R.id.imageButtonCamera);
        contactImage = findViewById(R.id.imageViewContact);
    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedDate) {
        birthdayText.setText(DateFormat.format("dd/MM/yyyy", selectedDate));
        currentContact.setBirthday(selectedDate);
    }
}