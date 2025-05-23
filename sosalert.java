package com.example.womenperonalsecurity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class sosalert extends AppCompatActivity {

    private TextView tvCountdown;
    private Button btnCancel;
    private CountDownTimer countDownTimer;
    private boolean isCancelled = false;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<String> emergencyContacts;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosalert);

        tvCountdown = findViewById(R.id.tvCountdown);
        btnCancel = findViewById(R.id.btnCancel);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        emergencyContacts = new ArrayList<>();

        checkPermissions();
        loadEmergencyContacts();



        if (emergencyContacts == null ||emergencyContacts.isEmpty()) {
            showNoContactsDialog();
        } else {
            startCountdown();
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancelled = true;
                countDownTimer.cancel();
                Toast.makeText(sosalert.this, "SOS Alert Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        startCountdown();

    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), 1);
        }
    }
    private void startCountdown() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) millisUntilFinished / 1000;
                tvCountdown.setText("Sending SOS Alert in " + secondsRemaining);
            }

            @Override
            public void onFinish() {
                if (!isCancelled) {
                    sendSOSMessage();
                }
            }
        }.start();
    }
    private void showNoContactsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Emergency Contacts")
                .setMessage("You have not added any emergency contacts. Please add at least one contact before using SOS Alert.")
                .setPositiveButton("Add", (dialog, which) -> {
                    Intent intent = new Intent(sosalert.this, emergencycall.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(sosalert.this, "SOS Alert Canceled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setCancelable(false)
                .show();
    }
    private void sendSOSMessage() {
        loadEmergencyContacts();
        if (emergencyContacts.isEmpty()) {
            showNoContactsDialog(); // Show dialog only if no contacts exist
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location-> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String locationUrl = "https://maps.google.com/?q=" + latitude + "," + longitude;
                String message = "HELP ME!! IT'S AN EMERGENCY!! Please reach ASAP to the location below:\n" + locationUrl;

                List<String> sentContacts = new ArrayList<>();
                for (String contact : emergencyContacts) {
                    sentContacts.add(contact);
                }
                sendSMS(emergencyContacts, message);

// Now make the emergency call
                makeEmergencyCall();

                Toast.makeText(sosalert.this, "SOS Message Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(sosalert.this, "Unable to get location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeEmergencyCall() {
        loadEmergencyContacts();
        if (emergencyContacts == null ||emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts available", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = emergencyContacts.get(0); // Get the first emergency contact
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Emergency contact number is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
            return;
        }
        if (!phoneNumber.matches("\\d{10,}")) { // Validate phone number format
            Toast.makeText(this, "Invalid emergency contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
        Toast.makeText(this, "Calling " + phoneNumber, Toast.LENGTH_SHORT).show();
    }
    private void sendSMS(List<String> phoneNumbers, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        for (String phoneNumber : phoneNumbers) {
            if (!phoneNumber.matches("\\d{10,}")) {  // Validate number format
                Log.e("SMS", "Invalid phone number: " + phoneNumber);
                continue;
            }
            try {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Log.d("SMS", "✅ SMS Sent to: " + phoneNumber);
                Toast.makeText(this, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("SMS", "❌ Failed to send SMS to: " + phoneNumber, e);
                Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadEmergencyContacts() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String loggedEmail = sharedPreferences.getString("loggedemail", "default_user");

        // Retrieve contacts stored as a Set<String>
        Set<String> contactsSet = sharedPreferences.getStringSet("contacts_" + loggedEmail, new HashSet<>());

        emergencyContacts.clear();  // Clear the previous list to avoid duplicates

        if (contactsSet != null && !contactsSet.isEmpty()) {
            for (String contact : contactsSet) {
                String[] contactDetails = contact.split(" - ");
                if (contactDetails.length > 1) {
                    emergencyContacts.add(contactDetails[1]); // Store only the phone number
                }
            }
        }

        // ✅ If no contacts exist, reset the list and show the dialog
        if (emergencyContacts.isEmpty()) {
            showNoContactsDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSOSMessage();
            } else {
                Toast.makeText(this, "Location permission is required to send SOS", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 3) { // CALL_PHONE permission request
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeEmergencyCall();
            } else {
                Toast.makeText(this, "Call permission is required to make emergency calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

}