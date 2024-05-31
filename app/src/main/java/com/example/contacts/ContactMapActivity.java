package com.example.contacts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    final int PERMISSION_REQUEST_LOCATION = 101;
    ImageButton listImageButton, mapImageButton, settingsImageButton;
    GoogleMap map;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    List<Contact> contactsList = new ArrayList<>();
    Contact contact;
    //EditText addressEditText, cityEditText, stateEditText, zipcodeEditText;
    //Button getCoordinatesButton;
    //TextView latitudeText, longitudeText, accuracyText;
    //LocationManager locationManager;
    //LocationListener gpsListener, networkListener;
    //Location currentBestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_map);
        initLayoutComponents();
        NavButtonsInitializer.initNavButtons(listImageButton, mapImageButton, settingsImageButton, this);
        //initGetCoordinates();
        initMap();
        initContacts();
    }

    private void initContacts() {
        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(this);
            ds.open();
            if (extras == null) {
                contactsList = ds.getAllContacts("contactname", "ASC");
            } else {
                contact = ds.getSpecificContact(extras.getInt("contactId"));
            }
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Contacts Cannot be retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMap() {
        fusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(this);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null)
                    return;
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                    Toast.makeText(
                            ContactMapActivity.this,
                            "Lat: " + location.getLatitude() +
                                    "Long: " + location.getLongitude() +
                                    "Acc: " + location.getAccuracy()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
    }

    /*private void initGetCoordinates() {
        getCoordinatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getCoordinatesFromName();
                getCoordinatesFromSensor();
            }
        });
    }*/

    private void getCoordinatesFromSensor() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(findViewById(R.id.activity_map),
                                "Contacts require this permission to locate your contacts",
                                Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(ContactMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(ContactMapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Toast.makeText(this,
                    "Contacts will not locate your contacts",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startLocationUpdates() {
        /*try {
            locationManager = (LocationManager) getBaseContext().
                    getSystemService(Context.LOCATION_SERVICE);
            gpsListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (isBetterLocation(location)) {
                        currentBestLocation = location;
                        latitudeText.setText(String.valueOf(location.getLatitude()));
                        longitudeText.setText(String.valueOf(location.getLongitude()));
                        accuracyText.setText(String.valueOf(location.getAccuracy()));
                    }
                }
            };
            networkListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (isBetterLocation(location)) {
                        currentBestLocation = location;
                        latitudeText.setText(String.valueOf(location.getLatitude()));
                        longitudeText.setText(String.valueOf(location.getLongitude()));
                        accuracyText.setText(String.valueOf(location.getAccuracy()));
                    }
                }
            };
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0, 0, networkListener);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0, 0, gpsListener);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error retrieving location",
                    Toast.LENGTH_SHORT).show();
        }*/
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, null);
            map.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /*private boolean isBetterLocation(Location location) {
        boolean isBetter = false;
        if (currentBestLocation == null) {
            isBetter = true;
        } else if (location.getAccuracy() <= currentBestLocation.getAccuracy())
            isBetter = true;
        else if (location.getTime() - currentBestLocation.getTime() > 5 * 60 * 1000)
            isBetter = true;
        return isBetter;
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //locationManager.removeUpdates(gpsListener);
            //locationManager.removeUpdates(networkListener);
            stopLocationUpdates();
        } catch (Exception ignored) {
        }
    }

    /*private void getCoordinatesFromName() {
        String address = addressEditText.getText().toString() + ", "
                + cityEditText.getText().toString() + ", "
                + stateEditText.getText().toString() + ", "
                + zipcodeEditText.getText().toString();
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(ContactMapActivity.this);
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && addressList.size() > 0) {
                latitudeText.setText(String.valueOf(addressList.get(0).getLatitude()));
                longitudeText.setText(String.valueOf(addressList.get(0).getLongitude()));
            } else {
                latitudeText.setText("None");
                longitudeText.setText("None");
            }
        } catch (IOException e) {
            Log.d("getCoordinatesFromName", e.getMessage());
            latitudeText.setText("Error");
            longitudeText.setText("Error");
        }
    }*/

    private void initLayoutComponents() {
        listImageButton = findViewById(R.id.imageButtonList);
        mapImageButton = findViewById(R.id.imageButtonMap);
        settingsImageButton = findViewById(R.id.imageButtonSettings);
        mapImageButton.setEnabled(false);
        //addressEditText = findViewById(R.id.editAddress);
        //cityEditText = findViewById(R.id.editCity);
        //stateEditText = findViewById(R.id.editState);
        //zipcodeEditText = findViewById(R.id.editZip);
        //getCoordinatesButton = findViewById(R.id.buttonGetLocation);
        //latitudeText = findViewById(R.id.textLatitude);
        //longitudeText = findViewById(R.id.textLongitude);
        //accuracyText = findViewById(R.id.textAccuracy);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getCoordinatesFromSensor();
        Point size = new Point();
        WindowManager windowManager = getWindowManager();
        windowManager.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;
        if (contactsList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Contact contact : contactsList) {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses;
                String address = contact.getStreetAddress() + ", "
                        + contact.getCity() + ", "
                        + contact.getState() + ", "
                        + contact.getZipcode();
                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                    if (addresses != null && addresses.size() > 0) {
                        LatLng point = new LatLng(
                                addresses.get(0).getLatitude(),
                                addresses.get(0).getLongitude());
                        builder.include(point);
                        googleMap.addMarker(new MarkerOptions().position(point)
                                .title(contact.getContactName()).snippet(address));
                    }
                } catch (Exception e) {
                }
            }
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                    builder.build(),
                    measuredWidth, measuredHeight, 350));
        } else if (contact != null) {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses;
            String address = contact.getStreetAddress() + ", "
                    + contact.getCity() + ", "
                    + contact.getState() + ", "
                    + contact.getZipcode();
            try {
                addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && addresses.size() > 0) {
                    LatLng point = new LatLng(
                            addresses.get(0).getLatitude(),
                            addresses.get(0).getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(point)
                            .title(contact.getContactName()).snippet(address));
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(point, 13));
                }
            } catch (Exception e) {
            }

        }
        else{
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle("No Data to Display");
            dialog.setMessage("No data is available for display on the map");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog.show();
        }
    }


}