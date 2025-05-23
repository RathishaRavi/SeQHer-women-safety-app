package com.example.womenperonalsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class set_pin extends AppCompatActivity {

    private EditText setPinEditText;
    private Button setButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin); // Ensure this matches your layout file name

        // Initialize views
        setPinEditText = findViewById(R.id.set_pin);
        setButton = findViewById(R.id.set_btn);

        // Set up the SET button click listener
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetButtonClick();
            }
        });
    }

    private void handleSetButtonClick() {
        String pin = setPinEditText.getText().toString().trim();

        if (pin.isEmpty()) {
            Toast.makeText(this, "Please enter a valid PIN", Toast.LENGTH_SHORT).show();
        } else if (pin.length() < 4) {
            Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "PIN set successfully: " + pin, Toast.LENGTH_SHORT).show();
            // You can add logic to navigate to another activity if needed
            Intent intent = new Intent(set_pin.this,register.class);
            startActivity(intent);
            finish();
        }
    }
}
