package com.example.womenperonalsecurity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class livelocation extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView textViewLatitude, textViewLongitude, textViewCountry, textViewLocality, textViewAddress;
    private Button buttonGetLocation, buttonShareLocation;
    private ImageView image_back;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private ArrayList<String> emergencyContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livelocation);

        image_back = findViewById(R.id.back);
        image_back.setOnClickListener(v -> {
            Intent intent = new Intent(livelocation.this, home.class);
            startActivity(intent);
        });

        textViewLatitude = findViewById(R.id.textView7);
        textViewLongitude = findViewById(R.id.textView8);
        textViewCountry = findViewById(R.id.textView9);
        textViewLocality = findViewById(R.id.textView10);
        textViewAddress = findViewById(R.id.textView11);
        buttonGetLocation = findViewById(R.id.button);
        buttonShareLocation = findViewById(R.id.buttonShare);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        loadEmergencyContacts();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    textViewLatitude.setText(String.valueOf(latitude));
                    textViewLongitude.setText(String.valueOf(longitude));

                    Geocoder geocoder = new Geocoder(livelocation.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            textViewCountry.setText(address.getCountryName());
                            textViewLocality.setText(address.getLocality());
                            textViewAddress.setText(address.getAddressLine(0));
                            saveLocationToSharedPreferences(latitude, longitude, address.getAddressLine(0));
                        } else {
                            Toast.makeText(livelocation.this, "No address found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(livelocation.this, "Unable to get address", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        buttonGetLocation.setOnClickListener(v -> getLocation());
        buttonShareLocation.setOnClickListener(v -> shareLocation());
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    private void shareLocation() {
        loadEmergencyContacts(); // Reload emergency contacts from SharedPreferences

        if (emergencyContacts == null || emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("LiveLocation", MODE_PRIVATE);
        String latitude = prefs.getString("latitude", "0.0");
        String longitude = prefs.getString("longitude", "0.0");
        String fullAddress = prefs.getString("address", "Unknown");

        String locationLink = "https://maps.google.com/?q=" + latitude + "," + longitude;
        String message = "🚨 Emergency! I'm at:\n" + fullAddress + "\n📍 " + locationLink;

        // Validate and format emergency contact numbers
        StringBuilder recipientNumbers = new StringBuilder();
        for (String contact : emergencyContacts) {
            contact = contact.replaceAll("[^0-9+]", ""); // Remove invalid characters
            if (!contact.isEmpty()) {
                if (recipientNumbers.length() > 0) {
                    recipientNumbers.append(";"); // Separate multiple numbers
                }
                recipientNumbers.append(contact);
            }
        }

        if (recipientNumbers.length() == 0) {
            Toast.makeText(this, "No valid emergency contacts!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send SMS to all emergency contacts at once
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + recipientNumbers.toString())); // Multiple recipients
        smsIntent.putExtra("sms_body", message);

        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send emergency SMS", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveLocationToSharedPreferences(double latitude, double longitude, String address) {
        SharedPreferences prefs = getSharedPreferences("LiveLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.putString("address", address);
        editor.apply();
    }

    private void showNoContactsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Emergency Contacts");
        builder.setMessage("You haven't added any emergency contacts. Please add them in settings.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadEmergencyContacts() {
        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);
        String loggedEmail = userPrefs.getString("loggedemail", "default_user");

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        // Use getStringSet() instead of getString()
        Set<String> contactsSet = sharedPreferences.getStringSet("contacts_" + loggedEmail, new HashSet<>());

        emergencyContacts = new ArrayList<>(contactsSet);

        Log.d("ContactsDebug", "Retrieved contacts: " + emergencyContacts.toString());

        if (emergencyContacts.isEmpty()) {
            showNoContactsDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}