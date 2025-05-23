package com.example.womenperonalsecurity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class aboutus extends AppCompatActivity {
private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus); // Make sure this matches your XML file name
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aboutus.this, home.class);
                startActivity(intent);
            }
        });
        TextView titleText = findViewById(R.id.titleText);
        TextView descriptionText = findViewById(R.id.descriptionText);
        TextView missionText = findViewById(R.id.missionText);
        TextView featuresText = findViewById(R.id.featuresText);

        // Set up the Contact Us button
        Button contactButton = findViewById(R.id.contactButton);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs();
            }
        });
    }

    private void contactUs() {
        // Create an intent to send an email or open a contact form
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"seqher.sr@gmail.com"}); // Replace with your support email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about [Feel Safe App]"); // Replace with your app name
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I would like to know more about..."); // Optional message

        // Verify that there's an app to handle the intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
}