package com.example.womenperonalsecurity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class contact extends AppCompatActivity {

    private Button buttonPolice, buttonAmbulance, buttonChildCare, buttonWomenHelpLine;
    private Button buttonRailwayPolice, buttonAntiRagging, buttonrailwayprotection;
    private Button buttoncybercrime ,buttonRoadAccident, buttonMedical;
    private ImageView image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // Ensure this matches your layout file name
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contact.this, home.class);
                startActivity(intent);
            }
        });
        // Initialize buttons
        buttonPolice = findViewById(R.id.button);
        buttonAmbulance = findViewById(R.id.button2);
        buttonChildCare = findViewById(R.id.button3);
        buttonWomenHelpLine = findViewById(R.id.button4);
        buttonRailwayPolice = findViewById(R.id.button5);
        buttonAntiRagging = findViewById(R.id.button6);
        buttonrailwayprotection = findViewById(R.id.button7);
        buttoncybercrime = findViewById(R.id.button8);
        buttonRoadAccident = findViewById(R.id.button9);
        buttonMedical = findViewById(R.id.button10);

        // Set button click listeners
        buttonPolice.setOnClickListener(v -> makeCall("100"));
        buttonAmbulance.setOnClickListener(v -> makeCall("108"));
        buttonChildCare.setOnClickListener(v -> makeCall("1098"));
        buttonWomenHelpLine.setOnClickListener(v -> makeCall("1091"));
        buttonRailwayPolice.setOnClickListener(v -> makeCall("1512"));
        buttonAntiRagging.setOnClickListener(v -> makeCall("1800-180-5522"));
        buttonrailwayprotection.setOnClickListener(v -> makeCall("1322"));
        buttoncybercrime.setOnClickListener(v -> makeCall("1930"));
        buttonRoadAccident.setOnClickListener(v -> makeCall("1073"));
        buttonMedical.setOnClickListener(v -> makeCall("104"));
    }

    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }
}